PACKAGES:prepend = "ostree-devicetree-overlays "
ALLOW_EMPTY:ostree-devicetree-overlays = "1"
FILES:ostree-devicetree-overlays = "${nonarch_base_libdir}/modules/*/dtb/*.dtbo ${nonarch_base_libdir}/modules/*/dtb/overlays.txt"

# Overlays in KERNEL_DEVICETREE to be applied during boot time
KERNEL_DEVICETREE_OVERLAY_BOOT ?= ""

do_install:append () {
    if [ ${@ oe.types.boolean('${OSTREE_DEPLOY_DEVICETREE}')} = True ]; then
        install -d $kerneldir/dtb/overlays
        if [ -d "${DEPLOY_DIR_IMAGE}/overlays" ]; then
            if [ ! -e ${DEPLOY_DIR_IMAGE}/overlays/none_deployed ]; then
                install -m 0644 ${DEPLOY_DIR_IMAGE}/overlays/*.dtbo $kerneldir/dtb/overlays
                install -m 0644 ${DEPLOY_DIR_IMAGE}/overlays.txt $kerneldir/dtb
            fi
        fi

        kernel_overlays=""
        if [ -n "$(find "$kerneldir/dtb/" -maxdepth 1 -type f -name "*.dtbo" | head -n1)" ]; then
            for dtbo in ${KERNEL_DEVICETREE_OVERLAY_BOOT}; do
                if [ ! -e "$kerneldir/dtb/$dtbo" ]; then
                    bbfatal "$dtbo wasn't generated during the kernel build step, please make sure it's listed in KERNEL_DEVICETREE."
                fi
                kernel_overlays="$kernel_overlays $dtbo"
            done

            # Move overlays built during kernel compilation to the correct directory
            mv $kerneldir/dtb/*.dtbo $kerneldir/dtb/overlays/
        fi

        if [ ! -f "$kerneldir/dtb/overlays.txt" ]; then
            echo "fdt_overlays=$(echo $kernel_overlays)" > "${DEPLOY_DIR_IMAGE}/overlays.txt"
            install -m 0644 ${DEPLOY_DIR_IMAGE}/overlays.txt $kerneldir/dtb
        elif [ -n "$kernel_overlays" ]; then
            bbwarn "overlays.txt already exists in the boot filesystem. Appending kernel overlays listed in KERNEL_DEVICETREE_OVERLAY_BOOT."
            echo " $kernel_overlays" >> "$kerneldir/dtb/overlays.txt"
        fi

    elif [ "${KERNEL_IMAGETYPE}" = "fitImage" ]; then
        if [ -e ${DEPLOY_DIR_IMAGE}/overlays.txt ]; then
            install -d $kerneldir/dtb
            install -m 0644 ${DEPLOY_DIR_IMAGE}/overlays.txt $kerneldir/dtb
        fi
    fi
}
do_install[depends] += "${@'virtual/dtb:do_deploy' if '${PREFERRED_PROVIDER_virtual/dtb}' else ''}"
