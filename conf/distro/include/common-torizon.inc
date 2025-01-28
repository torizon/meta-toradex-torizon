require conf/distro/poky.conf
require base-distro.inc

IMAGE_CLASSES:append = " image_type_common_torizon"

# Override shared by all torizon distros
DISTROOVERRIDES .= ":common-torizon-distro"

# We do not need teziimg, ota-ext4 or wic images by default
IMAGE_FSTYPES_REMOVE ?= "wic.gz wic.bmap wic.xz"
BBMASK += " \
    /meta-arm/recipes-test/pacbti/test-pacbti.bb \
"

OSTREE_DEPLOY_DEVICETREE = "0"

require conf/distro/include/versioning.inc

SDK_VENDOR = "-tznsdk"
SDK_NAME_PREFIX = "${DISTRO}"
# do not use elements in SDK_VERSION which are in vardepsexclude
SDK_VERSION = "${TDX_RELEASE}"

TARGET_VENDOR = "-tzn"

# create etc/build in the rootfs
INHERIT += "image-buildinfo"

# Log information on images and packages
INHERIT += "buildhistory"
BUILDHISTORY_COMMIT = "1"

IMAGE_LINGUAS ?= "en-us"

# Some Toradex recipes like linux-toradex, u-boot-toradex,
# device-tree-overlays are appending SCM hashes to PV, version
# numbers going backwards are not a problem as we don't provide
# binary feeds.
#
# This avoids some annoying errors as follows:
# | QA Issue: Package version went backwards which would break package feeds
ERROR_QA:remove = "version-going-backwards"

DISTRO_FEATURES:append = " pam systemd usrmerge"
DISTRO_FEATURES:remove = "sysvinit ptest"
VIRTUAL-RUNTIME_init_manager = "systemd"
PACKAGECONFIG:append:pn-systemd = " serial-getty-generator"
PACKAGECONFIG:append:pn-qemu-native = " libusb"

PREFERRED_RPROVIDER_network-configuration = "networkmanager"

# keep the dtb vendor directories in fitimage node names
KERNEL_DTBVENDORED = "1"

# Defines the root home directory as /root, as required by systemd.
ROOT_HOME = "/root"

# This function changes the default tune for machines which
# are based on armv7a or armv7ve to use common tune value, note
# that we enforce hard-float which is default on Linux microPlatform.
# If you have one of those machines which are armv7a or armv7ve but
# can't support hard-float, please change tune = 'armv7athf' to
# tune = 'armv7at' below but then this is for your own distro.

def at_least_one(elements, list):
    for i in list:
        if i in elements:
            return True
    return False

def arm_tune_handler(d):
    armv7athf_tune_features = ['armv7a', 'armv7ve', 'cortexa5', 'cortexa7', 'cortexa9', 'cortexa15']
    armv8a_tune_features = ['armv8a', 'crc']
    features = d.getVar('TUNE_FEATURES').split()
    if at_least_one(armv7athf_tune_features, features):
        tune = 'armv7athf'
        if 'bigendian' in features:
            tune += 'b'
        if 'vfpv3' in features:
            tune += '-vfpv3'
        if 'vfpv3d16' in features:
            tune += '-vfpv3d16'
        if 'neon' in features:
            tune += '-neon'
        if 'vfpv4' in features:
            tune += '-vfpv4'
    elif at_least_one(armv8a_tune_features, features):
        # all currently used i.MX 8 members support the following
        tune = 'armv8a-crypto'
    else:
        tune = d.getVar('DEFAULTTUNE')
    return tune

DEFAULTTUNE:tzn := "${@arm_tune_handler(d)}"
