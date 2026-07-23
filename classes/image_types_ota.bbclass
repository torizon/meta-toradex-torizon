OTA_SYSROOT = "${WORKDIR}/ota-sysroot"
TAR_IMAGE_ROOTFS:task-image-ota = "${OTA_SYSROOT}"
IMAGE_TYPEDEP:ota = "ostreecommit"
do_image_ota[dirs] = "${OTA_SYSROOT}"
do_image_ota[cleandirs] = "${OTA_SYSROOT}"
do_image_ota[depends] = "${@'grub:do_populate_sysroot' if d.getVar('OSTREE_BOOTLOADER') == 'grub' else ''} \
                         ${@'virtual/bootloader:do_deploy' if d.getVar('OSTREE_BOOTLOADER') == 'u-boot' else ''} \
                         ${@'systemd-boot:do_deploy' if d.getVar('OSTREE_BOOTLOADER') == 'systemd-boot' else ''}"
IMAGE_CMD:ota () {
	ostree admin --sysroot=${OTA_SYSROOT} init-fs --modern ${OTA_SYSROOT}
	ostree admin --sysroot=${OTA_SYSROOT} os-init ${OSTREE_OSNAME}

	# Preparation required to steer ostree bootloader detection
	mkdir -p ${OTA_SYSROOT}/boot/loader.0
	ln -s loader.0 ${OTA_SYSROOT}/boot/loader

	if [ "${OSTREE_BOOTLOADER}" = "grub" ]; then
		# Used by ostree-grub-generator called by the ostree binary
		export OSTREE_BOOT_PARTITION=${OSTREE_BOOT_PARTITION}

		mkdir -p ${OTA_SYSROOT}/boot/grub2
		ln -s ../loader/grub.cfg ${OTA_SYSROOT}/boot/grub2/grub.cfg
	elif [ "${OSTREE_BOOTLOADER}" = "u-boot" ]; then
		touch ${OTA_SYSROOT}/boot/loader/uEnv.txt
	elif [ "${OSTREE_BOOTLOADER}" = "syslinux" ]; then
		mkdir -p ${OTA_SYSROOT}/boot/syslinux
		touch ${OTA_SYSROOT}/boot/loader/syslinux.cfg
		ln -s ../loader/syslinux.cfg ${OTA_SYSROOT}/boot/syslinux/syslinux.cfg
	elif [ "${OSTREE_BOOTLOADER}" = "systemd-boot" ]; then
		# Tell libostree not to invoke grub2-editenv (C3)
		ostree config --repo=${OTA_SYSROOT}/ostree/repo set sysroot.bootloader none
	elif [ "${OSTREE_BOOTLOADER}" = "none" ]; then
		ostree config --repo=${OTA_SYSROOT}/ostree/repo set sysroot.bootloader none
	else
		bbfatal "Invalid bootloader: ${OSTREE_BOOTLOADER}"
	fi

	# Apply generic configurations to the deployed repository; they are
	# specified as a series of "key:value ..." pairs.
	for cfg in ${OSTREE_OTA_REPO_CONFIG}; do
		ostree config --repo=${OTA_SYSROOT}/ostree/repo set \
		       "$(echo "${cfg}" | cut -d ":" -f1)" \
		       "$(echo "${cfg}" | cut -d ":" -f2-)"
	done

	ostree_target_hash=$(cat ${WORKDIR}/ostree_manifest)

	# Use OSTree hash to avoid any potential race conditions between
	# multiple builds accessing the same ${OSTREE_REPO}.
	ostree --repo=${OTA_SYSROOT}/ostree/repo pull-local --remote=${OSTREE_OSNAME} ${OSTREE_REPO} ${ostree_target_hash}
	kargs_list=""
	for arg in $(printf '%s' "${OSTREE_KERNEL_ARGS}"); do
		kargs_list="${kargs_list} --karg-append=${arg}"
	done

	# Create the same reference on the device we use in the archive OSTree
	# repo in ${OSTREE_REPO}. This reference will show up when showing the
	# deployment on the device:
	# ostree admin status
	# If a remote with the name ${OSTREE_OSNAME} is configured, this also
	# will allow to use:
	# ostree admin upgrade
	ostree --repo=${OTA_SYSROOT}/ostree/repo refs --create=${OSTREE_OSNAME}:${OSTREE_BRANCHNAME} ${ostree_target_hash}
	ostree admin --sysroot=${OTA_SYSROOT} deploy ${kargs_list} --os=${OSTREE_OSNAME} ${OSTREE_OSNAME}:${OSTREE_BRANCHNAME}

	if [ ${@ oe.types.boolean('${OSTREE_SYSROOT_READONLY}')} = True ]; then
		ostree config --repo=${OTA_SYSROOT}/ostree/repo set sysroot.readonly true
	fi

	cp -a ${IMAGE_ROOTFS}/var/sota ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/ || true
	# Create /var/sota if it doesn't exist yet
	mkdir -p ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/sota
	# Ensure the permissions are correctly set
	chmod 700 ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/sota

	cp -a ${IMAGE_ROOTFS}/var/local ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/ || true

	mkdir -p ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/rootdirs
	cp -a ${IMAGE_ROOTFS}/home ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/rootdirs/home || true

	# Ensure that /var/local exists (AGL symlinks /usr/local to /var/local)
	install -d ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/local
	# Set package version for the first deployment
	target_version=${ostree_target_hash}
	if [ -n "${GARAGE_TARGET_VERSION}" ]; then
		target_version=${GARAGE_TARGET_VERSION}
	elif [ -e "${STAGING_DATADIR_NATIVE}/target_version" ]; then
		target_version=$(cat "${STAGING_DATADIR_NATIVE}/target_version")
	fi
	mkdir -p ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/sota/import
	echo "{\"${ostree_target_hash}\":\"${GARAGE_TARGET_NAME}-${target_version}\"}" > ${OTA_SYSROOT}/ostree/deploy/${OSTREE_OSNAME}/var/sota/import/installed_versions

	# When the boot content is placed on a dedicated boot partition
	# (systemd-boot / XBOOTLDR), stage the ostree-generated /boot tree so it
	# can be packed into a separate ext4 image (boot-ext4), and empty the
	# /boot directory in the otaroot so it only serves as a mountpoint.
	if [ "${OSTREE_BOOTLOADER}" = "systemd-boot" ]; then
		rm -rf ${BOOT_STAGING}
		mkdir -p ${BOOT_STAGING}
		cp -a ${OTA_SYSROOT}/boot/. ${BOOT_STAGING}/
		rm -rf ${OTA_SYSROOT}/boot
		mkdir ${OTA_SYSROOT}/boot
	fi
}
BOOT_STAGING = "${WORKDIR}/boot-staging"

