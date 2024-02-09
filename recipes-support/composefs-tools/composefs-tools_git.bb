SUMMARY = "composefs tools"
DESCRIPTION = "The composefs project combines several underlying Linux \
features to provide a very flexible mechanism to support read-only \
mountable filesystem trees, stacking on top of an underlying \"lower\" \
Linux filesystem."

# TODO: REVIEW
# NOTE: multiple licenses have been detected; they have been separated with &
# in the LICENSE value for now since it is a reasonable assumption that all
# of the licenses apply. If instead there is a choice between the multiple
# licenses then you should change the value to separate the licenses with |
# instead of &. If there is any doubt, check the accompanying documentation
# to determine which situation is applicable.
LICENSE = "GPL-2.0-only & GPL-3.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    file://tools/COPYING;md5=d32239bcb673463ab874e80d47fae504"

# TODO: Consider removing the following patches on the next upgrade.
SRC_URI = "\
    git://github.com/containers/composefs.git;protocol=https;branch=main \
    file://0001-mount-Allow-building-when-macro-MOUNT_ATTR_IDMAP-is-.patch \
    file://0002-mount-Allow-building-when-macro-LOOP_CONFIGURE-is-no.patch \
    file://0001-configure.ac-disable-Werror-unused-result-temporaril.patch \
"

SRCREV = "cca8be49843385ce556fccf51f75821f70fb7769"
PV = "0.1.4+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS = "openssl fuse3"
# NOTE: We are currently removing the dependency on fuse3 on the native build
# just to avoid having to solve a dependecy chain.
DEPENDS:remove:class-native = "fuse3"

inherit autotools pkgconfig

# The main programs are actually bash scripts (calling a binary under
# bin/.libs), so they need a runtime dependency on bash.
RDEPENDS:${PN} = "bash"

BBCLASSEXTEND = "native nativesdk"
