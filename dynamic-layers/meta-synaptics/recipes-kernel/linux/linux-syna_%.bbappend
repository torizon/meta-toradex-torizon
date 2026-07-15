require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://fragment.cfg \
"

# Enable SDIO support in the device tree for Luna SL1680
SRC_URI:append:luna-sl1680 = " \
    file://0001-dolphin-rdk.dts-enable-sdio-connection.patch \
    file://luna-sl1680.cfg \
    file://0001-dhd_linux-fix-issue-which-freezes-sl1680-chips-board.patch;patchdir=drivers/synaptics \
"

# 0001-dhd_linux-fix-issue-which-freezes-sl1680-chips-board.patch touches
# drivers/synaptics/net/wireless/bcmdhd/dhd_linux.c, which lives inside a nested
# git repo (drivers/synaptics has its own .git). kernel-yocto.bbclass's do_patch
# only understands patchdir="kernel-meta" as a special case (see find_patches() in
# kernel-yocto.bbclass) - any other patchdir value, including ours, is silently
# excluded from the applied series instead of being applied or erroring out. The
# patchdir parameter above only serves to keep it out of that automatic series;
# apply it for real here, directly against the nested repo.
do_patch:append:luna-sl1680() {
    ( cd ${S}/drivers/synaptics && git am ${WORKDIR}/0001-dhd_linux-fix-issue-which-freezes-sl1680-chips-board.patch )
}

# Synaptics kernel recipe is overwriting INITRAMFS_IMAGE
# We need to set it back to initramfs-ostree-torizon-image
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"
