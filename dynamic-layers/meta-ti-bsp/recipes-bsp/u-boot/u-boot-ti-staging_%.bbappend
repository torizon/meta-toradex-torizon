FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://env_mmc.cfg"

require recipes-bsp/u-boot/u-boot-rollback.inc
