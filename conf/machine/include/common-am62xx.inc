# Base configurations for TI's BSP AM62 devices

# Adding custom override to differentiate between Verdin AM62 and TI's AM62xx EVMs
MACHINEOVERRIDES =. "common-ti:"

WKS_FILE:sota = "torizon-am62xx-sota.wks"

TORIZON_TTY_DEVICE ?= "ttyS2,115200"
OSTREE_KERNEL_ARGS:sota = "quiet logo.nologo vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fbcon=map:10 console=${TORIZON_TTY_DEVICE} root=LABEL=otaroot rootfstype=ext4"

UBOOT_BOOT_PARTITION_NUMBER = "2"
OTAROOT_PARTITION_NUMBER = "2"

IMAGE_FSTYPES:append = " wic"
IMAGE_BOOT_FILES:append = " tispl.bin u-boot.img tiboot3.bin"

PREFERRED_PROVIDER_virtual/dtb = ""
