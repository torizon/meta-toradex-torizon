FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://plymouth \
    file://ostree \
    file://kmod \
    file://0001-Mount-run-with-tmpfs.patch \
    file://0002-only-scan-for-block-devices.patch \
"

SRC_URI:append:cfs-support = "\
    file://composefs \
    file://80-composefs.conf \
"

PACKAGES:append = " \
    initramfs-module-plymouth \
    initramfs-module-ostree \
    initramfs-module-kmod \
"

PACKAGES:append:cfs-support = "\
    initramfs-module-composefs \
"

SUMMARY:initramfs-module-plymouth = "initramfs support for plymouth"
RDEPENDS:initramfs-module-plymouth = "${PN}-base plymouth ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-udev-rules', '', d)}"
FILES:initramfs-module-plymouth = "/init.d/02-plymouth"

SUMMARY:initramfs-module-ostree = "initramfs support for ostree based filesystems"
RDEPENDS:initramfs-module-ostree = "${PN}-base ostree-switchroot"
FILES:initramfs-module-ostree = "/init.d/95-ostree"

SUMMARY:initramfs-module-composefs = "initramfs support for booting composefs images"
RDEPENDS:initramfs-module-composefs = "${PN}-base kernel-module-erofs kernel-module-overlay"
FILES:initramfs-module-composefs = "\
    /init.d/94-composefs \
    ${nonarch_libdir}/ostree/prepare-root.conf \
"

SUMMARY:initramfs-module-kmod = "initramfs support for loading kernel modules"
RDEPENDS:initramfs-module-kmod = "${PN}-base"
FILES:initramfs-module-kmod = "\
    /init.d/01-kmod \
    /etc/modules-load.d/* \
"

do_install:append() {
    install -m 0755 ${WORKDIR}/plymouth ${D}/init.d/02-plymouth
    install -m 0755 ${WORKDIR}/ostree ${D}/init.d/95-ostree
    install -m 0755 ${WORKDIR}/kmod ${D}/init.d/01-kmod
}

require recipes-extended/ostree/ostree-prepare-root.inc

do_install:append:cfs-support() {
    # Bundled into initramfs-module-kmod package:
    install -d ${D}/etc/modules-load.d/
    install -m 0755 ${WORKDIR}/80-composefs.conf ${D}/etc/modules-load.d/80-composefs.conf

    # Bundled into initramfs-module-composefs package:
    install -m 0755 ${WORKDIR}/composefs ${D}/init.d/94-composefs
    install -d ${D}${nonarch_libdir}/ostree/
    install -m 0644 /dev/null ${D}${nonarch_libdir}/ostree/prepare-root.conf
    write_prepare_root_config ${D}${nonarch_libdir}/ostree/prepare-root.conf
}

# Adding modules so plymouth can show the splash screen during boot
SRC_URI:append:mx8-nxp-bsp = " file://50-imx8-graphics.conf"
RDEPENDS:initramfs-module-kmod:append:mx8-nxp-bsp = " \
    kernel-module-display-connector \
    kernel-module-lontium-lt8912b \
    kernel-module-sec-dsim \
    kernel-module-sec-mipi-dsim-imx \
    kernel-module-ti-sn65dsi83 \
"

do_install:append:mx8-nxp-bsp() {
    install -d ${D}/etc/modules-load.d/
    install -m 0755 ${WORKDIR}/50-imx8-graphics.conf ${D}/etc/modules-load.d/50-imx8-graphics.conf
}

SRC_URI:append:ti-soc = " file://50-am62-graphics.conf"
RDEPENDS:initramfs-module-kmod:append:ti-soc = " \
    kernel-module-pwm-tiehrpwm \
    kernel-module-fb-sys-fops \
    kernel-module-sysimgblt \
    kernel-module-sysfillrect \
    kernel-module-syscopyarea \
    kernel-module-drm-kms-helper \
    kernel-module-drm-dma-helper \
    kernel-module-tidss \
    kernel-module-display-connector \
    kernel-module-tc358768 \
    kernel-module-ti-sn65dsi83 \
    kernel-module-lontium-lt8912b \
"

do_install:append:ti-soc() {
    install -d ${D}/etc/modules-load.d/
    install -m 0755 ${WORKDIR}/50-am62-graphics.conf ${D}/etc/modules-load.d/50-am62-graphics.conf
}
