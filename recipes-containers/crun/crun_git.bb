DESCRIPTION = "A fast and low-memory footprint OCI Container Runtime fully written in C."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
PRIORITY = "optional"

SRCREV_crun = "90b21ddd007f0306952b8b38aa66ddee58afffe4"
SRCREV_libocispec = "19c05670c37a42c217caa7b141bcaada7867cc15"
SRCREV_ispec = "9615142d016838b5dfe7453f80af0be74feb5c7c"
SRCREV_rspec = "720792f25ae6e9ee6b1332db698f37659e69ce8d"
SRCREV_yajl = "f344d21280c3e4094919fd318bc5ce75da91fc06"

SRCREV_FORMAT = "crun_rspec"
SRC_URI = "git://github.com/containers/crun.git;branch=main;name=crun;protocol=https \
           git://github.com/containers/libocispec.git;branch=main;name=libocispec;destsuffix=git/libocispec;protocol=https \
           git://github.com/opencontainers/runtime-spec.git;branch=main;name=rspec;destsuffix=git/libocispec/runtime-spec;protocol=https \
           git://github.com/opencontainers/image-spec.git;branch=main;name=ispec;destsuffix=git/libocispec/image-spec;protocol=https \
           git://github.com/containers/yajl.git;branch=main;name=yajl;destsuffix=git/libocispec/yajl;protocol=https \
          "

PV = "v1.12+git${SRCREV_crun}"
S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES ?= "systemd seccomp"

inherit autotools-brokensep pkgconfig features_check

PACKAGECONFIG ??= ""

DEPENDS = "yajl libcap go-md2man-native m4-native"
# TODO: is there a packageconfig to turn this off ?
DEPENDS += "libseccomp"
DEPENDS += "systemd"
DEPENDS += "oci-image-spec oci-runtime-spec"
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
