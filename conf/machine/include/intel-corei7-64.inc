
WKS_FILE:intel-corei7-64:sota = "efiimage-sota.wks"
WKS_FILE_DEPENDS:remove = "tezi-metadata virtual/dtb"

OSTREE_KERNEL_ARGS:sota:append:intel-corei7-64 = "console=ttyS0,115200 root=LABEL=otaroot rootfstype=ext4 quiet logo.nologo vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fbcon=map:10 "

OSTREE_BOOTLOADER:intel-corei7-64 = "grub"
OSTREE_DEPLOY_DEVICETREE:intel-corei7-64 = "0"
EFI_PROVIDER:intel-corei7-64 = "grub-efi"
SOTA_CLIENT_FEATURES:remove = "ubootenv"
UBOOT_BINARY_OTA_IGNORE:intel-corei7-64 = "1"

IMAGE_FSTYPES_REMOVE = "hddimg teziimg"
IMAGE_FSTYPES:append = " wic.vmdk wic.vdi"

hostname:pn-base-files = "torizon-x86"

PREFERRED_PROVIDER_u-boot-default-script = ""
PREFERRED_PROVIDER_u-boot-fw-utils = "grub-ota-fallback"
PREFERRED_RPROVIDER_u-boot-fw-utils = "grub-ota-fallback"
PREFERRED_PROVIDER_virtual/dtb = ""

# If an older kernel is preferred, set the PREFERRED_VERSION
#PREFERRED_VERSION_linux-xenomai-3 = "5.10%"
#PREFERRED_VERSION_linux-xenomai-4 = "5.10%"

IMAGE_INSTALL:remove = " minnowboard-efi-startup network-configuration"
LINUX_VERSION_EXTENSION ?= "-torizon-${LINUX_KERNEL_TYPE}"
