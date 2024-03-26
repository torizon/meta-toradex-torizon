FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

require linux-torizon.inc

# use 6.6 kernel to get composefs fsverity support
SRC_URI:remove:cfs-signed = "git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.10;destsuffix=${KMETA}"
SRC_URI:remove:cfs-signed = "git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.15;destsuffix=${KMETA}"
SRC_URI:append:cfs-signed = " git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.6;destsuffix=${KMETA}"

SRC_URI += "\
    file://torizoncore.scc \
"

SRC_URI:append:cfs-support = " \
    file://composefs.scc \
"

SRC_URI:append:cfs-signed = " \
    file://composefs-fsverity.scc \
"

KBRANCH:qemuarm64:cfs-signed = "v6.6/standard/qemuarm64"
SRCREV_machine:qemuarm64:cfs-signed = "a04baee60b5a7cf4d9c0c2f4856c6d5bb9b98074"
SRCREV_meta:cfs-signed = "8cd63077f67a0f7ff639a2ff24c82b09d71048a4"
LINUX_VERSION:cfs-signed = "6.6.20"

inherit toradex-kernel-localversion
