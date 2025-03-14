require recipes-bsp/u-boot/u-boot-rollback.inc

FILESEXTRAPATHS:prepend:common-ti := "${THISDIR}/am62:"

SRC_URI:append:common-ti = " \
    file://env_mmc.cfg \
    file://bootcommand.cfg \
"
