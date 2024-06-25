FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

require linux-torizon.inc

SRC_URI += "\
    file://torizon.scc \
"

inherit toradex-kernel-localversion
