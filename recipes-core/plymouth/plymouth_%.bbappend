FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://0001-disable-boot-splash-later.patch \
    file://0001-themes-only-install-spinner.patch \
    file://torizonlogo-white.png \
    file://spinner.plymouth \
"

PACKAGECONFIG = "drm udev"

EXTRA_OECONF += "--with-runtimedir=/run"

do_install:append () {
    install -m 0644 ${WORKDIR}/torizonlogo-white.png ${D}${datadir}/plymouth/themes/spinner/watermark.png
    install -m 0644 ${WORKDIR}/spinner.plymouth ${D}${datadir}/plymouth/themes/spinner/spinner.plymouth
}
