FILESEXTRAPATHS:prepend := "${THISDIR}/${P}:"

SRC_URI:append = " \
    file://0001-update-default-grub-cfg-header.patch \
    file://0002-Add-support-for-the-fdtfile-variable-in-uEnv.txt.patch \
    file://0003-ostree-fetcher-curl-handle-non-404-errors-as-G_IO_ER.patch \
    file://0004-ostree-fetcher-curl-set-max-parallel-connections.patch \
    file://0005-curl-Make-socket-callback-during-cleanup-into-no-op.patch \
"

require recipes-extended/ostree/ostree-torizon.inc
