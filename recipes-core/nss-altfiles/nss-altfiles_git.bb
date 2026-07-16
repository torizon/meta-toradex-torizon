SUMMARY = "NSS module which can read user information from files in the same format \
as /etc/passwd and /etc/group stored in an alternate location"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb1949d8d807e528c1673da700aff41f"

SRC_URI = "git://github.com/flatcar-linux/nss-altfiles.git;protocol=https;branch=main"

# Modify these as desired
PV = "2.43.0+git${SRCPV}"
SRCREV = "30ec1be7a9a253c6b723a9d2127d77b088f9ff20"

# Name Service Switch is provided by GNU C library for Linux
python __anonymous () {
    if d.getVar('TCLIBC') != "glibc":
        raise bb.parse.SkipRecipe("incompatible with %s C library" %
                                   d.getVar('TCLIBC'))
}

# The .so has to be installed under /lib for the libc to use it.
EXTRA_OECONF = "--datadir=${libdir} --prefix=${libdir} --with-types=pwd,grp,spwd"

inherit autotools-brokensep
