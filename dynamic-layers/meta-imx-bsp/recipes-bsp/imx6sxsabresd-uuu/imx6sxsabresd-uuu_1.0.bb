SUMMARY = "uuu script that writes the SABRE-SD i.MX 6SoloX SD card through the board"
DESCRIPTION = "Host-side uuu script: loads U-Boot over the boot ROM's serial \
downloader, then writes the whole SD card in one fastboot transaction. It opens \
the image and the bootloader by their deployed names, so it is deployed beside \
them."
HOMEPAGE = "https://github.com/torizon/meta-toradex-torizon"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PV = "1.0"

SRC_URI = "file://imx6sxsabresd.uuu"

S = "${WORKDIR}"

inherit deploy

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# Host-side script: deployed beside the flashing artifacts, not into the rootfs.
do_deploy() {
    install -m 0644 ${S}/imx6sxsabresd.uuu ${DEPLOYDIR}/imx6sxsabresd.uuu
}
addtask deploy before do_build after do_compile

COMPATIBLE_MACHINE = "imx6sxsabresd"
