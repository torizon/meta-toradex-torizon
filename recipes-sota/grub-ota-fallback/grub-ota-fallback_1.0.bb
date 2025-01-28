SUMMARY = "Adds fallback logic to GRUB boot script"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://99_fallback_logic \
    file://grubenv-create.service \
    file://fw_printenv \
    file://fw_setenv \
"

S = "${WORKDIR}"

inherit systemd

RDEPENDS:${PN} = "bash"

SYSTEMD_SERVICE:${PN} = "grubenv-create.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/fw_printenv ${D}${bindir}/fw_printenv
    install -m 0755 ${WORKDIR}/fw_setenv ${D}${bindir}/fw_setenv

    install -d ${D}${sysconfdir}/ostree.d
    install -m 0755 ${WORKDIR}/99_fallback_logic ${D}${sysconfdir}/ostree.d/99_fallback_logic

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/grubenv-create.service ${D}${systemd_unitdir}/system/grubenv-create.service
}

FILES:${PN} += "\
    ${systemd_unitdir}/system/grubenv-create.service \
    ${bindir}/fw_printenv \
    ${bindir}/fw_setenv \
"
