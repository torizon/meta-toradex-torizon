HOMEPAGE = "https://podman.io/"
SUMMARY =  "A daemonless container engine"
DESCRIPTION = "Podman is a daemonless container engine for developing, \
    managing, and running OCI Containers on your Linux System. Containers can \
    either be run as root or in rootless mode. Simply put: \
    `alias docker=podman`. \
    "

inherit features_check
REQUIRED_DISTRO_FEATURES ?= "seccomp ipv6"

DEPENDS = " \
    go-metalinter-native \
    gpgme \
    libseccomp \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    gettext-native \
"

SRCREV = "1d51e91436c6cdf257ff26f38b5c941a51bb12d1"
SRC_URI = " \
    git://github.com/containers/libpod.git;branch=v4.9;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX} \
    ${@bb.utils.contains('PACKAGECONFIG', 'rootless', 'file://50-podman-rootless.conf', '', d)} \
    file://run-ptest \
"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=3d9b931fa23ab1cacd0087f9e2ee12c0"

GO_IMPORT = "import"

S = "${WORKDIR}/git"

PV = "4.9.5+git"

CVE_STATUS[CVE-2022-2989] = "fixed-version: fixed since v4.3.0"
CVE_STATUS[CVE-2023-0778] = "fixed-version: fixed since v4.5.0"
CVE_STATUE[CVE-2023-48795] = "fixed-version: fixed since v4.8.3"
CVE_STATUE[CVE-2024-23651] = "fixed-version: fixed since v4.9.2"
CVE_STATUS[CVE-2024-23652] = "fixed-version: fixed since v4.9.2"
CVE_STATUS[CVE-2024-23653] = "fixed-version: fixed since v4.9.2"
CVE_STATUE[CVE-2024-1753] = "fixed-version: fixed since v4.9.4"

PACKAGES =+ "${PN}-contrib"

PODMAN_PKG = "github.com/containers/libpod"
BUILDTAGS ?= "seccomp varlink \
${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
exclude_graphdriver_btrfs exclude_graphdriver_devicemapper"

# overide LDFLAGS to allow podman to build without: "flag provided but not # defined: -Wl,-O1
export LDFLAGS=""

# https://github.com/llvm/llvm-project/issues/53999
TOOLCHAIN = "gcc"

# podmans Makefile expects BUILDFLAGS to be set but go.bbclass defines them in GOBUILDFLAGS
export BUILDFLAGS="${GOBUILDFLAGS}"

inherit go goarch
inherit systemd pkgconfig ptest

do_configure[noexec] = "1"

EXTRA_OEMAKE = " \
     PREFIX=${prefix} BINDIR=${bindir} LIBEXECDIR=${libexecdir} \
     ETCDIR=${sysconfdir} TMPFILESDIR=${nonarch_libdir}/tmpfiles.d \
     SYSTEMDDIR=${systemd_unitdir}/system USERSYSTEMDDIR=${systemd_unitdir}/user \
"

# remove 'docker' from the packageconfig if you don't want podman to
# build and install the docker wrapper. If docker is enabled in the
# packageconfig, the podman package will rconfict with docker.
PACKAGECONFIG ?= "docker"

do_compile() {
	cd ${S}/src
	rm -rf .gopath
	mkdir -p .gopath/src/"$(dirname "${PODMAN_PKG}")"
	ln -sf ../../../../import/ .gopath/src/"${PODMAN_PKG}"

	ln -sf "../../../import/vendor/github.com/varlink/" ".gopath/src/github.com/varlink"

	export GOARCH="${BUILD_GOARCH}"
	export GOPATH="${S}/src/.gopath"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

	cd ${S}/src/.gopath/src/"${PODMAN_PKG}"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export GOARCH=${TARGET_GOARCH}
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

	# podman now builds go-md2man and requires the host/build details
	export NATIVE_GOOS=${BUILD_GOOS}
	export NATIVE_GOARCH=${BUILD_GOARCH}

	oe_runmake NATIVE_GOOS=${BUILD_GOOS} NATIVE_GOARCH=${BUILD_GOARCH} BUILDTAGS="${BUILDTAGS}"
}

do_install() {
	cd ${S}/src/.gopath/src/"${PODMAN_PKG}"

	export GOARCH="${BUILD_GOARCH}"
	export GOPATH="${S}/src/.gopath"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

	oe_runmake install DESTDIR="${D}"
	if ${@bb.utils.contains('PACKAGECONFIG', 'docker', 'true', 'false', d)}; then
		oe_runmake install.docker DESTDIR="${D}"
	fi

	# Silence docker emulation warnings.
	mkdir -p ${D}/etc/containers
	touch ${D}/etc/containers/nodocker

	if ${@bb.utils.contains('PACKAGECONFIG', 'rootless', 'true', 'false', d)}; then
		install -d "${D}${sysconfdir}/sysctl.d"
		install -m 0644 "${WORKDIR}/50-podman-rootless.conf" "${D}${sysconfdir}/sysctl.d"
	fi
}

do_install_ptest () {
	cp ${S}/src/import/Makefile ${D}${PTEST_PATH}
	install -d ${D}${PTEST_PATH}/test
	cp -r ${S}/src/import/test/system ${D}${PTEST_PATH}/test

	# Some compatibility links for the Makefile assumptions.
	install -d ${D}${PTEST_PATH}/bin
	ln -s ${bindir}/podman ${D}${PTEST_PATH}/bin/podman
	ln -s ${bindir}/podman-remote ${D}${PTEST_PATH}/bin/podman-remote
}

FILES:${PN} += " \
    ${systemd_unitdir}/system/* \
    ${nonarch_libdir}/systemd/* \
    ${systemd_unitdir}/user/* \
    ${nonarch_libdir}/tmpfiles.d/* \
    ${datadir}/user-tmpfiles.d/* \
    ${sysconfdir}/cni \
"

SYSTEMD_SERVICE:${PN} = "podman.service podman.socket"

# The other option for this is "busybox", since meta-virt ensures
# that busybox is configured with nsenter
VIRTUAL-RUNTIME_base-utils-nsenter ?= "util-linux-nsenter"

# networking: cni, netavark
VIRTUAL-RUNTIME_container_networking ?= "cni"

VIRTUAL-RUNTIME_container_runtime ?= "virtual-runc"

COMPATIBLE_HOST = "^(?!mips).*"

RDEPENDS:${PN} += "\
	conmon ${VIRTUAL-RUNTIME_container_runtime} iptables ${VIRTUAL-RUNTIME_container_networking} skopeo ${VIRTUAL-RUNTIME_base-utils-nsenter} \
	${@bb.utils.contains('PACKAGECONFIG', 'rootless', 'fuse-overlayfs slirp4netns', '', d)} \
"
RRECOMMENDS:${PN} += "slirp4netns kernel-module-xt-masquerade kernel-module-xt-comment"
RCONFLICTS:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'docker', 'docker', '', d)}"

RDEPENDS:${PN}-ptest += " \
	bash \
	bats \
	buildah \
	catatonit \
	coreutils \
	file \
	gnupg \
	jq \
	make \
	tar \
"
