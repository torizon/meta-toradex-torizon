RDEPENDS:${PN}:class-target:remove:sota = "virtual-grub-bootconf"

GRUB_BUILDIN += "reboot regexp"

# Create startup.nsh so it can be consumed by wic
do_deploy:append:class-target() {
	DEST_IMAGE=$(echo ${GRUB_IMAGE} | sed -e 's/^grub-efi-//')
	printf 'fs0:\\EFI\\BOOT\\%s\n' "${DEST_IMAGE}" > startup.nsh
	install -m 755 ${B}/startup.nsh ${DEPLOYDIR}
}
