FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:class-target = " file://0001-setup.cfg-drop-all-extras.patch;apply=no"

RDEPENDS:${PN}:remove:class-target = "${PYTHON_PN}-certifi ${PYTHON_PN}-cryptography ${PYTHON_PN}-idna ${PYTHON_PN}-pyopenssl"
