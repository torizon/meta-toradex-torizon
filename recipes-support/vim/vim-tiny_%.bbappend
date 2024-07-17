FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://defaults.vim \
"

do_install:append() {
    install -d ${D}/${datadir}/vim
    install -m 0644 ${UNPACKDIR}/defaults.vim ${D}/${datadir}/vim/
}

FILES:${PN} += "${datadir}/vim/*"
