require recipes-bsp/u-boot/u-boot-ota.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-ota:"

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-stm32mp:"

SRC_URI += "file://0001-STM32MP2-27-Use-otaroot-label-to-detect-the-partitio.patch"
