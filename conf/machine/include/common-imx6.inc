# Base configuration for Torizon on i.MX 6 devices

# Adding custom override so a recipe append reaches the i.MX 6 Torizon machines
MACHINEOVERRIDES =. "common-imx6:"

WKS_FILE:sota = "torizon-imx6-sota.wks"
IMAGE_FSTYPES:append = " wic"

UBOOT_BOOT_PARTITION_NUMBER = "1"
OTAROOT_PARTITION_NUMBER = "1"

PREFERRED_PROVIDER_virtual/dtb = ""
PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"

NET_NAME:pn-networkmanager = "eth"
NET_NUMS:pn-networkmanager = "0 1"

# The hook carries its own separator: a setter writes the leading space itself.
TORIZON_TTY_DEVICE ?= "ttymxc0,115200"
TORIZON_SOC_KERNEL_ARGS ?= ""

# clk-imx6sx reparents the UART without recomputing the divisor, so earlycon is
# unreadable until the serial driver probes.
TORIZON_SOC_KERNEL_ARGS:mx6sx-generic-bsp = " uart_from_osc"

# root= and rootfstype= come from u-boot-distro-boot's uEnv.txt, not from here.
OSTREE_KERNEL_ARGS:append:sota = " console=${TORIZON_TTY_DEVICE} earlycon${TORIZON_SOC_KERNEL_ARGS}"
