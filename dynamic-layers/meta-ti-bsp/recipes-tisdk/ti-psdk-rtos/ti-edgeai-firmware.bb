# Vendored from meta-edgeai (git.ti.com/git/edgeai/meta-edgeai) at 9d6653bbc074e0c7466d8bd2979b81146039308e
SUMMARY = "TI RTOS prebuilt binary firmware images for EdgeAI"

LICENSE = "TI-TFL"
LIC_FILES_CHKSUM = "file://${COREBASE}/../meta-ti/meta-ti-bsp/licenses/TI-TFL;md5=a1b59cb7ba626b9dbbcbf00f3fbc438a"

COMPATIBLE_MACHINE = "j721e|j721s2|j784s4|j722s|j742s2"

PACKAGE_ARCH = "${MACHINE_ARCH}"

INHIBIT_DEFAULT_DEPS = "1"

inherit deploy
inherit update-alternatives

PLAT_SFX = ""
PLAT_SFX:j721e = "j721e"
PLAT_SFX:j721s2 = "j721s2"
PLAT_SFX:j784s4 = "j784s4"
PLAT_SFX:j722s = "j722s"
PLAT_SFX:j742s2 = "j742s2"

SRCREV = "6d4cdad9c31e3dd29e31d48c1b361d2aa74ddbd5"
BRANCH = "main"

SRC_URI = " \
    git://git.ti.com/git/processor-sdk/psdk_fw.git;protocol=https;branch=${BRANCH} \
"

S = "${WORKDIR}/git"

PV = "1.0.0"

# Secure Build
inherit ti-secdev

FW_DIR = "${PLAT_SFX}/vision_apps_eaik"

INSTALL_FW_DIR = "${nonarch_base_libdir}/firmware/vision_apps_eaik/"

MCU_1_0_FW = "vx_app_rtos_linux_mcu1_0.out"
MCU_1_1_FW = "vx_app_rtos_linux_mcu1_1.out"
MCU_2_0_FW = "vx_app_rtos_linux_mcu2_0.out"
MCU_2_1_FW = "vx_app_rtos_linux_mcu2_1.out"
MCU_3_0_FW = "vx_app_rtos_linux_mcu3_0.out"
MCU_3_1_FW = "vx_app_rtos_linux_mcu3_1.out"
MCU_4_0_FW = "vx_app_rtos_linux_mcu4_0.out"
MCU_4_1_FW = "vx_app_rtos_linux_mcu4_1.out"
C66_1_FW   = "vx_app_rtos_linux_c6x_1.out"
C66_2_FW   = "vx_app_rtos_linux_c6x_2.out"
C7X_1_FW   = "vx_app_rtos_linux_c7x_1.out"
C7X_2_FW   = "vx_app_rtos_linux_c7x_2.out"
C7X_3_FW   = "vx_app_rtos_linux_c7x_3.out"
C7X_4_FW   = "vx_app_rtos_linux_c7x_4.out"

FW_LIST = ""
FW_LIST:j721e =     "              ${MCU_2_0_FW} ${MCU_2_1_FW} ${C66_1_FW} ${C66_2_FW} ${C7X_1_FW}"
FW_LIST:j721s2 =    "              ${MCU_2_0_FW} ${MCU_2_1_FW} ${C7X_1_FW} ${C7X_2_FW}"
FW_LIST:j784s4 =    "              ${MCU_2_0_FW} ${MCU_2_1_FW} ${MCU_3_0_FW} ${MCU_3_1_FW} ${MCU_4_0_FW} ${MCU_4_1_FW}                         ${C7X_1_FW} ${C7X_2_FW} ${C7X_3_FW} ${C7X_4_FW}"
FW_LIST:j742s2 =    "                            ${MCU_2_0_FW} ${MCU_2_1_FW} ${MCU_3_0_FW} ${MCU_3_1_FW} ${MCU_4_0_FW} ${MCU_4_1_FW}                         ${C7X_1_FW} ${C7X_2_FW} ${C7X_3_FW}"
FW_LIST:j722s =     "                            ${MCU_2_0_FW}                                                                                               ${C7X_1_FW} ${C7X_2_FW}"

