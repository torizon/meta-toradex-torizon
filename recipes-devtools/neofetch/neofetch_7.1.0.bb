SUMMARY="Neofetch is a command-line system information tool"

DESCRIPTION="Neofetch is a tool that displays system information, in the terminal, \
in an aesthetic way, with TorizonCore logo and colors."

SECTION="console/utils"

HOMEPAGE="https://github.com/dylanaraps/neofetch"

LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=d300b86297c170b6498705fbb6794e3f"

SRC_URI="\
	git://github.com/dylanaraps/neofetch.git;protocol=https;branch=master \
	file://0001-Add-TorizonCore-OS.patch \
	file://0001-neofetch-add-info-func-for-container-engine.patch \
"

S = "${WORKDIR}/git"

SRCREV="60d07dee6b76769d8c487a40639fb7b5a1a7bc85"
PV = "7.1.0+git${SRCPV}"

CLEANBROKEN = "1"

do_install(){	
	install -d ${D}${bindir}
	install -m 0755 ${S}/neofetch ${D}${bindir}
}
