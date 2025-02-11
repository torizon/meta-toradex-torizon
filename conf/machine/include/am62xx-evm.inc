WKS_FILE:sota:am62xx-evm = "torizon-am62xx-sota.wks"

OSTREE_KERNEL_ARGS:sota:am62xx-evm = "quiet logo.nologo vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fbcon=map:10 console=ttyS2,115200 root=LABEL=otaroot rootfstype=ext4"

UBOOT_BOOT_PARTITION_NUMBER:am62xx-evm = "2"
OTAROOT_PARTITION_NUMBER:am62xx-evm = "2"
UENV_EXTRA_CONFIGS:am62xx-evm = "if test "${board_name}" = "am62b_p1_skevm"; then if test "${fdtfile}" = "ti/k3-am625-sk.dtb"; then env set fdtfile k3-am625-sk.dtb; fi; fi"

KERNEL_DEVICETREE:am62xx-evm = " \
    ti/k3-am625-sk.dtb \
    ti/k3-am62x-sk-csi2-imx219.dtbo \
    ti/k3-am62x-sk-csi2-ov5640.dtbo \
    ti/k3-am62x-sk-csi2-tevi-ov5640.dtbo \
    ti/k3-am62x-sk-hdmi-audio.dtbo \
"
# Overlays in KERNEL_DEVICETREE to be applied during boot time
KERNEL_DEVICETREE_OVERLAY_BOOT:am62xx-evm = " \
    k3-am62x-sk-csi2-imx219.dtbo \
    k3-am62x-sk-csi2-ov5640.dtbo \
    k3-am62x-sk-csi2-tevi-ov5640.dtbo \
    k3-am62x-sk-hdmi-audio.dtbo \
"

IMAGE_FSTYPES:append = " wic"
IMAGE_BOOT_FILES:append:am62xx-evm = " tispl.bin u-boot.img tiboot3.bin tiboot3-am62x-hs-fs-evm.bin tiboot3-am62x-gp-evm.bin tiboot3-am62x-hs-evm.bin"

PREFERRED_PROVIDER_virtual/dtb = ""
hostname:pn-base-files = "am62xx-evm"
