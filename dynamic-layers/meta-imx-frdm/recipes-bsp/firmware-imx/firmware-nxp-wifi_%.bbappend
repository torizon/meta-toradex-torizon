# Remove the one extra instance of the ${PN}-nxpiw610-sdio that
# gets added by the newer version of meta-imx in the Torizon
# manifest.  When the meta-imx-frdm repo gets updated to match
# this will likely be removable.
python() {
    packages = d.getVar("PACKAGES")
    substring = "%s-nxpiw610-sdio" % d.getVar("PN")
    count = packages.count(substring)
    if count < 2:
        bb.warn("dynamic-layers/meta-imx-frdm/recipes-bsp/firmware-imx/firmware-nxp-wifi_%.bbappend can be removed")
    if count == 2:
        d.setVar("PACKAGES", packages.replace(substring, '', 1))
    else:
        bb.fatal(f"Substring appears {count} times")
}