do_install() {
    # Sign the firmware
    for FW_NAME in ${FW_LIST}
    do
        ${TI_SECURE_DEV_PKG}/scripts/secure-binary-image.sh ${S}/${FW_DIR}/${FW_NAME} ${S}/${FW_DIR}/${FW_NAME}.signed
    done

    # Install the Firmware
    install -d ${D}${INSTALL_FW_DIR}
    for FW_NAME in ${FW_LIST}
    do
        install -m 0644 ${S}/${FW_DIR}/${FW_NAME}        ${D}${INSTALL_FW_DIR}
        install -m 0644 ${S}/${FW_DIR}/${FW_NAME}.signed ${D}${INSTALL_FW_DIR}
    done
}

do_deploy() {
}

# Set up names for the firmwares
ALTERNATIVE:${PN}:j721e = "\
                    j7-main-r5f0_0-fw \
                    j7-main-r5f0_1-fw \
                    j7-c66_0-fw \
                    j7-c66_1-fw \
                    j7-c71_0-fw\
                    j7-main-r5f0_0-fw-sec \
                    j7-main-r5f0_1-fw-sec \
                    j7-c66_0-fw-sec \
                    j7-c66_1-fw-sec \
                    j7-c71_0-fw-sec \
                    "

ALTERNATIVE:${PN}:j721s2 = "\
                    j721s2-main-r5f0_0-fw \
                    j721s2-main-r5f0_1-fw \
                    j721s2-c71_0-fw \
                    j721s2-c71_1-fw \
                    j721s2-main-r5f0_0-fw-sec \
                    j721s2-main-r5f0_1-fw-sec \
                    j721s2-c71_0-fw-sec \
                    j721s2-c71_1-fw-sec \
                    "

ALTERNATIVE:${PN}:j784s4 = "\
                    j784s4-main-r5f0_0-fw \
                    j784s4-main-r5f0_1-fw \
                    j784s4-main-r5f1_0-fw \
                    j784s4-main-r5f1_1-fw \
                    j784s4-main-r5f2_0-fw \
                    j784s4-main-r5f2_1-fw \
                    j784s4-c71_0-fw \
                    j784s4-c71_1-fw \
                    j784s4-c71_2-fw \
                    j784s4-c71_3-fw \
                    j784s4-main-r5f0_0-fw-sec \
                    j784s4-main-r5f0_1-fw-sec \
                    j784s4-main-r5f1_0-fw-sec \
                    j784s4-main-r5f1_1-fw-sec \
                    j784s4-main-r5f2_0-fw-sec \
                    j784s4-main-r5f2_1-fw-sec \
                    j784s4-c71_0-fw-sec \
                    j784s4-c71_1-fw-sec \
                    j784s4-c71_2-fw-sec \
                    j784s4-c71_3-fw-sec \
                    "

ALTERNATIVE:${PN}:j742s2 = "\
                    j742s2-main-r5f0_0-fw \
                    j742s2-main-r5f0_1-fw \
                    j742s2-main-r5f1_0-fw \
                    j742s2-main-r5f1_1-fw \
                    j742s2-main-r5f2_0-fw \
                    j742s2-main-r5f2_1-fw \
                    j742s2-c71_0-fw \
                    j742s2-c71_1-fw \
                    j742s2-c71_2-fw \
                    j742s2-main-r5f0_0-fw-sec \
                    j742s2-main-r5f0_1-fw-sec \
                    j742s2-main-r5f1_0-fw-sec \
                    j742s2-main-r5f1_1-fw-sec \
                    j742s2-main-r5f2_0-fw-sec \
                    j742s2-main-r5f2_1-fw-sec \
                    j742s2-c71_0-fw-sec \
                    j742s2-c71_1-fw-sec \
                    j742s2-c71_2-fw-sec \
                    "

