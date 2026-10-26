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
            file://dummy_actions.sh \
            "
SRC_URI:append:imx-generic-bsp = " \
                                   file://fuse_actions-in.sh \
                                   "

RDEPENDS:${PN} += "bash coreutils jq util-linux mmc-utils sed u-boot-fw-utils"
RDEPENDS:${PN}:remove:genericx86-64 = "u-boot-fw-utils"
RDEPENDS:${PN}:remove:intel-x86-common = "u-boot-fw-utils"
DEPENDS:imx-generic-bsp = "jq-native"

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
    install -m 0644 ${WORKDIR}/secondaries.json ${D}${libdir}/sota/secondaries.json
    sed -i "s/@@MACHINE@@/${MACHINE}/g" ${D}${libdir}/sota/secondaries.json

    install -d ${D}${bindir}
    install -m 0744 ${WORKDIR}/${BOOTLOADER_ACTION_HANDLER} \
        ${D}${bindir}/${BOOTLOADER_ACTION_HANDLER}
    if [ "${BOOTLOADER_ACTION_HANDLER}" = "bl_actions.sh" ]; then
        install -m 0644 ${WORKDIR}/common_actions.sh ${D}${bindir}/common_actions.sh
    else
        # The ECU keeps its handler path: copies of secondaries.json, as in /etc/sota, name it.
        ln -s ${BOOTLOADER_ACTION_HANDLER} ${D}${bindir}/bl_actions.sh
    fi
}

do_install:append:imx-generic-bsp () {
    if [ "${FUSE_ACTION_HANDLER}" = "fuse_actions.sh" ]; then
        sed -e 's/@@MACHINE@@/${MACHINE}/' \
            ${WORKDIR}/fuse_actions-in.sh > ${WORKDIR}/fuse_actions.sh
    fi
    install -d ${D}${bindir}
    install -m 0744 ${WORKDIR}/${FUSE_ACTION_HANDLER} ${D}${bindir}/${FUSE_ACTION_HANDLER}
    # The ECU keeps its handler path: copies of secondaries.json, as in /etc/sota, name it.
    if [ "${FUSE_ACTION_HANDLER}" != "fuse_actions.sh" ]; then
        ln -s ${FUSE_ACTION_HANDLER} ${D}${bindir}/fuse_actions.sh
    fi

    local machine="${MACHINE}"
    cat ${D}${libdir}/sota/secondaries.json |\
        jq '.["torizon-generic"] +=
             [{"partial_verifying": false,
               "ecu_hardware_id": "'"$machine"'-fuses",
               "full_client_dir": "/var/sota/storage/fuse",
               "ecu_private_key": "sec.private",
               "ecu_public_key": "sec.public",
               "firmware_path": "/var/sota/storage/fuse/fuse.yml",
               "target_name_path": "/var/sota/storage/fuse/target_name",
               "metadata_path": "/var/sota/storage/fuse/metadata",
               "action_handler_path": "/usr/bin/fuse_actions.sh"}]' \
        > ${WORKDIR}/temp.json

    install -m 0644 ${WORKDIR}/temp.json ${D}${libdir}/sota/secondaries.json
}

FILES:${PN} = " \
                ${libdir}/sota/conf.d/50-secondaries.toml \
                ${libdir}/sota/secondaries.json \
                ${bindir}/bl_actions.sh \
                ${bindir}/common_actions.sh \
                ${bindir}/dummy_actions.sh \
                "
FILES:${PN}:append:imx-generic-bsp = " \
                                ${bindir}/fuse_actions.sh \
                                ${bindir}/${FUSE_ACTION_HANDLER} \
                                "
