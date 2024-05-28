SUMMARY = "A container network stack"
HOMEPAGE = "https://github.com/containers/netavark"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "167e2e4f167cadc635bbacb5331674b67787d08e"

# It is possible to fetch the source using the crate fetcher instead:
#SRC_URI = "crate://crates.io/${BPN}/${PV}"
SRC_URI = "git://github.com/containers/netavark.git;protocol=https;nobranch=1 \
           file://tests.patch \
           file://run-ptest"
require ${BPN}-crates.inc

S = "${WORKDIR}/git"

PACKAGECONFIG ?= "aardvark-dns"

# From the documentation of netavark
# https://github.com/containers/netavark/blob/v1.1.0/DISTRO_PACKAGE.md#dependency-on-aardvark-dns
# The aardvark-dns will be installed by default with netavark, but
# netavark will be functional without it.
PACKAGECONFIG[aardvark-dns]= ",,, aardvark-dns"

inherit cargo cargo-update-recipe-crates features_check ptest

# Cargo installs the binary to bin so move it to where podman expects it
do_install:append() {
	install -d ${D}${libexecdir}
	mv ${D}${bindir} ${D}${libexecdir}/podman
}

do_install_ptest() {
	cp -r ${S}/test ${D}${PTEST_PATH}
}

# rdepends on aardvark-dns which rdepends on slirp4netns
REQUIRED_DISTRO_FEATURES ?= "seccomp"


DEPENDS += "protobuf-c-native protobuf-c"

# bind-utils is used to install dig
# procps-ps is necessary because the ps from busybox is
# not having the same behavior
# iputils is used because busybox ping behaves differently
RDEPENDS:${PN}-ptest += " \
    bash \
    bats \
    bind-utils \
    coreutils \
    dbus-daemon-proxy \
    iproute2 \
    iputils \
    jq \
    nmap \
    procps-ps \
    util-linux-nsenter \
    util-linux-unshare \
"
