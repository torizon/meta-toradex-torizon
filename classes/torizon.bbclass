# TorizonCore configuration

# Additional boot arguments are prepended by the U-Boot distro boot
# boot flow in boot.cmd.
#
# This boot arguments are supplied to OSTree deploy command. To
# change kernel boot arguments in a deployed OSTree use:
# ostree admin deploy --karg-none --karg="newargs" ...
OSTREE_KERNEL_ARGS = "quiet logo.nologo vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fbcon=map:3"

# When composefs support is enabled the root mount will usually be an overlay
# and the systemd-gpt-auto-generator generator will complain because it cannot
# determine the block device associated with that mount; disable the generator
# to avoid warning messages during boot.
OSTREE_KERNEL_ARGS:append:cfs-support = " systemd.gpt_auto=0"

OSTREE_KERNEL = "${@'${KERNEL_IMAGETYPE}-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}' if d.getVar('KERNEL_IMAGETYPE') == 'fitImage' else '${KERNEL_IMAGETYPE}'}"
OSTREE_DEPLOY_DEVICETREE = "${@'0' if d.getVar('KERNEL_IMAGETYPE') == 'fitImage' else '1'}"

# Cross machines / BSPs
## Drop IMX BSP that is not needed
MACHINE_EXTRA_RRECOMMENDS:remove:imx = "imx-alsa-plugins"

# SDK_VERSION should not contain ${TDX_PRERELEASE} (DATETIME),
# or else it might lead to mismatch paths when installing
# target packages and host packages.
SDK_VERSION = "${TDX_RELEASE}"

# Needed by inputting sudo password on build machine
HOSTTOOLS:cfs-signed = " zenity"
