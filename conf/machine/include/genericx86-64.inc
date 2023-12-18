WKS_FILE:sota:genericx86-64 = "efidisk-sota.wks"
WKS_FILE_DEPENDS:remove = "tezi-metadata virtual/dtb"

OSTREE_KERNEL_ARGS:genericx86-64 = "quiet logo.nologo plymouth.ignore-serial-consoles splash console=ttyS0,115200 root=LABEL=otaroot rootfstype=ext4"
OSTREE_BOOTLOADER:genericx86-64 = "grub"
OSTREE_DEPLOY_DEVICETREE:genericx86-64 = "0"
EFI_PROVIDER:genericx86-64 = "grub-efi"
SOTA_CLIENT_FEATURES:remove = "ubootenv"

IMAGE_FSTYPES_REMOVE = "hddimg teziimg"
IMAGE_FSTYPES:append = " wic.vmdk wic.vdi"

hostname:pn-base-files = "${MACHINE}"
CORE_IMAGE_BASE_INSTALL:remove = "set-hostname"

PREFERRED_PROVIDER_u-boot-default-script = ""
PREFERRED_PROVIDER_virtual/dtb = ""
