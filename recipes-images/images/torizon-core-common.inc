LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DESCRIPTION:append:experimental = " This release is not a officially supported \
build of TorizonCore, and is labeled experimental because of limited hardware \
constraints such as storage or memory, or include software we are still evaluating."

IMAGE_FEATURES += "ssh-server-openssh bash-completion-pkgs"

# Enough free space for a full image update
IMAGE_OVERHEAD_FACTOR = "2.3"

inherit core-image

do_rootfs[cleandirs] += "${IMAGE_ROOTFS}"

TEZI_IMAGE_NAME = "${IMAGE_BASENAME}${IMAGE_BASENAME_SUFFIX}-${MACHINE}"
IMAGE_NAME = "${IMAGE_BASENAME}${IMAGE_BASENAME_SUFFIX}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
IMAGE_LINK_NAME = "${IMAGE_BASENAME}${IMAGE_BASENAME_SUFFIX}-${MACHINE}"

IMAGE_FEATURES += "splash"

SPLASH = "plymouth"

# Base packages
CORE_IMAGE_BASE_INSTALL:append = " \
    alsa-ucm-conf-tdx \
    cpufrequtils \
    curl \
    ethtool \
    evtest \
    fluent-bit \
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
    neofetch \
    networkmanager \
    openssh-sftp-server \
    ostree-customize-plymouth \
    ostree-devicetree-overlays \
    packagegroup-core-full-cmdline-multiuser \
    packagegroup-core-full-cmdline-utils \
    rsync \
    set-hostname \
    sshfs-fuse \
    sudo \
    systemd-analyze \
    torizon-conf \
    torizon-users \
    tzdata \
    udev-toradex-rules \
    usermount \
    vim-tiny \
    wireguard-tools \
    zram \
"

CORE_IMAGE_BASE_INSTALL:append:tdx = " \
    tdx-info \
"

# SOTA related packages
CORE_IMAGE_BASE_INSTALL:append:sota = " \
    auto-provisioning \
    rac \
"

CORE_IMAGE_BASE_INSTALL:append:mx8-nxp-bsp = " \
    kernel-module-imx-gpu-viv \
"

CORE_IMAGE_BASE_INSTALL:append:verdin-am62 = " \
    ti-img-rogue-driver \
"

CORE_IMAGE_BASE_INSTALL:append:mx8mp-nxp-bsp = " \
    kernel-module-isp-vvcam \
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
