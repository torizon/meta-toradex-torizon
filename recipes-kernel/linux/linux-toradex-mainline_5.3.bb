LINUX_VERSION ?= "5.3.10"
PV = "${LINUX_VERSION}+git${SRCPV}"

export DTC_FLAGS = "-@"

require recipes-kernel/linux/linux-toradex-mainline.inc
require recipes-kernel/linux/linux-yocto.inc

SRCREV_machine = "401bf3f29b1aa6d9ca32bd3252fc9beabe93d80b"
SRCREV_meta = "075a1227a3c0c548b9954cf5d5adced01c92ef76"

KBRANCH = "toradex_5.3.y"
KMETABRANCH = "toradex_5.3.y"
LINUX_KERNEL_TYPE = "standard"
LINUX_VERSION_EXTENSION ?= "-torizon-${LINUX_KERNEL_TYPE}"

KMETA = "kernel-meta"

KMETAREPOSITORY="github.com/toradex/toradex-kernel-cache.git"
KMETAPROTOCOL="https"

SRC_URI += " \
    git://${KMETAREPOSITORY};protocol=${KMETAPROTOCOL};type=kmeta;name=meta;branch=${KMETABRANCH};destsuffix=${KMETA} \
"
