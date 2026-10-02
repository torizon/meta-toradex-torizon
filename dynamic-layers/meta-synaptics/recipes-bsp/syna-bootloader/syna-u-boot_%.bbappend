require recipes-bsp/u-boot/u-boot-rollback.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://extra-uboot-config.cfg \
"

# extra-uboot-config.cfg is merged straight from this layer's tree, so the
# per-machine boot partition can't be substituted into it; set
# CONFIG_BOOTCOMMAND in the merged .config instead.
do_configure:append () {
    sed -i '/^CONFIG_BOOTCOMMAND=/d' ${B}/.config
    echo 'CONFIG_BOOTCOMMAND="setenv devtype mmc; setenv devnum 0; ext4load ${devtype} ${devnum}:${UBOOT_BOOT_PARTITION_NUMBER} ${loadaddr} /boot.scr && source ${loadaddr};"' >> ${B}/.config
}
