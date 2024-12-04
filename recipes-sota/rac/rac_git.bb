SUMMARY = "Remote access client (RAC) for Torizon OS"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

inherit cargo systemd

# Main source respository
SRC_URI = " \
    git://github.com/torizon/rac.git;protocol=https;branch=main;name=rac \
    git://github.com/toradex/tough;protocol=https;branch=rac;name=tough;destsuffix=tough \
    file://remote-access.service \
    file://client.toml \
"

SRCREV_FORMAT = "rac_tough"

SRCREV_rac = "3200bfc68a5b8fa645f54e8c715f32d313df925a"
SRCREV_tough = "9316c096b32196df75ba17a8a5502b19baffe24e"

# Disable AUTOREV, it does not guarantee work, since the crate dependencies
# listed below in the SRC_URI might also need to be updated.
# If you want to enable AUTOREV, uncomment the following lines and in the case
# the dependencies need to be updated, use "cargo bitbake", update the SRC_URI,
# remove the SRC_URI sha256sums completely below and build the recipe, which will
# output an updated list of SRC_URI sha256sums for each dependency from cargo.
# SRCREV_rac:use-head-next = "${AUTOREV}"
# SRCREV_tough:use-head-next = "${AUTOREV}"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} = "remote-access.service"
# Keep disabled by default for now
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

PV = "0.0+git${SRCPV}"

