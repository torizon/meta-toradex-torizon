# From linux-toradex-upstream as base
require recipes-kernel/linux/linux-toradex-upstream_6.12.bb

# From linux-toradex%.bbappend for Torizon
require recipes-kernel/linux/linux-torizon.inc
require recipes-kernel/linux/linux-toradex-kmeta.inc

SUMMARY = "Linux kernel for Xenomai 3 with Dovetail and the Cobalt core patches"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME", True) == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-xenomai-3":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-xenomai-3 to enable it")
}

LINUX_REPO = "git://source.denx.de/Xenomai/linux-dovetail.git"
XENBRANCH = "master"


FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " \
           git://source.denx.de/Xenomai/xenomai.git;protocol=https;branch=${XENBRANCH};name=xenomai;destsuffix=xenomai; \
           file://xenomai-common.cfg \
           file://xenomai3.cfg \
	  "

PV = "6.12"
LINUX_VERSION ?= "6.12.8"
KBRANCH = "v6.12.y-dovetail-rebase"
KERNEL_VERSION_SANITY_SKIP = "1"
SRCREV_machine = "ed55d775d3b3df3948c43598e7a68be7c0737392"
SRCREV_machine:use-head-next = "${AUTOREV}"
SRCREV_xenomai ?= "c46e3e2c5a9fe95ccac1772882c6451fe2f719b6"

do_patch:append () {
    ${WORKDIR}/xenomai/scripts/prepare-kernel.sh --linux=${STAGING_KERNEL_DIR} --arch=${ARCH}
}
