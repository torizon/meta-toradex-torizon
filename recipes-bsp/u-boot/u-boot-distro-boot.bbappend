FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://uEnv.txt.in \
"

# DISTRO_BOOT_PREDEF_FITCONF: String in this variable will be added by the boot script to the string
# passed to 'bootm' when booting a FIT image. This can be leveraged to force some FIT configurations
# (such as configurations representing device-tree overlays) to be used when booting.
DISTRO_BOOT_PREDEF_FITCONF ??= ""

do_compile:append () {
    bbdebug 1 "Building uEnv.txt..."
    sed -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_DTB_PREFIX@@/${DTB_PREFIX}/' \
        -e 's/@@DISTRO_BOOT_PREDEF_FITCONF@@/${DISTRO_BOOT_PREDEF_FITCONF}/' \
        ${S}/uEnv.txt.in > uEnv.txt
}

do_install () {
    install -d ${D}${libdir}/ostree-boot
    install -m 0644 uEnv.txt ${D}${libdir}/ostree-boot/
}

PACKAGES = "ostree-uboot-env"
FILES:ostree-uboot-env = "${libdir}/ostree-boot/uEnv.txt"
