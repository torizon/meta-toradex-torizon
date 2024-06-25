SUMMARY = "Torizon OS - experimental image using Podman"
DESCRIPTION = "Torizon OS Linux experimental image based on production image \
using Podman container engine."

require torizon-base.inc
require torizon-container.inc

VIRTUAL-RUNTIME_container_engine = "podman"
IMAGE_VARIANT = "Podman"

CORE_IMAGE_BASE_INSTALL:append = " \
    fuse-overlayfs \
"
