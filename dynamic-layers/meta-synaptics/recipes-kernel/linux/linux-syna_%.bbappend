require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://fragment.cfg \
    file://CVE-2026-31431-01-crypto-scatterwalk-Backport-memcpy_sglist.patch \
    file://CVE-2026-31431-02-crypto-algif_aead-Revert-to-operating-out-of-place.patch \
    file://CVE-2026-43284-xfrm-esp-avoid-in-place-decrypt-on-shared-skb-frags.patch \
"

# Enable SDIO support in the device tree for Luna SL1680
SRC_URI:append:luna-sl1680 = " \
    file://0001-dolphin-rdk.dts-enable-sdio-connection.patch \
    file://luna-sl1680.cfg \
"

# Synaptics kernel recipe is overwriting INITRAMFS_IMAGE
# We need to set it back to initramfs-ostree-torizon-image
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"

CVE_STATUS[CVE-2026-31431] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=19d43105a97be0810edbda875f2cd03f30dc130c"
CVE_STATUS[CVE-2026-43284] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=50ed1e7873100f77abad20fd31c51029bc49cd03"

