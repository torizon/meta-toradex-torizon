# Adlink's memory-{2G,4G,8G}.dts overlays use target-path = "/soc", but the
# kernel's memory node is at the root (/memory@40000000), so the overlay only
# adds an unused /soc/memory@40000000 and MemTotal never changes. Patch them to
# target "/"; drop this once meta-adlink-mtk is fixed upstream.
FILESEXTRAPATHS:prepend:lec-mtk1200 := "${THISDIR}/${PN}/lec-mtk1200:"

SRC_URI:append:lec-mtk1200 = " file://0001-lec-mtk1200-target-root-node-in-memory-overlays.patch"
