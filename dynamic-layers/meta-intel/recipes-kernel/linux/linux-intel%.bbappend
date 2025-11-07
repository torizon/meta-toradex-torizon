require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Upstream Kernel now depends on lz4 instead of lz4f, and it
# breaks the build for Scarthgap.
DEPENDS += "lz4-native"

SRC_URI += "\
    file://intel-corei7-64.cfg \
"
