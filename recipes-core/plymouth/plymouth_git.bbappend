FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://0001-disable-boot-splash-later.patch \
    file://torizonlogo-white.png \
    file://spinner.plymouth \
"

PACKAGECONFIG = "pango drm"

do_install_append () {
    install -m 0644 ${WORKDIR}/torizonlogo-white.png ${D}${datadir}/plymouth/themes/spinner/watermark.png
    install -m 0644 ${WORKDIR}/spinner.plymouth ${D}${datadir}/plymouth/themes/spinner/spinner.plymouth
}
