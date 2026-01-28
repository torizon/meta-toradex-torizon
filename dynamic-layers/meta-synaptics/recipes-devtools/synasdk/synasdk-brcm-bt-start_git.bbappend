# Fix bluetooth UART configuration for luna-sl1680
# The original recipe checks MACHINE="sl1680" but luna-sl1680 uses MACHINE="luna-sl1680"

do_patch:append:luna-sl1680() {
    cd ${WORKDIR}
    patch -p1 < dolphin_brcm_bt_start.patch
}
