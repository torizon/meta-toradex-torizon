SRCREV_meta = "c8df6466e976f742552e3b88358fac0a0f552103"
SRCREV_meta:use-head-next = "${AUTOREV}"

KMETABRANCH = "kirkstone-6.x.y"
KMETA = "kernel-meta"

KMETAREPOSITORY="github.com/toradex/toradex-kernel-cache.git"
KMETAPROTOCOL="https"

SRC_URI += "git://${KMETAREPOSITORY};protocol=${KMETAPROTOCOL};type=kmeta;name=meta;branch=${KMETABRANCH};destsuffix=${KMETA}"
