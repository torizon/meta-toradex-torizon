PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_RPROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"
PREFERRED_PROVIDER_u-boot-default-script:sota = "u-boot-distro-boot"
IMAGE_BOOT_FILES:append = " boot.scr-${MACHINE};boot.scr"
WKS_FILE_DEPENDS:append = " u-boot-default-script"

OTAROOT_PARTITION_NUMBER = "${UBOOT_BOOT_PARTITION_NUMBER}"
export UBOOT_BOOT_PARTITION_NUMBER

# Synaptics U-Boot's fdt_update (run by booti) works in place on a fixed
# 0x1ff00-byte DTB buffer (DTB_SPACE) and silently skips its /memory fixups
# if the FDT is larger. The base DTB plus this resize, rounded up to 4 KiB,
# plus booti's relocation pad (CONFIG_SYS_FDT_PAD, 0x3000) must stay below
# that.
FDT_OVERLAYS_RESIZE = "0x8000"

OSTREE_DEVICETREE = " \
    ${SYNA_KERNEL_DTS_FILE} \
    ${SYNA_KERNEL_DTBO_FILE} \
"

BBMASK += "/meta-synaptics/dynamic-layers/virtualization-layer/recipes-containers/docker/docker-moby_git.bbappend"

PREFERRED_PROVIDER_virtual/dtb = ""

# meta-synaptics only sets this in its own local.conf template, which we
# don't use. Its swupdate image recipes fail to parse when it's unset.
ENABLE_RESCUE_MODE ?= "0"

PREFERRED_VERSION_meson = "1.3.1"
PREFERRED_VERSION_meson-native = "1.3.1"

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

    # Prepare emmc_part_list and emmc_image_list for SYNAIMG. We make copies first to avoid modifying the original files and conflicting on subsequent builds
    cp ${DEPLOY_DIR_IMAGE}/emmc_part_list ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/emmc_part_list
    EMMC_PART_LIST_FILE="${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/emmc_part_list"

    cp ${DEPLOY_DIR_IMAGE}/emmc_image_list ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/emmc_image_list
    EMMC_IMAGE_LIST_FILE="${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/emmc_image_list"

    # Remove unused partitions from emmc_part_list and copy to SYNAIMG folder
    sed -i '/key_b/d' ${EMMC_PART_LIST_FILE}
    sed -i '/tzk_b/d' ${EMMC_PART_LIST_FILE}
    sed -i '/bl_b/d' ${EMMC_PART_LIST_FILE}
    sed -i '/boot_a/d' ${EMMC_PART_LIST_FILE}
    sed -i '/boot_b/d' ${EMMC_PART_LIST_FILE}
    sed -i '/rootfs_b/d' ${EMMC_PART_LIST_FILE}
    sed -i '/fastlogo_b/d' ${EMMC_PART_LIST_FILE}
    sed -i '/^devinfo/d' ${EMMC_PART_LIST_FILE}
    sed -i '/^home/d' ${EMMC_PART_LIST_FILE}

    # sl1680's U-Boot runs show_logo, which reads the logo from fastlogo_a and
    # keeps A/B boot control in misc. Without misc, its bootctrl code misses the
    # lookup failure (it checks for -1, part_get_info_by_name() returns -ENOENT)
    # and writes into rootfs_a instead, corrupting its ext4 group descriptors.
    if ! echo ":${MACHINEOVERRIDES}:" | grep -q ':sl1680:'; then
        sed -i '/^fastlogo_a/d' ${EMMC_PART_LIST_FILE}
        sed -i '/^misc/d' ${EMMC_PART_LIST_FILE}
    fi

    # rootfs_a goes last, so resize-helper can grow it to the end of the eMMC
    # and its sparse chunks are flashed last (the flash tool drops the USB link
    # on the first write after them). The bootloaders find fastlogo_a and misc
    # by name, not by number.
    rootfs_line=$(grep '^rootfs_a' ${EMMC_PART_LIST_FILE})
    sed -i '/^rootfs_a/d' ${EMMC_PART_LIST_FILE}
    echo "${rootfs_line}" >> ${EMMC_PART_LIST_FILE}

    # U-Boot keeps its environment at fixed offsets, in the last 64 KiB below
    # 64 MiB and below 80 MiB (CONFIG_ENV_OFFSET and CONFIG_ENV_OFFSET_REDUND),
    # and nothing in the list covers them. Reserve them: insert ubootenv before
    # the first partition that would reach 64 MiB, ending at 80 MiB.
    awk '
        !done && $1 !~ /^#/ && NF {
            split($0, f, ",")
            start = (f[2] + 0 > 0) ? f[2] + 0 : pos
            if (start + f[3] >= 64) {
                if (start != pos || pos >= 64) exit 1
                printf "%-20s,0\t,%d\t,                \n", "ubootenv", 80 - pos
                done = 1
            }
            pos = start + f[3]
        }
        { print }
    ' ${EMMC_PART_LIST_FILE} > ${EMMC_PART_LIST_FILE}.tmp || bbfatal "can't reserve 64-80 MiB for the U-Boot environment"
    mv ${EMMC_PART_LIST_FILE}.tmp ${EMMC_PART_LIST_FILE}
    grep -q '^ubootenv' ${EMMC_PART_LIST_FILE} || bbfatal "no room reserved for the U-Boot environment"

    # Re-create emmc_image_list and copy to SYNAIMG folder
    cat <<EOF >${EMMC_IMAGE_LIST_FILE}
