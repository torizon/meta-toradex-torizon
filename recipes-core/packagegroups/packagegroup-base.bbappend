RDEPENDS:packagegroup-base-wifi:append = " \
    linux-firmware-rtl8188 \
    linux-firmware-rtl8192cu \
    linux-firmware-sd8887 \
    linux-firmware-sd8997 \
    hostapd \
"

RDEPENDS:packagegroup-base-wifi:append:tdx = " \
    mwifiexap \
"

RDEPENDS:packagegroup-base-wifi:append:apalis-imx8 = " \
    mwifiex-delay \
"

RDEPENDS:packagegroup-base-wifi:append:colibri-imx8x = " \
    mwifiex-delay \
"

RDEPENDS:packagegroup-base-wifi:append:verdin-imx8mm = " \
    mwifiex-delay \
"