ALTERNATIVE:${PN}:j722s = "\
                    j722s-main-r5f0_0-fw \
                    j722s-c71_0-fw \
                    j722s-c71_1-fw \
                    j722s-main-r5f0_0-fw-sec \
                    j722s-c71_0-fw-sec \
                    j722s-c71_1-fw-sec \
                    "

# Set up link names for the firmwares

ALTERNATIVE_LINK_NAME[j7-main-r5f0_0-fw] = "${nonarch_base_libdir}/firmware/j7-main-r5f0_0-fw"
ALTERNATIVE_LINK_NAME[j7-main-r5f0_1-fw] = "${nonarch_base_libdir}/firmware/j7-main-r5f0_1-fw"
ALTERNATIVE_LINK_NAME[j7-c66_0-fw] = "${nonarch_base_libdir}/firmware/j7-c66_0-fw"
ALTERNATIVE_LINK_NAME[j7-c66_1-fw] = "${nonarch_base_libdir}/firmware/j7-c66_1-fw"
ALTERNATIVE_LINK_NAME[j7-c71_0-fw] = "${nonarch_base_libdir}/firmware/j7-c71_0-fw"

ALTERNATIVE_LINK_NAME[j7-main-r5f0_0-fw-sec] = "${nonarch_base_libdir}/firmware/j7-main-r5f0_0-fw-sec"
ALTERNATIVE_LINK_NAME[j7-main-r5f0_1-fw-sec] = "${nonarch_base_libdir}/firmware/j7-main-r5f0_1-fw-sec"
ALTERNATIVE_LINK_NAME[j7-c66_0-fw-sec] = "${nonarch_base_libdir}/firmware/j7-c66_0-fw-sec"
ALTERNATIVE_LINK_NAME[j7-c66_1-fw-sec] = "${nonarch_base_libdir}/firmware/j7-c66_1-fw-sec"
ALTERNATIVE_LINK_NAME[j7-c71_0-fw-sec] = "${nonarch_base_libdir}/firmware/j7-c71_0-fw-sec"

ALTERNATIVE_LINK_NAME[j721s2-main-r5f0_0-fw] = "${nonarch_base_libdir}/firmware/j721s2-main-r5f0_0-fw"
ALTERNATIVE_LINK_NAME[j721s2-main-r5f0_1-fw] = "${nonarch_base_libdir}/firmware/j721s2-main-r5f0_1-fw"
ALTERNATIVE_LINK_NAME[j721s2-c71_0-fw] = "${nonarch_base_libdir}/firmware/j721s2-c71_0-fw"
ALTERNATIVE_LINK_NAME[j721s2-c71_1-fw] = "${nonarch_base_libdir}/firmware/j721s2-c71_1-fw"

ALTERNATIVE_LINK_NAME[j721s2-main-r5f0_0-fw-sec] = "${nonarch_base_libdir}/firmware/j721s2-main-r5f0_0-fw-sec"
ALTERNATIVE_LINK_NAME[j721s2-main-r5f0_1-fw-sec] = "${nonarch_base_libdir}/firmware/j721s2-main-r5f0_1-fw-sec"
ALTERNATIVE_LINK_NAME[j721s2-c71_0-fw-sec] = "${nonarch_base_libdir}/firmware/j721s2-c71_0-fw-sec"
ALTERNATIVE_LINK_NAME[j721s2-c71_1-fw-sec] = "${nonarch_base_libdir}/firmware/j721s2-c71_1-fw-sec"

ALTERNATIVE_LINK_NAME[j784s4-main-r5f0_0-fw] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f0_0-fw"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f0_1-fw] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f0_1-fw"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f1_0-fw] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f1_0-fw"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f1_1-fw] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f1_1-fw"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f2_0-fw] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f2_0-fw"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f2_1-fw] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f2_1-fw"
ALTERNATIVE_LINK_NAME[j784s4-c71_0-fw] = "${nonarch_base_libdir}/firmware/j784s4-c71_0-fw"
ALTERNATIVE_LINK_NAME[j784s4-c71_1-fw] = "${nonarch_base_libdir}/firmware/j784s4-c71_1-fw"
ALTERNATIVE_LINK_NAME[j784s4-c71_2-fw] = "${nonarch_base_libdir}/firmware/j784s4-c71_2-fw"
ALTERNATIVE_LINK_NAME[j784s4-c71_3-fw] = "${nonarch_base_libdir}/firmware/j784s4-c71_3-fw"

