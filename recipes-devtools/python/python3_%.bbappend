FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://0001-zipfile.py-do-not-raise-error-if-date_time-1980.patch \
"

# Optimization for python
# See https://www.phoronix.com/scan.php?page=news_item&px=Fedora-32-Python-Optimized
TARGET_CFLAGS:append = " -fno-semantic-interposition"

# Remove gdbm due to GPLv3
PACKAGECONFIG:remove = "gdbm"
