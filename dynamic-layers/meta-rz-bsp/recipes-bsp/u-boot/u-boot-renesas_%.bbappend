require recipes-bsp/u-boot/u-boot-rollback.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-renesas:"

SRC_URI += "file://0001-include-configs-smarc-rzv2l-add-required-Torizon-boo.patch"
