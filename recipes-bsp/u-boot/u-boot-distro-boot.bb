DESCRIPTION = "Boot script for launching images with U-Boot distro boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "u-boot-mkimage-native"

SRC_URI = "\
    file://boot.cmd.in \
    file://uEnv.txt.in \
"

APPEND ?= ""

KERNEL_BOOTCMD ??= "bootz"
KERNEL_BOOTCMD:aarch64 ?= "booti"

DTB_PREFIX ??= "${@d.getVar('KERNEL_DTB_PREFIX').replace("/", "_") if d.getVar('KERNEL_DTB_PREFIX') else ''}"

# FITCONF_FDT_OVERLAYS: String in this variable will be added by the boot
# script to the string passed to 'bootm' when booting a FIT image. This can
# be leveraged to force some FIT configurations (such as configurations
# representing device-tree overlays) to be used when booting.
FITCONF_FDT_OVERLAYS ??= ""

# FDT_OVERLAYS_RESIZE: extra space added to the device tree (fdt resize) before
# applying device-tree overlays, when not booting a FIT image. It must fit all
# overlays applied at boot, otherwise fdt apply fails and leaves the device
# tree unusable.
FDT_OVERLAYS_RESIZE ??= "0x20000"

require u-boot-fusing.inc

UENV_VARS_TO_DEL ?= ""
# List of uEnv.txt variables not present during initial generation
UENV_VARS_TO_DEL_EXTRA ?= "kernel_image \
    ramdisk_image \
    fdtdir \
    bootargs \
    kernel_image2 \
    ramdisk_image2 \
    fdtdir2 \
    bootargs2 \
    fdtfile2 \
    kernel_image_type2 \
    index \
    temp \
"

def get_bank_word_internal(d):
    bank_word_var = d.getVar("BANK_WORD")
    if not bank_word_var:
        return ""

    bank_word_in=list(eval(bank_word_var))
    bank_word=""
    for i, bw in enumerate(bank_word_in, start=1):
        bank_word+="fuse_bank_word_{}={} {}\\n".format(i, bw[0], bw[1])
    return bank_word

BANK_WORD_INTERNAL = "${@get_bank_word_internal(d)}"

keep_fusing_block() {
    local uenvfile="${1?uEnv.txt file must be specified}"
    local suffix="${2?suffix must be specified}"
    local bs="#+START_FUSING_BLOCK_"
    local be="#+END_FUSING_BLOCK_"

    # Find all blocks following the format:
    # #+START_FUSING_BLOCK_<some-suffix>
    # ...
    # #+END_FUSING_BLOCK_<some-suffix>
    #
    suffixes=$(cat "${uenvfile}" | \
               sed -n -e "s/^${bs}\([A-Z_]\+\).*$/\1/p" | \
               sort | uniq)

    bbdebug 1 "Identified the following fusing block suffixes:" ${suffixes}

    for sf_ in ${suffixes}; do
        if [ "${sf_}" = "${suffix}" ]; then
            bbdebug 1 "Keeping fusing code block with suffix ${sf_} in uEnv.txt."
            sed -e "/^${bs}${sf_}\([[:space:]].*$\|$\)/d" \
                -e "/^${be}${sf_}\([[:space:]].*$\|$\)/d" \
                -i "${uenvfile}"
        else
            bbdebug 1 "Removing fusing code block with suffix ${sf_} from uEnv.txt."
            sed -e "/^${bs}${sf_}\([[:space:]].*$\|$\)/,/^${be}${sf_}\([[:space:]].*$\|$\)/d" \
                -i "${uenvfile}"
        fi
    done
}

inherit deploy

# BOOTCMD_INIT_ENV_SUPP: when set to "1", includes a block in boot.cmd that
# forces a 'env save' on first boot to ensure the U-Boot environment storage
# is properly initialized. Required on platforms where the environment area is
# not pre-initialized by the bootloader (e.g. luna-sl1680).
BOOTCMD_INIT_ENV_SUPP ?= "0"
BOOTCMD_INIT_ENV_SUPP:luna-sl1680 = "1"
BOOTCMD_INIT_ENV_SUPP:sl1680 = "1"

UBOOT_BOOT_PARTITION_NUMBER ?= "1"
OTAROOT_PARTITION_NUMBER ?= "1"
UENV_EXTRA_CONFIGS ?= "true"
BOOTARG_ROOT ?= "LABEL=otaroot"