ALTERNATIVE_LINK_NAME[j784s4-main-r5f0_0-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f0_0-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f0_1-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f0_1-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f1_0-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f1_0-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f1_1-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f1_1-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f2_0-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f2_0-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-main-r5f2_1-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-main-r5f2_1-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-c71_0-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-c71_0-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-c71_1-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-c71_1-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-c71_2-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-c71_2-fw-sec"
ALTERNATIVE_LINK_NAME[j784s4-c71_3-fw-sec] = "${nonarch_base_libdir}/firmware/j784s4-c71_3-fw-sec"

ALTERNATIVE_LINK_NAME[j742s2-main-r5f0_0-fw] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f0_0-fw"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f0_1-fw] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f0_1-fw"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f1_0-fw] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f1_0-fw"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f1_1-fw] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f1_1-fw"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f2_0-fw] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f2_0-fw"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f2_1-fw] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f2_1-fw"
ALTERNATIVE_LINK_NAME[j742s2-c71_0-fw] = "${nonarch_base_libdir}/firmware/j742s2-c71_0-fw"
ALTERNATIVE_LINK_NAME[j742s2-c71_1-fw] = "${nonarch_base_libdir}/firmware/j742s2-c71_1-fw"
ALTERNATIVE_LINK_NAME[j742s2-c71_2-fw] = "${nonarch_base_libdir}/firmware/j742s2-c71_2-fw"

ALTERNATIVE_LINK_NAME[j742s2-main-r5f0_0-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f0_0-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f0_1-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f0_1-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f1_0-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f1_0-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f1_1-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f1_1-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f2_0-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f2_0-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-main-r5f2_1-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-main-r5f2_1-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-c71_0-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-c71_0-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-c71_1-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-c71_1-fw-sec"
ALTERNATIVE_LINK_NAME[j742s2-c71_2-fw-sec] = "${nonarch_base_libdir}/firmware/j742s2-c71_2-fw-sec"

ALTERNATIVE_LINK_NAME[j722s-main-r5f0_0-fw] = "${nonarch_base_libdir}/firmware/j722s-main-r5f0_0-fw"
ALTERNATIVE_LINK_NAME[j722s-c71_0-fw] = "${nonarch_base_libdir}/firmware/j722s-c71_0-fw"
ALTERNATIVE_LINK_NAME[j722s-c71_1-fw] = "${nonarch_base_libdir}/firmware/j722s-c71_1-fw"

ALTERNATIVE_LINK_NAME[j722s-main-r5f0_0-fw-sec] = "${nonarch_base_libdir}/firmware/j722s-main-r5f0_0-fw-sec"
ALTERNATIVE_LINK_NAME[j722s-c71_0-fw-sec] = "${nonarch_base_libdir}/firmware/j722s-c71_0-fw-sec"
ALTERNATIVE_LINK_NAME[j722s-c71_1-fw-sec] = "${nonarch_base_libdir}/firmware/j722s-c71_1-fw-sec"

# Create the firmware alternatives

ALTERNATIVE_TARGET[j7-main-r5f0_0-fw] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}"
ALTERNATIVE_TARGET[j7-main-r5f0_1-fw] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}"
ALTERNATIVE_TARGET[j7-c66_0-fw] = "${INSTALL_FW_DIR}/${C66_1_FW}"
ALTERNATIVE_TARGET[j7-c66_1-fw] = "${INSTALL_FW_DIR}/${C66_2_FW}"
ALTERNATIVE_TARGET[j7-c71_0-fw] = "${INSTALL_FW_DIR}/${C7X_1_FW}"

