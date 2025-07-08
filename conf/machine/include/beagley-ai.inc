WKS_FILE:sota:beagley-ai = "torizon-am62xx-sota.wks"

OSTREE_KERNEL_ARGS:sota:beagley-ai = "quiet logo.nologo vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fbcon=map:10 console=ttyS2,115200 root=LABEL=otaroot rootfstype=ext4"

CORE_IMAGE_BASE_INSTALL:append:beagley-ai = " ti-img-rogue-driver"

UBOOT_BOOT_PARTITION_NUMBER:beagley-ai = "2"
OTAROOT_PARTITION_NUMBER:beagley-ai = "2"

IMAGE_FSTYPES:append = " wic"
IMAGE_BOOT_FILES:append:beagley-ai = " tispl.bin u-boot.img tiboot3.bin tiboot3-j722s-hs-fs-evm.bin"

PREFERRED_PROVIDER_virtual/dtb = ""
hostname:pn-base-files = "beagley-ai"
