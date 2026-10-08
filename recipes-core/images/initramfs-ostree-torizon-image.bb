DESCRIPTION = "Torizon OS OSTree initramfs image"

PACKAGE_INSTALL = "initramfs-framework-base initramfs-module-udev \
    initramfs-module-rootfs initramfs-module-debug initramfs-module-ostree \
    initramfs-module-plymouth ${VIRTUAL-RUNTIME_base-utils} base-passwd \
    initramfs-module-kmod"

PACKAGE_INSTALL:append:cfs-support = "\
    initramfs-module-composefs \
"

# Additional firmware needed for splash screen on DisplayPort with Aquila AM69
PACKAGE_INSTALL:append:aquila-am69 = "\
    cadence-mhdp-fw \
"

# The SDMA controller and cfg80211 both ask for firmware while the initramfs is
# still the root, so it has to be here: a request the kernel cannot satisfy
# directly is not retried once the real rootfs appears.
PACKAGE_INSTALL:append:common-imx6 = "\
    firmware-imx-sdma-imx6q \
    wireless-regdb-static \
"

SYSTEMD_DEFAULT_TARGET = "initrd.target"

IMAGE_NAME_SUFFIX = ""
# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = "splash"

export IMAGE_BASENAME = "initramfs-ostree-torizon-image"
IMAGE_LINGUAS = ""

LICENSE = "MIT"

IMAGE_FSTYPES = "cpio.gz"
IMAGE_FSTYPES:remove = "wic wic.gz wic.bmap wic.vmdk wic.vdi ext4 ext4.gz teziimg"

IMAGE_CLASSES:remove = "image_type_torizon image_types_ostree image_types_ota image_repo_manifest license_image qemuboot"

# avoid circular dependencies
EXTRA_IMAGEDEPENDS = ""

inherit core-image nopackages

IMAGE_ROOTFS_SIZE = "8192"

# Users will often ask for extra space in their rootfs by setting this
# globally.  Since this is a initramfs, we don't want to make it bigger
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_OVERHEAD_FACTOR = "1.0"

BAD_RECOMMENDATIONS += "busybox-syslog"

# Remove post-process commands inherited from torizon_base_image_type.inc that
# are irrelevant for the initramfs (containers, OTA, and os-release tweaks).
# This also suppresses the warning about the missing IMAGE_VARIANT with
# initramfs.
ROOTFS_POSTPROCESS_COMMAND:remove = "\
    adjust_container_engines; \
    gen_bootloader_ota_files; \
    tweak_os_release_variant; \
"
