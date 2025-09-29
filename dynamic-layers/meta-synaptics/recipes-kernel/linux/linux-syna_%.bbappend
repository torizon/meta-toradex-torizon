require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://fragment.cfg \
"

# Enable SDIO support in the device tree for Winglet
SRC_URI:append:winglet = " \
    file://0001-dolphin-rdk.dts-enable-sdio-connection.patch \
    file://winglet.cfg \
"

# Synaptics kernel recipe is overwriting INITRAMFS_IMAGE
# We need to set it back to initramfs-ostree-torizon-image
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"
