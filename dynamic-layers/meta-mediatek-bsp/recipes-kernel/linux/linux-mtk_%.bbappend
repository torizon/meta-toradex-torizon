# Kernel modules and vmlinux embed the absolute kernel-source build path via
# __FILE__ in BUG()/WARN() (__bug_table; CONFIG_DEBUG_INFO is off), tripping the
# [buildpaths] QA check on every .ko. The proper fix (rewriting __FILE__ via the
# -fmacro-prefix-map=${STAGING_KERNEL_DIR}=... that kernel-arch.bbclass already
# passes) does not work here: this aarch64 cross gcc does not apply
# -fmacro-prefix-map to __FILE__, and disabling CONFIG_DEBUG_BUGVERBOSE via a
# config fragment is reverted by olddefconfig. The warnings are non-fatal - the
# image builds and boots; the embedded paths are only a reproducibility/leak
# concern - so drop the buildpaths check for this recipe to keep the log usable.
WARN_QA:remove = "buildpaths"

# Drop CONFIG_LOCALVERSION_AUTO so the kernel release / module path / vermagic
# don't carry setlocalversion's redundant "-g<sha>-dirty" (the shared kernel tree
# is dirtied by the vendor do_copy_source modifying tracked mt8195.dtsi).
# CONFIG_LOCALVERSION ("-mtk+g<srcrev>") is kept. Done post-configure because the
# vendor do_copy_defconfig overwrites .config (dropping fragments) beforehand.
do_configure:append:lec-mtk1200() {
    for f in ${B}/.config ${B}/include/config/auto.conf; do
        [ -f "$f" ] && sed -i 's/^CONFIG_LOCALVERSION_AUTO=y$/# CONFIG_LOCALVERSION_AUTO is not set/' "$f"
    done
}
