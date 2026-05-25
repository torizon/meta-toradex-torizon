require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://0001-crypto-scatterwalk-Backport-memcpy_sglist.patch \
    file://cve-2026-31431.patch \
    file://cve-2026-43284.patch \
"

CVE_STATUS[CVE-2026-31431] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=19d43105a97be0810edbda875f2cd03f30dc130c"
CVE_STATUS[CVE-2026-43284] = "fixed-version: Patch from https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git/patch/?id=50ed1e7873100f77abad20fd31c51029bc49cd03"
