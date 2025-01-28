FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:class-target = " file://0001-setup.py-drop-cffi-from-requirements.patch"

RDEPENDS:${PN}:remove:class-target = "${PYTHON_PN}-cffi"
RDEPENDS:${PN}:append:class-target = " ${PYTHON_PN}-cffi-backend"
