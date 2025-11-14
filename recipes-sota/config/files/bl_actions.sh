#!/bin/bash

shopt -s expand_aliases
source "/usr/bin/common_actions.sh"

# Avoid being interrupted by systemd since operations here are supposed to be critical.
trap '' TERM

# ---
# Configuration
# ---

# DRY_RUN="0": normal execution mode (also if not set)
# DRY_RUN="1": show commands that would be executed but do not execute them (regarding
#              commands that change the state of the system)
# DRY_RUN="2": execute all commands except switching the partition in the CSD and reboot
DRY_RUN="${BL_DRY_RUN:-0}"

LOG_ENABLED="${BL_LOG_ENABLED:-0}"
LOG_VARS="${BL_LOG_VARS:-0}"

LOG_DIR="/var/lib/rollback-manager"
LOG_FILE="${LOG_DIR}/bootloader-update.log"

set -o pipefail

# External programs:
req_program "/usr/bin/cat"         && alias CAT="$_"
req_program "/usr/bin/dd"          && alias DD="$_"
req_program "/usr/bin/fw_printenv" && alias FW_PRINTENV="$_"
req_program "/usr/bin/fw_setenv"   && alias FW_SETENV="$_"
req_program "/usr/bin/jq"          && alias JQ="$_"
req_program "/usr/bin/lsblk"       && alias LSBLK="$_"
req_program "/usr/bin/mmc"         && alias MMC="$_"
req_program "/usr/bin/rm"          && alias RM="$_"
req_program "/usr/bin/sed"         && alias SED="$_"
req_program "/usr/sbin/reboot"     && alias REBOOT="$_"
req_program "/usr/bin/stat"        && alias STAT="$_"
req_program "/usr/bin/tr"          && alias TR="$_"

maybe_run() {
    if [ "$DRY_RUN" = "1" ]; then
        log "WOULD RUN:" "$@"
        return 0
    else
        log "RUN:" "$@"
    fi
    eval "$@"
}

# output: Device name (without the /dev/)
get_emmc_dev() {
    local otaroot="/dev/disk/by-label/otaroot"
    if [ ! -e "$otaroot" ]; then
        die "Cannot find OTA root partition!"
    fi
    local devname="$(LSBLK -ndo pkname $otaroot)"
    if [ ! -b "/dev/$devname" ]; then
        die "Cannot determine eMMC device for bootloader"
    fi
    echo "$devname"
    return 0
}

# $1: eMMC device name (without /dev/)
# output: Current partition (integer as string: 1, 2)
get_emmc_active_partnum() {
    local devname="/dev/$1"
    local actpart=$(MMC extcsd read "$devname" | SED -Ene 's/^\s*boot partition\s*([0-9]+)\s*enabled.*$/\1/ip')
    if [ -z "$actpart" ]; then
        die "Cannot determine active partition on device $devname"
    fi
    echo "$actpart"
    return 0
}

# $1: eMMC device name (without /dev/)
# $2: Partition to activate (integer as string: 1, 2; 0 to disable)
switch_emmc_active_partnum() {
    local devname="/dev/$1"
    local partnum="$2"
    maybe_run MMC bootpart enable "$partnum" 1 "$devname"
    return
}

# $1: current partition
# output: Partition to switch to (integer as string)
get_emmc_other_partnum() {
    local actpart="$1"
    if [ "$actpart" = "1" ]; then
        echo "2"
    elif [ "$actpart" = "2" ]; then
        echo "1"
    else
        die "Cannot determine partition to switch to (current=$actpart)"
    fi
    return 0
}

get_prev_csdpart() {
    local prev_csdpart=$(FW_PRINTENV bl_prev_csdpart | SED -Ee 's/\s*bl_prev_csdpart=(.*)$/\1/')
    [ "$prev_csdpart" = "1" -o "$prev_csdpart" = "2" ] || die "Cannot determine previous partition from CSD"
    echo "$prev_csdpart"
    return 0
}

# $1: eMMC device name
# $2: eMMC boot partition number
get_bootpart() {
    local partnum="$2"
    local partidx=""
    if [ "$partnum" = "1" ]; then
        partidx="0"
    elif [ "$partnum" = "2" ]; then
        partidx="1"
    else
        die "Bad partition number passed to get_bootpart ($partnum)"
    fi
    echo "${1}boot${partidx}"
    return 0
}

# output: version or empty string if not available (no failure case)
get_bl_cur_version() {
    local src="/proc/device-tree/chosen/u-boot,version"
    local ver=$(CAT "$src" 2>/dev/null | TR -d '\0')
    echo "$ver"
    return 0
}

