#!/bin/bash

set -o pipefail

SOTA_BASE_DIR="/var/sota"
SOTA_CRED_DIR="$SOTA_BASE_DIR/import"

CONFIG_FILE="$SOTA_BASE_DIR/auto-provisioning.json"
PROVISIONED_FILE="$SOTA_BASE_DIR/import/pkey.pem"

PROV_CLIENT_ID=""
PROV_SECRET=""
PROV_TOKEN_ENDPOINT=""
PROV_ACCESS_TOKEN=""
PROV_HIBERNATED=""

PROV_REGISTER_ENDPOINT=${PROV_REGISTER_ENDPOINT:-"https://app.torizon.io/api/accounts/devices"}

log() {
    echo "$@"
}

log_error() {
    echo "ERROR:" "$@"
}

exit_error() {
    log_error "$@"
    exit 1
}

exit_ok() {
    log "$@"
    exit 0
}

check_provisioning_status() {
    log "Checking provisioning status"

    if [ ! -f "$CONFIG_FILE" ]; then
        exit_ok "Auto-provisioning configuration file not found [$CONFIG_FILE]"
    fi
    if [ -f "$PROVISIONED_FILE" ]; then
        exit_ok "Device seems to be already provisioned (provisioning file exists [$PROVISIONED_FILE])"
    fi
}

read_json_property() {
    local property=$1
    local config=$2
    local value=""

    value=$(echo "$config" | jq -r "$property" 2>&-)
    if [ -z "$value" ] || [ "$value" = "null" ]; then
        echo "Could not read [$property] property from the JSON data"
        exit 1
    else
        echo "$value"
        exit 0
    fi
}

read_config_file() {
    local config

    config=$(cat $CONFIG_FILE)

    if ! PROV_CLIENT_ID=$(read_json_property ".client_id" "$config"); then
        exit_error "$PROV_CLIENT_ID"
    fi
    if ! PROV_SECRET=$(read_json_property ".secret" "$config"); then
        exit_error "$PROV_SECRET"
    fi
    if ! PROV_TOKEN_ENDPOINT=$(read_json_property ".token_endpoint" "$config"); then
        exit_error "$PROV_TOKEN_ENDPOINT"
    fi
    if ! PROV_HIBERNATED=$(read_json_property ".hibernated" "$config"); then
        PROV_HIBERNATED="false"
    fi
}

get_provisioning_token() {
    local http_response=""

    http_response=$(curl -s \
                         -u "${PROV_CLIENT_ID}:${PROV_SECRET}" \
                         -d "grant_type=client_credentials" \
                         "${PROV_TOKEN_ENDPOINT}")

    if ! PROV_ACCESS_TOKEN=$(read_json_property ".access_token" "$http_response"); then
        log_error "HTTP response: $http_response"
        exit_error "$PROV_ACCESS_TOKEN"
    fi
}

register_device() {
    local device_id=""
    local device_name=""
    local http_code post_data

    # get_device_id() can be optionally defined in a user-defined overrides script.
    if [ "$(type -t get_device_id)" = "function" ]; then
        if ! device_id=$(get_device_id); then
            exit_error "get_device_id returned an error ($?); aborting device registration"
        fi
    else
        device_id="$(cat /etc/hostname)"
    fi

    # get_device_name() can be optionally defined in a user-defined overrides script.
    if [ "$(type -t get_device_name)" = "function" ]; then
        if ! device_name=$(get_device_name); then
            exit_error "get_device_name returned an error ($?); aborting device registration"
        fi
    fi

    log "Provisioning device with deviceID=\"${device_id}\", deviceName=\"${device_name}\" and downloading credentials"

    cd "$(mktemp -d)" || exit_error "Could not create temporary directory to download credentials"

    post_data=$(jq -n \
                   --arg devid "${device_id}" \
                   --arg devnm "${device_name}" \
                   --arg hbrnt "${PROV_HIBERNATED}" \
                   '{"device_id": $devid, "device_name": $devnm, "hibernated": $hbrnt | test("true")}')
    http_code=$(curl -s -w '%{http_code}' --max-time 30 -X POST \
                     -H "Authorization: Bearer ${PROV_ACCESS_TOKEN}" \
                     -d "${post_data}" \
                     -o "device.zip" \
                     "$PROV_REGISTER_ENDPOINT")

    if [ "$http_code" != "200" ]; then
        http_message=$(cat device.zip)
        exit_error "Failed to provision the device - http code: [$http_code] - http message: [$http_message]"
    fi
}

write_credentials() {
    log "Updating device credentials"

    rm -Rf $SOTA_CRED_DIR && mkdir -p $SOTA_CRED_DIR
    if ! unzip device.zip -d $SOTA_CRED_DIR >/dev/null; then
        exit_error "Failed extracting credentials file"
    fi

    rm -rf $SOTA_BASE_DIR/sql.db

    rm -rf $CONFIG_FILE
}

restart_services() {
    log "Restarting aktualizr-torizon"
    systemctl restart aktualizr-torizon

    log "Enabling and restarting fluent-bit"
    touch /etc/fluent-bit/enabled
    systemctl restart fluent-bit

    if systemctl is-enabled remote-access; then
        log "Restarting Remote Access Client (RAC)"
        systemctl restart remote-access
    fi
}

main() {
    log "Starting auto-provisioning script"
    check_provisioning_status
    read_config_file
    get_provisioning_token
    register_device
    write_credentials
    restart_services
    log "Device successfully provisioned"
}

# Source the overrides script.
#
# Currently the script is expected to expose two functions, namely:
#
# - get_device_id: if defined, it should output the "device ID" to be used when
#   registering the device to the platform.
# - get_device_name: if defined, it should output the "device name" to be used
#   when registering the device to the platform.
#
for ovrdir in /etc/sota /usr/lib/sota; do
    ovrscript="${ovrdir}/auto-provisioning-overrides.sh"
    if [ -f "${ovrscript}" ]; then
        if [[ "$(stat -c '%U' "${ovrscript}")" == "root" ]] && \
           [[ "$(stat -c '%A' "${ovrscript}")" =~ ^-....-..-.$ ]]; then
            log "Sourcing ${ovrscript}"
            # shellcheck disable=SC1090
            source "${ovrscript}"
        else
            log_error "Refusing to source ${ovrscript}:" \
                      "script is writable by users other than root or is not owned by root."
        fi
        # Stop at the first script found (whether it's a valid one or not).
        break
    fi
done

main "$@"
