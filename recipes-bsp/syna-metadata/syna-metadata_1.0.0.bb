SUMMARY = "Recovery script files for Synaptics board"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://flash-image.sh file://README"

inherit deploy

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYNAIMG_DEPLOY = "${DEPLOYDIR}/${BPN}"

do_deploy() {
    install -d ${SYNAIMG_DEPLOY}
    install -m 755 ${WORKDIR}/flash-image.sh ${SYNAIMG_DEPLOY}
    install -m 644 ${WORKDIR}/README ${SYNAIMG_DEPLOY}
}

addtask deploy after do_install

COMPATIBLE_MACHINE = "(sl1680|sl2619|winglet)"
