SUMMARY = "Generate U-Boot style uEnv config for the Tegra bootloader"

DESCRIPTION = "Linux for Tegra (L4T) from NVIDIA is based on the UEFI edk2 \
 bootloader. Native format for the boot options in this bootloader is \
 extlinux.conf. For Torizon the Tegra bootloader is updated to apply \
 U-Boot's style uEnv.txt. Supported are the only options to specify \
 paths to the kernel, initrd and dtb images and bootargs, which are \
 added to uEnv.txt by the ostree recipes when build or by the \
 aktualizr service during the update. These are: kernel_image, \
 ramdisk_image, fdtdir, fdtfile and bootargs and their second \
 numbers like as kernel_image2. \
"

LICENSE = "MIT"

do_install () {
	install -d ${D}${libdir}/ostree-boot
# ostree doesn't set the "fdtfile" variable automatically so predefine it here.
	cat <<EOF > uEnv.txt
fdtfile=${KERNEL_DEVICETREE}
EOF
	install -m 0644 uEnv.txt ${D}${libdir}/ostree-boot/
}

PACKAGES = "ostree-uenv-extlinux-conf"
FILES:ostree-uenv-extlinux-conf = "${libdir}/ostree-boot/uEnv.txt"
