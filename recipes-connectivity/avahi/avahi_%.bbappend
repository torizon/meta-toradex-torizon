do_install:append() {
    # On Torizon we use avahi-daemon to setup the hostname. So we need to properly
    # order it's service to only run after 'set-hostname.service' finishes.
    sed -i '/^After=/ s/$/ network-pre.target/' ${D}${systemd_system_unitdir}/avahi-daemon.service
    sed -i '/^Description=/a\Wants=network-pre.target' ${D}${systemd_system_unitdir}/avahi-daemon.service
}
