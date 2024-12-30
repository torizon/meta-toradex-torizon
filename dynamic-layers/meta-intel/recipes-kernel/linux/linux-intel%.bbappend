require recipes-kernel/linux/linux-torizon.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Patch already upstream
SRC_URI:append = " \
    file://0001-i915-hwmon-dgfx-check.patch;apply=no \
"
