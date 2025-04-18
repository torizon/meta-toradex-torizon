PREFERRED_PROVIDER_virtual/dtb = ""

# NOTE: disable ST images not required for Torizon
ST_BOOTFS:stm32mpcommon   ?= "0"
ST_VENDORFS:stm32mpcommon ?= "0"
ST_USERFS:stm32mpcommon   ?= "0"

# NOTE: remove dependency on userfs since we don't build it
WKS_FILE_DEPENDS:remove:stm32mpcommon = "st-image-userfs"

# STM32MP bootargs to enable UART console
OSTREE_KERNEL_ARGS:stm32mpcommon = "earlyprintk earlycon console=ttySTM0,115200"

# Use ota-ext4 image instead of default ext4 for rootfs and mark the rootfs partition as System.
# Note that rootfs is the only filesystem image to be installed since STM32MP's bootfs, vendorfs and
# userfs images are disabled in the Torizon, so it is ok to change the common suffix.
PARTITIONS_SUFFIX = "ota-ext4"
PARTITIONS_IMAGES[rootfs] = "${STM32MP_ROOTFS_IMAGE},${STM32MP_ROOTFS_LABEL},,${STM32MP_ROOTFS_SIZE},System"

PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"
UBOOT_BOOT_PARTITION_NUMBER = "${distro_bootpart}"
UENV_EXTRA_CONFIGS = "env set otaroot ${distro_bootpart}"
KERNEL_BOOTCMD:stm32mp1common = "bootm"

IMAGE_ROOTFS_MAXSIZE = "2500000"

def partImage2partConfig_trz(d, config, fstype=None):
    """
    Convert PARTTIONS_IMAGES['config'] setting format to format expected to feed
    PARTITIONS_CONFIG[xxx].
    Manage <image_name> update respect to 'fstype' provided and apply the rootfs
    namming or standard partition image one.
        FROM: <image_name>,<partition_label>,<mountpoint>,<size>,<type>
        TO  : <binary_name>,<partition_label>,<size>,<type>
    """
    items = d.getVarFlag('PARTITIONS_IMAGES', config).split(',') or ""
    if len(items) != 5:
        bb.fatal('[partImage2partConfig] Wrong settings for PARTTIONS_IMAGES[%s] : %s' % (config, items))
    overrides = d.getVar('OVERRIDES')
    if items[2] == '' and 'openstlinuxcommon' not in overrides.split(':'):
        bin_suffix = '-${MACHINE}'
    else:
        bin_suffix = '-${DISTRO}-${MACHINE}'
    if fstype:
        bin_name = items[0] + bin_suffix + '.' + fstype
    else:
        bin_name = items[0] + bin_suffix
    # Set string for PARTITIONS_CONFIG item: <binary_name>,<partlabel>,<size>,<type>
    part_format = bin_name + ',' + items[1] + ',' + items[3] + ',' + items[4]
    return part_format

PARTITIONS_CONFIG[emmc] = "\
    ${@' '.join(['%s' % partImage2partConfig_trz(d, config, '${PARTITIONS_SUFFIX}') for config in d.getVar('PARTITIONS_LIST').split()])} \
    "
PARTITIONS_CONFIG[sdcard] = "\
    ${@' '.join(['%s' % partImage2partConfig_trz(d, config, '${PARTITIONS_SUFFIX}') for config in d.getVar('PARTITIONS_LIST').split()])} \
    "

# Disable resize-helper since it unsets the bootable flag from the rootfs partition,
# which leads to U-Boot can't find bootable images on the STM32MP targets
CORE_IMAGE_BASE_INSTALL:remove:stm32mpcommon = " \
    resize-helper \
"