ALTERNATIVE_TARGET[j7-main-r5f0_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}.signed"
ALTERNATIVE_TARGET[j7-main-r5f0_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}.signed"
ALTERNATIVE_TARGET[j7-c66_0-fw-sec] = "${INSTALL_FW_DIR}/${C66_1_FW}.signed"
ALTERNATIVE_TARGET[j7-c66_1-fw-sec] = "${INSTALL_FW_DIR}/${C66_2_FW}.signed"
ALTERNATIVE_TARGET[j7-c71_0-fw-sec] = "${INSTALL_FW_DIR}/${C7X_1_FW}.signed"

ALTERNATIVE_TARGET[j721s2-main-r5f0_0-fw] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}"
ALTERNATIVE_TARGET[j721s2-main-r5f0_1-fw] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}"
ALTERNATIVE_TARGET[j721s2-c71_0-fw] = "${INSTALL_FW_DIR}/${C7X_1_FW}"
ALTERNATIVE_TARGET[j721s2-c71_1-fw] = "${INSTALL_FW_DIR}/${C7X_2_FW}"

ALTERNATIVE_TARGET[j721s2-main-r5f0_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}.signed"
ALTERNATIVE_TARGET[j721s2-main-r5f0_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}.signed"
ALTERNATIVE_TARGET[j721s2-c71_0-fw-sec] = "${INSTALL_FW_DIR}/${C7X_1_FW}.signed"
ALTERNATIVE_TARGET[j721s2-c71_1-fw-sec] = "${INSTALL_FW_DIR}/${C7X_2_FW}.signed"

ALTERNATIVE_TARGET[j784s4-main-r5f0_0-fw] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}"
ALTERNATIVE_TARGET[j784s4-main-r5f0_1-fw] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}"
ALTERNATIVE_TARGET[j784s4-main-r5f1_0-fw] = "${INSTALL_FW_DIR}/${MCU_3_0_FW}"
ALTERNATIVE_TARGET[j784s4-main-r5f1_1-fw] = "${INSTALL_FW_DIR}/${MCU_3_1_FW}"
ALTERNATIVE_TARGET[j784s4-main-r5f2_0-fw] = "${INSTALL_FW_DIR}/${MCU_4_0_FW}"
ALTERNATIVE_TARGET[j784s4-main-r5f2_1-fw] = "${INSTALL_FW_DIR}/${MCU_4_1_FW}"
ALTERNATIVE_TARGET[j784s4-c71_0-fw] = "${INSTALL_FW_DIR}/${C7X_1_FW}"
ALTERNATIVE_TARGET[j784s4-c71_1-fw] = "${INSTALL_FW_DIR}/${C7X_2_FW}"
ALTERNATIVE_TARGET[j784s4-c71_2-fw] = "${INSTALL_FW_DIR}/${C7X_3_FW}"
ALTERNATIVE_TARGET[j784s4-c71_3-fw] = "${INSTALL_FW_DIR}/${C7X_4_FW}"

ALTERNATIVE_TARGET[j784s4-main-r5f0_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}.signed"
ALTERNATIVE_TARGET[j784s4-main-r5f0_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}.signed"
ALTERNATIVE_TARGET[j784s4-main-r5f1_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_3_0_FW}.signed"
ALTERNATIVE_TARGET[j784s4-main-r5f1_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_3_1_FW}.signed"
ALTERNATIVE_TARGET[j784s4-main-r5f2_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_4_0_FW}.signed"
ALTERNATIVE_TARGET[j784s4-main-r5f2_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_4_1_FW}.signed"
ALTERNATIVE_TARGET[j784s4-c71_0-fw-sec] = "${INSTALL_FW_DIR}/${C7X_1_FW}.signed"
ALTERNATIVE_TARGET[j784s4-c71_1-fw-sec] = "${INSTALL_FW_DIR}/${C7X_2_FW}.signed"
ALTERNATIVE_TARGET[j784s4-c71_2-fw-sec] = "${INSTALL_FW_DIR}/${C7X_3_FW}.signed"
ALTERNATIVE_TARGET[j784s4-c71_3-fw-sec] = "${INSTALL_FW_DIR}/${C7X_4_FW}.signed"

