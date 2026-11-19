FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://0002-boot-sm_temperature.c-silence-chip-temperature-messa.patch;patchdir=./boot \
"
