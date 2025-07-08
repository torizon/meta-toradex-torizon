require common-am62xx.inc

TORIZON_TTY_DEVICE = "ttyS0,115200"

UENV_EXTRA_CONFIGS = "if test "${board_name}" = "am62lx" -a "${fdtfile}" = "ti/k3-am62l3-evm.dtb"; then env set fdtfile k3-am62l3-evm.dtb; fi"

OSTREE_DEVICETREE = "\
    ti/k3-am62l3-evm.dtb \
    ti/k3-am62l3-evm-lpmdemo.dtb \
    ti/k3-am62l3-evm-m2-cc3351.dtbo \
    ti/k3-am62l3-evm-pwm.dtbo \
    ti/k3-am62l3-evm-dsi-rpi-7inch-panel.dtbo \
"

