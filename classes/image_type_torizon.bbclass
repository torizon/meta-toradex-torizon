inherit image_type_tezi

TEZI_ROOT_LABEL = "otaroot"
TEZI_ROOT_NAME = "ota"
TEZI_ROOT_SUFFIX = "ota.tar.zst"
TEZI_USE_BOOTFILES = "false"

MINIMUM_TORIZON_TEZI_CONFIG_FORMAT = "3"
# Check if BSP's TEZI_CONFIG_FORMAT is higher than the one we need. If it is, we use BSP's value
python() {
    bsp_tezi = d.getVar('TEZI_CONFIG_FORMAT')
    torizon_tezi = d.getVar('MINIMUM_TORIZON_TEZI_CONFIG_FORMAT')
    if int(bsp_tezi) < int(torizon_tezi):
        d.setVar('TEZI_CONFIG_FORMAT', torizon_tezi)
}

python adjust_tezi_artifacts() {
    artifacts = d.getVar('TEZI_ARTIFACTS').replace(d.getVar('KERNEL_IMAGETYPE'), '').replace(d.getVar('KERNEL_DEVICETREE'), '')
    d.setVar('TEZI_ARTIFACTS', artifacts)
}

TEZI_IMAGE_TEZIIMG_PREFUNCS:append = " adjust_tezi_artifacts"

# '^metadata_csum' is needed to allow uboot save env to ext4 filesystem
EXTRA_IMAGECMD:ota-ext4:qemuarm64 = "-O ^64bit,^metadata_csum -L otaroot -i 4096 -t ext4"

IMAGE_CMD:ota:prepend() {
	if [ "${OSTREE_BOOTLOADER}" = "u-boot" ]; then
		cp -a ${DEPLOY_DIR_IMAGE}/boot.scr-${MACHINE} ${OTA_SYSROOT}/boot.scr
	fi
}
do_image_ota[depends] += "${@'u-boot-default-script:do_deploy' if d.getVar('OSTREE_BOOTLOADER') == 'u-boot' else ''}"

def get_tdx_ostree_purpose(purpose):
    return purpose.lower()

TDX_OSTREE_PURPOSE ?= "${@get_tdx_ostree_purpose(d.getVar('TDX_PURPOSE'))}"

# Use new branch naming
OSTREE_BRANCHNAME = "${TDX_MAJOR}/${MACHINE}/${DISTRO}/${IMAGE_BASENAME}/${TDX_OSTREE_PURPOSE}"

# Force ostree summary to be updated
OSTREE_UPDATE_SUMMARY = "1"

# Kernel source metadata
OSTREE_KERNEL_SOURCE_META_DATA = "('${OSTREE_KERNEL_SOURCE_URL}', '${OSTREE_KERNEL_SOURCE_BRANCH}', '${OSTREE_KERNEL_SOURCE_VERSION}')"
OSTREE_KERNEL_SOURCE_META_DATA[vardepvalue] = "${OSTREE_KERNEL_SOURCE_URL}"
OSTREE_KERNEL_SOURCE_META_DATA[vardepvalue] = "${OSTREE_KERNEL_SOURCE_BRANCH}"
OSTREE_KERNEL_SOURCE_META_DATA[vardepvalue] = "${OSTREE_KERNEL_SOURCE_VERSION}"

OSTREE_KERNEL_SOURCE_URL = "${@oe.utils.read_file('${DEPLOY_DIR_IMAGE}/.kernel_scmurl')}"
OSTREE_KERNEL_SOURCE_BRANCH = "${@oe.utils.read_file('${DEPLOY_DIR_IMAGE}/.kernel_scmbranch')}"
OSTREE_KERNEL_SOURCE_VERSION = "${@oe.utils.read_file('${DEPLOY_DIR_IMAGE}/.kernel_scmversion')}"

