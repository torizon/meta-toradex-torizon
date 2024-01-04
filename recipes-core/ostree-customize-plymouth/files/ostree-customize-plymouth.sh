#!/bin/bash

set -e

OSTREE_MERGE_DIR=/var/tmp/ostree-merge
SPLASH_INITRAMFS_DIR=/var/tmp/splash-dir
SPLASH_INITRAMFS=/var/tmp/initramfs.splash

display_usage() {
	cat << EOF
This script must be run with super-user privileges!

Usage:
$0 [splash] [ostree ref] [new ostree branch]

Note: You can use 'ostree admin status' to get the current OSTree reference.
EOF
}

if [  $# -lt 3 ]
then
	display_usage
	exit 1
fi

if [[ ( $# == "--help") ||  $# == "-h" ]]
then
	display_usage
	exit 0
fi

if [[ "$(id -u)" != "0" ]]; then
	echo "This script must be run as a sudoer!"
	exit 1
fi

SPLASH_FILE=$1
OSTREE_REF=$2
OSTREE_BRANCH=$3


mkdir -p "${SPLASH_INITRAMFS_DIR}/usr/share/plymouth/themes/spinner/"
cp "$SPLASH_FILE" "${SPLASH_INITRAMFS_DIR}/usr/share/plymouth/themes/spinner/watermark.png"

# Create initramfs archive for splash screen image only
echo ./usr/share/plymouth/themes/spinner/watermark.png | \
	cpio -H newc -D "${SPLASH_INITRAMFS_DIR}" -o | gzip > ${SPLASH_INITRAMFS}

rm -r "${SPLASH_INITRAMFS_DIR}"

# Combine with current initramfs. Idea taken from
# https://unix.stackexchange.com/questions/243657/appending-files-to-initramfs-image-reliable
KERNEL_DIRS=(/usr/lib/modules/*/)
KERNEL_DIR=${KERNEL_DIRS[0]}

mkdir -p ${OSTREE_MERGE_DIR}${KERNEL_DIR}
cat ${KERNEL_DIR}/initramfs.img ${SPLASH_INITRAMFS} > \
	${OSTREE_MERGE_DIR}${KERNEL_DIR}/initramfs.img

ostree commit -b "$OSTREE_BRANCH" --tree=ref="$OSTREE_REF" --tree=dir="${OSTREE_MERGE_DIR}"

cat << EOF
Successfully created new branch "${OSTREE_BRANCH}" with the custom splash screen
${SPLASH_FILE} integrated. Deploy this branch using:
  ostree admin deploy "${OSTREE_BRANCH}"
EOF