# Dependencies as specified by main project's Cargo.toml
# Make sure to keep this up-to-date as needed
# Auto-generated via "cargo bitbake"
SRC_URI += " \
    crate://crates.io/addr2line/0.21.0 \
    crate://crates.io/adler/1.0.2 \
    crate://crates.io/aead/0.5.2 \
    crate://crates.io/aes-gcm/0.10.3 \
    crate://crates.io/aes/0.8.4 \
    crate://crates.io/ahash/0.8.11 \
    crate://crates.io/aho-corasick/1.1.3 \
    crate://crates.io/android-tzdata/0.1.1 \
    crate://crates.io/android_system_properties/0.1.5 \
    crate://crates.io/anyhow/1.0.86 \
    crate://crates.io/async-broadcast/0.7.1 \
    crate://crates.io/async-channel/2.3.1 \
    crate://crates.io/async-executor/1.13.0 \
    crate://crates.io/async-fs/2.1.2 \
    crate://crates.io/async-io/2.3.3 \
    crate://crates.io/async-lock/3.4.0 \
    crate://crates.io/async-process/2.2.3 \
    crate://crates.io/async-recursion/1.1.1 \
    crate://crates.io/async-signal/0.2.9 \
    crate://crates.io/async-task/4.7.1 \
    crate://crates.io/async-trait/0.1.81 \
    crate://crates.io/atomic-waker/1.1.2 \
    crate://crates.io/autocfg/1.3.0 \
    crate://crates.io/axum-core/0.3.4 \
    crate://crates.io/axum/0.6.20 \
    crate://crates.io/backtrace/0.3.71 \
    crate://crates.io/base16ct/0.1.1 \
    crate://crates.io/base16ct/0.2.0 \
    crate://crates.io/base64/0.21.7 \
    crate://crates.io/base64/0.22.1 \
    crate://crates.io/base64ct/1.6.0 \
    crate://crates.io/bcrypt-pbkdf/0.10.0 \
    crate://crates.io/bitflags/1.3.2 \
    crate://crates.io/bitflags/2.6.0 \
    crate://crates.io/block-buffer/0.10.4 \
    crate://crates.io/block-buffer/0.9.0 \
    crate://crates.io/block-padding/0.3.3 \
    crate://crates.io/blocking/1.6.1 \
    crate://crates.io/blowfish/0.9.1 \
    crate://crates.io/bstr/1.9.1 \
    crate://crates.io/bumpalo/3.16.0 \
    crate://crates.io/byteorder/1.5.0 \
    crate://crates.io/bytes/1.8.0 \
    crate://crates.io/cbc/0.1.2 \
    crate://crates.io/cc/1.1.5 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/cfg_aliases/0.2.1 \
    crate://crates.io/chacha20/0.9.1 \
    crate://crates.io/chrono/0.4.38 \
    crate://crates.io/cipher/0.4.4 \
    crate://crates.io/color-eyre/0.6.3 \
    crate://crates.io/concurrent-queue/2.5.0 \
    crate://crates.io/config/0.13.4 \
    crate://crates.io/console/0.15.8 \
    crate://crates.io/const-oid/0.9.6 \
    crate://crates.io/const-random-macro/0.1.16 \
    crate://crates.io/const-random/0.1.18 \
    crate://crates.io/core-foundation-sys/0.8.6 \
    crate://crates.io/core-foundation/0.9.4 \
    crate://crates.io/cpufeatures/0.2.12 \
    crate://crates.io/crc32fast/1.4.2 \
    crate://crates.io/crossbeam-utils/0.8.20 \
    crate://crates.io/crunchy/0.2.2 \
    crate://crates.io/crypto-bigint/0.4.9 \
    crate://crates.io/crypto-bigint/0.5.5 \
    crate://crates.io/crypto-common/0.1.6 \
    crate://crates.io/ctr/0.9.2 \
    crate://crates.io/curve25519-dalek-derive/0.1.1 \
    crate://crates.io/curve25519-dalek/3.2.0 \
    crate://crates.io/curve25519-dalek/4.1.3 \
    crate://crates.io/data-encoding/2.6.0 \
    crate://crates.io/delegate/0.12.0 \
    crate://crates.io/der/0.6.1 \
    crate://crates.io/der/0.7.9 \
    crate://crates.io/deranged/0.3.11 \
    crate://crates.io/des/0.8.1 \
    crate://crates.io/diff/0.1.13 \
    crate://crates.io/digest/0.10.7 \
    crate://crates.io/digest/0.9.0 \
    crate://crates.io/doc-comment/0.3.3 \
    crate://crates.io/downcast-rs/1.2.1 \
    crate://crates.io/dyn-clone/1.0.17 \
    crate://crates.io/ecdsa/0.14.8 \
    crate://crates.io/ecdsa/0.16.9 \
    crate://crates.io/ed25519-dalek/1.0.1 \
    crate://crates.io/ed25519-dalek/2.1.1 \
    crate://crates.io/ed25519/1.5.3 \
    crate://crates.io/ed25519/2.2.3 \
    crate://crates.io/elliptic-curve/0.12.3 \
    crate://crates.io/elliptic-curve/0.13.8 \
    crate://crates.io/encode_unicode/0.3.6 \
    crate://crates.io/encoding_rs/0.8.34 \
    crate://crates.io/endi/1.1.0 \
    crate://crates.io/enum-iterator-derive/1.4.0 \
    crate://crates.io/enum-iterator/1.5.0 \
    crate://crates.io/enumflags2/0.7.10 \
    crate://crates.io/enumflags2_derive/0.7.10 \
    crate://crates.io/env_logger/0.10.2 \
    crate://crates.io/equivalent/1.0.1 \
    crate://crates.io/errno/0.3.9 \
    crate://crates.io/event-listener-strategy/0.5.2 \
    crate://crates.io/event-listener/5.3.1 \
    crate://crates.io/eyre/0.6.12 \
    crate://crates.io/fastrand/2.1.0 \
    crate://crates.io/ff/0.12.1 \
    crate://crates.io/ff/0.13.0 \
    crate://crates.io/fiat-crypto/0.2.9 \
    crate://crates.io/filedescriptor/0.8.2 \
    crate://crates.io/flate2/1.0.30 \
    crate://crates.io/flurry/0.5.1 \
    crate://crates.io/fnv/1.0.7 \
    crate://crates.io/form_urlencoded/1.2.1 \
    crate://crates.io/futures-channel/0.3.31 \
    crate://crates.io/futures-core/0.3.31 \
    crate://crates.io/futures-executor/0.3.30 \
    crate://crates.io/futures-io/0.3.31 \
    crate://crates.io/futures-lite/2.3.0 \
    crate://crates.io/futures-macro/0.3.31 \
    crate://crates.io/futures-sink/0.3.31 \
    crate://crates.io/futures-task/0.3.31 \
    crate://crates.io/futures-util/0.3.31 \
    crate://crates.io/futures/0.3.30 \
    crate://crates.io/generic-array/0.14.7 \
    crate://crates.io/getrandom/0.1.16 \
    crate://crates.io/getrandom/0.2.15 \
    crate://crates.io/getset/0.1.2 \
    crate://crates.io/ghash/0.5.1 \
    crate://crates.io/gimli/0.28.1 \
    crate://crates.io/git2/0.16.1 \
    crate://crates.io/globset/0.4.14 \
    crate://crates.io/group/0.12.1 \
    crate://crates.io/group/0.13.0 \
    crate://crates.io/h2/0.3.26 \
    crate://crates.io/hashbrown/0.14.5 \
    crate://crates.io/heck/0.4.1 \
    crate://crates.io/hermit-abi/0.3.9 \
    crate://crates.io/hermit-abi/0.4.0 \
    crate://crates.io/hex-literal/0.4.1 \
    crate://crates.io/hex/0.4.3 \
    crate://crates.io/hkdf/0.12.4 \
    crate://crates.io/hmac/0.12.1 \
    crate://crates.io/home/0.5.9 \
    crate://crates.io/http-body/0.4.6 \
    crate://crates.io/http/0.2.12 \
    crate://crates.io/httparse/1.9.4 \
    crate://crates.io/httpdate/1.0.3 \
    crate://crates.io/humantime/2.1.0 \
    crate://crates.io/hyper-rustls/0.24.2 \
    crate://crates.io/hyper/0.14.30 \
    crate://crates.io/iana-time-zone-haiku/0.1.2 \
    crate://crates.io/iana-time-zone/0.1.60 \
    crate://crates.io/idna/0.5.0 \
    crate://crates.io/indenter/0.3.3 \
    crate://crates.io/indexmap/2.2.6 \
    crate://crates.io/inout/0.1.3 \
    crate://crates.io/ioctl-rs/0.1.6 \
    crate://crates.io/ipnet/2.9.0 \
    crate://crates.io/is-terminal/0.4.12 \
    crate://crates.io/itoa/1.0.11 \
    crate://crates.io/jobserver/0.1.31 \
    crate://crates.io/js-sys/0.3.72 \
    crate://crates.io/lazy_static/1.5.0 \
    crate://crates.io/libc/0.2.155 \
    crate://crates.io/libgit2-sys/0.14.2+1.5.1 \
    crate://crates.io/libm/0.2.8 \
    crate://crates.io/libz-sys/1.1.18 \
    crate://crates.io/linux-raw-sys/0.4.14 \
    crate://crates.io/lock_api/0.4.12 \
    crate://crates.io/log/0.4.22 \
    crate://crates.io/matchit/0.7.3 \
    crate://crates.io/md5/0.7.0 \
    crate://crates.io/memchr/2.7.4 \
    crate://crates.io/memoffset/0.6.5 \
    crate://crates.io/memoffset/0.9.1 \
    crate://crates.io/mime/0.3.17 \
    crate://crates.io/minimal-lexical/0.2.1 \
    crate://crates.io/miniz_oxide/0.7.4 \
    crate://crates.io/mio/0.8.11 \
    crate://crates.io/nix/0.25.1 \
    crate://crates.io/nix/0.26.4 \
    crate://crates.io/nix/0.29.0 \
    crate://crates.io/nom/7.1.3 \
    crate://crates.io/ntapi/0.4.1 \
    crate://crates.io/num-bigint-dig/0.8.4 \
    crate://crates.io/num-bigint/0.4.6 \
    crate://crates.io/num-conv/0.1.0 \
    crate://crates.io/num-integer/0.1.46 \
    crate://crates.io/num-iter/0.1.45 \
    crate://crates.io/num-traits/0.2.19 \
    crate://crates.io/num_cpus/1.16.0 \
    crate://crates.io/object/0.32.2 \
    crate://crates.io/olpc-cjson/0.1.3 \
    crate://crates.io/once_cell/1.19.0 \
    crate://crates.io/opaque-debug/0.3.1 \
    crate://crates.io/ordered-stream/0.2.0 \
    crate://crates.io/owo-colors/3.5.0 \
    crate://crates.io/p256/0.11.1 \
    crate://crates.io/p256/0.13.2 \
    crate://crates.io/p384/0.11.2 \
    crate://crates.io/p384/0.13.0 \
    crate://crates.io/p521/0.13.3 \
    crate://crates.io/pageant/0.0.1-beta.3 \
    crate://crates.io/parking/2.2.0 \
    crate://crates.io/parking_lot/0.12.3 \
    crate://crates.io/parking_lot_core/0.9.10 \
    crate://crates.io/pathdiff/0.2.1 \
    crate://crates.io/pbkdf2/0.12.2 \
    crate://crates.io/pem-rfc7468/0.6.0 \
    crate://crates.io/pem-rfc7468/0.7.0 \
    crate://crates.io/pem/3.0.4 \
    crate://crates.io/percent-encoding/2.3.1 \
    crate://crates.io/pin-project-internal/1.1.5 \
    crate://crates.io/pin-project-lite/0.2.14 \
    crate://crates.io/pin-project/1.1.5 \
    crate://crates.io/pin-utils/0.1.0 \
    crate://crates.io/piper/0.2.3 \
    crate://crates.io/pkcs1/0.4.1 \
    crate://crates.io/pkcs1/0.7.5 \
    crate://crates.io/pkcs5/0.7.1 \
    crate://crates.io/pkcs8/0.10.2 \
    crate://crates.io/pkcs8/0.9.0 \
    crate://crates.io/pkg-config/0.3.30 \
    crate://crates.io/polling/3.7.2 \
    crate://crates.io/poly1305/0.8.0 \
    crate://crates.io/polyval/0.6.2 \
    crate://crates.io/portable-pty/0.8.1 \
    crate://crates.io/powerfmt/0.2.0 \
    crate://crates.io/ppv-lite86/0.2.17 \
    crate://crates.io/pretty_assertions/1.4.0 \
    crate://crates.io/primeorder/0.13.6 \
    crate://crates.io/proc-macro-crate/3.1.0 \
    crate://crates.io/proc-macro-error-attr/1.0.4 \
    crate://crates.io/proc-macro-error/1.0.4 \
    crate://crates.io/proc-macro2/1.0.86 \
    crate://crates.io/quote/1.0.36 \
    crate://crates.io/rand/0.7.3 \
    crate://crates.io/rand/0.8.5 \
    crate://crates.io/rand_chacha/0.2.2 \
    crate://crates.io/rand_chacha/0.3.1 \
    crate://crates.io/rand_core/0.5.1 \
    crate://crates.io/rand_core/0.6.4 \
    crate://crates.io/rand_hc/0.2.0 \
    crate://crates.io/redox_syscall/0.5.7 \
    crate://crates.io/regex-automata/0.4.7 \
    crate://crates.io/regex-syntax/0.8.4 \
    crate://crates.io/regex/1.10.5 \
    crate://crates.io/reqwest/0.11.27 \
    crate://crates.io/rfc6979/0.3.1 \
    crate://crates.io/rfc6979/0.4.0 \
    crate://crates.io/ring/0.17.8 \
    crate://crates.io/rsa/0.7.2 \
    crate://crates.io/rsa/0.9.6 \
    crate://crates.io/russh-cryptovec/0.7.3 \
    crate://crates.io/russh-keys/0.46.0 \
    crate://crates.io/russh-sftp/2.0.5 \
    crate://crates.io/russh-util/0.46.0 \
    crate://crates.io/russh/0.46.0 \
    crate://crates.io/rustc-demangle/0.1.24 \
    crate://crates.io/rustc_version/0.4.0 \
    crate://crates.io/rustix/0.38.34 \
    crate://crates.io/rustls-pemfile/1.0.4 \
    crate://crates.io/rustls-webpki/0.101.7 \
    crate://crates.io/rustls/0.21.12 \
    crate://crates.io/rustversion/1.0.17 \
    crate://crates.io/ryu/1.0.18 \
    crate://crates.io/salsa20/0.10.2 \
    crate://crates.io/same-file/1.0.6 \
    crate://crates.io/scopeguard/1.2.0 \
    crate://crates.io/scrypt/0.11.0 \
    crate://crates.io/sct/0.7.1 \
    crate://crates.io/sec1/0.3.0 \
    crate://crates.io/sec1/0.7.3 \
    crate://crates.io/seize/0.3.3 \
    crate://crates.io/semver/1.0.23 \
    crate://crates.io/serde/1.0.204 \
    crate://crates.io/serde_derive/1.0.204 \
    crate://crates.io/serde_json/1.0.120 \
    crate://crates.io/serde_path_to_error/0.1.16 \
    crate://crates.io/serde_plain/1.0.2 \
    crate://crates.io/serde_repr/0.1.19 \
    crate://crates.io/serde_urlencoded/0.7.1 \
    crate://crates.io/serial-core/0.4.0 \
    crate://crates.io/serial-unix/0.4.0 \
    crate://crates.io/serial-windows/0.4.0 \
    crate://crates.io/serial/0.4.0 \
    crate://crates.io/sha1/0.10.6 \
    crate://crates.io/sha2/0.10.8 \
    crate://crates.io/sha2/0.9.9 \
    crate://crates.io/shared_library/0.1.9 \
    crate://crates.io/shell-words/1.1.0 \
    crate://crates.io/signal-hook-registry/1.4.2 \
    crate://crates.io/signature/1.6.4 \
    crate://crates.io/signature/2.2.0 \
    crate://crates.io/slab/0.4.9 \
    crate://crates.io/smallvec/1.13.2 \
    crate://crates.io/snafu-derive/0.7.5 \
    crate://crates.io/snafu/0.7.5 \
    crate://crates.io/socket2/0.5.7 \
    crate://crates.io/spin/0.9.8 \
    crate://crates.io/spki/0.6.0 \
    crate://crates.io/spki/0.7.3 \
    crate://crates.io/ssh-cipher/0.2.0 \
    crate://crates.io/ssh-encoding/0.1.0 \
    crate://crates.io/ssh-encoding/0.2.0 \
    crate://crates.io/ssh-key/0.5.1 \
    crate://crates.io/ssh-key/0.6.7 \
    crate://crates.io/static_assertions/1.1.0 \
    crate://crates.io/subtle/2.6.1 \
    crate://crates.io/syn/1.0.109 \
    crate://crates.io/syn/2.0.71 \
    crate://crates.io/sync_wrapper/0.1.2 \
    crate://crates.io/sysinfo/0.27.8 \
    crate://crates.io/system-configuration-sys/0.5.0 \
    crate://crates.io/system-configuration/0.5.1 \
    crate://crates.io/tempfile/3.10.1 \
    crate://crates.io/termcolor/1.4.1 \
    crate://crates.io/termios/0.2.2 \
    crate://crates.io/thiserror-impl/1.0.63 \
    crate://crates.io/thiserror/1.0.63 \
    crate://crates.io/time-core/0.1.2 \
    crate://crates.io/time-macros/0.2.18 \
    crate://crates.io/time/0.3.36 \
    crate://crates.io/tiny-keccak/2.0.2 \
    crate://crates.io/tinyvec/1.8.0 \
    crate://crates.io/tinyvec_macros/0.1.1 \
    crate://crates.io/tokio-macros/2.3.0 \
    crate://crates.io/tokio-retry/0.3.0 \
    crate://crates.io/tokio-rustls/0.24.1 \
    crate://crates.io/tokio-stream/0.1.15 \
    crate://crates.io/tokio-util/0.7.11 \
    crate://crates.io/tokio/1.38.1 \
    crate://crates.io/toml/0.5.11 \
    crate://crates.io/toml_datetime/0.6.6 \
    crate://crates.io/toml_edit/0.21.1 \
    crate://crates.io/tower-layer/0.3.2 \
    crate://crates.io/tower-service/0.3.2 \
    crate://crates.io/tower/0.4.13 \
    crate://crates.io/tracing-attributes/0.1.27 \
    crate://crates.io/tracing-core/0.1.32 \
    crate://crates.io/tracing/0.1.40 \
    crate://crates.io/try-lock/0.2.5 \
    crate://crates.io/typed-path/0.7.1 \
    crate://crates.io/typenum/1.17.0 \
    crate://crates.io/uds_windows/1.1.0 \
    crate://crates.io/unicode-bidi/0.3.15 \
    crate://crates.io/unicode-ident/1.0.12 \
    crate://crates.io/unicode-normalization/0.1.23 \
    crate://crates.io/unicode-width/0.1.13 \
    crate://crates.io/universal-hash/0.5.1 \
    crate://crates.io/untrusted/0.9.0 \
    crate://crates.io/url/2.5.2 \
    crate://crates.io/uuid/1.10.0 \
    crate://crates.io/vcpkg/0.2.15 \
    crate://crates.io/vergen/7.5.1 \
    crate://crates.io/version_check/0.9.4 \
    crate://crates.io/walkdir/2.5.0 \
    crate://crates.io/want/0.3.1 \
    crate://crates.io/wasi/0.11.0+wasi-snapshot-preview1 \
    crate://crates.io/wasi/0.9.0+wasi-snapshot-preview1 \
    crate://crates.io/wasm-bindgen-backend/0.2.95 \
    crate://crates.io/wasm-bindgen-futures/0.4.45 \
    crate://crates.io/wasm-bindgen-macro-support/0.2.95 \
    crate://crates.io/wasm-bindgen-macro/0.2.95 \
    crate://crates.io/wasm-bindgen-shared/0.2.95 \
    crate://crates.io/wasm-bindgen/0.2.95 \
    crate://crates.io/wasm-streams/0.4.0 \
    crate://crates.io/web-sys/0.3.69 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-util/0.1.8 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi/0.3.9 \
    crate://crates.io/windows-core/0.52.0 \
    crate://crates.io/windows-core/0.58.0 \
    crate://crates.io/windows-implement/0.58.0 \
    crate://crates.io/windows-interface/0.58.0 \
    crate://crates.io/windows-result/0.2.0 \
    crate://crates.io/windows-strings/0.1.0 \
    crate://crates.io/windows-sys/0.48.0 \
    crate://crates.io/windows-sys/0.52.0 \
    crate://crates.io/windows-targets/0.48.5 \
    crate://crates.io/windows-targets/0.52.6 \
    crate://crates.io/windows/0.58.0 \
    crate://crates.io/windows_aarch64_gnullvm/0.48.5 \
    crate://crates.io/windows_aarch64_gnullvm/0.52.6 \
    crate://crates.io/windows_aarch64_msvc/0.48.5 \
    crate://crates.io/windows_aarch64_msvc/0.52.6 \
    crate://crates.io/windows_i686_gnu/0.48.5 \
    crate://crates.io/windows_i686_gnu/0.52.6 \
    crate://crates.io/windows_i686_gnullvm/0.52.6 \
    crate://crates.io/windows_i686_msvc/0.48.5 \
    crate://crates.io/windows_i686_msvc/0.52.6 \
    crate://crates.io/windows_x86_64_gnu/0.48.5 \
    crate://crates.io/windows_x86_64_gnu/0.52.6 \
    crate://crates.io/windows_x86_64_gnullvm/0.48.5 \
    crate://crates.io/windows_x86_64_gnullvm/0.52.6 \
    crate://crates.io/windows_x86_64_msvc/0.48.5 \
    crate://crates.io/windows_x86_64_msvc/0.52.6 \
    crate://crates.io/winnow/0.5.40 \
    crate://crates.io/winreg/0.10.1 \
    crate://crates.io/winreg/0.50.0 \
    crate://crates.io/xdg-home/1.2.0 \
    crate://crates.io/yansi/0.5.1 \
    crate://crates.io/zbus/4.3.1 \
    crate://crates.io/zbus_macros/4.3.1 \
    crate://crates.io/zbus_names/3.0.0 \
    crate://crates.io/zerocopy-derive/0.7.35 \
    crate://crates.io/zerocopy/0.7.35 \
    crate://crates.io/zeroize/1.8.1 \
    crate://crates.io/zeroize_derive/1.4.2 \
    crate://crates.io/zvariant/4.1.2 \
    crate://crates.io/zvariant_derive/4.1.2 \
    crate://crates.io/zvariant_utils/2.0.0 \
"

