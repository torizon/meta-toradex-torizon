FILESEXTRAPATHS:prepend := "${THISDIR}/u-boot-ota:"

SRC_URI:append = " \
    file://bootcommand.cfg \
    file://bootcount.cfg \
    file://bootlimit.cfg \
"

# Produce files required for generating file "u-boot-ota.json" during
# TorizonCore image generation, namely:
#
# - u-boot-version.json: contains information about the version of u-boot.
# - u-boot-initial-env.raw: the normal u-boot-initial-env file without sorting;
#   the file is not sorted to allow for the search of a binary blob inside the
#   u-boot binary.

generate_version_file () {
    local outdir outfile ubootversion ubootrelease
    outdir=${1:?Missing output directory argument}
    outfile=${2:?Missing output file argument}
    # Get u-boot version and release using make (targets 'ubootversion' and 'ubootrelease');
    # some versions of u-boot have quotes in the string which are removed.
    ubootversion="$(oe_runmake -C ${S} O=${outdir} -s ubootversion | sed 's/\"//g')"
    ubootrelease="$(oe_runmake -C ${S} O=${outdir} -s ubootrelease | sed 's/\"//g')"
    printf '{\n  "ubootversion": "%s",\n  "ubootrelease": "%s"\n}\n' \
           "${ubootversion}" "${ubootrelease}" > "${outdir}/${outfile}"
}

generate_environ_file () {
    local outdir outfile envfile
    outdir=${1:?Missing build output directory argument}
    outfile=${2:?Missing output file argument}
    envfile="${outdir}/env/common.o"
    if [ ! -f "${envfile}" ]; then
        bbfatal "Couldn't find ${envfile} to get raw environment."
        return 1
    fi
    # Extract environment similar to what `make u-boot-initial-env` does but
    # without sorting it and replace NULs by NLs:
    if [ ! -f ${outdir}/tools/printinitialenv ]; then
        "${OBJCOPY}" \
            --dump-section .rodata.default_environment="${outdir}/${outfile}" \
            "${outdir}/env/common.o"
        sed --in-place -e 's/\x00/\x0A/g' "${outdir}/${outfile}"
    else
        ${outdir}/tools/printinitialenv > "${outdir}/${outfile}"
    fi
}

do_compile:append () {
    if [ -n "${UBOOT_CONFIG}" ]; then
        unset i j
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]; then
                    generate_version_file "${B}/${config}" "u-boot-version.json-${type}"
                    generate_environ_file "${B}/${config}" "u-boot-initial-env.raw-${type}"
                fi
            done
        done
    else
        generate_version_file "${B}" "u-boot-version.json"
        generate_environ_file "${B}" "u-boot-initial-env.raw"
    fi
}

deploy_version_file () {
    local indir infile outdir outfile stdoutfile
    indir=${1:?Missing input directory argument}
    infile=${2:?Missing input file argument}
    outdir=${3:?Missing output directory argument}
    outfile=${4:?Missing output file argument}
    stdoutfile=${5:-u-boot-version.json}
    install -D -m 0644 "${indir}/${infile}" "${outdir}/${outfile}"
    ln -sf "${outfile}" "${outdir}/${stdoutfile}"
}

deploy_environ_file () {
    local indir infile outdir outfile stdoutfile
    indir=${1:?Missing input directory argument}
    infile=${2:?Missing input file argument}
    outdir=${3:?Missing output directory argument}
    outfile=${4:?Missing output file argument}
    stdoutfile=${5:-u-boot-initial-env.raw}
    install -D -m 0644 "${indir}/${infile}" "${outdir}/${outfile}"
    ln -sf "${outfile}" "${outdir}/${stdoutfile}"
}

do_deploy:append () {
    if [ -n "${UBOOT_CONFIG}" ]; then
        unset i j
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]; then
                    deploy_version_file \
                        "${B}/${config}" "u-boot-version.json-${type}" \
                        "${DEPLOYDIR}" "u-boot-version.json-${MACHINE}-${type}-${PV}-${PR}"
                    deploy_environ_file \
                        "${B}/${config}" "u-boot-initial-env.raw-${type}" \
                        "${DEPLOYDIR}" "u-boot-initial-env.raw-${MACHINE}-${type}-${PV}-${PR}"
                fi
            done
        done
    else
        deploy_version_file \
            "${B}" "u-boot-version.json" \
            "${DEPLOYDIR}" "u-boot-version.json-${MACHINE}-${PV}-${PR}"
        deploy_environ_file \
            "${B}" "u-boot-initial-env.raw" \
            "${DEPLOYDIR}" "u-boot-initial-env.raw-${MACHINE}-${PV}-${PR}"
    fi
}
