# Torizon configuration for signed images

# Inherit class to sign BSP related images (bootloader, kernel FIT image).
inherit tdx-signed

# Inherit class responsible for the integrity and authenticity of the rootfs
# when using ostree (based on composefs).
inherit cfs-signed
