# From linux-toradex-upstream as base
require recipes-kernel/linux/linux-toradex-upstream_6.12.bb

# From linux-toradex%.bbappend for Torizon
require recipes-kernel/linux/linux-torizon.inc
require recipes-kernel/linux/linux-toradex-kmeta.inc

SUMMARY = "Linux kernel with Xenomai 4 patches and the EVL core"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME", True) == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-xenomai-4":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-xenomai-4 to enable it")
}

LINUX_REPO = "git://source.denx.de/Xenomai/xenomai4/linux-evl.git"


FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " \
           file://xenomai-common.cfg \
           file://xenomai4.cfg \
	  "

PV = "6.12"
LINUX_VERSION ?= "6.12.8"
KBRANCH = "v6.12.y-evl-rebase"
KERNEL_VERSION_SANITY_SKIP = "1"
SRCREV_machine = "0d91a2ee592e2110bdbdb2d751f0596b475464d2"
SRCREV_machine:use-head-next = "${AUTOREV}"
