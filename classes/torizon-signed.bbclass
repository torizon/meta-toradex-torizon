# TorizonCore configuration for signed images

# Inherit class to sign BSP related images (bootloader, kernel FIT image).
inherit tdx-signed

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
# - cfs-signed: TBD (not yet implemented)
#
# TODO: Get rid of remaining uses of override "torizon-signed".
DISTROOVERRIDES:append = ":cfs-support"
