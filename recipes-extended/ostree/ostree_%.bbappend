FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit bash-completion

SRC_URI:append = " \
    file://0001-update-default-grub-cfg-header.patch \
    file://0002-Add-support-for-the-fdtfile-variable-in-uEnv.txt.patch \
    file://0003-ostree-fetcher-curl-set-max-parallel-connections.patch \
    file://ostree-pending-reboot.service \
    file://ostree-pending-reboot.path \
"

# TODO: Upstream this addition.
PACKAGECONFIG[composefs] = "--with-composefs, --without-composefs"

# TODO: Upstream this addition.
do_configure:prepend() {
    cp ${S}/composefs/libcomposefs/Makefile-lib.am ${S}/composefs/libcomposefs/Makefile-lib.am.inc
}

# Disable PTEST for ostree as it requires options that are not enabled when
# building with meta-updater
PTEST_ENABLED = "0"

# gpgme is not required, and it brings GPLv3 dependencies
PACKAGECONFIG:remove = "gpgme"

# Ensure static linking is disabled because the static version of
# ostree-prepare-root can only be run as PID 1, thus not being suitable for
# running from the initial ramdisk.
PACKAGECONFIG:remove = "static"

# Build ostree with composefs support only if override "cfs-support" is set.
PACKAGECONFIG:append:cfs-support = " composefs"

# Ensure ed25519 is available for signing commits.
PACKAGECONFIG:append:cfs-signed = " ed25519-libsodium"

SYSTEMD_SERVICE:${PN} += "ostree-pending-reboot.path ostree-pending-reboot.service"

def is_ti(d):
    overrides = d.getVar('OVERRIDES')
    if not overrides:
        return False
    overrides = overrides.split(':')
    if 'ti-soc' in overrides:
        return True
    else:
        return False

def get_deps(d):
    if is_ti(d):  # TI
        return 'u-boot-toradex-ti' if d.getVar('PREFERRED_PROVIDER_u-boot') else ''
    else:  # NXP/x86 generic/QEMU
        return 'u-boot-default-script' if d.getVar('PREFERRED_PROVIDER_u-boot-default-script') else ''

def get_rdeps(d):
    if is_ti(d):  # TI
        return 'ostree-uboot-env' if d.getVar('PREFERRED_PROVIDER_u-boot') else ''
    else:  # NXP/x86 generic/QEMU
        return 'ostree-uboot-env' if d.getVar('PREFERRED_PROVIDER_u-boot-default-script') else ''

DEPENDS:append:class-target = " ${@get_deps(d)}"
RDEPENDS:${PN}:append:class-target = " ${@get_rdeps(d)}"

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/ostree-pending-reboot.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/ostree-pending-reboot.path ${D}${systemd_system_unitdir}
}

require ostree-prepare-root.inc

do_install:append:cfs-support() {
    install -m 0644 /dev/null ${D}${nonarch_libdir}/ostree/prepare-root.conf
    write_prepare_root_config ${D}${nonarch_libdir}/ostree/prepare-root.conf
}
