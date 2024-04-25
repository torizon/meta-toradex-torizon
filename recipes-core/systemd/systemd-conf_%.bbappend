FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://system.conf-torizon \
    file://system.conf-docker \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install:append() {
	# Do not install default config file for wired networks, we use NetworkManager
	# by default
	rm -rf ${D}${systemd_unitdir}/network

	# Install systemd configuration snippet for TorizonCore
	install -d ${D}${systemd_unitdir}/system.conf.d/
	install -m 0644 ${WORKDIR}/system.conf-torizon ${D}${systemd_unitdir}/system.conf.d/10-${BPN}.conf
	install -m 0644 ${WORKDIR}/system.conf-docker ${D}${systemd_unitdir}/system.conf.d/20-docker.conf

	sed -i "s/@@MACHINE@@/${MACHINE}/g" ${D}${systemd_unitdir}/system.conf.d/10-${BPN}.conf
}
