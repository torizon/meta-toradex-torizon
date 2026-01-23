require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://fragment.cfg \
"

# Enable SDIO support in the device tree for Luna SL1680
SRC_URI:append:luna-sl1680 = " \
    file://0001-dolphin-rdk.dts-enable-sdio-connection.patch \
    file://luna-sl1680.cfg \
"

# Synaptics kernel recipe is overwriting INITRAMFS_IMAGE
# We need to set it back to initramfs-ostree-torizon-image
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"
