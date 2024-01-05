FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-cmd-support-config-option-to-locate-authentication-f.patch"

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
}
