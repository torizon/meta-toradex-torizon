MAINTAINER = "Toradex Torizon OS Team <torizon.os@toradex.com>"

TCLIBCAPPEND = ""

COPY_LIC_MANIFEST = "1"
COPY_LIC_DIRS = "1"

IMAGE_BASENAME_SUFFIX ?= ""

IMX_DEFAULT_BSP = "nxp"
IMX_DEFAULT_BSP:upstream = "mainline"

SDK_NAME_PREFIX = "${DISTRO}"
SDK_VERSION = "${DISTRO_VERSION}"

IMAGE_CLASSES:remove = "image_types_fsl"

# Torizon machine specific overrides
INHERIT += "torizon"

# This also disables the static packageconfig. runc needs to be dynamically
# linked for seccomp support. https://github.com/opencontainers/runc/issues/2008
PACKAGECONFIG:remove:pn-${PREFERRED_PROVIDER_virtual/runc} = "static"
PACKAGECONFIG:append:pn-${PREFERRED_PROVIDER_virtual/runc} = " seccomp"
PACKAGECONFIG:append:pn-${PREFERRED_PROVIDER_virtual/docker} = " seccomp"
PACKAGECONFIG:append:pn-systemd = " seccomp"

# SOTA
GARAGE_SIGN_TOOL = "uptane-sign"
OSTREE_OSNAME = "torizon"
OSTREE_MULTI_DEVICETREE_SUPPORT = "1"
SOTA_CLIENT_FEATURES:append = " ubootenv"
INITRAMFS_IMAGE = "initramfs-ostree-torizon-image"
INITRAMFS_FSTYPES = "cpio.gz"
BUILD_OSTREE_TARBALL = "0"
IMAGE_FSTYPES:remove:sota = "${IMAGE_FSTYPES_REMOVE}"

# No need to install boot script, kernel image, devicetrees or firmware into the boot partition
IMAGE_BOOT_FILES = ""
IMAGE_BOOT_FILES_REMOVE:sota = "boot.scr-${MACHINE};boot.scr overlays.txt overlays/*;overlays/"
IMAGE_BOOT_FILES_REMOVE:append:apalis-imx8 = " hdmitxfw.bin dpfw.bin"

DISTRO_FEATURES:append = " virtualization stateless-system"
DISTRO_FEATURES_REMOVE ?= "3g alsa irda pcmcia nfc ldconfig pulseaudio wayland x11 ptest multiarch vulkan"
DISTRO_FEATURES_REMOVE:remove:verdin-imx95 = "vulkan"
DISTRO_FEATURES_REMOVE:remove:toradex-smarc-imx95 = "vulkan"
DISTRO_FEATURES_REMOVE:append:imx-generic-bsp = " opengl"
DISTRO_FEATURES:remove = "${DISTRO_FEATURES_REMOVE}"

# No need for x11 even for native
DISTRO_FEATURES_NATIVE:remove:imx-generic-bsp = "x11"
DISTRO_FEATURES_NATIVESDK:remove:imx-generic-bsp = "x11"

# Note, enable or disable the useradd-staticids in a configured system,
# the TMPDIR/DEPLOY_DIR/SSTATE_DIR may contain incorrect uid/gid values.
# Clearing them will correct this condition.
USERADDEXTENSION = "useradd-staticids"
USERADD_UID_TABLES = "files/torizon-static-passwd"
USERADD_GID_TABLES = "files/torizon-static-group"
USERADD_ERROR_DYNAMIC = "skip"

BBMASK += " \
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
TOS_SECBOOT_REQUIRED_BOOTARGS:qemuarm64 ?= ""
# Override BSP configuration:
TDX_SECBOOT_REQUIRED_BOOTARGS:tdx-signed = "${TOS_SECBOOT_REQUIRED_BOOTARGS}"