SRC_URI[addr2line-0.21.0.sha256sum] = "8a30b2e23b9e17a9f90641c7ab1549cd9b44f296d3ccbf309d2863cfe398a0cb"
SRC_URI[adler-1.0.2.sha256sum] = "f26201604c87b1e01bd3d98f8d5d9a8fcbb815e8cedb41ffccbeb4bf593a35fe"
SRC_URI[aead-0.5.2.sha256sum] = "d122413f284cf2d62fb1b7db97e02edb8cda96d769b16e443a4f6195e35662b0"
SRC_URI[aes-gcm-0.10.3.sha256sum] = "831010a0f742e1209b3bcea8fab6a8e149051ba6099432c8cb2cc117dec3ead1"
SRC_URI[aes-0.8.4.sha256sum] = "b169f7a6d4742236a0a00c541b845991d0ac43e546831af1249753ab4c3aa3a0"
SRC_URI[ahash-0.8.11.sha256sum] = "e89da841a80418a9b391ebaea17f5c112ffaaa96f621d2c285b5174da76b9011"
SRC_URI[aho-corasick-1.1.3.sha256sum] = "8e60d3430d3a69478ad0993f19238d2df97c507009a52b3c10addcd7f6bcb916"
SRC_URI[android-tzdata-0.1.1.sha256sum] = "e999941b234f3131b00bc13c22d06e8c5ff726d1b6318ac7eb276997bbb4fef0"
SRC_URI[android_system_properties-0.1.5.sha256sum] = "819e7219dbd41043ac279b19830f2efc897156490d7fd6ea916720117ee66311"
SRC_URI[anyhow-1.0.86.sha256sum] = "b3d1d046238990b9cf5bcde22a3fb3584ee5cf65fb2765f454ed428c7a0063da"
SRC_URI[async-broadcast-0.7.1.sha256sum] = "20cd0e2e25ea8e5f7e9df04578dc6cf5c83577fd09b1a46aaf5c85e1c33f2a7e"
SRC_URI[async-channel-2.3.1.sha256sum] = "89b47800b0be77592da0afd425cc03468052844aff33b84e33cc696f64e77b6a"
SRC_URI[async-executor-1.13.0.sha256sum] = "d7ebdfa2ebdab6b1760375fa7d6f382b9f486eac35fc994625a00e89280bdbb7"
SRC_URI[async-fs-2.1.2.sha256sum] = "ebcd09b382f40fcd159c2d695175b2ae620ffa5f3bd6f664131efff4e8b9e04a"
SRC_URI[async-io-2.3.3.sha256sum] = "0d6baa8f0178795da0e71bc42c9e5d13261aac7ee549853162e66a241ba17964"
SRC_URI[async-lock-3.4.0.sha256sum] = "ff6e472cdea888a4bd64f342f09b3f50e1886d32afe8df3d663c01140b811b18"
SRC_URI[async-process-2.2.3.sha256sum] = "f7eda79bbd84e29c2b308d1dc099d7de8dcc7035e48f4bf5dc4a531a44ff5e2a"
SRC_URI[async-recursion-1.1.1.sha256sum] = "3b43422f69d8ff38f95f1b2bb76517c91589a924d1559a0e935d7c8ce0274c11"
SRC_URI[async-signal-0.2.9.sha256sum] = "dfb3634b73397aa844481f814fad23bbf07fdb0eabec10f2eb95e58944b1ec32"
SRC_URI[async-task-4.7.1.sha256sum] = "8b75356056920673b02621b35afd0f7dda9306d03c79a30f5c56c44cf256e3de"
SRC_URI[async-trait-0.1.81.sha256sum] = "6e0c28dcc82d7c8ead5cb13beb15405b57b8546e93215673ff8ca0349a028107"
SRC_URI[atomic-waker-1.1.2.sha256sum] = "1505bd5d3d116872e7271a6d4e16d81d0c8570876c8de68093a09ac269d8aac0"
SRC_URI[autocfg-1.3.0.sha256sum] = "0c4b4d0bd25bd0b74681c0ad21497610ce1b7c91b1022cd21c80c6fbdd9476b0"
SRC_URI[axum-core-0.3.4.sha256sum] = "759fa577a247914fd3f7f76d62972792636412fbfd634cd452f6a385a74d2d2c"
SRC_URI[axum-0.6.20.sha256sum] = "3b829e4e32b91e643de6eafe82b1d90675f5874230191a4ffbc1b336dec4d6bf"
SRC_URI[backtrace-0.3.71.sha256sum] = "26b05800d2e817c8b3b4b54abd461726265fa9789ae34330622f2db9ee696f9d"
SRC_URI[base16ct-0.1.1.sha256sum] = "349a06037c7bf932dd7e7d1f653678b2038b9ad46a74102f1fc7bd7872678cce"
SRC_URI[base16ct-0.2.0.sha256sum] = "4c7f02d4ea65f2c1853089ffd8d2787bdbc63de2f0d29dedbcf8ccdfa0ccd4cf"
SRC_URI[base64-0.21.7.sha256sum] = "9d297deb1925b89f2ccc13d7635fa0714f12c87adce1c75356b39ca9b7178567"
SRC_URI[base64-0.22.1.sha256sum] = "72b3254f16251a8381aa12e40e3c4d2f0199f8c6508fbecb9d91f575e0fbb8c6"
SRC_URI[base64ct-1.6.0.sha256sum] = "8c3c1a368f70d6cf7302d78f8f7093da241fb8e8807c05cc9e51a125895a6d5b"
SRC_URI[bcrypt-pbkdf-0.10.0.sha256sum] = "6aeac2e1fe888769f34f05ac343bbef98b14d1ffb292ab69d4608b3abc86f2a2"
SRC_URI[bitflags-1.3.2.sha256sum] = "bef38d45163c2f1dde094a7dfd33ccf595c92905c8f8f4fdc18d06fb1037718a"
SRC_URI[bitflags-2.6.0.sha256sum] = "b048fb63fd8b5923fc5aa7b340d8e156aec7ec02f0c78fa8a6ddc2613f6f71de"
SRC_URI[block-buffer-0.10.4.sha256sum] = "3078c7629b62d3f0439517fa394996acacc5cbc91c5a20d8c658e77abd503a71"
SRC_URI[block-buffer-0.9.0.sha256sum] = "4152116fd6e9dadb291ae18fc1ec3575ed6d84c29642d97890f4b4a3417297e4"
SRC_URI[block-padding-0.3.3.sha256sum] = "a8894febbff9f758034a5b8e12d87918f56dfc64a8e1fe757d65e29041538d93"
SRC_URI[blocking-1.6.1.sha256sum] = "703f41c54fc768e63e091340b424302bb1c29ef4aa0c7f10fe849dfb114d29ea"
SRC_URI[blowfish-0.9.1.sha256sum] = "e412e2cd0f2b2d93e02543ceae7917b3c70331573df19ee046bcbc35e45e87d7"
SRC_URI[bstr-1.9.1.sha256sum] = "05efc5cfd9110c8416e471df0e96702d58690178e206e61b7173706673c93706"
SRC_URI[bumpalo-3.16.0.sha256sum] = "79296716171880943b8470b5f8d03aa55eb2e645a4874bdbb28adb49162e012c"
SRC_URI[byteorder-1.5.0.sha256sum] = "1fd0f2584146f6f2ef48085050886acf353beff7305ebd1ae69500e27c67f64b"
SRC_URI[bytes-1.8.0.sha256sum] = "9ac0150caa2ae65ca5bd83f25c7de183dea78d4d366469f148435e2acfbad0da"
SRC_URI[cbc-0.1.2.sha256sum] = "26b52a9543ae338f279b96b0b9fed9c8093744685043739079ce85cd58f289a6"
SRC_URI[cc-1.1.5.sha256sum] = "324c74f2155653c90b04f25b2a47a8a631360cb908f92a772695f430c7e31052"
SRC_URI[cfg-if-1.0.0.sha256sum] = "baf1de4339761588bc0619e3cbc0120ee582ebb74b53b4efbf79117bd2da40fd"
SRC_URI[cfg_aliases-0.2.1.sha256sum] = "613afe47fcd5fac7ccf1db93babcb082c5994d996f20b8b159f2ad1658eb5724"
SRC_URI[chacha20-0.9.1.sha256sum] = "c3613f74bd2eac03dad61bd53dbe620703d4371614fe0bc3b9f04dd36fe4e818"
SRC_URI[chrono-0.4.38.sha256sum] = "a21f936df1771bf62b77f047b726c4625ff2e8aa607c01ec06e5a05bd8463401"
SRC_URI[cipher-0.4.4.sha256sum] = "773f3b9af64447d2ce9850330c473515014aa235e6a783b02db81ff39e4a3dad"
SRC_URI[color-eyre-0.6.3.sha256sum] = "55146f5e46f237f7423d74111267d4597b59b0dad0ffaf7303bce9945d843ad5"
SRC_URI[concurrent-queue-2.5.0.sha256sum] = "4ca0197aee26d1ae37445ee532fefce43251d24cc7c166799f4d46817f1d3973"
SRC_URI[config-0.13.4.sha256sum] = "23738e11972c7643e4ec947840fc463b6a571afcd3e735bdfce7d03c7a784aca"
SRC_URI[console-0.15.8.sha256sum] = "0e1f83fc076bd6dd27517eacdf25fef6c4dfe5f1d7448bafaaf3a26f13b5e4eb"
SRC_URI[const-oid-0.9.6.sha256sum] = "c2459377285ad874054d797f3ccebf984978aa39129f6eafde5cdc8315b612f8"
SRC_URI[const-random-macro-0.1.16.sha256sum] = "f9d839f2a20b0aee515dc581a6172f2321f96cab76c1a38a4c584a194955390e"
SRC_URI[const-random-0.1.18.sha256sum] = "87e00182fe74b066627d63b85fd550ac2998d4b0bd86bfed477a0ae4c7c71359"
SRC_URI[core-foundation-sys-0.8.6.sha256sum] = "06ea2b9bc92be3c2baa9334a323ebca2d6f074ff852cd1d7b11064035cd3868f"
SRC_URI[core-foundation-0.9.4.sha256sum] = "91e195e091a93c46f7102ec7818a2aa394e1e1771c3ab4825963fa03e45afb8f"
SRC_URI[cpufeatures-0.2.12.sha256sum] = "53fe5e26ff1b7aef8bca9c6080520cfb8d9333c7568e1829cef191a9723e5504"
SRC_URI[crc32fast-1.4.2.sha256sum] = "a97769d94ddab943e4510d138150169a2758b5ef3eb191a9ee688de3e23ef7b3"
SRC_URI[crossbeam-utils-0.8.20.sha256sum] = "22ec99545bb0ed0ea7bb9b8e1e9122ea386ff8a48c0922e43f36d45ab09e0e80"
SRC_URI[crunchy-0.2.2.sha256sum] = "7a81dae078cea95a014a339291cec439d2f232ebe854a9d672b796c6afafa9b7"
SRC_URI[crypto-bigint-0.4.9.sha256sum] = "ef2b4b23cddf68b89b8f8069890e8c270d54e2d5fe1b143820234805e4cb17ef"
SRC_URI[crypto-bigint-0.5.5.sha256sum] = "0dc92fb57ca44df6db8059111ab3af99a63d5d0f8375d9972e319a379c6bab76"
SRC_URI[crypto-common-0.1.6.sha256sum] = "1bfb12502f3fc46cca1bb51ac28df9d618d813cdc3d2f25b9fe775a34af26bb3"
SRC_URI[ctr-0.9.2.sha256sum] = "0369ee1ad671834580515889b80f2ea915f23b8be8d0daa4bbaf2ac5c7590835"
SRC_URI[curve25519-dalek-derive-0.1.1.sha256sum] = "f46882e17999c6cc590af592290432be3bce0428cb0d5f8b6715e4dc7b383eb3"
SRC_URI[curve25519-dalek-3.2.0.sha256sum] = "0b9fdf9972b2bd6af2d913799d9ebc165ea4d2e65878e329d9c6b372c4491b61"
SRC_URI[curve25519-dalek-4.1.3.sha256sum] = "97fb8b7c4503de7d6ae7b42ab72a5a59857b4c937ec27a3d4539dba95b5ab2be"
SRC_URI[data-encoding-2.6.0.sha256sum] = "e8566979429cf69b49a5c740c60791108e86440e8be149bbea4fe54d2c32d6e2"
SRC_URI[delegate-0.12.0.sha256sum] = "4e018fccbeeb50ff26562ece792ed06659b9c2dae79ece77c4456bb10d9bf79b"
SRC_URI[der-0.6.1.sha256sum] = "f1a467a65c5e759bce6e65eaf91cc29f466cdc57cb65777bd646872a8a1fd4de"
SRC_URI[der-0.7.9.sha256sum] = "f55bf8e7b65898637379c1b74eb1551107c8294ed26d855ceb9fd1a09cfc9bc0"
SRC_URI[deranged-0.3.11.sha256sum] = "b42b6fa04a440b495c8b04d0e71b707c585f83cb9cb28cf8cd0d976c315e31b4"
SRC_URI[des-0.8.1.sha256sum] = "ffdd80ce8ce993de27e9f063a444a4d53ce8e8db4c1f00cc03af5ad5a9867a1e"
SRC_URI[diff-0.1.13.sha256sum] = "56254986775e3233ffa9c4d7d3faaf6d36a2c09d30b20687e9f88bc8bafc16c8"
SRC_URI[digest-0.10.7.sha256sum] = "9ed9a281f7bc9b7576e61468ba615a66a5c8cfdff42420a70aa82701a3b1e292"
SRC_URI[digest-0.9.0.sha256sum] = "d3dd60d1080a57a05ab032377049e0591415d2b31afd7028356dbf3cc6dcb066"
SRC_URI[doc-comment-0.3.3.sha256sum] = "fea41bba32d969b513997752735605054bc0dfa92b4c56bf1189f2e174be7a10"
SRC_URI[downcast-rs-1.2.1.sha256sum] = "75b325c5dbd37f80359721ad39aca5a29fb04c89279657cffdda8736d0c0b9d2"
SRC_URI[dyn-clone-1.0.17.sha256sum] = "0d6ef0072f8a535281e4876be788938b528e9a1d43900b82c2569af7da799125"
SRC_URI[ecdsa-0.14.8.sha256sum] = "413301934810f597c1d19ca71c8710e99a3f1ba28a0d2ebc01551a2daeea3c5c"
SRC_URI[ecdsa-0.16.9.sha256sum] = "ee27f32b5c5292967d2d4a9d7f1e0b0aed2c15daded5a60300e4abb9d8020bca"
SRC_URI[ed25519-dalek-1.0.1.sha256sum] = "c762bae6dcaf24c4c84667b8579785430908723d5c889f469d76a41d59cc7a9d"
SRC_URI[ed25519-dalek-2.1.1.sha256sum] = "4a3daa8e81a3963a60642bcc1f90a670680bd4a77535faa384e9d1c79d620871"
SRC_URI[ed25519-1.5.3.sha256sum] = "91cff35c70bba8a626e3185d8cd48cc11b5437e1a5bcd15b9b5fa3c64b6dfee7"
SRC_URI[ed25519-2.2.3.sha256sum] = "115531babc129696a58c64a4fef0a8bf9e9698629fb97e9e40767d235cfbcd53"
SRC_URI[elliptic-curve-0.12.3.sha256sum] = "e7bb888ab5300a19b8e5bceef25ac745ad065f3c9f7efc6de1b91958110891d3"
SRC_URI[elliptic-curve-0.13.8.sha256sum] = "b5e6043086bf7973472e0c7dff2142ea0b680d30e18d9cc40f267efbf222bd47"
SRC_URI[encode_unicode-0.3.6.sha256sum] = "a357d28ed41a50f9c765dbfe56cbc04a64e53e5fc58ba79fbc34c10ef3df831f"
SRC_URI[encoding_rs-0.8.34.sha256sum] = "b45de904aa0b010bce2ab45264d0631681847fa7b6f2eaa7dab7619943bc4f59"
SRC_URI[endi-1.1.0.sha256sum] = "a3d8a32ae18130a3c84dd492d4215c3d913c3b07c6b63c2eb3eb7ff1101ab7bf"
SRC_URI[enum-iterator-derive-1.4.0.sha256sum] = "a1ab991c1362ac86c61ab6f556cff143daa22e5a15e4e189df818b2fd19fe65b"
SRC_URI[enum-iterator-1.5.0.sha256sum] = "9fd242f399be1da0a5354aa462d57b4ab2b4ee0683cc552f7c007d2d12d36e94"
SRC_URI[enumflags2-0.7.10.sha256sum] = "d232db7f5956f3f14313dc2f87985c58bd2c695ce124c8cdd984e08e15ac133d"
SRC_URI[enumflags2_derive-0.7.10.sha256sum] = "de0d48a183585823424a4ce1aa132d174a6a81bd540895822eb4c8373a8e49e8"
SRC_URI[env_logger-0.10.2.sha256sum] = "4cd405aab171cb85d6735e5c8d9db038c17d3ca007a4d2c25f337935c3d90580"
SRC_URI[equivalent-1.0.1.sha256sum] = "5443807d6dff69373d433ab9ef5378ad8df50ca6298caf15de6e52e24aaf54d5"
SRC_URI[errno-0.3.9.sha256sum] = "534c5cf6194dfab3db3242765c03bbe257cf92f22b38f6bc0c58d59108a820ba"
SRC_URI[event-listener-strategy-0.5.2.sha256sum] = "0f214dc438f977e6d4e3500aaa277f5ad94ca83fbbd9b1a15713ce2344ccc5a1"
SRC_URI[event-listener-5.3.1.sha256sum] = "6032be9bd27023a771701cc49f9f053c751055f71efb2e0ae5c15809093675ba"
SRC_URI[eyre-0.6.12.sha256sum] = "7cd915d99f24784cdc19fd37ef22b97e3ff0ae756c7e492e9fbfe897d61e2aec"
SRC_URI[fastrand-2.1.0.sha256sum] = "9fc0510504f03c51ada170672ac806f1f105a88aa97a5281117e1ddc3368e51a"
SRC_URI[ff-0.12.1.sha256sum] = "d013fc25338cc558c5c2cfbad646908fb23591e2404481826742b651c9af7160"
SRC_URI[ff-0.13.0.sha256sum] = "ded41244b729663b1e574f1b4fb731469f69f79c17667b5d776b16cda0479449"
SRC_URI[fiat-crypto-0.2.9.sha256sum] = "28dea519a9695b9977216879a3ebfddf92f1c08c05d984f8996aecd6ecdc811d"
SRC_URI[filedescriptor-0.8.2.sha256sum] = "7199d965852c3bac31f779ef99cbb4537f80e952e2d6aa0ffeb30cce00f4f46e"
SRC_URI[flate2-1.0.30.sha256sum] = "5f54427cfd1c7829e2a139fcefea601bf088ebca651d2bf53ebc600eac295dae"
SRC_URI[flurry-0.5.1.sha256sum] = "037030493fadfabb7b5638c2d665c0d2d2e393d8fc7aff27926524cf98efd8c0"
SRC_URI[fnv-1.0.7.sha256sum] = "3f9eec918d3f24069decb9af1554cad7c880e2da24a9afd88aca000531ab82c1"
SRC_URI[form_urlencoded-1.2.1.sha256sum] = "e13624c2627564efccf4934284bdd98cbaa14e79b0b5a141218e507b3a823456"
SRC_URI[futures-channel-0.3.31.sha256sum] = "2dff15bf788c671c1934e366d07e30c1814a8ef514e1af724a602e8a2fbe1b10"
SRC_URI[futures-core-0.3.31.sha256sum] = "05f29059c0c2090612e8d742178b0580d2dc940c837851ad723096f87af6663e"
SRC_URI[futures-executor-0.3.30.sha256sum] = "a576fc72ae164fca6b9db127eaa9a9dda0d61316034f33a0a0d4eda41f02b01d"
SRC_URI[futures-io-0.3.31.sha256sum] = "9e5c1b78ca4aae1ac06c48a526a655760685149f0d465d21f37abfe57ce075c6"
SRC_URI[futures-lite-2.3.0.sha256sum] = "52527eb5074e35e9339c6b4e8d12600c7128b68fb25dcb9fa9dec18f7c25f3a5"
SRC_URI[futures-macro-0.3.31.sha256sum] = "162ee34ebcb7c64a8abebc059ce0fee27c2262618d7b60ed8faf72fef13c3650"
SRC_URI[futures-sink-0.3.31.sha256sum] = "e575fab7d1e0dcb8d0c7bcf9a63ee213816ab51902e6d244a95819acacf1d4f7"
SRC_URI[futures-task-0.3.31.sha256sum] = "f90f7dce0722e95104fcb095585910c0977252f286e354b5e3bd38902cd99988"
SRC_URI[futures-util-0.3.31.sha256sum] = "9fa08315bb612088cc391249efdc3bc77536f16c91f6cf495e6fbe85b20a4a81"
SRC_URI[futures-0.3.30.sha256sum] = "645c6916888f6cb6350d2550b80fb63e734897a8498abe35cfb732b6487804b0"
SRC_URI[generic-array-0.14.7.sha256sum] = "85649ca51fd72272d7821adaf274ad91c288277713d9c18820d8499a7ff69e9a"
SRC_URI[getrandom-0.1.16.sha256sum] = "8fc3cb4d91f53b50155bdcfd23f6a4c39ae1969c2ae85982b135750cccaf5fce"
SRC_URI[getrandom-0.2.15.sha256sum] = "c4567c8db10ae91089c99af84c68c38da3ec2f087c3f82960bcdbf3656b6f4d7"
SRC_URI[getset-0.1.2.sha256sum] = "e45727250e75cc04ff2846a66397da8ef2b3db8e40e0cef4df67950a07621eb9"
SRC_URI[ghash-0.5.1.sha256sum] = "f0d8a4362ccb29cb0b265253fb0a2728f592895ee6854fd9bc13f2ffda266ff1"
SRC_URI[gimli-0.28.1.sha256sum] = "4271d37baee1b8c7e4b708028c57d816cf9d2434acb33a549475f78c181f6253"
SRC_URI[git2-0.16.1.sha256sum] = "ccf7f68c2995f392c49fffb4f95ae2c873297830eb25c6bc4c114ce8f4562acc"
SRC_URI[globset-0.4.14.sha256sum] = "57da3b9b5b85bd66f31093f8c408b90a74431672542466497dcbdfdc02034be1"
SRC_URI[group-0.12.1.sha256sum] = "5dfbfb3a6cfbd390d5c9564ab283a0349b9b9fcd46a706c1eb10e0db70bfbac7"
SRC_URI[group-0.13.0.sha256sum] = "f0f9ef7462f7c099f518d754361858f86d8a07af53ba9af0fe635bbccb151a63"
SRC_URI[h2-0.3.26.sha256sum] = "81fe527a889e1532da5c525686d96d4c2e74cdd345badf8dfef9f6b39dd5f5e8"
SRC_URI[hashbrown-0.14.5.sha256sum] = "e5274423e17b7c9fc20b6e7e208532f9b19825d82dfd615708b70edd83df41f1"
SRC_URI[heck-0.4.1.sha256sum] = "95505c38b4572b2d910cecb0281560f54b440a19336cbbcb27bf6ce6adc6f5a8"
SRC_URI[hermit-abi-0.3.9.sha256sum] = "d231dfb89cfffdbc30e7fc41579ed6066ad03abda9e567ccafae602b97ec5024"
SRC_URI[hermit-abi-0.4.0.sha256sum] = "fbf6a919d6cf397374f7dfeeea91d974c7c0a7221d0d0f4f20d859d329e53fcc"
SRC_URI[hex-literal-0.4.1.sha256sum] = "6fe2267d4ed49bc07b63801559be28c718ea06c4738b7a03c94df7386d2cde46"
SRC_URI[hex-0.4.3.sha256sum] = "7f24254aa9a54b5c858eaee2f5bccdb46aaf0e486a595ed5fd8f86ba55232a70"
SRC_URI[hkdf-0.12.4.sha256sum] = "7b5f8eb2ad728638ea2c7d47a21db23b7b58a72ed6a38256b8a1849f15fbbdf7"
SRC_URI[hmac-0.12.1.sha256sum] = "6c49c37c09c17a53d937dfbb742eb3a961d65a994e6bcdcf37e7399d0cc8ab5e"
SRC_URI[home-0.5.9.sha256sum] = "e3d1354bf6b7235cb4a0576c2619fd4ed18183f689b12b006a0ee7329eeff9a5"
SRC_URI[http-body-0.4.6.sha256sum] = "7ceab25649e9960c0311ea418d17bee82c0dcec1bd053b5f9a66e265a693bed2"
SRC_URI[http-0.2.12.sha256sum] = "601cbb57e577e2f5ef5be8e7b83f0f63994f25aa94d673e54a92d5c516d101f1"
SRC_URI[httparse-1.9.4.sha256sum] = "0fcc0b4a115bf80b728eb8ea024ad5bd707b615bfed49e0665b6e0f86fd082d9"
SRC_URI[httpdate-1.0.3.sha256sum] = "df3b46402a9d5adb4c86a0cf463f42e19994e3ee891101b1841f30a545cb49a9"
SRC_URI[humantime-2.1.0.sha256sum] = "9a3a5bfb195931eeb336b2a7b4d761daec841b97f947d34394601737a7bba5e4"
SRC_URI[hyper-rustls-0.24.2.sha256sum] = "ec3efd23720e2049821a693cbc7e65ea87c72f1c58ff2f9522ff332b1491e590"
SRC_URI[hyper-0.14.30.sha256sum] = "a152ddd61dfaec7273fe8419ab357f33aee0d914c5f4efbf0d96fa749eea5ec9"
SRC_URI[iana-time-zone-haiku-0.1.2.sha256sum] = "f31827a206f56af32e590ba56d5d2d085f558508192593743f16b2306495269f"
SRC_URI[iana-time-zone-0.1.60.sha256sum] = "e7ffbb5a1b541ea2561f8c41c087286cc091e21e556a4f09a8f6cbf17b69b141"
SRC_URI[idna-0.5.0.sha256sum] = "634d9b1461af396cad843f47fdba5597a4f9e6ddd4bfb6ff5d85028c25cb12f6"
SRC_URI[indenter-0.3.3.sha256sum] = "ce23b50ad8242c51a442f3ff322d56b02f08852c77e4c0b4d3fd684abc89c683"
SRC_URI[indexmap-2.2.6.sha256sum] = "168fb715dda47215e360912c096649d23d58bf392ac62f73919e831745e40f26"
SRC_URI[inout-0.1.3.sha256sum] = "a0c10553d664a4d0bcff9f4215d0aac67a639cc68ef660840afe309b807bc9f5"
SRC_URI[ioctl-rs-0.1.6.sha256sum] = "f7970510895cee30b3e9128319f2cefd4bde883a39f38baa279567ba3a7eb97d"
SRC_URI[ipnet-2.9.0.sha256sum] = "8f518f335dce6725a761382244631d86cf0ccb2863413590b31338feb467f9c3"
SRC_URI[is-terminal-0.4.12.sha256sum] = "f23ff5ef2b80d608d61efee834934d862cd92461afc0560dedf493e4c033738b"
SRC_URI[itoa-1.0.11.sha256sum] = "49f1f14873335454500d59611f1cf4a4b0f786f9ac11f4312a78e4cf2566695b"
SRC_URI[jobserver-0.1.31.sha256sum] = "d2b099aaa34a9751c5bf0878add70444e1ed2dd73f347be99003d4577277de6e"
SRC_URI[js-sys-0.3.72.sha256sum] = "6a88f1bda2bd75b0452a14784937d796722fdebfe50df998aeb3f0b7603019a9"
SRC_URI[lazy_static-1.5.0.sha256sum] = "bbd2bcb4c963f2ddae06a2efc7e9f3591312473c50c6685e1f298068316e66fe"
SRC_URI[libc-0.2.155.sha256sum] = "97b3888a4aecf77e811145cadf6eef5901f4782c53886191b2f693f24761847c"
SRC_URI[libgit2-sys-0.14.2+1.5.1.sha256sum] = "7f3d95f6b51075fe9810a7ae22c7095f12b98005ab364d8544797a825ce946a4"
SRC_URI[libm-0.2.8.sha256sum] = "4ec2a862134d2a7d32d7983ddcdd1c4923530833c9f2ea1a44fc5fa473989058"
SRC_URI[libz-sys-1.1.18.sha256sum] = "c15da26e5af7e25c90b37a2d75cdbf940cf4a55316de9d84c679c9b8bfabf82e"
SRC_URI[linux-raw-sys-0.4.14.sha256sum] = "78b3ae25bc7c8c38cec158d1f2757ee79e9b3740fbc7ccf0e59e4b08d793fa89"
SRC_URI[lock_api-0.4.12.sha256sum] = "07af8b9cdd281b7915f413fa73f29ebd5d55d0d3f0155584dade1ff18cea1b17"
SRC_URI[log-0.4.22.sha256sum] = "a7a70ba024b9dc04c27ea2f0c0548feb474ec5c54bba33a7f72f873a39d07b24"
SRC_URI[matchit-0.7.3.sha256sum] = "0e7465ac9959cc2b1404e8e2367b43684a6d13790fe23056cc8c6c5a6b7bcb94"
SRC_URI[md5-0.7.0.sha256sum] = "490cc448043f947bae3cbee9c203358d62dbee0db12107a74be5c30ccfd09771"
SRC_URI[memchr-2.7.4.sha256sum] = "78ca9ab1a0babb1e7d5695e3530886289c18cf2f87ec19a575a0abdce112e3a3"
SRC_URI[memoffset-0.6.5.sha256sum] = "5aa361d4faea93603064a027415f07bd8e1d5c88c9fbf68bf56a285428fd79ce"
SRC_URI[memoffset-0.9.1.sha256sum] = "488016bfae457b036d996092f6cb448677611ce4449e970ceaf42695203f218a"
SRC_URI[mime-0.3.17.sha256sum] = "6877bb514081ee2a7ff5ef9de3281f14a4dd4bceac4c09388074a6b5df8a139a"
SRC_URI[minimal-lexical-0.2.1.sha256sum] = "68354c5c6bd36d73ff3feceb05efa59b6acb7626617f4962be322a825e61f79a"
SRC_URI[miniz_oxide-0.7.4.sha256sum] = "b8a240ddb74feaf34a79a7add65a741f3167852fba007066dcac1ca548d89c08"
SRC_URI[mio-0.8.11.sha256sum] = "a4a650543ca06a924e8b371db273b2756685faae30f8487da1b56505a8f78b0c"
SRC_URI[nix-0.25.1.sha256sum] = "f346ff70e7dbfd675fe90590b92d59ef2de15a8779ae305ebcbfd3f0caf59be4"
SRC_URI[nix-0.26.4.sha256sum] = "598beaf3cc6fdd9a5dfb1630c2800c7acd31df7aaf0f565796fba2b53ca1af1b"
SRC_URI[nix-0.29.0.sha256sum] = "71e2746dc3a24dd78b3cfcb7be93368c6de9963d30f43a6a73998a9cf4b17b46"
SRC_URI[nom-7.1.3.sha256sum] = "d273983c5a657a70a3e8f2a01329822f3b8c8172b73826411a55751e404a0a4a"
SRC_URI[ntapi-0.4.1.sha256sum] = "e8a3895c6391c39d7fe7ebc444a87eb2991b2a0bc718fdabd071eec617fc68e4"
SRC_URI[num-bigint-dig-0.8.4.sha256sum] = "dc84195820f291c7697304f3cbdadd1cb7199c0efc917ff5eafd71225c136151"
SRC_URI[num-bigint-0.4.6.sha256sum] = "a5e44f723f1133c9deac646763579fdb3ac745e418f2a7af9cd0c431da1f20b9"
SRC_URI[num-conv-0.1.0.sha256sum] = "51d515d32fb182ee37cda2ccdcb92950d6a3c2893aa280e540671c2cd0f3b1d9"
SRC_URI[num-integer-0.1.46.sha256sum] = "7969661fd2958a5cb096e56c8e1ad0444ac2bbcd0061bd28660485a44879858f"
SRC_URI[num-iter-0.1.45.sha256sum] = "1429034a0490724d0075ebb2bc9e875d6503c3cf69e235a8941aa757d83ef5bf"
SRC_URI[num-traits-0.2.19.sha256sum] = "071dfc062690e90b734c0b2273ce72ad0ffa95f0c74596bc250dcfd960262841"
SRC_URI[num_cpus-1.16.0.sha256sum] = "4161fcb6d602d4d2081af7c3a45852d875a03dd337a6bfdd6e06407b61342a43"
SRC_URI[object-0.32.2.sha256sum] = "a6a622008b6e321afc04970976f62ee297fdbaa6f95318ca343e3eebb9648441"
SRC_URI[olpc-cjson-0.1.3.sha256sum] = "d637c9c15b639ccff597da8f4fa968300651ad2f1e968aefc3b4927a6fb2027a"
SRC_URI[once_cell-1.19.0.sha256sum] = "3fdb12b2476b595f9358c5161aa467c2438859caa136dec86c26fdd2efe17b92"
SRC_URI[opaque-debug-0.3.1.sha256sum] = "c08d65885ee38876c4f86fa503fb49d7b507c2b62552df7c70b2fce627e06381"
SRC_URI[ordered-stream-0.2.0.sha256sum] = "9aa2b01e1d916879f73a53d01d1d6cee68adbb31d6d9177a8cfce093cced1d50"
SRC_URI[owo-colors-3.5.0.sha256sum] = "c1b04fb49957986fdce4d6ee7a65027d55d4b6d2265e5848bbb507b58ccfdb6f"
SRC_URI[p256-0.11.1.sha256sum] = "51f44edd08f51e2ade572f141051021c5af22677e42b7dd28a88155151c33594"
SRC_URI[p256-0.13.2.sha256sum] = "c9863ad85fa8f4460f9c48cb909d38a0d689dba1f6f6988a5e3e0d31071bcd4b"
SRC_URI[p384-0.11.2.sha256sum] = "dfc8c5bf642dde52bb9e87c0ecd8ca5a76faac2eeed98dedb7c717997e1080aa"
SRC_URI[p384-0.13.0.sha256sum] = "70786f51bcc69f6a4c0360e063a4cac5419ef7c5cd5b3c99ad70f3be5ba79209"
SRC_URI[p521-0.13.3.sha256sum] = "0fc9e2161f1f215afdfce23677034ae137bbd45016a880c2eb3ba8eb95f085b2"
SRC_URI[pageant-0.0.1-beta.3.sha256sum] = "2c8ca7f786256e4d89f369656546b1f504cfd2d5ec796f0b99600919dd993723"
SRC_URI[parking-2.2.0.sha256sum] = "bb813b8af86854136c6922af0598d719255ecb2179515e6e7730d468f05c9cae"
SRC_URI[parking_lot-0.12.3.sha256sum] = "f1bf18183cf54e8d6059647fc3063646a1801cf30896933ec2311622cc4b9a27"
SRC_URI[parking_lot_core-0.9.10.sha256sum] = "1e401f977ab385c9e4e3ab30627d6f26d00e2c73eef317493c4ec6d468726cf8"
SRC_URI[pathdiff-0.2.1.sha256sum] = "8835116a5c179084a830efb3adc117ab007512b535bc1a21c991d3b32a6b44dd"
SRC_URI[pbkdf2-0.12.2.sha256sum] = "f8ed6a7761f76e3b9f92dfb0a60a6a6477c61024b775147ff0973a02653abaf2"
SRC_URI[pem-rfc7468-0.6.0.sha256sum] = "24d159833a9105500e0398934e205e0773f0b27529557134ecfc51c27646adac"
SRC_URI[pem-rfc7468-0.7.0.sha256sum] = "88b39c9bfcfc231068454382784bb460aae594343fb030d46e9f50a645418412"
SRC_URI[pem-3.0.4.sha256sum] = "8e459365e590736a54c3fa561947c84837534b8e9af6fc5bf781307e82658fae"
SRC_URI[percent-encoding-2.3.1.sha256sum] = "e3148f5046208a5d56bcfc03053e3ca6334e51da8dfb19b6cdc8b306fae3283e"
SRC_URI[pin-project-internal-1.1.5.sha256sum] = "2f38a4412a78282e09a2cf38d195ea5420d15ba0602cb375210efbc877243965"
SRC_URI[pin-project-lite-0.2.14.sha256sum] = "bda66fc9667c18cb2758a2ac84d1167245054bcf85d5d1aaa6923f45801bdd02"
SRC_URI[pin-project-1.1.5.sha256sum] = "b6bf43b791c5b9e34c3d182969b4abb522f9343702850a2e57f460d00d09b4b3"
SRC_URI[pin-utils-0.1.0.sha256sum] = "8b870d8c151b6f2fb93e84a13146138f05d02ed11c7e7c54f8826aaaf7c9f184"
SRC_URI[piper-0.2.3.sha256sum] = "ae1d5c74c9876f070d3e8fd503d748c7d974c3e48da8f41350fa5222ef9b4391"
SRC_URI[pkcs1-0.4.1.sha256sum] = "eff33bdbdfc54cc98a2eca766ebdec3e1b8fb7387523d5c9c9a2891da856f719"
SRC_URI[pkcs1-0.7.5.sha256sum] = "c8ffb9f10fa047879315e6625af03c164b16962a5368d724ed16323b68ace47f"
SRC_URI[pkcs5-0.7.1.sha256sum] = "e847e2c91a18bfa887dd028ec33f2fe6f25db77db3619024764914affe8b69a6"
SRC_URI[pkcs8-0.10.2.sha256sum] = "f950b2377845cebe5cf8b5165cb3cc1a5e0fa5cfa3e1f7f55707d8fd82e0a7b7"
SRC_URI[pkcs8-0.9.0.sha256sum] = "9eca2c590a5f85da82668fa685c09ce2888b9430e83299debf1f34b65fd4a4ba"
SRC_URI[pkg-config-0.3.30.sha256sum] = "d231b230927b5e4ad203db57bbcbee2802f6bce620b1e4a9024a07d94e2907ec"
SRC_URI[polling-3.7.2.sha256sum] = "a3ed00ed3fbf728b5816498ecd316d1716eecaced9c0c8d2c5a6740ca214985b"
SRC_URI[poly1305-0.8.0.sha256sum] = "8159bd90725d2df49889a078b54f4f79e87f1f8a8444194cdca81d38f5393abf"
SRC_URI[polyval-0.6.2.sha256sum] = "9d1fe60d06143b2430aa532c94cfe9e29783047f06c0d7fd359a9a51b729fa25"
SRC_URI[portable-pty-0.8.1.sha256sum] = "806ee80c2a03dbe1a9fb9534f8d19e4c0546b790cde8fd1fea9d6390644cb0be"
SRC_URI[powerfmt-0.2.0.sha256sum] = "439ee305def115ba05938db6eb1644ff94165c5ab5e9420d1c1bcedbba909391"
SRC_URI[ppv-lite86-0.2.17.sha256sum] = "5b40af805b3121feab8a3c29f04d8ad262fa8e0561883e7653e024ae4479e6de"
SRC_URI[pretty_assertions-1.4.0.sha256sum] = "af7cee1a6c8a5b9208b3cb1061f10c0cb689087b3d8ce85fb9d2dd7a29b6ba66"
SRC_URI[primeorder-0.13.6.sha256sum] = "353e1ca18966c16d9deb1c69278edbc5f194139612772bd9537af60ac231e1e6"
SRC_URI[proc-macro-crate-3.1.0.sha256sum] = "6d37c51ca738a55da99dc0c4a34860fd675453b8b36209178c2249bb13651284"
SRC_URI[proc-macro-error-attr-1.0.4.sha256sum] = "a1be40180e52ecc98ad80b184934baf3d0d29f979574e439af5a55274b35f869"
SRC_URI[proc-macro-error-1.0.4.sha256sum] = "da25490ff9892aab3fcf7c36f08cfb902dd3e71ca0f9f9517bea02a73a5ce38c"
SRC_URI[proc-macro2-1.0.86.sha256sum] = "5e719e8df665df0d1c8fbfd238015744736151d4445ec0836b8e628aae103b77"
SRC_URI[quote-1.0.36.sha256sum] = "0fa76aaf39101c457836aec0ce2316dbdc3ab723cdda1c6bd4e6ad4208acaca7"
SRC_URI[rand-0.7.3.sha256sum] = "6a6b1679d49b24bbfe0c803429aa1874472f50d9b363131f0e89fc356b544d03"
SRC_URI[rand-0.8.5.sha256sum] = "34af8d1a0e25924bc5b7c43c079c942339d8f0a8b57c39049bef581b46327404"
SRC_URI[rand_chacha-0.2.2.sha256sum] = "f4c8ed856279c9737206bf725bf36935d8666ead7aa69b52be55af369d193402"
SRC_URI[rand_chacha-0.3.1.sha256sum] = "e6c10a63a0fa32252be49d21e7709d4d4baf8d231c2dbce1eaa8141b9b127d88"
SRC_URI[rand_core-0.5.1.sha256sum] = "90bde5296fc891b0cef12a6d03ddccc162ce7b2aff54160af9338f8d40df6d19"
SRC_URI[rand_core-0.6.4.sha256sum] = "ec0be4795e2f6a28069bec0b5ff3e2ac9bafc99e6a9a7dc3547996c5c816922c"
SRC_URI[rand_hc-0.2.0.sha256sum] = "ca3129af7b92a17112d59ad498c6f81eaf463253766b90396d39ea7a39d6613c"
SRC_URI[redox_syscall-0.5.7.sha256sum] = "9b6dfecf2c74bce2466cabf93f6664d6998a69eb21e39f4207930065b27b771f"
SRC_URI[regex-automata-0.4.7.sha256sum] = "38caf58cc5ef2fed281f89292ef23f6365465ed9a41b7a7754eb4e26496c92df"
SRC_URI[regex-syntax-0.8.4.sha256sum] = "7a66a03ae7c801facd77a29370b4faec201768915ac14a721ba36f20bc9c209b"
SRC_URI[regex-1.10.5.sha256sum] = "b91213439dad192326a0d7c6ee3955910425f441d7038e0d6933b0aec5c4517f"
SRC_URI[reqwest-0.11.27.sha256sum] = "dd67538700a17451e7cba03ac727fb961abb7607553461627b97de0b89cf4a62"
SRC_URI[rfc6979-0.3.1.sha256sum] = "7743f17af12fa0b03b803ba12cd6a8d9483a587e89c69445e3909655c0b9fabb"
SRC_URI[rfc6979-0.4.0.sha256sum] = "f8dd2a808d456c4a54e300a23e9f5a67e122c3024119acbfd73e3bf664491cb2"
SRC_URI[ring-0.17.8.sha256sum] = "c17fa4cb658e3583423e915b9f3acc01cceaee1860e33d59ebae66adc3a2dc0d"
SRC_URI[rsa-0.7.2.sha256sum] = "094052d5470cbcef561cb848a7209968c9f12dfa6d668f4bca048ac5de51099c"
SRC_URI[rsa-0.9.6.sha256sum] = "5d0e5124fcb30e76a7e79bfee683a2746db83784b86289f6251b54b7950a0dfc"
SRC_URI[russh-cryptovec-0.7.3.sha256sum] = "fadd2c0ab350e21c66556f94ee06f766d8bdae3213857ba7610bfd8e10e51880"
SRC_URI[russh-keys-0.46.0.sha256sum] = "6e3db166c8678c824627c2c46f619ed5ce4ae33f38a35403c62f6ab8f3985867"
SRC_URI[russh-sftp-2.0.5.sha256sum] = "3a01c4a01f72b31d3a7d5fb039cbfe4c5cc72da2b0182aa31d38c573444e1ae9"
SRC_URI[russh-util-0.46.0.sha256sum] = "63aeb9d2b74f8f38befdc0c5172d5ffcf58f3d2ffcb423f3b6cdfe2c2d747b80"
SRC_URI[russh-0.46.0.sha256sum] = "c536b90d8e2468d8dedc8de2369383c101325e23fffa3a30de713032862a11d4"
SRC_URI[rustc-demangle-0.1.24.sha256sum] = "719b953e2095829ee67db738b3bfa9fa368c94900df327b3f07fe6e794d2fe1f"
SRC_URI[rustc_version-0.4.0.sha256sum] = "bfa0f585226d2e68097d4f95d113b15b83a82e819ab25717ec0590d9584ef366"
SRC_URI[rustix-0.38.34.sha256sum] = "70dc5ec042f7a43c4a73241207cecc9873a06d45debb38b329f8541d85c2730f"
SRC_URI[rustls-pemfile-1.0.4.sha256sum] = "1c74cae0a4cf6ccbbf5f359f08efdf8ee7e1dc532573bf0db71968cb56b1448c"
SRC_URI[rustls-webpki-0.101.7.sha256sum] = "8b6275d1ee7a1cd780b64aca7726599a1dbc893b1e64144529e55c3c2f745765"
SRC_URI[rustls-0.21.12.sha256sum] = "3f56a14d1f48b391359b22f731fd4bd7e43c97f3c50eee276f3aa09c94784d3e"
SRC_URI[rustversion-1.0.17.sha256sum] = "955d28af4278de8121b7ebeb796b6a45735dc01436d898801014aced2773a3d6"
SRC_URI[ryu-1.0.18.sha256sum] = "f3cb5ba0dc43242ce17de99c180e96db90b235b8a9fdc9543c96d2209116bd9f"
SRC_URI[salsa20-0.10.2.sha256sum] = "97a22f5af31f73a954c10289c93e8a50cc23d971e80ee446f1f6f7137a088213"
SRC_URI[same-file-1.0.6.sha256sum] = "93fc1dc3aaa9bfed95e02e6eadabb4baf7e3078b0bd1b4d7b6b0b68378900502"
SRC_URI[scopeguard-1.2.0.sha256sum] = "94143f37725109f92c262ed2cf5e59bce7498c01bcc1502d7b9afe439a4e9f49"
SRC_URI[scrypt-0.11.0.sha256sum] = "0516a385866c09368f0b5bcd1caff3366aace790fcd46e2bb032697bb172fd1f"
SRC_URI[sct-0.7.1.sha256sum] = "da046153aa2352493d6cb7da4b6e5c0c057d8a1d0a9aa8560baffdd945acd414"
SRC_URI[sec1-0.3.0.sha256sum] = "3be24c1842290c45df0a7bf069e0c268a747ad05a192f2fd7dcfdbc1cba40928"
SRC_URI[sec1-0.7.3.sha256sum] = "d3e97a565f76233a6003f9f5c54be1d9c5bdfa3eccfb189469f11ec4901c47dc"
SRC_URI[seize-0.3.3.sha256sum] = "689224d06523904ebcc9b482c6a3f4f7fb396096645c4cd10c0d2ff7371a34d3"
SRC_URI[semver-1.0.23.sha256sum] = "61697e0a1c7e512e84a621326239844a24d8207b4669b41bc18b32ea5cbf988b"
SRC_URI[serde-1.0.204.sha256sum] = "bc76f558e0cbb2a839d37354c575f1dc3fdc6546b5be373ba43d95f231bf7c12"
SRC_URI[serde_derive-1.0.204.sha256sum] = "e0cd7e117be63d3c3678776753929474f3b04a43a080c744d6b0ae2a8c28e222"
SRC_URI[serde_json-1.0.120.sha256sum] = "4e0d21c9a8cae1235ad58a00c11cb40d4b1e5c784f1ef2c537876ed6ffd8b7c5"
SRC_URI[serde_path_to_error-0.1.16.sha256sum] = "af99884400da37c88f5e9146b7f1fd0fbcae8f6eec4e9da38b67d05486f814a6"
SRC_URI[serde_plain-1.0.2.sha256sum] = "9ce1fc6db65a611022b23a0dec6975d63fb80a302cb3388835ff02c097258d50"
SRC_URI[serde_repr-0.1.19.sha256sum] = "6c64451ba24fc7a6a2d60fc75dd9c83c90903b19028d4eff35e88fc1e86564e9"
SRC_URI[serde_urlencoded-0.7.1.sha256sum] = "d3491c14715ca2294c4d6a88f15e84739788c1d030eed8c110436aafdaa2f3fd"
SRC_URI[serial-core-0.4.0.sha256sum] = "3f46209b345401737ae2125fe5b19a77acce90cd53e1658cda928e4fe9a64581"
SRC_URI[serial-unix-0.4.0.sha256sum] = "f03fbca4c9d866e24a459cbca71283f545a37f8e3e002ad8c70593871453cab7"
SRC_URI[serial-windows-0.4.0.sha256sum] = "15c6d3b776267a75d31bbdfd5d36c0ca051251caafc285827052bc53bcdc8162"
SRC_URI[serial-0.4.0.sha256sum] = "a1237a96570fc377c13baa1b88c7589ab66edced652e43ffb17088f003db3e86"
SRC_URI[sha1-0.10.6.sha256sum] = "e3bf829a2d51ab4a5ddf1352d8470c140cadc8301b2ae1789db023f01cedd6ba"
SRC_URI[sha2-0.10.8.sha256sum] = "793db75ad2bcafc3ffa7c68b215fee268f537982cd901d132f89c6343f3a3dc8"
SRC_URI[sha2-0.9.9.sha256sum] = "4d58a1e1bf39749807d89cf2d98ac2dfa0ff1cb3faa38fbb64dd88ac8013d800"
SRC_URI[shared_library-0.1.9.sha256sum] = "5a9e7e0f2bfae24d8a5b5a66c5b257a83c7412304311512a0c054cd5e619da11"
SRC_URI[shell-words-1.1.0.sha256sum] = "24188a676b6ae68c3b2cb3a01be17fbf7240ce009799bb56d5b1409051e78fde"
SRC_URI[signal-hook-registry-1.4.2.sha256sum] = "a9e9e0b4211b72e7b8b6e85c807d36c212bdb33ea8587f7569562a84df5465b1"
SRC_URI[signature-1.6.4.sha256sum] = "74233d3b3b2f6d4b006dc19dee745e73e2a6bfb6f93607cd3b02bd5b00797d7c"
SRC_URI[signature-2.2.0.sha256sum] = "77549399552de45a898a580c1b41d445bf730df867cc44e6c0233bbc4b8329de"
SRC_URI[slab-0.4.9.sha256sum] = "8f92a496fb766b417c996b9c5e57daf2f7ad3b0bebe1ccfca4856390e3d3bb67"
SRC_URI[smallvec-1.13.2.sha256sum] = "3c5e1a9a646d36c3599cd173a41282daf47c44583ad367b8e6837255952e5c67"
SRC_URI[snafu-derive-0.7.5.sha256sum] = "990079665f075b699031e9c08fd3ab99be5029b96f3b78dc0709e8f77e4efebf"
SRC_URI[snafu-0.7.5.sha256sum] = "e4de37ad025c587a29e8f3f5605c00f70b98715ef90b9061a815b9e59e9042d6"
SRC_URI[socket2-0.5.7.sha256sum] = "ce305eb0b4296696835b71df73eb912e0f1ffd2556a501fcede6e0c50349191c"
SRC_URI[spin-0.9.8.sha256sum] = "6980e8d7511241f8acf4aebddbb1ff938df5eebe98691418c4468d0b72a96a67"
SRC_URI[spki-0.6.0.sha256sum] = "67cf02bbac7a337dc36e4f5a693db6c21e7863f45070f7064577eb4367a3212b"
SRC_URI[spki-0.7.3.sha256sum] = "d91ed6c858b01f942cd56b37a94b3e0a1798290327d1236e4d9cf4eaca44d29d"
SRC_URI[ssh-cipher-0.2.0.sha256sum] = "caac132742f0d33c3af65bfcde7f6aa8f62f0e991d80db99149eb9d44708784f"
SRC_URI[ssh-encoding-0.1.0.sha256sum] = "19cfdc32e0199062113edf41f344fbf784b8205a94600233c84eb838f45191e1"
SRC_URI[ssh-encoding-0.2.0.sha256sum] = "eb9242b9ef4108a78e8cd1a2c98e193ef372437f8c22be363075233321dd4a15"
SRC_URI[ssh-key-0.5.1.sha256sum] = "288d8f5562af5a3be4bda308dd374b2c807b940ac370b5efa1c99311da91d9a1"
SRC_URI[ssh-key-0.6.7.sha256sum] = "3b86f5297f0f04d08cabaa0f6bff7cb6aec4d9c3b49d87990d63da9d9156a8c3"
SRC_URI[static_assertions-1.1.0.sha256sum] = "a2eb9349b6444b326872e140eb1cf5e7c522154d69e7a0ffb0fb81c06b37543f"
SRC_URI[subtle-2.6.1.sha256sum] = "13c2bddecc57b384dee18652358fb23172facb8a2c51ccc10d74c157bdea3292"
SRC_URI[syn-1.0.109.sha256sum] = "72b64191b275b66ffe2469e8af2c1cfe3bafa67b529ead792a6d0160888b4237"
SRC_URI[syn-2.0.71.sha256sum] = "b146dcf730474b4bcd16c311627b31ede9ab149045db4d6088b3becaea046462"
SRC_URI[sync_wrapper-0.1.2.sha256sum] = "2047c6ded9c721764247e62cd3b03c09ffc529b2ba5b10ec482ae507a4a70160"
SRC_URI[sysinfo-0.27.8.sha256sum] = "a902e9050fca0a5d6877550b769abd2bd1ce8c04634b941dbe2809735e1a1e33"
SRC_URI[system-configuration-sys-0.5.0.sha256sum] = "a75fb188eb626b924683e3b95e3a48e63551fcfb51949de2f06a9d91dbee93c9"
SRC_URI[system-configuration-0.5.1.sha256sum] = "ba3a3adc5c275d719af8cb4272ea1c4a6d668a777f37e115f6d11ddbc1c8e0e7"
SRC_URI[tempfile-3.10.1.sha256sum] = "85b77fafb263dd9d05cbeac119526425676db3784113aa9295c88498cbf8bff1"
SRC_URI[termcolor-1.4.1.sha256sum] = "06794f8f6c5c898b3275aebefa6b8a1cb24cd2c6c79397ab15774837a0bc5755"
SRC_URI[termios-0.2.2.sha256sum] = "d5d9cf598a6d7ce700a4e6a9199da127e6819a61e64b68609683cc9a01b5683a"
SRC_URI[thiserror-impl-1.0.63.sha256sum] = "a4558b58466b9ad7ca0f102865eccc95938dca1a74a856f2b57b6629050da261"
SRC_URI[thiserror-1.0.63.sha256sum] = "c0342370b38b6a11b6cc11d6a805569958d54cfa061a29969c3b5ce2ea405724"
SRC_URI[time-core-0.1.2.sha256sum] = "ef927ca75afb808a4d64dd374f00a2adf8d0fcff8e7b184af886c3c87ec4a3f3"
SRC_URI[time-macros-0.2.18.sha256sum] = "3f252a68540fde3a3877aeea552b832b40ab9a69e318efd078774a01ddee1ccf"
SRC_URI[time-0.3.36.sha256sum] = "5dfd88e563464686c916c7e46e623e520ddc6d79fa6641390f2e3fa86e83e885"
SRC_URI[tiny-keccak-2.0.2.sha256sum] = "2c9d3793400a45f954c52e73d068316d76b6f4e36977e3fcebb13a2721e80237"
SRC_URI[tinyvec-1.8.0.sha256sum] = "445e881f4f6d382d5f27c034e25eb92edd7c784ceab92a0937db7f2e9471b938"
SRC_URI[tinyvec_macros-0.1.1.sha256sum] = "1f3ccbac311fea05f86f61904b462b55fb3df8837a366dfc601a0161d0532f20"
SRC_URI[tokio-macros-2.3.0.sha256sum] = "5f5ae998a069d4b5aba8ee9dad856af7d520c3699e6159b185c2acd48155d39a"
SRC_URI[tokio-retry-0.3.0.sha256sum] = "7f57eb36ecbe0fc510036adff84824dd3c24bb781e21bfa67b69d556aa85214f"
SRC_URI[tokio-rustls-0.24.1.sha256sum] = "c28327cf380ac148141087fbfb9de9d7bd4e84ab5d2c28fbc911d753de8a7081"
SRC_URI[tokio-stream-0.1.15.sha256sum] = "267ac89e0bec6e691e5813911606935d77c476ff49024f98abcea3e7b15e37af"
SRC_URI[tokio-util-0.7.11.sha256sum] = "9cf6b47b3771c49ac75ad09a6162f53ad4b8088b76ac60e8ec1455b31a189fe1"
SRC_URI[tokio-1.38.1.sha256sum] = "eb2caba9f80616f438e09748d5acda951967e1ea58508ef53d9c6402485a46df"
SRC_URI[toml-0.5.11.sha256sum] = "f4f7f0dd8d50a853a531c426359045b1998f04219d88799810762cd4ad314234"
SRC_URI[toml_datetime-0.6.6.sha256sum] = "4badfd56924ae69bcc9039335b2e017639ce3f9b001c393c1b2d1ef846ce2cbf"
SRC_URI[toml_edit-0.21.1.sha256sum] = "6a8534fd7f78b5405e860340ad6575217ce99f38d4d5c8f2442cb5ecb50090e1"
SRC_URI[tower-layer-0.3.2.sha256sum] = "c20c8dbed6283a09604c3e69b4b7eeb54e298b8a600d4d5ecb5ad39de609f1d0"
SRC_URI[tower-service-0.3.2.sha256sum] = "b6bc1c9ce2b5135ac7f93c72918fc37feb872bdc6a5533a8b85eb4b86bfdae52"
SRC_URI[tower-0.4.13.sha256sum] = "b8fa9be0de6cf49e536ce1851f987bd21a43b771b09473c3549a6c853db37c1c"
SRC_URI[tracing-attributes-0.1.27.sha256sum] = "34704c8d6ebcbc939824180af020566b01a7c01f80641264eba0999f6c2b6be7"
SRC_URI[tracing-core-0.1.32.sha256sum] = "c06d3da6113f116aaee68e4d601191614c9053067f9ab7f6edbcb161237daa54"
SRC_URI[tracing-0.1.40.sha256sum] = "c3523ab5a71916ccf420eebdf5521fcef02141234bbc0b8a49f2fdc4544364ef"
SRC_URI[try-lock-0.2.5.sha256sum] = "e421abadd41a4225275504ea4d6566923418b7f05506fbc9c0fe86ba7396114b"
SRC_URI[typed-path-0.7.1.sha256sum] = "668404597c2c687647f6f8934f97c280fd500db28557f52b07c56b92d3dc500a"
SRC_URI[typenum-1.17.0.sha256sum] = "42ff0bf0c66b8238c6f3b578df37d0b7848e55df8577b3f74f92a69acceeb825"
SRC_URI[uds_windows-1.1.0.sha256sum] = "89daebc3e6fd160ac4aa9fc8b3bf71e1f74fbf92367ae71fb83a037e8bf164b9"
SRC_URI[unicode-bidi-0.3.15.sha256sum] = "08f95100a766bf4f8f28f90d77e0a5461bbdb219042e7679bebe79004fed8d75"
SRC_URI[unicode-ident-1.0.12.sha256sum] = "3354b9ac3fae1ff6755cb6db53683adb661634f67557942dea4facebec0fee4b"
SRC_URI[unicode-normalization-0.1.23.sha256sum] = "a56d1686db2308d901306f92a263857ef59ea39678a5458e7cb17f01415101f5"
SRC_URI[unicode-width-0.1.13.sha256sum] = "0336d538f7abc86d282a4189614dfaa90810dfc2c6f6427eaf88e16311dd225d"
SRC_URI[universal-hash-0.5.1.sha256sum] = "fc1de2c688dc15305988b563c3854064043356019f97a4b46276fe734c4f07ea"
SRC_URI[untrusted-0.9.0.sha256sum] = "8ecb6da28b8a351d773b68d5825ac39017e680750f980f3a1a85cd8dd28a47c1"
SRC_URI[url-2.5.2.sha256sum] = "22784dbdf76fdde8af1aeda5622b546b422b6fc585325248a2bf9f5e41e94d6c"
SRC_URI[uuid-1.10.0.sha256sum] = "81dfa00651efa65069b0b6b651f4aaa31ba9e3c3ce0137aaad053604ee7e0314"
SRC_URI[vcpkg-0.2.15.sha256sum] = "accd4ea62f7bb7a82fe23066fb0957d48ef677f6eeb8215f372f52e48bb32426"
SRC_URI[vergen-7.5.1.sha256sum] = "f21b881cd6636ece9735721cf03c1fe1e774fe258683d084bb2812ab67435749"
SRC_URI[version_check-0.9.4.sha256sum] = "49874b5167b65d7193b8aba1567f5c7d93d001cafc34600cee003eda787e483f"
SRC_URI[walkdir-2.5.0.sha256sum] = "29790946404f91d9c5d06f9874efddea1dc06c5efe94541a7d6863108e3a5e4b"
SRC_URI[want-0.3.1.sha256sum] = "bfa7760aed19e106de2c7c0b581b509f2f25d3dacaf737cb82ac61bc6d760b0e"
SRC_URI[wasi-0.11.0+wasi-snapshot-preview1.sha256sum] = "9c8d87e72b64a3b4db28d11ce29237c246188f4f51057d65a7eab63b7987e423"
SRC_URI[wasi-0.9.0+wasi-snapshot-preview1.sha256sum] = "cccddf32554fecc6acb585f82a32a72e28b48f8c4c1883ddfeeeaa96f7d8e519"
SRC_URI[wasm-bindgen-backend-0.2.95.sha256sum] = "cb6dd4d3ca0ddffd1dd1c9c04f94b868c37ff5fac97c30b97cff2d74fce3a358"
SRC_URI[wasm-bindgen-futures-0.4.45.sha256sum] = "cc7ec4f8827a71586374db3e87abdb5a2bb3a15afed140221307c3ec06b1f63b"
SRC_URI[wasm-bindgen-macro-support-0.2.95.sha256sum] = "26c6ab57572f7a24a4985830b120de1594465e5d500f24afe89e16b4e833ef68"
SRC_URI[wasm-bindgen-macro-0.2.95.sha256sum] = "e79384be7f8f5a9dd5d7167216f022090cf1f9ec128e6e6a482a2cb5c5422c56"
SRC_URI[wasm-bindgen-shared-0.2.95.sha256sum] = "65fc09f10666a9f147042251e0dda9c18f166ff7de300607007e96bdebc1068d"
SRC_URI[wasm-bindgen-0.2.95.sha256sum] = "128d1e363af62632b8eb57219c8fd7877144af57558fb2ef0368d0087bddeb2e"
SRC_URI[wasm-streams-0.4.0.sha256sum] = "b65dc4c90b63b118468cf747d8bf3566c1913ef60be765b5730ead9e0a3ba129"
SRC_URI[web-sys-0.3.69.sha256sum] = "77afa9a11836342370f4817622a2f0f418b134426d91a82dfb48f532d2ec13ef"
SRC_URI[winapi-i686-pc-windows-gnu-0.4.0.sha256sum] = "ac3b87c63620426dd9b991e5ce0329eff545bccbbb34f3be09ff6fb6ab51b7b6"
SRC_URI[winapi-util-0.1.8.sha256sum] = "4d4cc384e1e73b93bafa6fb4f1df8c41695c8a91cf9c4c64358067d15a7b6c6b"
SRC_URI[winapi-x86_64-pc-windows-gnu-0.4.0.sha256sum] = "712e227841d057c1ee1cd2fb22fa7e5a5461ae8e48fa2ca79ec42cfc1931183f"
SRC_URI[winapi-0.3.9.sha256sum] = "5c839a674fcd7a98952e593242ea400abe93992746761e38641405d28b00f419"
SRC_URI[windows-core-0.52.0.sha256sum] = "33ab640c8d7e35bf8ba19b884ba838ceb4fba93a4e8c65a9059d08afcfc683d9"
SRC_URI[windows-core-0.58.0.sha256sum] = "6ba6d44ec8c2591c134257ce647b7ea6b20335bf6379a27dac5f1641fcf59f99"
SRC_URI[windows-implement-0.58.0.sha256sum] = "2bbd5b46c938e506ecbce286b6628a02171d56153ba733b6c741fc627ec9579b"
SRC_URI[windows-interface-0.58.0.sha256sum] = "053c4c462dc91d3b1504c6fe5a726dd15e216ba718e84a0e46a88fbe5ded3515"
SRC_URI[windows-result-0.2.0.sha256sum] = "1d1043d8214f791817bab27572aaa8af63732e11bf84aa21a45a78d6c317ae0e"
SRC_URI[windows-strings-0.1.0.sha256sum] = "4cd9b125c486025df0eabcb585e62173c6c9eddcec5d117d3b6e8c30e2ee4d10"
SRC_URI[windows-sys-0.48.0.sha256sum] = "677d2418bec65e3338edb076e806bc1ec15693c5d0104683f2efe857f61056a9"
SRC_URI[windows-sys-0.52.0.sha256sum] = "282be5f36a8ce781fad8c8ae18fa3f9beff57ec1b52cb3de0789201425d9a33d"
SRC_URI[windows-targets-0.48.5.sha256sum] = "9a2fa6e2155d7247be68c096456083145c183cbbbc2764150dda45a87197940c"
SRC_URI[windows-targets-0.52.6.sha256sum] = "9b724f72796e036ab90c1021d4780d4d3d648aca59e491e6b98e725b84e99973"
SRC_URI[windows-0.58.0.sha256sum] = "dd04d41d93c4992d421894c18c8b43496aa748dd4c081bac0dc93eb0489272b6"
SRC_URI[windows_aarch64_gnullvm-0.48.5.sha256sum] = "2b38e32f0abccf9987a4e3079dfb67dcd799fb61361e53e2882c3cbaf0d905d8"
SRC_URI[windows_aarch64_gnullvm-0.52.6.sha256sum] = "32a4622180e7a0ec044bb555404c800bc9fd9ec262ec147edd5989ccd0c02cd3"
SRC_URI[windows_aarch64_msvc-0.48.5.sha256sum] = "dc35310971f3b2dbbf3f0690a219f40e2d9afcf64f9ab7cc1be722937c26b4bc"
SRC_URI[windows_aarch64_msvc-0.52.6.sha256sum] = "09ec2a7bb152e2252b53fa7803150007879548bc709c039df7627cabbd05d469"
SRC_URI[windows_i686_gnu-0.48.5.sha256sum] = "a75915e7def60c94dcef72200b9a8e58e5091744960da64ec734a6c6e9b3743e"
SRC_URI[windows_i686_gnu-0.52.6.sha256sum] = "8e9b5ad5ab802e97eb8e295ac6720e509ee4c243f69d781394014ebfe8bbfa0b"
SRC_URI[windows_i686_gnullvm-0.52.6.sha256sum] = "0eee52d38c090b3caa76c563b86c3a4bd71ef1a819287c19d586d7334ae8ed66"
SRC_URI[windows_i686_msvc-0.48.5.sha256sum] = "8f55c233f70c4b27f66c523580f78f1004e8b5a8b659e05a4eb49d4166cca406"
SRC_URI[windows_i686_msvc-0.52.6.sha256sum] = "240948bc05c5e7c6dabba28bf89d89ffce3e303022809e73deaefe4f6ec56c66"
SRC_URI[windows_x86_64_gnu-0.48.5.sha256sum] = "53d40abd2583d23e4718fddf1ebec84dbff8381c07cae67ff7768bbf19c6718e"
SRC_URI[windows_x86_64_gnu-0.52.6.sha256sum] = "147a5c80aabfbf0c7d901cb5895d1de30ef2907eb21fbbab29ca94c5b08b1a78"
SRC_URI[windows_x86_64_gnullvm-0.48.5.sha256sum] = "0b7b52767868a23d5bab768e390dc5f5c55825b6d30b86c844ff2dc7414044cc"
SRC_URI[windows_x86_64_gnullvm-0.52.6.sha256sum] = "24d5b23dc417412679681396f2b49f3de8c1473deb516bd34410872eff51ed0d"
SRC_URI[windows_x86_64_msvc-0.48.5.sha256sum] = "ed94fce61571a4006852b7389a063ab983c02eb1bb37b47f8272ce92d06d9538"
SRC_URI[windows_x86_64_msvc-0.52.6.sha256sum] = "589f6da84c646204747d1270a2a5661ea66ed1cced2631d546fdfb155959f9ec"
SRC_URI[winnow-0.5.40.sha256sum] = "f593a95398737aeed53e489c785df13f3618e41dbcd6718c6addbf1395aa6876"
SRC_URI[winreg-0.10.1.sha256sum] = "80d0f4e272c85def139476380b12f9ac60926689dd2e01d4923222f40580869d"
SRC_URI[winreg-0.50.0.sha256sum] = "524e57b2c537c0f9b1e69f1965311ec12182b4122e45035b1508cd24d2adadb1"
SRC_URI[xdg-home-1.2.0.sha256sum] = "ca91dcf8f93db085f3a0a29358cd0b9d670915468f4290e8b85d118a34211ab8"
SRC_URI[yansi-0.5.1.sha256sum] = "09041cd90cf85f7f8b2df60c646f853b7f535ce68f85244eb6731cf89fa498ec"
SRC_URI[zbus-4.3.1.sha256sum] = "851238c133804e0aa888edf4a0229481c753544ca12a60fd1c3230c8a500fe40"
SRC_URI[zbus_macros-4.3.1.sha256sum] = "8d5a3f12c20bd473be3194af6b49d50d7bb804ef3192dc70eddedb26b85d9da7"
SRC_URI[zbus_names-3.0.0.sha256sum] = "4b9b1fef7d021261cc16cba64c351d291b715febe0fa10dc3a443ac5a5022e6c"
SRC_URI[zerocopy-derive-0.7.35.sha256sum] = "fa4f8080344d4671fb4e831a13ad1e68092748387dfc4f55e356242fae12ce3e"
SRC_URI[zerocopy-0.7.35.sha256sum] = "1b9b4fd18abc82b8136838da5d50bae7bdea537c574d8dc1a34ed098d6c166f0"
SRC_URI[zeroize-1.8.1.sha256sum] = "ced3678a2879b30306d323f4542626697a464a97c0a07c9aebf7ebca65cd4dde"
SRC_URI[zeroize_derive-1.4.2.sha256sum] = "ce36e65b0d2999d2aafac989fb249189a141aee1f53c612c1f37d72631959f69"
SRC_URI[zvariant-4.1.2.sha256sum] = "1724a2b330760dc7d2a8402d841119dc869ef120b139d29862d6980e9c75bfc9"
SRC_URI[zvariant_derive-4.1.2.sha256sum] = "55025a7a518ad14518fb243559c058a2e5b848b015e31f1d90414f36e3317859"
SRC_URI[zvariant_utils-2.0.0.sha256sum] = "fc242db087efc22bd9ade7aa7809e4ba828132edc312871584a6b4391bdf8786"

