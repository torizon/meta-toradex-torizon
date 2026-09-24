FILESEXTRAPATHS:prepend:imx93-11x11-lpddr4x-frdm := "${THISDIR}/files:"

SRC_URI:append:imx93-11x11-lpddr4x-frdm = " file://fw_env.config"

PACKAGE_ARCH:imx93-11x11-lpddr4x-frdm = "${MACHINE_ARCH}"

do_install:append:imx93-11x11-lpddr4x-frdm() {
    install -d ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}/${sysconfdir}/
}

# Raw MMC region compiled into U-Boot: greenboot's bootcount reset needs it.
FILESEXTRAPATHS:prepend:common-imx6 := "${THISDIR}/files:"

SRC_URI:append:common-imx6 = " file://fw_env.config"
PACKAGE_ARCH:common-imx6 = "${MACHINE_ARCH}"

do_install:append:common-imx6 () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}

FILES:${PN}-bin:append:common-imx6 = " ${sysconfdir}/fw_env.config"
