# TODO: remove this whole go_1.22.12.bbappend once the fix is included in the upstream release
# https://patchwork.yoctoproject.org/project/oe-core/patch/20260309165351.311700-1-eduardo.f120@yahoo.com/

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Remove the patch that originally included the issue.
SRC_URI:remove = " \
    file://CVE-2025-61726.patch \
"

# Re-add the patch, but now corrected with the GODEBUG variable inserted in the right place.
SRC_URI:append = " \
    file://0001-net-url-add-urlmaxqueryparams-GODEBUG-to-limit-the-n.patch \
"
