FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-fuse:"

TDX_UBOOT_FUSE_PATCHES_COMMON = " \
    file://0001-toradex-common-add-hab-utilities-module.patch \
"

# TODO: Consider moving this to meta-toradex-security / u-boot-hab.inc
TDX_UBOOT_FUSE_PATCHES_UPSTREAM = " \
    ${TDX_UBOOT_FUSE_PATCHES_COMMON} \
    file://0001-toradex-adjust-hab_status-exit-code-behavior.patch \
"

TDX_UBOOT_FUSE_PATCHES_DOWNSTREAM = " \
    ${TDX_UBOOT_FUSE_PATCHES_COMMON} \
    file://0001-toradex-adjust-ahab_status-exit-code-behavior.patch \
"

TDX_UBOOT_FUSE_PATCHES = "${TDX_UBOOT_FUSE_PATCHES_UPSTREAM}"
TDX_UBOOT_FUSE_PATCHES:apalis-imx8 = "${TDX_UBOOT_FUSE_PATCHES_DOWNSTREAM}"
TDX_UBOOT_FUSE_PATCHES:colibri-imx8x = "${TDX_UBOOT_FUSE_PATCHES_DOWNSTREAM}"
TDX_UBOOT_FUSE_PATCHES:verdin-am62 = ""
TDX_UBOOT_FUSE_PATCHES:verdin-am62-k3r5 = ""

SRC_URI:append = " \
    ${TDX_UBOOT_FUSE_PATCHES} \
"
