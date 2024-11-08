DESCRIPTION = "A fast and low-memory footprint OCI Container Runtime fully written in C."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
PRIORITY = "optional"

SRCREV_crun = "4b75c7cb574d642481c9346eb3d54e91c33ebaf2"
SRCREV_libocispec = "68211ccc41201c45ad276b04c7f67d61e80b1f7a"
SRCREV_ispec = "da92727e9c8761ec087890466b8756b755aefd37"
SRCREV_rspec = "2d3f72ecad9e97c898e1eb04b899a51241f1cabd"
SRCREV_yajl = "f344d21280c3e4094919fd318bc5ce75da91fc06"

SRCREV_FORMAT = "crun_rspec"
SRC_URI = "git://github.com/containers/crun.git;branch=main;name=crun;protocol=https \
           git://github.com/containers/libocispec.git;branch=main;name=libocispec;destsuffix=git/libocispec;protocol=https \
           git://github.com/opencontainers/runtime-spec.git;branch=main;name=rspec;destsuffix=git/libocispec/runtime-spec;protocol=https \
           git://github.com/opencontainers/image-spec.git;branch=main;name=ispec;destsuffix=git/libocispec/image-spec;protocol=https \
           git://github.com/containers/yajl.git;branch=main;name=yajl;destsuffix=git/libocispec/yajl;protocol=https \
          "

PV = "v1.17+git${SRCREV_crun}"
S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

PACKAGECONFIG ??= " \
    caps external-yajl man \
    ${@bb.utils.contains('DISTRO_FEATURES', 'seccomp', 'seccomp', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
"

PACKAGECONFIG[caps] = "--enable-caps,--disable-caps,libcap"
PACKAGECONFIG[external-yajl] = "--disable-embedded-yajl,--enable-embedded-yajl,yajl"
# whether to regenerate manpages that are already present in the repo
PACKAGECONFIG[man] = ",,go-md2man-native"
PACKAGECONFIG[seccomp] = "--enable-seccomp,--disable-seccomp,libseccomp"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"

DEPENDS = "m4-native"
DEPENDS:append:libc-musl = " argp-standalone"

do_configure:prepend () {
    # extracted from autogen.sh in crun source. This avoids
    # git submodule fetching.
    mkdir -p m4
    autoreconf -fi
}

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}