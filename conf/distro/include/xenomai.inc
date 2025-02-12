
# Xenomai config

XENOMAI_KERNEL_ARGS = "audit=0 clocksource=arch_sys_counter nohz_full=1,3 \
                       irqaffinity=0 isolcpus=1,3 rcu_nocbs=1,3 rcu_nocb_poll=1024 \
                       nosoftlockup numa_balancing=disable hugepages=1024 "
OSTREE_KERNEL_ARGS:sota:append:verdin-imx8mp:xenomai4 = " ${XENOMAI_KERNEL_ARGS} "
OSTREE_KERNEL_ARGS:sota:append:verdin-imx8mp:xenomai3 = " ${XENOMAI_KERNEL_ARGS} "

PREFERRED_PROVIDER_virtual/kernel:xenomai4:upstream = "linux-xenomai-4"
PREFERRED_PROVIDER_virtual/dtb:xenomai4:upstream = "device-tree-overlays-mainline"
LINUX_VERSION_EXTENSION:xenomai4:upstream ?= "-torizon-${LINUX_KERNEL_TYPE}-xenomai4"

PREFERRED_PROVIDER_virtual/kernel:xenomai3:upstream = "linux-xenomai-3"
PREFERRED_PROVIDER_virtual/dtb:xenomai3:upstream = "device-tree-overlays-mainline"
LINUX_VERSION_EXTENSION:xenomai3:upstream ?= "-torizon-${LINUX_KERNEL_TYPE}-xenomai3"

# This is required to build the upstream device trees
IMX_DEFAULT_BSP:xenomai4:upstream = "mainline"
IMX_DEFAULT_BSP:xenomai3:upstream = "mainline"
