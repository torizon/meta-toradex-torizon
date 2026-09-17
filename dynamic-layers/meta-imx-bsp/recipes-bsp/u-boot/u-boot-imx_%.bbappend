require ${@bb.utils.contains_any('MACHINE', 'imx95-19x19-verdin imx93frdm', 'recipes-bsp/u-boot/u-boot-rollback.inc', '', d)}
# `require` is resolved before overrides apply, so the family is read out of MACHINEOVERRIDES.
require ${@'recipes-bsp/u-boot/u-boot-rollback.inc' if 'common-imx6' in (d.getVar('MACHINEOVERRIDES') or '').split(':') else ''}

FILESEXTRAPATHS:prepend:imx95-19x19-verdin := "${THISDIR}/files:"
FILESEXTRAPATHS:prepend:imx93-11x11-lpddr4x-frdm := "${THISDIR}/files:"

SRC_URI:append:imx95-19x19-verdin = " \
    file://bootcommand.cfg \
"

SRC_URI:append:imx93-11x11-lpddr4x-frdm = " \
    file://env_mmc.cfg \
    file://bootcommand.cfg \
"

FILESEXTRAPATHS:prepend:mx6sx-generic-bsp := "${THISDIR}/files/mx6sx:"
FILESEXTRAPATHS:prepend:common-imx6 := "${THISDIR}/files/common-imx6:"
FILESEXTRAPATHS:prepend:imx6sxsabresd := "${THISDIR}/files/imx6sxsabresd:"

SRC_URI:append:mx6sx-generic-bsp = " file://no-ldo-bypass.cfg"

SRC_URI:append:common-imx6 = " file://torizon-imx6sx.env file://torizon-imx6sx-env.cfg"

# A second i.MX 6 machine whose U-Boot builds elsewhere overrides this.
UBOOT_ENV_BOARD_DIR ?= "board/freescale/mx6sxsabresd"

do_configure:prepend:common-imx6() {
    install -m 0644 ${WORKDIR}/torizon-imx6sx.env ${S}/${UBOOT_ENV_BOARD_DIR}/torizon-imx6sx.env
}

SRC_URI:append:imx6sxsabresd = " file://torizon-boot.cfg file://fastboot.cfg"
