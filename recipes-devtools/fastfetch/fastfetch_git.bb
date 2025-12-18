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
LIC_FILES_CHKSUM = "file://LICENSE;md5=83d9effb45945153bc98303441b5c03f"

PV = "2.38.0+git${SRCPV}"
SRC_URI = "git://github.com/fastfetch-cli/fastfetch.git;protocol=https;branch=master"

SRCREV = "1b219a9d5c2a6f96b06b723152e43fed7b220cbf"

inherit cmake

do_install(){
    install -d ${D}${bindir}
    install -m 0755 ${B}/fastfetch ${D}${bindir}
}
