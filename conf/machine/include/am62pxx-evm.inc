require common-am62xx.inc

CORE_IMAGE_BASE_INSTALL:append:am62pxx = " ti-img-rogue-driver"

UENV_EXTRA_CONFIGS:am62pxx-evm = "if test "${board_name}" = "am62px" -a "${fdtfile}" = "ti/k3-am62p5-sk.dtb"; then env set fdtfile k3-am62p5-sk.dtb; echo DONE; fi"

# If there are any DT overlays in OSTREE_DEVICETREE that should be automatically applied during
# boot, set them in OSTREE_DEVICETREE_OVERLAY_BOOT, similar to the example below:
# OSTREE_DEVICETREE_OVERLAY_BOOT:am62pxx-evm = " \
#     k3-am62x-sk-csi2-imx219.dtbo \
#     k3-am62x-sk-csi2-ov5640.dtbo \
#     k3-am62x-sk-csi2-tevi-ov5640.dtbo \
#     k3-am62x-sk-hdmi-audio.dtbo \
# "

IMAGE_BOOT_FILES:append:am62pxx-evm = " tiboot3-am62px-hs-fs-evm.bin"
