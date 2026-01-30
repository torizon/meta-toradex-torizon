require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://intel-corei7-64.cfg \
"

RDEPENDS_${PN}:append = " linux-firmware-rtl8188 "
