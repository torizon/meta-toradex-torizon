FILESEXTRAPATHS:prepend:ti-soc := "${THISDIR}/am62:"

SRC_URI:append:ti-soc = " file://fw_env.config"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install:append:ti-soc() {
    install -d ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}/${sysconfdir}/
}
