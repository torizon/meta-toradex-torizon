FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

##
# Using '+git${SRCPV}' on PV was causing a variable loop, since there's a cyclic dependency
# on the variables. So to fix that, we remove SRCPV from it.
PV = "v1.6.1+git"

##
# Redefining SRC_URI's docker repo, since we need to add destsuffix option to it.
# As of commit cc4ec43a2b657fb4c58429ab14f1edc2473c1327 [go: Drop fork of unpack
# code, mandate GO_SRCURI_DESTSUFFIX], this variable is required in this recipe.
SRC_URI:remove = "git://github.com/containers/skopeo;branch=main;protocol=https"
SRC_URI:append = " git://github.com/containers/skopeo;branch=main;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX}"

SRC_URI += " \
    file://0001-docker-config-support-default-system-config.patch \
"
