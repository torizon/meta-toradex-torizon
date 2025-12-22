SUMMARY = "Initramfs plymouth customization script for Torizon OS"
DESCRIPTION = "Script which allows to customize the Plymouth splash screen in \
the initramfs. Appends the changes to an existing initramfs and stores the \
updated initramfs in a new OSTree commit."
SECTION = "admin"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ostree-customize-plymouth.sh"

do_install() {
    install -d ${D}${bindir}/
    install -m 0755 ${UNPACKDIR}/ostree-customize-plymouth.sh ${D}${bindir}/
}

RDEPENDS:${PN} = "bash"
INHIBIT_DEFAULT_DEPS = "1"
