WKS_FILE:sota = "torizon-imx93frdm-sota.wks"

OSTREE_KERNEL_ARGS:sota:imx:append = "console=ttyLP0,115200 earlycon root=LABEL=otaroot rootfstype=ext4"

UENV_EXTRA_CONFIGS = "env set ramdisk_addr_r "${initrd_addr}""

UBOOT_BOOT_PARTITION_NUMBER = "1"
OTAROOT_PARTITION_NUMBER = "1"

PREFERRED_PROVIDER_virtual/dtb = ""
PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"

# Lock the BSP recipes to the versions used by the upstream FRDM support.
# This may be removed if/when meta-imx-frdm is updated to work with the
# same manifest as Torizon
SRCREV:pn-u-boot-imx = "de16f4f17221b2ff72b8cb18c28cd8a29f3c2710"
SRCREV:pn-linux-imx = "d23d64eea5111e1607efcce1d601834fceec92cb"
LINUX_VERSION:pn-linux-imx = "6.6.36"
