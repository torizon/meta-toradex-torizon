# The lpddr4_pmu_train_* this recipe deploys is packed into the TCB signing
# tarball out of DEPLOY_DIR_IMAGE, which serves it as a hardlink into this
# recipe's WORKDIR. rm_work would otherwise be free to remove that while the
# packing task reads.
require recipes-bsp/tcb-signing-files/tcb-signing-files.inc

do_rm_work[depends] += "${TCB_SIGNING_PACK_DEPENDS}"
