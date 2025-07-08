# Deploy additional files needed by TorizonCore Builder to recreate the bootloader container from its constituent parts
# This is needed by TCB for its signing feature

do_deploy:append:verdin-imx8mp() {
    install -m 0644 ${B}/${MACHINE}_defconfig/.config ${DEPLOYDIR}/uboot_config
    install -d ${DEPLOYDIR}/spl
    install -m 0644 ${B}/${MACHINE}_defconfig/spl/u-boot-spl ${DEPLOYDIR}/spl/
    install -m 0644 ${B}/${MACHINE}_defconfig/spl/u-boot-spl.dtb ${DEPLOYDIR}/spl/
    install -m 0644 ${B}/${MACHINE}_defconfig/spl/u-boot-spl-nodtb.bin ${DEPLOYDIR}/spl/
    install -d ${DEPLOYDIR}/u-boot-dtbs/freescale
    install -m 0644 ${B}/${MACHINE}_defconfig/dts/upstream/src/arm64/freescale/imx8mp-verdin-wifi-dev.dtb \
                    ${DEPLOYDIR}/u-boot-dtbs/freescale/
}
