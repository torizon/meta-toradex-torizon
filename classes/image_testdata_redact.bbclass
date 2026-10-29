# write_image_test_data (rootfs-postcommands.bbclass) dumps the whole
# datastore into ${IMAGE_NAME}.testdata.json, which is deployed next to the
# image. Credentials handed to the build, such as NVDCVE_API_KEY for
# cve-update-nvd2-native, end up in that file. Drop them, and any value that
# embeds them, once the file is written.
#
# :append (not +=) keeps this after write_image_test_data: IMAGE_CLASSES are
# inherited before rootfs-postcommands.

TESTDATA_REDACT_VARS ?= "NVDCVE_API_KEY"

ROOTFS_POSTPROCESS_COMMAND:append = " redact_image_test_data"

python redact_image_test_data() {
    import json

    testdata = os.path.join(d.getVar('IMGDEPLOYDIR'), "%s.testdata.json" % d.getVar('IMAGE_NAME'))
    if not os.path.exists(testdata):
        return

    with open(testdata) as f:
        data = json.load(f)

    secrets = []
    for var in (d.getVar('TESTDATA_REDACT_VARS') or "").split():
        value = data.pop(var, None)
        if value:
            secrets.append(value)

    for key, value in data.items():
        for secret in secrets:
            value = value.replace(secret, "")
        data[key] = value

    with open(testdata, "w") as f:
        json.dump(data, f, skipkeys=True, indent=4, sort_keys=True)
}
