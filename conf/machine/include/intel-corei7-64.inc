
WKS_FILE:intel-corei7-64:sota = "efiimage-sota.wks"
WKS_FILE_DEPENDS:remove = "tezi-metadata virtual/dtb"

OSTREE_KERNEL_ARGS:sota:append:intel-corei7-64 = "console=ttyS0,115200 root=LABEL=otaroot rootfstype=ext4 quiet logo.nologo vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fbcon=map:10 "

XENOMAI_KERNEL_ARGS = "audit=0 hpet=disable clocksource=tsc tsc=reliable intel_pstate=disable intel_idle.max_cstate=0 intel.max_cstate=0 processor.max_cstate=0 processor_idle.max_cstate=0 \
                    rcupdate.rcu_cpu_stall_suppress=1 mce=off nmi_watchdog=0 nosoftlockup noht numa_balancing=disable hugepages=1024 rcu_nocb_poll=1024 irqaffinity=0 isolcpus=1,3 rcu_nocbs=1,3 nohz_full=1,3 \
                    i915.enable_rc6=0 i915.enable_dc=0 i915.disable_power_well=0 "
OSTREE_KERNEL_ARGS:sota:append:intel-corei7-64:xenomai3 = " ${XENOMAI_KERNEL_ARGS} "
OSTREE_KERNEL_ARGS:sota:append:intel-corei7-64:xenomai4 = " ${XENOMAI_KERNEL_ARGS} "

OSTREE_BOOTLOADER:intel-corei7-64 = "grub"
OSTREE_DEPLOY_DEVICETREE:intel-corei7-64 = "0"
EFI_PROVIDER:intel-corei7-64 = "grub-efi"
SOTA_CLIENT_FEATURES:remove = "ubootenv"
UBOOT_BINARY_OTA_IGNORE:intel-corei7-64 = "1"

IMAGE_FSTYPES_REMOVE = "hddimg teziimg"
IMAGE_FSTYPES:append = " wic.vmdk wic.vdi"

hostname:pn-base-files = "torizon-x86"

PREFERRED_PROVIDER_u-boot-default-script = ""
PREFERRED_PROVIDER_virtual/dtb = ""

# Weak assignment so a local.conf override (before or after this include)
# wins; local.conf is fully parsed before meta-intel's machine conf
# ?= "linux-intel", so this still beats that default. See docs/README-x86.md.
PREFERRED_PROVIDER_virtual/kernel:intel-corei7-64 ?= "linux-yocto"
PREFERRED_VERSION_linux-yocto:intel-corei7-64 ?= "6.6%"

PREFERRED_PROVIDER_virtual/kernel:xenomai3 = "linux-xenomai-3"
PREFERRED_VERSION_linux-xenomai-3 ?= "5.10.%"

PREFERRED_PROVIDER_virtual/kernel:xenomai4 = "linux-xenomai-4"
PREFERRED_VERSION_linux-xenomai-4 = "5.10.%"

IMAGE_INSTALL:append = " grub-ota-fallback"
IMAGE_INSTALL:remove = " minnowboard-efi-startup network-configuration"
# Default linux-yocto inherits toradex-kernel-localversion (KERNEL_LOCALVERSION
# = -${TDX_VERSION}); these extensions apply only to the xenomai kernels.
LINUX_VERSION_EXTENSION:xenomai3 ?= "-torizon-${LINUX_KERNEL_TYPE}-xenomai3"
LINUX_VERSION_EXTENSION:xenomai4 ?= "-torizon-${LINUX_KERNEL_TYPE}-xenomai4"
