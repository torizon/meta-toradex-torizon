# RTL8821AU has no driver in this kernel (6.6.37 predates mainline
# rtw88's RTL8821AU support); lwfinger/rtw88 packages the same
# backport as an externally-buildable tree — Makefile honors KERNEL_SRC
# directly, no patch needed.
SUMMARY = "Realtek RTL8821AU USB Wi-Fi driver"
LICENSE = "GPL-2.0-only | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://main.c;beginline=1;endline=1;md5=04cb8411563d8726ae2273d76febc90d"

inherit module

SRC_URI = "git://github.com/lwfinger/rtw88;branch=master;protocol=https"
SRCREV = "a56bcd26e770257612a0803249cbd4095fc6feca"

S = "${WORKDIR}/git"

# __FILE__-based macros embed the TMPDIR-rooted ${S} path, tripping
# buildpaths QA on all five .kos; this aarch64 cross-gcc doesn't apply
# -f{macro,file}-prefix-map to __FILE__, so there's no rewrite fix —
# drop the check instead.
WARN_QA:remove = "buildpaths"

# rtw_8821a links against rtw_88xxa's exported PHY/IQK code — a real
# dependency, not just rtw_core/rtw_usb.
RTW88_MODULES = "rtw_core rtw_usb rtw_88xxa rtw_8821a rtw_8821au"

# Build only the modules listed in RTW88_MODULES.
do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake -C ${STAGING_KERNEL_DIR} M=${S} \
		   KERNEL_PATH=${STAGING_KERNEL_DIR} \
		   KERNEL_VERSION=${KERNEL_VERSION} \
		   CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
		   AR="${KERNEL_AR}" OBJCOPY="${KERNEL_OBJCOPY}" \
		   STRIP="${KERNEL_STRIP}" \
		   O=${STAGING_KERNEL_BUILDDIR} \
		   KBUILD_EXTRA_SYMBOLS="${KBUILD_EXTRA_SYMBOLS}" \
		   $(for mod in ${RTW88_MODULES}; do echo -n "$mod.ko "; done)
}

# module.bbclass's default do_install (`make modules_install`) isn't
# provided by this Makefile — stage the .kos ourselves.
do_install() {
	install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/rtw88
	for mod in ${RTW88_MODULES}; do
		install -m 0644 ${B}/${mod}.ko \
			${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/rtw88/
	done
}

# The five kernel-module-rtw-* packages reach ${PN}'s RDEPENDS on their
# own (module.bbclass's KERNEL_MODULES_META_PACKAGE = "${PN}" wires split
# kernel modules up automatically); rtw8821a's fw.bin doesn't come from
# this recipe, so it's the one dependency still declared explicitly.
RDEPENDS:${PN} = "linux-firmware-rtl8821"
