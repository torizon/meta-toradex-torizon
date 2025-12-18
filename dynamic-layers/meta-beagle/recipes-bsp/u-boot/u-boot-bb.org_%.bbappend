require recipes-bsp/u-boot/u-boot-rollback.inc

FILESEXTRAPATHS:prepend:beagley-ai := "${THISDIR}/beagley-ai:"

SRC_URI:append:beagley-ai = "\
    file://bootcommand.cfg \
    file://env_mmc.cfg \
"

# Reapply all *.cfg fragments in SRC_URI, as u-boot-mergeconfig.inc from meta-ti-bsp has
# apparently reseted all configs not in UBOOT_CONFIG_FRAGMENTS to their default values
do_configure:append () {
    # Copied from ${COREBASE}/meta/recipes-bsp/u-boot/u-boot-configure.inc
    merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
    cml1_do_configure
}
