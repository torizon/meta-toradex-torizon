# Definitions shared by both sides of the "TCB signing files" tarball: the
# intermediate, pre-signing bootloader artifacts that TorizonCore Builder needs
# to re-sign a pre-built Torizon OS image. The tcb-signing-files recipe builds
# and deploys it; classes/image_type_torizon.bbclass puts it in the Tezi image.

# Name of the tarball, as deployed and as seen inside the Tezi image.
TCB_SIGNING_FILES_TARBALL = "tcb_signing_files.tar.gz"

TCB_SIGNING_FILES_RECIPE = "tcb-signing-files"

def is_hab_signed_bootloader_and_fit_enabled(d):
    if d.getVar('TDX_IMX_HAB_ENABLE') == '1' and d.getVar('UBOOT_SIGN_ENABLE') == '1':
        return '1'

    return '0'

# Whether this build exports the TCB signing files at all. What the condition
# reads comes from the globally inherited signing class, so a recipe and an
# image resolve it the same way.
TCB_SIGNING_SUPPORT ?= "0"
TCB_SIGNING_SUPPORT:verdin-imx8mp ?= "${@is_hab_signed_bootloader_and_fit_enabled(d)}"
TCB_SIGNING_SUPPORT:verdin-imx8mm ?= "${@is_hab_signed_bootloader_and_fit_enabled(d)}"

# What goes into the tarball, relative to DEPLOY_DIR_IMAGE. The shell expands
# these in that directory, so globs work and an entry matching nothing fails
# the build rather than producing a short tarball.
TCB_SIGNING_FILELIST ?= ""
TCB_SIGNING_FILELIST:verdin-imx8mp ?= "uboot_config bl31* lpddr4_pmu_train_* u-boot.dtb u-boot-nodtb.bin spl/ u-boot-dtbs/"
TCB_SIGNING_FILELIST:verdin-imx8mm ?= "uboot_config bl31* lpddr4_pmu_train_* u-boot.dtb u-boot-nodtb.bin spl/ u-boot-dtbs/"

# The do_deploy tasks that put those files in DEPLOY_DIR_IMAGE. Keep this in
# step with TCB_SIGNING_FILELIST: a file packed but not depended upon is a race.
TCB_SIGNING_INPUT_DEPENDS ?= ""
TCB_SIGNING_INPUT_DEPENDS:verdin-imx8mp ?= "virtual/bootloader ${IMX_DEFAULT_ATF_PROVIDER} ${IMX_EXTRA_FIRMWARE}"
TCB_SIGNING_INPUT_DEPENDS:verdin-imx8mm ?= "virtual/bootloader ${IMX_DEFAULT_ATF_PROVIDER} ${IMX_EXTRA_FIRMWARE}"
