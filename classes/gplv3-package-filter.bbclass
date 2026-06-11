# Remove the packages listed in TORIZON_EXCLUDED_PACKAGES_GPL_V3 from the
# RDEPENDS/RRECOMMENDS of every package of every recipe, so a GPLv3 package
# can be dropped from the build by listing it in a single place instead of
# one .bbappend per recipe that pulls it in.
#
# This class is inherited globally from base-distro.inc and the package list
# is defined there as well.
#
# Note: this covers runtime dependencies declared in recipe metadata (e.g.
# packagegroups). Dependencies generated during do_package (shared library
# auto-dependencies, dynamic packages) are not affected; BAD_RECOMMENDATIONS
# in base-distro.inc act as the image-level backstop for those.

TORIZON_EXCLUDED_PACKAGES_GPL_V3 ??= ""

python () {
    excluded = set((d.getVar('TORIZON_EXCLUDED_PACKAGES_GPL_V3') or '').split())
    if not excluded:
        return

    # Host tools may be GPLv3; only filter target recipes.
    for native_class in ('native', 'nativesdk', 'cross', 'crosssdk'):
        if bb.data.inherits_class(native_class, d):
            return

    for pkg in (d.getVar('PACKAGES') or '').split():
        for depvar in ('RDEPENDS:' + pkg, 'RRECOMMENDS:' + pkg):
            value = d.getVar(depvar)
            if not value:
                continue
            deps = bb.utils.explode_dep_versions2(value)
            filtered = {name: constraint for name, constraint in deps.items() if name not in excluded}
            if len(filtered) != len(deps):
                d.setVar(depvar, bb.utils.join_deps(filtered, commasep=False))
}
