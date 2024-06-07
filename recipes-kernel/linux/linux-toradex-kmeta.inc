SRCREV_meta = "494078b8b4c9cd65bfff94fd3179ecff58a21a6d"
SRCREV_meta:use-head-next = "${AUTOREV}"

KMETABRANCH = "kirkstone-6.x.y"
KMETA = "kernel-meta"

KMETAREPOSITORY="github.com/toradex/toradex-kernel-cache.git"
KMETAPROTOCOL="https"

SRC_URI += "git://${KMETAREPOSITORY};protocol=${KMETAPROTOCOL};type=kmeta;name=meta;branch=${KMETABRANCH};destsuffix=${KMETA}"

KERNEL_FEATURES:append:cfs-support = " features/erofs/erofs.scc"
