FILESEXTRAPATHS:prepend:common-ti := "${THISDIR}/am62:"

SRC_URI:append:common-ti = " file://fw_env.config"

PACKAGE_ARCH:common-ti = "${MACHINE_ARCH}"

do_install:append:common-ti() {
    install -d ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}/${sysconfdir}/
}
