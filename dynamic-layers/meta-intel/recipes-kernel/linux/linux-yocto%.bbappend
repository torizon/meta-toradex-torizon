FILESEXTRAPATHS:prepend:intel-x86-common := "${THISDIR}/files:"

SRC_URI:append:intel-x86-common = " \
    file://intel-corei7-64.cfg \
"

# Match linux-intel's former KERNEL_EXTRA_FEATURES (beyond linux-yocto's
# netfilter default): security hardening fragments and the Intel NPU driver.
KERNEL_EXTRA_FEATURES:append:intel-x86-common = " \
    features/security/security.scc \
    features/intel-npu/intel-npu.scc \
"

# torizon.cfg sets CONFIG_KERNEL_LZ4, which needs the host lz4 tool to
# compress vmlinux (arch/x86/boot/compressed).
DEPENDS:append:intel-x86-common = " lz4-native"
