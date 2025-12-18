TEGRA_ESSENTIAL_EXTRA_RDEPENDS:remove =  " \
  l4t-launcher-extlinux \
"

OSTREE_KERNEL_ARGS:append = " mminit_loglevel=4 console=ttyTCU0,115200 firmware_class.path=/etc/firmware nospectre_bhb console=tty0 root=/dev/${TNSPEC_BOOTDEV} rootfstype=ext4 rootwait"

IMAGE_TEGRAFLASH_KERNEL = "${DEPLOY_DIR_IMAGE}/tegra-minimal-initramfs-${MACHINE}.cboot"
IMAGE_TEGRAFLASH_FS_TYPE = "ota-ext4"

BBMASK += "meta-tegra/external/sota/recipes-bsp/uefi/edk2-firmware-tegra_%.bbappend"

do_image_tegraflash[depends] += " tegra-minimal-initramfs:do_image_complete"

MACHINE_EXTRA_RDEPENDS:remove = " tegra-redundant-boot"
