# Configuration that goes into prepare-root.conf (see ostree-prepare-root manual):
#
# - PREP_ROOT_ETC_TRANSIENT: whether /etc is transient ("true" or "false")
# - PREP_ROOT_CFS_ENABLED: enabling of composefs ("yes", "no", "maybe" or "signed")
#
PREP_ROOT_ETC_TRANSIENT ?= "true"
PREP_ROOT_CFS_ENABLED ?= "no"
PREP_ROOT_CFS_ENABLED:cfs-support ?= "yes"
PREP_ROOT_CFS_ENABLED:cfs-signed ?= "signed"

write_prepare_root_config() {
    local outfile="${1?Output file name required}"

    cat >${outfile} <<EOF
[etc]
transient = ${PREP_ROOT_ETC_TRANSIENT}

[composefs]
enabled = ${PREP_ROOT_CFS_ENABLED}

[sysroot]
readonly = false
EOF
}
