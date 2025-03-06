require recipes-bsp/u-boot/u-boot-rollback.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/am62:"

SRC_URI:append:ti-soc = " \
    file://env_mmc.cfg \
    file://bootcommand.cfg \
"
