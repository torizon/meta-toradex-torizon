SUMMARY = "Remote access client (RAC) for Torizon OS"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

inherit cargo cargo-update-recipe-crates systemd

# Main source respository
SRC_URI = " \
    git://github.com/torizon/rac.git;protocol=https;branch=main;name=rac \
    git://github.com/toradex/tough.git;protocol=https;branch=rac;name=tough;destsuffix=tough \
    file://remote-access.service \
    file://client.toml \
"

SRCREV_FORMAT = "rac_tough"

SRCREV_rac = "c54d1d48c071db549ca8119cfd1f60f7c1c28127"
SRCREV_tough = "69e51d241b950951907b439a9996692967a9e82b"

# Track the latest revisions on head-next builds.
SRCREV_rac:use-head-next = "${AUTOREV}"
SRCREV_tough:use-head-next = "${AUTOREV}"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} = "remote-access.service"

PV = "0.0+git${SRCPV}"

require ${BPN}-crates.inc

# Use --offline rather than --frozen mode with "cargo build".
#
# Due to the changes we needed to do in RAC Yocto recipe in order to use our toradex/tough registry,
# the tough repository got sort of aliased, so the upstream Git URL was in fact referencing our
# toradex/tough fork. Since this is a hack on top of Cargo, it got somewhat lost while parsing
# Cargo.lock and thought that the repository there needed to change to the upstream one to reflect
# the state of things.
#
CARGO_BUILD_FLAGS:remove = "--frozen"
CARGO_BUILD_FLAGS:append = " --offline"

# Remove -fcanon-prefix-map from CFLAGS since it's not supported by all GCC versions.
# The functionality is already provided by -fdebug-prefix-map and -fmacro-prefix-map.
CFLAGS:remove = "-fcanon-prefix-map"

cargo_add_rac_patch_paths() {
    if [ -f "${CARGO_HOME}/config.toml" ]; then
        cargo_cfg="${CARGO_HOME}/config.toml"
    elif [ -f "${CARGO_HOME}/config" ]; then
        cargo_cfg="${CARGO_HOME}/config"
    else
        bbfatal "Neither ${CARGO_HOME}/config.toml nor ${CARGO_HOME}/config found. Ensure cargo configuration is properly initialized before this step."
    fi
    # Need to fix config file due to the tough repo having a virtual manifest.
    # Which is not supported by the cargo bbclasses currently,
    # see: https://github.com/openembedded/openembedded-core/commit/684a8af41c5bb70db68e75f72bdc4c9b09630810
    sed -i 's|tough =.*|tough = { path = "${WORKDIR}/tough/tough" }|g' "$cargo_cfg"
    sed -i '/olpc-cjson =.*/d' "$cargo_cfg"
    sed -i '/^tough =.*/a olpc-cjson = { path = "${WORKDIR}/tough/olpc-cjson" }' "$cargo_cfg"
}

do_configure[postfuncs] += "cargo_add_rac_patch_paths"

RUNRAC_FOLDER ??= "/home/torizon/run/rac"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/remote-access.service ${D}${systemd_unitdir}/system/remote-access.service
    sed -i "s|@@RUNRAC_FOLDER@@|${RUNRAC_FOLDER}|g" \
        ${D}${systemd_unitdir}/system/remote-access.service

    install -d ${D}${sysconfdir}/rac
    install -m 0644 ${WORKDIR}/client.toml ${D}${sysconfdir}/rac
    sed -i "s|@@RUNRAC_FOLDER@@|${RUNRAC_FOLDER}|g" \
        ${D}${sysconfdir}/rac/client.toml

    install -d ${D}${datadir}/dbus-1/system.d
    install -m 0644 ${S}/dbus/io.torizon.Rac1.conf ${D}${datadir}/dbus-1/system.d/io.torizon.Rac1.conf
}

FILES:${PN} += "${datadir}/dbus-1/system.d"
