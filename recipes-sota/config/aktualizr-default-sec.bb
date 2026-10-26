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
# Per-machine defaults are in conf/distro/include/torizon-secondaries.inc; a machine with none for
# the bootloader (or, on i.MX, the fuses) stops the build until one is chosen.
TORIZON_SECONDARY_DOCKER_COMPOSE ?= "1"
TORIZON_SECONDARY_BOOTLOADER ?= "${TORIZON_SECONDARY_BOOTLOADER_DEFAULT}"
TORIZON_SECONDARY_FUSES ?= "${TORIZON_SECONDARY_FUSES_DEFAULT}"
TORIZON_SECONDARY_BOOTLOADER_DEFAULT ??= ""
# Only i.MX machines have ever registered the fuses secondary.
TORIZON_SECONDARY_FUSES_DEFAULT ??= "${@'' if 'imx-generic-bsp' in \
                                  d.getVar('MACHINEOVERRIDES').split(':') else '0'}"

# Where the bootloader cannot be updated the bootloader ECU stays, with a handler that refuses
# updates (see FUSE_ACTION_HANDLER).
BOOTLOADER_ACTION_HANDLER = "bl_actions.sh"
BOOTLOADER_ACTION_HANDLER:intel-x86-common = "dummy_actions.sh"
BOOTLOADER_ACTION_HANDLER:genericx86-64 = "dummy_actions.sh"

# Where uEnv.txt cannot program fuses (unsigned builds, SoCs absent from the fusing table) the
# fuses ECU stays, with a handler that refuses updates: Torizon Cloud rejects a returning ECU.
FUSE_ACTION_HANDLER = "${@'fuse_actions.sh' \
    if d.getVar('FUSE_SUPPORT') == '1' and d.getVar('FUSE_SUPPORT_TYPE') == 'NXP' \
    else 'dummy_actions.sh'}"

python () {
    # Multiconfigs with a distro other than Torizon OS never build this recipe.
    if not bb.utils.contains('DISTRO_FEATURES', 'sota', True, False, d):
        raise bb.parse.SkipRecipe('needs the sota distro feature')

    machine = d.getVar('MACHINE')

    if d.getVar('BL_UPDATE_SUPPORT') is not None:
        bb.fatal(
            'BL_UPDATE_SUPPORT is set, but it no longer has any effect. Whether an image '
            'registers the bootloader secondary is now set by TORIZON_SECONDARY_BOOTLOADER '
            '("1" or "0"), and the handler it gets depends on the machine. Remove '
            'BL_UPDATE_SUPPORT; to keep what images have registered so far, leave '
            'TORIZON_SECONDARY_BOOTLOADER at "1" (BL_UPDATE_SUPPORT = "0" kept the bootloader '
            'secondary registered as well).')

    for var, secondary in (('TORIZON_SECONDARY_DOCKER_COMPOSE', 'docker-compose'),
                           ('TORIZON_SECONDARY_BOOTLOADER', 'bootloader'),
                           ('TORIZON_SECONDARY_FUSES', 'fuses')):
        value = d.getVar(var) or ''
        if value in ('0', '1'):
            continue
        # Firmware multiconfigs (such as TI's k3r5) parse this recipe too, for machines without
        # values, and never build it.
        if not value and d.getVar('BB_CURRENT_MC') != 'default':
            raise bb.parse.SkipRecipe('%s is not set in multiconfig %s'
                                      % (var, d.getVar('BB_CURRENT_MC')))
        state = 'not set' if not value else 'set to "%s"' % value
        bb.fatal(
            '%s is %s for MACHINE "%s". Set it to "1" to register the %s secondary, as '
            'Torizon OS has done so far, or to "0" not to, in local.conf or, with "?=", in '
            'the machine configuration. A device that has registered the secondary needs '
            '"1" in every later image: Torizon Cloud rejects an ECU that disappears and '
            'comes back.' % (var, state, machine, secondary))
}

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
