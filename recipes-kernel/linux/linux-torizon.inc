FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:cfs-support = "\
    file://composefs.scc \
"

SRC_URI:append:cfs-signed = "\
    file://composefs-fsverity.scc \
"

KBUILD_BUILD_VERSION .= "-Torizon"

do_patch:append:preempt-rt() {
	# When the RT patches are applied via "git apply" by the kgit-s2q tool
	# (part of yocto-kernel-tools) the author is set to "invalid_git config"
	# which we validate here to ensure we'll amend the proper commit.
	#
	author0=$(git --git-dir="${S}/.git" log -1 --format='%an' HEAD)
	author1=$(git --git-dir="${S}/.git" log -1 --format='%an' HEAD^)
	if [ "${author0}" != "invalid_git config" ]; then
		bbnote "Amendment of RT patches commit dates not required"
		return 0
	fi

	# The assumption here is that only the latest commit has the invalid
	# author while the previous one is valid (that is, all RT patches are
	# concentrated in a single commit).
	if [ "${author1}" = "invalid_git config" ]; then
		bbfatal "Assumption for PREMPT_RT patches is no longer valid; task needs review!"
	fi

	# Reset the date of the commit introducing the RT patches to get a
	# reproducible hash.
	#
	# This is basically a workaround for the fact that the RT patches are
	# applied as a single patch file without author and commit date
	# information which causes the generated commit to always have a
	# different hash even with the exact same base kernel and RT patches.
	#
	bbnote "Amending dates of last commit (holding the RT patches)"
	GIT_COMMITTER_DATE='1970-01-01T00:00:00 +0000' \
	git --git-dir="${S}/.git" commit \
	    --amend --no-edit -m 'RT patches' \
	    --date='1970-01-01T00:00:00 +0000'
}

# Print kernel URL and BRANCH to files to be used by ostree commit as metadata
kernel_do_deploy:append() {
	url=`git --git-dir=${S}/.git config --get remote.origin.url`
	printf "%s" ${url#*//} > ${DEPLOYDIR}/.kernel_scmurl

	branch=`git --git-dir=${S}/.git symbolic-ref --short -q HEAD`
	printf "%s" $branch > ${DEPLOYDIR}/.kernel_scmbranch

	head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
	printf "%s" $head > ${DEPLOYDIR}/.kernel_scmversion
}
