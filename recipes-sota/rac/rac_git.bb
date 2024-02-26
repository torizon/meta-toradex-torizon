SUMMARY = "Remote access client (RAC) for TorizonCore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

inherit cargo systemd

# Main source respository
SRC_URI = " \
    git://github.com/toradex/torizon-rac.git;protocol=https;branch=main; \
    file://remote-access.service \
    file://client.toml \
"

SRCREV = "ddeb836a11adc5b93daed65bf36452dfcebbcb76"
SRCREV:use-head-next = "${AUTOREV}"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} = "remote-access.service"
# Keep disabled by default for now
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

PV = "0.0+git${SRCPV}"

# Dependencies as specified by main project's Cargo.toml
# Make sure to keep this up-to-date as needed
# Auto-generated via "cargo bitbake"
SRC_URI += " \
    crate://crates.io/addr2line/0.19.0 \
    crate://crates.io/adler/1.0.2 \
    crate://crates.io/aead/0.5.1 \
    crate://crates.io/aes-gcm/0.10.1 \
    crate://crates.io/aes/0.8.2 \
    crate://crates.io/aho-corasick/0.7.20 \
    crate://crates.io/android_system_properties/0.1.5 \
    crate://crates.io/anyhow/1.0.69 \
    crate://crates.io/async-trait/0.1.64 \
    crate://crates.io/autocfg/1.1.0 \
    crate://crates.io/axum-core/0.3.2 \
    crate://crates.io/axum/0.6.7 \
    crate://crates.io/backtrace/0.3.67 \
    crate://crates.io/base16ct/0.1.1 \
    crate://crates.io/base64/0.13.1 \
    crate://crates.io/base64/0.21.2 \
    crate://crates.io/base64ct/1.5.3 \
    crate://crates.io/bcrypt-pbkdf/0.9.0 \
    crate://crates.io/bit-vec/0.6.3 \
    crate://crates.io/bitflags/1.3.2 \
    crate://crates.io/block-buffer/0.10.3 \
    crate://crates.io/block-buffer/0.9.0 \
    crate://crates.io/block-padding/0.3.2 \
    crate://crates.io/blowfish/0.9.1 \
    crate://crates.io/bstr/1.5.0 \
    crate://crates.io/bumpalo/3.11.1 \
    crate://crates.io/byteorder/1.4.3 \
    crate://crates.io/bytes/1.4.0 \
    crate://crates.io/cbc/0.1.2 \
    crate://crates.io/cc/1.0.78 \
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
    crate://crates.io/cpufeatures/0.2.5 \
    crate://crates.io/crc32fast/1.3.2 \
    crate://crates.io/crypto-bigint/0.4.9 \
    crate://crates.io/crypto-common/0.1.6 \
    crate://crates.io/ctor/0.1.26 \
    crate://crates.io/ctr/0.9.2 \
    crate://crates.io/curve25519-dalek/3.2.0 \
    crate://crates.io/cxx-build/1.0.91 \
    crate://crates.io/cxx/1.0.91 \
    crate://crates.io/cxxbridge-flags/1.0.91 \
    crate://crates.io/cxxbridge-macro/1.0.91 \
    crate://crates.io/data-encoding/2.3.3 \
    crate://crates.io/der/0.6.1 \
    crate://crates.io/der/0.7.6 \
    crate://crates.io/diff/0.1.13 \
    crate://crates.io/digest/0.10.6 \
    crate://crates.io/digest/0.9.0 \
    crate://crates.io/dirs-sys/0.3.7 \
    crate://crates.io/dirs/4.0.0 \
    crate://crates.io/doc-comment/0.3.3 \
    crate://crates.io/downcast-rs/1.2.0 \
    crate://crates.io/dyn-clone/1.0.11 \
    crate://crates.io/ecdsa/0.14.8 \
    crate://crates.io/ed25519-dalek/1.0.1 \
    crate://crates.io/ed25519/1.5.2 \
    crate://crates.io/elliptic-curve/0.12.3 \
    crate://crates.io/encode_unicode/0.3.6 \
    crate://crates.io/encoding_rs/0.8.31 \
    crate://crates.io/enum-iterator-derive/1.2.0 \
    crate://crates.io/enum-iterator/1.4.0 \
    crate://crates.io/env_logger/0.10.0 \
    crate://crates.io/errno-dragonfly/0.1.2 \
    crate://crates.io/errno/0.2.8 \
    crate://crates.io/eyre/0.6.8 \
    crate://crates.io/fastrand/1.9.0 \
    crate://crates.io/ff/0.12.1 \
    crate://crates.io/filedescriptor/0.8.2 \
    crate://crates.io/flate2/1.0.25 \
    crate://crates.io/fnv/1.0.7 \
    crate://crates.io/form_urlencoded/1.1.0 \
    crate://crates.io/futures-channel/0.3.27 \
    crate://crates.io/futures-core/0.3.27 \
    crate://crates.io/futures-executor/0.3.27 \
    crate://crates.io/futures-io/0.3.27 \
    crate://crates.io/futures-macro/0.3.27 \
    crate://crates.io/futures-sink/0.3.27 \
    crate://crates.io/futures-task/0.3.27 \
    crate://crates.io/futures-util/0.3.27 \
    crate://crates.io/futures/0.3.27 \
    crate://crates.io/generic-array/0.14.6 \
    crate://crates.io/getrandom/0.1.16 \
    crate://crates.io/getrandom/0.2.8 \
    crate://crates.io/getset/0.1.2 \
    crate://crates.io/ghash/0.5.0 \
    crate://crates.io/gimli/0.27.2 \
    crate://crates.io/git2/0.16.1 \
    crate://crates.io/globset/0.4.10 \
    crate://crates.io/group/0.12.1 \
    crate://crates.io/h2/0.3.15 \
    crate://crates.io/hashbrown/0.12.3 \
    crate://crates.io/heck/0.4.1 \
    crate://crates.io/hermit-abi/0.2.6 \
    crate://crates.io/hex-literal/0.3.4 \
    crate://crates.io/hex/0.4.3 \
    crate://crates.io/hmac/0.12.1 \
    crate://crates.io/http-body/0.4.5 \
    crate://crates.io/http-range-header/0.3.0 \
    crate://crates.io/http/0.2.9 \
    crate://crates.io/httparse/1.8.0 \
    crate://crates.io/httpdate/1.0.2 \
    crate://crates.io/humantime/2.1.0 \
    crate://crates.io/hyper-rustls/0.23.2 \
    crate://crates.io/hyper/0.14.23 \
    crate://crates.io/iana-time-zone-haiku/0.1.1 \
    crate://crates.io/iana-time-zone/0.1.53 \
    crate://crates.io/idna/0.3.0 \
    crate://crates.io/indenter/0.3.3 \
    crate://crates.io/indexmap/1.9.2 \
    crate://crates.io/inout/0.1.3 \
    crate://crates.io/instant/0.1.12 \
    crate://crates.io/io-lifetimes/1.0.3 \
    crate://crates.io/ioctl-rs/0.1.6 \
    crate://crates.io/ipnet/2.7.0 \
    crate://crates.io/is-terminal/0.4.2 \
    crate://crates.io/itoa/1.0.5 \
    crate://crates.io/jobserver/0.1.26 \
    crate://crates.io/js-sys/0.3.60 \
    crate://crates.io/lazy_static/1.4.0 \
    crate://crates.io/libc/0.2.139 \
    crate://crates.io/libgit2-sys/0.14.2+1.5.1 \
    crate://crates.io/libm/0.2.6 \
    crate://crates.io/libz-sys/1.1.8 \
    crate://crates.io/link-cplusplus/1.0.8 \
    crate://crates.io/linux-raw-sys/0.1.4 \
    crate://crates.io/lock_api/0.4.9 \
    crate://crates.io/log/0.4.17 \
    crate://crates.io/matchit/0.7.0 \
    crate://crates.io/md5/0.7.0 \
    crate://crates.io/memchr/2.5.0 \
    crate://crates.io/memoffset/0.6.5 \
    crate://crates.io/mime/0.3.16 \
    crate://crates.io/minimal-lexical/0.2.1 \
    crate://crates.io/miniz_oxide/0.6.2 \
    crate://crates.io/mio/0.8.5 \
    crate://crates.io/nix/0.25.1 \
    crate://crates.io/nix/0.26.2 \
    crate://crates.io/nom/7.1.3 \
    crate://crates.io/ntapi/0.4.0 \
    crate://crates.io/num-bigint-dig/0.8.2 \
    crate://crates.io/num-bigint/0.4.3 \
    crate://crates.io/num-integer/0.1.45 \
    crate://crates.io/num-iter/0.1.43 \
    crate://crates.io/num-traits/0.2.15 \
    crate://crates.io/num_cpus/1.15.0 \
    crate://crates.io/object/0.30.3 \
    crate://crates.io/olpc-cjson/0.1.3 \
    crate://crates.io/once_cell/1.16.0 \
    crate://crates.io/opaque-debug/0.3.0 \
    crate://crates.io/output_vt100/0.1.3 \
    crate://crates.io/owo-colors/3.5.0 \
    crate://crates.io/p256/0.11.1 \
    crate://crates.io/p384/0.11.2 \
    crate://crates.io/parking_lot/0.12.1 \
    crate://crates.io/parking_lot_core/0.9.6 \
    crate://crates.io/password-hash/0.4.2 \
    crate://crates.io/path-absolutize/3.1.0 \
    crate://crates.io/path-dedot/3.1.0 \
    crate://crates.io/pathdiff/0.2.1 \
    crate://crates.io/pbkdf2/0.11.0 \
    crate://crates.io/pem-rfc7468/0.6.0 \
    crate://crates.io/pem-rfc7468/0.7.0 \
    crate://crates.io/pem/1.1.1 \
    crate://crates.io/percent-encoding/2.2.0 \
    crate://crates.io/pin-project-internal/1.0.12 \
    crate://crates.io/pin-project-lite/0.2.9 \
    crate://crates.io/pin-project/1.0.12 \
    crate://crates.io/pin-utils/0.1.0 \
    crate://crates.io/pkcs1/0.4.1 \
    crate://crates.io/pkcs8/0.9.0 \
    crate://crates.io/pkg-config/0.3.26 \
    crate://crates.io/poly1305/0.8.0 \
    crate://crates.io/polyval/0.6.0 \
    crate://crates.io/portable-pty/0.8.0 \
    crate://crates.io/ppv-lite86/0.2.17 \
    crate://crates.io/pretty_assertions/1.3.0 \
    crate://crates.io/proc-macro-error-attr/1.0.4 \
    crate://crates.io/proc-macro-error/1.0.4 \
    crate://crates.io/proc-macro2/1.0.49 \
    crate://crates.io/quote/1.0.23 \
    crate://crates.io/rand/0.7.3 \
    crate://crates.io/rand/0.8.5 \
    crate://crates.io/rand_chacha/0.2.2 \
    crate://crates.io/rand_chacha/0.3.1 \
    crate://crates.io/rand_core/0.5.1 \
    crate://crates.io/rand_core/0.6.4 \
    crate://crates.io/rand_hc/0.2.0 \
    crate://crates.io/redox_syscall/0.2.16 \
    crate://crates.io/redox_users/0.4.3 \
    crate://crates.io/regex-syntax/0.6.29 \
    crate://crates.io/regex/1.7.3 \
    crate://crates.io/reqwest/0.11.13 \
    crate://crates.io/rfc6979/0.3.1 \
    crate://crates.io/ring/0.16.20 \
    crate://crates.io/rsa/0.7.2 \
    crate://crates.io/rustc-demangle/0.1.21 \
    crate://crates.io/rustc_version/0.4.0 \
    crate://crates.io/rustix/0.36.6 \
    crate://crates.io/rustls-pemfile/1.0.1 \
    crate://crates.io/rustls/0.20.7 \
    crate://crates.io/rustversion/1.0.11 \
    crate://crates.io/ryu/1.0.12 \
    crate://crates.io/same-file/1.0.6 \
    crate://crates.io/scopeguard/1.1.0 \
    crate://crates.io/scratch/1.0.3 \
    crate://crates.io/sct/0.7.0 \
    crate://crates.io/sec1/0.3.0 \
    crate://crates.io/semver/1.0.16 \
    crate://crates.io/serde/1.0.152 \
    crate://crates.io/serde_derive/1.0.152 \
    crate://crates.io/serde_json/1.0.93 \
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
    crate://crates.io/signal-hook-registry/1.4.0 \
    crate://crates.io/signature/1.6.4 \
    crate://crates.io/slab/0.4.7 \
    crate://crates.io/smallvec/1.10.0 \
    crate://crates.io/snafu-derive/0.7.4 \
    crate://crates.io/snafu/0.7.4 \
    crate://crates.io/socket2/0.4.7 \
    crate://crates.io/spin/0.5.2 \
    crate://crates.io/spki/0.6.0 \
    crate://crates.io/spki/0.7.2 \
    crate://crates.io/ssh-encoding/0.1.0 \
    crate://crates.io/ssh-key/0.5.1 \
    crate://crates.io/static_assertions/1.1.0 \
    crate://crates.io/subtle/2.4.1 \
    crate://crates.io/syn/1.0.107 \
    crate://crates.io/sync_wrapper/0.1.2 \
    crate://crates.io/synstructure/0.12.6 \
    crate://crates.io/sysinfo/0.27.8 \
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
    crate://crates.io/tinyvec_macros/0.1.0 \
    crate://crates.io/tokio-macros/1.8.2 \
    crate://crates.io/tokio-retry/0.3.0 \
    crate://crates.io/tokio-rustls/0.23.4 \
    crate://crates.io/tokio-stream/0.1.12 \
    crate://crates.io/tokio-util/0.7.7 \
    crate://crates.io/tokio/1.26.0 \
    crate://crates.io/toml/0.5.10 \
    crate://crates.io/tower-http/0.3.5 \
    crate://crates.io/tower-layer/0.3.2 \
    crate://crates.io/tower-service/0.3.2 \
    crate://crates.io/tower/0.4.13 \
    crate://crates.io/tracing-core/0.1.30 \
    crate://crates.io/tracing/0.1.37 \
    crate://crates.io/try-lock/0.2.3 \
    crate://crates.io/typenum/1.16.0 \
    crate://crates.io/unicode-bidi/0.3.8 \
    crate://crates.io/unicode-ident/1.0.6 \
    crate://crates.io/unicode-normalization/0.1.22 \
    crate://crates.io/unicode-width/0.1.10 \
    crate://crates.io/unicode-xid/0.2.4 \
    crate://crates.io/universal-hash/0.5.0 \
    crate://crates.io/untrusted/0.7.1 \
    crate://crates.io/url/2.3.1 \
    crate://crates.io/uuid/1.3.0 \
    crate://crates.io/vcpkg/0.2.15 \
    crate://crates.io/vergen/7.5.1 \
    crate://crates.io/version_check/0.9.4 \
    crate://crates.io/walkdir/2.3.3 \
    crate://crates.io/want/0.3.0 \
    crate://crates.io/wasi/0.10.0+wasi-snapshot-preview1 \
    crate://crates.io/wasi/0.11.0+wasi-snapshot-preview1 \
    crate://crates.io/wasi/0.9.0+wasi-snapshot-preview1 \
    crate://crates.io/wasm-bindgen-backend/0.2.83 \
    crate://crates.io/wasm-bindgen-futures/0.4.33 \
    crate://crates.io/wasm-bindgen-macro-support/0.2.83 \
    crate://crates.io/wasm-bindgen-macro/0.2.83 \
    crate://crates.io/wasm-bindgen-shared/0.2.83 \
    crate://crates.io/wasm-bindgen/0.2.83 \
    crate://crates.io/web-sys/0.3.60 \
    crate://crates.io/webpki/0.22.0 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-util/0.1.5 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi/0.3.9 \
    crate://crates.io/windows-sys/0.42.0 \
    crate://crates.io/windows-sys/0.45.0 \
    crate://crates.io/windows-targets/0.42.1 \
    crate://crates.io/windows_aarch64_gnullvm/0.42.1 \
    crate://crates.io/windows_aarch64_msvc/0.42.1 \
    crate://crates.io/windows_i686_gnu/0.42.1 \
    crate://crates.io/windows_i686_msvc/0.42.1 \
    crate://crates.io/windows_x86_64_gnu/0.42.1 \
    crate://crates.io/windows_x86_64_gnullvm/0.42.1 \
    crate://crates.io/windows_x86_64_msvc/0.42.1 \
    crate://crates.io/winreg/0.10.1 \
    crate://crates.io/yansi/0.5.1 \
    crate://crates.io/yasna/0.5.1 \
    crate://crates.io/zeroize/1.5.7 \
    crate://crates.io/zeroize_derive/1.3.3 \
    git://github.com/toradex/tough;protocol=https;nobranch=1;name=tough;destsuffix=tough \
    git://github.com/warp-tech/russh.git;protocol=https;nobranch=1;name=russh;destsuffix=russh \
