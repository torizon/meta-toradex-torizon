# This class provides the rootfs protection based on composefs. This would
# normally be used together with other protections such as those provided by
# tdx-signed.bbclass (from meta-toradex-security).
#
# Users of Torizon OS would normally employ torizon-signed.bbclass in their
# projects to get a secure boot setup where all boot and rootfs artifacts are
# properly protected.

# Enable protection features related to the root filesystem; this is done by
# means of two overrides, namely:
#
# - cfs-support: Enable composefs support; when set, the ostree deployments will
#   contain a composefs image (by default) and the system will usually boot from
#   that image; however, the presence of the composefs image will not be
#   enforced at runtime. Unless that presence is enforced by other overrides,
#   the system will be capable of booting from a legacy ostree deployment (based
#   on hardlinks).
#
# - cfs-signed: Enable the actual ostree commit signing at build time (plus the
#   addition of the composefs digest into the commit metadata) and the signature
#   verification at runtime.
#
DISTROOVERRIDES:append = ":cfs-support:cfs-signed"

# Variables a user will likely want to set up:
#
# - CFS_GENERATE_KEYS: Set to "1" or "0" to enable or disable the composefs key
#   pair generation at build time.
#
# - CFS_SIGN_KEYDIR: Directory where the composefs keys are kept (retrieved from
#   or generated into when not found (if the key generation is enabled)).
#
# - CFS_SIGN_KEYNAME: Base name of the key files; the file holding the secret
#   key will be named ${CFS_SIGN_KEYNAME}.sec and the one with the public key
#   will be ${CFS_SIGN_KEYNAME}.pub.
#
CFS_GENERATE_KEYS ?= "1"
CFS_SIGN_KEYDIR ?= "${TOPDIR}/keys/ostree"
CFS_SIGN_KEYNAME ?= "cfs-dev"
