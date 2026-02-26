# in Torizon only need nvbootctrl to support resetting boot counter
do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/sbin/nvbootctrl ${D}${sbindir}
}

# remove unnecessary rdepends
RDEPENDS:${PN}:remove = " setup-nv-boot-control-service tegra-configs-bootloader util-linux-lsblk"
