FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-Add-TorizonCore-specific-groups.patch \
    file://0002-Adapt-group-numbers-to-match-the-static-assignment-i.patch \
"