EXTRA_IMAGECMD:ota-ext4 ?= "-L otaroot -i 4096 -t ext4"
IMAGE_TYPEDEP:ota-ext4 = "ota"
IMAGE_ROOTFS:task-image-ota-ext4 = "${OTA_SYSROOT}"
IMAGE_CMD:ota-ext4 () {
	ln -sf ${STAGING_DIR_NATIVE}${base_sbindir_native}/mkfs.ext4 ${STAGING_DIR_NATIVE}${base_sbindir_native}/mkfs.ota-ext4
	ln -sf ${STAGING_DIR_NATIVE}${base_sbindir_native}/fsck.ext4 ${STAGING_DIR_NATIVE}${base_sbindir_native}/fsck.ota-ext4
	oe_mkext234fs ota-ext4 ${EXTRA_IMAGECMD}
}
do_image_ota_ext4[depends] += "e2fsprogs-native:do_populate_sysroot"
do_image_wic[depends] += "${@bb.utils.contains('IMAGE_FSTYPES', 'ota-ext4', '%s:do_image_ota_ext4' % d.getVar('PN'), '', d)}"

# Separate ext4 image holding the ostree-generated /boot tree, staged by the
# IMAGE_CMD:ota step above (BOOT_STAGING). Packed onto the XBOOTLDR partition.
EXTRA_IMAGECMD:boot-ext4 ?= "-L xbootldr -i 4096 -t ext4 -O ^has_journal"
IMAGE_TYPEDEP:boot-ext4 = "ota"
IMAGE_ROOTFS:task-image-boot-ext4 = "${BOOT_STAGING}"
# Size the boot-ext4 filesystem to exactly fill the 1024M XBOOTLDR partition
# (efiimage-sota-signed.wks --fixed-size=1024M) so /boot reports full capacity.
# 1048576 KiB = 1024 MiB; overhead factor 1 overrides torizon-base.inc 2.3.
IMAGE_ROOTFS_SIZE:task-image-boot-ext4 = "1048576"
IMAGE_OVERHEAD_FACTOR:task-image-boot-ext4 = "1"
IMAGE_ROOTFS_EXTRA_SPACE:task-image-boot-ext4 = "0"
IMAGE_CMD:boot-ext4 () {
	ln -sf ${STAGING_DIR_NATIVE}${base_sbindir_native}/mkfs.ext4 ${STAGING_DIR_NATIVE}${base_sbindir_native}/mkfs.boot-ext4
	ln -sf ${STAGING_DIR_NATIVE}${base_sbindir_native}/fsck.ext4 ${STAGING_DIR_NATIVE}${base_sbindir_native}/fsck.boot-ext4
	oe_mkext234fs boot-ext4 ${EXTRA_IMAGECMD}
}
do_image_boot_ext4[depends] += "e2fsprogs-native:do_populate_sysroot"
# Signed images use a dedicated XBOOTLDR partition; append boot-ext4 fstype.
IMAGE_FSTYPES:append:tdx-signed = " boot-ext4"
do_image_wic[depends] += "${@bb.utils.contains('IMAGE_FSTYPES', 'boot-ext4', '%s:do_image_boot_ext4' % d.getVar('PN'), '', d)}"

EXTRA_IMAGECMD:ota-btrfs ?= "-L otaroot -n 4096 --shrink"
IMAGE_TYPEDEP:ota-btrfs = "ota"
IMAGE_ROOTFS:task-image-ota-btrfs = "${OTA_SYSROOT}"
MIN_BTRFS_SIZE ?= "16384"
IMAGE_CMD:ota-btrfs () {
	# Pristine copy from
	# https://git.openembedded.org/openembedded-core/tree/meta/classes-recipe/image_types.bbclass#n103
	size=${ROOTFS_SIZE}
	if [ ${size} -lt ${MIN_BTRFS_SIZE} ] ; then
		size=${MIN_BTRFS_SIZE}
		bbwarn "Rootfs size is too small for BTRFS. Filesystem will be extended to ${size}K"
	fi
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.btrfs seek=${size} count=0 bs=1024
	mkfs.btrfs ${EXTRA_IMAGECMD} -r ${OTA_SYSROOT} ${IMGDEPLOYDIR}/${IMAGE_NAME}.btrfs
}
do_image_ota_btrfs[depends] += "btrfs-tools-native:do_populate_sysroot"
do_image_wic[depends] += "${@bb.utils.contains('IMAGE_FSTYPES', 'ota-btrfs', '%s:do_image_ota_btrfs' % d.getVar('PN'), '', d)}"
