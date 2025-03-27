# Toradex Kernel LOCALVERSION extension
#
# This allow to easy reuse of code between different kernel recipes
#
# The following options are supported:
#
#  SCMVERSION           Puts the Git hash in kernel local version
#  KERNEL_LOCALVERSION  Value used in LOCALVERSION by the oe kernel classes
#
# Copyright 2014, 2015 (C) O.S. Systems Software LTDA.
# Copyright 2019 (C) Toradex AG

inherit toradex-kernel-config

TDX_VERSION ??= "0"
# Do not include the kernel repo's git hash to the kernel version.
# If patches are applied from OE this is not reproducible as patches
# get applied by 'git am' and a new hash value will be present with
# each build.
# Additionally external kernel modules fail to install if the kernel
# is taken from sstate but the module is rebuilt.
SCMVERSION = "n"
KERNEL_LOCALVERSION ?= "-${TDX_VERSION}"
# mute the meta-freescale/classes/fsl-kernel-localversion setting, otherwise
# with latest master we get -${TDX_VERSION} twice in the resulting version.
LINUX_VERSION_EXTENSION = ""

kernel_do_configure:append() {
	if [ "${SCMVERSION}" = "y" ]; then
		# Add GIT revision to the local version
		# SRCREV_machine is used in kernel recipes using kernel-yocto.bbclass,
		# e.g. our linux-toradex-mainline recipe
		if [ -n "${SRCREV_machine}" ]; then
			if [ "${SRCREV_machine}" = "AUTOINC" ]; then
				branch=`git --git-dir=${S}/.git  symbolic-ref --short -q HEAD`
				head=`git --git-dir=${S}/.git rev-parse --verify --short=12 origin/${branch} 2> /dev/null`
			else
				head=`git --git-dir=${S}/.git rev-parse --verify --short=12 ${SRCREV_machine} 2> /dev/null`
			fi
		# SRCREV is used by linux-toradex recipes
		elif [ -n "${SRCREV}" -a "${SRCREV}" = "AUTOINC" ]; then
			branch=`git --git-dir=${S}/.git  symbolic-ref --short -q HEAD`
			head=`git --git-dir=${S}/.git rev-parse --verify --short=12 origin/${branch} 2> /dev/null`
		elif [ -n "${SRCREV}" -a "${SRCREV}" != "INVALID" ]; then
			head=`git --git-dir=${S}/.git rev-parse --verify --short=12 ${SRCREV} 2> /dev/null`
		else
			head=`git --git-dir=${S}/.git rev-parse --verify --short=12 HEAD 2> /dev/null`
		fi
		printf "+git.%s" $head > ${S}/.scmversion
		kernel_configure_variable LOCALVERSION_AUTO y
	else
		kernel_configure_variable LOCALVERSION_AUTO n
	fi
}

def get_linux_base_version(d):
    linux_version = d.getVar('LINUX_VERSION')
    if '-rt' in linux_version:
        return linux_version.split('-rt')[0]
    return linux_version

CVE_VERSION = "${@get_linux_base_version(d)}"
