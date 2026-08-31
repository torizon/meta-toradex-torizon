# fiptool links -lcrypto, but MediaTek's TF-A v2.6 fork has no LDOPTS line in
# tools/fiptool/Makefile for the base meta-arm inc to inject ${BUILD_LDFLAGS}
# into, so the native -L path never reaches the link and do_compile fails with
# "ld: cannot find -lcrypto". Inject it onto the LDLIBS line instead, ahead of
# -lcrypto. The \$\{...\} escaping keeps the literal token for make to expand
# (its value has commas that would break the sed); the vendor do_install strips
# it back out. Mirrors the base meta-arm inc.
do_compile:prepend() {
	sed -i '/^LDLIBS/ s,:=,:= \$\{BUILD_LDFLAGS\},' ${S}/tools/fiptool/Makefile
}