get_bl_new_version() {
    # Primary source of information: field 'bootloader.dtVersion'
    local dt_version=$(echo "$SECONDARY_CUSTOM_METADATA" | JQ -r '.bootloader.dtVersion' 2>/dev/null)
    if [ $? -eq 0 -a "$dt_version" != "null" ]; then
        echo "$dt_version"
        return 0
    fi
    # Secondary source of information: field 'version'
    local pkg_version=$(echo "$SECONDARY_CUSTOM_METADATA" | JQ -r '.version' 2>/dev/null)
    if [ $? -eq 0 -a "$pkg_version" != "null" ]; then
        echo "$pkg_version"
        return 0
    fi
    # Something wrong with the metadata.
    die "Cannot determine new bootloader version"
}

get_dd_options() {
    # Primary source of information: field 'bootloader.ddOptions'
    local dd_deflt="$1"
    local dd_opts0=$(echo "$SECONDARY_CUSTOM_METADATA" | JQ -r '.bootloader.ddOptions' 2>/dev/null)
    if [ $? -eq 0 -a "$dd_opts0" != "null" ]; then
        if [ "${dd_opts0:0:1}" = "!" ]; then
            # If string starts with an exclamation mark use it as is (ignore defaults).
            echo "${dd_opts0:1}"
            return 0
        else
            echo "${dd_opts0} ${dd_deflt}"
            return 0
        fi
    fi
    # Something wrong with the metadata.
    die "Cannot determine dd options for writing"
}

conv_dd_options() {
    # TODO: Review: currently we only take 'seek' an convert it into 'skip'.
    echo "$1" | SED -Ee 's/.*\bseek=([0-9]+).*/skip=\1/'
}

get_file_size() {
    local fname="$1"
    [ -r "$fname" ] || die "Cannot read $fname"
    local fsize=$(STAT -c "%s" $fname)
    [ -n "$fsize" ] || die "Cannot determine $fname's size"
    echo "$fsize"
    return 0
}

clean_uboot_vars() {
    log "Clearing u-boot variables"
    # Note that we're setting bl_prev_csdpart and bl_updt_status to '-' just because fw_setenv does
    # not seem to work with an empty string but right below we are also deleting these variables.
    maybe_run FW_SETENV \
              bl_prev_csdpart "-" \
              bl_updt_status "-" \
              bootcount "0" \
              upgrade_available "0"
    # Delete variables used only during the bootloader update (not strictly needed).
    maybe_run FW_SETENV bl_prev_csdpart
    maybe_run FW_SETENV bl_updt_status
}

# Process the following fields under '.bootloader.env' (of the custom metadata):
# 'resetOnUpdate', 'embeddedOffset', 'embeddedSize', 'setVars' and 'keepVars'.
#
# Result: Script to set all environment (written to file defined by $1).
#
create_setenv_script_from_embedded() {
    local out_script=${1:?Output script must be specified}
    local env_reset=$(echo "$SECONDARY_CUSTOM_METADATA" |
                      JQ -r '.bootloader.env.resetOnUpdate' 2>/dev/null)
    local env_offset=$(echo "$SECONDARY_CUSTOM_METADATA" |
                       JQ -r '.bootloader.env.embeddedOffset' 2>/dev/null)
    local env_size=$(echo "$SECONDARY_CUSTOM_METADATA" |
                     JQ -r '.bootloader.env.embeddedSize' 2>/dev/null)

    log "Create setenv script: envReset=$env_reset, envOffset=$env_offset, envSize=$env_size"

    if [ "$env_reset" = "true" -a "$env_offset" -gt 0 -a "$env_size" -gt 0 ] 2>/dev/null; then
        # Conditions to reset the environment look good.

        # Extract environment from binary; save into separate file so that if we can't
        # get the new environment we don't clear the previous one either for increased
        # robustness.
        DD if="$SECONDARY_FIRMWARE_PATH" \
           iflag="skip_bytes,count_bytes" \
           skip="${env_offset}" count="${env_size}" 2>/dev/null \
           | SED 's/\x00/\x0A/g' > "${out_script}.new"
        if [ $? -ne 0 ]; then
            log "Could not extract environment from binary!"
        else
            echo '# Clear previous environment:' >> $out_script
            FW_PRINTENV | sed 's/^\( *[^=]\+\)=\(.*\)$/\1=/' >> $out_script
            if [ $? -ne 0 ]; then
                log "Could not clear previous environment!"
            fi

            echo '# Set new environment:' >> $out_script
            CAT "${out_script}.new" >> $out_script
            RM -f "${out_script}.new"
        fi

        # Handle 'bootloader.env.keepVars' section.
        local keep_expr=$(echo "$SECONDARY_CUSTOM_METADATA" |
                          JQ -r '.bootloader.env.keepVars | join("\\|")' 2>/dev/null)
        if [ -n "$keep_expr" ]; then
            echo '# Restore variables to be kept (handle "keepVars"):' >> $out_script
            FW_PRINTENV | sed -n "s/^ *\(${keep_expr}\)=\(.*\)$/\1=\2/p" >> $out_script
        fi
    else
        log "Environment reset not requested through the metadata"
    fi

    # Handle 'bootloader.env.setVars' section.
    local set_vars_text=$(
        echo "$SECONDARY_CUSTOM_METADATA" |
        JQ -r '.bootloader.env.setVars | to_entries[] | "\(.key)=\(.value)"' 2>/dev/null)
    if [ $? -eq 0 -a -n "$set_vars_text" ]; then
        echo '# Set some variables (handle "setVars"):' >> $out_script
        echo "$set_vars_text" >> $out_script
    fi

    return 0
}

