#!/bin/bash
#
# Emulates fw_printenv/fw_setenv for Torizon on NVIDIA Jetson platforms.
#
# On Jetson, there is no U-Boot and hence no U-Boot environment. This script
# emulates the upgrade_available, rollback, and bootcount variables used by
# Torizon OTA (aktualizr/greenboot) by storing them as bit flags in the
# TorizonBootFlags EFI variable managed by the EDK2 UEFI firmware.
#
# Usage:
#   fw_printenv [var ...]          Print one or more variables (all if none given)
#   fw_setenv <name> [<value>]     Set a variable
#   fw_setenv -s <script>          Apply assignments from a script file

app=$(basename "$0")

NVIDIA_VARS_GUID="781e084c-a330-417c-b678-38e696380cb9"
BOOT_FLAGS_VAR="TorizonBootFlags"

flags=$(efivar -n "${NVIDIA_VARS_GUID}-${BOOT_FLAGS_VAR}" -p | tail -n 1 | awk '{print $2}')
flags=$(printf %d "0x${flags}")
(( flags &= 3 )) # support 2 flags (bits 0 and 1) for the upgrade_available and rollback

is_zero() {
	[[ -n "$1" ]] && [[ "$1" =~ ^[0-9]+$ ]] && [[ "$1" -eq 0 ]]
}

is_zero_or_empty() {
	[[ -z "$1" ]] || { [[ "$1" =~ ^[0-9]+$ ]] && [[ "$1" -eq 0 ]]; }
}

update_flags_from_assignment() {
	local name="$1"
	local value="$2"
	case "$name" in
		upgrade_available)
			if is_zero_or_empty "$value"; then
				(( flags &= ~1 ))
			else
				(( flags |= 1 ))
			fi
			;;
		rollback)
			if is_zero_or_empty "$value"; then
				(( flags &= ~2 ))
			else
				(( flags |= 2 ))
			fi
			;;
		bootcount)
			if is_zero "$value" ; then
				/usr/sbin/nvbootctrl verify
			fi
			;;
	esac
}

if [[ "$app" == "fw_setenv" ]]; then
	orig_flags=$flags
	if [[ "$1" == "-s" ]] && [[ -n "$2" ]]; then
		script="$2"
		if [[ -r "$script" ]]; then
			while IFS= read -r line; do
				[[ -z "$line" || "$line" == \#* ]] && continue
				read -r name value _ <<< "$line"
				update_flags_from_assignment "$name" "$value"
			done < "$script"
		fi
	else
		update_flags_from_assignment "$1" "$2"
	fi

	if [[ $flags -ne $orig_flags ]]; then
		efivar_path="/sys/firmware/efi/efivars/${BOOT_FLAGS_VAR}-${NVIDIA_VARS_GUID}"
		restore_immutable() { chattr +i "$efivar_path"; }
		trap restore_immutable EXIT INT TERM
		chattr -i "$efivar_path"
		printf '%b' "\x07\x00\x00\x00\x0$(( flags & 3 ))" > "$efivar_path"
		restore_immutable
		trap - EXIT INT TERM
	fi
fi

print_var() {
	case "$1" in
		upgrade_available)
			echo "upgrade_available=$(( flags & 1 ))"
			;;
		rollback)
			echo "rollback=$(( (flags & 2) >> 1 ))"
			;;
	esac
}

if [[ "$app" == "fw_printenv" ]]; then
	if [[ $# -eq 0 ]]; then
		print_var upgrade_available
		print_var rollback
	else
		for var in "$@"; do
			print_var "$var"
		done
	fi
fi
