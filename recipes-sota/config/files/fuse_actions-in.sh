#!/usr/bin/bash

shopt -s expand_aliases
source "/usr/bin/common_actions.sh"

# Avoid being interrupted by systemd since operations here are supposed to be critical.
trap '' TERM

# ---
# Configuration
# ---

# DRY_RUN="0": normal execution mode (also if not set)
# DRY_RUN="1": sets U-Boot variable "fuse_dry_run", which causes uEnv.txt to print commands
#              that would have been executed but not actually execute them
DRY_RUN="${FUSE_DRY_RUN:-0}"

LOG_ENABLED="${FUSE_LOG_ENABLED:-0}"
LOG_VARS="${FUSE_LOG_VARS:-0}"

LOG_DIR="/var/lib/rollback-manager"
LOG_FILE="${LOG_DIR}/fuse-update.log"

set -o pipefail

# External programs:
req_program "/usr/bin/cat"         && alias CAT="$_"
req_program "/usr/bin/dirname"     && alias DIRNAME="$_"
req_program "/usr/bin/fw_printenv" && alias FW_PRINTENV="$_"
req_program "/usr/bin/fw_setenv"   && alias FW_SETENV="$_"
req_program "/usr/bin/sed"         && alias SED="$_"
req_program "/usr/sbin/reboot"     && alias REBOOT="$_"
req_program "/usr/bin/wc"          && alias WC="$_"

# Returns the expected number of secure boot fuses based on the type of device
get_fuse_num() {
    local machine
    machine=${MACHINE:-@@MACHINE@@}
    case $machine in
        *imx6ull*|*imx7*|*imx6*|*imx8m*)
            local fuse_num=8
            ;;
        *imx8*)
            local fuse_num=16
            ;;
        *)
            die "Unsupported device $machine for fuse update type"
            return 1
            ;;
    esac

    echo "$fuse_num"
    return 0
}

# Validates whether the given yaml file has the expected required fields
validate_yaml() {
    local exp_fuse_num=$1

    local found_fuse_num
    found_fuse_num=$(CAT "${SECONDARY_FIRMWARE_PATH}" 2>/dev/null | SED -n -e "s/^.*fuse-val.*://p" | WC -l)
    if [ "$found_fuse_num" -ne "$exp_fuse_num" ]; then
        log "Expected $exp_fuse_num fuse values, but found $found_fuse_num instead"
        return 1
    fi

    local found_fuse_close
    found_fuse_close=$(CAT "${SECONDARY_FIRMWARE_PATH}" 2>/dev/null | SED -n -e "s/^.*fuse-close.*://p" | WC -l)
    if [ "$found_fuse_close" -ne "1" ]; then
        log "Did not find fuse-close field in yaml file"
        return 1
    fi

    return 0
}

# Parses the hex values from the yaml file
parse_fuse_vals() {
    local fuse_num
    fuse_num=$(get_fuse_num) || return 1
    validate_yaml "$fuse_num" || return 1

    local fuse_vals val i

    for ((i=1; i<=fuse_num; i++)); do
        val=$(CAT "${SECONDARY_FIRMWARE_PATH}" 2>/dev/null | SED -n -e "s/^.*fuse-val${i}: \(0x[0-9a-fA-F]\+\)$/\1/p")
        [ -n "$val" ] || die "Unable to parse fuse-val$i"
        log "Parsed fuse-val$i: $val"
        fuse_vals="${fuse_vals} ${val}"
    done

    # Remove unneeded space character in front of first value
    echo "${fuse_vals:1}"
    return 0
}

# Parses boolean value from "fuse-close" field in the yaml
parse_fuse_close() {
    local close
    close=$(CAT "${SECONDARY_FIRMWARE_PATH}" | SED -n -e "s/^.*fuse-close: //p")
    if [ -z "$close" ]; then
        die "Unable to parse fuse-close"
        return 1
    else
        log "Parsed fuse-close: $close"
    fi

    if [ "$close" = "true" ] || [ "$close" = "True" ]; then
        close="1"
    elif [ "$close" = "false" ] || [ "$close" = "False" ]; then
        close="0"
    else
        die "Invalid value for fuse-close: $close"
        return 1
    fi

    echo "$close"
    return 0
}

clear_uboot_vars() {
    log "Clearing U-Boot variables related to fuse update"
    FW_SETENV fuse_prog_list
    FW_SETENV fuse_prog_close
    FW_SETENV fuse_status
    FW_SETENV fuse_dry_run
}

