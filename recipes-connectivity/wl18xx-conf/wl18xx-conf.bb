SUMMARY = "PHY configuration for the TI WiLink 8 Wi-Fi module of imx6sx-blaze"
DESCRIPTION = "\
The wl18xx driver reads ti-connectivity/wl18xx-conf.bin at probe and applies \
the front-end, antenna and TX power settings it carries. Without it the driver \
warns and falls back to built-in defaults. The file is the board vendor's and \
is not carried here: the builder supplies it."

# The file is the board vendor's data and is neither stored nor distributed with
# this layer. Place it at WL18XX_CONF_BIN before building (docs/README-nxp.md);
# the default is next to the build directory, in the directory `repo sync` ran in.
# Without the file the package is empty, the build warns, and the image carries no
# calibration; the recipe still builds then, because the machine recommends it.
WL18XX_CONF_BIN ?= "${TOPDIR}/../wl18xx-conf.bin"

LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "imx6sx-blaze"

python () {
    path = d.getVar('WL18XX_CONF_BIN')
    # Re-parse when the file appears or goes away; its content is tracked through SRC_URI.
    bb.parse.mark_dependency(d, path)
    if os.path.isfile(path):
        d.appendVar('SRC_URI', ' file://' + path)
}

S = "${WORKDIR}"

# The file is per board: keep it out of the shared package feeds of other machines.
PACKAGE_ARCH = "${MACHINE_ARCH}"
INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    if [ ! -f "${WL18XX_CONF_BIN}" ]; then
        bbwarn "No ${WL18XX_CONF_BIN}: the image carries no Wi-Fi calibration. The board vendor supplies the file (docs/README-nxp.md)."
        return
    fi
    install -d ${D}${nonarch_base_libdir}/firmware/ti-connectivity
    install -m 0644 ${WL18XX_CONF_BIN} \
        ${D}${nonarch_base_libdir}/firmware/ti-connectivity/wl18xx-conf.bin
}

FILES:${PN} = "${nonarch_base_libdir}/firmware/ti-connectivity/wl18xx-conf.bin"
ALLOW_EMPTY:${PN} = "1"
