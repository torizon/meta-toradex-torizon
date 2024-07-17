FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://inputrc.torizon"

do_install:append () {
	install -m 0644 ${UNPACKDIR}/inputrc.torizon ${D}${sysconfdir}/inputrc
}
