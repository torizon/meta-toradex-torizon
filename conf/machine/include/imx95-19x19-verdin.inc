WKS_FILE:sota = "torizon-imx95-sota.wks"

OSTREE_KERNEL_ARGS:sota:imx:append = "console=ttyLP0,115200 earlycon root=LABEL=otaroot rootfstype=ext4"

UENV_EXTRA_CONFIGS = "env set ramdisk_addr_r "${initrd_addr}""

UBOOT_BOOT_PARTITION_NUMBER = "1"
OTAROOT_PARTITION_NUMBER = "1"

PREFERRED_PROVIDER_virtual/dtb = ""
PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"
