require go-common.inc

FILESEXTRAPATHS:prepend := "${FILE_DIRNAME}/go-1.21:${FILE_DIRNAME}/go:"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707"

SRC_URI += "\
    file://0001-cmd-go-make-content-based-hash-generation-less-pedan.patch \
    file://0002-cmd-go-Allow-GOTOOLDIR-to-be-overridden-in-the-envir.patch \
    file://0003-ld-add-soname-to-shareable-objects.patch \
    file://0004-make.bash-override-CC-when-building-dist-and-go_boot.patch \
    file://0005-cmd-dist-separate-host-and-target-builds.patch \
    file://0006-cmd-go-make-GOROOT-precious-by-default.patch \
    file://0007-exec.go-do-not-write-linker-flags-into-buildids.patch \
    file://0008-src-cmd-dist-buildgo.go-do-not-hardcode-host-compile.patch \
    file://0009-go-Filter-build-paths-on-staticly-linked-arches.patch \
    file://CVE-2023-24531_1.patch \
    file://CVE-2023-24531_2.patch \
"
SRC_URI[main.sha256sum] = "0fe745c530f2f1d67193af3c5ea25246be077989ec5178df266e975f3532449e"
