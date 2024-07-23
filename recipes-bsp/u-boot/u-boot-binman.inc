FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-binman:"

python() {
    # If U-Boot is not providing the boot container we also disable BINMAN
    # altogether to avoid build errors.
    #
    # TODO: Consider doing this at the BSP level.
    #
    disable_binman_patches_imx8m = " file://0001-imx8m-Kconfig-do-not-enable-binman-by-default.patch"
    if d.getVar("UBOOT_PROVIDES_BOOT_CONTAINER") != "1":
        d.setVar("SRC_URI:append:mx8m-generic-bsp", disable_binman_patches_imx8m)
}
