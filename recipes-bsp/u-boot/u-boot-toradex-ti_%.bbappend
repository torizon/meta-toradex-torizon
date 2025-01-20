FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-ota:"

SRC_URI:append = " \
    file://bootcommand.cfg \
    file://bootcount.cfg \
    file://bootlimit.cfg \
"
