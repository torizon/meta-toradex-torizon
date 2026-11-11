OSTREE_KERNEL_ARGS:append:sota = " console=ttyS0,115200 earlycon root=LABEL=otaroot rootfstype=ext4"

SYNA_SDK_CONFIG_NAME = "sl2619_poky_aarch64_rdk"
SYNA_SDK_CONFIG_FILE = "sl2619_poky_aarch64_rdk_defconfig"

UBOOT_BOOT_PARTITION_NUMBER = "8"

hostname:pn-base-files = "torizon-sl2619"

MACHINE_EXTRA_RRECOMMENDS:remove = "synasdk-gpu-mali"

require sl-common.inc
