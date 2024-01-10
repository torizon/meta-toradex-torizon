require conf/distro/include/tdx-base.inc

MAINTAINER = "Toradex TorizonCore Team <torizoncore@toradex.com>"

TCLIBCAPPEND = ""

COPY_LIC_MANIFEST = "1"
COPY_LIC_DIRS = "1"

IMAGE_CLASSES:append = " image_type_torizon"
IMAGE_CLASSES:remove = "image_type_tezi image_types_fsl"

IMAGE_BASENAME_SUFFIX ?= ""

IMX_DEFAULT_BSP = "nxp"
IMX_DEFAULT_BSP:upstream = "mainline"

SDK_NAME_PREFIX = "${DISTRO}"
SDK_VERSION = "${DISTRO_VERSION}"

# TorizonCore machine specific overrides
INHERIT += "torizon"

# Toradex specific classes
INHERIT += "toradex-sanity toradex-mirrors"

# This also disables the static packageconfig. runc needs to be dynamically
# linked for seccomp support. https://github.com/opencontainers/runc/issues/2008
PACKAGECONFIG:remove:pn-${PREFERRED_PROVIDER_virtual/runc} = "static"
PACKAGECONFIG:append:pn-${PREFERRED_PROVIDER_virtual/runc} = " seccomp"
PACKAGECONFIG:append:pn-${PREFERRED_PROVIDER_virtual/docker} = " seccomp"
PACKAGECONFIG:append:pn-systemd = " seccomp"

# SOTA
OSTREE_OSNAME = "torizon"
OSTREE_MULTI_DEVICETREE_SUPPORT = "1"
SOTA_CLIENT_FEATURES:append = " ubootenv"
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"
INITRAMFS_FSTYPES = "cpio.gz"
BUILD_OSTREE_TARBALL = "0"
# We do not need teziimg, ota-ext4 or wic images by default
IMAGE_FSTYPES_REMOVE ?= "ota-ext4 wic wic.gz wic.bmap wic.xz"
IMAGE_FSTYPES:remove:sota = "${IMAGE_FSTYPES_REMOVE}"

# No need to install boot script, kernel image, devicetrees or firmware into the boot partition
IMAGE_BOOT_FILES = ""
IMAGE_BOOT_FILES_REMOVE:sota = "boot.scr-${MACHINE};boot.scr overlays.txt overlays/*;overlays/"
IMAGE_BOOT_FILES_REMOVE:append:apalis-imx8 = " hdmitxfw.bin dpfw.bin"

DISTRO_FEATURES:append = " virtualization stateless-system"
DISTRO_FEATURES:remove = "3g alsa irda pcmcia nfc ldconfig pulseaudio wayland x11 ptest multiarch vulkan"
DISTRO_FEATURES:imx-generic-bsp:remove = "opengl"

# No need for x11 even for native
DISTRO_FEATURES_NATIVE:imx-generic-bsp:remove = "x11"
DISTRO_FEATURES_NATIVESDK:imx-generic-bsp:remove = "x11"

# Note, enable or disable the useradd-staticids in a configured system,
# the TMPDIR/DEPLOY_DIR/SSTATE_DIR may contain incorrect uid/gid values.
# Clearing them will correct this condition.
USERADDEXTENSION = "useradd-staticids"
USERADD_UID_TABLES = "files/torizon-static-passwd"
USERADD_GID_TABLES = "files/torizon-static-group"
USERADD_ERROR_DYNAMIC = "skip"

# Compatibility with toradex layers
BBMASK += " \
    /meta-toradex-bsp-common/recipes-core/systemd/systemd_%.bbappend \
    /meta-freescale/recipes-graphics/cairo \
    /openembedded-core/meta/recipes-devtools/cargo \
    /openembedded-core/meta/recipes-devtools/rust \
"

# machines where TorizonCore is experimental
EXPERIMENTALOVERRIDES = ""
# Currently TorizonCore does not support any experimental machines, in future such machines can be added like below:
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

# Rust toolchain preferred versions:
RUSTVERSION ?= "1.70%"

PREFERRED_VERSION_cargo ?= "${RUSTVERSION}"
PREFERRED_VERSION_cargo-native ?= "${RUSTVERSION}"
PREFERRED_VERSION_libstd-rs ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-cross-${TARGET_ARCH} ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-llvm ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-llvm-native ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-native ?= "${RUSTVERSION}"
