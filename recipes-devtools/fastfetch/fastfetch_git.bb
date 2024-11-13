SUMMARY = "Like neofetch, but much faster because written mostly in C."

DESCRIPTION = "\
    Fastfetch is a neofetch-like tool for fetching system information and \
    displaying them in a pretty way. It is written mainly in C, with \
    performance and customizability in mind. Currently, Linux, Android, \
    FreeBSD, MacOS and Windows 7+ are supported. \
"

HOMEPAGE = "https://github.com/fastfetch-cli/fastfetch"
BUGTRACKER = "https://github.com/fastfetch-cli/fastfetch/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=23e30b4a719e5645c9663669760d6601"

PV = "2.29.0+git${SRCPV}"
SRC_URI = "git://github.com/fastfetch-cli/fastfetch.git;protocol=https;branch=master"

SRCREV = "9be70a73d3315c3ce1772700585af17923a4b953"

S = "${WORKDIR}/git"

inherit cmake

do_install(){
    install -d ${D}${bindir}
    install -m 0755 ${B}/fastfetch ${D}${bindir}
}
