# Vendor identity (was inherited from meta-toradex-distro's tdx-base.inc)
TARGET_VENDOR = "-tdx"
SDK_VENDOR = "-tdxsdk"

# Poky defaults to sysvinit (its own weak default beats OE-core's even
# weaker ??= fallback); pin Torizon OS to systemd explicitly.
INIT_MANAGER = "systemd"

# Distro features Torizon OS needs beyond base-distro.inc's own set
DISTRO_FEATURES:append = " opencl pam systemd-resolved tpm2 pni-names"

PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_RPROVIDER_u-boot-fw-utils = "libubootenv"

# Default virtual/dtb provider for NXP i.MX8/i.MX9 vendor-BSP builds.
PREFERRED_PROVIDER_virtual/dtb ?= "device-tree-overlays-imx"

PREFERRED_PROVIDER_opencl-headers:imxgpu      = "imx-gpu-viv"
PREFERRED_PROVIDER_opencl-clhpp:imxgpu        = "imx-gpu-viv"
PREFERRED_PROVIDER_opencl-icd-loader:imxgpu   = "imx-gpu-viv"
PREFERRED_RPROVIDER_opencl-icd-loader:imxgpu  = "libopencl-imx"

PREFERRED_RPROVIDER_libnss-mdns = "avahi-libnss-mdns"

PACKAGECONFIG:append:pn-systemd = " serial-getty-generator"
PACKAGECONFIG:append:pn-qemu-native = " libusb"

# Show Tezi EULA license
TEZI_SHOW_EULA_LICENSE ?= "1"
TEZI_SHOW_EULA_LICENSE:use-mainline-bsp ?= "0"
TEZI_SHOW_EULA_LICENSE:colibri-imx6:use-mainline-bsp ?= "1"
TEZI_SHOW_EULA_LICENSE:apalis-imx6:use-mainline-bsp ?= "1"

# create etc/build in the rootfs
INHERIT += "image-buildinfo"

# Log information on images and packages
INHERIT += "buildhistory"
BUILDHISTORY_COMMIT = "1"

IMAGE_LINGUAS ?= "en-us"

# Some Toradex recipes like linux-toradex, u-boot-toradex,
# device-tree-overlays are appending SCM hashes to PV, version
# numbers going backwards are not a problem as we don't provide
# binary feeds.
#
# This avoids some annoying errors as follows:
# | QA Issue: Package version went backwards which would break package feeds
ERROR_QA:remove = "version-going-backwards"

# keep the dtb vendor directories in fitimage node names
KERNEL_DTBVENDORED = "1"