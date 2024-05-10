SUMMARY="Fastfetch is a command-line system information tool"

DESCRIPTION="Fastfetch is a tool that displays system information, in the terminal, \
in an aesthetic way, with TorizonCore logo and colors."

SECTION="console/utils"

HOMEPAGE="https://github.com/fastfetch-cli/fastfetch"

LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=98a1943cfec0523326f067e7e6b48af8"

FILES:${PN} += "${datadir}/*"
FILES:${PN} += "${bindir}/*"

SRC_URI="\
	git://github.com/fastfetch-cli/fastfetch;protocol=https;branch=master \
"

S = "${WORKDIR}/git"

SRCREV="da0db042fa193e555b7ca8aa70c8962018843f0f"
PV = "2.11.5+git${SRCPV}"

inherit cmake

