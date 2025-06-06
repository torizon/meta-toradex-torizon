SRCREV_torizon-meta = "c404c3fc77b0e455ddd39b36e398ccef42c59061"
SRCREV_torizon-meta:use-head-next = "${AUTOREV}"

KMETABRANCH = "scarthgap-7.x.y"
KMETATORIZON = "torizon-kernel-meta"

KMETAREPOSITORY="github.com/toradex/toradex-kernel-cache.git"
KMETAPROTOCOL="https"

SRC_URI += "git://${KMETAREPOSITORY};protocol=${KMETAPROTOCOL};type=kmeta;name=torizon-meta;branch=${KMETABRANCH};destsuffix=${KMETATORIZON}"

## Compose Torizon top level .scc file and include it to our build.
KERNEL_FEATURES += "bsp/${MACHINE}-${LINUX_KERNEL_TYPE}-torizon.scc"
