SUMMARY = "syna2img Tools"
DESCRIPTION = "Python zipapp tools for converting between raw disk images and the Synaptics Astra SYNAIMG format"
HOMEPAGE = "https://gitlab.com/toradex/rd/torizon-core/syna2img"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f8119945852ced49987c9fa5b98500d0"

PV = "1.0.0"
SRC_URI = "git://gitlab.com/toradex/rd/torizon-core/syna2img.git;protocol=https;branch=main"
SRCREV = "cdc7cdeb401fff4f93dc0c628f7ec3ec774cfce0"
SRCREV:use-head-next = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit deploy

do_configure[noexec] = "1"

do_compile() {
    cd ${S}
    python3 build.py syna2img
    python3 build.py img2syna
}

SYNAIMG_DEPLOY = "${DEPLOYDIR}/${BPN}"

do_deploy() {
    install -d ${SYNAIMG_DEPLOY}
    install -m 755 ${S}/syna2img.pyz ${SYNAIMG_DEPLOY}
    install -m 755 ${S}/img2syna.pyz ${SYNAIMG_DEPLOY}
}

addtask deploy after do_compile

COMPATIBLE_MACHINE = "(sl1680|luna-sl1680)"
