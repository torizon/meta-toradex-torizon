FILESEXTRAPATHS:prepend := "${THISDIR}/files/${MACHINE}:"

SRC_URI:append = " file://fw_env.config"

do_install:append() {
    install -d ${D}/${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}/${sysconfdir}/
}
