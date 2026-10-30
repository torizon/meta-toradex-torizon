# buildtools-tarball populates an SDK without being an image, so
# IMAGE_CLASSES does not reach it and its testdata.json would keep the
# build credentials. Inherit after populate_sdk so the redaction runs after
# write_sdk_test_data.
inherit image_testdata_redact
