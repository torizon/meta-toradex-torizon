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

TEZI_ROOT_FSOPTS:append:torizon-signed = " -O verity"

python adjust_tezi_artifacts() {
    artifacts = d.getVar('TEZI_ARTIFACTS').replace(d.getVar('KERNEL_IMAGETYPE'), '').replace(d.getVar('KERNEL_DEVICETREE'), '')
    d.setVar('TEZI_ARTIFACTS', artifacts)
}

TEZI_IMAGE_TEZIIMG_PREFUNCS:append = " adjust_tezi_artifacts"

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

# Use immediate expansion here to avoid calling a somewhat costly function whenever
# EXTRA_OSTREE_COMMIT is expanded.
OSTREE_LAYER_REVISION_INFO := "${@get_layer_revision_information(d)}"

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
    --add-metadata=oe.layers="${OSTREE_LAYER_REVISION_INFO}" \
"

IMAGE_CMD:ostreecommit[vardepsexclude] += "EXTRA_OSTREE_COMMIT OSTREE_COMMIT_SUBJECT"

require recipes-extended/ostree/gen-cfs-keys.inc

generate_cfs_keys[lockfiles] += "${DEPLOY_DIR_IMAGE}/cfskeys.lock"
generate_cfs_keys() {
    gen_cfs_keys
}

CFS_OSTREECOMMIT_PREFUNCS_COND ?= " generate_cfs_keys"
CFS_OSTREECOMMIT_PREFUNCS ?= \
    "${@d.getVar('CFS_OSTREECOMMIT_PREFUNCS_COND') if 'cfs-signed' in d.getVar('OVERRIDES') else ''}"

CFS_OSTREECOMMIT_DEPENDS_COND ?= "\
    coreutils-native:do_populate_sysroot \
    openssl-native:do_populate_sysroot \
"
CFS_OSTREECOMMIT_DEPENDS ?= \
    "${@d.getVar('CFS_OSTREECOMMIT_DEPENDS_COND') if 'cfs-signed' in d.getVar('OVERRIDES') else ''}"

CFS_OSTREECOMMIT_FILE_CHECKSUMS ?= "${@cfs_get_key_file_checksums(d)}"

do_image_ostreecommit[prefuncs] += "${CFS_OSTREECOMMIT_PREFUNCS}"
do_image_ostreecommit[depends] += "${CFS_OSTREECOMMIT_DEPENDS}"
do_image_ostreecommit[file-checksums] += "${CFS_OSTREECOMMIT_FILE_CHECKSUMS}"

EXTRA_OSTREE_COMMIT:append:cfs-signed = "\
    --generate-composefs-metadata \
    --sign-from-file=${CFS_SIGN_KEYDIR}/${CFS_SIGN_KEYNAME}.sec \
    --sign-type=ed25519 \
"

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
