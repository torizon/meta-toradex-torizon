FILESEXTRAPATHS:prepend := "${THISDIR}/files/${MACHINE}:"

# The package gains machine-specific content below.
PACKAGE_ARCH:lec-mtk-i1200-ufs = "${MACHINE_ARCH}"

SRC_URI:append:lec-mtk-i1200-ufs = " file://fw_env.config file://61-ufs-boot1.rules"

do_install:append:lec-mtk-i1200-ufs() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/
    install -Dm 0644 ${WORKDIR}/61-ufs-boot1.rules \
        ${D}${nonarch_base_libdir}/udev/rules.d/61-ufs-boot1.rules
}

FILES:${PN}:append:lec-mtk-i1200-ufs = " ${nonarch_base_libdir}/udev/rules.d/61-ufs-boot1.rules"
RDEPENDS:${PN}:append:lec-mtk-i1200-ufs = " udev"
