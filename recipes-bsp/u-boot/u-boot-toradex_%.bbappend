require u-boot-ota.inc

deploy_uboot_with_spl () {
    #Deploy u-boot-with-spl.imx
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    install -D -m 644 ${B}/${config}/u-boot-with-spl.imx ${DEPLOYDIR}/u-boot-with-spl.imx-${MACHINE}-${type}
                    ln -sf u-boot-with-spl.imx-${MACHINE}-${type} ${DEPLOYDIR}/u-boot-with-spl.imx
                fi
            done
            unset  j
        done
        unset  i
    else
        install -D -m 644 ${B}/u-boot-with-spl.imx ${DEPLOYDIR}/u-boot-with-spl.imx
    fi
}

do_deploy:append:colibri-imx6 () {
    deploy_uboot_with_spl
}

do_deploy:append:apalis-imx6 () {
    deploy_uboot_with_spl
}
