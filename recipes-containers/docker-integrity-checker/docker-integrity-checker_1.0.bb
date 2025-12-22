SUMMARY = "Service to check docker data integrity and try to recover from corruption"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://docker-integrity-checker.service \
    file://docker-integrity-checker.sh \
"

S = "${UNPACKDIR}/sources"

inherit systemd

SYSTEMD_PACKAGES:${PN} = "docker-integrity-checker"

SYSTEMD_SERVICE:${PN} = "docker-integrity-checker.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/docker-integrity-checker.service ${D}${systemd_unitdir}/system
    install -d ${D}${bindir}    
    install -m 0755 ${UNPACKDIR}/docker-integrity-checker.sh ${D}${bindir}
}
