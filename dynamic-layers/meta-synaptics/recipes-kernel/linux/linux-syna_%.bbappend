require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://fragment.cfg \
"

# Synaptics kernel recipe is overwriting INITRAMFS_IMAGE
# We need to set it back to initramfs-ostree-torizon-image
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"
