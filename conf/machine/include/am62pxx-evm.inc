WKS_FILE:sota:am62pxx-evm = "torizon-am62xx-sota.wks"

OSTREE_KERNEL_ARGS:sota:am62pxx-evm = "quiet logo.nologo vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fbcon=map:10 console=ttyS2,115200 root=LABEL=otaroot rootfstype=ext4"

CORE_IMAGE_BASE_INSTALL:append:am62pxx = " ti-img-rogue-driver"

UBOOT_BOOT_PARTITION_NUMBER:am62pxx-evm = "2"
OTAROOT_PARTITION_NUMBER:am62pxx-evm = "2"
UENV_EXTRA_CONFIGS:am62pxx-evm = "if test "${board_name}" = "am62px"; then if test "${fdtfile}" = "ti/k3-am62p5-sk.dtb"; then env set fdtfile k3-am62p5-sk.dtb; fi; fi"

KERNEL_DEVICETREE:am62pxx-evm = " \
    ti/k3-am62p5-sk.dtb \
    ti/k3-am62x-sk-csi2-imx219.dtbo \
    ti/k3-am62x-sk-csi2-ov5640.dtbo \
    ti/k3-am62x-sk-csi2-tevi-ov5640.dtbo \
    ti/k3-am62x-sk-hdmi-audio.dtbo \
"
# Overlays in KERNEL_DEVICETREE to be applied during boot time
KERNEL_DEVICETREE_OVERLAY_BOOT:am62pxx-evm = " \
    k3-am62x-sk-csi2-imx219.dtbo \
    k3-am62x-sk-csi2-ov5640.dtbo \
    k3-am62x-sk-csi2-tevi-ov5640.dtbo \
    k3-am62x-sk-hdmi-audio.dtbo \
"

IMAGE_FSTYPES:append = " wic"
IMAGE_BOOT_FILES:append:am62pxx-evm = " tispl.bin u-boot.img tiboot3.bin tiboot3-am62px-hs-fs-evm.bin"

PREFERRED_PROVIDER_virtual/dtb = ""
hostname:pn-base-files = "am62pxx-evm"
