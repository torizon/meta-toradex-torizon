gen_cfs_keys0() {
    local keydir="${1}"
    local keyname="${2}"

    local pubkey="${keydir}/${keyname}.pub"
    local seckey="${keydir}/${keyname}.sec"

    mkdir -p "${keydir}"

    local pem="$(openssl genpkey -algorithm ed25519 -outform PEM)"
    local pub="$(printf "%s" "${pem}" | openssl pkey -outform DER -pubout | tail -c 32 | base64)"
    local seed="$(printf "%s" "${pem}" | openssl pkey -outform DER | tail -c 32 | base64)"

    # Save secret key (concatenate seed and pub key); see libtest.sh in the ostree code base:
    (printf "%s" "${seed}" | base64 -d && \
     printf "%s" "${pub}"  | base64 -d) | base64 > "${seckey}"

    # Save public key:
    printf "%s" "${pub}" > "${pubkey}"
}

gen_cfs_keys() {
    if [ -z "${CFS_SIGN_KEYDIR}" ] || [ -z "${CFS_SIGN_KEYNAME}" ] || [ -z "${CFS_GENERATE_KEYS}" ]; then
        bbfatal "Secure boot signing not correctly set up."
    fi

    local pubkey="${CFS_SIGN_KEYDIR}/${CFS_SIGN_KEYNAME}.pub"
    local seckey="${CFS_SIGN_KEYDIR}/${CFS_SIGN_KEYNAME}.sec"

    if [ "${CFS_GENERATE_KEYS}" = "1" ]; then
        if [ -f "${pubkey}" ] && [ -f "${seckey}" ]; then
	    bbnote "Key pair \"${CFS_SIGN_KEYNAME}\" already exists; skipping generation."
	else
	    # Generate the key pair:
	    bbnote "Generating key pair \"${CFS_SIGN_KEYNAME}\" (.pub, .sec)."
	    gen_cfs_keys0 "${CFS_SIGN_KEYDIR}" "${CFS_SIGN_KEYNAME}"
	fi
    fi

    if [ ! -f "${seckey}" ]; then
        bbfatal "Could not find key file \"${seckey}\"."
    fi
}

def cfs_get_key_file_checksums(d):
    if "cfs-signed" not in d.getVar("OVERRIDES").split(":"):
        return ""
    seckey = os.path.join(d.getVar("CFS_SIGN_KEYDIR"),
                          d.getVar("CFS_SIGN_KEYNAME") + ".sec")
    pubkey = os.path.join(d.getVar("CFS_SIGN_KEYDIR"),
                          d.getVar("CFS_SIGN_KEYNAME") + ".pub")
    return " ".join([
        seckey + (":True" if os.path.exists(seckey) else ":False"),
        pubkey + (":True" if os.path.exists(pubkey) else ":False")
    ])