# Create change log file inside ostree
OSTREE_CHANGE_LOG_FILE = "${OSTREE_REPO}/${DISTRO}-${IMAGE_BASENAME}-diff.log"
OSTREE_CHANGE_LOG_FOOTER = "================================"
OSTREE_CHANGE_LOG_HEADER = "Changes in version ${IMAGE_NAME}"
OSTREE_CREATE_DIFF = "1"

# Use full distro version only as commit subject, we have distro an image name
# in the OSTree branch name.
OSTREE_COMMIT_SUBJECT = "${DISTRO_VERSION}"
OSTREE_COMMIT_SUBJECT[vardepsexclude] = "DISTRO_VERSION"

# Get git hashes from all layers and print it as GLib g_variant_parse() string
# inspiration taken from image-buildinfo
def get_layer_revision_information(d):
    import bb.process
    import subprocess
    try:
        layers = []
        paths = (d.getVar("BBLAYERS" or "")).split()

        for path in paths:
            # Use relative path from ${OEROOT}/layers/ as layer name
            name = os.path.relpath(path, os.path.join(d.getVar('OEROOT'), "layers"))
            rev, _ = bb.process.run('export PSEUDO_UNLOAD=1; git rev-parse HEAD', cwd=path)
            branch, _ = bb.process.run('export PSEUDO_UNLOAD=1; git rev-parse --abbrev-ref HEAD', cwd=path)
            try:
                subprocess.check_output("""cd %s; export PSEUDO_UNLOAD=1; set -e;
                                        git diff --quiet --no-ext-diff
                                        git diff --quiet --no-ext-diff --cached""" % path,
                                        shell=True,
                                        stderr=subprocess.STDOUT)
                modified = ""
            except subprocess.CalledProcessError as ex:
                modified = ":modified"
            # Key/value pair per layer
            layers.append("'{}': '{}:{}{}'".format(name, branch.strip(), rev.strip(), modified))

        # Create GLib dictionary
        return "{" + ",".join(layers) + "}"
    except:
        e = sys.exc_info()[0]
        bb.warn("Failed to get layers information. Exception: {}".format(e))

EXTRA_OSTREE_COMMIT[vardepsexclude] = "OSTREE_KERNEL_SOURCE_META_DATA"
EXTRA_OSTREE_COMMIT = " \
    --add-metadata-string=oe.machine="${MACHINE}" \
    --add-metadata-string=oe.distro="${DISTRO}" \
    --add-metadata-string=oe.distro-codename="${DISTRO_CODENAME}" \
    --add-metadata-string=oe.image="${IMAGE_BASENAME}" \
    --add-metadata-string=oe.tdx-build-purpose="${TDX_OSTREE_PURPOSE}" \
    --add-metadata-string=oe.tdx-major="${TDX_MAJOR}" \
    --add-metadata-string=oe.arch="${TARGET_ARCH}" \
    --add-metadata-string=oe.package-arch="${TUNE_PKGARCH}" \
    --add-metadata-string=oe.kargs-default="${OSTREE_KERNEL_ARGS}" \
    --add-metadata=oe.kernel-source="${OSTREE_KERNEL_SOURCE_META_DATA}" \
    --add-metadata-string=oe.garage-target-name="${GARAGE_TARGET_NAME}" \
    --add-metadata-string=oe.garage-target-version="${GARAGE_TARGET_VERSION}" \
    --add-metadata-string=oe.sota-hardware-id="${SOTA_HARDWARE_ID}" \
    --add-metadata=oe.layers="${@get_layer_revision_information(d)} " \
"

IMAGE_CMD:ostreecommit[vardepsexclude] += "EXTRA_OSTREE_COMMIT OSTREE_COMMIT_SUBJECT"

do_image_ostreecommit[postfuncs] += " generate_diff_file"
generate_diff_file[lockfiles] += "${OSTREE_REPO}/ostree.lock"

