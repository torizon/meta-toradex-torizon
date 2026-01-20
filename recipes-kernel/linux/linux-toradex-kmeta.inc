SRCREV_torizon-meta = "c513566a0876f8415343495e3bad8d88801f7cba"
SRCREV_torizon-meta:use-head-next = "${AUTOREV}"

KMETABRANCH = "scarthgap-7.x.y"
KMETATORIZON = "torizon-kernel-meta"

KMETAREPOSITORY="github.com/toradex/toradex-kernel-cache.git"
KMETAPROTOCOL="https"

SRC_URI += "git://${KMETAREPOSITORY};protocol=${KMETAPROTOCOL};type=kmeta;name=torizon-meta;branch=${KMETABRANCH};destsuffix=${KMETATORIZON}"

## Compose Torizon top level .scc file and include it to our build.
KERNEL_FEATURES += "bsp/${MACHINE}-${LINUX_KERNEL_TYPE}-torizon.scc"

# Enable THUMB2 on all machines except the apalis-imx6.
# TODO: This is a hotfix for the Torizon 7.5.0 release and should be removed in the future.
SRC_URI:append = "\
    ${@bb.utils.contains_any('MACHINE', 'apalis-imx6', '', 'file://enable-thumb2.cfg', d)} \
"
