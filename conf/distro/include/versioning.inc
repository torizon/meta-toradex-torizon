TDX_BUILDNBR ?= "0"
TDX_PURPOSE ?= "Testing"

TDX_MAJOR ?= "7"
TDX_MINOR ?= "0"
TDX_PATCH ?= "0"

def get_tdx_prerelease(purpose, datetime):
    if purpose == "Testing":
        return '-devel-%s' % (datetime)
    elif purpose == "Nightly":
        return '-devel-%s' % (datetime[:8])
    elif purpose == "Monthly":
        return '-devel-%s' % (datetime[:6])
    elif purpose == "Release":
        return ''
    else:
        return '-devel-%s' % (datetime)

TDX_BUILD ?= "+build.${TDX_BUILDNBR}"
TDX_MATRIX_BUILD_TIME ?= "${DATETIME}"
TDX_MATRIX_BUILD_TIME[vardepsexclude] = "DATETIME"
TDX_PRERELEASE ?= "${@get_tdx_prerelease(d.getVar('TDX_PURPOSE'), d.getVar('TDX_MATRIX_BUILD_TIME'))}"
TDX_RELEASE ?= "${TDX_MAJOR}.${TDX_MINOR}.${TDX_PATCH}"

# Toradex Version number without date for U-Boot/Kernel and other Toradex
# specific software artifacts.
TDX_VERSION ?= "${TDX_RELEASE}${@'' if d.getVar('TDX_PURPOSE') == 'Release' else '-devel'}"

# Complete image version with date and build number
DISTRO_VERSION = "${TDX_RELEASE}${TDX_PRERELEASE}${TDX_BUILD}"
IMAGE_VERSION_SUFFIX = "-${DISTRO_VERSION}"