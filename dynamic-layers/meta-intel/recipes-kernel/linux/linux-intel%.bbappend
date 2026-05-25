require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Upstream Kernel now depends on lz4 instead of lz4f, and it
# breaks the build for Scarthgap.
DEPENDS += "lz4-native"

SRC_URI += "\
    file://intel-corei7-64.cfg \
"

RDEPENDS_${PN}:append = " linux-firmware-rtl8188 "

SRC_URI += " \
    file://CVE-2026-31431-01-scatterwalk-backport-memcpy_sglist.patch \
    file://CVE-2026-31431-02-algif_aead-use-memcpy_sglist.patch \
    file://CVE-2026-31431-03-algif_aead-revert-to-out-of-place.patch \
    file://CVE-2026-43284-xfrm-esp-avoid-in-place-decrypt.patch \
"


CVE_STATUS[CVE-2026-31431] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=3115af9644c342b356f3f07a4dd1c8905cd9a6fc"
CVE_STATUS[CVE-2026-43284] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=50ed1e7873100f77abad20fd31c51029bc49cd03"
