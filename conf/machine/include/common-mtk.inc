# Shared Torizon <-> MediaTek BSP integration glue for Genio 1200 (MT8395)
# machines provided by meta-adlink-mtk / meta-mediatek-bsp.

# --- GPU userspace ----------------------------------------------------------
# The BSP (mt8195.inc) defaults to the proprietary Mali DDK r48. Uncomment to
# switch to the open-source Panfrost/Mesa stack (overrides the BSP's ?= defaults).
#PREFERRED_PROVIDER_virtual/egl      = "mesa"
#PREFERRED_PROVIDER_virtual/libegl   = "mesa"
#PREFERRED_PROVIDER_virtual/libgbm   = "mesa"
#PREFERRED_PROVIDER_virtual/libgl    = "mesa"
#PREFERRED_PROVIDER_virtual/libgles1 = "mesa"
#PREFERRED_PROVIDER_virtual/libgles2 = "mesa"
#PREFERRED_PROVIDER_virtual/libgles3 = "mesa"
#PREFERRED_PROVIDER_virtual/mesa     = "mesa"

# --- OTA / U-Boot environment -----------------------------------------------
# Use libubootenv for OTA env access, as on other Torizon BSPs.
PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_RPROVIDER_u-boot-fw-utils = "libubootenv"

# --- OSTree -> U-Boot boot integration --------------------------------------
# Boot the stock Torizon way: distro_bootcmd scans storage, sources /boot.scr,
# which imports the OSTree-managed /boot/loader/uEnv.txt and runs bootcmd_run.
# u-boot-distro-boot provides boot.scr and the base uEnv.txt; this PREFERRED_-
# PROVIDER also triggers the ostree bbappend to install ostree-uboot-env.
PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"

# Torizon's u-boot-fuse.inc appends i.MX-only HAB patches that fail do_patch on
# the MediaTek u-boot. Disable them for this non-i.MX machine.
TDX_UBOOT_FUSE_PATCHES:lec-mtk1200 = ""

# Native deps the vendor recipes omit: u-boot's mkeficapsule needs gnutls/libuuid,
# TF-A's fiptool/bl2 need libcrypto; otherwise do_compile fails to find them.
DEPENDS:append:pn-u-boot = " gnutls-native util-linux-native"
DEPENDS:append:pn-trusted-firmware-a = " openssl-native"
DEPENDS:append:pn-bl2 = " openssl-native"

# Bootable partition is resolved at runtime by the distro scan (rootfs is marked
# --active in the wks, and is not partition #1). Thread ${distro_bootpart} into
# boot.scr and otaroot instead of hardcoding the index.
UBOOT_BOOT_PARTITION_NUMBER = "${distro_bootpart}"
# fitImage conf node is conf-<KERNEL_DEVICETREE, '/'->'_'>; point fdtfile/fdt_file at it.
FITIMAGE_CONF_NODE = "${@d.getVar('KERNEL_DEVICETREE').split()[0].replace('/', '_')}"
UENV_EXTRA_CONFIGS = "env set otaroot ${distro_bootpart} ; env set fdtfile ${FITIMAGE_CONF_NODE} ; env set fdt_file ${FITIMAGE_CONF_NODE}"

# Device tree deployed into the OSTree boot directory.
OSTREE_DEVICETREE ?= "mediatek/lec-mtk-i1200-ufs.dtb"

# --- OP-TEE userspace -------------------------------------------------------
# tee-supplicant; not pulled in by the BSP, so install it explicitly.
IMAGE_INSTALL:append = " optee-client"

# --- DRAM calibration (libdram) ---------------------------------------------
# Without the MediaTek NDA, TF-A links prebuilt libdram, which ships only a
# "genio-1200-evk" config (no "-ufs"). Use it for the prebuilt path, keep the
# -ufs name for the NDA source build. This is ADLINK's own vendor default for
# the module (meta-adlink-mtk's lec-mtk-i1200-ufs.conf sets the same).
LIBDRAM_BOARD_NAME:lec-mtk1200 = "${@bb.utils.contains('DISTRO_FEATURES', 'nda-mtk', 'genio-1200-evk-ufs', 'genio-1200-evk', d)}"

# --- Image / flash integration ----------------------------------------------
# Torizon OSTree layout: FIP bootloader + single ext4 OSTree sysroot, no /boot
# partition. The vendor mediatek-bootimg.wks fails do_image_wic once
# IMAGE_BOOT_FILES is empty, so use the OSTree wks (matches the genio-flash
# partition table: bootloaders + rootfs).
WKS_FILE:sota = "torizon-mtk-sota.wks"

# Mask meta-adlink-mtk bbappends for recipes provided only by meta-rity and
# board-assets, which Torizon does not use.
BBMASK += "meta-adlink-mtk/recipes-rity/"
BBMASK += "meta-adlink-mtk/recipes-core/board-assets/"
