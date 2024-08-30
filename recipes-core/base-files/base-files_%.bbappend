FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

hostname = ""

# This deviates a bit from what OE-core is doing. Especially we want the full
# distro version (with date) in /etc/issue. From what I understand the filtering
# has been mainly done to avoid inconsistency, but since we anyway rebuild
# base-files (e.g. due to build number) it shoud not matter much for us.
do_install_basefilesissue () {
	install -m 644 ${WORKDIR}/issue*  ${D}${sysconfdir}
	printf "${DISTRO_NAME} " >> ${D}${sysconfdir}/issue
	printf "${DISTRO_NAME} " >> ${D}${sysconfdir}/issue.net
	printf "%s \\\n \\\l\n\n" "${DISTRO_VERSION}" >> ${D}${sysconfdir}/issue
	printf "%s %%h\n\n" "${DISTRO_VERSION}" >> ${D}${sysconfdir}/issue.net
}
