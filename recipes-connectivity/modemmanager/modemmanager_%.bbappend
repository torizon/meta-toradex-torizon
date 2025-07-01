FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://0001-quectel-disable-qmi-unsolicited-profile-manager-even.patch \
    file://0002-broadband-modem-qmi-quectel-fix-task-completion-when.patch \
"

