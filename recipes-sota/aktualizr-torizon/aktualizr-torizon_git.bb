SUMMARY = "Toradex implementation of the Aktualizr SOTA client"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

GARAGE_SIGN_PV = "0.7.4-25-g7cfca74"
SRC_URI[garagesign.md5sum] = "584cd16aa7824e34b593dae63796466b"
SRC_URI[garagesign.sha256sum] = "c7d5fdceef3e815363e3aa398c38643ca213f9b7f66d50f55c76a66cb74565d2"

SRC_URI = " \
  gitsm://github.com/toradex/aktualizr.git;protocol=https;branch=toradex-kirkstone-6.x.y \
  file://aktualizr-torizon.service \
  file://gateway.url \
  file://root.crt \
  https://tuf-cli-releases.ota.here.com/cli-${GARAGE_SIGN_PV}.tgz;unpack=0;name=garagesign \
  file://aktualizr-tmpfiles.conf \
"

SRCREV = "9444e804662e7c6e38e5f615f3e368f4a78d1fdb"
SRCREV:use-head-next = "${AUTOREV}"

S = "${WORKDIR}/git"

PV = "1.0+git${SRCPV}"

DEPENDS = "boost curl openssl libarchive libsodium sqlite3 asn1c-native ostree"
RDEPENDS:${PN}:class-target = "aktualizr-hwid lshw bash aktualizr-default-sec aktualizr-polling-interval aktualizr-reboot greenboot"

inherit cmake pkgconfig systemd python3native

SYSTEMD_SERVICE:${PN} = "aktualizr-torizon.service"

# For find_package(Git)
OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

PACKAGECONFIG ?= "ostree ${@bb.utils.filter('SOTA_CLIENT_FEATURES', 'hsm serialcan ubootenv', d)}"
PACKAGECONFIG[warning-as-error] = "-DWARNING_AS_ERROR=ON,-DWARNING_AS_ERROR=OFF,"
PACKAGECONFIG[ostree] = "-DBUILD_OSTREE=ON,-DBUILD_OSTREE=OFF,ostree,"
PACKAGECONFIG[ubootenv] = ",,u-boot-fw-utils,u-boot-fw-utils"
PACKAGECONFIG:remove:class-native = "ubootenv"
PACKAGECONFIG:class-native = "sota-tools"
PACKAGECONFIG[sota-tools] = "\
  -DBUILD_SOTA_TOOLS=ON -DGARAGE_SIGN_ARCHIVE=${WORKDIR}/cli-${GARAGE_SIGN_PV}.tgz, \
  -DBUILD_SOTA_TOOLS=OFF, \
  glib-2.0 python3-native python3-requests-native, \
"

PROVIDES += "aktualizr"
RPROVIDES:${PN} += "aktualizr aktualizr-info aktualizr-shared-prov"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/aktualizr-torizon.service ${D}${systemd_unitdir}/system/aktualizr-torizon.service

    install -m 0700 -d ${D}${libdir}/sota/conf.d 
    install -m 0644 ${S}/config/sota-device-cred.toml ${D}/${libdir}/sota/conf.d/20-sota-device-cred.toml
    install -m 0644 ${S}/config/sota-uboot-env.toml ${D}/${libdir}/sota/conf.d/30-rollback.toml
    
    install -m 0644 ${WORKDIR}/gateway.url ${D}/${libdir}/sota/gateway.url
    install -m 0644 ${WORKDIR}/root.crt ${D}/${libdir}/sota/root.crt

    install -d ${D}${nonarch_libdir}/tmpfiles.d
    install -m 0644 ${WORKDIR}/aktualizr-tmpfiles.conf ${D}${nonarch_libdir}/tmpfiles.d/aktualizr.conf
}

PACKAGES =+ "${PN}-misc"

FILES:${PN} += " \
  ${bindir}/aktualizr-torizon \
  ${libdir}/libaktualizr.so \
  ${systemd_unitdir}/system/aktualizr-torizon.service \
  ${sysconfdir}/sota/* \
  ${libdir}/sota/* \
  ${libdir}/sota/conf.d \
  ${libdir}/sota/conf.d/20-sota-device-cred.toml \
  ${libdir}/sota/conf.d/30-rollback.toml \
  ${bindir}/aktualizr-info \
  ${binddir}/aktualizr-cert-provider \
  ${nonarch_libdir}/tmpfiles.d/aktualizr.conf \
"

FILES:${PN}-dev = " \
  ${libdir}/libsota_tools.so \
  ${includedir}/libaktualizr \
  ${libdir}/pkgconfig \
"

FILES:${PN}-misc = " \
  ${bindir}/aktualizr-secondary \ 
  ${libdir}/libaktualizr_secondary.so \
  ${libdir}/libsota_tools.so \
  ${bindir}/aktualizr-get \
  ${bindir}/uptane-generator \
"

BBCLASSEXTEND = "native"
