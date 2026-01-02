OSTREE_KERNEL_ARGS:append:sota = " console=ttyS0,115200 earlycon root=LABEL=otaroot rootfstype=ext4"

SYNA_SDK_CONFIG_NAME = "sl2619_poky_aarch64_rdk"
SYNA_SDK_CONFIG_FILE = "sl2619_poky_aarch64_rdk_defconfig"

UBOOT_BOOT_PARTITION_NUMBER = "8"

UENV_EXTRA_CONFIGS = "\
    env set skip_fdt_overlays 1; \
    env set fdtfile ${@ os.path.basename(d.getVar('SYNA_KERNEL_DTS_FILE'))}; \
    env set skip_fdt_update 0x2; \
    env set kernel_addr_r 0x7c00000; \
    env set ramdisk_addr_r 0xcf00000; \
    env set fdt_addr_r 0x10000000; \
    env set initrd_high 0xffffffffffffffff; \
"

hostname:pn-base-files = "torizon-sl2619"

MACHINE_EXTRA_RRECOMMENDS:remove = "synasdk-gpu-mali"

require sl-common.inc
