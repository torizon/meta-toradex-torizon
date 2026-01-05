# Deploy additional files needed by TorizonCore Builder to recreate the bootloader container from its constituent parts
# This is needed by TCB for its signing feature

tcbsgn_check_config() {
    local cfg="${1?property name in the U-Boot config}"
    local exp="${2?property value}"
    local cur

    cur=$(cat "${B}/${MACHINE}_defconfig/.config" | sed -ne "s/^${cfg}=\"\(.*\)\"$/\1/p")
    if [ "${cur}" != "${exp}" ]; then
        bbfatal "Value of \"${cfg}\" property in the U-Boot config" \
		"(\"${cur}\") does not match the expected value (\"${exp}\");" \
                "do_deploy() must be reviewed."
    fi
}

tcbsgn_deploy_common_files_imx8m() {
    install -m 0644 ${B}/${MACHINE}_defconfig/.config ${DEPLOYDIR}/uboot_config
    install -d ${DEPLOYDIR}/spl
    install -m 0644 ${B}/${MACHINE}_defconfig/spl/u-boot-spl ${DEPLOYDIR}/spl/
    install -m 0644 ${B}/${MACHINE}_defconfig/spl/u-boot-spl.dtb ${DEPLOYDIR}/spl/
    install -m 0644 ${B}/${MACHINE}_defconfig/spl/u-boot-spl-nodtb.bin ${DEPLOYDIR}/spl/
}

do_deploy:append:verdin-imx8mp() {
    # Check assumptions:
    local def_dt="freescale/imx8mp-verdin-wifi-dev"
    tcbsgn_check_config "CONFIG_DEFAULT_DEVICE_TREE" "${def_dt}"
    tcbsgn_check_config "CONFIG_OF_LIST" "${def_dt}"

    # Deploy common files:
    tcbsgn_deploy_common_files_imx8m

    # Deploy files specific to the imx8mp:
    install -d "${DEPLOYDIR}/u-boot-dtbs/freescale"
    install -m 0644 "${B}/${MACHINE}_defconfig/dts/upstream/src/arm64/${def_dt}.dtb" \
                    "${DEPLOYDIR}/u-boot-dtbs/freescale/"
}
}