"

SRC_URI[addr2line-0.19.0.sha256sum] = "a76fd60b23679b7d19bd066031410fb7e458ccc5e958eb5c325888ce4baedc97"
SRC_URI[adler-1.0.2.sha256sum] = "f26201604c87b1e01bd3d98f8d5d9a8fcbb815e8cedb41ffccbeb4bf593a35fe"
SRC_URI[aead-0.5.1.sha256sum] = "5c192eb8f11fc081b0fe4259ba5af04217d4e0faddd02417310a927911abd7c8"
SRC_URI[aes-gcm-0.10.1.sha256sum] = "82e1366e0c69c9f927b1fa5ce2c7bf9eafc8f9268c0b9800729e8b267612447c"
SRC_URI[aes-0.8.2.sha256sum] = "433cfd6710c9986c576a25ca913c39d66a6474107b406f34f91d4a8923395241"
SRC_URI[aho-corasick-0.7.20.sha256sum] = "cc936419f96fa211c1b9166887b38e5e40b19958e5b895be7c1f93adec7071ac"
SRC_URI[android_system_properties-0.1.5.sha256sum] = "819e7219dbd41043ac279b19830f2efc897156490d7fd6ea916720117ee66311"
SRC_URI[anyhow-1.0.69.sha256sum] = "224afbd727c3d6e4b90103ece64b8d1b67fbb1973b1046c2281eed3f3803f800"
SRC_URI[async-trait-0.1.64.sha256sum] = "1cd7fce9ba8c3c042128ce72d8b2ddbf3a05747efb67ea0313c635e10bda47a2"
SRC_URI[autocfg-1.1.0.sha256sum] = "d468802bab17cbc0cc575e9b053f41e72aa36bfa6b7f55e3529ffa43161b97fa"
SRC_URI[axum-core-0.3.2.sha256sum] = "1cae3e661676ffbacb30f1a824089a8c9150e71017f7e1e38f2aa32009188d34"
SRC_URI[axum-0.6.7.sha256sum] = "2fb79c228270dcf2426e74864cabc94babb5dbab01a4314e702d2f16540e1591"
SRC_URI[backtrace-0.3.67.sha256sum] = "233d376d6d185f2a3093e58f283f60f880315b6c60075b01f36b3b85154564ca"
SRC_URI[base16ct-0.1.1.sha256sum] = "349a06037c7bf932dd7e7d1f653678b2038b9ad46a74102f1fc7bd7872678cce"
SRC_URI[base64-0.13.1.sha256sum] = "9e1b586273c5702936fe7b7d6896644d8be71e6314cfe09d3167c95f712589e8"
SRC_URI[base64-0.21.2.sha256sum] = "604178f6c5c21f02dc555784810edfb88d34ac2c73b2eae109655649ee73ce3d"
SRC_URI[base64ct-1.5.3.sha256sum] = "b645a089122eccb6111b4f81cbc1a49f5900ac4666bb93ac027feaecf15607bf"
SRC_URI[bcrypt-pbkdf-0.9.0.sha256sum] = "3806a8db60cf56efee531616a34a6aaa9a114d6da2add861b0fa4a188881b2c7"
SRC_URI[bit-vec-0.6.3.sha256sum] = "349f9b6a179ed607305526ca489b34ad0a41aed5f7980fa90eb03160b69598fb"
SRC_URI[bitflags-1.3.2.sha256sum] = "bef38d45163c2f1dde094a7dfd33ccf595c92905c8f8f4fdc18d06fb1037718a"
SRC_URI[block-buffer-0.10.3.sha256sum] = "69cce20737498f97b993470a6e536b8523f0af7892a4f928cceb1ac5e52ebe7e"
SRC_URI[block-buffer-0.9.0.sha256sum] = "4152116fd6e9dadb291ae18fc1ec3575ed6d84c29642d97890f4b4a3417297e4"
SRC_URI[block-padding-0.3.2.sha256sum] = "0a90ec2df9600c28a01c56c4784c9207a96d2451833aeceb8cc97e4c9548bb78"
SRC_URI[blowfish-0.9.1.sha256sum] = "e412e2cd0f2b2d93e02543ceae7917b3c70331573df19ee046bcbc35e45e87d7"
SRC_URI[bstr-1.5.0.sha256sum] = "a246e68bb43f6cd9db24bea052a53e40405417c5fb372e3d1a8a7f770a564ef5"
SRC_URI[bumpalo-3.11.1.sha256sum] = "572f695136211188308f16ad2ca5c851a712c464060ae6974944458eb83880ba"
SRC_URI[byteorder-1.4.3.sha256sum] = "14c189c53d098945499cdfa7ecc63567cf3886b3332b312a5b4585d8d3a6a610"
SRC_URI[bytes-1.4.0.sha256sum] = "89b2fd2a0dcf38d7971e2194b6b6eebab45ae01067456a7fd93d5547a61b70be"
SRC_URI[cbc-0.1.2.sha256sum] = "26b52a9543ae338f279b96b0b9fed9c8093744685043739079ce85cd58f289a6"
SRC_URI[cc-1.0.78.sha256sum] = "a20104e2335ce8a659d6dd92a51a767a0c062599c73b343fd152cb401e828c3d"
SRC_URI[cfg-if-1.0.0.sha256sum] = "baf1de4339761588bc0619e3cbc0120ee582ebb74b53b4efbf79117bd2da40fd"
SRC_URI[chacha20-0.9.0.sha256sum] = "c7fc89c7c5b9e7a02dfe45cd2367bae382f9ed31c61ca8debe5f827c420a2f08"
SRC_URI[chrono-0.4.23.sha256sum] = "16b0a3d9ed01224b22057780a37bb8c5dbfe1be8ba48678e7bf57ec4b385411f"
SRC_URI[cipher-0.4.3.sha256sum] = "d1873270f8f7942c191139cb8a40fd228da6c3fd2fc376d7e92d47aa14aeb59e"
SRC_URI[codespan-reporting-0.11.1.sha256sum] = "3538270d33cc669650c4b093848450d380def10c331d38c768e34cac80576e6e"
SRC_URI[color-eyre-0.6.2.sha256sum] = "5a667583cca8c4f8436db8de46ea8233c42a7d9ae424a82d338f2e4675229204"
SRC_URI[config-0.13.3.sha256sum] = "d379af7f68bfc21714c6c7dea883544201741d2ce8274bb12fa54f89507f52a7"
SRC_URI[console-0.15.5.sha256sum] = "c3d79fbe8970a77e3e34151cc13d3b3e248aa0faaecb9f6091fa07ebefe5ad60"
SRC_URI[const-oid-0.9.2.sha256sum] = "520fbf3c07483f94e3e3ca9d0cfd913d7718ef2483d2cfd91c0d9e91474ab913"
SRC_URI[core-foundation-sys-0.8.3.sha256sum] = "5827cebf4670468b8772dd191856768aedcb1b0278a04f989f7766351917b9dc"
SRC_URI[cpufeatures-0.2.5.sha256sum] = "28d997bd5e24a5928dd43e46dc529867e207907fe0b239c3477d924f7f2ca320"
SRC_URI[crc32fast-1.3.2.sha256sum] = "b540bd8bc810d3885c6ea91e2018302f68baba2129ab3e88f32389ee9370880d"
SRC_URI[crypto-bigint-0.4.9.sha256sum] = "ef2b4b23cddf68b89b8f8069890e8c270d54e2d5fe1b143820234805e4cb17ef"
SRC_URI[crypto-common-0.1.6.sha256sum] = "1bfb12502f3fc46cca1bb51ac28df9d618d813cdc3d2f25b9fe775a34af26bb3"
SRC_URI[ctor-0.1.26.sha256sum] = "6d2301688392eb071b0bf1a37be05c469d3cc4dbbd95df672fe28ab021e6a096"
SRC_URI[ctr-0.9.2.sha256sum] = "0369ee1ad671834580515889b80f2ea915f23b8be8d0daa4bbaf2ac5c7590835"
SRC_URI[curve25519-dalek-3.2.0.sha256sum] = "0b9fdf9972b2bd6af2d913799d9ebc165ea4d2e65878e329d9c6b372c4491b61"
SRC_URI[cxx-build-1.0.91.sha256sum] = "48fcaf066a053a41a81dfb14d57d99738b767febb8b735c3016e469fac5da690"
SRC_URI[cxx-1.0.91.sha256sum] = "86d3488e7665a7a483b57e25bdd90d0aeb2bc7608c8d0346acf2ad3f1caf1d62"
SRC_URI[cxxbridge-flags-1.0.91.sha256sum] = "a2ef98b8b717a829ca5603af80e1f9e2e48013ab227b68ef37872ef84ee479bf"
SRC_URI[cxxbridge-macro-1.0.91.sha256sum] = "086c685979a698443656e5cf7856c95c642295a38599f12fb1ff76fb28d19892"
SRC_URI[data-encoding-2.3.3.sha256sum] = "23d8666cb01533c39dde32bcbab8e227b4ed6679b2c925eba05feabea39508fb"
SRC_URI[der-0.6.1.sha256sum] = "f1a467a65c5e759bce6e65eaf91cc29f466cdc57cb65777bd646872a8a1fd4de"
SRC_URI[der-0.7.6.sha256sum] = "56acb310e15652100da43d130af8d97b509e95af61aab1c5a7939ef24337ee17"
SRC_URI[diff-0.1.13.sha256sum] = "56254986775e3233ffa9c4d7d3faaf6d36a2c09d30b20687e9f88bc8bafc16c8"
SRC_URI[digest-0.10.6.sha256sum] = "8168378f4e5023e7218c89c891c0fd8ecdb5e5e4f18cb78f38cf245dd021e76f"
SRC_URI[digest-0.9.0.sha256sum] = "d3dd60d1080a57a05ab032377049e0591415d2b31afd7028356dbf3cc6dcb066"
SRC_URI[dirs-sys-0.3.7.sha256sum] = "1b1d1d91c932ef41c0f2663aa8b0ca0342d444d842c06914aa0a7e352d0bada6"
SRC_URI[dirs-4.0.0.sha256sum] = "ca3aa72a6f96ea37bbc5aa912f6788242832f75369bdfdadcb0e38423f100059"
SRC_URI[doc-comment-0.3.3.sha256sum] = "fea41bba32d969b513997752735605054bc0dfa92b4c56bf1189f2e174be7a10"
SRC_URI[downcast-rs-1.2.0.sha256sum] = "9ea835d29036a4087793836fa931b08837ad5e957da9e23886b29586fb9b6650"
SRC_URI[dyn-clone-1.0.11.sha256sum] = "68b0cf012f1230e43cd00ebb729c6bb58707ecfa8ad08b52ef3a4ccd2697fc30"
SRC_URI[ecdsa-0.14.8.sha256sum] = "413301934810f597c1d19ca71c8710e99a3f1ba28a0d2ebc01551a2daeea3c5c"
SRC_URI[ed25519-dalek-1.0.1.sha256sum] = "c762bae6dcaf24c4c84667b8579785430908723d5c889f469d76a41d59cc7a9d"
SRC_URI[ed25519-1.5.2.sha256sum] = "1e9c280362032ea4203659fc489832d0204ef09f247a0506f170dafcac08c369"
SRC_URI[elliptic-curve-0.12.3.sha256sum] = "e7bb888ab5300a19b8e5bceef25ac745ad065f3c9f7efc6de1b91958110891d3"
SRC_URI[encode_unicode-0.3.6.sha256sum] = "a357d28ed41a50f9c765dbfe56cbc04a64e53e5fc58ba79fbc34c10ef3df831f"
SRC_URI[encoding_rs-0.8.31.sha256sum] = "9852635589dc9f9ea1b6fe9f05b50ef208c85c834a562f0c6abb1c475736ec2b"
SRC_URI[enum-iterator-derive-1.2.0.sha256sum] = "355f93763ef7b0ae1c43c4d8eccc9d5848d84ad1a1d8ce61c421d1ac85a19d05"
SRC_URI[enum-iterator-1.4.0.sha256sum] = "706d9e7cf1c7664859d79cd524e4e53ea2b67ea03c98cc2870c5e539695d597e"
SRC_URI[env_logger-0.10.0.sha256sum] = "85cdab6a89accf66733ad5a1693a4dcced6aeff64602b634530dd73c1f3ee9f0"
SRC_URI[errno-dragonfly-0.1.2.sha256sum] = "aa68f1b12764fab894d2755d2518754e71b4fd80ecfb822714a1206c2aab39bf"
SRC_URI[errno-0.2.8.sha256sum] = "f639046355ee4f37944e44f60642c6f3a7efa3cf6b78c78a0d989a8ce6c396a1"
SRC_URI[eyre-0.6.8.sha256sum] = "4c2b6b5a29c02cdc822728b7d7b8ae1bab3e3b05d44522770ddd49722eeac7eb"
SRC_URI[fastrand-1.9.0.sha256sum] = "e51093e27b0797c359783294ca4f0a911c270184cb10f85783b118614a1501be"
SRC_URI[ff-0.12.1.sha256sum] = "d013fc25338cc558c5c2cfbad646908fb23591e2404481826742b651c9af7160"
SRC_URI[filedescriptor-0.8.2.sha256sum] = "7199d965852c3bac31f779ef99cbb4537f80e952e2d6aa0ffeb30cce00f4f46e"
SRC_URI[flate2-1.0.25.sha256sum] = "a8a2db397cb1c8772f31494cb8917e48cd1e64f0fa7efac59fbd741a0a8ce841"
SRC_URI[fnv-1.0.7.sha256sum] = "3f9eec918d3f24069decb9af1554cad7c880e2da24a9afd88aca000531ab82c1"
SRC_URI[form_urlencoded-1.1.0.sha256sum] = "a9c384f161156f5260c24a097c56119f9be8c798586aecc13afbcbe7b7e26bf8"
SRC_URI[futures-channel-0.3.27.sha256sum] = "164713a5a0dcc3e7b4b1ed7d3b433cabc18025386f9339346e8daf15963cf7ac"
SRC_URI[futures-core-0.3.27.sha256sum] = "86d7a0c1aa76363dac491de0ee99faf6941128376f1cf96f07db7603b7de69dd"
SRC_URI[futures-executor-0.3.27.sha256sum] = "1997dd9df74cdac935c76252744c1ed5794fac083242ea4fe77ef3ed60ba0f83"
SRC_URI[futures-io-0.3.27.sha256sum] = "89d422fa3cbe3b40dca574ab087abb5bc98258ea57eea3fd6f1fa7162c778b91"
SRC_URI[futures-macro-0.3.27.sha256sum] = "3eb14ed937631bd8b8b8977f2c198443447a8355b6e3ca599f38c975e5a963b6"
SRC_URI[futures-sink-0.3.27.sha256sum] = "ec93083a4aecafb2a80a885c9de1f0ccae9dbd32c2bb54b0c3a65690e0b8d2f2"
SRC_URI[futures-task-0.3.27.sha256sum] = "fd65540d33b37b16542a0438c12e6aeead10d4ac5d05bd3f805b8f35ab592879"
SRC_URI[futures-util-0.3.27.sha256sum] = "3ef6b17e481503ec85211fed8f39d1970f128935ca1f814cd32ac4a6842e84ab"
SRC_URI[futures-0.3.27.sha256sum] = "531ac96c6ff5fd7c62263c5e3c67a603af4fcaee2e1a0ae5565ba3a11e69e549"
SRC_URI[generic-array-0.14.6.sha256sum] = "bff49e947297f3312447abdca79f45f4738097cc82b06e72054d2223f601f1b9"
SRC_URI[getrandom-0.1.16.sha256sum] = "8fc3cb4d91f53b50155bdcfd23f6a4c39ae1969c2ae85982b135750cccaf5fce"
SRC_URI[getrandom-0.2.8.sha256sum] = "c05aeb6a22b8f62540c194aac980f2115af067bfe15a0734d7277a768d396b31"
SRC_URI[getset-0.1.2.sha256sum] = "e45727250e75cc04ff2846a66397da8ef2b3db8e40e0cef4df67950a07621eb9"
SRC_URI[ghash-0.5.0.sha256sum] = "d930750de5717d2dd0b8c0d42c076c0e884c81a73e6cab859bbd2339c71e3e40"
SRC_URI[gimli-0.27.2.sha256sum] = "ad0a93d233ebf96623465aad4046a8d3aa4da22d4f4beba5388838c8a434bbb4"
SRC_URI[git2-0.16.1.sha256sum] = "ccf7f68c2995f392c49fffb4f95ae2c873297830eb25c6bc4c114ce8f4562acc"
SRC_URI[globset-0.4.10.sha256sum] = "029d74589adefde59de1a0c4f4732695c32805624aec7b68d91503d4dba79afc"
SRC_URI[group-0.12.1.sha256sum] = "5dfbfb3a6cfbd390d5c9564ab283a0349b9b9fcd46a706c1eb10e0db70bfbac7"
SRC_URI[h2-0.3.15.sha256sum] = "5f9f29bc9dda355256b2916cf526ab02ce0aeaaaf2bad60d65ef3f12f11dd0f4"
SRC_URI[hashbrown-0.12.3.sha256sum] = "8a9ee70c43aaf417c914396645a0fa852624801b24ebb7ae78fe8272889ac888"
SRC_URI[heck-0.4.1.sha256sum] = "95505c38b4572b2d910cecb0281560f54b440a19336cbbcb27bf6ce6adc6f5a8"
SRC_URI[hermit-abi-0.2.6.sha256sum] = "ee512640fe35acbfb4bb779db6f0d80704c2cacfa2e39b601ef3e3f47d1ae4c7"
SRC_URI[hex-literal-0.3.4.sha256sum] = "7ebdb29d2ea9ed0083cd8cece49bbd968021bd99b0849edb4a9a7ee0fdf6a4e0"
SRC_URI[hex-0.4.3.sha256sum] = "7f24254aa9a54b5c858eaee2f5bccdb46aaf0e486a595ed5fd8f86ba55232a70"
SRC_URI[hmac-0.12.1.sha256sum] = "6c49c37c09c17a53d937dfbb742eb3a961d65a994e6bcdcf37e7399d0cc8ab5e"
SRC_URI[http-body-0.4.5.sha256sum] = "d5f38f16d184e36f2408a55281cd658ecbd3ca05cce6d6510a176eca393e26d1"
SRC_URI[http-range-header-0.3.0.sha256sum] = "0bfe8eed0a9285ef776bb792479ea3834e8b94e13d615c2f66d03dd50a435a29"
SRC_URI[http-0.2.9.sha256sum] = "bd6effc99afb63425aff9b05836f029929e345a6148a14b7ecd5ab67af944482"
SRC_URI[httparse-1.8.0.sha256sum] = "d897f394bad6a705d5f4104762e116a75639e470d80901eed05a860a95cb1904"
SRC_URI[httpdate-1.0.2.sha256sum] = "c4a1e36c821dbe04574f602848a19f742f4fb3c98d40449f11bcad18d6b17421"
SRC_URI[humantime-2.1.0.sha256sum] = "9a3a5bfb195931eeb336b2a7b4d761daec841b97f947d34394601737a7bba5e4"
SRC_URI[hyper-rustls-0.23.2.sha256sum] = "1788965e61b367cd03a62950836d5cd41560c3577d90e40e0819373194d1661c"
SRC_URI[hyper-0.14.23.sha256sum] = "034711faac9d2166cb1baf1a2fb0b60b1f277f8492fd72176c17f3515e1abd3c"
SRC_URI[iana-time-zone-haiku-0.1.1.sha256sum] = "0703ae284fc167426161c2e3f1da3ea71d94b21bedbcc9494e92b28e334e3dca"
SRC_URI[iana-time-zone-0.1.53.sha256sum] = "64c122667b287044802d6ce17ee2ddf13207ed924c712de9a66a5814d5b64765"
SRC_URI[idna-0.3.0.sha256sum] = "e14ddfc70884202db2244c223200c204c2bda1bc6e0998d11b5e024d657209e6"
SRC_URI[indenter-0.3.3.sha256sum] = "ce23b50ad8242c51a442f3ff322d56b02f08852c77e4c0b4d3fd684abc89c683"
SRC_URI[indexmap-1.9.2.sha256sum] = "1885e79c1fc4b10f0e172c475f458b7f7b93061064d98c3293e98c5ba0c8b399"
SRC_URI[inout-0.1.3.sha256sum] = "a0c10553d664a4d0bcff9f4215d0aac67a639cc68ef660840afe309b807bc9f5"
SRC_URI[instant-0.1.12.sha256sum] = "7a5bbe824c507c5da5956355e86a746d82e0e1464f65d862cc5e71da70e94b2c"
SRC_URI[io-lifetimes-1.0.3.sha256sum] = "46112a93252b123d31a119a8d1a1ac19deac4fac6e0e8b0df58f0d4e5870e63c"
SRC_URI[ioctl-rs-0.1.6.sha256sum] = "f7970510895cee30b3e9128319f2cefd4bde883a39f38baa279567ba3a7eb97d"
SRC_URI[ipnet-2.7.0.sha256sum] = "11b0d96e660696543b251e58030cf9787df56da39dab19ad60eae7353040917e"
SRC_URI[is-terminal-0.4.2.sha256sum] = "28dfb6c8100ccc63462345b67d1bbc3679177c75ee4bf59bf29c8b1d110b8189"
SRC_URI[itoa-1.0.5.sha256sum] = "fad582f4b9e86b6caa621cabeb0963332d92eea04729ab12892c2533951e6440"
SRC_URI[jobserver-0.1.26.sha256sum] = "936cfd212a0155903bcbc060e316fb6cc7cbf2e1907329391ebadc1fe0ce77c2"
SRC_URI[js-sys-0.3.60.sha256sum] = "49409df3e3bf0856b916e2ceaca09ee28e6871cf7d9ce97a692cacfdb2a25a47"
SRC_URI[lazy_static-1.4.0.sha256sum] = "e2abad23fbc42b3700f2f279844dc832adb2b2eb069b2df918f455c4e18cc646"
SRC_URI[libc-0.2.139.sha256sum] = "201de327520df007757c1f0adce6e827fe8562fbc28bfd9c15571c66ca1f5f79"
SRC_URI[libgit2-sys-0.14.2+1.5.1.sha256sum] = "7f3d95f6b51075fe9810a7ae22c7095f12b98005ab364d8544797a825ce946a4"
SRC_URI[libm-0.2.6.sha256sum] = "348108ab3fba42ec82ff6e9564fc4ca0247bdccdc68dd8af9764bbc79c3c8ffb"
SRC_URI[libz-sys-1.1.8.sha256sum] = "9702761c3935f8cc2f101793272e202c72b99da8f4224a19ddcf1279a6450bbf"
SRC_URI[link-cplusplus-1.0.8.sha256sum] = "ecd207c9c713c34f95a097a5b029ac2ce6010530c7b49d7fea24d977dede04f5"
SRC_URI[linux-raw-sys-0.1.4.sha256sum] = "f051f77a7c8e6957c0696eac88f26b0117e54f52d3fc682ab19397a8812846a4"
SRC_URI[lock_api-0.4.9.sha256sum] = "435011366fe56583b16cf956f9df0095b405b82d76425bc8981c0e22e60ec4df"
SRC_URI[log-0.4.17.sha256sum] = "abb12e687cfb44aa40f41fc3978ef76448f9b6038cad6aef4259d3c095a2382e"
SRC_URI[matchit-0.7.0.sha256sum] = "b87248edafb776e59e6ee64a79086f65890d3510f2c656c000bf2a7e8a0aea40"
SRC_URI[md5-0.7.0.sha256sum] = "490cc448043f947bae3cbee9c203358d62dbee0db12107a74be5c30ccfd09771"
SRC_URI[memchr-2.5.0.sha256sum] = "2dffe52ecf27772e601905b7522cb4ef790d2cc203488bbd0e2fe85fcb74566d"
SRC_URI[memoffset-0.6.5.sha256sum] = "5aa361d4faea93603064a027415f07bd8e1d5c88c9fbf68bf56a285428fd79ce"
SRC_URI[mime-0.3.16.sha256sum] = "2a60c7ce501c71e03a9c9c0d35b861413ae925bd979cc7a4e30d060069aaac8d"
SRC_URI[minimal-lexical-0.2.1.sha256sum] = "68354c5c6bd36d73ff3feceb05efa59b6acb7626617f4962be322a825e61f79a"
SRC_URI[miniz_oxide-0.6.2.sha256sum] = "b275950c28b37e794e8c55d88aeb5e139d0ce23fdbbeda68f8d7174abdf9e8fa"
SRC_URI[mio-0.8.5.sha256sum] = "e5d732bc30207a6423068df043e3d02e0735b155ad7ce1a6f76fe2baa5b158de"
SRC_URI[nix-0.25.1.sha256sum] = "f346ff70e7dbfd675fe90590b92d59ef2de15a8779ae305ebcbfd3f0caf59be4"
SRC_URI[nix-0.26.2.sha256sum] = "bfdda3d196821d6af13126e40375cdf7da646a96114af134d5f417a9a1dc8e1a"
SRC_URI[nom-7.1.3.sha256sum] = "d273983c5a657a70a3e8f2a01329822f3b8c8172b73826411a55751e404a0a4a"
SRC_URI[ntapi-0.4.0.sha256sum] = "bc51db7b362b205941f71232e56c625156eb9a929f8cf74a428fd5bc094a4afc"
SRC_URI[num-bigint-dig-0.8.2.sha256sum] = "2399c9463abc5f909349d8aa9ba080e0b88b3ce2885389b60b993f39b1a56905"
SRC_URI[num-bigint-0.4.3.sha256sum] = "f93ab6289c7b344a8a9f60f88d80aa20032336fe78da341afc91c8a2341fc75f"
SRC_URI[num-integer-0.1.45.sha256sum] = "225d3389fb3509a24c93f5c29eb6bde2586b98d9f016636dff58d7c6f7569cd9"
SRC_URI[num-iter-0.1.43.sha256sum] = "7d03e6c028c5dc5cac6e2dec0efda81fc887605bb3d884578bb6d6bf7514e252"
SRC_URI[num-traits-0.2.15.sha256sum] = "578ede34cf02f8924ab9447f50c28075b4d3e5b269972345e7e0372b38c6cdcd"
SRC_URI[num_cpus-1.15.0.sha256sum] = "0fac9e2da13b5eb447a6ce3d392f23a29d8694bff781bf03a16cd9ac8697593b"
SRC_URI[object-0.30.3.sha256sum] = "ea86265d3d3dcb6a27fc51bd29a4bf387fae9d2986b823079d4986af253eb439"
SRC_URI[olpc-cjson-0.1.3.sha256sum] = "d637c9c15b639ccff597da8f4fa968300651ad2f1e968aefc3b4927a6fb2027a"
SRC_URI[once_cell-1.16.0.sha256sum] = "86f0b0d4bf799edbc74508c1e8bf170ff5f41238e5f8225603ca7caaae2b7860"
SRC_URI[opaque-debug-0.3.0.sha256sum] = "624a8340c38c1b80fd549087862da4ba43e08858af025b236e509b6649fc13d5"
SRC_URI[output_vt100-0.1.3.sha256sum] = "628223faebab4e3e40667ee0b2336d34a5b960ff60ea743ddfdbcf7770bcfb66"
SRC_URI[owo-colors-3.5.0.sha256sum] = "c1b04fb49957986fdce4d6ee7a65027d55d4b6d2265e5848bbb507b58ccfdb6f"
SRC_URI[p256-0.11.1.sha256sum] = "51f44edd08f51e2ade572f141051021c5af22677e42b7dd28a88155151c33594"
SRC_URI[p384-0.11.2.sha256sum] = "dfc8c5bf642dde52bb9e87c0ecd8ca5a76faac2eeed98dedb7c717997e1080aa"
SRC_URI[parking_lot-0.12.1.sha256sum] = "3742b2c103b9f06bc9fff0a37ff4912935851bee6d36f3c02bcc755bcfec228f"
SRC_URI[parking_lot_core-0.9.6.sha256sum] = "ba1ef8814b5c993410bb3adfad7a5ed269563e4a2f90c41f5d85be7fb47133bf"
SRC_URI[password-hash-0.4.2.sha256sum] = "7676374caaee8a325c9e7a2ae557f216c5563a171d6997b0ef8a65af35147700"
SRC_URI[path-absolutize-3.1.0.sha256sum] = "43eb3595c63a214e1b37b44f44b0a84900ef7ae0b4c5efce59e123d246d7a0de"
SRC_URI[path-dedot-3.1.0.sha256sum] = "9d55e486337acb9973cdea3ec5638c1b3bcb22e573b2b7b41969e0c744d5a15e"
SRC_URI[pathdiff-0.2.1.sha256sum] = "8835116a5c179084a830efb3adc117ab007512b535bc1a21c991d3b32a6b44dd"
SRC_URI[pbkdf2-0.11.0.sha256sum] = "83a0692ec44e4cf1ef28ca317f14f8f07da2d95ec3fa01f86e4467b725e60917"
SRC_URI[pem-rfc7468-0.6.0.sha256sum] = "24d159833a9105500e0398934e205e0773f0b27529557134ecfc51c27646adac"
SRC_URI[pem-rfc7468-0.7.0.sha256sum] = "88b39c9bfcfc231068454382784bb460aae594343fb030d46e9f50a645418412"
SRC_URI[pem-1.1.1.sha256sum] = "a8835c273a76a90455d7344889b0964598e3316e2a79ede8e36f16bdcf2228b8"
SRC_URI[percent-encoding-2.2.0.sha256sum] = "478c572c3d73181ff3c2539045f6eb99e5491218eae919370993b890cdbdd98e"
SRC_URI[pin-project-internal-1.0.12.sha256sum] = "069bdb1e05adc7a8990dce9cc75370895fbe4e3d58b9b73bf1aee56359344a55"
SRC_URI[pin-project-lite-0.2.9.sha256sum] = "e0a7ae3ac2f1173085d398531c705756c94a4c56843785df85a60c1a0afac116"
SRC_URI[pin-project-1.0.12.sha256sum] = "ad29a609b6bcd67fee905812e544992d216af9d755757c05ed2d0e15a74c6ecc"
SRC_URI[pin-utils-0.1.0.sha256sum] = "8b870d8c151b6f2fb93e84a13146138f05d02ed11c7e7c54f8826aaaf7c9f184"
SRC_URI[pkcs1-0.4.1.sha256sum] = "eff33bdbdfc54cc98a2eca766ebdec3e1b8fb7387523d5c9c9a2891da856f719"
SRC_URI[pkcs8-0.9.0.sha256sum] = "9eca2c590a5f85da82668fa685c09ce2888b9430e83299debf1f34b65fd4a4ba"
SRC_URI[pkg-config-0.3.26.sha256sum] = "6ac9a59f73473f1b8d852421e59e64809f025994837ef743615c6d0c5b305160"
SRC_URI[poly1305-0.8.0.sha256sum] = "8159bd90725d2df49889a078b54f4f79e87f1f8a8444194cdca81d38f5393abf"
SRC_URI[polyval-0.6.0.sha256sum] = "7ef234e08c11dfcb2e56f79fd70f6f2eb7f025c0ce2333e82f4f0518ecad30c6"
SRC_URI[portable-pty-0.8.0.sha256sum] = "e2b3618fce1e28b21553c7858a53d3fc8779fd6f5bc7404ee2a7472816c67cbb"
SRC_URI[ppv-lite86-0.2.17.sha256sum] = "5b40af805b3121feab8a3c29f04d8ad262fa8e0561883e7653e024ae4479e6de"
SRC_URI[pretty_assertions-1.3.0.sha256sum] = "a25e9bcb20aa780fd0bb16b72403a9064d6b3f22f026946029acb941a50af755"
SRC_URI[proc-macro-error-attr-1.0.4.sha256sum] = "a1be40180e52ecc98ad80b184934baf3d0d29f979574e439af5a55274b35f869"
SRC_URI[proc-macro-error-1.0.4.sha256sum] = "da25490ff9892aab3fcf7c36f08cfb902dd3e71ca0f9f9517bea02a73a5ce38c"
SRC_URI[proc-macro2-1.0.49.sha256sum] = "57a8eca9f9c4ffde41714334dee777596264c7825420f521abc92b5b5deb63a5"
SRC_URI[quote-1.0.23.sha256sum] = "8856d8364d252a14d474036ea1358d63c9e6965c8e5c1885c18f73d70bff9c7b"
SRC_URI[rand-0.7.3.sha256sum] = "6a6b1679d49b24bbfe0c803429aa1874472f50d9b363131f0e89fc356b544d03"
SRC_URI[rand-0.8.5.sha256sum] = "34af8d1a0e25924bc5b7c43c079c942339d8f0a8b57c39049bef581b46327404"
SRC_URI[rand_chacha-0.2.2.sha256sum] = "f4c8ed856279c9737206bf725bf36935d8666ead7aa69b52be55af369d193402"
SRC_URI[rand_chacha-0.3.1.sha256sum] = "e6c10a63a0fa32252be49d21e7709d4d4baf8d231c2dbce1eaa8141b9b127d88"
SRC_URI[rand_core-0.5.1.sha256sum] = "90bde5296fc891b0cef12a6d03ddccc162ce7b2aff54160af9338f8d40df6d19"
SRC_URI[rand_core-0.6.4.sha256sum] = "ec0be4795e2f6a28069bec0b5ff3e2ac9bafc99e6a9a7dc3547996c5c816922c"
SRC_URI[rand_hc-0.2.0.sha256sum] = "ca3129af7b92a17112d59ad498c6f81eaf463253766b90396d39ea7a39d6613c"
SRC_URI[redox_syscall-0.2.16.sha256sum] = "fb5a58c1855b4b6819d59012155603f0b22ad30cad752600aadfcb695265519a"
SRC_URI[redox_users-0.4.3.sha256sum] = "b033d837a7cf162d7993aded9304e30a83213c648b6e389db233191f891e5c2b"
SRC_URI[regex-syntax-0.6.29.sha256sum] = "f162c6dd7b008981e4d40210aca20b4bd0f9b60ca9271061b07f78537722f2e1"
SRC_URI[regex-1.7.3.sha256sum] = "8b1f693b24f6ac912f4893ef08244d70b6067480d2f1a46e950c9691e6749d1d"
SRC_URI[reqwest-0.11.13.sha256sum] = "68cc60575865c7831548863cc02356512e3f1dc2f3f82cb837d7fc4cc8f3c97c"
SRC_URI[rfc6979-0.3.1.sha256sum] = "7743f17af12fa0b03b803ba12cd6a8d9483a587e89c69445e3909655c0b9fabb"
SRC_URI[ring-0.16.20.sha256sum] = "3053cf52e236a3ed746dfc745aa9cacf1b791d846bdaf412f60a8d7d6e17c8fc"
SRC_URI[rsa-0.7.2.sha256sum] = "094052d5470cbcef561cb848a7209968c9f12dfa6d668f4bca048ac5de51099c"
SRC_URI[rustc-demangle-0.1.21.sha256sum] = "7ef03e0a2b150c7a90d01faf6254c9c48a41e95fb2a8c2ac1c6f0d2b9aefc342"
SRC_URI[rustc_version-0.4.0.sha256sum] = "bfa0f585226d2e68097d4f95d113b15b83a82e819ab25717ec0590d9584ef366"
SRC_URI[rustix-0.36.6.sha256sum] = "4feacf7db682c6c329c4ede12649cd36ecab0f3be5b7d74e6a20304725db4549"
SRC_URI[rustls-pemfile-1.0.1.sha256sum] = "0864aeff53f8c05aa08d86e5ef839d3dfcf07aeba2db32f12db0ef716e87bd55"
SRC_URI[rustls-0.20.7.sha256sum] = "539a2bfe908f471bfa933876bd1eb6a19cf2176d375f82ef7f99530a40e48c2c"
SRC_URI[rustversion-1.0.11.sha256sum] = "5583e89e108996506031660fe09baa5011b9dd0341b89029313006d1fb508d70"
SRC_URI[ryu-1.0.12.sha256sum] = "7b4b9743ed687d4b4bcedf9ff5eaa7398495ae14e61cba0a295704edbc7decde"
SRC_URI[same-file-1.0.6.sha256sum] = "93fc1dc3aaa9bfed95e02e6eadabb4baf7e3078b0bd1b4d7b6b0b68378900502"
SRC_URI[scopeguard-1.1.0.sha256sum] = "d29ab0c6d3fc0ee92fe66e2d99f700eab17a8d57d1c1d3b748380fb20baa78cd"
SRC_URI[scratch-1.0.3.sha256sum] = "ddccb15bcce173023b3fedd9436f882a0739b8dfb45e4f6b6002bee5929f61b2"
SRC_URI[sct-0.7.0.sha256sum] = "d53dcdb7c9f8158937a7981b48accfd39a43af418591a5d008c7b22b5e1b7ca4"
SRC_URI[sec1-0.3.0.sha256sum] = "3be24c1842290c45df0a7bf069e0c268a747ad05a192f2fd7dcfdbc1cba40928"
SRC_URI[semver-1.0.16.sha256sum] = "58bc9567378fc7690d6b2addae4e60ac2eeea07becb2c64b9f218b53865cba2a"
SRC_URI[serde-1.0.152.sha256sum] = "bb7d1f0d3021d347a83e556fc4683dea2ea09d87bccdf88ff5c12545d89d5efb"
SRC_URI[serde_derive-1.0.152.sha256sum] = "af487d118eecd09402d70a5d72551860e788df87b464af30e5ea6a38c75c541e"
SRC_URI[serde_json-1.0.93.sha256sum] = "cad406b69c91885b5107daf2c29572f6c8cdb3c66826821e286c533490c0bc76"
SRC_URI[serde_path_to_error-0.1.9.sha256sum] = "26b04f22b563c91331a10074bda3dd5492e3cc39d56bd557e91c0af42b6c7341"
SRC_URI[serde_plain-1.0.1.sha256sum] = "d6018081315db179d0ce57b1fe4b62a12a0028c9cf9bbef868c9cf477b3c34ae"
SRC_URI[serde_urlencoded-0.7.1.sha256sum] = "d3491c14715ca2294c4d6a88f15e84739788c1d030eed8c110436aafdaa2f3fd"
SRC_URI[serial-core-0.4.0.sha256sum] = "3f46209b345401737ae2125fe5b19a77acce90cd53e1658cda928e4fe9a64581"
SRC_URI[serial-unix-0.4.0.sha256sum] = "f03fbca4c9d866e24a459cbca71283f545a37f8e3e002ad8c70593871453cab7"
SRC_URI[serial-windows-0.4.0.sha256sum] = "15c6d3b776267a75d31bbdfd5d36c0ca051251caafc285827052bc53bcdc8162"
SRC_URI[serial-0.4.0.sha256sum] = "a1237a96570fc377c13baa1b88c7589ab66edced652e43ffb17088f003db3e86"
SRC_URI[sha1-0.10.5.sha256sum] = "f04293dc80c3993519f2d7f6f511707ee7094fe0c6d3406feb330cdb3540eba3"
SRC_URI[sha2-0.10.6.sha256sum] = "82e6b795fe2e3b1e845bafcb27aa35405c4d47cdfc92af5fc8d3002f76cebdc0"
SRC_URI[sha2-0.9.9.sha256sum] = "4d58a1e1bf39749807d89cf2d98ac2dfa0ff1cb3faa38fbb64dd88ac8013d800"
SRC_URI[shared_library-0.1.9.sha256sum] = "5a9e7e0f2bfae24d8a5b5a66c5b257a83c7412304311512a0c054cd5e619da11"
SRC_URI[shell-words-1.1.0.sha256sum] = "24188a676b6ae68c3b2cb3a01be17fbf7240ce009799bb56d5b1409051e78fde"
SRC_URI[signal-hook-registry-1.4.0.sha256sum] = "e51e73328dc4ac0c7ccbda3a494dfa03df1de2f46018127f60c693f2648455b0"
SRC_URI[signature-1.6.4.sha256sum] = "74233d3b3b2f6d4b006dc19dee745e73e2a6bfb6f93607cd3b02bd5b00797d7c"
SRC_URI[slab-0.4.7.sha256sum] = "4614a76b2a8be0058caa9dbbaf66d988527d86d003c11a94fbd335d7661edcef"
SRC_URI[smallvec-1.10.0.sha256sum] = "a507befe795404456341dfab10cef66ead4c041f62b8b11bbb92bffe5d0953e0"
SRC_URI[snafu-derive-0.7.4.sha256sum] = "475b3bbe5245c26f2d8a6f62d67c1f30eb9fffeccee721c45d162c3ebbdf81b2"
SRC_URI[snafu-0.7.4.sha256sum] = "cb0656e7e3ffb70f6c39b3c2a86332bb74aa3c679da781642590f3c1118c5045"
SRC_URI[socket2-0.4.7.sha256sum] = "02e2d2db9033d13a1567121ddd7a095ee144db4e1ca1b1bda3419bc0da294ebd"
SRC_URI[spin-0.5.2.sha256sum] = "6e63cff320ae2c57904679ba7cb63280a3dc4613885beafb148ee7bf9aa9042d"
SRC_URI[spki-0.6.0.sha256sum] = "67cf02bbac7a337dc36e4f5a693db6c21e7863f45070f7064577eb4367a3212b"
SRC_URI[spki-0.7.2.sha256sum] = "9d1e996ef02c474957d681f1b05213dfb0abab947b446a62d37770b23500184a"
SRC_URI[ssh-encoding-0.1.0.sha256sum] = "19cfdc32e0199062113edf41f344fbf784b8205a94600233c84eb838f45191e1"
SRC_URI[ssh-key-0.5.1.sha256sum] = "288d8f5562af5a3be4bda308dd374b2c807b940ac370b5efa1c99311da91d9a1"
SRC_URI[static_assertions-1.1.0.sha256sum] = "a2eb9349b6444b326872e140eb1cf5e7c522154d69e7a0ffb0fb81c06b37543f"
SRC_URI[subtle-2.4.1.sha256sum] = "6bdef32e8150c2a081110b42772ffe7d7c9032b606bc226c8260fd97e0976601"
SRC_URI[syn-1.0.107.sha256sum] = "1f4064b5b16e03ae50984a5a8ed5d4f8803e6bc1fd170a3cda91a1be4b18e3f5"
SRC_URI[sync_wrapper-0.1.2.sha256sum] = "2047c6ded9c721764247e62cd3b03c09ffc529b2ba5b10ec482ae507a4a70160"
SRC_URI[synstructure-0.12.6.sha256sum] = "f36bdaa60a83aca3921b5259d5400cbf5e90fc51931376a9bd4a0eb79aa7210f"
SRC_URI[sysinfo-0.27.8.sha256sum] = "a902e9050fca0a5d6877550b769abd2bd1ce8c04634b941dbe2809735e1a1e33"
SRC_URI[tempfile-3.4.0.sha256sum] = "af18f7ae1acd354b992402e9ec5864359d693cd8a79dcbef59f76891701c1e95"
SRC_URI[termcolor-1.1.3.sha256sum] = "bab24d30b911b2376f3a13cc2cd443142f0c81dda04c118693e35b3835757755"
SRC_URI[termios-0.2.2.sha256sum] = "d5d9cf598a6d7ce700a4e6a9199da127e6819a61e64b68609683cc9a01b5683a"
SRC_URI[thiserror-impl-1.0.38.sha256sum] = "1fb327af4685e4d03fa8cbcf1716380da910eeb2bb8be417e7f9fd3fb164f36f"
SRC_URI[thiserror-1.0.38.sha256sum] = "6a9cd18aa97d5c45c6603caea1da6628790b37f7a34b6ca89522331c5180fed0"
SRC_URI[time-core-0.1.0.sha256sum] = "2e153e1f1acaef8acc537e68b44906d2db6436e2b35ac2c6b42640fff91f00fd"
SRC_URI[time-macros-0.2.8.sha256sum] = "fd80a657e71da814b8e5d60d3374fc6d35045062245d80224748ae522dd76f36"
SRC_URI[time-0.1.45.sha256sum] = "1b797afad3f312d1c66a56d11d0316f916356d11bd158fbc6ca6389ff6bf805a"
SRC_URI[time-0.3.20.sha256sum] = "cd0cbfecb4d19b5ea75bb31ad904eb5b9fa13f21079c3b92017ebdf4999a5890"
SRC_URI[tinyvec-1.6.0.sha256sum] = "87cc5ceb3875bb20c2890005a4e226a4651264a5c75edb2421b52861a0a0cb50"
SRC_URI[tinyvec_macros-0.1.0.sha256sum] = "cda74da7e1a664f795bb1f8a87ec406fb89a02522cf6e50620d016add6dbbf5c"
SRC_URI[tokio-macros-1.8.2.sha256sum] = "d266c00fde287f55d3f1c3e96c500c362a2b8c695076ec180f27918820bc6df8"
SRC_URI[tokio-retry-0.3.0.sha256sum] = "7f57eb36ecbe0fc510036adff84824dd3c24bb781e21bfa67b69d556aa85214f"
SRC_URI[tokio-rustls-0.23.4.sha256sum] = "c43ee83903113e03984cb9e5cebe6c04a5116269e900e3ddba8f068a62adda59"
SRC_URI[tokio-stream-0.1.12.sha256sum] = "8fb52b74f05dbf495a8fba459fdc331812b96aa086d9eb78101fa0d4569c3313"
SRC_URI[tokio-util-0.7.7.sha256sum] = "5427d89453009325de0d8f342c9490009f76e999cb7672d77e46267448f7e6b2"
SRC_URI[tokio-1.26.0.sha256sum] = "03201d01c3c27a29c8a5cee5b55a93ddae1ccf6f08f65365c2c918f8c1b76f64"
SRC_URI[toml-0.5.10.sha256sum] = "1333c76748e868a4d9d1017b5ab53171dfd095f70c712fdb4653a406547f598f"
SRC_URI[tower-http-0.3.5.sha256sum] = "f873044bf02dd1e8239e9c1293ea39dad76dc594ec16185d0a1bf31d8dc8d858"
SRC_URI[tower-layer-0.3.2.sha256sum] = "c20c8dbed6283a09604c3e69b4b7eeb54e298b8a600d4d5ecb5ad39de609f1d0"
SRC_URI[tower-service-0.3.2.sha256sum] = "b6bc1c9ce2b5135ac7f93c72918fc37feb872bdc6a5533a8b85eb4b86bfdae52"
SRC_URI[tower-0.4.13.sha256sum] = "b8fa9be0de6cf49e536ce1851f987bd21a43b771b09473c3549a6c853db37c1c"
SRC_URI[tracing-core-0.1.30.sha256sum] = "24eb03ba0eab1fd845050058ce5e616558e8f8d8fca633e6b163fe25c797213a"
SRC_URI[tracing-0.1.37.sha256sum] = "8ce8c33a8d48bd45d624a6e523445fd21ec13d3653cd51f681abf67418f54eb8"
SRC_URI[try-lock-0.2.3.sha256sum] = "59547bce71d9c38b83d9c0e92b6066c4253371f15005def0c30d9657f50c7642"
SRC_URI[typenum-1.16.0.sha256sum] = "497961ef93d974e23eb6f433eb5fe1b7930b659f06d12dec6fc44a8f554c0bba"
SRC_URI[unicode-bidi-0.3.8.sha256sum] = "099b7128301d285f79ddd55b9a83d5e6b9e97c92e0ea0daebee7263e932de992"
SRC_URI[unicode-ident-1.0.6.sha256sum] = "84a22b9f218b40614adcb3f4ff08b703773ad44fa9423e4e0d346d5db86e4ebc"
SRC_URI[unicode-normalization-0.1.22.sha256sum] = "5c5713f0fc4b5db668a2ac63cdb7bb4469d8c9fed047b1d0292cc7b0ce2ba921"
SRC_URI[unicode-width-0.1.10.sha256sum] = "c0edd1e5b14653f783770bce4a4dabb4a5108a5370a5f5d8cfe8710c361f6c8b"
SRC_URI[unicode-xid-0.2.4.sha256sum] = "f962df74c8c05a667b5ee8bcf162993134c104e96440b663c8daa176dc772d8c"
SRC_URI[universal-hash-0.5.0.sha256sum] = "7d3160b73c9a19f7e2939a2fdad446c57c1bbbbf4d919d3213ff1267a580d8b5"
SRC_URI[untrusted-0.7.1.sha256sum] = "a156c684c91ea7d62626509bce3cb4e1d9ed5c4d978f7b4352658f96a4c26b4a"
SRC_URI[url-2.3.1.sha256sum] = "0d68c799ae75762b8c3fe375feb6600ef5602c883c5d21eb51c09f22b83c4643"
SRC_URI[uuid-1.3.0.sha256sum] = "1674845326ee10d37ca60470760d4288a6f80f304007d92e5c53bab78c9cfd79"
SRC_URI[vcpkg-0.2.15.sha256sum] = "accd4ea62f7bb7a82fe23066fb0957d48ef677f6eeb8215f372f52e48bb32426"
SRC_URI[vergen-7.5.1.sha256sum] = "f21b881cd6636ece9735721cf03c1fe1e774fe258683d084bb2812ab67435749"
SRC_URI[version_check-0.9.4.sha256sum] = "49874b5167b65d7193b8aba1567f5c7d93d001cafc34600cee003eda787e483f"
SRC_URI[walkdir-2.3.3.sha256sum] = "36df944cda56c7d8d8b7496af378e6b16de9284591917d307c9b4d313c44e698"
SRC_URI[want-0.3.0.sha256sum] = "1ce8a968cb1cd110d136ff8b819a556d6fb6d919363c61534f6860c7eb172ba0"
SRC_URI[wasi-0.10.0+wasi-snapshot-preview1.sha256sum] = "1a143597ca7c7793eff794def352d41792a93c481eb1042423ff7ff72ba2c31f"
SRC_URI[wasi-0.11.0+wasi-snapshot-preview1.sha256sum] = "9c8d87e72b64a3b4db28d11ce29237c246188f4f51057d65a7eab63b7987e423"
SRC_URI[wasi-0.9.0+wasi-snapshot-preview1.sha256sum] = "cccddf32554fecc6acb585f82a32a72e28b48f8c4c1883ddfeeeaa96f7d8e519"
SRC_URI[wasm-bindgen-backend-0.2.83.sha256sum] = "4c8ffb332579b0557b52d268b91feab8df3615f265d5270fec2a8c95b17c1142"
SRC_URI[wasm-bindgen-futures-0.4.33.sha256sum] = "23639446165ca5a5de86ae1d8896b737ae80319560fbaa4c2887b7da6e7ebd7d"
SRC_URI[wasm-bindgen-macro-support-0.2.83.sha256sum] = "07bc0c051dc5f23e307b13285f9d75df86bfdf816c5721e573dec1f9b8aa193c"
SRC_URI[wasm-bindgen-macro-0.2.83.sha256sum] = "052be0f94026e6cbc75cdefc9bae13fd6052cdcaf532fa6c45e7ae33a1e6c810"
SRC_URI[wasm-bindgen-shared-0.2.83.sha256sum] = "1c38c045535d93ec4f0b4defec448e4291638ee608530863b1e2ba115d4fff7f"
SRC_URI[wasm-bindgen-0.2.83.sha256sum] = "eaf9f5aceeec8be17c128b2e93e031fb8a4d469bb9c4ae2d7dc1888b26887268"
SRC_URI[web-sys-0.3.60.sha256sum] = "bcda906d8be16e728fd5adc5b729afad4e444e106ab28cd1c7256e54fa61510f"
SRC_URI[webpki-0.22.0.sha256sum] = "f095d78192e208183081cc07bc5515ef55216397af48b873e5edcd72637fa1bd"
SRC_URI[winapi-i686-pc-windows-gnu-0.4.0.sha256sum] = "ac3b87c63620426dd9b991e5ce0329eff545bccbbb34f3be09ff6fb6ab51b7b6"
SRC_URI[winapi-util-0.1.5.sha256sum] = "70ec6ce85bb158151cae5e5c87f95a8e97d2c0c4b001223f33a334e3ce5de178"
SRC_URI[winapi-x86_64-pc-windows-gnu-0.4.0.sha256sum] = "712e227841d057c1ee1cd2fb22fa7e5a5461ae8e48fa2ca79ec42cfc1931183f"
SRC_URI[winapi-0.3.9.sha256sum] = "5c839a674fcd7a98952e593242ea400abe93992746761e38641405d28b00f419"
SRC_URI[windows-sys-0.42.0.sha256sum] = "5a3e1820f08b8513f676f7ab6c1f99ff312fb97b553d30ff4dd86f9f15728aa7"
SRC_URI[windows-sys-0.45.0.sha256sum] = "75283be5efb2831d37ea142365f009c02ec203cd29a3ebecbc093d52315b66d0"
SRC_URI[windows-targets-0.42.1.sha256sum] = "8e2522491fbfcd58cc84d47aeb2958948c4b8982e9a2d8a2a35bbaed431390e7"
SRC_URI[windows_aarch64_gnullvm-0.42.1.sha256sum] = "8c9864e83243fdec7fc9c5444389dcbbfd258f745e7853198f365e3c4968a608"
SRC_URI[windows_aarch64_msvc-0.42.1.sha256sum] = "4c8b1b673ffc16c47a9ff48570a9d85e25d265735c503681332589af6253c6c7"
SRC_URI[windows_i686_gnu-0.42.1.sha256sum] = "de3887528ad530ba7bdbb1faa8275ec7a1155a45ffa57c37993960277145d640"
SRC_URI[windows_i686_msvc-0.42.1.sha256sum] = "bf4d1122317eddd6ff351aa852118a2418ad4214e6613a50e0191f7004372605"
SRC_URI[windows_x86_64_gnu-0.42.1.sha256sum] = "c1040f221285e17ebccbc2591ffdc2d44ee1f9186324dd3e84e99ac68d699c45"
SRC_URI[windows_x86_64_gnullvm-0.42.1.sha256sum] = "628bfdf232daa22b0d64fdb62b09fcc36bb01f05a3939e20ab73aaf9470d0463"
SRC_URI[windows_x86_64_msvc-0.42.1.sha256sum] = "447660ad36a13288b1db4d4248e857b510e8c3a225c822ba4fb748c0aafecffd"
SRC_URI[winreg-0.10.1.sha256sum] = "80d0f4e272c85def139476380b12f9ac60926689dd2e01d4923222f40580869d"
SRC_URI[yansi-0.5.1.sha256sum] = "09041cd90cf85f7f8b2df60c646f853b7f535ce68f85244eb6731cf89fa498ec"
SRC_URI[yasna-0.5.1.sha256sum] = "aed2e7a52e3744ab4d0c05c20aa065258e84c49fd4226f5191b2ed29712710b4"
SRC_URI[zeroize-1.5.7.sha256sum] = "c394b5bd0c6f669e7275d9c20aa90ae064cb22e75a1cad54e1b34088034b149f"
SRC_URI[zeroize_derive-1.3.3.sha256sum] = "44bf07cb3e50ea2003396695d58bf46bc9887a1f362260446fad6bc4e79bd36c"

