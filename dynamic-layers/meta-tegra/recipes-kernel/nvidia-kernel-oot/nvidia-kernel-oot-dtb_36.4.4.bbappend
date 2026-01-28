do_deploy() {
    for dtb in ${KERNEL_DEVICETREE}; do
        dtbf="${STAGING_DIR_HOST}/boot/devicetree/$dtb"
        if [ ! -f "$dtbf" ]; then
            bbfatal "Not found: $dtbf"
        fi
    done
    install -d ${DEPLOYDIR}
    install -m 0644 ${STAGING_DIR_HOST}/boot/devicetree/* ${DEPLOYDIR}/
}
