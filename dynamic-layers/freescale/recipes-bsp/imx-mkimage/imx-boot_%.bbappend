# The head.hash and SECO container this recipe deploys under imx-boot-tools/ are
# packed into the TCB signing tarball out of DEPLOY_DIR_IMAGE, which serves them
# as hardlinks into this recipe's WORKDIR. rm_work would otherwise be free to
# remove them while the packing task reads.
require recipes-bsp/tcb-signing-files/tcb-signing-files.inc

do_rm_work[depends] += "${TCB_SIGNING_PACK_DEPENDS}"

# mkimage_imx8 appends the imx-mkimage commit (head.hash) to the boot
# container. TCB needs the same bytes to rebuild the container identically.
do_deploy:append:apalis-imx8() {
    if [ "${TCB_SIGNING_SUPPORT}" = "1" ]; then
        install -m 0644 ${BOOT_STAGING}/head.hash ${DEPLOYDIR}/${BOOT_TOOLS}/head.hash
    fi
}
