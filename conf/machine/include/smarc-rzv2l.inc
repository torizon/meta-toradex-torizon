OSTREE_KERNEL_ARGS:sota:append = "console=ttySC0,115200 earlycon root=LABEL=otaroot rootfstype=ext4"

DISTRO_FEATURES += " acl debuginfod ext2 ipv4 ipv6 usbgadget usbhost wifi xattr nfs zeroconf pci vfat seccomp"

PREFERRED_PROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_RPROVIDER_u-boot-fw-utils = "libubootenv"
PREFERRED_PROVIDER_u-boot-default-script = "u-boot-distro-boot"
PREFERRED_PROVIDER_u-boot-default-script:sota = "u-boot-distro-boot"
IMAGE_BOOT_FILES:append = " boot.scr-${MACHINE};boot.scr"
WKS_FILE_DEPENDS:append = " u-boot-default-script"

hostname:pn-base-files = "torizon-rzv2l"

PREFERRED_PROVIDER_virtual/dtb = ""
