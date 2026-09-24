require recipes-kernel/linux/linux-torizon.inc

# NXP's Kernel recipe uses this variable to manually append fragments
# to the generated .config.
DELTA_KERNEL_DEFCONFIG:append = "torizon.cfg"

FILESEXTRAPATHS:prepend:mx6-generic-bsp := "${THISDIR}/files:"
SRC_URI:append:mx6-generic-bsp = " file://torizon-container.cfg"
DELTA_KERNEL_DEFCONFIG:append:mx6-generic-bsp = " torizon-container.cfg"

SRC_URI:append:imx6sxsabresd = " file://0001-ARM-dts-imx6sx-sdb-reva-reset-through-the-internal-wa.patch"
