FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-cmd-support-config-option-to-locate-authentication-f.patch"

PACKAGECONFIG:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'completion', '', d)}"
DEPENDS:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'qemu-native', '', d)}"

inherit qemu bash-completion

QEMU_OPTIONS = ""
ALLOW_EMPTY:${PN}-bash-completion = "1"

do_install:append () {
	if ${@bb.utils.contains('PACKAGECONFIG', 'docker', 'true', 'false', d)}; then
		if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
			# API session does not expire
			sed -i -e 's#^ExecStart=\(.*\)$#ExecStart=\1 -t 0#g' ${D}${systemd_unitdir}/system/podman.service

			# Add alias docker.service to podman.service
			echo "Alias=docker.service" >> ${D}${systemd_unitdir}/system/podman.service
		fi

		# Run podman binary with sudo
		sed -i -e "s#${bindir}/podman#sudo ${bindir}/podman#" ${D}${bindir}/docker
	fi

	if ${@bb.utils.contains('PACKAGECONFIG', 'completion', 'true', 'false', d)}; then
		qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_TARGET}', ['${B}', '${STAGING_DIR_TARGET}/${base_libdir}'])}"
		cat > ${WORKDIR}/podman-qemuwrapper << EOF
#!/bin/sh

$qemu_binary "\$@"
EOF
		chmod +x ${WORKDIR}/podman-qemuwrapper
		${WORKDIR}/podman-qemuwrapper ./bin/podman completion bash > ${B}/podman-bash-completion
		install -d ${D}${datadir}/bash-completion/completions
		install -m 0644 ${B}/podman-bash-completion ${D}${datadir}/bash-completion/completions/podman
		if ${@bb.utils.contains('PACKAGECONFIG', 'docker', 'true', 'false', d)}; then
			sed "s/podman/docker/g" ${B}/podman-bash-completion > ${B}/docker-bash-completion
			install -m 0644 ${B}/docker-bash-completion ${D}${datadir}/bash-completion/completions/docker
		fi
	fi
}
