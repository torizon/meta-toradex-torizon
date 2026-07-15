SUMMARY = "AUDK Ext4Dxe UEFI filesystem driver (systemd-boot)"
DESCRIPTION = "Builds ImageTool (native host tool) then cross-compiles \
Ext4Pkg/Ext4Dxe.efi from acidanthera/audk and deploys it as a \
systemd-boot driver onto the EFI System Partition."
HOMEPAGE = "https://github.com/acidanthera/audk"

LICENSE = "BSD-2-Clause-Patent & BSD-3-Clause"
LIC_FILES_CHKSUM = "\
    file://License.txt;md5=2b415520383f7964e96700ae12b4570a \
    file://OpenCorePkg/LICENSE.txt;md5=628933781c2977be7031d9cbe30b2947 \
"

SRC_URI = "gitsm://github.com/acidanthera/audk.git;protocol=https;branch=audk-stable-202511"
SRCREV  = "a77501f29dccb99679a92efb39783414ca700fb3"
S = "${WORKDIR}/git"

COMPATIBLE_HOST = "x86_64.*"

DEPENDS = "nasm-native python3-native util-linux-native"

inherit deploy nopackages

do_compile() {
    cd ${S}

    unset CFLAGS CXXFLAGS LDFLAGS
    oe_runmake -C ${S}/BaseTools/ImageTool \
        CC="${BUILD_CC}" \
        WERROR=0 \
        DEBUG=0 \
        UDK_PATH=${S} \
        OC_USER=${S}/OpenCorePkg

    . ${S}/edksetup.sh

    export GCC_BIN="${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}"
    export GCC_X64_PREFIX="${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}"

    build \
        -p Ext4Pkg/Ext4Pkg.dsc \
        -a X64 \
        -t GCC \
        -b RELEASE \
        -m Ext4Pkg/Ext4Dxe/Ext4Dxe.inf
}

do_deploy() {
    install -m 0644 \
        ${S}/Build/Ext4Pkg/RELEASE_GCC/X64/Ext4Dxe.efi \
        ${DEPLOYDIR}/ext4_x64.efi
}
addtask deploy after do_compile before do_build
