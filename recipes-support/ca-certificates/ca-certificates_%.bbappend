FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Backport from OE-core master (c5098a7074), pending on scarthgap.
# Drop once the pinned OE-core carries it.
SRC_URI += "file://0001-Blacklist-emSign-ECC-Root-CA-G3-and-Comodo-AAA-Servi.patch"
