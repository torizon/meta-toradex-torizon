SUMMARY = "Set reboot command in Aktualizr-Torizon"
DESCRIPTION = "Configures aktualizr-torizon to reboot when needed after an update"
SECTION = "base"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad"

inherit allarch

SRC_URI = " \
    file://70-reboot.toml \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install:append () {
    install -m 0700 -d ${D}${libdir}/sota/conf.d
    install -m 0644 ${UNPACKDIR}/70-reboot.toml ${D}${libdir}/sota/conf.d/70-reboot.toml
}

FILES:${PN} = " \
    ${libdir}/sota/conf.d/70-reboot.toml \
"
