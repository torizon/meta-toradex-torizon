FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_AUTO_ENABLE ?= "disable"

SRC_URI += "file://zram.conf"

do_install:append() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/zram.conf ${D}${sysconfdir}/default/zram
}
