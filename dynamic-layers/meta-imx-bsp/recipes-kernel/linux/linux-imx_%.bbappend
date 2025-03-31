require recipes-kernel/linux/linux-torizon.inc

# NXP's Kernel recipe uses this variable to manually append fragments
# to the generated .config.
DELTA_KERNEL_DEFCONFIG:append = "torizon.cfg"
