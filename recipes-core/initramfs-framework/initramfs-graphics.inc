# List of modules to be added to initramfs image.
# Important: the order here matters, since the modules will be probed
# in the same order they are added to this variable!
INITRAMFS_EXTRA_KMODS ?= ""

# Modules necessary for our Verdin iMX8M(M|P) to display splash screen.
INITRAMFS_EXTRA_KMODS:mx8-nxp-bsp = "\
    fsl_imx_ldb \
    imx8mp_ldb \
    phy_fsl_imx8mp_lvds \
    irq_imx_irqsteer \
    sec_dsim \
    display_connector \
    sec_mipi_dsim_imx \
    ti_sn65dsi83 \
    lontium_lt8912b \
"

# Modules necessary for our TI modules to display splash screen.
INITRAMFS_EXTRA_KMODS:ti-soc = "\
    pwm_tiehrpwm \
    tidss \
    display_connector \
    tc358768 \
    ti_sn65dsi83 \
    lontium_lt8912b \
    sii902x \
"
# On BeagleY AI we need this extra module on top of TI modules
INITRAMFS_EXTRA_KMODS:append:beagley-ai = " ite_it66121"

def get_initramfs_kmods(d):
    kmods = d.getVar("INITRAMFS_EXTRA_KMODS").split()
    build_modules = [f"kernel-module-{m.replace('_', '-')}" for m in kmods]
    return " ".join(build_modules)