SRCREV_FORMAT .= "russh"
SRCREV_russh = "0.37.0-beta.1"
SRCREV_FORMAT .= "tough"
SRCREV_tough = "28c2deb20a654426f09129bcd0938ad92de02f33"

# There is a postfunc that runs after do_configure. This fixing logic needs to run after this postfunc.
# It is because of this ordering this is do_compile:prepend instead of do_configure:append.
cargo_add_rac_patch_paths() {
    # Need to fix config file due to the tough and russh repo having a virtual manifest.
    # Which is not supported by the cargo bbclasses currently,
    # see: https://github.com/openembedded/openembedded-core/commit/684a8af41c5bb70db68e75f72bdc4c9b09630810
    sed -i 's|tough =.*|tough = { path = "${WORKDIR}/tough/tough" }|g' ${CARGO_HOME}/config
    sed -i '/olpc-cjson =.*/d' ${CARGO_HOME}/config
    sed -i '/^tough =.*/a olpc-cjson = { path = "${WORKDIR}/tough/olpc-cjson" }' ${CARGO_HOME}/config

    sed -i 's|russh =.*|russh = { path = "${WORKDIR}/russh/russh" }|g' ${CARGO_HOME}/config
    sed -i '/russh-keys =.*/d' ${CARGO_HOME}/config
    sed -i '/russh-cryptovec =.*/d' ${CARGO_HOME}/config
    sed -i '/^russh =.*/a russh-cryptovec = { path = "${WORKDIR}/russh/cryptovec" }' ${CARGO_HOME}/config
    sed -i '/^russh =.*/a russh-keys = { path = "${WORKDIR}/russh/russh-keys" }' ${CARGO_HOME}/config
}

do_configure[postfuncs] += "cargo_add_rac_patch_paths"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/remote-access.service ${D}${systemd_unitdir}/system/remote-access.service

    install -d ${D}${sysconfdir}/rac
    install -m 0644 ${WORKDIR}/client.toml ${D}${sysconfdir}/rac
}
