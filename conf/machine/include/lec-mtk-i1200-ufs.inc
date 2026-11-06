# Torizon integration overlay for the Adlink LEC-MTK-i1200 (Genio 1200 / MT8395),
# UFS boot variant. Auto-included for MACHINE=lec-mtk-i1200-ufs via the local.conf
# template, on top of the Adlink vendor machine (meta-adlink-mtk).

require common-mtk.inc

hostname:pn-base-files = "torizon-lec-mtk-i1200-ufs"

# Memory sizes with a conf-memory-<size>.dtbo in the FIT image: one per
# memory-<size>.dts in meta-adlink-mtk (recipes-kernel/dtbo/lec-mtk-i1200/,
# patched by this layer's dtbo_%.bbappend).
# Add a size here only together with its overlay: booting a conf the FIT image
# does not contain fails the boot, and that cannot be recovered remotely.
MEMORY_OVERLAYS ?= "2G 4G 8G"

# Apply the memory overlay Adlink's U-Boot selects from the SKU pins. U-Boot puts
# it into boot_conf (e.g. "<NULL>#conf-memory-8G.dtbo#conf-temp-er.dtbo"), but
# Torizon's boot script only uses fitconf_fdt_overlays, so append each
# conf-memory-<size>.dtbo from MEMORY_OVERLAYS that boot_conf contains. Anything
# else leaves the overlay unset, i.e. the 4 GiB default.
UENV_EXTRA_CONFIGS:append = " ; for memsz in ${MEMORY_OVERLAYS} ; do if setexpr memconf sub "conf-memory-${memsz}.dtbo" "" "${boot_conf}" ; then env set fitconf_fdt_overlays "${fitconf_fdt_overlays}\#conf-memory-${memsz}.dtbo" ; fi ; done ; env set memconf"

# Kernel command line for the OSTree / OTA rootfs. Console is uart0 (ttyS0) at
# 921600, matching the board DTS stdout-path and U-Boot.
#
# Overrides (not appends to) torizon.bbclass's shared OSTREE_KERNEL_ARGS
# default: routes console output to fbcon and disables the Plymouth splash so
# it doesn't obscure the login prompt.
OSTREE_KERNEL_ARGS:sota:lec-mtk1200 = "logo.nologo vt.global_cursor_default=0 plymouth.enable=0 nosplash fbcon=map:0 console=ttyS0,921600 console=tty1 earlycon root=LABEL=otaroot rootfstype=ext4 rootwait"

# The board carries a TPM2.0.
DISTRO_FEATURES:append = " tpm2"

# Build and deploy host-side genio2img/img2genio torizoncore-builder
# converters alongside the image.
EXTRA_IMAGEDEPENDS:append = " genio2img-tools"

# RTL8821AU USB Wi-Fi driver (recipes-kernel/rtw88); named by PN since
# only the kernel recipe declares PACKAGES_DYNAMIC for kernel-module-*.
IMAGE_INSTALL:append = " rtw88"

# Socket-tied naming for USB wireless interfaces (recipes-bsp/usb-wifi-naming);
# the name must be fixed to the SMARC socket, identical on every unit.
IMAGE_INSTALL:append = " usb-wifi-naming"
