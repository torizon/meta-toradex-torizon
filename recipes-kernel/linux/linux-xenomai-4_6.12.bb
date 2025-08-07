SUMMARY = "Linux kernel with Xenomai 4 patches and the EVL core"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME", True) == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-xenomai-4":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-xenomai-4 to enable it")
}

# Use BSP kernel recipe as the base
BASE_KERNEL_CONFIG:aarch64:tdx = "recipes-kernel/linux/linux-toradex-upstream_6.12.bb"
# meta-intel does not have a 6.12 recipe on scarthgap, only on master, so fix on 6.6
BASE_KERNEL_CONFIG:intel-corei7-64 = "recipes-kernel/linux/linux-intel_6.6.bb"
# Remove source code from
# https://github.com/YoeDistro/meta-intel/blob/scarthgap/recipes-kernel/linux/linux-intel_6.6.bb
SRC_URI:remove:intel-corei7-64 = " \
    git://github.com/intel/linux-intel-lts.git;protocol=https;name=machine;branch=${KBRANCH}; \
    file://0001-6.6-vt-conmakehash-improve-reproducibility.patch \
    file://0001-6.6-lib-build_OID_registry-fix-reproducibility-issues.patch \
"
require ${BASE_KERNEL_CONFIG}

# Include Torizon features
require recipes-kernel/linux/linux-torizon.inc
require recipes-kernel/linux/linux-toradex-kmeta.inc

# Path to search for BSP patches
FILESEXTRAPATHS:prepend:aarch64:tdx := \
    "${TOPDIR}/layers/meta-toradex-bsp-common/recipes-kernel/linux/linux-toradex-upstream_6.12: \
"
TDX_PATCHES ?= ""
FILESEXTRAPATHS:prepend:intel-corei7-64 := " \
    ${TOPDIR}/layers/meta-intel/recipes-kernel/linux/linux-intel: \
"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

LINUX_REPO = "git://source.denx.de/Xenomai/xenomai4/linux-evl.git"
SRC_URI = " \
    ${LINUX_REPO};protocol=https;branch=${KBRANCH};name=machine \
    ${KCONFIG_REPO};protocol=https;type=kmeta;name=meta;branch=${KMETA_BRANCH};destsuffix=${KMETA} \
    git://${KMETAREPOSITORY};protocol=${KMETAPROTOCOL};type=kmeta;name=torizon-meta;branch=${KMETABRANCH};destsuffix=${KMETATORIZON} \
    ${TDX_PATCHES} \
    \
    file://xenomai-common.cfg \
    file://xenomai4.cfg \
"

LINUX_VERSION = "6.12.8"
KBRANCH = "v6.12.y-evl-rebase"
KMETA_BRANCH:aarch64:tdx = "main"
KERNEL_VERSION_SANITY_SKIP = "1"
SRCREV_machine = "0d91a2ee592e2110bdbdb2d751f0596b475464d2"
SRCREV_machine:use-head-next = "${AUTOREV}"
SRCREV_meta:aarch64:tdx = "${SRCREV_meta-toradex-bsp}"
SRCREV_meta:use-head-next = "${AUTOREV}"

# meta-intel does not have a 6.12 recipe on scarthgap, only on master, so fix on 6.6
LINUX_VERSION:intel-corei7-64 = "6.6.69"
KBRANCH:intel-corei7-64 = "v6.6.y-evl-rebase"
KMETA_BRANCH:intel-corei7-64 = "yocto-6.6"
SRCREV_machine:intel-corei7-64 = "a183f2498555d05cddd096212d5b81f10894a762"
