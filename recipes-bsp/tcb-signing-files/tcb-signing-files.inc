# Definitions shared by everything that handles the "TCB signing files" tarball:
# the intermediate, pre-signing bootloader artifacts that TorizonCore Builder
# needs to re-sign a pre-built Torizon OS image. The tcb-signing-files recipe
# builds and deploys it, image_type_torizon.bbclass puts it in the Tezi image,
# and u-boot-tcb-sign.inc deploys what it packs.

# Name of the tarball, as deployed and as seen inside the Tezi image.
TCB_SIGNING_FILES_TARBALL = "tcb_signing_files.tar.gz"

TCB_SIGNING_FILES_RECIPE = "tcb-signing-files"

# Root of the export tree, for machines that export a tree instead of loose
# files. The U-Boot recipe installs into it and the file list packs it, so the
# name lives here rather than being repeated on both sides.
TCB_SIGNING_EXPORT_DIR ?= "tcb-signing"

def is_hab_signed_bootloader_and_fit_enabled(d):
    if d.getVar('TDX_IMX_HAB_ENABLE') == '1' and d.getVar('UBOOT_SIGN_ENABLE') == '1':
        return '1'

    return '0'

# The TI K3 counterpart of the above. A separate function so that each names the
# condition it tests, and the k3r5 multiconfig needs a third form anyway.
def is_k3_secboot_and_fit_enabled(d):
    if d.getVar('TDX_K3_SECBOOT_ENABLE') == '1' and d.getVar('UBOOT_SIGN_ENABLE') == '1':
        return '1'

    return '0'

# The same condition as the k3r5 multiconfig sees it. That multiconfig builds no
# FIT, so requiring UBOOT_SIGN_ENABLE would switch its half of the export off
# for good. When the two disagree, its artifacts are deployed and its U-Boot
# workdir is kept, but nothing packs them.
def is_k3_secboot_enabled(d):
    if d.getVar('TDX_K3_SECBOOT_ENABLE') == '1':
        return '1'

    return '0'

# Whether this build exports the TCB signing files at all. What the condition
# reads comes from the globally inherited signing class, so a recipe and an
# image resolve it the same way.
TCB_SIGNING_SUPPORT ?= "0"
TCB_SIGNING_SUPPORT:verdin-imx8mp ?= "${@is_hab_signed_bootloader_and_fit_enabled(d)}"
TCB_SIGNING_SUPPORT:verdin-imx8mm ?= "${@is_hab_signed_bootloader_and_fit_enabled(d)}"
TCB_SIGNING_SUPPORT:verdin-am62 ?= "${@is_k3_secboot_and_fit_enabled(d)}"
TCB_SIGNING_SUPPORT:verdin-am62-k3r5 ?= "${@is_k3_secboot_enabled(d)}"
TCB_SIGNING_SUPPORT:verdin-am62p ?= "${@is_k3_secboot_and_fit_enabled(d)}"
TCB_SIGNING_SUPPORT:verdin-am62p-k3r5 ?= "${@is_k3_secboot_enabled(d)}"

# Hash by the value this resolves to: otherwise every task reading it also
# depends on the helper's text, which BitBake captures down to the comments
# that follow it.
TCB_SIGNING_SUPPORT[vardepvalue] = "${TCB_SIGNING_SUPPORT}"

# The machines that pack a tarball. The packing recipe's COMPATIBLE_MACHINE and
# the hold at the end of this file both derive from this list, so neither can
# name a machine the other does not; each entry also needs a file list below.
TCB_SIGNING_PACK_MACHINES ?= "verdin-imx8mp verdin-imx8mm verdin-am62 verdin-am62p"

# What goes into the tarball, relative to DEPLOY_DIR_IMAGE. The shell expands
# these in that directory, so globs work and an entry matching nothing fails
# the build rather than producing a short tarball.
TCB_SIGNING_FILELIST ?= ""
TCB_SIGNING_FILELIST:verdin-imx8mp ?= "uboot_config bl31* lpddr4_pmu_train_* u-boot.dtb u-boot-nodtb.bin spl/ u-boot-dtbs/"
TCB_SIGNING_FILELIST:verdin-imx8mm ?= "uboot_config bl31* lpddr4_pmu_train_* u-boot.dtb u-boot-nodtb.bin spl/ u-boot-dtbs/"

# The K3 machines pack the tree whole: their two multiconfigs contribute
# artifacts with the same basenames, so a glob list of loose files cannot
# separate them.
TCB_SIGNING_FILELIST:verdin-am62 ?= "${TCB_SIGNING_EXPORT_DIR}/"
TCB_SIGNING_FILELIST:verdin-am62p ?= "${TCB_SIGNING_EXPORT_DIR}/"

# The do_deploy tasks that put those files in DEPLOY_DIR_IMAGE. Keep this in
# step with TCB_SIGNING_FILELIST: a file packed but not depended upon is a race.
TCB_SIGNING_INPUT_DEPENDS ?= ""
TCB_SIGNING_INPUT_DEPENDS:verdin-imx8mp ?= "virtual/bootloader ${IMX_DEFAULT_ATF_PROVIDER} ${IMX_EXTRA_FIRMWARE}"
TCB_SIGNING_INPUT_DEPENDS:verdin-imx8mm ?= "virtual/bootloader ${IMX_DEFAULT_ATF_PROVIDER} ${IMX_EXTRA_FIRMWARE}"

# One producer for the K3 machines, not the four their artifacts come from: the
# U-Boot recipe installs the whole export tree out of its own workdir and
# sysroot.
TCB_SIGNING_INPUT_DEPENDS:verdin-am62 ?= "virtual/bootloader"
TCB_SIGNING_INPUT_DEPENDS:verdin-am62p ?= "virtual/bootloader"

# Inputs built in another multiconfig, which TCB_SIGNING_INPUT_DEPENDS cannot
# express: an mcdepends entry has to name the multiconfigs it crosses.
TCB_SIGNING_INPUT_MCDEPENDS ?= ""
TCB_SIGNING_INPUT_MCDEPENDS:verdin-am62 ?= "mc::k3r5:virtual/bootloader:do_deploy"
TCB_SIGNING_INPUT_MCDEPENDS:verdin-am62p ?= "mc::k3r5:virtual/bootloader:do_deploy"

# Every recipe whose deployed files are packed adds this to do_rm_work[depends]:
# DEPLOY_DIR_IMAGE serves them as hardlinks into that recipe's WORKDIR, which
# rm_work is otherwise free to remove while the tarball is still being packed.
# Naming a machine the packing recipe is not built for would leave every
# producer depending on a recipe nothing provides, so the test is the same list
# that recipe's COMPATIBLE_MACHINE comes from; with the export off it must not
# enter the build at all, which is the other half.
TCB_SIGNING_PACK_DEPENDS ?= "${@d.getVar('TCB_SIGNING_FILES_RECIPE') + ':do_compile' if d.getVar('TCB_SIGNING_SUPPORT') == '1' and d.getVar('MACHINE') in d.getVar('TCB_SIGNING_PACK_MACHINES').split() else ''}"
