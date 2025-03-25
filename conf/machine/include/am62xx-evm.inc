require common-am62xx.inc

UENV_EXTRA_CONFIGS = "if test "${board_name}" = "am62b_p1_skevm" -a "${fdtfile}" = "ti/k3-am625-sk.dtb"; then env set fdtfile k3-am625-sk.dtb; fi"

KERNEL_DEVICETREE = " \
    ti/k3-am625-sk.dtb \
    ti/k3-am62x-sk-csi2-imx219.dtbo \
    ti/k3-am62x-sk-csi2-ov5640.dtbo \
    ti/k3-am62x-sk-csi2-tevi-ov5640.dtbo \
    ti/k3-am62x-sk-hdmi-audio.dtbo \
"
# If there are any DT overlays in OSTREE_DEVICETREE that should be automatically applied during
# boot, set them in OSTREE_DEVICETREE_OVERLAY_BOOT, similar to the example below:
# OSTREE_DEVICETREE_OVERLAY_BOOT = " \
#     k3-am62x-sk-csi2-imx219.dtbo \
#     k3-am62x-sk-csi2-ov5640.dtbo \
#     k3-am62x-sk-csi2-tevi-ov5640.dtbo \
#     k3-am62x-sk-hdmi-audio.dtbo \
# "

IMAGE_BOOT_FILES:append = " tiboot3-am62x-hs-fs-evm.bin tiboot3-am62x-gp-evm.bin tiboot3-am62x-hs-evm.bin"
