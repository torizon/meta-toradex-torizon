FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://0001-fs-fat-Don-t-error-when-mtime-is-0.patch \
"

RDEPENDS:${PN}:class-target:remove:sota = "virtual-grub-bootconf"

GRUB_BUILDIN += "reboot"

# Create startup.nsh so it can be consumed by wic
do_deploy:append:class-target() {
	DEST_IMAGE=$(echo ${GRUB_IMAGE} | sed -e 's/^grub-efi-//')
	echo 'fs0:\\EFI\\BOOT\\'${DEST_IMAGE} > startup.nsh
	install -m 755 ${B}/startup.nsh ${DEPLOYDIR}
}
