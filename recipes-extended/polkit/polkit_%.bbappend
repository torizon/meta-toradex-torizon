# We only want to use the polkit library
REQUIRED_DISTRO_FEATURES:remove = " polkit"

DEPENDS:remove = "mozjs"

do_install:append() {
    # remove from the package polkitd and other files not required by Torizon
    rm -rf ${D}/etc/polkit-1/
    rm -rf ${D}/usr/lib/polkit-1/polkitd
    rm -rf ${D}/usr/share/polkit-1/rules.d

    # Avoid starting polkitd since we dont install it
    sed -i -e 's,ExecStart=.*,ExecStart=/bin/true,g' ${D}${systemd_unitdir}/system/polkit.service
    sed -i -e '/^ExecStart=.*$/aRemainAfterExit=yes' ${D}${systemd_unitdir}/system/polkit.service
}
