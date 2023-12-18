# A list of registry strings, for instance:
# PODMAN_DEFAULT_REGISTRIES = "docker.io registry.fedoraproject.org quay.io registry.access.redhat.com registry.centos.org"
#
# When set with a non-empty value, it would replace the original '[registries.search]' field
# in ${sysconfdir}/containers/registries.conf.
PODMAN_DEFAULT_REGISTRIES = "docker.io"

do_install:append() {
    if [ -n "${PODMAN_DEFAULT_REGISTRIES}" ]; then
        registries="registries = ["
        i=0
        for registry in ${PODMAN_DEFAULT_REGISTRIES}; do
            i=$(expr $i + 1)
            if [ "$i" -gt "1" ]; then
                registries="$registries, ""'$registry'"
            else
                registries="$registries""'$registry'"
            fi
        done
        registries="$registries""]"

        sed -i -e "/^\[registries.search\]/{n;d}" ${D}${sysconfdir}/containers/registries.conf
        sed -i -e "/^\[registries.search\]/a $registries" ${D}${sysconfdir}/containers/registries.conf
    fi
}
