require linux-xenomai-4-evl.inc

SUMMARY = "Linux kernel with Xenomai 4 patches and the EVL core"

# Skip processing of this recipe if it is not explicitly specified as the
# PREFERRED_PROVIDER for virtual/kernel. This avoids errors when trying
# to build multiple virtual/kernel providers, e.g. as dependency of
# core-image-rt-sdk, core-image-rt.
python () {
    if d.getVar("KERNEL_PACKAGE_NAME", True) == "kernel" and d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-xenomai-4":
        raise bb.parse.SkipPackage("Set PREFERRED_PROVIDER_virtual/kernel to linux-xenomai-4 to enable it")
}

KBRANCH = "v5.10.y-evl-rebase"
KMETA_BRANCH = "yocto-5.10"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

DEPENDS += "elfutils-native openssl-native util-linux-native"

LINUX_VERSION ?= "5.10.209"
SRCREV_machine ?= "a48a4304d51f6d106e9b6e95ff63e95d3e502b03"
SRCREV_meta ?= "603507f09e4a22a650e37fb9dcfbcb69ceb36841"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/security/security.scc"
