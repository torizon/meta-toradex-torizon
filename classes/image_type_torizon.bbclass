inherit image_type_tezi

TEZI_ROOT_LABEL = "otaroot"
TEZI_ROOT_NAME = "ota"
TEZI_ROOT_SUFFIX = "ota.tar.zst"
TEZI_USE_BOOTFILES = "false"

MINIMUM_TORIZON_TEZI_CONFIG_FORMAT = "3"
# Check if BSP's TEZI_CONFIG_FORMAT is higher than the one we need. If it is, we use BSP's value
python() {
    bsp_tezi = d.getVar('TEZI_CONFIG_FORMAT')
    torizon_tezi = d.getVar('MINIMUM_TORIZON_TEZI_CONFIG_FORMAT')
    if int(bsp_tezi) < int(torizon_tezi):
        d.setVar('TEZI_CONFIG_FORMAT', torizon_tezi)
}

# Enable verity on the filesystem when composefs is selected.
TEZI_ROOT_FSOPTS:append:cfs-signed = " -O verity"

python adjust_tezi_artifacts() {
    artifacts = d.getVar('TEZI_ARTIFACTS').replace(d.getVar('KERNEL_IMAGETYPE'), '').replace(d.getVar('KERNEL_DEVICETREE'), '')
    d.setVar('TEZI_ARTIFACTS', artifacts)
}

# Definitions shared with the recipe that builds the "TCB signing files" tarball.
require recipes-bsp/tcb-signing-files/tcb-signing-files.inc

# Pull the tarball into the build only when this image is meant to carry it.
do_image_teziimg[depends] += "${@d.getVar('TCB_SIGNING_FILES_RECIPE') + ':do_deploy' if d.getVar('TCB_SIGNING_SUPPORT') == '1' else ''}"

python add_signing_files_to_tezi_artifacts() {
    if d.getVar('TCB_SIGNING_SUPPORT') != '1':
        return

    # rootfs_tezi_run_json() sets TEZI_ARTIFACTS instead of appending to it, so
    # this prefunc has to run after it or the tarball is dropped without a trace.
    if not d.getVar('TEZI_ARTIFACTS'):
        bb.fatal('TEZI_ARTIFACTS is empty when adding the TCB signing files: '
                 'add_signing_files_to_tezi_artifacts must run after '
                 'rootfs_tezi_run_json, which sets that variable. Check the '
                 'ordering of TEZI_IMAGE_TEZIIMG_PREFUNCS.')

    tarball = os.path.join(d.getVar('DEPLOY_DIR_IMAGE'), d.getVar('TCB_SIGNING_FILES_TARBALL'))
    d.appendVar('TEZI_ARTIFACTS', ' ' + tarball)
}

TEZI_IMAGE_TEZIIMG_PREFUNCS:append = " add_signing_files_to_tezi_artifacts adjust_tezi_artifacts"

require torizon_base_image_type.inc


UBOOT_BINARY_OTA:apalis-imx6 = "u-boot-with-spl.imx"
UBOOT_BINARY_OTA:colibri-imx6 = "u-boot-with-spl.imx"
UBOOT_BINARY_OTA:colibri-imx6ull-emmc = "u-boot.imx"
UBOOT_BINARY_OTA:colibri-imx7-emmc = "u-boot.imx"
UBOOT_BINARY_OTA:apalis-imx8 = "imx-boot"
UBOOT_BINARY_OTA:colibri-imx8x = "imx-boot"
UBOOT_BINARY_OTA:verdin-imx8mm = "imx-boot"
UBOOT_BINARY_OTA:verdin-imx8mp = "imx-boot"
UBOOT_BINARY_OTA:verdin-am62 = " \
    firmware-verdin-am62-gp.bin:gp \
    firmware-verdin-am62-hs-fs.bin:hs-fs \
    firmware-verdin-am62-hs.bin:hs \
"
UBOOT_BINARY_OTA:verdin-am62p = " \
    firmware-verdin-am62px-hs-fs.bin:hs-fs \
    firmware-verdin-am62px-hs.bin:hs \
"
UBOOT_BINARY_OTA:aquila-am69 = "u-boot.img"
UBOOT_BINARY_OTA:toradex-smarc-imx8mp = "u-boot.bin"
UBOOT_BINARY_OTA:toradex-smarc-imx95 = "u-boot.bin"
UBOOT_BINARY_OTA:qemuarm64 = "u-boot.bin"

# disable for now while we investigate build issues
UBOOT_BINARY_OTA_IGNORE:aquila-am69 = "1"
UBOOT_BINARY_OTA_IGNORE:aquila-imx95 = "1"
UBOOT_BINARY_OTA_IGNORE:toradex-smarc-imx8mp = "1"
UBOOT_BINARY_OTA_IGNORE:toradex-smarc-imx95 = "1"
UBOOT_BINARY_OTA_IGNORE:verdin-imx95 = "1"
UBOOT_BINARY_OTA_IGNORE:genericx86-64 = "1"
UBOOT_BINARY_OTA_IGNORE:lino-imx93 = "1"
UBOOT_BINARY_OTA_IGNORE:toradex-osm-imx93 = "1"

TEZI_IMAGE_TEZIIMG_PREFUNCS:prepend = "gen_torizon_prov_data "

do_image_teziimg[cleandirs] += "${WORKDIR}/prov-data"
do_image_teziimg[vardeps] += "${TORIZON_IMG_VARDEPS}"
do_image_teziimg[file-checksums] += "${TORIZON_IMG_FILE_CHECKSUMS}"
do_image_teziimg[depends] += "${TORIZON_IMG_DEPENDS}"
