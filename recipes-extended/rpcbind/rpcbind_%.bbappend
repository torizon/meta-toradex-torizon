FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:sota = " file://0001-rpcbind.service-run-after-systemd-tmpfiles-setup.patch"

# we are using nss-altfiles and /etc/passwd and related files are palced in 
# /usr/lib/
PACKAGECONFIG:append = ' ${@bb.utils.contains("DISTRO_FEATURES", "stateless-system", "nss-altfiles", "", d)}'
PACKAGECONFIG[nss-altfiles] = '--with-nss-modules="files altfiles",,,'
