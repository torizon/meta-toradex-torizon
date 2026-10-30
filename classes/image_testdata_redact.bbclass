# write_image_test_data (rootfs-postcommands.bbclass) and write_sdk_test_data
# (populate_sdk_base.bbclass) dump the whole datastore into a testdata.json
# deployed next to the image or SDK. Credentials handed to the build, such as
# NVDCVE_API_KEY for cve-update-nvd2-native, end up in that file. Drop them,
# and replace their values in any other variable with <redacted>, once the
# file is written.
#
# NVDCVE_API_KEY is always redacted; add more variables with += or :append,
# or opt out with :remove.
#
# Inherited through IMAGE_CLASSES, and by buildtools-tarball (an SDK recipe,
# not an image) through its bbappend.
#
# write_image_test_data is added with += in rootfs-postcommands, so the
# :append below runs after it whatever the inherit order. write_sdk_test_data
# is added with :append in populate_sdk_base, so this class must be inherited
# after populate_sdk_base (image.bbclass does).
#
# The match is by value, so a short or common value in TESTDATA_REDACT_VARS
# also gets replaced inside unrelated variables.

TESTDATA_REDACT_VARS ??= ""
TESTDATA_REDACT_VARS:append = " NVDCVE_API_KEY"

ROOTFS_POSTPROCESS_COMMAND:append = " redact_image_test_data"
POPULATE_SDK_POST_TARGET_COMMAND:append = " redact_sdk_test_data"

def redact_test_data(d, testdata, writer, cmdvar):
    import json

    if not os.path.exists(testdata):
        # A silent return here would ship the credentials again if the
        # writer ever ran after us, so only accept it when the writer is
        # not part of this build.
        if writer in (d.getVar(cmdvar) or "").split():
            bb.fatal("%s is scheduled but %s does not exist: redaction would be a no-op" % (writer, testdata))
        return

    with open(testdata) as f:
        data = json.load(f)

    secrets = []
    for var in (d.getVar('TESTDATA_REDACT_VARS') or "").split():
        value = data.pop(var, None)
        if value:
            secrets.append(value)

    # Longest first, so a secret that contains another one is not left
    # half redacted.
    secrets.sort(key=len, reverse=True)

    for key, value in data.items():
        for secret in secrets:
            value = value.replace(secret, "<redacted>")
        data[key] = value

    with open(testdata, "w") as f:
        json.dump(data, f, skipkeys=True, indent=4, sort_keys=True)

python redact_image_test_data() {
    redact_test_data(d, os.path.join(d.getVar('IMGDEPLOYDIR'), "%s.testdata.json" % d.getVar('IMAGE_NAME')),
                     "write_image_test_data", "ROOTFS_POSTPROCESS_COMMAND")
}

python redact_sdk_test_data() {
    redact_test_data(d, "%s/%s.testdata.json" % (d.getVar('SDKDEPLOYDIR'), d.getVar('TOOLCHAIN_OUTPUTNAME')),
                     "write_sdk_test_data", "POPULATE_SDK_POST_TARGET_COMMAND")
}
