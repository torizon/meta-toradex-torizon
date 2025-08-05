FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://10-toradex-net-rename.rules \
    file://90-toradex-gpio.rules \
    file://91-toradex-i2cdev.rules \
    file://92-toradex-spidev.rules \
    file://93-toradex-backlight.rules \
    file://94-toradex-pwm.rules \
    file://95-toradex-dma.rules \
    file://toradex-net-rename.sh \
"

do_install:append () {
    install -m 0644 ${WORKDIR}/10-toradex-net-rename.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/90-toradex-gpio.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/91-toradex-i2cdev.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/92-toradex-spidev.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/93-toradex-backlight.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/94-toradex-pwm.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/95-toradex-dma.rules ${D}${sysconfdir}/udev/rules.d/

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/toradex-net-rename.sh ${D}${bindir}/
}

RDEPENDS:${PN} += "bash"
