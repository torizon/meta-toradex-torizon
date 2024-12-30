require linux-xenomai-3-ipipe.inc

SUMMARY = "Linux kernel for Xenomai 3 with Dovetail and the Cobalt core patches"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME", True) == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-xenomai-3":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-xenomai-3 to enable it")
}

KBRANCH = "ipipe-x86-5.4.y"
KMETA_BRANCH = "yocto-5.4"
XENBRANCH = "stable/v3.1.x"
#IPIPEBRANCH = "ipipe-x86-5.4.y"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

DEPENDS += "elfutils-native openssl-native util-linux-native"

LINUX_VERSION ?= "5.4.255"
#SRCREV_machine ?= "ea2d8185fa954bc9c9a0c703cc4ff9c4b5cb8df5"
SRCREV_machine ?= "256f1388f5960b2cc22044b2acc38d4e82187211"
SRCREV_meta ?= "c917f683a6394ae00f81139ae57ae0112d4b7528"
SRCREV_xenomai ?= "aa39d890c4fbf14d1300946ca90a6aa6dcb649cb"
#SRCREV_ipipe ?= "256f1388f5960b2cc22044b2acc38d4e82187211"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/security/security.scc"

do_patch:append () {
    ${WORKDIR}/xenomai/scripts/prepare-kernel.sh --linux=${STAGING_KERNEL_DIR} --arch=x86
}
