##
# Using '+git${SRCPV}' on PV was causing a variable loop, since there's a cyclic dependency
# on the variables. So to fix that, we remove SRCPV from it.
PV = "${RUNC_VERSION}+git"

##
# Redefining SRC_URI's docker repo, since we need to add destsuffix option to it.
# As of commit cc4ec43a2b657fb4c58429ab14f1edc2473c1327 [go: Drop fork of unpack
# code, mandate GO_SRCURI_DESTSUFFIX], this variable is required in this recipe.
SRC_URI:remove = "git://github.com/opencontainers/runc;branch=release-1.1;protocol=https"
SRC_URI:append = " git://github.com/opencontainers/runc;branch=release-1.1;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX}"
