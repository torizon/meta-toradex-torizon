SRCREV_torizon-meta = "b07ca2553bc9108283d0f6d5a433640b77e58a5a"
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
