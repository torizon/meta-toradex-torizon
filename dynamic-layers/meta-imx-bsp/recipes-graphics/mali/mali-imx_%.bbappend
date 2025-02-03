DEPENDS = ""

do_install () {
    install -d ${D}${nonarch_base_libdir}/firmware
    cp -r ${S}/usr/lib/firmware/* ${D}${nonarch_base_libdir}/firmware
}

