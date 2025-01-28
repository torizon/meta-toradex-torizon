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

# Fusing support inside uEnv.txt:
#
# - FUSE_SUPPORT_SIGNED_BUILD: internal usage (automatically set to "1" on tdx-signed builds).
# - FUSE_SUPPORT_DEFAULT: internal usage (default value of FUSE_SUPPORT)
# - FUSE_SUPPORT: whether or not to add support for fusing in uEnv.txt.
# - FUSE_SUPPORT_TYPE: type of support to add (allowed value: "NXP").
#
FUSE_SUPPORT_SIGNED_BUILD = "0"
FUSE_SUPPORT_SIGNED_BUILD:tdx-signed = "1"
FUSE_SUPPORT_DEFAULT = "0"
FUSE_SUPPORT ?= "${FUSE_SUPPORT_DEFAULT}"
FUSE_SUPPORT_TYPE = ""

# Machine specific values related to HAB/AHAB fusing (i.MX specific)
BANK_WORD = ""
FUSE_NUM = ""
STATUS_CMD = ""
CLOSE_CMD = ""

IMX6_COMMON_BANK_WORD = "\
    (3, 0), \
    (3, 1), \
    (3, 2), \
    (3, 3), \
    (3, 4), \
    (3, 5), \
    (3, 6), \
    (3, 7) \
"

IMX7_IMX8M_COMMON_BANK_WORD = "\
    (6, 0), \
    (6, 1), \
    (6, 2), \
    (6, 3), \
    (7, 0), \
    (7, 1), \
    (7, 2), \
    (7, 3) \
"

FUSE_NUM_8 = "1 2 3 4 5 6 7 8"
FUSE_NUM_16 = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16"

HAB_STATUS_CMD = "hab_status"
AHAB_STATUS_CMD = "ahab_status"

IMX6_COMMON_CLOSE_CMD = "fuse prog -y 0 6 0x00000002"
IMX7_IMX8M_COMMON_CLOSE_CMD = "fuse prog -y 1 3 0x02000000"
AHAB_CLOSE_CMD = "ahab_close"

BANK_WORD:mx6ull-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6ull-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6ull-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6ull-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx6ull-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx6ull-generic-bsp = "NXP"

BANK_WORD:mx6q-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6q-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6q-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6q-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx6q-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx6q-generic-bsp = "NXP"

BANK_WORD:mx6dl-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6dl-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6dl-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6dl-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx6dl-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx6dl-generic-bsp = "NXP"

BANK_WORD:mx7-generic-bsp = "${IMX7_IMX8M_COMMON_BANK_WORD}"
FUSE_NUM:mx7-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx7-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx7-generic-bsp = "${IMX7_IMX8M_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx7-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx7-generic-bsp = "NXP"

BANK_WORD:mx8m-generic-bsp = "${IMX7_IMX8M_COMMON_BANK_WORD}"
FUSE_NUM:mx8m-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx8m-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx8m-generic-bsp = "${IMX7_IMX8M_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx8m-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx8m-generic-bsp = "NXP"

BANK_WORD:mx8qm-generic-bsp = "\
    (0, 722), \
    (0, 723), \
    (0, 724), \
    (0, 725), \
    (0, 726), \
    (0, 727), \
    (0, 728), \
    (0, 729), \
    (0, 730), \
    (0, 731), \
    (0, 732), \
    (0, 733), \
    (0, 734), \
    (0, 735), \
    (0, 736), \
    (0, 737) \
"
FUSE_NUM:mx8qm-generic-bsp = "${FUSE_NUM_16}"
STATUS_CMD:mx8qm-generic-bsp = "${AHAB_STATUS_CMD}"
CLOSE_CMD:mx8qm-generic-bsp = "${AHAB_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx8qm-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx8qm-generic-bsp = "NXP"

BANK_WORD:mx8x-generic-bsp = "\
    (0, 730), \
    (0, 731), \
    (0, 732), \
    (0, 733), \
    (0, 734), \
    (0, 735), \
    (0, 736), \
    (0, 737), \
    (0, 738), \
    (0, 739), \
    (0, 740), \
    (0, 741), \
    (0, 742), \
    (0, 743), \
    (0, 744), \
    (0, 745) \
"
FUSE_NUM:mx8x-generic-bsp = "${FUSE_NUM_16}"
STATUS_CMD:mx8x-generic-bsp = "${AHAB_STATUS_CMD}"
CLOSE_CMD:mx8x-generic-bsp = "${AHAB_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx8x-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx8x-generic-bsp = "NXP"

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

UBOOT_BOOT_PARTITION_NUMBER ?= "1"
OTAROOT_PARTITION_NUMBER ?= "1"
UENV_EXTRA_CONFIGS ?= "true"

do_compile() {
    sed -e 's/@@UBOOT_BOOT_PARTITION_NUMBER@@/${UBOOT_BOOT_PARTITION_NUMBER}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_DTB_PREFIX@@/${DTB_PREFIX}/' \
        -e 's/@@APPEND@@/${APPEND}/' \
        -e 's/@@FITCONF_FDT_OVERLAYS@@/${FITCONF_FDT_OVERLAYS}/' \
        "${WORKDIR}/boot.cmd.in" > boot.cmd

    bbdebug 1 "Building uEnv.txt..."
    sed -e 's#@@UENV_EXTRA_CONFIGS@@#${UENV_EXTRA_CONFIGS}#' \
        -e 's/@@OTAROOT_PARTITION_NUMBER@@/${OTAROOT_PARTITION_NUMBER}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_DTB_PREFIX@@/${DTB_PREFIX}/' \
        -e 's/@@FITCONF_FDT_OVERLAYS@@/${FITCONF_FDT_OVERLAYS}/' \
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
