# Deploy additional files needed by TorizonCore Builder to recreate the
# bootloader container from its constituent parts, for TCB's signing feature.

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

# Key-free u-boot.dtb, for the K3 machines. With UBOOT_SIGN_ENABLE = "1",
# oe-core's uboot-sign.bbclass injects the build's own FIT signing key into
# u-boot.dtb in place, so no copy without it survives. TCB needs one to install
# the user's key into, or it cannot reproduce what Yocto built. concat_dtb() is
# where the injection happens, so a prepend there catches the file in time.

TCB_SIGNING_UBOOT_DTB_NOKEYS ?= "${UBOOT_DTB_BINARY}-nokeys"

tcbsgn_snapshot_pristine_dtb() {
    # Called with (type, binary), both unused. cwd is ${B}/${config}, set by
    # do_uboot_assemble_fitimage().
    if [ ! -e "${UBOOT_DTB_BINARY}" ]; then
        bbfatal "\"${UBOOT_DTB_BINARY}\" not found in \"$(pwd)\"; it is required" \
                "to export a pristine DTB for re-signing (and without it the" \
                "build could not embed its own FIT key either)."
    fi

    if ! fdtget -l "${UBOOT_DTB_BINARY}" / >/dev/null 2>&1; then
        bbfatal "\"$(pwd)/${UBOOT_DTB_BINARY}\" is not a readable device tree blob."
    fi

    if fdtget -l "${UBOOT_DTB_BINARY}" /signature >/dev/null 2>&1; then
        bbfatal "\"$(pwd)/${UBOOT_DTB_BINARY}\" already contains a /signature node," \
                "so it is not a pristine DTB. This usually means" \
                "do_uboot_assemble_fitimage() ran again without do_compile();" \
                "clean the recipe and rebuild."
    fi

    bbdebug 1 "Snapshotting pristine ${UBOOT_DTB_BINARY} as ${TCB_SIGNING_UBOOT_DTB_NOKEYS}"
    install -m 0644 "${UBOOT_DTB_BINARY}" "${TCB_SIGNING_UBOOT_DTB_NOKEYS}"
}

tcbsgn_deploy_pristine_dtb() {
    local srcdir

    # concat_dtb() runs with cwd set to the U-Boot build directory, so that is
    # where the snapshot was left.
    tcbsgn_uboot_set_config_dir
    srcdir="${tcbsgn_cfgdir}"

    if [ ! -e "${srcdir}/${TCB_SIGNING_UBOOT_DTB_NOKEYS}" ]; then
        bbfatal "\"${srcdir}/${TCB_SIGNING_UBOOT_DTB_NOKEYS}\" not found, but FIT" \
                "signing is enabled and this machine exports a pristine DTB for" \
                "TCB re-signing. The concat_dtb:prepend that produces it did not" \
                "run -- check whether oe-core's uboot-sign.bbclass still calls" \
                "concat_dtb()."
    fi

    # Deployed under its build-directory name, without the versioned file and
    # symlink deploy_dtb() makes, for consumers with no binman tree to read.
    install -m 0644 "${srcdir}/${TCB_SIGNING_UBOOT_DTB_NOKEYS}" \
                    "${DEPLOYDIR}/${TCB_SIGNING_UBOOT_DTB_NOKEYS}"
}

concat_dtb:prepend:verdin-am62() {
    tcbsgn_snapshot_pristine_dtb "$@"
}

do_deploy:append:verdin-am62() {
    # Mirrors the condition under which uboot_assemble_fitimage_helper() calls
    # concat_dtb(), i.e. exactly the case in which the snapshot is taken.
    #
    # TODO: gate this on the K3 counterpart of TCB_SIGNING_SUPPORT once the
    # tarball export defines it, instead of repeating the condition here.
    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${UBOOT_DTB_BINARY}" ]; then
        tcbsgn_deploy_pristine_dtb
    fi
}
