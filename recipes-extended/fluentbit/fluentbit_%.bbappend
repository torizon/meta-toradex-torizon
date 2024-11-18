FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DEPENDS += "libyaml"

SRC_URI += "\
    file://fluent-bit.service \
    file://fluent-bit.conf \
    file://emmc-health \
    file://custom-empty.conf \
"

PACKAGECONFIG = ""

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

# Remove Packageconfig default disable options, since they're causing build errors
EXTRA_OECMAKE:remove = "\
    -DFLB_ALL=No \
    -DFLB_ARROW=No \
    -DFLB_AVRO_ENCODER=No \
    -DFLB_AWS=No \
    -DFLB_AWS_ERROR_REPORTER=No \
    -DFLB_BACKTRACE=No \
    -DFLB_BINARY=No \
    -DFLB_CHUNK_TRACE=No \
    -DFLB_CONFIG_YAML=No \
    -DFLB_COVERAGE=No \
    -DFLB_CUSTOM_CALYPTIA=No \
    -DFLB_DEBUG=No \
    -DFLB_ENFORCE_ALIGNMENT=No \
    -DFLB_HTTP_CLIENT_DEBUG=No \
    -DFLB_HTTP_SERVER=No \
    -DFLB_IN_KAFKA=OFF \
    -DFLB_INOTIFY=No \
    -DFLB_IPO=no \
    -DFLB_JEMALLOC=No \
    -DFLB_METRICS=No \
    -DFLB_MINIMAL=No \
    -DFLB_MTRACE=No \
    -DFLB_PARSER=No \
    -DFLB_POSIX_TLS=No \
    -DFLB_PROXY_GO=No \
    -DFLB_RECORD_ACCESSOR=No \
    -DFLB_REGEX=No \
    -DFLB_RELEASE=No \
    -DFLB_RUN_LDCONFIG=No \
    -DFLB_SIGNV4=No \
    -DFLB_SMALL=No \
    -DFLB_SQLDB=No \
    -DFLB_STREAM_PROCESSOR=No \
    -DFLB_TESTS_RUNTIME=No \
    -DFLB_TLS=No \
    -DFLB_TRACE=No \
    -DFLB_UTF8_ENCODER=No \
    -DFLB_VALGRIND=No \
    -DFLB_WAMRC=No \
    -DFLB_WASM_STACK_PROTECT=No \
    -DFLB_WINDOWS_DEFAULTS=No \
"

SYSTEMD_SERVICE:${PN} = "fluent-bit.service"

do_install:append() {
    # fluentbit unconditionally install init scripts, let's remove them to install our own
    rm -Rf ${D}${nonarch_base_libdir} ${D}${sysconfdir}/init

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/fluent-bit.service ${D}${systemd_unitdir}/system/fluent-bit.service
    install -d ${D}${sysconfdir}/fluent-bit/
    install -m 0755 ${UNPACKDIR}/fluent-bit.conf ${D}${sysconfdir}/fluent-bit/fluent-bit.conf
    install -m 0755 ${UNPACKDIR}/emmc-health ${D}${bindir}
    install -d ${D}${sysconfdir}/fluent-bit/fluent-bit.d
    install -m 0644 ${UNPACKDIR}/custom-empty.conf ${D}${sysconfdir}/fluent-bit/fluent-bit.d
}