generate_diff_file () {
    if [ "${OSTREE_CREATE_DIFF}" = "1"  ]; then
        if [ -n "${OSTREE_CHANGE_LOG_FILE}"  ]; then
            if ostree --repo=${OSTREE_REPO} diff ${OSTREE_BRANCHNAME} ; then
                touch "${OSTREE_CHANGE_LOG_FILE}"
                echo "${OSTREE_CHANGE_LOG_HEADER}" >> "${OSTREE_CHANGE_LOG_FILE}"
                ostree --repo=${OSTREE_REPO} diff ${OSTREE_BRANCHNAME} >> ${OSTREE_CHANGE_LOG_FILE}
                echo "${OSTREE_CHANGE_LOG_FOOTER}" >> "${OSTREE_CHANGE_LOG_FILE}"
            fi
        fi
    fi
}

IMAGE_DATETIME_FILES ??= " \
    ${sysconfdir}/issue \
    ${sysconfdir}/issue.net \
    ${sysconfdir}/os-release \
    ${sysconfdir}/timestamp \
    ${sysconfdir}/version \
    ${libdir}/os-release \
"

ROOTFS_POSTPROCESS_COMMAND += "tweak_os_release_variant;"

# Tweak /etc/os-release according to IMAGE_VARIANT defined in image recipe
tweak_os_release_variant () {
	if [ -n "${IMAGE_VARIANT}" ]; then
		sed -i -e "s/^VARIANT=.*$/VARIANT=\"${IMAGE_VARIANT}\"/g" ${IMAGE_ROOTFS}${sysconfdir}/os-release
	else
		bbwarn "IMAGE_VARIANT is missing, would be better to define it for a TorizonCore image recipe."
	fi
}

ROOTFS_POSTPROCESS_COMMAND += "adjust_container_engines;"

# Ajust rootfs for container engines
adjust_container_engines () {
	if [ -f ${IMAGE_ROOTFS}${bindir}/podman ]; then
		# Allow torizon user to execute podman without password
		echo >> ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/50-torizon
		echo "# torizon user can execute podman without password" >> ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/50-torizon
		echo "torizon ALL=(ALL) NOPASSWD:/usr/bin/podman" >> ${IMAGE_ROOTFS}${sysconfdir}/sudoers.d/50-torizon
	elif [ -f ${IMAGE_ROOTFS}${bindir}/docker ]; then
		# We build docker-compose as a standalone binary in docker-compose recipe,
		# which is installed as ${bindir}/docker-compose, but we'd also like to
		# allow it to be run as a docker plugin 'docker compose', create the link
		# to support that.
		install -d ${IMAGE_ROOTFS}${nonarch_libdir}/docker/cli-plugins
		ln -sf ${bindir}/docker-compose ${IMAGE_ROOTFS}${nonarch_libdir}/docker/cli-plugins/docker-compose
	fi
}

# Make bootloader binary and version files available in deployment directory.
ROOTFS_POSTPROCESS_COMMAND += "gen_bootloader_ota_files;"

UBOOT_BINARY_OTA = ""
UBOOT_BINARY_OTA:apalis-imx6 = "u-boot-with-spl.imx"
UBOOT_BINARY_OTA:colibri-imx6 = "u-boot-with-spl.imx"
UBOOT_BINARY_OTA:colibri-imx6ull-emmc = "u-boot.imx"
UBOOT_BINARY_OTA:colibri-imx7-emmc = "u-boot.imx"
UBOOT_BINARY_OTA:apalis-imx8 = "imx-boot"
UBOOT_BINARY_OTA:colibri-imx8x = "imx-boot"
UBOOT_BINARY_OTA:apalis-imx8 = "imx-boot"
UBOOT_BINARY_OTA:verdin-imx8mm = "imx-boot"
UBOOT_BINARY_OTA:verdin-imx8mp = "imx-boot"
UBOOT_BINARY_OTA:verdin-am62 = "u-boot.img"
UBOOT_BINARY_OTA:qemuarm64 = "u-boot.bin"