preboot.subimg.gz,b1
preboot.subimg.gz,b2
format,sd1
EOF

    # sd1 is the factory_setting partition, already being formatted
    partition_count=2
    grep -v '#' ${EMMC_PART_LIST_FILE} | while read -r partition_line; do
        # Isolate the partition name (first column) before stripping any _a/_b
        # suffix: cut -d'_' -f1 alone would return the whole line unchanged for
        # names with no underscore (e.g. "misc"), which then word-splits on the
        # embedded whitespace/commas and corrupts the generated image list.
        partition=$(echo "${partition_line}" | awk '{print $1}' | cut -d'_' -f 1)
        # factory_setting has nothing to flash and is already being formatted
        [ "${partition}" = "factory" ] && continue
        if [ "${partition}" = "rootfs" ]; then
            # Check if our partitioning is aligned with expected U-Boot env settings
            [ "${UBOOT_BOOT_PARTITION_NUMBER}" != "${partition_count}" ] && bbfatal "U-Boot UBOOT_BOOT_PARTITION_NUMBER (= ${UBOOT_BOOT_PARTITION_NUMBER}) is set to a value different than the rootfs partition (= ${partition_count})"
            subimg_cnt=$(ls ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/rootfs_ota_s.subimg.[0-9]* | wc -w)
            for subimg in $(seq 0 $(expr $subimg_cnt - 1)); do
                echo "rootfs_ota_s.subimg.${subimg},sd${partition_count}" >> ${EMMC_IMAGE_LIST_FILE}
            done
        elif [ "${partition}" = "misc" ] || [ "${partition}" = "ubootenv" ]; then
            # runtime state, not a subimage: ship it erased like the vendor list
            echo "erase,sd${partition_count}" >> ${EMMC_IMAGE_LIST_FILE}
        else
            echo "${partition}.subimg.gz,sd${partition_count}" >> ${EMMC_IMAGE_LIST_FILE}
        fi
        partition_count=$(expr $partition_count + 1)
    done
    # Create a SYNAIMG-flash.tar.gz file with SYNAIMG, usb-tool and recovery folder inside.
    install -d ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}-flash
    (
        cd ${DEPLOY_DIR_IMAGE}
        cp -L -R syna-metadata/* syna-usb-tool/* ${SYNAIMG_DEPLOY_SUBDIR} ${SYNAIMG_DEPLOY_SUBDIR}-flash
        if [ -d syna2img-tools ]; then
            cp -L -R syna2img-tools/* ${SYNAIMG_DEPLOY_SUBDIR}-flash
        fi
        tar -czf ${SYNAIMG_DEPLOY_SUBDIR}-flash.tar.gz -C ${DEPLOY_DIR_IMAGE} ${SYNAIMG_DEPLOY_SUBDIR}-flash
    )
}
do_image_ota_ext4[depends] += "syna-metadata:do_deploy syna-usb-tool:do_deploy"

# Ensure do_image_synaimg runs after do_image_ostree completes since both copy from IMAGE_ROOTFS,
# and if they run simultaneously it can cause FileNotFoundError on temporary files created by rsync/tar.
do_image_synaimg[depends] += "${PN}:do_image_ostree"
