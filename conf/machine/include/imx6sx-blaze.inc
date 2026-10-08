require common-imx6.inc

# The bootloader lives in the eMMC boot0 hardware partition, so this image has
# no bootloader partition. Set after the require, which assigns with a hard =.
WKS_FILE:sota = "torizon-imx6sx-blaze-sota.wks"

TORIZON_TTY_DEVICE = "ttymxc2,115200"

EXTRA_IMAGEDEPENDS:append = " torizon-uuu"

# image_type_common_torizon.bbclass disables bootloader OTA for every machine
# with a plain assignment, so re-enabling it takes the machine override. This
# board can carry it: the bootloader is a bare u-boot.imx in an eMMC boot
# partition.
UBOOT_BINARY_OTA_IGNORE:imx6sx-blaze = "0"
UBOOT_BINARY_OTA:imx6sx-blaze = "u-boot.imx"

# packagegroup-base-wifi carries no TI firmware, so the driver cannot boot the
# chip without linux-firmware-wl18xx.
#
# The module's PHY calibration, wl18xx-conf, carries the board vendor's
# wl18xx-conf.bin when the builder supplies it (see wl18xx-conf.bb); without it
# the package is empty and the driver falls back to built-in defaults.
MACHINE_FEATURES += "wifi usbhost usbgadget"
MACHINE_EXTRA_RRECOMMENDS:append = " linux-firmware-wl18xx wl18xx-conf"
