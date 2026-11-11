# virtual/dtb on MediaTek is this recipe, not toradex-devicetree: deploy
# overlays.txt here, with toradex-devicetree.bbclass's boot-overlay contract.
TEZI_EXTERNAL_KERNEL_DEVICETREE_BOOT ??= ""

do_deploy:append () {
    overlays=
    for dtbo in ${TEZI_EXTERNAL_KERNEL_DEVICETREE_BOOT}; do
        if [ ! -e ${DEPLOYDIR}/devicetree/$dtbo ]; then
            bbfatal "$dtbo is not provided by ${PN}, please make sure it's in its SRC_URI."
        fi
        overlays="$overlays $dtbo"
    done

    echo "fdt_overlays=$(echo $overlays)" > ${DEPLOYDIR}/overlays.txt
}
