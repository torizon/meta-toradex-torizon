require recipes-bsp/u-boot/u-boot-rollback.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/imx95:"

SRC_URI:append = " \
    file://bootcommand.cfg \
"
