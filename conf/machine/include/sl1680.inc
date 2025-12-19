OSTREE_KERNEL_ARGS:append:sota = " console=ttyS0,115200 earlycon root=LABEL=otaroot rootfstype=ext4 cma=524288000@1556086784"

DISTRO_FEATURES:append = " tpm2"

IMAGE_INSTALL:append = " \
    imgtec-pvr-firmware \
    kernel-module-berlin-chipid \
    kernel-module-berlin-ir \
    kernel-module-bluetooth-lpm \
    kernel-module-bluetooth-rfkill \
    kernel-module-dolphin-hwmon \
    kernel-module-dwc3-syna \
    kernel-module-hl7593 \
    kernel-module-hrx-v4l2 \
    kernel-module-pcie-berlin \
    kernel-module-phy-berlin-pcie \
    kernel-module-phy-syna-usb \
    kernel-module-pvrsrvkm \
    kernel-module-rpi-panel-attiny-regulator \
    kernel-module-sm \
    kernel-module-syna-axi-meter \
    kernel-module-tps6286x-regulator \
    linux-firmware-syna \
    optee-client \
    optee-os-tadevkit \
    optee-examples \
    optee-test \
    synasdk-macaddr \
    synasdk-brcm-bt-start \
    synasdk-v4l2isp-daemon \
    synasdk-vpu-plugin \
    synasdk-v4l2isp-qtapp \
    synasdk-synap-module \
    v4l-utils \
"

SYNA_SDK_CONFIG_NAME = "sl1680_poky_aarch64_rdk"
SYNA_SDK_CONFIG_FILE = "sl1680_poky_aarch64_rdk_defconfig"

UBOOT_BOOT_PARTITION_NUMBER = "6"

UENV_EXTRA_CONFIGS = "\
    env set skip_fdt_overlays 1; \
    env set fdtfile ${@ os.path.basename(d.getVar('SYNA_KERNEL_DTS_FILE'))}; \
    env set skip_fdt_update 0x2; \
    env set kernel_addr_r 0x7c00000; \
    env set ramdisk_addr_r 0xcf00000; \
    env set fdt_addr_r 0x10000000; \
    env set initrd_high 0xffffffffffffffff; \
"

hostname:pn-base-files = "torizon-sl1680"

require sl-common.inc
