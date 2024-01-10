WKS_FILE:sota:genericx86-64 = "efidisk-sota.wks"
WKS_FILE_DEPENDS:remove = "tezi-metadata virtual/dtb"
WKS_FILE_DEPENDS:append = " ovmf"

OSTREE_KERNEL_ARGS:genericx86-64 = "quiet logo.nologo plymouth.ignore-serial-consoles splash console=ttyS0,115200 root=LABEL=otaroot rootfstype=ext4"
OSTREE_BOOTLOADER:genericx86-64 = "grub"
OSTREE_DEPLOY_DEVICETREE:genericx86-64 = "0"
EFI_PROVIDER:genericx86-64 = "grub-efi"
SOTA_CLIENT_FEATURES:remove = "ubootenv"

IMAGE_FSTYPES_REMOVE = "hddimg teziimg"
IMAGE_FSTYPES:append = " wic.vmdk wic.vdi"

QB_DRIVE_TYPE = "/dev/hd"
QB_DEFAULT_FSTYPE = "wic"
QB_MEM:genericx86-64 = "-m 2048"

PACKAGECONFIG:append:pn-qemu-system-native = " sdl"
PACKAGECONFIG:append:pn-nativesdk-qemu = " sdl"

NET_NAME:pn-networkmanager = "enp"
NET_NUMS:pn-networkmanager = "0"

PREFERRED_PROVIDER_u-boot-default-script = ""
PREFERRED_PROVIDER_virtual/dtb = ""
