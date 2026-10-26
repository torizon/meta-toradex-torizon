SUMMARY = "Default aktualizr-torizon secondaries"
DESCRIPTION = "Configuration files to enable the secondaries pre-bundled with Torizon OS by default"
SECTION = "base"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad"

inherit allarch

require recipes-bsp/u-boot/u-boot-fusing.inc

SRC_URI = " \
            file://50-secondaries.toml \
            file://secondaries.json \
            file://bl_actions.sh \
            file://common_actions.sh \
            file://fuse_actions-in.sh \
            file://dummy_actions.sh \
            "

RDEPENDS:${PN} += "bash coreutils jq util-linux mmc-utils sed u-boot-fw-utils"
RDEPENDS:${PN}:remove:genericx86-64 = "u-boot-fw-utils"
RDEPENDS:${PN}:remove:intel-x86-common = "u-boot-fw-utils"
DEPENDS = "jq-native"

# Which secondaries the device registers ("1") or not ("0"). A secondary a device has registered
# must stay registered in later images: Torizon Cloud rejects an ECU that disappears and returns.
TORIZON_SECONDARY_DOCKER_COMPOSE ?= "1"
TORIZON_SECONDARY_BOOTLOADER ?= "${TORIZON_SECONDARY_BOOTLOADER_DEFAULT}"
TORIZON_SECONDARY_FUSES ?= "${TORIZON_SECONDARY_FUSES_DEFAULT}"
# Per-machine defaults: the secondaries every machine registers today.
TORIZON_SECONDARY_BOOTLOADER_DEFAULT ??= "1"
TORIZON_SECONDARY_FUSES_DEFAULT ??= "${@'1' if 'imx-generic-bsp' in \
                                  d.getVar('MACHINEOVERRIDES').split(':') else '0'}"

# Check if bootloader update is supported. Where it is not, the bootloader ECU stays, with a
# handler that refuses updates (see FUSE_ACTION_HANDLER).
BL_UPDATE_SUPPORT ?= "1"
BL_UPDATE_SUPPORT:intel-x86-common ?= "0"
BL_UPDATE_SUPPORT:genericx86-64 ?= "0"
BOOTLOADER_ACTION_HANDLER = "${@'bl_actions.sh' \
    if d.getVar('BL_UPDATE_SUPPORT') == '1' else 'dummy_actions.sh'}"

# Where uEnv.txt cannot program fuses (unsigned builds, SoCs absent from the fusing table) the
# fuses ECU stays, with a handler that refuses updates: Torizon Cloud rejects a returning ECU.
FUSE_ACTION_HANDLER = "${@'fuse_actions.sh' \
    if d.getVar('FUSE_SUPPORT') == '1' and d.getVar('FUSE_SUPPORT_TYPE') == 'NXP' \
    else 'dummy_actions.sh'}"

do_install:append () {
    install -m 0700 -d ${D}${libdir}/sota/conf.d
    install -m 0644 ${WORKDIR}/50-secondaries.toml ${D}${libdir}/sota/conf.d/50-secondaries.toml

    # Drop the secondaries this image does not register. Entries this recipe does not know (a
    # layer's own secondaries.json may add some) pass through.
    keep=""
    drop=""
    if [ "${TORIZON_SECONDARY_DOCKER_COMPOSE}" = "1" ]; then
        keep="$keep docker-compose=TORIZON_SECONDARY_DOCKER_COMPOSE"
    else
        drop="$drop docker-compose"
    fi
    if [ "${TORIZON_SECONDARY_BOOTLOADER}" = "1" ]; then
        keep="$keep ${MACHINE}-bootloader=TORIZON_SECONDARY_BOOTLOADER"
        install -d ${D}${bindir}
        install -m 0744 ${WORKDIR}/${BOOTLOADER_ACTION_HANDLER} \
            ${D}${bindir}/${BOOTLOADER_ACTION_HANDLER}
        if [ "${BOOTLOADER_ACTION_HANDLER}" = "bl_actions.sh" ]; then
            install -m 0644 ${WORKDIR}/common_actions.sh ${D}${bindir}/common_actions.sh
        else
            # The ECU keeps its handler path: copies of secondaries.json, as in /etc/sota, name it.
            ln -s ${BOOTLOADER_ACTION_HANDLER} ${D}${bindir}/bl_actions.sh
        fi
    else
        drop="$drop ${MACHINE}-bootloader"
    fi
    if [ "${TORIZON_SECONDARY_FUSES}" = "1" ]; then
        keep="$keep ${MACHINE}-fuses=TORIZON_SECONDARY_FUSES"
        install -d ${D}${bindir}
        if [ "${FUSE_ACTION_HANDLER}" = "fuse_actions.sh" ]; then
            sed -e 's/@@MACHINE@@/${MACHINE}/' \
                ${WORKDIR}/fuse_actions-in.sh > ${WORKDIR}/fuse_actions.sh
            install -m 0644 ${WORKDIR}/common_actions.sh ${D}${bindir}/common_actions.sh
        fi
        install -m 0744 ${WORKDIR}/${FUSE_ACTION_HANDLER} ${D}${bindir}/${FUSE_ACTION_HANDLER}
        if [ "${FUSE_ACTION_HANDLER}" != "fuse_actions.sh" ]; then
            ln -s ${FUSE_ACTION_HANDLER} ${D}${bindir}/fuse_actions.sh
        fi
    else
        drop="$drop ${MACHINE}-fuses"
    fi

    # Only list sections hold ECU entries; others, such as aktualizr's "IP", pass through.
    sed -e "s/@@MACHINE@@/${MACHINE}/g" ${WORKDIR}/secondaries.json |
        jq --arg drop "$drop" \
           'map_values(if type == "array"
                       then map(select(.ecu_hardware_id as $id
                                       | ($drop | split(" ") | index($id)) == null))
                       else . end)
            | with_entries(select(.value != []))' > ${WORKDIR}/secondaries-kept.json

    # A secondaries.json from another layer may lack an entry: the device would drop that ECU.
    for entry in $keep; do
        id=${entry%%=*}
        n=$(jq --arg id "$id" \
               '[.[] | arrays | .[] | objects | select(.ecu_hardware_id == $id)] | length' \
               ${WORKDIR}/secondaries-kept.json)
        if [ "$n" != "1" ]; then
            if [ "$id" = "${MACHINE}-fuses" ]; then
                hint=". On i.MX, images built before this change added that entry themselves, even"
                hint="$hint to a layer's own file, so devices have registered it."
            else
                hint=", or, if devices have never registered this secondary, set"
                hint="$hint ${entry#*=} = \"0\"."
            fi
            bbfatal "secondaries.json lists the $id secondary $n times, but this image" \
                "registers it, so it must be listed exactly once. If a layer provides its own" \
                "secondaries.json, copy the entry from the one in meta-toradex-torizon$hint"
        fi
    done
    install -m 0644 ${WORKDIR}/secondaries-kept.json ${D}${libdir}/sota/secondaries.json
}

FILES:${PN} = " \
                ${libdir}/sota/conf.d/50-secondaries.toml \
                ${libdir}/sota/secondaries.json \
                ${bindir}/bl_actions.sh \
                ${bindir}/common_actions.sh \
                ${bindir}/fuse_actions.sh \
                ${bindir}/dummy_actions.sh \
                "
