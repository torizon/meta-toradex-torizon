do_install:append:qemuarm64() {
    # Use the only rootdiski's PARTUUID as the serial number
    sed -i -e '/\/bin\/sh/ahexpartuuid="0x$(ls /dev/disk/by-partuuid | head -1 | tr - 1 | cut -c -8)"' ${D}${bindir}/sethostname
    sed -i -e 's#recovery-mode#`printf "%d" $hexpartuuid | cut -c -8`#' ${D}${bindir}/sethostname
}

do_install:append:genericx86-64() {
    # Use the only rootdiski's PARTUUID as the serial number
    sed -i -e '/\/bin\/sh/ahexpartuuid="0x$(ls /dev/disk/by-partuuid | head -1 | tr - 1 | cut -c -8)"' ${D}${bindir}/sethostname
    sed -i -e 's#recovery-mode#`printf "%d" $hexpartuuid | cut -c -8`#' ${D}${bindir}/sethostname
}
