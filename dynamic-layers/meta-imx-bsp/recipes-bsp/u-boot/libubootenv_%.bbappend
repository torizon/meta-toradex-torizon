FILESEXTRAPATHS:prepend:imx93-11x11-lpddr4x-frdm := "${THISDIR}/files:"

SRC_URI:append:imx93-11x11-lpddr4x-frdm = " file://fw_env.config"

PACKAGE_ARCH:imx93-11x11-lpddr4x-frdm = "${MACHINE_ARCH}"

do_install:append:imx93-11x11-lpddr4x-frdm() {
    install -d ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}/${sysconfdir}/
}
