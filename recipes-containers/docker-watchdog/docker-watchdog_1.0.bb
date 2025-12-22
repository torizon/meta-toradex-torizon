SUMMARY = "Monitors Docker host's container health status, restarting unhealthy containers"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://docker-watchdog.service \
    file://docker-watchdog.sh \
"

S = "${UNPACKDIR}/sources"

inherit systemd

SYSTEMD_PACKAGES:${PN} = "docker-watchdog"

SYSTEMD_SERVICE:${PN} = "docker-watchdog.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/docker-watchdog.service ${D}${systemd_unitdir}/system
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/docker-watchdog.sh ${D}${bindir}
}
