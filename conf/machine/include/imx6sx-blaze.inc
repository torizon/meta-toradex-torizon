require common-imx6.inc

# The bootloader lives in the eMMC boot0 hardware partition, so this image has
# no bootloader partition. Set after the require, which assigns with a hard =.
WKS_FILE:sota = "torizon-imx6sx-blaze-sota.wks"

TORIZON_TTY_DEVICE = "ttymxc2,115200"
