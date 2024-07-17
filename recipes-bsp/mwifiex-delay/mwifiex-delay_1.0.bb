SUMMARY = "Workaround MWiFiEx long boot time"
DESCRIPTION = "Delays the loading of mwifiex drivers till network-online.target is reached and results in a shorter boot time"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit systemd

SRC_URI = " \
    file://mwifiex-blacklist.conf \
    file://mwifiex-delay.service \
    file://mwifiex-delay.sh \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SYSTEMD_SERVICE:${PN} = "mwifiex-delay.service"

do_install () {
    install -d ${D}${sysconfdir}/modprobe.d/
    install -m 0755 ${UNPACKDIR}/mwifiex-blacklist.conf ${D}${sysconfdir}/modprobe.d/
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/mwifiex-delay.service ${D}${systemd_unitdir}/system/
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/mwifiex-delay.sh ${D}${sbindir}/
}
