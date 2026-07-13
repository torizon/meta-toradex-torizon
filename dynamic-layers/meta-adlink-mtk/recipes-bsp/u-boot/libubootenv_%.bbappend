FILESEXTRAPATHS:prepend := "${THISDIR}/files/${MACHINE}:"

SRC_URI:append:lec-mtk-i1200-ufs = " file://fw_env.config"

do_install:append:lec-mtk-i1200-ufs() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/
}
