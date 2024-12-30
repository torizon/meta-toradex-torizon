WKS_FILE:sota = "torizon-imx95-sota.wks"

OSTREE_KERNEL_ARGS:sota:imx:append = "console=ttyLP0,115200 earlycon root=LABEL=otaroot rootfstype=ext4"

BBMASK += "\
    /meta-imx/meta-imx-bsp/recipes-bsp/u-boot \
"