FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://0001-85-nm-unmanaged.rules-do-not-manage-docker-bridges.patch \
    file://toradex-nmconnection.conf \
    file://network.nmconnection.in \
    file://99-disable-uap.conf \
"

# Depend on libedit as it has a more friendly license than readline (GPLv3)
DEPENDS += "libedit"

PACKAGECONFIG:remove = "dnsmasq"

PACKAGECONFIG:append = " modemmanager ppp"

NET_NAME = "ethernet"
NET_NUMS = "0 1"

do_install:append() {
    install -m 0600 ${WORKDIR}/toradex-nmconnection.conf ${D}${nonarch_libdir}/NetworkManager/conf.d

    for netnum in ${NET_NUMS}; do
        sed -e "s/@NET_NAME@/${NET_NAME}/g" -e "s/@NET_NUM@/$netnum/g" \
            ${WORKDIR}/network.nmconnection.in \
            >  ${D}${sysconfdir}/NetworkManager/system-connections/network"$netnum".nmconnection
    done

    chmod 0600 ${D}${sysconfdir}/NetworkManager/system-connections/network?.nmconnection

    install -m 0600 ${WORKDIR}/99-disable-uap.conf ${D}${sysconfdir}/NetworkManager/conf.d/99-disable-uap.conf
}
