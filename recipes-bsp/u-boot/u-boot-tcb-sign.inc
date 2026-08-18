# Deploy additional files needed by TorizonCore Builder to recreate the bootloader container from its constituent parts
# This is needed by TCB for its signing feature

# Set ${tcbsgn_cfgdir} to the directory U-Boot was built in, following the same
# two branches as oe-core's do_uboot_assemble_fitimage(). More than one
# UBOOT_MACHINE entry is fatal, since every deploy name here is flat. The result
# comes back in a variable because BitBake emits only the shell functions it
# sees called, and a call inside a command substitution is not one of them.
tcbsgn_uboot_set_config_dir() {
    if [ -n "${UBOOT_CONFIG}" ]; then
        set -- ${UBOOT_MACHINE}
        if [ $# -ne 1 ]; then
            bbfatal "Expected a single U-Boot config in UBOOT_MACHINE" \
                    "(\"${UBOOT_MACHINE}\"); do_deploy() must be reviewed."
        fi
        tcbsgn_cfgdir="${B}/$1"
    else
        tcbsgn_cfgdir="${B}"
    fi
}

tcbsgn_check_config() {
    local cfg="${1?property name in the U-Boot config}"
    local exp="${2?property value}"
    local cur

    tcbsgn_uboot_set_config_dir

    # An absent property and an empty one both yield ""; either way the
    # assumption being checked no longer holds.
    cur=$(sed -ne "s/^${cfg}=\"\(.*\)\"$/\1/p" "${tcbsgn_cfgdir}/.config")
    if [ "${cur}" != "${exp}" ]; then
        bbfatal "Value of \"${cfg}\" property in the U-Boot config" \
		"(\"${cur}\") does not match the expected value (\"${exp}\");" \
                "do_deploy() must be reviewed."
    fi
}

tcbsgn_deploy_common_files_imx8m() {
    tcbsgn_uboot_set_config_dir

    install -m 0644 ${tcbsgn_cfgdir}/.config ${DEPLOYDIR}/uboot_config
    install -d ${DEPLOYDIR}/spl
    install -m 0644 ${tcbsgn_cfgdir}/spl/u-boot-spl ${DEPLOYDIR}/spl/
    install -m 0644 ${tcbsgn_cfgdir}/spl/u-boot-spl.dtb ${DEPLOYDIR}/spl/
    install -m 0644 ${tcbsgn_cfgdir}/spl/u-boot-spl-nodtb.bin ${DEPLOYDIR}/spl/
}

do_deploy:append:verdin-imx8mp() {
    local def_dt="freescale/imx8mp-verdin-wifi-dev"

    tcbsgn_uboot_set_config_dir

    # Check assumptions:
    tcbsgn_check_config "CONFIG_DEFAULT_DEVICE_TREE" "${def_dt}"
    tcbsgn_check_config "CONFIG_OF_LIST" "${def_dt}"

    # Deploy common files:
    tcbsgn_deploy_common_files_imx8m

    # Deploy files specific to the imx8mp:
    install -d "${DEPLOYDIR}/u-boot-dtbs/freescale"
    install -m 0644 "${tcbsgn_cfgdir}/dts/upstream/src/arm64/${def_dt}.dtb" \
                    "${DEPLOYDIR}/u-boot-dtbs/freescale/"
}

do_deploy:append:verdin-imx8mm() {
    local def_dt="freescale/imx8mm-verdin-wifi-dev"

    tcbsgn_uboot_set_config_dir

    # Check assumptions:
    tcbsgn_check_config "CONFIG_DEFAULT_DEVICE_TREE" "${def_dt}"
    tcbsgn_check_config "CONFIG_OF_LIST" "${def_dt}"

    # Deploy common files:
    tcbsgn_deploy_common_files_imx8m

    # Deploy files specific to the imx8mm:
    install -d "${DEPLOYDIR}/u-boot-dtbs/freescale"
    install -m 0644 "${tcbsgn_cfgdir}/dts/upstream/src/arm64/${def_dt}.dtb" \
                    "${DEPLOYDIR}/u-boot-dtbs/freescale/"
}
