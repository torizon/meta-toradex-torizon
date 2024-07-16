SUMMARY = "Fast Log processor and Forwarder"
DESCRIPTION = "\
    Fluent Bit is a data collector, processor and \
    forwarder for Linux. It supports several input sources and \
    backends (destinations) for your data. \
"

HOMEPAGE = "http://fluentbit.io"
BUGTRACKER = "https://github.com/fluent/fluent-bit/issues"
SECTION = "net"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

DEPENDS = "\
    bison-native \
    flex-native \
    libyaml \
    openssl \
    systemd \
    zlib \
"

PV = "3.1.2+git${SRCPV}"

SRC_URI = "\
           git://github.com/fluent/fluent-bit.git;protocol=https;nobranch=1 \
           file://fluent-bit.service \
           file://fluent-bit.conf \
           file://emmc-health \
           "
SRCREV = "a6feacd6e92e08c0e5290f255864e70b12301fa8"

S = "${WORKDIR}/git"

# Use CMake 'Unix Makefiles' generator
OECMAKE_GENERATOR ?= "Unix Makefiles"

# Fluent Bit build options
# ========================

# Disable Debug symbols
EXTRA_OECMAKE += "-DFLB_DEBUG=Off "

# Host related setup
EXTRA_OECMAKE += "-DGNU_HOST=${HOST_SYS} -DHOST=${HOST_ARCH} "

# Disable LuaJIT and filter_lua support
EXTRA_OECMAKE += "-DFLB_LUAJIT=Off -DFLB_FILTER_LUA=Off "

# Disable Windows/ia32 only features
EXTRA_OECMAKE += "-DFLB_WASM=Off "

# Disable Library and examples
EXTRA_OECMAKE += "-DFLB_SHARED_LIB=Off -DFLB_EXAMPLES=Off "

# Systemd support
EXTRA_OECMAKE += "-DFLB_IN_SYSTEMD=On "

# Enable Kafka Output plugin
EXTRA_OECMAKE += "-DFLB_OUT_KAFKA=On -DWITH_CURL=Off "

inherit cmake systemd pkgconfig

SYSTEMD_SERVICE:${PN} = "fluent-bit.service"

do_install:append() {
    # fluent-bit unconditionally install init scripts, let's remove them to install our own
    rm -Rf ${D}/lib ${D}/etc/init

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/fluent-bit.service ${D}${systemd_unitdir}/system/fluent-bit.service
    install -d ${D}${sysconfdir}/fluent-bit/
    install -m 0755 ${UNPACKDIR}/fluent-bit.conf ${D}${sysconfdir}/fluent-bit/fluent-bit.conf
    install -m 0755 ${UNPACKDIR}/emmc-health ${D}${bindir}
}
