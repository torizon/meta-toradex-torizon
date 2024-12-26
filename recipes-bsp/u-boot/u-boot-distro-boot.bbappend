FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://uEnv.txt.in \
"

# FITCONF_FDT_OVERLAYS: String in this variable will be added by the boot
# script to the string passed to 'bootm' when booting a FIT image. This can
# be leveraged to force some FIT configurations (such as configurations
# representing device-tree overlays) to be used when booting.
FITCONF_FDT_OVERLAYS ??= ""

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

BANK_WORD:mx6q-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6q-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6q-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6q-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"

BANK_WORD:mx6dl-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6dl-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6dl-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6dl-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"

BANK_WORD:mx7-generic-bsp = "${IMX7_IMX8M_COMMON_BANK_WORD}"
FUSE_NUM:mx7-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx7-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx7-generic-bsp = "${IMX7_IMX8M_COMMON_CLOSE_CMD}"

BANK_WORD:mx8m-generic-bsp = "${IMX7_IMX8M_COMMON_BANK_WORD}"
FUSE_NUM:mx8m-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx8m-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx8m-generic-bsp = "${IMX7_IMX8M_COMMON_CLOSE_CMD}"

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
        bank_word+="fuse_bank_word_{}={} {};\\n".format(i, bw[0], bw[1])
    return bank_word

BANK_WORD_INTERNAL = "${@get_bank_word_internal(d)}"

do_compile:append () {
    bbdebug 1 "Building uEnv.txt..."
    sed -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_DTB_PREFIX@@/${DTB_PREFIX}/' \
        -e 's/@@FITCONF_FDT_OVERLAYS@@/${FITCONF_FDT_OVERLAYS}/' \
        -e "s/@@BANK_WORD@@/${BANK_WORD_INTERNAL}/" \
        -e 's/@@FUSE_NUM@@/${FUSE_NUM}/' \
        -e 's/@@STATUS_CMD@@/${STATUS_CMD}/' \
        -e 's/@@CLOSE_CMD@@/${CLOSE_CMD}/' \
        ${WORKDIR}/uEnv.txt.in > ${WORKDIR}/uEnv.txt.temp

    vars_to_del="${UENV_VARS_TO_DEL}"
    if [ -z "${vars_to_del}" ]; then
        vars_to_del="$(cat ${WORKDIR}/uEnv.txt.temp | \
                       sed -n -e 's/^\([A-Za-z_][A-Za-z0-9_]*\)=.*/\1/gp')"
        # Needed to remove newlines from previous output
        vars_to_del=$(echo ${vars_to_del})
    fi
    vars_to_del="${vars_to_del} ${UENV_VARS_TO_DEL_EXTRA}"

    sed -e "s/@@VARS_TO_DEL@@/${vars_to_del}/" \
        ${WORKDIR}/uEnv.txt.temp > uEnv.txt
}

do_install () {
    install -d ${D}${libdir}/ostree-boot
    install -m 0644 uEnv.txt ${D}${libdir}/ostree-boot/
}

PACKAGES = "ostree-uboot-env"
FILES:ostree-uboot-env = "${libdir}/ostree-boot/uEnv.txt"