do_compile() {
    sed -e 's/@@UBOOT_BOOT_PARTITION_NUMBER@@/${UBOOT_BOOT_PARTITION_NUMBER}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_DTB_PREFIX@@/${DTB_PREFIX}/' \
        -e 's/@@APPEND@@/${APPEND}/' \
        -e 's/@@FITCONF_FDT_OVERLAYS@@/${FITCONF_FDT_OVERLAYS}/' \
        "${WORKDIR}/boot.cmd.in" > boot.cmd

    if [ "${BOOTCMD_INIT_ENV_SUPP}" = "1" ]; then
        sed -i "/#+START_ENVSAVE_BLOCK/d; /#+END_ENVSAVE_BLOCK/d" "boot.cmd"
    else
        sed -i "/#+START_ENVSAVE_BLOCK/,/#+END_ENVSAVE_BLOCK/d" "boot.cmd"
    fi

    bbdebug 1 "Building uEnv.txt..."
    sed -e 's#@@UENV_EXTRA_CONFIGS@@#${UENV_EXTRA_CONFIGS}#' \
        -e 's/@@OTAROOT_PARTITION_NUMBER@@/${OTAROOT_PARTITION_NUMBER}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_DTB_PREFIX@@/${DTB_PREFIX}/' \
        -e 's/@@FITCONF_FDT_OVERLAYS@@/${FITCONF_FDT_OVERLAYS}/' \
        -e 's/@@BOOTARG_ROOT@@/${BOOTARG_ROOT}/' \
        -e 's/@@FDT_OVERLAYS_RESIZE@@/${FDT_OVERLAYS_RESIZE}/' \
        ${WORKDIR}/uEnv.txt.in > ${WORKDIR}/uEnv.txt.temp

    if [ "${FUSE_SUPPORT}" = "1" ]; then
        if [ "${FUSE_SUPPORT_TYPE}" = "NXP" ]; then
            sed -e "s/@@BANK_WORD@@/${BANK_WORD_INTERNAL}/" \
                -e 's/@@FUSE_NUM@@/${FUSE_NUM}/' \
                -e 's/@@STATUS_CMD@@/${STATUS_CMD}/' \
                -e 's/@@CLOSE_CMD@@/${CLOSE_CMD}/' \
                -i ${WORKDIR}/uEnv.txt.temp
            keep_fusing_block ${WORKDIR}/uEnv.txt.temp "NXP"
        else
            bbfatal "Unknown FUSE_SUPPORT_TYPE (${FUSE_SUPPORT_TYPE})."
        fi
    else
        keep_fusing_block ${WORKDIR}/uEnv.txt.temp "DUMMY"
    fi

    if grep -q "@@VARS_TO_DEL@@" ${WORKDIR}/uEnv.txt.temp; then
        vars_to_del="${UENV_VARS_TO_DEL}"
        if [ -z "${vars_to_del}" ]; then
            vars_to_del=$(sed -n -e 's/^\([A-Za-z_][A-Za-z0-9_]*\)=.*/\1/gp' \
	                      ${WORKDIR}/uEnv.txt.temp)
        fi
        vars_to_del="${vars_to_del} ${UENV_VARS_TO_DEL_EXTRA}"
        vars_to_del=$(echo ${vars_to_del})
        sed -e "s/@@VARS_TO_DEL@@/${vars_to_del}/" \
            -i ${WORKDIR}/uEnv.txt.temp
    fi

    if grep -q "@@.*@@" ${WORKDIR}/uEnv.txt.temp; then
        missing=$(sed -n -e 's/^.*\(@@[A-Z0-9_]\+@@\).*$/\1/p' ${WORKDIR}/uEnv.txt.temp)
        missing=$(echo ${missing})
        bbfatal "Some build-time variables have not been replaced in uEnv.txt: ${missing}."
    fi

    # Drop full-line comments from the file.
    sed -e "/^#\([[:blank:]].*\|\)$/d" -i ${WORKDIR}/uEnv.txt.temp

    cp ${WORKDIR}/uEnv.txt.temp uEnv.txt
}

do_install () {
    install -d ${D}${libdir}/ostree-boot
    install -m 0644 uEnv.txt ${D}${libdir}/ostree-boot/
}

do_deploy() {
    mkimage -T script -C none -n "Distro boot script" -d boot.cmd boot.scr
    install -m 0644 boot.scr ${DEPLOYDIR}/boot.scr-${MACHINE}
}
addtask deploy after do_install before do_build

PROVIDES += "u-boot-default-script"

TDX_AMEND_BOOT_SCRIPT:torizon-distro = "0"
TDX_AMEND_BOOT_SCRIPT:common-distro = "0"

PACKAGES = "ostree-uboot-env"
FILES:ostree-uboot-env = "${libdir}/ostree-boot/uEnv.txt"

PACKAGE_ARCH = "${MACHINE_ARCH}"
