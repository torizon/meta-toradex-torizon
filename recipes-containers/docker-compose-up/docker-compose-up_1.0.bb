SUMMARY = "Docker Compose up service"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docker-compose.service;beginline=1;endline=1;md5=50e1eb67aba60e41e02887b7d25adfb2"

SRC_URI = " \
    file://docker-compose.service \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit systemd

SYSTEMD_PACKAGES = "docker-compose-up"

SYSTEMD_SERVICE:${PN} = " docker-compose.service"

do_install() {
	install -d ${D}${nonarch_base_libdir}/systemd/system
	install -m 0644 ${UNPACKDIR}/docker-compose.service ${D}${nonarch_base_libdir}/systemd/system
}
