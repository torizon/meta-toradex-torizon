require conf/distro/include/tdx-base.inc
require base-distro.inc

IMAGE_CLASSES:append = " image_type_torizon"
IMAGE_CLASSES:remove = "image_type_tezi"

# Override shared by all torizon distros
DISTROOVERRIDES .= ":torizon-distro"

# Toradex specific classes
INHERIT += "toradex-sanity toradex-mirrors"

# We do not need teziimg, ota-ext4 or wic images by default
IMAGE_FSTYPES_REMOVE ?= "ota-ext4 wic wic.gz wic.bmap wic.xz"

DISTRO_FEATURES_REMOVE ?= "3g alsa irda pcmcia nfc ldconfig pulseaudio wayland x11 ptest multiarch vulkan"

# Compatibility with toradex layers
BBMASK += " \
    /meta-toradex-bsp-common/recipes-core/systemd/systemd_%.bbappend \
    /meta-freescale/recipes-graphics/cairo \
"

# machines where Torizon is experimental
EXPERIMENTALOVERRIDES = ""
# Currently Torizon does not support any experimental machines, in future such machines can be added like below:
#EXPERIMENTALOVERRIDES:colibri-imx6ull = ":experimental"
DISTROOVERRIDES .= "${EXPERIMENTALOVERRIDES}"
