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
LIC_FILES_CHKSUM = "file://LICENSE;md5=2090e7d93df7ad5a3d41f6fb4226ac76"

PV = "2.66.0+git${SRCPV}"
SRC_URI = "git://github.com/fastfetch-cli/fastfetch.git;protocol=https;branch=master"

SRCREV = "08698098579bb8b043b0a343159b8018f5cea4fc"

inherit cmake

do_install(){
    install -d ${D}${bindir}
    install -m 0755 ${B}/fastfetch ${D}${bindir}
}
