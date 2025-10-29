# Patching preboot/build.sh, preboot/lib/scripts/run-stages.bashrc and
#preboot/lib/scripts/stage3/build.bashrc where those scripts are executed
# with /bin/sh which may not support all bash features and causes build
# failures on some systems.

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:winglet = " \
    file://ch0.tar.gz \
"

do_compile:prepend:winglet() {
    cp -r ${WORKDIR}/ch0 ${S}/boot/preboot/prebuilts/bcm_ree/dolphin/A0/generic/hwinit/lpddr4x/4GB/3733/
}
