FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

##
# Redefining SRC_URI's docker repo, since we need to add destsuffix option to it.
# As of commit cc4ec43a2b657fb4c58429ab14f1edc2473c1327 [go: Drop fork of unpack
# code, mandate GO_SRCURI_DESTSUFFIX], this variable is required in this recipe.
SRC_URI:remove = "git://github.com/docker/docker.git;branch=20.10;name=docker;protocol=https"
SRC_URI:append = " git://github.com/docker/docker.git;branch=20.10;name=docker;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX}"

SRC_URI:append = " \
    file://0001-dockerd-daemon-use-default-system-config-when-none-i.patch \
    file://0002-cli-config-support-default-system-config.patch \
    file://0003-Correct-panic-when-IPv4-lacks-IFA_ADDRESS-address.patch \
"

require docker-torizon.inc
