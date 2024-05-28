# Given a credentials file, go to server and get an access token to allow
# access to API endpoints including the ones implementing the  image and
# director Uptane repositories.
#
# Result is exported to variables:
#
# - TORIZON_OTA_ACCESS_TOKEN
# - TORIZON_OTA_API_URL
# - TORIZON_OTA_REPO_URL
# - TORIZON_OTA_DIRECTOR_URL
#
ota_get_token_from_creds() {
    local creds="${1?Expecting credentials file}"
    local force="${2:-0}"

    if [ "${force}" = "0" ] && [ -n "${TORIZON_OTA_ACCESS_TOKEN}" ]; then
	bbdebug 1 "Reusing previous access token"
	return 0
    fi

    local token_url client_id client_secret
    local treehub_json

    treehub_json="$(unzip -p "${creds}" "treehub.json")"
    [ -n "${treehub_json}" ] || return 1

    token_url="$(echo "${treehub_json}" | jq -r ".oauth2.server")"
    [ "${token_url}" != "null" ] || return 1
    client_id="$(echo "${treehub_json}" | jq -r ".oauth2.client_id")"
    [ "${client_id}" != "null" ] || return 1
    client_secret="$(echo "${treehub_json}" | jq -r ".oauth2.client_secret")"
    [ "${client_secret}" != "null" ] || return 1

    token_url="${token_url}/token"

    bbdebug 1 "token_url='${token_url}'"
    bbdebug 2 "client_id='${client_id}'"
    # echo "client_secret='${client_secret}'"

    # Determine actual token.
    local tkres token
    tkres=$(curl -s "${token_url}" \
		 -d client_id="${client_id}" \
		 -d client_secret="${client_secret}" \
		 -d grant_type="client_credentials" \
		 --fail \
		 --cacert "${STAGING_ETCDIR_NATIVE}/ssl/certs/ca-certificates.crt")
    bbdebug 2 "tkres='${tkres}'"
    token=$(echo "${tkres}" | jq -r ".access_token")

    if [ "${token}" = "null" ]; then
	bbdebug 1 "Could not get token from OTA server."
	return 1
    fi

    # Determine common base URLs (API, director and (image) repo).
    local api_url repo_url director_url
    repo_url="$(unzip -p "${creds}" "tufrepo.url")"
    repo_url="${repo_url%/}"
    api_url="${repo_url%%/repo}/api"
    director_url="${repo_url%%/repo}/director"

    # echo "token='${token}'"
    # echo "api_url='${api_url}'"
    # echo "repo_url='${repo_url}'"
    # echo "director_url='${director_url}'"

    # Output is kept in environment variables.
    export TORIZON_OTA_ACCESS_TOKEN="${token}"
    export TORIZON_OTA_API_URL="${api_url}"
    export TORIZON_OTA_REPO_URL="${repo_url}"
    export TORIZON_OTA_DIRECTOR_URL="${director_url}"
}

ota_clear_token() {
    export TORIZON_OTA_ACCESS_TOKEN=""
}

ota_get_root_metadata() {
    local repo="${1:-repo}"
    local fname="${2:-root.json}"

    if [ -z "${TORIZON_OTA_ACCESS_TOKEN}" ]; then
	bbwarn "get_root_metadata: TORIZON_OTA_ACCESS_TOKEN must be set"
	return 1
    fi

    local base_url
    if [ "${repo}" = "repo" ]; then
	# "repo" means the Uptane image repository.
	url="${TORIZON_OTA_REPO_URL}/api/v1/user_repo/${fname}"
    elif [ "${repo}" = "director" ]; then
	# "director" means the Uptane director repository.
	url="${TORIZON_OTA_DIRECTOR_URL}/api/v1/admin/repo/${fname}"
    else
	bbwarn "Invalid parameter passed to get_root_metadata(repo='${repo}')"
	return 1
    fi

    curl -s "${url}" \
	 -H "Authorization: Bearer ${TORIZON_OTA_ACCESS_TOKEN}" \
	 --fail \
	 --cacert "${STAGING_ETCDIR_NATIVE}/ssl/certs/ca-certificates.crt"
}
