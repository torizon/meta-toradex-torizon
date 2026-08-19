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

# TCB signing export tree, for the AM62. Its two multiconfigs produce files with
# identical basenames and different content, so the flat file list used on the
# iMX8M cannot describe them: export one root per multiconfig instead, each a
# binman working directory a consumer can enter and run binman in. Deliberately
# absent, for the consumer to supply: arch/arm/mach-k3/keys/ and u-boot.dtb.

# Root of the export tree, relative to DEPLOYDIR. TCB_SIGNING_FILELIST must
# name the same directory; keep the two in step.
TCB_SIGNING_EXPORT_DIR ?= "tcb-signing"
TCB_SIGNING_EXPORT_DIR_A53 ?= "${TCB_SIGNING_EXPORT_DIR}/a53"
TCB_SIGNING_EXPORT_DIR_K3R5 ?= "${TCB_SIGNING_EXPORT_DIR}/k3r5"

# Where the A53 binman descriptor's include path looks for firmware. Fixed, so
# distro configuration cannot move it; the k3r5 one names ti-sysfw/ instead.
TCB_SIGNING_EXPORT_FWDIR ?= "usr/lib/firmware"

# Install a regular file into the export tree, creating its directory. Symlinks
# are dereferenced, so the tarball carries no dangling link and no duplicate.
tcbsgn_export_install() {
    local src="${1?source file}"
    local dstdir="${2?destination directory}"

    if [ ! -f "${src}" ]; then
        bbfatal "\"${src}\" not found, but it is required by the TCB signing" \
                "export for MACHINE \"${MACHINE}\". Either the U-Boot build" \
                "layout changed or this artifact is no longer produced;" \
                "do_deploy() must be reviewed."
    fi

    install -d "${dstdir}"
    install -m 0644 "${src}" "${dstdir}/"
}

# Everything binman needs to rebuild and re-sign tispl.bin, u-boot.img and
# firmware-verdin-am62-*. All of it comes from this recipe's own workdir, ${S}
# or sysroot, which is why the tarball recipe needs no other producer.
tcbsgn_deploy_am62_export_a53() {
    local def_dt="k3-am625-verdin-wifi-dev"
    local boarddir="board/toradex/verdin-am62"
    local root="${DEPLOYDIR}/${TCB_SIGNING_EXPORT_DIR_A53}"
    local srcfw="${RECIPE_SYSROOT}${nonarch_base_libdir}/firmware"
    local dstfw="${root}/${TCB_SIGNING_EXPORT_FWDIR}"
    local f

    tcbsgn_uboot_set_config_dir

    # Assumptions the paths below are written against. The of-list checks also
    # guard the layout: one DTB per root holds only while binman builds one DT.
    tcbsgn_check_config "CONFIG_SYS_VENDOR" "toradex"
    tcbsgn_check_config "CONFIG_SYS_BOARD" "verdin-am62"
    tcbsgn_check_config "CONFIG_DEFAULT_DEVICE_TREE" "${def_dt}"
    tcbsgn_check_config "CONFIG_OF_LIST" "${def_dt}"
    tcbsgn_check_config "CONFIG_SPL_OF_LIST" "${def_dt}"

    # The binman image descriptor, taken from the key-free snapshot: u-boot.dtb
    # in the build tree already carries this build's FIT key.
    tcbsgn_export_install "${tcbsgn_cfgdir}/${TCB_SIGNING_UBOOT_DTB_NOKEYS}" "${root}"

    # Payload of the u-boot FIT subimage.
    tcbsgn_export_install "${tcbsgn_cfgdir}/u-boot-nodtb.bin" "${root}"

    # Only u-boot-spl-nodtb.bin is a payload; binman reads the unstripped ELF
    # and u-boot-spl.sym to resolve SPL symbols.
    for f in u-boot-spl u-boot-spl.bin u-boot-spl-dtb.bin u-boot-spl-nodtb.bin \
             u-boot-spl.sym; do
        tcbsgn_export_install "${tcbsgn_cfgdir}/spl/${f}" "${root}/spl"
    done

    # SPL control DTB: tispl.bin's signed fdt-0 payload.
    tcbsgn_export_install "${tcbsgn_cfgdir}/spl/dts/${def_dt}.dtb" "${root}/spl/dts"

    # of-list DTB. Same basename as the SPL control and kernel DTBs, different
    # content, which is why this export is a tree and not a file list.
    tcbsgn_export_install "${tcbsgn_cfgdir}/arch/arm/dts/${def_dt}.dtb" \
                          "${root}/arch/arm/dts"

    # binman regenerates the TIFS and DM config blobs from these rather than
    # reusing the built ones; each root keeps its own copy to stay independent.
    for f in board pm rm sec; do
        tcbsgn_export_install "${S}/${boarddir}/${f}-cfg.yaml" "${root}/${boarddir}"
    done
    tcbsgn_export_install "${S}/arch/arm/mach-k3/schema.yaml" \
                          "${root}/arch/arm/mach-k3"

    # Firmware subimage inputs, at the sysroot paths meta-ti's u-boot-ti.inc
    # uses (PACKAGECONFIG[atf], [optee], [dm]). bl31.bin sits at the root.
    tcbsgn_export_install "${RECIPE_SYSROOT}/firmware/bl31.bin" "${root}/firmware"
    tcbsgn_export_install "${srcfw}/bl32.bin" "${dstfw}"
    tcbsgn_export_install "${srcfw}/ti-dm/${PLAT_SFX}/${DM_FIRMWARE}" \
                          "${dstfw}/ti-dm/${PLAT_SFX}"

    # All three stub blobs go into tispl.bin whatever the device type; the
    # per-variant ones belong to the k3r5 root.
    for f in gp hs-cert hs-enc; do
        tcbsgn_export_install "${srcfw}/ti-sysfw/ti-fs-stub-firmware-am62x-${f}.bin" \
                              "${dstfw}/ti-sysfw"
    done

    # The A53 descriptor assembles the OTA container byproducts from these;
    # do_compile stages them from the k3r5 multiconfig through mcdepends.
    for f in gp hs hs-fs; do
        tcbsgn_export_install "${srcfw}/tiboot3-am62x-${f}-verdin.bin" "${root}"
    done
}

