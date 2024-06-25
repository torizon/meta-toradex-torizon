SUMMARY = "Torizon OS user accounts"
DESCRIPTION = "Set up user accounts for Torizon OS"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

EXCLUDE_FROM_WORLD = "1"
INHIBIT_DEFAULT_DEPS = "1"

ALLOW_EMPTY:${PN} = "1"

inherit useradd

USERADD_PACKAGES = "${PN}"

GROUPADD_PARAM:${PN} = "torizon"

# default password is 'torizon', generated with the following command: 'openssl passwd torizon'
USERADD_PARAM:${PN} = "-G adm,sudo,users,plugdev,audio,video,gpio,i2cdev,spidev,dialout,input,pwm -m -d /home/torizon -p GudJRR5U.mLp2 torizon"

pkg_postinst_ontarget:${PN} () {
    if [ ! -e /etc/.passwd_changed ]; then
        passwd -e torizon
        touch /etc/.passwd_changed
    fi
}
