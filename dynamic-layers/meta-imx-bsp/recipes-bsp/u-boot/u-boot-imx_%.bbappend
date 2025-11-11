require ${@bb.utils.contains_any('MACHINE', 'imx95-19x19-verdin', 'recipes-bsp/u-boot/u-boot-rollback.inc', '', d)}

FILESEXTRAPATHS:prepend:imx95-19x19-verdin := "${THISDIR}/files:"

SRC_URI:append:imx95-19x19-verdin = " \
    file://bootcommand.cfg \
"
