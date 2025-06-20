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

# TODO: Consider always enabling verity.
#       This would facilitate upgrades from "tdx-signed" to "torizon-signed";
#       If not done beforehand, the upgrade process would require running
#       "tune2fs -O verity" on the device.
TEZI_ROOT_FSOPTS:append:cfs-signed = " -O verity"

python adjust_tezi_artifacts() {
    artifacts = d.getVar('TEZI_ARTIFACTS').replace(d.getVar('KERNEL_IMAGETYPE'), '').replace(d.getVar('KERNEL_DEVICETREE'), '')
    d.setVar('TEZI_ARTIFACTS', artifacts)
}

TEZI_IMAGE_TEZIIMG_PREFUNCS:append = " adjust_tezi_artifacts"

require torizon_base_image_type.inc


UBOOT_BINARY_OTA:apalis-imx6 = "u-boot-with-spl.imx"
UBOOT_BINARY_OTA:colibri-imx6 = "u-boot-with-spl.imx"
UBOOT_BINARY_OTA:colibri-imx6ull-emmc = "u-boot.imx"
UBOOT_BINARY_OTA:colibri-imx7-emmc = "u-boot.imx"
UBOOT_BINARY_OTA:apalis-imx8 = "imx-boot"
UBOOT_BINARY_OTA:colibri-imx8x = "imx-boot"
UBOOT_BINARY_OTA:verdin-imx8mm = "imx-boot"
UBOOT_BINARY_OTA:verdin-imx8mp = "imx-boot"
UBOOT_BINARY_OTA:verdin-am62 = "u-boot.img"
UBOOT_BINARY_OTA:verdin-am62p = "u-boot.img"
UBOOT_BINARY_OTA:aquila-am69 = "u-boot.img"
UBOOT_BINARY_OTA:toradex-smarc-imx8mp = "u-boot.bin"
UBOOT_BINARY_OTA:toradex-smarc-imx95 = "u-boot.bin"
UBOOT_BINARY_OTA:qemuarm64 = "u-boot.bin"

# disable for now while we investigate build issues
UBOOT_BINARY_OTA_IGNORE:verdin-am62 = "1"
UBOOT_BINARY_OTA_IGNORE:verdin-am62p = "1"
UBOOT_BINARY_OTA_IGNORE:aquila-am69 = "1"
UBOOT_BINARY_OTA_IGNORE:toradex-smarc-imx8mp = "1"
UBOOT_BINARY_OTA_IGNORE:toradex-smarc-imx95 = "1"
UBOOT_BINARY_OTA_IGNORE:genericx86-64 = "1"

TEZI_IMAGE_TEZIIMG_PREFUNCS:prepend = "gen_torizon_prov_data "

do_image_teziimg[cleandirs] += "${WORKDIR}/prov-data"
do_image_teziimg[vardeps] += "${TORIZON_IMG_VARDEPS}"
do_image_teziimg[file-checksums] += "${TORIZON_IMG_FILE_CHECKSUMS}"
do_image_teziimg[depends] += "${TORIZON_IMG_DEPENDS}"
