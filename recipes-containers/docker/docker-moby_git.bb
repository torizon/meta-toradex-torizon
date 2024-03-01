HOMEPAGE = "http://www.docker.com"
SUMMARY = "Linux container runtime"
DESCRIPTION = "Linux container runtime \
 Docker complements kernel namespacing with a high-level API which \
 operates at the process level. It runs unix processes with strong \
 guarantees of isolation and repeatability across servers. \
 . \
 Docker is a great building block for automating distributed systems: \
 large-scale web deployments, database clusters, continuous deployment \
 systems, private PaaS, service-oriented architectures, etc. \
 . \
 This package contains the daemon and client, which are \
 officially supported on x86_64 and arm hosts. \
 Other architectures are considered experimental. \
 . \
 Also, note that kernel version 3.10 or above is required for proper \
 operation of the daemon process, and that any lower versions may have \
 subtle and/or glaring issues. \
 "

# Notes:
#   - This docker variant uses moby and the other individually maintained
#     upstream variants for SRCREVs
#   - It is a true community / upstream tracking build, and is not a
#     docker curated set of commits or additions
#   - The version number on this package tracks the versions assigned to
#     the curated docker-ce repository. This allows compatibility and
#     functional equivalence, while allowing new features to be more
#     easily added.
#   - This could be called "docker-moby" or just "moby" in the future, but
#     that would require the creation of a virtual/docker dependency, which
#     is possible, but overkill at the moment (while we wait for the upstream
#     to stop changing).
#   - The common components of this recipe and docker-ce do need to be moved
#     to a docker.inc recipe

SRCREV_moby = "791d8ab87747169b4cbfcdf2fd57c81952bae6d5"
SRCREV_libnetwork = "dcdf8f176d1e13ad719e913e796fb698d846de98"
SRCREV_cli = "911449ca245308472a3d34a7f1a98b918e65c8c3"
SRCREV_FORMAT = "moby_libnetwork"
SRC_URI = "\
    git://github.com/moby/moby.git;branch=20.10;name=moby;protocol=https \
    git://github.com/docker/libnetwork.git;branch=master;name=libnetwork;destsuffix=git/libnetwork;protocol=https \
    git://github.com/docker/cli;branch=20.10;name=cli;destsuffix=git/cli;protocol=https \
    file://docker.init \
    file://0001-libnetwork-use-GO-instead-of-go.patch \
    file://0001-cli-use-external-GO111MODULE-and-cross-compiler.patch \
    file://0001-dynbinary-use-go-cross-compiler.patch \
    file://0001-Revert-go-updates-from-1.19.12-to-1.18.7.patch;patchdir=src/import \
    file://0002-Revert-go-updates-from-1.18.7-to-1.17.13.patch;patchdir=src/import \
    file://0003-builder.go-avoid-using-strings.Cut-from-go-1.18.patch;patchdir=src/import \
"

DOCKER_COMMIT = "${SRCREV_moby}"

require docker.inc

# Apache-2.0 for docker
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=4859e97a9c7780e77972d989f0823f28"

# 58 commits after v20.10.25 to include the fixes for go compatibility after
# https://lists.openembedded.org/g/openembedded-core/message/185082
# https://github.com/moby/moby/compare/v20.10.25...791d8ab87747169b4cbfcdf2fd57c81952bae6d5
DOCKER_VERSION = "20.10.25"
PV = "${DOCKER_VERSION}+git${SRCREV_moby}"

CVE_PRODUCT = "docker mobyproject:moby"
