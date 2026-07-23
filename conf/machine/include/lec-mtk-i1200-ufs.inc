# Torizon integration overlay for the Adlink LEC-MTK-i1200 (Genio 1200 / MT8395),
# UFS boot variant. Auto-included for MACHINE=lec-mtk-i1200-ufs via the local.conf
# template, on top of the Adlink vendor machine (meta-adlink-mtk).

require common-mtk.inc

hostname:pn-base-files = "torizon-lec-mtk-i1200-ufs"

# Kernel command line for the OSTree / OTA rootfs. Console is uart0 (ttyS0) at
# 921600, matching the board DTS stdout-path and U-Boot.
OSTREE_KERNEL_ARGS:append:sota = " console=ttyS0,921600 earlycon root=LABEL=otaroot rootfstype=ext4 rootwait"

# The board carries a TPM2.0.
DISTRO_FEATURES:append = " tpm2"