update_uboot_vars() {
    # Create temporary script that will hold all fw_printenv operations to be
    # executed at once.
    local tmp_script=$(mktemp -t bl_update_script.XXXXXXXXXX)

    # Handle environment reset; the only type supported at the moment is "embedded"
    # meaning that the environment to reset to is somewhere inside the u-boot binary.
    local env_type=$(echo "$SECONDARY_CUSTOM_METADATA" |
                     JQ -r '.bootloader.env.type' 2>/dev/null)
    if [ $? -eq 0 -a "$env_type" = "embedded" ]; then
        create_setenv_script_from_embedded "${tmp_script}"
    else
        log "Metadata does not provide information about u-boot environment variables"
    fi

    # Handle the setting of the variables passed as parameter.
    if [ "${#@}" -gt 0 ]; then
        echo "# Set extra variables for update operation:" >> $tmp_script
        printf "%s=%s\n" "$@" >> $tmp_script
    fi

    # Execute temporary script.
    log "Executing setenv script"
    maybe_run FW_SETENV -s "$tmp_script"
    local setenv_res="$?"

    if [ "$DRY_RUN" = "1" ]; then
        log "This is the setenv script that would have been executed:"
        log "$(CAT "${tmp_script}")"
    fi

    # Get rid of the temporary script.
    RM -f "${tmp_script}"

    return $setenv_res
}

