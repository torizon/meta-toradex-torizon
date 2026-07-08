require recipes-bsp/u-boot/u-boot-fuse.inc
require recipes-bsp/u-boot/u-boot-rollback.inc
require recipes-bsp/u-boot/u-boot-tcb-sign.inc

# Path for the include file that genreates U-Boot OTA necessary JSON file
U_BOOT_OTA_INCLUDE = "recipes-bsp/u-boot/u-boot-ota.inc"
# Since U-Boot OTA is not available for TI devices, we can't include this file
# otherwise we get a build error.
U_BOOT_OTA_INCLUDE:ti-soc = ""

require ${U_BOOT_OTA_INCLUDE}
