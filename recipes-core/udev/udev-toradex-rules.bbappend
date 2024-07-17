FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://10-toradex-net-rename.rules \
    file://90-toradex-gpio.rules \
    file://91-toradex-i2cdev.rules \
    file://92-toradex-spidev.rules \
    file://93-toradex-backlight.rules \
    file://94-toradex-pwm.rules \
    file://toradex-net-rename.sh \
"

do_install:append () {
    install -m 0644 ${S}/10-toradex-net-rename.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${S}/90-toradex-gpio.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${S}/91-toradex-i2cdev.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${S}/92-toradex-spidev.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${S}/93-toradex-backlight.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${S}/94-toradex-pwm.rules ${D}${sysconfdir}/udev/rules.d/

    install -d ${D}${bindir}
    install -m 0755 ${S}/toradex-net-rename.sh ${D}${bindir}/
}
