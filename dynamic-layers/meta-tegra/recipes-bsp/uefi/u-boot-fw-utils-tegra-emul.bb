SUMMARY = "Tegra emulation for the U-Boot tools to access environment"

DESCRIPTION = "This package provides emulation for the fw_printenv/setenv utilities \
on the Tegra platform to be used in the Torizon OTA update service. \
This emulation allows to get/set the upgrade_available and rollback variables and \
reset the boot counter (the bootcount variable). \
The upgrade_available and rollback variables are represented as flags in the \
special EFI variable, TorizonBootFlags, implemented in the Tegra bootloader. \
The boot counter is implemented in a Tegra HW register by bootloader, \
its reset is done by nvbootctrl in Linux provided by tegra-redundant-boot-base \
recipe from meta-tegra"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://fw_printenv.sh \
"

PROVIDES += "u-boot-fw-utils"
RPROVIDES:${PN} += "u-boot-fw-utils"

RDEPENDS:${PN} += " bash efivar e2fsprogs tegra-redundant-boot-base"

S = "${WORKDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/fw_printenv.sh  ${D}${bindir}/fw_printenv
	ln -s fw_printenv ${D}${bindir}/fw_setenv
}
