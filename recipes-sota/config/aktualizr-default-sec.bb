SUMMARY = "Default aktualizr-torizon secondaries"
DESCRIPTION = "Configuration files to enable the secondaries pre-bundled with Torizon OS by default"
SECTION = "base"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad"

inherit allarch

SRC_URI = " \
            file://50-secondaries.toml \
            file://secondaries.json \
            file://bl_actions.sh \
            file://common_actions.sh \
            "
SRC_URI:append:imx-generic-bsp = " \
                                   file://fuse_actions-in.sh \
                                   "

RDEPENDS:${PN} += "bash coreutils jq util-linux mmc-utils sed u-boot-fw-utils"
RDEPENDS:${PN}:remove:genericx86-64 = "u-boot-fw-utils"
RDEPENDS:${PN}:remove:intel-x86-common = "u-boot-fw-utils"
DEPENDS:imx-generic-bsp = "jq-native"

do_install:append () {
    install -m 0700 -d ${D}${libdir}/sota/conf.d
    install -m 0644 ${WORKDIR}/50-secondaries.toml ${D}${libdir}/sota/conf.d/50-secondaries.toml
    install -m 0644 ${WORKDIR}/secondaries.json ${D}${libdir}/sota/secondaries.json
    sed -i "s/@@MACHINE@@/${MACHINE}/g" ${D}${libdir}/sota/secondaries.json

    install -d ${D}${bindir}
    install -m 0744 ${WORKDIR}/bl_actions.sh ${D}${bindir}/bl_actions.sh
    install -m 0644 ${WORKDIR}/common_actions.sh ${D}${bindir}/common_actions.sh
}

do_install:append:imx-generic-bsp () {
    sed -e 's/@@MACHINE@@/${MACHINE}/' \
        ${WORKDIR}/fuse_actions-in.sh > ${WORKDIR}/fuse_actions.sh
    install -m 0744 ${WORKDIR}/fuse_actions.sh ${D}${bindir}/fuse_actions.sh

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
                "
FILES:${PN}:append:imx-generic-bsp = " \
                                ${bindir}/fuse_actions.sh \
                                "
