require recipes-bsp/u-boot/u-boot-rollback.inc

# Patching build.sh to fix bash dependency on sh environment
# where build.sh is executed with /bin/sh which may not support all bash features
# and causes build failures on some systems.

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://0001-uboot-build.sh-change-build-script-to-accept-extra-c.patch;patchdir=./build \
    file://extra-uboot-config.cfg \
"

do_compile:prepend () {
    export extra_uboot_config=${WORKDIR}/extra-uboot-config.cfg
}
