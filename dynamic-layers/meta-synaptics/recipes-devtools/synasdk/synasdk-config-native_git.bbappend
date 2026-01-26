# Patching the Kernel configurations to accept the different memory used
# in Toradex's SBC module

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:luna-sl1680 = " file://0001-synasdk-config-native-sl1680-select-ch0-memory-varia.patch;patchdir=./configs"
