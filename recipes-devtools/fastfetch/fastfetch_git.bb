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
LIC_FILES_CHKSUM = "file://LICENSE;md5=98a1943cfec0523326f067e7e6b48af8"

PV = "2.16.0+git${SRCPV}"
SRC_URI = "git://github.com/fastfetch-cli/fastfetch.git;protocol=https;branch=master"

SRCREV = "d435ef6a77a1d48912294694a979b10f0c6efcc8"

S = "${WORKDIR}/git"

inherit cmake

do_install(){
    install -d ${D}${bindir}
    install -m 0755 ${B}/fastfetch ${D}${bindir}
}
