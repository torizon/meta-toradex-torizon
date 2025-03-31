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

# Use our variant of the aktualizr client instead of the
# default from meta-updater
PREFERRED_PROVIDER_aktualizr = "aktualizr-torizon"
PREFERRED_PROVIDER_aktualizr-native = "aktualizr-torizon-native"
PREFERRED_RPROVIDER_aktualizr = "aktualizr-torizon"
PREFERRED_RPROVIDER_aktualizr-info = "aktualizr-torizon"
PREFERRED_RPROVIDER_aktualizr-shared-prov = "aktualizr-torizon"

# disable warning message from meta-security
SKIP_META_SECURITY_SANITY_CHECK = "1"

# Configure the "kernel command-line protection" for Torizon OS (relevant when
# the tdx-signed class is in use).
TOS_SECBOOT_REQUIRED_BOOTARGS_COMMON ?= "root=LABEL=otaroot rootfstype=ext4 ${OSTREE_KERNEL_ARGS}"
TOS_SECBOOT_REQUIRED_BOOTARGS ?= "${TOS_SECBOOT_REQUIRED_BOOTARGS_COMMON}"
TOS_SECBOOT_REQUIRED_BOOTARGS:apalis-imx6 ?= "enable_wait_mode=off vmalloc=400M ${TOS_SECBOOT_REQUIRED_BOOTARGS_COMMON}"
TOS_SECBOOT_REQUIRED_BOOTARGS:colibri-imx6 ?= "enable_wait_mode=off galcore.contiguousSize=50331648 ${TOS_SECBOOT_REQUIRED_BOOTARGS_COMMON}"
TOS_SECBOOT_REQUIRED_BOOTARGS:colibri-imx6ull-emmc ?= "user_debug=30 ${TOS_SECBOOT_REQUIRED_BOOTARGS_COMMON}"
TOS_SECBOOT_REQUIRED_BOOTARGS:qemuarm64 ?= ""
# Override BSP configuration:
TDX_SECBOOT_REQUIRED_BOOTARGS:tdx-signed = "${TOS_SECBOOT_REQUIRED_BOOTARGS}"
