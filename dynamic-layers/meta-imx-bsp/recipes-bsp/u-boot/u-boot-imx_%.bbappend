require ${@bb.utils.contains_any('MACHINE', 'imx95-19x19-verdin imx93frdm', 'recipes-bsp/u-boot/u-boot-rollback.inc', '', d)}

FILESEXTRAPATHS:prepend:imx95-19x19-verdin := "${THISDIR}/files:"
FILESEXTRAPATHS:prepend:imx93-11x11-lpddr4x-frdm := "${THISDIR}/files:"

SRC_URI:append:imx95-19x19-verdin = " \
    file://bootcommand.cfg \
"

SRC_URI:append:imx93-11x11-lpddr4x-frdm = " \
    file://env_mmc.cfg \
    file://bootcommand.cfg \
"
