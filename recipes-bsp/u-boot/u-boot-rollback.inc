FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-rollback:"

SRC_URI:append = " \
    file://bootcommand.cfg \
    file://bootcount.cfg \
    file://bootlimit.cfg \
"
