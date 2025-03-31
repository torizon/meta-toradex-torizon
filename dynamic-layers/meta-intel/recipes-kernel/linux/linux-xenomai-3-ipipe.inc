# from meta-intel
require recipes-kernel/linux/linux-intel.inc

# from common-torizon
require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = " \
           git://source.denx.de/Xenomai/ipipe-x86.git;protocol=https;branch=${KBRANCH};name=machine; \
           git://source.denx.de/Xenomai/xenomai.git;protocol=https;branch=${XENBRANCH};name=xenomai;destsuffix=xenomai; \
           ${KERNEL_CONFIG_URI} \
           file://xenomai-common.cfg \
           file://xenomai3.cfg \
           file://kernel5.4.cfg \
	  "