# Everything binman needs to rebuild and re-sign the tiboot3-am62x-*-verdin
# containers, sourced from this recipe's own workdir as in the A53 root above.
tcbsgn_deploy_am62_export_k3r5() {
    local def_dt="k3-am625-verdin-r5"
    local boarddir="board/toradex/verdin-am62"
    local root="${DEPLOYDIR}/${TCB_SIGNING_EXPORT_DIR_K3R5}"
    local srcfw="${RECIPE_SYSROOT}${nonarch_base_libdir}/firmware"
    local f

    tcbsgn_uboot_set_config_dir

    # Same checks as the A53 root, against this multiconfig's own values.
    tcbsgn_check_config "CONFIG_SYS_VENDOR" "toradex"
    tcbsgn_check_config "CONFIG_SYS_BOARD" "verdin-am62"
    tcbsgn_check_config "CONFIG_DEFAULT_DEVICE_TREE" "${def_dt}"
    tcbsgn_check_config "CONFIG_OF_LIST" "${def_dt}"
    tcbsgn_check_config "CONFIG_SPL_OF_LIST" "${def_dt}"

    # The binman image descriptor. No snapshot is needed: FIT signing is off in
    # this multiconfig, so u-boot.dtb never receives a key.
    tcbsgn_export_install "${tcbsgn_cfgdir}/${UBOOT_DTB_BINARY}" "${root}"

    # u-boot-spl.bin is the payload; binman reads the unstripped ELF and
    # u-boot-spl.sym to resolve SPL symbols.
    for f in u-boot-spl u-boot-spl.bin u-boot-spl-dtb.bin u-boot-spl-nodtb.bin \
             u-boot-spl.sym; do
        tcbsgn_export_install "${tcbsgn_cfgdir}/spl/${f}" "${root}/spl"
    done

    # of-list DTB. Same basename as the A53 root's, different content, which is
    # why each multiconfig gets a root of its own.
    tcbsgn_export_install "${tcbsgn_cfgdir}/arch/arm/dts/${def_dt}.dtb" \
                          "${root}/arch/arm/dts"

    # binman regenerates the TIFS and DM config blobs from these rather than
    # reusing the built ones; each root keeps its own copy to stay independent.
    for f in board pm rm sec; do
        tcbsgn_export_install "${S}/${boarddir}/${f}-cfg.yaml" "${root}/${boarddir}"
    done
    tcbsgn_export_install "${S}/arch/arm/mach-k3/schema.yaml" \
                          "${root}/arch/arm/mach-k3"

    # All five TIFS blobs: the descriptor declares gp, hs and hs-fs whatever
    # the device type, and --allow-missing turns a gap into an invalid container.
    for f in gp hs-cert hs-enc hs-fs-cert hs-fs-enc; do
        tcbsgn_export_install "${srcfw}/ti-sysfw/ti-fs-firmware-am62x-${f}.bin" \
                              "${root}/ti-sysfw"
    done
}

do_deploy:append:verdin-am62-k3r5() {
    # MACHINE gains a "-k3r5" suffix here, so a :verdin-am62 override does not
    # reach this task. No FIT is built, so there is no condition to mirror.
    #
    # TODO: gate this on the K3 counterpart of TCB_SIGNING_SUPPORT once the
    # tarball export defines it.
    tcbsgn_deploy_am62_export_k3r5
}

do_deploy:append:verdin-am62() {
    # Mirrors the condition under which uboot_assemble_fitimage_helper() calls
    # concat_dtb(): without the snapshot the export tree is incomplete anyway.
    #
    # TODO: gate this on the K3 counterpart of TCB_SIGNING_SUPPORT once the
    # tarball export defines it, instead of repeating the condition here.
    if [ "${UBOOT_SIGN_ENABLE}" = "1" -a -n "${UBOOT_DTB_BINARY}" ]; then
        tcbsgn_deploy_pristine_dtb
        tcbsgn_deploy_am62_export_a53
    fi
}
