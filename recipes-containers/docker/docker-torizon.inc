FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit bash-completion

SRC_URI:append = " \
    file://daemon.json \
    file://docker.service \
    file://chrome.json \
"

# Prefer docker.service instead of docker.socket as this is a critical service
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','docker.service','',d)}"

do_install:append() {
    install -d ${D}${libdir}/docker
    install -m 0644 ${WORKDIR}/daemon.json ${D}${libdir}/docker/

    # Replace default docker.service with the one provided by this recipe
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -m 644 ${WORKDIR}/docker.service ${D}/${systemd_unitdir}/system
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'seccomp', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/docker/seccomp
        install -m 0644 ${WORKDIR}/chrome.json ${D}${sysconfdir}/docker/seccomp/chrome.json
    fi

    COMPLETION_DIR=${D}${datadir}/bash-completion/completions
    install -d ${COMPLETION_DIR}
    install -m 0644 ${S}/cli/contrib/completion/bash/docker ${COMPLETION_DIR}
}

FILES:${PN} += "${libdir}/docker"
