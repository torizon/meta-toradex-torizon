OSTREE_KERNEL_ARGS:append:sota = " console=ttyS0,115200 earlycon root=LABEL=otaroot rootfstype=ext4 cma=524288000@1556086784"

DISTRO_FEATURES:append = " tpm2"

IMAGE_INSTALL:append = " \
    imgtec-pvr-firmware \
    kernel-module-berlin-chipid \
    kernel-module-berlin-ir \
    kernel-module-bluetooth-lpm \
    kernel-module-bluetooth-rfkill \
    kernel-module-dolphin-hwmon \
    kernel-module-dwc3-syna \
    kernel-module-hl7593 \
    kernel-module-hrx-v4l2 \
    kernel-module-pcie-berlin \
    kernel-module-phy-berlin-pcie \
    kernel-module-phy-syna-usb \
    kernel-module-pvrsrvkm \
    kernel-module-rpi-panel-attiny-regulator \
    kernel-module-sm \
    kernel-module-syna-axi-meter \
    kernel-module-tps6286x-regulator \
    linux-firmware-syna \
    optee-client \
    optee-os-tadevkit \
    optee-examples \
    optee-test \
    synasdk-macaddr \
    synasdk-brcm-bt-start \
    synasdk-v4l2isp-daemon \
    synasdk-vpu-plugin \
    synasdk-v4l2isp-qtapp \
    v4l-utils \
"

SYNA_SDK_CONFIG_NAME = "sl1680_poky_aarch64_rdk"
SYNA_SDK_CONFIG_FILE = "sl1680_poky_aarch64_rdk_defconfig"

PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_RPROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"
PREFERRED_PROVIDER_u-boot-default-script:sota = "u-boot-distro-boot"
IMAGE_BOOT_FILES:append = " boot.scr-${MACHINE};boot.scr"
WKS_FILE_DEPENDS:append = " u-boot-default-script"

UBOOT_BOOT_PARTITION_NUMBER = "6"
OTAROOT_PARTITION_NUMBER = "${UBOOT_BOOT_PARTITION_NUMBER}"
export UBOOT_BOOT_PARTITION_NUMBER

UENV_EXTRA_CONFIGS = "\
    env set skip_fdt_overlays 1; \
    env set fdtfile ${@ os.path.basename(d.getVar('SYNA_KERNEL_DTS_FILE'))}; \
    env set skip_fdt_update 0x2; \
    env set kernel_addr_r 0x7c00000; \
    env set ramdisk_addr_r 0xcf00000; \
    env set fdt_addr_r 0x10000000; \
    env set initrd_high 0xffffffffffffffff; \
"

OSTREE_DEVICETREE = " \
    ${SYNA_KERNEL_DTS_FILE} \
    ${SYNA_KERNEL_DTBO_FILE} \
"

BBMASK += "/meta-synaptics/dynamic-layers/virtualization-layer/recipes-containers/docker/docker-moby_git.bbappend"

hostname:pn-base-files = "torizon-sl1680"

PREFERRED_PROVIDER_virtual/dtb = ""

IMAGE_CMD:ota-ext4:append () {
    # Clean up
    [ -f "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs.subimg" ] && rm ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs.subimg
    [ -f "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs_s.subimg.0" ] && rm ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs_s.subimg.*

    # Align ROOTFS_SIZE to 4KB (4096 bytes) to avoid img2simg assertion errors
    ROOTFS_SIZE_ALIGNED=$(echo "scale=0; ((${ROOTFS_SIZE} + 4095) / 4096) * 4096" | bc)
    synaimg_mkext234fs ${OTA_SYSROOT} rootfs_ota ext4 ${ROOTFS_SIZE_ALIGNED} ${EXTRA_IMAGECMD}

    # Convert rootfs_ota.subimg to sparse image with 300MB size limit per sub-image
    img2simg ${DEPLOY_DIR_IMAGE}/rootfs_ota.subimg ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs_ota_s.subimg 4096
    simg2simg ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs_ota_s.subimg ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs_ota_s.subimg 314572800

    # Remove unused partitions from emmc_part_list and copy to SYNAIMG folder
    sed -i '/key_b/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/tzk_b/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/bl_b/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/boot_a/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/boot_b/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/firmware_b/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/rootfs_b/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/fastlogo_a/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/fastlogo_b/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/devinfo/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/misc/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    sed -i '/home/d' ${DEPLOY_DIR_IMAGE}/emmc_part_list
    cp ${DEPLOY_DIR_IMAGE}/emmc_part_list ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/emmc_part_list

    # Re-create emmc_image_list and copy to SYNAIMG folder
    cat <<EOF >${DEPLOY_DIR_IMAGE}/emmc_image_list
preboot.subimg.gz,b1
preboot.subimg.gz,b2
format,sd1
EOF

    # sd1 is the factory_setting partition, already being formatted
    partition_count=2
    for partition in $(grep -v '#' ${DEPLOY_DIR_IMAGE}/emmc_part_list | cut -d'_' -f 1); do
        # factory_setting has nothing to flash and is already being formatted
        [ "${partition}" = "factory" ] && continue
        if [ "${partition}" = "rootfs" ]; then
            # Check if our partitioning is aligned with expected U-Boot env settings
            [ "${UBOOT_BOOT_PARTITION_NUMBER}" != "${partition_count}" ] && bbfatal "U-Boot UBOOT_BOOT_PARTITION_NUMBER (= ${UBOOT_BOOT_PARTITION_NUMBER}) is set to a value different than the rootfs partition (= ${partition_count})"
            subimg_cnt=$(ls ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs_ota_s.subimg.[0-9]* | wc -w)
            for subimg in $(seq 0 $(expr $subimg_cnt - 1)); do
                echo "rootfs_ota_s.subimg.${subimg},sd${partition_count}" >> ${DEPLOY_DIR_IMAGE}/emmc_image_list
            done
        else
            echo "${partition}.subimg.gz,sd${partition_count}" >> ${DEPLOY_DIR_IMAGE}/emmc_image_list
        fi
        partition_count=$(expr $partition_count + 1)
    done
    cp ${DEPLOY_DIR_IMAGE}/emmc_image_list ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/emmc_image_list
    # Create a SYNAIMG-flash.tar.gz file with SYNAIMG, usb-tool and recovery folder inside.
    install -d ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}-flash
    (
        cd ${DEPLOY_DIR_IMAGE}
        cp -L -R syna-metadata/* syna-usb-tool/* ${SYNAIMG_DEPLOY_SUBDIR} ${SYNAIMG_DEPLOY_SUBDIR}-flash
        tar -czf ${SYNAIMG_DEPLOY_SUBDIR}-flash.tar.gz -C ${DEPLOY_DIR_IMAGE} ${SYNAIMG_DEPLOY_SUBDIR}-flash
    )
}

do_image_ota_ext4[depends] += "syna-metadata:do_deploy syna-usb-tool:do_deploy"