UBOOT_BINARY_OTA_IGNORE = "0"
# disable for now while we investigate build issues
UBOOT_BINARY_OTA_IGNORE:verdin-am62 = "1"
UBOOT_BINARY_OTA_IGNORE:genericx86-64 = "1"

# Function: find_uboot_env_blob
#
# Purpose: Find the location of given "environment variables blob" inside a
#          bootloader binary.
#
# Parameters:
#
# - $1: Name of u-boot binary file; this could be the any of the normal binaries
#       produced by compiling u-boot or a container image where the u-boot
#       binary would be encapsulated. The environment embedded into the binary
#       is supposed to be a series of NUL terminated strings.
# - $2: Name of the (known) u-boot environment file that is supposed to be embedded
#       into the binary; this is supposed to be produced in the same way as done by
#       u-boot's "u-boot-initial-env" make target except that no sorting of the
#       environment should be performed.
#
# Output: String providing information about the offset and size of the environment
#         found (if any); if the environment is not found the function will return
#         a non-zero exit code.
#
# How it works:
#
# The function looks for the longest variable in the known environment and searches
# for the same string inside the u-boot binary. For each "potential" hit it extracts
# the whole (potential) environment from the binary and compares with the known
# full environment (after replacing NULs with new lines) - if it matches then the
# location is where the environment is located. This process requires that the
# environment in the binary and the one being searched are in the exact same order.
#
find_uboot_env_blob () {
    local binfile envfile \
	  longest offset_in_env offsets_in_bin \
	  env_found env_size env_offset

    binfile="${1:?Binary file not specified}"
    envfile="${2:?Environment file not specified}"

    # Find longest line (variable assignment) in known environment:
    longest=""
    while IFS="" read line; do
        if [ ${#line} -gt ${#longest} ]; then
        longest="${line}"
        fi
    done < "${envfile}"

    if [ ${#longest} -lt 8 ]; then
        echo "Longest environment variable is too short!" >&2
        return 1
    fi

    # Determine total size of environment:
    env_size=$(stat -Lc "%s" "${envfile}")

    # Find offset of the longest line in the known environment:
    offset_in_env=$(strings -n"${#longest}" -td "${envfile}" |
                        grep -F "${longest}" |
                        { read location line; echo $location; })

    # Find offsets of the longest line in the binary:
    offsets_in_bin=$(strings -n"${#longest}" -td "${binfile}" |
                        grep -F "${longest}" |
                        { while read location line; do echo $location; done; })

    env_found=0
    for offset in ${offsets_in_bin}; do
        env_offset=$(expr ${offset} - ${offset_in_env})
        # Extract potential area from binary:
        dd if="${binfile}" of="${binfile}.tmp" \
           iflag="skip_bytes,count_bytes" \
           skip="${env_offset}" count="${env_size}" 2>/dev/null
        # Replace NULs with NLs just like `make u-boot-initial-env` in u-boot does.
        sed -i 's/\x00/\x0A/g' "${binfile}.tmp"
        if cmp "${binfile}.tmp" "${envfile}" >/dev/null; then
            # Note: beware that the output here will be parsed later.
            echo "Found environment in ${binfile}: offset=${env_offset}, size=${env_size}."
            env_found=1
            break
        fi
    done
    rm -f "${binfile}.tmp"

   if [ ${env_found} -eq 0 ]; then
       echo "Could not find environment inside ${binfile}!" >&2
       return 1
   fi

   return 0
}

# Function: gen_bootloader_ota_files
#
# Purpose: Generate bootloader files directed to platform/OTA usage.
#
# Parameters: None
#
# Results:
#
# - A symbolic link "u-boot-ota.bin" pointing to the u-boot binary to be employed
#   with the platform.
# - A file named "u-boot-ota.json" with information about the version of u-boot
#   and the location and size of the environment blob inside the u-boot binary.
#
# Both of the above files are generated in the deployment directory.
#
gen_bootloader_ota_files () {
    local deploydir binfile binfile_out \
          verfile otafile envfile \
          envinfo_msg envinfo_off envinfo_siz addjson

    if [ "${UBOOT_BINARY_OTA_IGNORE}" = "1" ]; then
        bbnote "Not generating links to bootloader binary for OTA on machine ${MACHINE}."
        return 0
    fi
    if [ -z "${UBOOT_BINARY_OTA}" ]; then
        bbfatal "Name of u-boot binary for OTA usage is not known for machine ${MACHINE}." \
                "To ignore this error, please set UBOOT_BINARY_OTA_IGNORE=\"1\"."
    fi

    deploydir="${DEPLOY_DIR_IMAGE}"
    binfile="${deploydir}/${UBOOT_BINARY_OTA}"
    verfile="${deploydir}/u-boot-version.json"

    if [ ! -f "${binfile}" ]; then
        bbfatal "Could not find bootloader binary file '${binfile}'"
    fi
    if [ ! -f "${verfile}" ]; then
        bbfatal "Could not find bootloader version file '${verfile}'"
    fi

    binfile_out="${deploydir}/u-boot-ota.bin"
    ln -sfnr $(readlink -f ${binfile}) ${binfile_out}

    otafile="${deploydir}/u-boot-ota.json"
    envfile="${deploydir}/u-boot-initial-env.raw"

    # Determine environment location and size inside u-boot binary.
    envinfo_msg=$(find_uboot_env_blob "${binfile_out}" "${envfile}")
    envinfo_off=$(echo "${envinfo_msg}" | sed 's/.* offset=\([0-9]\+\).*/\1/')
    envinfo_siz=$(echo "${envinfo_msg}" | sed 's/.* size=\([0-9]\+\).*/\1/')

    # Sanity check (arbitrary limits):
    [ "${envinfo_off}" -gt 64 -a "${envinfo_siz}" -gt 32 ]

    # Augment the information file with the u-boot environment location and size.
    rm -f ${otafile}
    cp -TLp ${verfile} ${otafile}

    # Following sed command will take a JSON file like this:
    # {
    #   "key1": "value1",
    #   "key2": "value2"
    # }
    # and turn it into something like this:
    # {
    #   "key1": "value1",
    #   "key2": "value2",
    # @@INSERT@@
    # }
    sed -i -n -e '
        N;             # Pattern space contains current and next line
        h;             # Save pattern space into hold space
        s/ *\n//g;     # Remove new lines and spaces at end of line from pattern space
        /[^,]}/ {      # Add comma and insertion point text
            x;
            s/ *\n/,\n@@INSERT@@\n/;
            p;
            d;
        };
        x;             # Get the pattern space with new lines back
        P;             # Print first line
        D;             # Delete first line and restart
    ' ${otafile}

    # Add new keys at insertion point.
    addjson="  \"envoffset\": ${envinfo_off},\n  \"envsize\": ${envinfo_siz}"
    sed -i -e "s/@@INSERT@@/${addjson}/" ${otafile}
}

IMAGE_PREPROCESS_COMMAND += "adjust_rootfs_datetime;"

# For the files listed in IMAGE_DATETIME_FILES that might contain DATETIME strings, substitute
# them with the current ${DATETIME} of do_image task, to replace the potential older DATATIMEs
# that installed from sstate.
adjust_rootfs_datetime () {
    for f in ${IMAGE_DATETIME_FILES}; do
        file=${IMAGE_ROOTFS}$f
        datetime=`grep -oE "[0-9]{4}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])(2[0-3]|[01][0-9])([0-5][0-9]){2}" $file | head -1`
        if [ -n "$datetime" ]; then
            sed -i -e "s#$datetime#${DATETIME}#g" $file
        fi
    done
}
adjust_rootfs_datetime[vardepsexclude] = "DATETIME"

TORIZON_SOTA_PROV_MODE ?= ""
TORIZON_SOTA_PROV_CREDENTIALS ?= \
    "${@d.getVar('SOTA_PACKED_CREDENTIALS') if d.getVar('SOTA_PACKED_CREDENTIALS') else ''}"
TORIZON_SOTA_PROV_SHARED_DATA ?= ""
TORIZON_SOTA_PROV_ONLINE_DATA ?= ""

TORIZON_SOTA_PROV_DATA_TARBALL = "provisioning-data.tar.gz"
TORIZON_SOTA_PROV_FILELIST ?= "\
    ${TORIZON_SOTA_PROV_DATA_TARBALL}:/ostree/deploy/torizon/var/sota/:true \
"
TEZI_ROOT_FILELIST:append = \
    "${@d.getVar('TORIZON_SOTA_PROV_FILELIST') \
       if d.getVar('TORIZON_SOTA_PROV_MODE') in ['online', 'offline'] else ''}"

require torizon_ota_helpers.inc

# Get provisioning data when user provides a credentials file.
get_torizon_prov_data1() {
    if [ ! -f "${TORIZON_SOTA_PROV_CREDENTIALS}" ]; then
        bbfatal "Could not find credentials file '${TORIZON_SOTA_PROV_CREDENTIALS}'."
    fi

    local destdir="${WORKDIR}/prov-data"
    local src dst tmp

    mkdir "${destdir}" 2>/dev/null || rm -fr "${destdir}"/*
    mkdir -m 0750 "${destdir}/tmp"
    mkdir -m 0750 "${destdir}/import"
    mkdir -m 0750 "${destdir}/import/repo"
    mkdir -m 0750 "${destdir}/import/director"

    # Image-repository root.json: shared between online and offline provisioning.
    src="root.json"
    tmp="${destdir}/tmp/repo-root.json"
    dst="${destdir}/import/repo/root.json"
    if ! unzip -p "${TORIZON_SOTA_PROV_CREDENTIALS}" "${src}" > "${tmp}" 2>/dev/null; then
        bbnote "Could not get '${src}' file from credentials file '${TORIZON_SOTA_PROV_CREDENTIALS}'; please update your credentials file."
        bbnote "Fetching image-repository root metadata from OTA server."
        ota_get_token_from_creds "${TORIZON_SOTA_PROV_CREDENTIALS}"
        ota_get_root_metadata "repo" > "${tmp}" \
            || bbfatal "Could not get image-repository root metadata from OTA server"
    fi
    mv "${tmp}" "${dst}" && chmod 0644 "${dst}"

    # Director-repository root.json: shared between online and offline provisioning.
    src="director.root.json"
    tmp="${destdir}/tmp/director.root.json"
    dst="${destdir}/import/director/root.json"
    if ! unzip -p "${TORIZON_SOTA_PROV_CREDENTIALS}" "${src}" > "${tmp}" 2>/dev/null; then
        bbnote "Could not get '${src}' file from credentials file '${TORIZON_SOTA_PROV_CREDENTIALS}'; please update your credentials file."
        bbnote "Fetching director-repository root metadata from OTA server."
        ota_get_token_from_creds "${TORIZON_SOTA_PROV_CREDENTIALS}"
        ota_get_root_metadata "director" > "${tmp}" \
            || bbfatal "Could not get director-repository root metadata from OTA server"
    fi
    mv "${tmp}" "${dst}" && chmod 0644 "${dst}"

    if [ "${TORIZON_SOTA_PROV_MODE}" = "online" ]; then
        src="provision.json"
        tmp="${destdir}/tmp/provision.json"
        dst="${destdir}/auto-provisioning.json"
        if ! unzip -p "${TORIZON_SOTA_PROV_CREDENTIALS}" "${src}" > "${tmp}" 2>/dev/null; then
            bbfatal "Could not get 'provision.json' file from credentials file '${TORIZON_SOTA_PROV_CREDENTIALS}'; please update your credentials file."
        fi
        mv "${tmp}" "${dst}" && chmod 0640 "${dst}"
    fi

    ota_clear_token

    rm -fr "${destdir}/tmp"
}

# Get provisioning data when user provides shared and (optional) online data.
get_torizon_prov_data2() {
    if [ ! -f "${TORIZON_SOTA_PROV_SHARED_DATA}" ]; then
        bbfatal "Could not find shared-data file '${TORIZON_SOTA_PROV_SHARED_DATA}'."
    fi

    local destdir="${WORKDIR}/prov-data"
    local dst tmp

    mkdir "${destdir}" 2>/dev/null || rm -fr "${destdir}"/*
    mkdir -m 0750 "${destdir}/tmp"
    mkdir -m 0750 "${destdir}/import"

    tar --preserve-permissions \
        -xvf "${TORIZON_SOTA_PROV_SHARED_DATA}" \
        -C "${destdir}/import"
    # Following directories/files are expected to exist inside the tarball
    # but may have different permissions; make sure they are correct.
    chmod 0750 "${destdir}/import/repo" || true
    chmod 0750 "${destdir}/import/director" || true
    
    if [ "${TORIZON_SOTA_PROV_MODE}" = "online" ]; then
        tmp="${destdir}/tmp/provision.json"
        dst="${destdir}/auto-provisioning.json"
        # Ignore base64 decoding errors; validate JSON instead.
        echo "${TORIZON_SOTA_PROV_ONLINE_DATA}" | base64 -di >"${tmp}" || true
        jq < "${tmp}" >/dev/null || bbfatal "Could not decode online provisioning data"
        mv "${tmp}" "${dst}" && chmod 0640 "${dst}"
    fi

    rm -fr "${destdir}/tmp"
}

gen_torizon_prov_data_tarball() {
    # Pass entries explicitly rather than passing '.'; this avoids including '.'  in the
    # tarball. Below we also remove the './' prefix to make the tarball similar to what
    # TorizonCore Builder produces.
    local entries
    entries="$(cd "${WORKDIR}/prov-data" && echo ./*)"
    [ -n "${entries}" ] && [ "${entries}" != "./*" ] || return 1

    # shellcheck disable=SC2086
    tar --preserve-permissions \
        --numeric-owner --owner=0 --group=0 \
        --transform='s#^./##' \
        -C "${WORKDIR}/prov-data" \
        -czf "${WORKDIR}/${TORIZON_SOTA_PROV_DATA_TARBALL}" ${entries}

    cp "${WORKDIR}/${TORIZON_SOTA_PROV_DATA_TARBALL}" \
       "${IMGDEPLOYDIR}/${TORIZON_SOTA_PROV_DATA_TARBALL}"
}

gen_torizon_prov_data_sanity_checks() {
    if [ "${TORIZON_SOTA_PROV_MODE}" != "offline" ] && \
       [ "${TORIZON_SOTA_PROV_MODE}" != "online" ]; then
        bbfatal "Unsupported provisioning mode: ${TORIZON_SOTA_PROV_MODE}"
    fi

    if [ -z "${TORIZON_SOTA_PROV_CREDENTIALS}" ] && \
       [ -z "${TORIZON_SOTA_PROV_SHARED_DATA}${TORIZON_SOTA_PROV_ONLINE_DATA}"]; then
        # Some credentials must be passed.
        bbfatal "Credentials required; either set TORIZON_SOTA_PROV_CREDENTIALS or" \
                "TORIZON_SOTA_PROV_SHARED_DATA (along with TORIZON_SOTA_PROV_ONLINE_DATA" \
                "when needed)"
    elif [ -n "${TORIZON_SOTA_PROV_CREDENTIALS}" ]; then
        # Passing the credentials works for both offline and online provisioning.
        if [ -n "${TORIZON_SOTA_PROV_SHARED_DATA}${TORIZON_SOTA_PROV_ONLINE_DATA}" ]; then
            bbfatal "Either set TORIZON_SOTA_PROV_CREDENTIALS or TORIZON_SOTA_PROV_SHARED_DATA" \
                    "(along with TORIZON_SOTA_PROV_ONLINE_DATA when needed)"
        fi
    elif [ "${TORIZON_SOTA_PROV_MODE}" = "offline" ]; then
        if [ -z "${TORIZON_SOTA_PROV_SHARED_DATA}" ]; then
            bbfatal "TORIZON_SOTA_PROV_SHARED_DATA must be set with offline provisioning"
        fi
    elif [ "${TORIZON_SOTA_PROV_MODE}" = "online" ]; then
        if [ -z "${TORIZON_SOTA_PROV_SHARED_DATA}" ] || \
           [ -z "${TORIZON_SOTA_PROV_ONLINE_DATA}" ]; then
            bbfatal "Both TORIZON_SOTA_PROV_SHARED_DATA and TORIZON_SOTA_PROV_ONLINE_DATA must" \
                    "be set with online provisioning"
        fi
    else
        bbfatal "Unhandled configuration in gen_torizon_prov_data()"
    fi
}

gen_torizon_prov_data() {
    if [ -z "${TORIZON_SOTA_PROV_MODE}" ]; then
        # Scale-provisining is not enabled.
        return 0
    fi
    gen_torizon_prov_data_sanity_checks

    bbnote "Adding provisioning data to Toradex Easy Installer image."

    if [ -n "${TORIZON_SOTA_PROV_CREDENTIALS}" ]; then
        # method 1: use credentials file.
        get_torizon_prov_data1
    else
        # method 1: use shared and online provisioning data.
        get_torizon_prov_data2
    fi

    gen_torizon_prov_data_tarball
}
TEZI_IMAGE_TEZIIMG_PREFUNCS:prepend = "gen_torizon_prov_data "

# Below we modify the varflags of 'do_image_teziimg' to ensure it gets called if
# some key variables are modified or if the contents of the credentials file change.
TORIZON_TEZIIMG_VARDEPS ?= \
    "TORIZON_SOTA_PROV_MODE TORIZON_SOTA_PROV_SHARED_DATA TORIZON_SOTA_PROV_ONLINE_DATA"

TORIZON_TEZIIMG_FILE_CHECKSUMS_COND ?= "\
    ${@(d.getVar('TORIZON_SOTA_PROV_CREDENTIALS') + ':True') \
      if d.getVar('TORIZON_SOTA_PROV_CREDENTIALS') else ''} \
"
TORIZON_TEZIIMG_FILE_CHECKSUMS ?= \
    "${@d.getVar('TORIZON_TEZIIMG_FILE_CHECKSUMS_COND') \
       if d.getVar('TORIZON_SOTA_PROV_MODE') in ['online', 'offline'] else ''}"

TORIZON_TEZIIMG_DEPENDS_COND ?= "\
    coreutils-native:do_populate_sysroot \
    curl-native:do_populate_sysroot \
    ca-certificates-native:do_populate_sysroot \
    tar-native:do_populate_sysroot \
    jq-native:do_populate_sysroot \
    unzip-native:do_populate_sysroot \
"
TORIZON_TEZIIMG_DEPENDS ?= \
    "${@d.getVar('TORIZON_TEZIIMG_DEPENDS_COND') \
       if d.getVar('TORIZON_SOTA_PROV_MODE') in ['online', 'offline'] else ''}"

do_image_teziimg[cleandirs] += "${WORKDIR}/prov-data"
do_image_teziimg[vardeps] += "${TORIZON_TEZIIMG_VARDEPS}"
do_image_teziimg[file-checksums] += "${TORIZON_TEZIIMG_FILE_CHECKSUMS}"
do_image_teziimg[depends] += "${TORIZON_TEZIIMG_DEPENDS}"
