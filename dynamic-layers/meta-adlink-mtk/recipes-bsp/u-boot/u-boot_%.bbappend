# Restore the standard U-Boot distro boot flow so Torizon boots the OSTree way.
# The Adlink board-support patch repoints BOOTENV_BOOT_TARGETS to a single
# "embedded" target (a signed boot script reading a raw "kernel" partition);
# Torizon has no such partition, so revert it and let distro_bootcmd scan storage
# for the bootable rootfs and source /boot.scr. meta-toradex-torizon's higher
# BBFILE_PRIORITY means this patch lands after the vendor's and cleanly reverts it.
FILESEXTRAPATHS:prepend:lec-mtk1200 := "${THISDIR}/${PN}/lec-mtk1200:"

SRC_URI:append:lec-mtk1200 = " file://0001-torizon-restore-distro-boot_targets.patch"

# Accept the legacy-format boot.scr (FIT_SIGNATURE defaults LEGACY_IMAGE_FORMAT off).
SRC_URI:append:lec-mtk1200 = " file://legacy-image-format.cfg"
