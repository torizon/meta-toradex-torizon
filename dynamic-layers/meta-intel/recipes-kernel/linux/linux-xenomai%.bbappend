LINUX_VERSION_EXTENSION:prepend = "-intel-pk"

COMPATIBLE_MACHINE ?= "(intel-corei7-64|intel-core2-32)"

KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "" ,d)}"

KCONFIG_REPO:intel-corei7-64 = "git://git.yoctoproject.org/yocto-kernel-cache"

# Add patches from
# https://github.com/YoeDistro/meta-intel/blob/scarthgap/recipes-kernel/linux/linux-intel.inc
TDX_PATCHES:append = " \
    file://fix-perf-reproducibility.patch \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-.patch \
    file://0002-mconf-fix-output-of-cflags-and-libraries.patch \
"
