require linux-xenomai-3-dovetail.inc

SUMMARY = "Linux kernel for Xenomai 3 with Dovetail and the Cobalt core patches"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME", True) == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-xenomai-3":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-xenomai-3 to enable it")
}

KBRANCH = "v5.10.y-dovetail"
KMETA_BRANCH = "yocto-5.10"
XENBRANCH = "stable/v3.2.x"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS += "elfutils-native openssl-native util-linux-native"

LINUX_VERSION ?= "5.10.209"
SRCREV_machine ?= "e2e46a0e4e4bf82d418f589a23a8d04df996b480"
SRCREV_meta ?= "603507f09e4a22a650e37fb9dcfbcb69ceb36841"
SRCREV_xenomai ?= "704ea7dab109c6ced8dee3a8377fbf72a977a0fc"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/security/security.scc"

do_patch:append () {
    ${WORKDIR}/xenomai/scripts/prepare-kernel.sh --linux=${STAGING_KERNEL_DIR} --arch=x86
}
