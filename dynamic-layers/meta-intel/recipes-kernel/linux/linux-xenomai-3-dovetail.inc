# from meta-intel
require recipes-kernel/linux/linux-yocto.inc
require recipes-kernel/linux/meta-intel-compat-kernel.inc

# from common-torizon
require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

KERNEL_CONFIG_URI ?= "git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=${KMETA_BRANCH};destsuffix=${KMETA}"

SRC_URI = " \
           git://source.denx.de/Xenomai/linux-dovetail.git;protocol=https;branch=${KBRANCH};name=machine; \
           git://source.denx.de/Xenomai/xenomai.git;protocol=https;branch=${XENBRANCH};name=xenomai;destsuffix=xenomai; \
           ${KERNEL_CONFIG_URI} \
           file://xenomai-common.cfg \
           file://xenomai3.cfg \
	  "

LINUX_VERSION_EXTENSION ??= "-intel-pk-${LINUX_KERNEL_TYPE}"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "2"

COMPATIBLE_MACHINE ?= "(intel-corei7-64|intel-core2-32)"

KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"
