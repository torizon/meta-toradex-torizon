require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://drm.cfg \
"
KERNEL_CONFIG_FRAGMENTS += "${WORKDIR}/drm.cfg"
