KERNEL_DEVICETREE_BASENAME = "${@make_dtb_boot_files(d)}"
# WKS_FILE_DEPENDS:append = " virtual/dtb"
DEPENDS += "${WKS_FILE_DEPENDS}"
IMAGE_BOOT_FILES_REMOVE = "${@'${KERNEL_DEVICETREE_BASENAME}' if d.getVar('KERNEL_IMAGETYPE') == 'fitImage' else ''}"
IMAGE_BOOT_FILES:append = " overlays.txt ${@'' if d.getVar('KERNEL_IMAGETYPE') == 'fitImage' else 'overlays/*;overlays/'}"
IMAGE_BOOT_FILES:remove = "${IMAGE_BOOT_FILES_REMOVE}"

RM_WORK_EXCLUDE += "${PN}"

# use DISTRO_FLAVOUR to append to the image name displayed in TEZI
DISTRO_FLAVOUR ??= ""
SUMMARY:append = "${DISTRO_FLAVOUR}"

# Append tar command to store uncompressed image size to ${T}.
# If a custom rootfs type is used make sure this file is created
# before compression.
IMAGE_CMD:tar:append = "; du -ks ${IMGDEPLOYDIR}/${IMAGE_NAME}.tar | cut -f 1 > ${T}/image-size${IMAGE_NAME_SUFFIX}"
CONVERSION_CMD:tar:append = "; du -ks ${IMGDEPLOYDIR}/${IMAGE_NAME}.${type}.tar | cut -f 1 > ${T}/image-size.${type}"
CONVERSION_CMD:tar = "touch ${IMGDEPLOYDIR}/${IMAGE_NAME}.${type}; ${IMAGE_CMD_TAR} --numeric-owner -cf ${IMGDEPLOYDIR}/${IMAGE_NAME}.${type}.tar -C ${TAR_IMAGE_ROOTFS} . || [ $? -eq 1 ]"
CONVERSIONTYPES:append = " tar"

require torizon_base_image_type.inc

EXTRA_OSTREE_COMMIT:remove = "--add-metadata=oe.layers="${OSTREE_LAYER_REVISION_INFO}""

UBOOT_BINARY_OTA_IGNORE = "1"

do_deploy[postfuncs] += "gen_torizon_prov_data"
do_deploy[cleandirs] += "${WORKDIR}/prov-data"
do_deploy[vardeps] += "${TORIZON_IMG_VARDEPS}"
do_deploy[file-checksums] += "${TORIZON_IMG_FILE_CHECKSUMS}"
do_deploy[depends] += "${TORIZON_IMG_DEPENDS}"