ALTERNATIVE_TARGET[j742s2-main-r5f0_0-fw] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}"
ALTERNATIVE_TARGET[j742s2-main-r5f0_1-fw] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}"
ALTERNATIVE_TARGET[j742s2-main-r5f1_0-fw] = "${INSTALL_FW_DIR}/${MCU_3_0_FW}"
ALTERNATIVE_TARGET[j742s2-main-r5f1_1-fw] = "${INSTALL_FW_DIR}/${MCU_3_1_FW}"
ALTERNATIVE_TARGET[j742s2-main-r5f2_0-fw] = "${INSTALL_FW_DIR}/${MCU_4_0_FW}"
ALTERNATIVE_TARGET[j742s2-main-r5f2_1-fw] = "${INSTALL_FW_DIR}/${MCU_4_1_FW}"
ALTERNATIVE_TARGET[j742s2-c71_0-fw] = "${INSTALL_FW_DIR}/${C7X_1_FW}"
ALTERNATIVE_TARGET[j742s2-c71_1-fw] = "${INSTALL_FW_DIR}/${C7X_2_FW}"
ALTERNATIVE_TARGET[j742s2-c71_2-fw] = "${INSTALL_FW_DIR}/${C7X_3_FW}"

ALTERNATIVE_TARGET[j742s2-main-r5f0_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}.signed"
ALTERNATIVE_TARGET[j742s2-main-r5f0_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_1_FW}.signed"
ALTERNATIVE_TARGET[j742s2-main-r5f1_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_3_0_FW}.signed"
ALTERNATIVE_TARGET[j742s2-main-r5f1_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_3_1_FW}.signed"
ALTERNATIVE_TARGET[j742s2-main-r5f2_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_4_0_FW}.signed"
ALTERNATIVE_TARGET[j742s2-main-r5f2_1-fw-sec] = "${INSTALL_FW_DIR}/${MCU_4_1_FW}.signed"
ALTERNATIVE_TARGET[j742s2-c71_0-fw-sec] = "${INSTALL_FW_DIR}/${C7X_1_FW}.signed"
ALTERNATIVE_TARGET[j742s2-c71_1-fw-sec] = "${INSTALL_FW_DIR}/${C7X_2_FW}.signed"
ALTERNATIVE_TARGET[j742s2-c71_2-fw-sec] = "${INSTALL_FW_DIR}/${C7X_3_FW}.signed"

ALTERNATIVE_TARGET[j722s-main-r5f0_0-fw] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}"
ALTERNATIVE_TARGET[j722s-c71_0-fw] = "${INSTALL_FW_DIR}/${C7X_1_FW}"
ALTERNATIVE_TARGET[j722s-c71_1-fw] = "${INSTALL_FW_DIR}/${C7X_2_FW}"

ALTERNATIVE_TARGET[j722s-main-r5f0_0-fw-sec] = "${INSTALL_FW_DIR}/${MCU_2_0_FW}.signed"
ALTERNATIVE_TARGET[j722s-c71_0-fw-sec] = "${INSTALL_FW_DIR}/${C7X_1_FW}.signed"
ALTERNATIVE_TARGET[j722s-c71_1-fw-sec] = "${INSTALL_FW_DIR}/${C7X_2_FW}.signed"

ALTERNATIVE_PRIORITY = "50"

# make sure that lib/firmware, and all its contents are part of the package
FILES:${PN} += "${nonarch_base_libdir}/firmware"

# This is used to prevent the build system to_strip the executables
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
# This is used to prevent the build system to split the debug info in a separate file
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
# As it likely to be a different arch from the Yocto build, disable checking by adding "arch" to INSANE_SKIP
INSANE_SKIP:${PN} += "arch"

# we don't want to configure and build the source code
do_compile[noexec] = "1"
do_configure[noexec] = "1"

addtask deploy after do_install