# Use --offline rather than --frozen mode with "cargo build".
#
# Due to the changes we needed to do in RAC Yocto recipe in order to use our toradex/tough registry,
# the tough repository got sort of aliased, so the upstream Git URL was in fact referencing our
# toradex/tough fork. Since this is a hack on top of Cargo, it got somewhat lost while parsing
# Cargo.lock and thought that the repository there needed to change to the upstream one to reflect
# the state of things.
#
CARGO_BUILD_FLAGS:remove = "--frozen"
CARGO_BUILD_FLAGS:append = " --offline"

# There is a postfunc that runs after do_configure. This fixing logic needs to run after this postfunc.
# It is because of this ordering this is do_compile:prepend instead of do_configure:append.
cargo_add_rac_patch_paths() {
    # Need to fix config file due to the tough repo having a virtual manifest.
    # Which is not supported by the cargo bbclasses currently,
    # see: https://github.com/openembedded/openembedded-core/commit/684a8af41c5bb70db68e75f72bdc4c9b09630810
    sed -i 's|tough =.*|tough = { path = "${WORKDIR}/tough/tough" }|g' ${CARGO_HOME}/config
    sed -i '/olpc-cjson =.*/d' ${CARGO_HOME}/config
    sed -i '/^tough =.*/a olpc-cjson = { path = "${WORKDIR}/tough/olpc-cjson" }' ${CARGO_HOME}/config
}

do_configure[postfuncs] += "cargo_add_rac_patch_paths"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/remote-access.service ${D}${systemd_unitdir}/system/remote-access.service

    install -d ${D}${sysconfdir}/rac
    install -m 0644 ${WORKDIR}/client.toml ${D}${sysconfdir}/rac
}
