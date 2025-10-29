# Patching the paths to prebuilt libs since Synaptics uses on their prebuilt path
# the poky distro name, resulting in a path like: aarch64-poky-linux
# which is not valid for TorizonOS (aarch64-tzn-linux), or any other distro.

PREBUILT_LIBS = "sysroot/linux-baseline/data/gfx_prebuilt/imagination/${DISPLAY_SERVER}/${SYNAMACH}/aarch64-poky-linux/lib/"
PREBUILT_BINS = "sysroot/linux-baseline/data/gfx_prebuilt/imagination/${DISPLAY_SERVER}/${SYNAMACH}/aarch64-poky-linux/bin/"
