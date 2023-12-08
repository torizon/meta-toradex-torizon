# List of containers tags TorizonCore is compatible with
MACHINE=@@MACHINE@@

TORIZON_PURPOSE=$(grep -Po '(?<=\.0-)\w+' /etc/os-release | sort -u)

# Common tags for all releases
if [ "$TORIZON_PURPOSE" = "devel" ]; then
    export CT_TAG_DEBIAN="rc-bookworm"
    export CT_TAG_CHROMIUM="rc"
    export CT_TAG_COG="rc"
    export CT_TAG_DOCKER_COMPOSE="rc"
    export CT_TAG_GRAPHICS_TESTS="rc"
    export CT_TAG_PORTAINER_DEMO="rc"
    export CT_TAG_QT5_WAYLAND="rc"
    export CT_TAG_QT5_WAYLAND_EXAMPLES="rc"
    export CT_TAG_RT_TESTS="rc"
    export CT_TAG_STRESS_TESTS="rc"
    export CT_TAG_TORIZONCORE_BUILDER="rc"
    export CT_TAG_WAYLAND_BASE="rc"
    export CT_TAG_WESTON="rc"
    export CT_TAG_WESTON_TOUCH_CALIBRATOR="rc"
else
    export CT_TAG_DEBIAN="3-bookworm"
    export CT_TAG_CHROMIUM="3"
    export CT_TAG_COG="3"
    export CT_TAG_GRAPHICS_TESTS="3"
    export CT_TAG_GRAPHICS_TESTS_VIVANTE="3"
    export CT_TAG_PORTAINER_DEMO="3"
    export CT_TAG_QT5_WAYLAND="3"
    export CT_TAG_QT5_WAYLAND_EXAMPLES="3"
    export CT_TAG_QT5_WAYLAND_EXAMPLES_VIVANTE="3"
    export CT_TAG_QT5_WAYLAND_VIVANTE="3"
    export CT_TAG_QT6_WAYLAND="3"
    export CT_TAG_QT6_WAYLAND_EXAMPLES="3"
    export CT_TAG_QT6_WAYLAND_EXAMPLES_VIVANTE="3"
    export CT_TAG_QT6_WAYLAND_VIVANTE="3"
    export CT_TAG_RT_TESTS="3"
    export CT_TAG_STRESS_TESTS="3"
    export CT_TAG_TORIZONCORE_BUILDER="3"
    export CT_TAG_WAYLAND_BASE="3"
    export CT_TAG_WAYLAND_BASE_VIVANTE="3"
    export CT_TAG_WESTON="3"
    export CT_TAG_WESTON_TOUCH_CALIBRATOR="3"
    export CT_TAG_WESTON_VIVANTE="3"
fi

# Machine-specific tags
case "$MACHINE" in
    apalis-imx6|colibri-imx7-emmc|colibri-imx6|colibri-imx6ull-emmc|qemuarm64|genericx86-64)
        ;;

    apalis-imx8|colibri-imx8x|verdin-imx8mm|verdin-imx8mp)
        if [ "$TORIZON_PURPOSE" = "devel" ]; then
            export CT_TAG_QT6_WAYLAND="rc"
            export CT_TAG_QT6_WAYLAND_EXAMPLES="rc"
        else
            export CT_TAG_QT6_WAYLAND="3"
            export CT_TAG_QT6_WAYLAND_EXAMPLES="3"
        fi
        ;;
    verdin-am62)
        if [ "$TORIZON_PURPOSE" = "devel" ]; then
            export CT_TAG_QT6_WAYLAND="rc"
            export CT_TAG_QT6_WAYLAND_EXAMPLES="rc"
        else
            export CT_TAG_QT6_WAYLAND="3"
            export CT_TAG_QT6_WAYLAND_EXAMPLES="3"
        fi
        ;;
esac
