FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://0001-Implement-support-for-the-Torizon-boot.patch;patchdir=../edk2-nvidia \
    file://0002-L4TLauncher-apply-overlays.txt-overlays-in-Torizon-b.patch;patchdir=../edk2-nvidia \
    file://0003-L4TLauncher-skip-device-tree-overlays-that-fail-to-a.patch;patchdir=../edk2-nvidia \
"
