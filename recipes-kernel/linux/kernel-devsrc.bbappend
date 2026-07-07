do_install:append() {
    (
        cd ${S}

        if [ "${ARCH}" = "arm64" ]; then
            # 6.12+
            cp -a --parents arch/arm64/kernel/vdso/vgetrandom.c $kerneldir/build/ 2>/dev/null || :
            cp -a --parents arch/arm64/kernel/vdso/vgetrandom-chacha.S $kerneldir/build/ 2>/dev/null || :

            cp -a --parents arch/arm64/tools/syscall_64.tbl $kerneldir/build/ 2>/dev/null || :
            cp -a --parents arch/arm64/tools/syscall_32.tbl $kerneldir/build/ 2>/dev/null || :

            chown -R root:root ${D}
        fi
    )

    mv $kerneldir/build $kerneldir/linux
    tar cjfv ${D}/usr/src/linux.tar.bz2 -C $kerneldir linux
    rm -rf ${D}/usr/lib ${D}/usr/src/kernel
}

FILES:${PN} += "/usr/src/linux.tar.bz2"

RDEPENDS:${PN}:remove = "bc python3 flex bison glibc-utils openssl-dev util-linux gcc-plugins libmpc-dev"
