FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://0001-disable-boot-splash-later.patch \
    file://torizonlogo-white.png \
    file://spinner.plymouth \
"

PLYMOUTH_THEMES = "spinner"
PACKAGECONFIG = "drm udev ${PLYMOUTH_THEMES} ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

do_install:append () {
    install -m 0644 ${UNPACKDIR}/torizonlogo-white.png ${D}${datadir}/plymouth/themes/spinner/watermark.png
    install -m 0644 ${UNPACKDIR}/spinner.plymouth ${D}${datadir}/plymouth/themes/spinner/spinner.plymouth
}
