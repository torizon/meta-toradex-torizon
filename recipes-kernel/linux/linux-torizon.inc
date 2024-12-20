FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:cfs-support = "\
    file://composefs.scc \
"

SRC_URI:append:cfs-signed = "\
    file://composefs-fsverity.scc \
"

KBUILD_BUILD_VERSION .= "-Torizon"

# Print kernel URL and BRANCH to files to be used by ostree commit as metadata
kernel_do_deploy:append() {
	url=`git --git-dir=${S}/.git config --get remote.origin.url`
	printf "%s" ${url#*//} > ${DEPLOYDIR}/.kernel_scmurl

	branch=`git --git-dir=${S}/.git symbolic-ref --short -q HEAD`
	printf "%s" $branch > ${DEPLOYDIR}/.kernel_scmbranch

	head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
	printf "%s" $head > ${DEPLOYDIR}/.kernel_scmversion
}
