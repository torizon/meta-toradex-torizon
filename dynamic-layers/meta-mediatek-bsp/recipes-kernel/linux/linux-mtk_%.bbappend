# Kernel modules and vmlinux embed the absolute kernel-source build path via
# __FILE__ in BUG()/WARN() (__bug_table; CONFIG_DEBUG_INFO is off), tripping the
# [buildpaths] QA check on every .ko. The proper fix (rewriting __FILE__ via the
# -fmacro-prefix-map=${STAGING_KERNEL_DIR}=... that kernel-arch.bbclass already
# passes) does not work here: this aarch64 cross gcc does not apply
# -fmacro-prefix-map to __FILE__, and a CONFIG_DEBUG_BUGVERBOSE fragment never
# reaches the kernel unless it is merged onto the baseline set below. The
# warnings are non-fatal - the image builds and boots; the embedded paths are
# only a reproducibility/leak concern - so drop the buildpaths check for this
# recipe to keep the log usable.
WARN_QA:remove = "buildpaths"

# Torizon's required kernel settings, merged by do_kernel_configme like any other
# fragment. The require also restores kernel_do_deploy:append(), whose .kernel_scm*
# files become the OSTree oe.kernel-source metadata.
require recipes-kernel/linux/linux-torizon.inc

# The vendor points KBUILD_DEFCONFIG at the kernel's generic arm64 defconfig, so
# do_kernel_configme merges every fragment onto that and the vendor
# do_copy_defconfig then overwrites the result with ADLINK's defconfig - which is
# why no fragment reaches the kernel. ADLINK's defconfig is already in SRC_URI and
# already a merge-base candidate; it is dropped only because KBUILD_DEFCONFIG
# outranks it. Clear that, and configme builds on the board defconfig and keeps
# the fragments, leaving do_copy_defconfig with nothing to do.
KBUILD_DEFCONFIG:lec-mtk1200 = ""

# Everything here hangs off lec-mtk1200, the vendor's own override: if it is
# renamed, the KBUILD_DEFCONFIG override, this task disable, the fragment
# exclusions and the read-back below all disappear together and the build stays
# green with the clobber back. Detect that by mirroring the vendor's condition
# for scheduling the clobber - any ADLINK defconfig on a machine other than
# absolute-vision, which gets the task before configme and so keeps its
# fragments either way.
python () {
    if 'lec-mtk1200' in (d.getVar('OVERRIDES') or '').split(':'):
        d.setVarFlag('do_copy_defconfig', 'noexec', '1')
        return
    kcfg = d.getVar('KERNEL_CONFIG_AARCH64') or ''
    if kcfg.startswith('adlink_') and d.getVar('MACHINE') != 'absolute-vision':
        bb.fatal("lec-mtk1200 override missing but %s is in use - the kernel "
                 "fragments would silently not apply" % kcfg)
}

# The MediaTek SoC and evaluation-kit fragments stay out of the merge. Their
# settings were reviewed one at a time against the built configuration: most
# already hold the value asked for, and the rest describe the evaluation kit's
# peripherals or features outside this board's scope. Excluding them here keeps
# that an explicit, reviewable decision rather than a side effect of the task
# ordering. optee.cfg is left in: it asks for nothing the defconfig lacks.
SRC_URI:remove:lec-mtk1200 = "file://mt8195.cfg file://mt8395-evk.cfg \
                              file://sof.cfg file://tsn.cfg file://mt7921.cfg"

# Drop CONFIG_LOCALVERSION_AUTO so the kernel release / module path / vermagic
# don't carry setlocalversion's redundant "-g<sha>-dirty" (the shared kernel tree
# is dirtied by the vendor do_copy_source modifying tracked mt8195.dtsi).
# CONFIG_LOCALVERSION ("-mtk+g<srcrev>") is kept, as configme appends it.
do_configure:append:lec-mtk1200() {
    for f in ${B}/.config ${B}/include/config/auto.conf; do
        [ -f "$f" ] && sed -i 's/^CONFIG_LOCALVERSION_AUTO=y$/# CONFIG_LOCALVERSION_AUTO is not set/' "$f"
    done

    # Read back what olddefconfig produced. merge_config.sh never exits non-zero,
    # and do_kernel_configcheck ignores every symbol of a fragment whose .scc
    # declares it non-hardware, which torizon.scc does - so neither can gate this.
    # Fatal only where absence is silent at run time: ext4 rejects the
    # security.capability xattr that docker pull writes, --cpus is accepted and
    # not enforced, and no cgroup can carry a block-I/O limit at all.
    missing=
    for sym in CONFIG_EXT4_FS_SECURITY CONFIG_CFS_BANDWIDTH CONFIG_BLK_DEV_THROTTLING; do
        grep -qxF "$sym=y" ${B}/.config || missing="$missing $sym"
    done
    if [ -n "$missing" ]; then
        bbfatal "torizon.cfg did not reach the built .config:$missing"
    fi

    # "# CONFIG_X is not set" is a request to disable, not a comment.
    while read -r req || [ -n "$req" ]; do
        case "$req" in
        "# CONFIG_"*" is not set") sym=${req#\# }; sym=${sym%% *} ;;
        ""|\#*)                    continue ;;
        *)                         sym=${req%%=*} ;;
        esac
        if grep -qxF "$req" ${B}/.config; then
            continue
        fi
        case "$sym" in
        # KERNEL_LZ4 does not exist on arm64 (Image.gz is a Makefile target, not
        # a kconfig choice); ZSMALLOC has no prompt while ZSWAP is unset, so it
        # takes =m from ZRAM's select whatever the fragment asks for.
        CONFIG_KERNEL_LZ4|CONFIG_ZSMALLOC)
            bbnote "torizon.cfg: $req not applied - known and expected here" ;;
        *)
            bbwarn "torizon.cfg: $req not applied - needs triage" ;;
        esac
    done < ${WORKDIR}/torizon.cfg
}

# The image recipe reads these three with a helper that returns "" for a file it
# cannot open, so a broken deploy path commits an empty oe.kernel-source triple
# and nothing anywhere reports it.
kernel_do_deploy:append:lec-mtk1200() {
    for f in .kernel_scmurl .kernel_scmbranch .kernel_scmversion; do
        [ -s ${DEPLOYDIR}/$f ] || bbfatal "kernel provenance: ${DEPLOYDIR}/$f missing or empty"
    done
}
