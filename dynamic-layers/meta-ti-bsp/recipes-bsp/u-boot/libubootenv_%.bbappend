SRC_URI:append:am62xx-evm = " file://fw_env.config"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install:append() {
    install -d ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}/${sysconfdir}/
}
