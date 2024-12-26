#!/bin/sh
CFG_COMPOSEFS=${OSTREE_REPO_CFG_COMPOSEFS:-@@OSTREE_REPO_CFG_COMPOSEFS@@}
CFG_FSVERITY=${OSTREE_REPO_CFG_FSVERITY:-@@OSTREE_REPO_CFG_FSVERITY@@}

ostree_cfg_set() {
    local key=${1?key required}
    local val=${2}
    local cur=$(ostree config get "${key}" 2>/dev/null)

    if [ "$val" != "$cur" ]; then
	# Avoid writing to the file if the value is already set as expected.
	if [ -n "${val}" ]; then
	    echo "setting ostree config '${key}' to '${val}'"
	    ostree config set "${key}" "${val}"
	else
	    echo "clearing ostree config '${key}'"
	    ostree config unset "${key}"
	fi
    else
	echo "ostree config '${key}' is already set to '${val}'"
    fi
}

ostree_cfg_set "ex-integrity.composefs" "${CFG_COMPOSEFS}"
ostree_cfg_set "ex-integrity.fsverity" "${CFG_FSVERITY}"
