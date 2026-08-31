SUMMARY = "Socket-tied interface naming for USB Wi-Fi adapters"
DESCRIPTION = "systemd .link policy naming a USB wireless interface after the SMARC \
USB signal it is attached to, so the name is fixed to the socket and identical on \
every unit; MAC-derived naming is kept as the catch-all for adapters attached \
below those sockets."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

USB_WIFI_LINK_FILES = "60-wlan-usb0.link 61-wlan-usb1.link 62-wlan-usb2.link \
                       63-wlan-usb3.link 64-wlan-usb4.link 65-wlan-usb5.link \
                       75-usb-wifi.link"

SRC_URI = "${@' '.join('file://' + f for f in d.getVar('USB_WIFI_LINK_FILES').split())} \
    file://README \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
# Module-level override token, shared by the UFS and QSPI machines; the .link
# files encode module wiring, which is valid on either.
COMPATIBLE_MACHINE = "^lec-mtk1200$"
INHIBIT_DEFAULT_DEPS = "1"

# nonarch_libdir, not systemd's own rootlibexecdir, which that recipe defines
# locally and is unset everywhere else.
do_install() {
	for f in ${USB_WIFI_LINK_FILES}; do
		install -Dm 0644 ${WORKDIR}/$f \
			${D}${nonarch_libdir}/systemd/network/$f
	done
	install -Dm 0644 ${WORKDIR}/README ${D}${datadir}/${BPN}/README
}

FILES:${PN} = "${@' '.join('${nonarch_libdir}/systemd/network/' + f for f in d.getVar('USB_WIFI_LINK_FILES').split())} \
    ${datadir}/${BPN}/README \
"

RDEPENDS:${PN} = "udev"
