SUMMARY = "Auto-provisioning is a service to automatically provision \
the device to the Platform Services"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://auto-provisioning.service \
    file://auto-provisioning.sh \
"

S = "${UNPACKDIR}/sources"

RDEPENDS:${PN} = "bash jq"

inherit allarch systemd

SYSTEMD_SERVICE:${PN} = "auto-provisioning.service"

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/auto-provisioning.service ${D}${systemd_unitdir}/system
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/auto-provisioning.sh ${D}${sbindir}
}
