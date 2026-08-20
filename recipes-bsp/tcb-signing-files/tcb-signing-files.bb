SUMMARY = "Bootloader artifacts needed by TorizonCore Builder to re-sign an image"
DESCRIPTION = "Packs the intermediate, pre-signing bootloader artifacts that \
TorizonCore Builder needs to recreate and re-sign the bootloader of a pre-built \
Torizon OS image. This recipe aggregates output deployed by other recipes; it \
builds nothing of its own."
SECTION = "BSP"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

require recipes-bsp/tcb-signing-files/tcb-signing-files.inc

inherit deploy nopackages

PACKAGE_ARCH = "${MACHINE_ARCH}"
# Anchored: unanchored, "verdin-am62" would also match verdin-am62p and the
# k3r5 multiconfig's verdin-am62-k3r5, neither of which this recipe packs for.
COMPATIBLE_MACHINE = "^(verdin-imx8mp|verdin-imx8mm|verdin-am62)$"

# bitbake world would otherwise build this on machines where the feature is off
# and nothing ever deploys the files below.
EXCLUDE_FROM_WORLD = "${@'0' if d.getVar('TCB_SIGNING_SUPPORT') == '1' else '1'}"

INHIBIT_DEFAULT_DEPS = "1"

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_install[noexec] = "1"

B = "${WORKDIR}/build"

# DEPLOY_DIR_IMAGE is otherwise just a directory that may not have been filled
# in yet, so wait on the deploy tasks of the recipes that fill it.
do_compile[depends] += "${@' '.join('%s:do_deploy' % r for r in d.getVar('TCB_SIGNING_INPUT_DEPENDS').split())}"

# Some machines build part of their input in another multiconfig, which needs an
# edge of its own; see TCB_SIGNING_INPUT_MCDEPENDS.
do_compile[mcdepends] += "${TCB_SIGNING_INPUT_MCDEPENDS}"

# The filelist decides the archive's contents, and the enable condition decides
# whether anything downstream uses it.
do_compile[vardeps] += "TCB_SIGNING_FILELIST TCB_SIGNING_SUPPORT"

do_compile() {
    # The inputs are deployed only when the feature is on; without this the tar
    # below fails on its own with a bare "Cannot stat".
    if [ "${TCB_SIGNING_SUPPORT}" != "1" ]; then
        bbfatal "TCB_SIGNING_SUPPORT is not \"1\" for MACHINE \"${MACHINE}\", so" \
                "nothing deploys the files this recipe packs. Build it only in a" \
                "configuration that enables the signing feature."
    fi

    if [ -z "${TCB_SIGNING_FILELIST}" ]; then
        bbfatal "TCB_SIGNING_FILELIST is empty for MACHINE \"${MACHINE}\", so there" \
                "is nothing to pack. Either define it or keep this recipe out of the" \
                "build (see TCB_SIGNING_SUPPORT)."
    fi

    # Without the flags below, member mtimes, ownership and order track the
    # build, so the same sources would not produce the same archive twice.
    tar_timestamp_args=""
    if [ -n "${SOURCE_DATE_EPOCH}" ]; then
        tar_timestamp_args="--mtime=@${SOURCE_DATE_EPOCH} --clamp-mtime"
    fi

    # The shell expands the filelist's globs where the command runs, not where
    # tar reads. --format is pinned so a new tar default cannot move the bytes.
    (cd "${DEPLOY_DIR_IMAGE}" && tar \
        --dereference --hard-dereference \
        --sort=name --format=gnu \
        ${tar_timestamp_args} \
        --owner=0 --group=0 --numeric-owner \
        -czf "${B}/${TCB_SIGNING_FILES_TARBALL}" ${TCB_SIGNING_FILELIST})
}
do_compile[dirs] = "${B}"
do_compile[cleandirs] = "${B}"

do_deploy() {
    install -m 0644 "${B}/${TCB_SIGNING_FILES_TARBALL}" "${DEPLOYDIR}/${TCB_SIGNING_FILES_TARBALL}"
}
addtask deploy after do_compile before do_build
