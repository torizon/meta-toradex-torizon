do_install:append() {
    # On Torizon we use avahi-daemon to setup the hostname. So we need to properly
    # order it's service to only run after 'set-hostname.service' finishes.
    sed -i '/^Before=network-pre.target/ s/$/ avahi-daemon.service/' ${D}${systemd_system_unitdir}/set-hostname.service
    sed -i '/^WantedBy=network.target/ s/$/ avahi-daemon.service/' ${D}${systemd_system_unitdir}/set-hostname.service
}

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
