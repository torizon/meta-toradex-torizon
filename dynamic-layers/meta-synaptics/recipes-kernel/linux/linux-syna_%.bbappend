require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://synaptics-core.cfg \
    file://synaptics-community.cfg \
    file://CVE-2026-31431-01-crypto-scatterwalk-Backport-memcpy_sglist.patch \
    file://CVE-2026-31431-02-crypto-algif_aead-Revert-to-operating-out-of-place.patch \
    file://CVE-2026-43284-xfrm-esp-avoid-in-place-decrypt-on-shared-skb-frags.patch \
"

# luna-sl1680.conf adds 'sl1680' to its own MACHINEOVERRIDES, so a
# SRC_URI:append:sl1680 override would also reach Luna. Test MACHINE directly
# to keep these fragments scoped to the SL1680 vendor board only.
SRC_URI:append = "${@bb.utils.contains('MACHINE', 'sl1680', ' file://sl1680-core.cfg file://sl1680-community.cfg', '', d)}"

# SDIO support in the device tree, and kernel configuration fragments, for Luna SL1680
SRC_URI:append:luna-sl1680 = " \
    file://0001-dolphin-rdk.dts-enable-sdio-connection.patch \
    file://luna-sl1680-core.cfg \
    file://luna-sl1680-community.cfg \
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
    ( cd ${S}/drivers/synaptics && git apply ${WORKDIR}/0001-dhd_linux-fix-issue-which-freezes-sl1680-chips-board.patch )
}

# Synaptics kernel recipe is overwriting INITRAMFS_IMAGE
# We need to set it back to initramfs-ostree-torizon-image
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"

CVE_STATUS[CVE-2026-31431] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=19d43105a97be0810edbda875f2cd03f30dc130c"
CVE_STATUS[CVE-2026-43284] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=50ed1e7873100f77abad20fd31c51029bc49cd03"
