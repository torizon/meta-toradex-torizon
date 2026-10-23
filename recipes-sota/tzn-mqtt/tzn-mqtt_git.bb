SUMMARY = "tzn-mqtt"
HOMEPAGE = "https://github.com/torizon/tzn-mqtt"
LICENSE = "Apache-2.0"

inherit cargo cargo-update-recipe-crates systemd

SRC_URI += " \
    git://github.com/torizon/tzn-mqtt.git;protocol=https;branch=main \
    file://tzn-mqtt.service \
"

SRCREV = "452f6c710e5878a98bb775b9ffa57a7498c26c1e"
SRCREV:use-head-next = "${AUTOREV}"

S = "${WORKDIR}/git"
PV = "0.0+git${SRCPV}"
CARGO_SRC_DIR = ""

LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
"

SYSTEMD_SERVICE:${PN} = "tzn-mqtt.service"

require ${BPN}-crates.inc

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/tzn-mqtt.service ${D}${systemd_unitdir}/system/tzn-mqtt.service
}
