#!/bin/bash

REBOOT_SENTINEL_FILE="/run/need-reboot"

# ---
# Error handling support
# ---

# _die: Exit script even from subshells while not evaluating the "die" handler.
# die:  Exit script even from subshells while evaluating "die" handler.
# before_dying: Define expression to evaluate when before dying.

_die_handler=''
_exit_handler() {
    local ec=$?
    if [ "$ec" -eq 113 ]; then
        exit "$ec"
    elif [ "$ec" -ge 64 -a "$ec" -le 112 ]; then
        if [ "$$" -eq "$BASHPID" -a -n "$_die_handler" ]; then
            # This is the top-level shell (run it and use its exit code).
            eval "$_die_handler"
            exit "$?"
        fi
        # Any exit code in the "user-defined" range propagates to the parent shell.
        exit "$ec"
    fi
}
set -E; trap '_exit_handler' ERR

_die() { exit 113; }
die() {
    log "FATAL: $@"
    if [ "$$" -eq "$BASHPID" -a -n "$_die_handler" ]; then
        # This is the top-level shell (run it and use its exit code).
        eval "$_die_handler"
        exit "$?"
    fi
    exit 112;
}
before_dying() { _die_handler="$1"; }
# ---

req_program() {
    if [ ! -x "$1" ]; then
        echo "FATAL: Program not found $1." >&2; _die;
    fi
    return 0
}

# External common programs:
req_program "/usr/bin/date"        && alias DATE="$_"
req_program "/usr/bin/mkdir"       && alias MKDIR="$_"
req_program "/usr/bin/sha256sum"   && alias SHA256SUM="$_"
req_program "/usr/bin/touch"       && alias TOUCH="$_"

# ---
# Basic logging infrastructure initialization.
# ---
prep_log_or_abort() {
    if [ "$LOG_ENABLED" = "1" ]; then
        if ! MKDIR -p "${LOG_DIR}"; then
            echo "Cannot create log directory" >&2; _die
        fi
        if ! TOUCH "${LOG_FILE}"; then
            echo "Cannot access log directory" >&2; _die
        fi
    fi
    return 0
}

log() {
    if [ "$LOG_ENABLED" = "1" ]; then
        echo "$(DATE '+%Y/%m/%d-%H:%M%S') $@" >>$LOG_FILE
    fi
}

log_action() {
    if [ "$LOG_ENABLED" = "1" ]; then
       (echo ""
        echo "---"
        echo "Date: $(DATE)"
        echo "Event: $1"
        shift
        echo "---"
        [ $# -ne 0 ] && echo "Extra parameters:"
        for parm in "$@"; do
            echo "- $parm"
        done
        if [ "$LOG_VARS" = "1" ]; then
            echo "Environment variables:"
            env
        fi) >>$LOG_FILE
    fi
}

# ---
# Finish logging infrastructure initialization.
# ---

get_file_sha256() {
    local fname="$1"
    [ -r "$fname" ] || die "Cannot read $fname"
    local sha256=$(SHA256SUM "$fname" | SED -Ene 's/^\s*([0-9a-f]{64})\s*.*$/\1/p')
    [ -n "$sha256" ] || die "Cannot determine $fname's sha256"
    echo "$sha256"
    return 0
}

check_target_sha256() {
    local real_sha256=$(get_file_sha256 "$SECONDARY_FIRMWARE_PATH")
    local exp_sha256="$SECONDARY_FIRMWARE_SHA256"
    if [ "$real_sha256" = "$exp_sha256" ]; then
        log "Firmware file passed SHA-256 check"
        return 0
    fi
    die "SHA-256 mismatch for $SECONDARY_FIRMWARE_PATH (actual: ${real_sha256:0:12}..., exp.: ${exp_sha256:0:12}...)"
}

check_install_vars() {
    [ -n "$SECONDARY_CUSTOM_METADATA" ] || die "SECONDARY_CUSTOM_METADATA is not set"
    [ -n "$SECONDARY_FIRMWARE_PATH" ]   || die "SECONDARY_FIRMWARE_PATH is not set"
    [ -n "$SECONDARY_FIRMWARE_SHA256" ] || die "SECONDARY_FIRMWARE_SHA256 is not set"
}

on_install_failed() {
    if [ "$LOG_ENABLED" = "1" ]; then
        echo '{"status": "failed", "message": "action failed; check log at '$LOG_FILE'"}'
    else
        echo '{"status": "failed", "message": "action failed; enable log to see more"}'
    fi
    exit 0
}
