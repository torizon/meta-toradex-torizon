# Remove mpfr as a dependency due to GPLv3.
# Requires the backported patches to remove the hardcoded dependecy on the
# mpfr C library
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
   file://0001-Fix-issues-with-floating-point-precision-and-roundin.patch \
   file://0002-Add-regression-tests-for-parsing-edge-cases.patch \
   file://0003-Fix-number-parsing-edge-cases.patch \
"

DEPENDS:remove = "mpfr"
