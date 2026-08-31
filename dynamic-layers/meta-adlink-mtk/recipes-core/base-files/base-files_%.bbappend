# The meta-adlink-mtk base-files bbappend installs usbgadget.conf for lec-mtk1200
# but omits the matching SRC_URI, so do_install fails ("cannot stat
# .../usbgadget.conf"). The file ships under the genio-1200-evk override (already
# on FILESEXTRAPATHS via the vendor bbappend); just supply the SRC_URI.
SRC_URI:append:lec-mtk1200 = " file://usbgadget.conf"
