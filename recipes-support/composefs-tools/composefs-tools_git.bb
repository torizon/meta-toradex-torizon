SUMMARY = "composefs tools"
DESCRIPTION = "\
    The composefs project combines several underlying Linux \
    features to provide a very flexible mechanism to support read-only \
    mountable filesystem trees, stacking on top of an underlying \"lower\" \
    Linux filesystem. \
"

# TODO: REVIEW
# NOTE: multiple licenses have been detected; they have been separated with &
# in the LICENSE value for now since it is a reasonable assumption that all
# of the licenses apply. If instead there is a choice between the multiple
# licenses then you should change the value to separate the licenses with |
# instead of &. If there is any doubt, check the accompanying documentation
# to determine which situation is applicable.
LICENSE = "GPL-2.0-only & GPL-3.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
    file://tools/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
"

DEPENDS = "\
    fuse3 \
    openssl \
"
# NOTE: We are currently removing the dependency on fuse3 on the native build
# just to avoid having to solve a dependecy chain.
DEPENDS:remove:class-native = "fuse3"

PV = "1.0.4+git${SRCPV}"
SRC_URI = "git://github.com/containers/composefs.git;protocol=https;branch=main"

SRCREV = "7623e4dc89f62ada5724d4e41d0a16d2671312f5"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

# The main programs are actually bash scripts (calling a binary under
# bin/.libs), so they need a runtime dependency on bash.
RDEPENDS:${PN} = "bash"

BBCLASSEXTEND = "native nativesdk"
