LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DESCRIPTION:append:experimental = " This release is not a officially supported \
build of Torizon OS, and is labeled experimental because of limited hardware \
constraints such as storage or memory, or include software we are still evaluating."

IMAGE_FEATURES += "ssh-server-openssh bash-completion-pkgs"

# Enough free space for a full image update
IMAGE_OVERHEAD_FACTOR = "2.3"

inherit ostree_layer_revision_info
inherit core-image

do_rootfs[cleandirs] += "${IMAGE_ROOTFS}"

TEZI_IMAGE_NAME = "${IMAGE_BASENAME}${IMAGE_BASENAME_SUFFIX}-${MACHINE}"
IMAGE_NAME = "${IMAGE_BASENAME}${IMAGE_BASENAME_SUFFIX}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
IMAGE_LINK_NAME = "${IMAGE_BASENAME}${IMAGE_BASENAME_SUFFIX}-${MACHINE}"

IMAGE_FEATURES += "splash"

SPLASH = "plymouth"

# Base packages
CORE_IMAGE_BASE_INSTALL:append = " \
    cpupower \
    curl \
    ethtool \
    evtest \
    fastfetch \
    fluentbit \
    htop \
    i2c-tools \
    iproute2 \
    iproute2-ip \
    jq \
    kernel-devsrc \
    kernel-modules \
    ldd \
    less \
    libpwquality \
    mmc-utils \
    nano \
    networkmanager \
    openssh-sftp-server \
    ostree-customize-plymouth \
    ostree-devicetree-overlays \
    packagegroup-core-full-cmdline-multiuser \
    packagegroup-core-full-cmdline-utils \
    parted \
    rsync \
    sshfs-fuse \
    sudo \
    systemd-analyze \
    torizon-conf \
    torizon-users \
    tzdata \
    usermount \
    vim-tiny \
    wireguard-tools \
    zram \
"

CORE_IMAGE_BASE_INSTALL:append:tdx = " \
    alsa-ucm-conf-tdx \
    set-hostname \
    tdx-info \
    udev-toradex-rules \
"

CORE_IMAGE_BASE_INSTALL:append:common-torizon-distro = " \
    resize-helper \
"

# SOTA related packages
CORE_IMAGE_BASE_INSTALL:append:sota = " \
    auto-provisioning \
    rac \
    tzn-mqtt \
"

# Packages that install firmwares/kernel modules that must be present
# in the rootfs. Firmware blobs, generally speaking, cannot be loaded
# from containers as their kernel module counterparts generally expect
# the files to be in an specific directory (generally /lib/firmware)

CORE_IMAGE_BASE_INSTALL:append:mx8-nxp-bsp = " \
    kernel-module-imx-gpu-viv \
"

CORE_IMAGE_BASE_INSTALL:append:am62xx = " \
    ti-img-rogue-driver \
"

CORE_IMAGE_BASE_INSTALL:append:verdin-am62p = " \
    ti-img-rogue-driver \
"

CORE_IMAGE_BASE_INSTALL:append:aquila-am69 = " \
    ti-img-rogue-driver \
"

CORE_IMAGE_BASE_INSTALL:append:mx8mp-nxp-bsp = " \
    kernel-module-isp-vvcam \
"

CORE_IMAGE_BASE_INSTALL:append:mx95-nxp-bsp = " \
    ${@bb.utils.contains("DISTROOVERRIDES", "common-torizon-distro", "mali-imx", "",d)} \
"

CORE_IMAGE_BASE_INSTALL:append:verdin-imx95 = " \
    mali-imx \
"

CORE_IMAGE_BASE_INSTALL:append:toradex-smarc-imx95 = " \
    mali-imx \
"

nss_altfiles_set_users_groups () {
	# Make a temporary directory to be used by pseudo to find the real /etc/passwd,/etc/group
	pseudo_dir=${WORKDIR}/pseudo-rootfs${sysconfdir}
	override_dir=${IMAGE_ROOTFS}${sysconfdir}
	nsswitch_conf=${IMAGE_ROOTFS}${sysconfdir}/nsswitch.conf

	sed -i -e '/^passwd/s/$/ altfiles/' -e '/^group/s/$/ altfiles/' -e '/^shadow/s/$/ altfiles/' ${nsswitch_conf}

	install -d ${pseudo_dir}
	install -m 644 ${override_dir}/passwd ${pseudo_dir}
	install -m 644 ${override_dir}/group ${pseudo_dir}
	install -m 400 ${override_dir}/shadow ${pseudo_dir}
	cp -a ${pseudo_dir}/* ${IMAGE_ROOTFS}${libdir}

	for file in passwd group shadow; do
		cat > ${override_dir}/${file} <<- EOF
			# NSS altfiles module is installed. Default user, group and shadow files are in
			# /usr/lib/
		EOF
		grep -r torizon ${IMAGE_ROOTFS}${libdir}/${file} >> ${override_dir}/${file}
	done
}

# include nss-altfiles support
CORE_IMAGE_BASE_INSTALL:append = ' ${@bb.utils.contains("DISTRO_FEATURES", "stateless-system", "nss-altfiles", "",d)}'
IMAGE_PREPROCESS_COMMAND:append = ' ${@bb.utils.contains("DISTRO_FEATURES", "stateless-system", "nss_altfiles_set_users_groups; ", "",d)}'
PSEUDO_PASSWD:prepend = "${@bb.utils.contains('DISTRO_FEATURES', 'stateless-system', '${WORKDIR}/pseudo-rootfs:', '', d)}"

# due to limited hardware resources, remove Colibri iMX6 Solo 256MB
# from the list of supported IDs in the Tezi image
TORADEX_PRODUCT_IDS:remove:colibri-imx6 = "0014 0016"

CORE_IMAGE_BASE_INSTALL:append:cfs-signed = "\
    fsverity-utils \
"

CORE_IMAGE_BASE_INSTALL:append:cfs-support = "\
    composefs \
"
