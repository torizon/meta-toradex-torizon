do_install:append() {
    mv $kerneldir/build $kerneldir/linux
    tar cjfv ${D}/usr/src/linux.tar.bz2 -C $kerneldir linux
    rm -rf ${D}/usr/lib ${D}/usr/src/kernel
}

FILES:${PN} += "/usr/src/linux.tar.bz2"

RDEPENDS:${PN}:remove = "bc python3 flex bison glibc-utils openssl-dev util-linux gcc-plugins libmpc-dev gawk"