do_install() {
    # Note: if die is called from this point on the die handler will be called.
    before_dying 'on_install_failed'

    check_install_vars
    local emmc_dev=$(get_emmc_dev)
    local active_partnum=$(get_emmc_active_partnum "$emmc_dev")
    local other_partnum=$(get_emmc_other_partnum "$active_partnum")
    local active_bootpart=$(get_bootpart "$emmc_dev" "$active_partnum")
    local other_bootpart=$(get_bootpart "$emmc_dev" "$other_partnum")
    local cur_version=$(get_bl_cur_version)
    local new_version=$(get_bl_new_version)
    local dd_options_wr=$(get_dd_options "conv=fdatasync")
    # TODO: Consider a new field in metadata to specify how to do the reading.
    local dd_options_rd=$(conv_dd_options "$(get_dd_options)")

    log "Starting installation of bootloader"
    log "eMMC device: ${emmc_dev}"
    log "Active boot partition: ${active_bootpart}"
    log "Inactive boot partition: ${other_bootpart}"
    log "Old BL version: ${cur_version}"
    log "New BL version: ${new_version}"
    log "Options for 'dd' (writing): ${dd_options_wr}"
    log "Options for 'dd' (reading): ${dd_options_rd}"

    # 1: Basic input checks.
    if [ "$new_version" = "$cur_version" ]; then
        log "Updating to the same version: rollback will not be detected!"
    fi

    # 2: Check that binary about to be flashed has expected SHA-256 (safeguard).
    check_target_sha256

    # ---
    # 3: Lock partition, store, unlock partition. WARNING: Do not call "die" in this stretch.
    # ---

    # 3.1: Unlock destination partition.
    # TODO: Add trap here to always re-lock the partition on every case.
    echo 0 >"/sys/class/block/${other_bootpart}/force_ro"

    # 3.2: Copy file into destination partition.
    # NOTE: Currently we use the default block size so that dd_options can be taken from image.json;
    #       writing speed does not seem to change much with the block size anyway.
    maybe_run DD $dd_options_wr \
              if="$SECONDARY_FIRMWARE_PATH" \
              of="/dev/${other_bootpart}" 2>/dev/null
    local dd_res1="$?"

    # 3.3: Lock destination partition.
    echo 1 >"/sys/class/block/${other_bootpart}/force_ro"
    # ---

    # Avoid calling 'die' inside protected step.
    [ "$dd_res1" -eq 0 ] || die "Could not write to boot partition"

    # 4: Verify data from partition.
    local fwsize=$(get_file_size "$SECONDARY_FIRMWARE_PATH")
    local part_sha256_=$(DD \
                         $dd_options_rd \
                         if="/dev/${other_bootpart}" \
                         iflag=count_bytes \
                         count="$fwsize" 2>/dev/null | SHA256SUM)
    local part_sha256=$(echo "${part_sha256_}" | SED -Ene 's/^\s*([0-9a-f]{64})\s*.*$/\1/p')
    local dd_res2="$?"
    [ "$dd_res2" -eq 0 ] || die "Could not verify data in boot partition"

    if [ "$DRY_RUN" = "1" ]; then
        log "Ignoring actual partition digest in dry-run mode"
    elif [ "$part_sha256" = "$SECONDARY_FIRMWARE_SHA256" ]; then
        log "Partition data passed SHA-256 check"
    else
        die "SHA-256 mismatch for data in partition (actual: ${part_sha256:0:12}..., exp.: ${SECONDARY_FIRMWARE_SHA256:0:12}...)"
    fi

    # 5: Set u-boot environment variables.
    update_uboot_vars bl_prev_csdpart "$active_partnum" \
                      bl_updt_status "attempt" \
                      bootcount "0" \
                      upgrade_available "1"
    local updenv_res="$?"
    [ "$updenv_res" -eq 0 ] || die "Could not set u-boot environment"

    # 6: Switch active boot partition.
    if [ "$DRY_RUN" = "2" ]; then
        log "Not switching partition in dry-run mode"
    else
        if ! switch_emmc_active_partnum "$emmc_dev" "$other_partnum"; then
            clean_uboot_vars
            die "Could not switch active partition"
        fi
    fi

    # ---
    # WARNING: From this point on we must avoid calling "die" otherwise we would need to rollback to
    # the previous partition before returning to aktualizr.
    # ---

    # 7: Request a reboot; beware that this script itself could get a SIGTERM sent by systemd (which
    #    is currently trapped and ignored).
    if [ "$DRY_RUN" = "2" ]; then
        log "Not rebooting in dry-run mode"
    else
        maybe_run TOUCH "${REBOOT_SENTINEL_FILE}" 2>/dev/null
        if  [ "$?" -ne 0 ]; then
            log "Could not create reboot sentinel file; falling back to normal reboot"
            # Fall back to using reboot.
            maybe_run REBOOT 2>/dev/null
            [ "$?" -eq 0 ] || log "Could not schedule reboot"
        fi
    fi

    echo '{"status": "need-completion", "message": "rebooting soon"}'
    return 0
}

do_complete_install() {
    if [ -e "${REBOOT_SENTINEL_FILE}" ]; then
        # This could happen if the reboot path/service is disabled.
        log "Delaying installation completion due to pending reboot"
        echo '{"status": "need-completion", "message": "delaying completion due to pending reboot"}'
        return 0
    fi

    # Note: if die is called from this point on the die handler will be called.; any failure here
    # will send a status of 'failed' to aktualizr (see 'on_install_failed()')
    before_dying 'on_install_failed'

    check_install_vars
    local emmc_dev=$(get_emmc_dev)
    local cur_version=$(get_bl_cur_version)
    local new_version=$(get_bl_new_version)

    log "Finishing installation of bootloader"
    log "eMMC device: ${emmc_dev}"
    log "Old BL version: ${cur_version}"
    log "New BL version: ${new_version}"

    if [ "$cur_version" = "$new_version" ]; then
        log "Successfully updated bootloader to version ${new_version}"
        clean_uboot_vars
        echo '{"status": "ok", "message": "bootloader update succeeded"}'
        return 0
    fi

    # ---
    # Error handling
    # ---
    local prev_partnum=$(get_prev_csdpart)
    log "Failed to update bootloader to version ${new_version}; performing rollback"

    # Switch to the previous boot partition; this should handle the case of a hardware rollback
    # where the system runs the previous bootloader even though the active partition is still
    # pointing to the bad/new bootloader version.
    if [ "$DRY_RUN" = "2" ]; then
        log "Not switching partition in dry-run mode"
    else
        switch_emmc_active_partnum "$emmc_dev" "$prev_partnum"
    fi

    clean_uboot_vars
    echo '{"status": "failed", "message": "bootloader update failed"}'
    return 0
}

# ---
# Main program
# ---

prep_log_or_abort

log_action "$@"

case "$1" in
    get-firmware-info)
        # Perform normal processing for this action.
        exit 64
        ;;
    install)
        do_install
        exit 0
        ;;
    complete-install)
        do_complete_install
        exit 0
        ;;
    *)
        # Perform normal processing for this action.
        log "Unknown action: $1"
        exit 64
        ;;
esac
