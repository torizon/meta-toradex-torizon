require common-imx6.inc

# The bootloader lives in the eMMC boot0 hardware partition, so this image has
# no bootloader partition. Set after the require, which assigns with a hard =.
WKS_FILE:sota = "torizon-imx6sx-blaze-sota.wks"

TORIZON_TTY_DEVICE = "ttymxc2,115200"

EXTRA_IMAGEDEPENDS:append = " torizon-uuu"

# packagegroup-base-wifi carries no TI firmware, so the driver cannot boot the
# chip without linux-firmware-wl18xx.
#
# The module's PHY calibration, wl18xx-conf, carries the board vendor's
# wl18xx-conf.bin when the builder supplies it (see wl18xx-conf.bb); without it
# the package is empty and the driver falls back to built-in defaults.
MACHINE_FEATURES += "wifi"
MACHINE_EXTRA_RRECOMMENDS:append = " linux-firmware-wl18xx wl18xx-conf"
