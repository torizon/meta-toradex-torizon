OSTREE_KERNEL_ARGS:append:sota = " console=ttyS0,115200 earlycon root=LABEL=otaroot rootfstype=ext4 cma=524288000@1556086784"

DISTRO_FEATURES:append = " tpm2"

IMAGE_INSTALL:append = " \
    imgtec-pvr-firmware \
    kernel-module-bluetooth-lpm \
    kernel-module-bluetooth-rfkill \
    kernel-module-hl7593 \
    kernel-module-hrx-v4l2 \
    kernel-module-pvrsrvkm \
    kernel-module-rpi-panel-attiny-regulator \
    linux-firmware-syna \
    optee-client \
    optee-examples \
    optee-os-tadevkit \
    optee-test \
    pciutils \
    synasdk-brcm-bt-start \
    synasdk-drm-factory-ca-program \
    synasdk-factory-ta \
    synasdk-macaddr \
    synasdk-synap-module \
    synasdk-v4l2isp-daemon \
    synasdk-vpu-plugin \
    syna-wifi-tools \
"

SYNA_SDK_CONFIG_NAME = "sl1680_poky_aarch64_rdk"
SYNA_SDK_CONFIG_FILE = "sl1680_poky_aarch64_rdk_defconfig"

UBOOT_BOOT_PARTITION_NUMBER = "6"

hostname:pn-base-files = "torizon-sl1680"

DISTRO_FEATURES_REMOVE:remove = "vulkan"

require sl-common.inc

do_image_ota_ext4[depends] += "syna2img-tools:do_deploy"
