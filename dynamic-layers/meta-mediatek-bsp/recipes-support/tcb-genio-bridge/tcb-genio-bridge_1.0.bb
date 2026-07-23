SUMMARY = "Host-side torizoncore-builder bridge for genio-flash images"
DESCRIPTION = "Wrapper that applies a torizoncore-builder rootfs customisation \
to a MediaTek genio-flash (aiotflash.tar) image: it unwraps the tarball, \
unsparses the system WIC, runs torizoncore-builder's raw-image path, re-sparses \
with the build's 4096-byte block size, and repacks the tarball for genio-flash. \
Ships the wrapper plus an example tcbuild configuration."
HOMEPAGE = "https://github.com/EmcraftSystems/meta-toradex-torizon"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PV = "1.0"

SRC_URI = "file://tcb-genio-bridge \
           file://tcbuild-genio.yaml \
"

S = "${WORKDIR}"

inherit deploy

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# Host-side tool: deploy beside the flashing artifacts, not into the rootfs.
BRIDGE_DEPLOY = "${DEPLOYDIR}/${BPN}"

do_deploy() {
    install -d ${BRIDGE_DEPLOY}
    install -m 0755 ${S}/tcb-genio-bridge ${BRIDGE_DEPLOY}/tcb-genio-bridge
    install -m 0644 ${S}/tcbuild-genio.yaml ${BRIDGE_DEPLOY}/tcbuild-genio.yaml
}
addtask deploy before do_build after do_compile

COMPATIBLE_MACHINE = "lec-mtk-i1200"