# Attempt to construct valid yaml based on information in U-Boot
create_yaml_content() {
    local fuse_num
    fuse_num=$(get_fuse_num)

    local fuse_val i
    for ((i=1; i<=fuse_num; i++)); do
        fuse_val=$(FW_PRINTENV fuse_val_${i} | SED -Ee "s/\s*fuse_val_${i}=(.*)$/\1/")
        if [ -z "$fuse_val" ]; then
            log "No value found for fuse_val_${i}, defaulting to 0x0"
            local fuse_val_${i}="0x0"
        else
            log "Value for fuse index ${i} is currently: $fuse_val"
            local fuse_val_${i}="$fuse_val"
        fi
    done

cat << EOF > "$SECONDARY_FIRMWARE_PATH"
fuses:
  fuse-val1: $fuse_val_1
  fuse-val2: $fuse_val_2
  fuse-val3: $fuse_val_3
  fuse-val4: $fuse_val_4
  fuse-val5: $fuse_val_5
  fuse-val6: $fuse_val_6
  fuse-val7: $fuse_val_7
  fuse-val8: $fuse_val_8
EOF

    if [ "$fuse_num" = "16" ]; then
cat << EOF >> "$SECONDARY_FIRMWARE_PATH"
  fuse-val9: $fuse_val_9
  fuse-val10: $fuse_val_10
  fuse-val11: $fuse_val_11
  fuse-val12: $fuse_val_12
  fuse-val13: $fuse_val_13
  fuse-val14: $fuse_val_14
  fuse-val15: $fuse_val_15
  fuse-val16: $fuse_val_16
EOF
    fi

    local fuse_close
    fuse_close=$(FW_PRINTENV fuse_val_close | SED -Ee "s/\s*fuse_val_close=(.*)$/\1/")
    if [ "$fuse_close" = 0 ]; then
        log "Device is currently open"
        local prog_close="false"
    elif [ "$fuse_close" = 1 ]; then
        log "Device is currently closed"
        local prog_close="true"
    else
        # If we can't determine the closing state from U-Boot then just assume it's open
        log "Unable to determine Device closed state, will assume device is open"
        local prog_close="false"
    fi

cat << EOF >> "$SECONDARY_FIRMWARE_PATH"
  fuse-close: $prog_close
EOF
}

# Creates target_name file if it does not already exist
create_target_file () {
    local fuse_dir
    fuse_dir=$(DIRNAME "$SECONDARY_FIRMWARE_PATH")

    target_file="${fuse_dir}/target_name"
    if [ ! -e "${target_file}" ]; then
        log "Generating target_name file"
	echo "fuse.yml" > "$target_file"
    fi
}

# Create firmware file from information available from U-boot if file does not exist
do_create_firmware() {
    log "Generating firmware file based on information in U-Boot"

    create_yaml_content
    create_target_file
    return 0
}

do_install() {
    before_dying 'on_install_failed'

    check_install_vars
    check_target_sha256

    # Define variables early as "local" will mask the true exit code of the functions
    local fuse_val_list fuse_close

    fuse_val_list=$(parse_fuse_vals) || die "Aborting update unable to parse fuse values"
    fuse_close=$(parse_fuse_close) || die "Aborting update unable to parse fuse values"

    # Set U-Boot environment variables for uEnv.txt
    FW_SETENV fuse_prog_list "$fuse_val_list" \
              fuse_prog_close "$fuse_close" \
              fuse_status "pending" \
              fuse_dry_run "$DRY_RUN"

    if [ "$?" -ne 0 ]; then
        # Clear U-Boot variable just in case
        clear_uboot_vars
        die "Failed to set U-Boot enviroment variables"
    fi

    # Schedule reboot to complete update
    TOUCH "${REBOOT_SENTINEL_FILE}" 2>/dev/null
    if  [ "$?" -ne 0 ]; then
        log "Could not create reboot sentinel file; falling back to normal reboot"
        # Fall back to using reboot.
        REBOOT 2>/dev/null
        if [ "$?" -eq 0 ]; then
            clear_uboot_vars
            die "Failed to reboot device"
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

    local fuse_status
    fuse_status=$(FW_PRINTENV fuse_status | SED -Ee 's/\s*fuse_status=(.*)$/\1/')

    case "$fuse_status" in
        pending|need-reboot)
            # Unexpected that we end up here, let's consider it a failure
            log "Unexpected fuse-status of $fuse_status, considering update a failure"
            echo '{"status": "failed", "message": "unexpected status detected"}'
            ;;
        failed)
            log "Failed fuse-status of $fuse_status"
            echo '{"status": "failed", "message": "secure-boot fuse update failed"}'
            ;;
        success)
            log "Successfully updated secure-boot fuses"
            echo '{"status": "ok", "message": "secure-boot fuse update succeeded"}'
            ;;
        *)
            log "Unrecognized fuse-status of $fuse_status, will consider update as failed"
            # Probably safer to just assume the update has failed in this case
            echo '{"status": "failed", "message": "secure-boot fuse update failed"}'
            ;;
    esac
    clear_uboot_vars
    return 0
}

# ---
# Main program
# ---

prep_log_or_abort

log_action "$@"

case "$1" in
    get-firmware-info)
        if [ -n "${SECONDARY_FIRMWARE_PATH}" ] && [ ! -e "${SECONDARY_FIRMWARE_PATH}" ]; then
            # Try to create firmware file so some hash can be sent to server
            do_create_firmware
        fi
        # Perform normal processing for this action
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
