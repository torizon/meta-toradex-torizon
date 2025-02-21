SUMMARY = "Linux kernel with Xenomai 3 Dovetail and the Cobalt core patches"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME", True) == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-xenomai-3":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-xenomai-3 to enable it")
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

LINUX_REPO = "git://source.denx.de/Xenomai/linux-dovetail.git"
SRC_URI = " \
    ${LINUX_REPO};protocol=https;branch=${KBRANCH};name=machine \
    ${KCONFIG_REPO};protocol=https;type=kmeta;name=meta;branch=${KMETA_BRANCH};destsuffix=${KMETA} \
    git://${KMETAREPOSITORY};protocol=${KMETAPROTOCOL};type=kmeta;name=torizon-meta;branch=${KMETABRANCH};destsuffix=${KMETATORIZON} \
    ${TDX_PATCHES} \
    \
    file://xenomai-common.cfg \
    file://xenomai3.cfg \
"

LINUX_VERSION = "6.12.8"
KBRANCH = "v6.12.y-dovetail-rebase"
KMETA_BRANCH:aarch64:tdx = "main"
KMETA_BRANCH:intel-corei7-64 = "yocto-6.6"
KERNEL_VERSION_SANITY_SKIP = "1"
SRCREV_machine = "ed55d775d3b3df3948c43598e7a68be7c0737392"
SRCREV_machine:use-head-next = "${AUTOREV}"
SRCREV_meta:aarch64:tdx = "${SRCREV_meta-toradex-bsp}"
SRCREV_meta:use-head-next = "${AUTOREV}"

# meta-intel does not have a 6.12 recipe on scarthgap, only on master, so fix on 6.6
LINUX_VERSION:intel-corei7-64 = "6.6.69"
KBRANCH:intel-corei7-64 = "v6.6.y-dovetail-rebase"
KMETA_BRANCH:intel-corei7-64 = "yocto-6.6"
SRCREV_machine:intel-corei7-64 = "2899dc64fca2535f2cc699e29538b381a21adbc0"

# For Xenomai 3 the patches are not kept in the kernel source repo
XENBRANCH = "master"
SRC_URI:append = " \
    git://source.denx.de/Xenomai/xenomai.git;protocol=https;branch=${XENBRANCH};name=xenomai;destsuffix=xenomai; \
"

SRCREV_xenomai ?= "c46e3e2c5a9fe95ccac1772882c6451fe2f719b6"

do_patch:append () {
    # Avoid error "ERROR: linux-xenomai-3-6.6.69+git-r0 do_kernel_configcheck: config analysis failed when running 'symbol_why.py"
    # due to https://source.denx.de/Xenomai/xenomai/-/commit/ec0fc2b4491e1e2f11ecb09f420cadeecdbbd02e
    # Just make sure to not enable XENO_DRIVERS_NET_FEC
    sed -i '/depends on BROKEN/d' ${WORKDIR}/xenomai/kernel/drivers/net/drivers/Kconfig

    # Apply the Xenomai patch
    ${WORKDIR}/xenomai/scripts/prepare-kernel.sh --linux=${STAGING_KERNEL_DIR} --arch=${ARCH}
}
