FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://0001-fs-fat-Don-t-error-when-mtime-is-0.patch \
"
