FILESEXTRAPATHS:prepend:beagley-ai := "${THISDIR}/beagley-ai:"

SRC_URI:append:beagley-ai = " file://fw_env.config"

PACKAGE_ARCH:beagley-ai = "${MACHINE_ARCH}"

do_install:append:beagley-ai() {
    install -d ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}/${sysconfdir}/
}
