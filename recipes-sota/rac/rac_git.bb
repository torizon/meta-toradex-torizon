SUMMARY = "Remote access client (RAC) for TorizonCore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

inherit cargo systemd

# Main source respository
SRC_URI = " \
    git://github.com/toradex/torizon-rac.git;protocol=https;branch=main;name=rac \
    git://github.com/toradex/tough;protocol=https;branch=rac;name=tough;destsuffix=tough \
    file://remote-access.service \
    file://client.toml \
"

SRCREV_FORMAT = "rac_tough"

SRCREV_rac = "15845bcaad19b9cbb79816b84184547df559e6b5"
SRCREV_tough = "9316c096b32196df75ba17a8a5502b19baffe24e"

# Disable AUTOREV, it does not guarantee work, since the below crate
# dependencies might also need to be updated.
# If you want to enable AUTOREV, uncomment the following lines, and you might
# need update crate dependencies as well, which depends on Cargo.toml in rac.
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
    crate://crates.io/aead/0.5.1 \
    crate://crates.io/aes-gcm/0.10.1 \
    crate://crates.io/aes/0.8.2 \
    crate://crates.io/aho-corasick/0.7.20 \
    crate://crates.io/android_system_properties/0.1.5 \
    crate://crates.io/anyhow/1.0.69 \
    crate://crates.io/async-recursion/1.0.5 \
    crate://crates.io/async-trait/0.1.77 \
    crate://crates.io/autocfg/1.1.0 \
    crate://crates.io/axum-core/0.3.2 \
    crate://crates.io/axum/0.6.7 \
    crate://crates.io/backtrace/0.3.69 \
    crate://crates.io/base16ct/0.1.1 \
    crate://crates.io/base16ct/0.2.0 \
    crate://crates.io/base64/0.21.2 \
    crate://crates.io/base64ct/1.5.3 \
    crate://crates.io/bcrypt-pbkdf/0.10.0 \
    crate://crates.io/bit-vec/0.6.3 \
    crate://crates.io/bitflags/1.3.2 \
    crate://crates.io/bitflags/2.4.2 \
    crate://crates.io/block-buffer/0.10.3 \
    crate://crates.io/block-buffer/0.9.0 \
    crate://crates.io/block-padding/0.3.2 \
    crate://crates.io/blowfish/0.9.1 \
    crate://crates.io/bstr/1.5.0 \
    crate://crates.io/bumpalo/3.15.3 \
    crate://crates.io/byteorder/1.4.3 \
    crate://crates.io/bytes/1.5.0 \
    crate://crates.io/cbc/0.1.2 \
    crate://crates.io/cc/1.0.88 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/chacha20/0.9.0 \
    crate://crates.io/chrono/0.4.23 \
    crate://crates.io/cipher/0.4.3 \
    crate://crates.io/codespan-reporting/0.11.1 \
    crate://crates.io/color-eyre/0.6.2 \
    crate://crates.io/config/0.13.3 \
    crate://crates.io/console/0.15.5 \
    crate://crates.io/const-oid/0.9.2 \
    crate://crates.io/core-foundation-sys/0.8.3 \
    crate://crates.io/core-foundation/0.9.3 \
    crate://crates.io/cpufeatures/0.2.12 \
    crate://crates.io/crc32fast/1.3.2 \
    crate://crates.io/crypto-bigint/0.4.9 \
    crate://crates.io/crypto-bigint/0.5.5 \
    crate://crates.io/crypto-common/0.1.6 \
    crate://crates.io/ctor/0.1.26 \
    crate://crates.io/ctr/0.9.2 \
    crate://crates.io/curve25519-dalek-derive/0.1.1 \
    crate://crates.io/curve25519-dalek/3.2.0 \
    crate://crates.io/curve25519-dalek/4.1.2 \
    crate://crates.io/cxx-build/1.0.91 \
    crate://crates.io/cxx/1.0.91 \
    crate://crates.io/cxxbridge-flags/1.0.91 \
    crate://crates.io/cxxbridge-macro/1.0.91 \
    crate://crates.io/data-encoding/2.3.3 \
    crate://crates.io/der/0.6.1 \
    crate://crates.io/der/0.7.6 \
    crate://crates.io/diff/0.1.13 \
    crate://crates.io/digest/0.10.7 \
    crate://crates.io/digest/0.9.0 \
    crate://crates.io/dirs-sys/0.4.1 \
    crate://crates.io/dirs/5.0.1 \
    crate://crates.io/doc-comment/0.3.3 \
    crate://crates.io/downcast-rs/1.2.0 \
    crate://crates.io/dyn-clone/1.0.11 \
    crate://crates.io/ecdsa/0.14.8 \
    crate://crates.io/ecdsa/0.16.9 \
    crate://crates.io/ed25519-dalek/1.0.1 \
    crate://crates.io/ed25519-dalek/2.1.1 \
    crate://crates.io/ed25519/1.5.2 \
    crate://crates.io/ed25519/2.2.3 \
    crate://crates.io/elliptic-curve/0.12.3 \
    crate://crates.io/elliptic-curve/0.13.8 \
    crate://crates.io/encode_unicode/0.3.6 \
    crate://crates.io/encoding_rs/0.8.33 \
    crate://crates.io/enum-iterator-derive/1.2.0 \
    crate://crates.io/enum-iterator/1.4.0 \
    crate://crates.io/env_logger/0.10.0 \
    crate://crates.io/equivalent/1.0.1 \
    crate://crates.io/errno-dragonfly/0.1.2 \
    crate://crates.io/errno/0.2.8 \
    crate://crates.io/eyre/0.6.8 \
    crate://crates.io/fastrand/1.9.0 \
    crate://crates.io/ff/0.12.1 \
    crate://crates.io/ff/0.13.0 \
    crate://crates.io/fiat-crypto/0.2.6 \
    crate://crates.io/filedescriptor/0.8.2 \
    crate://crates.io/flate2/1.0.25 \
    crate://crates.io/fnv/1.0.7 \
    crate://crates.io/form_urlencoded/1.2.1 \
    crate://crates.io/futures-channel/0.3.30 \
    crate://crates.io/futures-core/0.3.30 \
    crate://crates.io/futures-executor/0.3.27 \
    crate://crates.io/futures-io/0.3.30 \
    crate://crates.io/futures-macro/0.3.30 \
    crate://crates.io/futures-sink/0.3.30 \
    crate://crates.io/futures-task/0.3.30 \
    crate://crates.io/futures-util/0.3.30 \
    crate://crates.io/futures/0.3.27 \
    crate://crates.io/generic-array/0.14.6 \
    crate://crates.io/getrandom/0.1.16 \
    crate://crates.io/getrandom/0.2.12 \
    crate://crates.io/getset/0.1.2 \
    crate://crates.io/ghash/0.5.0 \
    crate://crates.io/gimli/0.28.1 \
    crate://crates.io/git2/0.16.1 \
    crate://crates.io/globset/0.4.10 \
    crate://crates.io/group/0.12.1 \
    crate://crates.io/group/0.13.0 \
    crate://crates.io/h2/0.3.24 \
    crate://crates.io/hashbrown/0.14.3 \
    crate://crates.io/heck/0.4.1 \
    crate://crates.io/hermit-abi/0.2.6 \
    crate://crates.io/hermit-abi/0.3.8 \
    crate://crates.io/hex-literal/0.4.1 \
    crate://crates.io/hex/0.4.3 \
    crate://crates.io/hmac/0.12.1 \
    crate://crates.io/http-body/0.4.6 \
    crate://crates.io/http-range-header/0.3.0 \
    crate://crates.io/http/0.2.11 \
    crate://crates.io/httparse/1.8.0 \
    crate://crates.io/httpdate/1.0.3 \
    crate://crates.io/humantime/2.1.0 \
    crate://crates.io/hyper-rustls/0.24.2 \
    crate://crates.io/hyper/0.14.28 \
    crate://crates.io/iana-time-zone-haiku/0.1.1 \
    crate://crates.io/iana-time-zone/0.1.53 \
    crate://crates.io/idna/0.5.0 \
    crate://crates.io/indenter/0.3.3 \
    crate://crates.io/indexmap/2.2.3 \
    crate://crates.io/inout/0.1.3 \
    crate://crates.io/instant/0.1.12 \
    crate://crates.io/io-lifetimes/1.0.3 \
    crate://crates.io/ioctl-rs/0.1.6 \
    crate://crates.io/ipnet/2.9.0 \
    crate://crates.io/is-terminal/0.4.2 \
    crate://crates.io/itoa/1.0.10 \
    crate://crates.io/js-sys/0.3.68 \
    crate://crates.io/lazy_static/1.4.0 \
    crate://crates.io/libc/0.2.153 \
    crate://crates.io/libgit2-sys/0.14.2+1.5.1 \
    crate://crates.io/libm/0.2.6 \
    crate://crates.io/libz-sys/1.1.8 \
    crate://crates.io/link-cplusplus/1.0.8 \
    crate://crates.io/linux-raw-sys/0.1.4 \
    crate://crates.io/lock_api/0.4.11 \
    crate://crates.io/log/0.4.20 \
    crate://crates.io/matchit/0.7.0 \
    crate://crates.io/md5/0.7.0 \
    crate://crates.io/memchr/2.7.1 \
    crate://crates.io/memoffset/0.6.5 \
    crate://crates.io/mime/0.3.17 \
    crate://crates.io/minimal-lexical/0.2.1 \
    crate://crates.io/miniz_oxide/0.6.2 \
    crate://crates.io/miniz_oxide/0.7.2 \
    crate://crates.io/mio/0.8.10 \
    crate://crates.io/nix/0.25.1 \
    crate://crates.io/nix/0.26.2 \
    crate://crates.io/nom/7.1.3 \
    crate://crates.io/ntapi/0.4.0 \
    crate://crates.io/num-bigint-dig/0.8.2 \
    crate://crates.io/num-bigint/0.4.3 \
    crate://crates.io/num-integer/0.1.45 \
    crate://crates.io/num-iter/0.1.43 \
    crate://crates.io/num-traits/0.2.15 \
    crate://crates.io/num_cpus/1.16.0 \
    crate://crates.io/object/0.32.2 \
    crate://crates.io/olpc-cjson/0.1.3 \
    crate://crates.io/once_cell/1.19.0 \
    crate://crates.io/opaque-debug/0.3.0 \
    crate://crates.io/option-ext/0.2.0 \
    crate://crates.io/output_vt100/0.1.3 \
    crate://crates.io/owo-colors/3.5.0 \
    crate://crates.io/p256/0.11.1 \
    crate://crates.io/p256/0.13.2 \
    crate://crates.io/p384/0.11.2 \
    crate://crates.io/p521/0.13.3 \
    crate://crates.io/parking_lot/0.12.1 \
    crate://crates.io/parking_lot_core/0.9.9 \
    crate://crates.io/password-hash/0.4.2 \
    crate://crates.io/pathdiff/0.2.1 \
    crate://crates.io/pbkdf2/0.11.0 \
    crate://crates.io/pbkdf2/0.12.2 \
    crate://crates.io/pem-rfc7468/0.6.0 \
    crate://crates.io/pem-rfc7468/0.7.0 \
    crate://crates.io/pem/3.0.3 \
    crate://crates.io/percent-encoding/2.3.1 \
    crate://crates.io/pin-project-internal/1.0.12 \
    crate://crates.io/pin-project-lite/0.2.13 \
    crate://crates.io/pin-project/1.0.12 \
    crate://crates.io/pin-utils/0.1.0 \
    crate://crates.io/pkcs1/0.4.1 \
    crate://crates.io/pkcs8/0.10.2 \
    crate://crates.io/pkcs8/0.9.0 \
    crate://crates.io/pkg-config/0.3.26 \
    crate://crates.io/platforms/3.3.0 \
    crate://crates.io/poly1305/0.8.0 \
    crate://crates.io/polyval/0.6.0 \
    crate://crates.io/portable-pty/0.8.0 \
    crate://crates.io/ppv-lite86/0.2.17 \
    crate://crates.io/pretty_assertions/1.3.0 \
    crate://crates.io/primeorder/0.13.6 \
    crate://crates.io/proc-macro-error-attr/1.0.4 \
    crate://crates.io/proc-macro-error/1.0.4 \
    crate://crates.io/proc-macro2/1.0.78 \
    crate://crates.io/quote/1.0.35 \
    crate://crates.io/rand/0.7.3 \
    crate://crates.io/rand/0.8.5 \
    crate://crates.io/rand_chacha/0.2.2 \
    crate://crates.io/rand_chacha/0.3.1 \
    crate://crates.io/rand_core/0.5.1 \
    crate://crates.io/rand_core/0.6.4 \
    crate://crates.io/rand_hc/0.2.0 \
    crate://crates.io/redox_syscall/0.2.16 \
    crate://crates.io/redox_syscall/0.4.1 \
    crate://crates.io/redox_users/0.4.3 \
    crate://crates.io/regex-syntax/0.6.29 \
    crate://crates.io/regex/1.7.3 \
    crate://crates.io/reqwest/0.11.24 \
    crate://crates.io/rfc6979/0.3.1 \
    crate://crates.io/rfc6979/0.4.0 \
    crate://crates.io/ring/0.17.8 \
    crate://crates.io/rsa/0.7.2 \
    crate://crates.io/russh-cryptovec/0.7.2 \
    crate://crates.io/russh-keys/0.42.0 \
    crate://crates.io/russh/0.42.0 \
    crate://crates.io/rustc-demangle/0.1.23 \
    crate://crates.io/rustc_version/0.4.0 \
    crate://crates.io/rustix/0.36.6 \
    crate://crates.io/rustls-pemfile/1.0.4 \
    crate://crates.io/rustls-webpki/0.101.7 \
    crate://crates.io/rustls/0.21.10 \
    crate://crates.io/rustversion/1.0.11 \
    crate://crates.io/ryu/1.0.17 \
    crate://crates.io/same-file/1.0.6 \
    crate://crates.io/scopeguard/1.2.0 \
    crate://crates.io/scratch/1.0.3 \
    crate://crates.io/sct/0.7.1 \
    crate://crates.io/sec1/0.3.0 \
    crate://crates.io/sec1/0.7.1 \
    crate://crates.io/semver/1.0.16 \
    crate://crates.io/serde/1.0.197 \
    crate://crates.io/serde_derive/1.0.197 \
    crate://crates.io/serde_json/1.0.114 \
    crate://crates.io/serde_path_to_error/0.1.9 \
    crate://crates.io/serde_plain/1.0.1 \
    crate://crates.io/serde_urlencoded/0.7.1 \
    crate://crates.io/serial-core/0.4.0 \
    crate://crates.io/serial-unix/0.4.0 \
    crate://crates.io/serial-windows/0.4.0 \
    crate://crates.io/serial/0.4.0 \
    crate://crates.io/sha1/0.10.5 \
    crate://crates.io/sha2/0.10.6 \
    crate://crates.io/sha2/0.9.9 \
    crate://crates.io/shared_library/0.1.9 \
    crate://crates.io/shell-words/1.1.0 \
    crate://crates.io/signal-hook-registry/1.4.1 \
    crate://crates.io/signature/1.6.4 \
    crate://crates.io/signature/2.2.0 \
    crate://crates.io/slab/0.4.9 \
    crate://crates.io/smallvec/1.13.1 \
    crate://crates.io/snafu-derive/0.7.4 \
    crate://crates.io/snafu/0.7.4 \
    crate://crates.io/socket2/0.5.6 \
    crate://crates.io/spin/0.5.2 \
    crate://crates.io/spin/0.9.8 \
    crate://crates.io/spki/0.6.0 \
    crate://crates.io/spki/0.7.2 \
    crate://crates.io/ssh-encoding/0.1.0 \
    crate://crates.io/ssh-key/0.5.1 \
    crate://crates.io/static_assertions/1.1.0 \
    crate://crates.io/subtle/2.4.1 \
    crate://crates.io/syn/1.0.109 \
    crate://crates.io/syn/2.0.51 \
    crate://crates.io/sync_wrapper/0.1.2 \
    crate://crates.io/synstructure/0.12.6 \
    crate://crates.io/sysinfo/0.27.8 \
    crate://crates.io/system-configuration-sys/0.5.0 \
    crate://crates.io/system-configuration/0.5.1 \
    crate://crates.io/tempfile/3.4.0 \
    crate://crates.io/termcolor/1.1.3 \
    crate://crates.io/termios/0.2.2 \
    crate://crates.io/thiserror-impl/1.0.38 \
    crate://crates.io/thiserror/1.0.38 \
    crate://crates.io/time-core/0.1.0 \
    crate://crates.io/time-macros/0.2.8 \
    crate://crates.io/time/0.1.45 \
    crate://crates.io/time/0.3.20 \
    crate://crates.io/tinyvec/1.6.0 \
    crate://crates.io/tinyvec_macros/0.1.1 \
    crate://crates.io/tokio-macros/2.2.0 \
    crate://crates.io/tokio-retry/0.3.0 \
    crate://crates.io/tokio-rustls/0.24.1 \
    crate://crates.io/tokio-stream/0.1.12 \
    crate://crates.io/tokio-util/0.7.10 \
    crate://crates.io/tokio/1.36.0 \
    crate://crates.io/toml/0.5.10 \
    crate://crates.io/tower-http/0.3.5 \
    crate://crates.io/tower-layer/0.3.2 \
    crate://crates.io/tower-service/0.3.2 \
    crate://crates.io/tower/0.4.13 \
    crate://crates.io/tracing-core/0.1.32 \
    crate://crates.io/tracing/0.1.40 \
    crate://crates.io/try-lock/0.2.5 \
    crate://crates.io/typed-path/0.7.1 \
    crate://crates.io/typenum/1.16.0 \
    crate://crates.io/unicode-bidi/0.3.15 \
    crate://crates.io/unicode-ident/1.0.12 \
    crate://crates.io/unicode-normalization/0.1.23 \
    crate://crates.io/unicode-width/0.1.10 \
    crate://crates.io/unicode-xid/0.2.4 \
    crate://crates.io/universal-hash/0.5.0 \
    crate://crates.io/untrusted/0.9.0 \
    crate://crates.io/url/2.5.0 \
    crate://crates.io/uuid/1.3.0 \
    crate://crates.io/vcpkg/0.2.15 \
    crate://crates.io/vergen/7.5.1 \
    crate://crates.io/version_check/0.9.4 \
    crate://crates.io/walkdir/2.3.3 \
    crate://crates.io/want/0.3.1 \
    crate://crates.io/wasi/0.10.0+wasi-snapshot-preview1 \
    crate://crates.io/wasi/0.11.0+wasi-snapshot-preview1 \
    crate://crates.io/wasi/0.9.0+wasi-snapshot-preview1 \
    crate://crates.io/wasm-bindgen-backend/0.2.91 \
    crate://crates.io/wasm-bindgen-futures/0.4.41 \
    crate://crates.io/wasm-bindgen-macro-support/0.2.91 \
    crate://crates.io/wasm-bindgen-macro/0.2.91 \
    crate://crates.io/wasm-bindgen-shared/0.2.91 \
    crate://crates.io/wasm-bindgen/0.2.91 \
    crate://crates.io/wasm-streams/0.4.0 \
    crate://crates.io/web-sys/0.3.68 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-util/0.1.5 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi/0.3.9 \
    crate://crates.io/windows-sys/0.42.0 \
    crate://crates.io/windows-sys/0.48.0 \
    crate://crates.io/windows-sys/0.52.0 \
    crate://crates.io/windows-targets/0.48.5 \
    crate://crates.io/windows-targets/0.52.3 \
    crate://crates.io/windows_aarch64_gnullvm/0.42.2 \
    crate://crates.io/windows_aarch64_gnullvm/0.48.5 \
    crate://crates.io/windows_aarch64_gnullvm/0.52.3 \
    crate://crates.io/windows_aarch64_msvc/0.42.2 \
    crate://crates.io/windows_aarch64_msvc/0.48.5 \
    crate://crates.io/windows_aarch64_msvc/0.52.3 \
    crate://crates.io/windows_i686_gnu/0.42.2 \
    crate://crates.io/windows_i686_gnu/0.48.5 \
    crate://crates.io/windows_i686_gnu/0.52.3 \
    crate://crates.io/windows_i686_msvc/0.42.2 \
    crate://crates.io/windows_i686_msvc/0.48.5 \
    crate://crates.io/windows_i686_msvc/0.52.3 \
    crate://crates.io/windows_x86_64_gnu/0.42.2 \
    crate://crates.io/windows_x86_64_gnu/0.48.5 \
    crate://crates.io/windows_x86_64_gnu/0.52.3 \
    crate://crates.io/windows_x86_64_gnullvm/0.42.2 \
    crate://crates.io/windows_x86_64_gnullvm/0.48.5 \
    crate://crates.io/windows_x86_64_gnullvm/0.52.3 \
    crate://crates.io/windows_x86_64_msvc/0.42.2 \
    crate://crates.io/windows_x86_64_msvc/0.48.5 \
    crate://crates.io/windows_x86_64_msvc/0.52.3 \
    crate://crates.io/winreg/0.10.1 \
    crate://crates.io/winreg/0.50.0 \
    crate://crates.io/yansi/0.5.1 \
    crate://crates.io/yasna/0.5.1 \
    crate://crates.io/zeroize/1.7.0 \
    crate://crates.io/zeroize_derive/1.3.3 \
"

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
