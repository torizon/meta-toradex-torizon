SUMMARY = "tzn-mqtt"
HOMEPAGE = "https://github.com/torizon/tzn-mqtt"
LICENSE = "Apache-2.0"

inherit cargo systemd

SRC_URI += " \
    git://github.com/torizon/tzn-mqtt.git;protocol=https;branch=main \
    file://tzn-mqtt.service \
"

SRCREV = "516ce5198a077fffcc2a19d946b3c21814841f01"
SRCREV:use-head-next = "${AUTOREV}"

S = "${WORKDIR}/git"
PV = "0.0+git${SRCPV}"
CARGO_SRC_DIR = ""

LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658 \
"

SYSTEMD_SERVICE:${PN} = "tzn-mqtt.service"
# Keep disabled by default for now
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

SRC_URI += " \
    crate://crates.io/addr2line/0.21.0 \
    crate://crates.io/adler/1.0.2 \
    crate://crates.io/aho-corasick/1.1.3 \
    crate://crates.io/asn1-rs-derive/0.5.0 \
    crate://crates.io/asn1-rs-impl/0.2.0 \
    crate://crates.io/asn1-rs/0.6.1 \
    crate://crates.io/async-broadcast/0.7.1 \
    crate://crates.io/async-channel/2.3.1 \
    crate://crates.io/async-executor/1.12.0 \
    crate://crates.io/async-fs/2.1.2 \
    crate://crates.io/async-io/2.3.3 \
    crate://crates.io/async-lock/3.4.0 \
    crate://crates.io/async-process/2.2.3 \
    crate://crates.io/async-recursion/1.1.1 \
    crate://crates.io/async-signal/0.2.8 \
    crate://crates.io/async-task/4.7.1 \
    crate://crates.io/async-trait/0.1.81 \
    crate://crates.io/atomic-waker/1.1.2 \
    crate://crates.io/autocfg/1.3.0 \
    crate://crates.io/backtrace/0.3.71 \
    crate://crates.io/base64/0.22.1 \
    crate://crates.io/bitflags/2.6.0 \
    crate://crates.io/block-buffer/0.10.4 \
    crate://crates.io/blocking/1.6.1 \
    crate://crates.io/bytes/1.6.0 \
    crate://crates.io/cc/1.1.0 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/cfg_aliases/0.2.1 \
    crate://crates.io/color-eyre/0.6.3 \
    crate://crates.io/color-spantrace/0.2.1 \
    crate://crates.io/concurrent-queue/2.5.0 \
    crate://crates.io/core-foundation-sys/0.8.6 \
    crate://crates.io/core-foundation/0.9.4 \
    crate://crates.io/cpufeatures/0.2.12 \
    crate://crates.io/crossbeam-utils/0.8.20 \
    crate://crates.io/crypto-common/0.1.6 \
    crate://crates.io/data-encoding/2.6.0 \
    crate://crates.io/der-parser/9.0.0 \
    crate://crates.io/deranged/0.3.11 \
    crate://crates.io/digest/0.10.7 \
    crate://crates.io/displaydoc/0.2.5 \
    crate://crates.io/endi/1.1.0 \
    crate://crates.io/enumflags2/0.7.10 \
    crate://crates.io/enumflags2_derive/0.7.10 \
    crate://crates.io/env_logger/0.10.2 \
    crate://crates.io/equivalent/1.0.1 \
    crate://crates.io/errno/0.3.9 \
    crate://crates.io/event-listener-strategy/0.5.2 \
    crate://crates.io/event-listener/5.3.1 \
    crate://crates.io/eyre/0.6.12 \
    crate://crates.io/fastrand/2.1.0 \
    crate://crates.io/flume/0.11.0 \
    crate://crates.io/futures-core/0.3.30 \
    crate://crates.io/futures-io/0.3.30 \
    crate://crates.io/futures-lite/2.3.0 \
    crate://crates.io/futures-macro/0.3.30 \
    crate://crates.io/futures-sink/0.3.30 \
    crate://crates.io/futures-task/0.3.30 \
    crate://crates.io/futures-util/0.3.30 \
    crate://crates.io/generic-array/0.14.7 \
    crate://crates.io/getrandom/0.2.15 \
    crate://crates.io/gimli/0.28.1 \
    crate://crates.io/hashbrown/0.14.5 \
    crate://crates.io/hermit-abi/0.3.9 \
    crate://crates.io/hermit-abi/0.4.0 \
    crate://crates.io/hex/0.4.3 \
    crate://crates.io/humantime/2.1.0 \
    crate://crates.io/indenter/0.3.3 \
    crate://crates.io/indexmap/2.2.6 \
    crate://crates.io/is-terminal/0.4.12 \
    crate://crates.io/itoa/1.0.11 \
    crate://crates.io/lazy_static/1.5.0 \
    crate://crates.io/libc/0.2.155 \
    crate://crates.io/linux-raw-sys/0.4.14 \
    crate://crates.io/lock_api/0.4.12 \
    crate://crates.io/log/0.4.22 \
    crate://crates.io/memchr/2.7.4 \
    crate://crates.io/memoffset/0.9.1 \
    crate://crates.io/minimal-lexical/0.2.1 \
    crate://crates.io/miniz_oxide/0.7.4 \
    crate://crates.io/mio/0.8.11 \
    crate://crates.io/nix/0.29.0 \
    crate://crates.io/nom/7.1.3 \
    crate://crates.io/num-bigint/0.4.6 \
    crate://crates.io/num-conv/0.1.0 \
    crate://crates.io/num-integer/0.1.46 \
    crate://crates.io/num-traits/0.2.19 \
    crate://crates.io/num_cpus/1.16.0 \
    crate://crates.io/object/0.32.2 \
    crate://crates.io/oid-registry/0.7.0 \
    crate://crates.io/once_cell/1.19.0 \
    crate://crates.io/openssl-probe/0.1.5 \
    crate://crates.io/ordered-stream/0.2.0 \
    crate://crates.io/owo-colors/3.5.0 \
    crate://crates.io/parking/2.2.0 \
    crate://crates.io/pin-project-lite/0.2.14 \
    crate://crates.io/pin-utils/0.1.0 \
    crate://crates.io/piper/0.2.3 \
    crate://crates.io/polling/3.7.2 \
    crate://crates.io/powerfmt/0.2.0 \
    crate://crates.io/ppv-lite86/0.2.17 \
    crate://crates.io/pretty_env_logger/0.5.0 \
    crate://crates.io/proc-macro-crate/3.1.0 \
    crate://crates.io/proc-macro2/1.0.86 \
    crate://crates.io/quote/1.0.36 \
    crate://crates.io/rand/0.8.5 \
    crate://crates.io/rand_chacha/0.3.1 \
    crate://crates.io/rand_core/0.6.4 \
    crate://crates.io/regex-automata/0.4.7 \
    crate://crates.io/regex-syntax/0.8.4 \
    crate://crates.io/regex/1.10.5 \
    crate://crates.io/ring/0.17.8 \
    crate://crates.io/rumqttc/0.24.0 \
    crate://crates.io/rustc-demangle/0.1.24 \
    crate://crates.io/rusticata-macros/4.1.0 \
    crate://crates.io/rustix/0.38.34 \
    crate://crates.io/rustls-native-certs/0.7.1 \
    crate://crates.io/rustls-pemfile/2.1.2 \
    crate://crates.io/rustls-pki-types/1.7.0 \
    crate://crates.io/rustls-webpki/0.102.5 \
    crate://crates.io/rustls/0.22.4 \
    crate://crates.io/ryu/1.0.18 \
    crate://crates.io/schannel/0.1.23 \
    crate://crates.io/scopeguard/1.2.0 \
    crate://crates.io/security-framework-sys/2.11.0 \
    crate://crates.io/security-framework/2.11.0 \
    crate://crates.io/serde/1.0.204 \
    crate://crates.io/serde_derive/1.0.204 \
    crate://crates.io/serde_json/1.0.120 \
    crate://crates.io/serde_repr/0.1.19 \
    crate://crates.io/sha1/0.10.6 \
    crate://crates.io/sharded-slab/0.1.7 \
    crate://crates.io/signal-hook-registry/1.4.2 \
    crate://crates.io/slab/0.4.9 \
    crate://crates.io/socket2/0.5.7 \
    crate://crates.io/spin/0.9.8 \
    crate://crates.io/static_assertions/1.1.0 \
    crate://crates.io/subtle/2.6.1 \
    crate://crates.io/syn/2.0.70 \
    crate://crates.io/synstructure/0.13.1 \
    crate://crates.io/tempfile/3.10.1 \
    crate://crates.io/termcolor/1.4.1 \
    crate://crates.io/thiserror-impl/1.0.61 \
    crate://crates.io/thiserror/1.0.61 \
    crate://crates.io/thread_local/1.1.8 \
    crate://crates.io/time-core/0.1.2 \
    crate://crates.io/time-macros/0.2.18 \
    crate://crates.io/time/0.3.36 \
    crate://crates.io/tokio-macros/2.3.0 \
    crate://crates.io/tokio-rustls/0.25.0 \
    crate://crates.io/tokio/1.38.0 \
    crate://crates.io/toml_datetime/0.6.6 \
    crate://crates.io/toml_edit/0.21.1 \
    crate://crates.io/tracing-attributes/0.1.27 \
    crate://crates.io/tracing-core/0.1.32 \
    crate://crates.io/tracing-error/0.2.0 \
    crate://crates.io/tracing-subscriber/0.3.18 \
    crate://crates.io/tracing/0.1.40 \
    crate://crates.io/typenum/1.17.0 \
    crate://crates.io/uds_windows/1.1.0 \
    crate://crates.io/unicode-ident/1.0.12 \
    crate://crates.io/untrusted/0.9.0 \
    crate://crates.io/valuable/0.1.0 \
    crate://crates.io/version_check/0.9.4 \
    crate://crates.io/wasi/0.11.0+wasi-snapshot-preview1 \
    crate://crates.io/webpki-roots/0.26.3 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-util/0.1.8 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi/0.3.9 \
    crate://crates.io/windows-sys/0.48.0 \
    crate://crates.io/windows-sys/0.52.0 \
    crate://crates.io/windows-targets/0.48.5 \
    crate://crates.io/windows-targets/0.52.6 \
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
    crate://crates.io/x509-parser/0.16.0 \
    crate://crates.io/xdg-home/1.2.0 \
    crate://crates.io/zbus/4.3.1 \
    crate://crates.io/zbus_macros/4.3.1 \
    crate://crates.io/zbus_names/3.0.0 \
    crate://crates.io/zeroize/1.8.1 \
    crate://crates.io/zvariant/4.1.2 \
    crate://crates.io/zvariant_derive/4.1.2 \
    crate://crates.io/zvariant_utils/2.0.0 \
"

SRC_URI[addr2line-0.21.0.sha256sum] = "8a30b2e23b9e17a9f90641c7ab1549cd9b44f296d3ccbf309d2863cfe398a0cb"
SRC_URI[adler-1.0.2.sha256sum] = "f26201604c87b1e01bd3d98f8d5d9a8fcbb815e8cedb41ffccbeb4bf593a35fe"
SRC_URI[aho-corasick-1.1.3.sha256sum] = "8e60d3430d3a69478ad0993f19238d2df97c507009a52b3c10addcd7f6bcb916"
SRC_URI[asn1-rs-derive-0.5.0.sha256sum] = "7378575ff571966e99a744addeff0bff98b8ada0dedf1956d59e634db95eaac1"
SRC_URI[asn1-rs-impl-0.2.0.sha256sum] = "7b18050c2cd6fe86c3a76584ef5e0baf286d038cda203eb6223df2cc413565f7"
SRC_URI[asn1-rs-0.6.1.sha256sum] = "22ad1373757efa0f70ec53939aabc7152e1591cb485208052993070ac8d2429d"
SRC_URI[async-broadcast-0.7.1.sha256sum] = "20cd0e2e25ea8e5f7e9df04578dc6cf5c83577fd09b1a46aaf5c85e1c33f2a7e"
SRC_URI[async-channel-2.3.1.sha256sum] = "89b47800b0be77592da0afd425cc03468052844aff33b84e33cc696f64e77b6a"
SRC_URI[async-executor-1.12.0.sha256sum] = "c8828ec6e544c02b0d6691d21ed9f9218d0384a82542855073c2a3f58304aaf0"
SRC_URI[async-fs-2.1.2.sha256sum] = "ebcd09b382f40fcd159c2d695175b2ae620ffa5f3bd6f664131efff4e8b9e04a"
SRC_URI[async-io-2.3.3.sha256sum] = "0d6baa8f0178795da0e71bc42c9e5d13261aac7ee549853162e66a241ba17964"
SRC_URI[async-lock-3.4.0.sha256sum] = "ff6e472cdea888a4bd64f342f09b3f50e1886d32afe8df3d663c01140b811b18"
SRC_URI[async-process-2.2.3.sha256sum] = "f7eda79bbd84e29c2b308d1dc099d7de8dcc7035e48f4bf5dc4a531a44ff5e2a"
SRC_URI[async-recursion-1.1.1.sha256sum] = "3b43422f69d8ff38f95f1b2bb76517c91589a924d1559a0e935d7c8ce0274c11"
SRC_URI[async-signal-0.2.8.sha256sum] = "794f185324c2f00e771cd9f1ae8b5ac68be2ca7abb129a87afd6e86d228bc54d"
SRC_URI[async-task-4.7.1.sha256sum] = "8b75356056920673b02621b35afd0f7dda9306d03c79a30f5c56c44cf256e3de"
SRC_URI[async-trait-0.1.81.sha256sum] = "6e0c28dcc82d7c8ead5cb13beb15405b57b8546e93215673ff8ca0349a028107"
SRC_URI[atomic-waker-1.1.2.sha256sum] = "1505bd5d3d116872e7271a6d4e16d81d0c8570876c8de68093a09ac269d8aac0"
SRC_URI[autocfg-1.3.0.sha256sum] = "0c4b4d0bd25bd0b74681c0ad21497610ce1b7c91b1022cd21c80c6fbdd9476b0"
SRC_URI[backtrace-0.3.71.sha256sum] = "26b05800d2e817c8b3b4b54abd461726265fa9789ae34330622f2db9ee696f9d"
SRC_URI[base64-0.22.1.sha256sum] = "72b3254f16251a8381aa12e40e3c4d2f0199f8c6508fbecb9d91f575e0fbb8c6"
SRC_URI[bitflags-2.6.0.sha256sum] = "b048fb63fd8b5923fc5aa7b340d8e156aec7ec02f0c78fa8a6ddc2613f6f71de"
SRC_URI[block-buffer-0.10.4.sha256sum] = "3078c7629b62d3f0439517fa394996acacc5cbc91c5a20d8c658e77abd503a71"
SRC_URI[blocking-1.6.1.sha256sum] = "703f41c54fc768e63e091340b424302bb1c29ef4aa0c7f10fe849dfb114d29ea"
SRC_URI[bytes-1.6.0.sha256sum] = "514de17de45fdb8dc022b1a7975556c53c86f9f0aa5f534b98977b171857c2c9"
SRC_URI[cc-1.1.0.sha256sum] = "eaff6f8ce506b9773fa786672d63fc7a191ffea1be33f72bbd4aeacefca9ffc8"
SRC_URI[cfg-if-1.0.0.sha256sum] = "baf1de4339761588bc0619e3cbc0120ee582ebb74b53b4efbf79117bd2da40fd"
SRC_URI[cfg_aliases-0.2.1.sha256sum] = "613afe47fcd5fac7ccf1db93babcb082c5994d996f20b8b159f2ad1658eb5724"
SRC_URI[color-eyre-0.6.3.sha256sum] = "55146f5e46f237f7423d74111267d4597b59b0dad0ffaf7303bce9945d843ad5"
SRC_URI[color-spantrace-0.2.1.sha256sum] = "cd6be1b2a7e382e2b98b43b2adcca6bb0e465af0bdd38123873ae61eb17a72c2"
SRC_URI[concurrent-queue-2.5.0.sha256sum] = "4ca0197aee26d1ae37445ee532fefce43251d24cc7c166799f4d46817f1d3973"
SRC_URI[core-foundation-sys-0.8.6.sha256sum] = "06ea2b9bc92be3c2baa9334a323ebca2d6f074ff852cd1d7b11064035cd3868f"
SRC_URI[core-foundation-0.9.4.sha256sum] = "91e195e091a93c46f7102ec7818a2aa394e1e1771c3ab4825963fa03e45afb8f"
SRC_URI[cpufeatures-0.2.12.sha256sum] = "53fe5e26ff1b7aef8bca9c6080520cfb8d9333c7568e1829cef191a9723e5504"
SRC_URI[crossbeam-utils-0.8.20.sha256sum] = "22ec99545bb0ed0ea7bb9b8e1e9122ea386ff8a48c0922e43f36d45ab09e0e80"
SRC_URI[crypto-common-0.1.6.sha256sum] = "1bfb12502f3fc46cca1bb51ac28df9d618d813cdc3d2f25b9fe775a34af26bb3"
SRC_URI[data-encoding-2.6.0.sha256sum] = "e8566979429cf69b49a5c740c60791108e86440e8be149bbea4fe54d2c32d6e2"
SRC_URI[der-parser-9.0.0.sha256sum] = "5cd0a5c643689626bec213c4d8bd4d96acc8ffdb4ad4bb6bc16abf27d5f4b553"
SRC_URI[deranged-0.3.11.sha256sum] = "b42b6fa04a440b495c8b04d0e71b707c585f83cb9cb28cf8cd0d976c315e31b4"
SRC_URI[digest-0.10.7.sha256sum] = "9ed9a281f7bc9b7576e61468ba615a66a5c8cfdff42420a70aa82701a3b1e292"
SRC_URI[displaydoc-0.2.5.sha256sum] = "97369cbbc041bc366949bc74d34658d6cda5621039731c6310521892a3a20ae0"
SRC_URI[endi-1.1.0.sha256sum] = "a3d8a32ae18130a3c84dd492d4215c3d913c3b07c6b63c2eb3eb7ff1101ab7bf"
SRC_URI[enumflags2-0.7.10.sha256sum] = "d232db7f5956f3f14313dc2f87985c58bd2c695ce124c8cdd984e08e15ac133d"
SRC_URI[enumflags2_derive-0.7.10.sha256sum] = "de0d48a183585823424a4ce1aa132d174a6a81bd540895822eb4c8373a8e49e8"
SRC_URI[env_logger-0.10.2.sha256sum] = "4cd405aab171cb85d6735e5c8d9db038c17d3ca007a4d2c25f337935c3d90580"
SRC_URI[equivalent-1.0.1.sha256sum] = "5443807d6dff69373d433ab9ef5378ad8df50ca6298caf15de6e52e24aaf54d5"
SRC_URI[errno-0.3.9.sha256sum] = "534c5cf6194dfab3db3242765c03bbe257cf92f22b38f6bc0c58d59108a820ba"
SRC_URI[event-listener-strategy-0.5.2.sha256sum] = "0f214dc438f977e6d4e3500aaa277f5ad94ca83fbbd9b1a15713ce2344ccc5a1"
SRC_URI[event-listener-5.3.1.sha256sum] = "6032be9bd27023a771701cc49f9f053c751055f71efb2e0ae5c15809093675ba"
SRC_URI[eyre-0.6.12.sha256sum] = "7cd915d99f24784cdc19fd37ef22b97e3ff0ae756c7e492e9fbfe897d61e2aec"
SRC_URI[fastrand-2.1.0.sha256sum] = "9fc0510504f03c51ada170672ac806f1f105a88aa97a5281117e1ddc3368e51a"
SRC_URI[flume-0.11.0.sha256sum] = "55ac459de2512911e4b674ce33cf20befaba382d05b62b008afc1c8b57cbf181"
SRC_URI[futures-core-0.3.30.sha256sum] = "dfc6580bb841c5a68e9ef15c77ccc837b40a7504914d52e47b8b0e9bbda25a1d"
SRC_URI[futures-io-0.3.30.sha256sum] = "a44623e20b9681a318efdd71c299b6b222ed6f231972bfe2f224ebad6311f0c1"
SRC_URI[futures-lite-2.3.0.sha256sum] = "52527eb5074e35e9339c6b4e8d12600c7128b68fb25dcb9fa9dec18f7c25f3a5"
SRC_URI[futures-macro-0.3.30.sha256sum] = "87750cf4b7a4c0625b1529e4c543c2182106e4dedc60a2a6455e00d212c489ac"
SRC_URI[futures-sink-0.3.30.sha256sum] = "9fb8e00e87438d937621c1c6269e53f536c14d3fbd6a042bb24879e57d474fb5"
SRC_URI[futures-task-0.3.30.sha256sum] = "38d84fa142264698cdce1a9f9172cf383a0c82de1bddcf3092901442c4097004"
SRC_URI[futures-util-0.3.30.sha256sum] = "3d6401deb83407ab3da39eba7e33987a73c3df0c82b4bb5813ee871c19c41d48"
SRC_URI[generic-array-0.14.7.sha256sum] = "85649ca51fd72272d7821adaf274ad91c288277713d9c18820d8499a7ff69e9a"
SRC_URI[getrandom-0.2.15.sha256sum] = "c4567c8db10ae91089c99af84c68c38da3ec2f087c3f82960bcdbf3656b6f4d7"
SRC_URI[gimli-0.28.1.sha256sum] = "4271d37baee1b8c7e4b708028c57d816cf9d2434acb33a549475f78c181f6253"
SRC_URI[hashbrown-0.14.5.sha256sum] = "e5274423e17b7c9fc20b6e7e208532f9b19825d82dfd615708b70edd83df41f1"
SRC_URI[hermit-abi-0.3.9.sha256sum] = "d231dfb89cfffdbc30e7fc41579ed6066ad03abda9e567ccafae602b97ec5024"
SRC_URI[hermit-abi-0.4.0.sha256sum] = "fbf6a919d6cf397374f7dfeeea91d974c7c0a7221d0d0f4f20d859d329e53fcc"
SRC_URI[hex-0.4.3.sha256sum] = "7f24254aa9a54b5c858eaee2f5bccdb46aaf0e486a595ed5fd8f86ba55232a70"
SRC_URI[humantime-2.1.0.sha256sum] = "9a3a5bfb195931eeb336b2a7b4d761daec841b97f947d34394601737a7bba5e4"
SRC_URI[indenter-0.3.3.sha256sum] = "ce23b50ad8242c51a442f3ff322d56b02f08852c77e4c0b4d3fd684abc89c683"
SRC_URI[indexmap-2.2.6.sha256sum] = "168fb715dda47215e360912c096649d23d58bf392ac62f73919e831745e40f26"
SRC_URI[is-terminal-0.4.12.sha256sum] = "f23ff5ef2b80d608d61efee834934d862cd92461afc0560dedf493e4c033738b"
SRC_URI[itoa-1.0.11.sha256sum] = "49f1f14873335454500d59611f1cf4a4b0f786f9ac11f4312a78e4cf2566695b"
SRC_URI[lazy_static-1.5.0.sha256sum] = "bbd2bcb4c963f2ddae06a2efc7e9f3591312473c50c6685e1f298068316e66fe"
SRC_URI[libc-0.2.155.sha256sum] = "97b3888a4aecf77e811145cadf6eef5901f4782c53886191b2f693f24761847c"
SRC_URI[linux-raw-sys-0.4.14.sha256sum] = "78b3ae25bc7c8c38cec158d1f2757ee79e9b3740fbc7ccf0e59e4b08d793fa89"
SRC_URI[lock_api-0.4.12.sha256sum] = "07af8b9cdd281b7915f413fa73f29ebd5d55d0d3f0155584dade1ff18cea1b17"
SRC_URI[log-0.4.22.sha256sum] = "a7a70ba024b9dc04c27ea2f0c0548feb474ec5c54bba33a7f72f873a39d07b24"
SRC_URI[memchr-2.7.4.sha256sum] = "78ca9ab1a0babb1e7d5695e3530886289c18cf2f87ec19a575a0abdce112e3a3"
SRC_URI[memoffset-0.9.1.sha256sum] = "488016bfae457b036d996092f6cb448677611ce4449e970ceaf42695203f218a"
SRC_URI[minimal-lexical-0.2.1.sha256sum] = "68354c5c6bd36d73ff3feceb05efa59b6acb7626617f4962be322a825e61f79a"
SRC_URI[miniz_oxide-0.7.4.sha256sum] = "b8a240ddb74feaf34a79a7add65a741f3167852fba007066dcac1ca548d89c08"
SRC_URI[mio-0.8.11.sha256sum] = "a4a650543ca06a924e8b371db273b2756685faae30f8487da1b56505a8f78b0c"
SRC_URI[nix-0.29.0.sha256sum] = "71e2746dc3a24dd78b3cfcb7be93368c6de9963d30f43a6a73998a9cf4b17b46"
SRC_URI[nom-7.1.3.sha256sum] = "d273983c5a657a70a3e8f2a01329822f3b8c8172b73826411a55751e404a0a4a"
SRC_URI[num-bigint-0.4.6.sha256sum] = "a5e44f723f1133c9deac646763579fdb3ac745e418f2a7af9cd0c431da1f20b9"
SRC_URI[num-conv-0.1.0.sha256sum] = "51d515d32fb182ee37cda2ccdcb92950d6a3c2893aa280e540671c2cd0f3b1d9"
SRC_URI[num-integer-0.1.46.sha256sum] = "7969661fd2958a5cb096e56c8e1ad0444ac2bbcd0061bd28660485a44879858f"
SRC_URI[num-traits-0.2.19.sha256sum] = "071dfc062690e90b734c0b2273ce72ad0ffa95f0c74596bc250dcfd960262841"
SRC_URI[num_cpus-1.16.0.sha256sum] = "4161fcb6d602d4d2081af7c3a45852d875a03dd337a6bfdd6e06407b61342a43"
SRC_URI[object-0.32.2.sha256sum] = "a6a622008b6e321afc04970976f62ee297fdbaa6f95318ca343e3eebb9648441"
SRC_URI[oid-registry-0.7.0.sha256sum] = "1c958dd45046245b9c3c2547369bb634eb461670b2e7e0de552905801a648d1d"
SRC_URI[once_cell-1.19.0.sha256sum] = "3fdb12b2476b595f9358c5161aa467c2438859caa136dec86c26fdd2efe17b92"
SRC_URI[openssl-probe-0.1.5.sha256sum] = "ff011a302c396a5197692431fc1948019154afc178baf7d8e37367442a4601cf"
SRC_URI[ordered-stream-0.2.0.sha256sum] = "9aa2b01e1d916879f73a53d01d1d6cee68adbb31d6d9177a8cfce093cced1d50"
SRC_URI[owo-colors-3.5.0.sha256sum] = "c1b04fb49957986fdce4d6ee7a65027d55d4b6d2265e5848bbb507b58ccfdb6f"
SRC_URI[parking-2.2.0.sha256sum] = "bb813b8af86854136c6922af0598d719255ecb2179515e6e7730d468f05c9cae"
SRC_URI[pin-project-lite-0.2.14.sha256sum] = "bda66fc9667c18cb2758a2ac84d1167245054bcf85d5d1aaa6923f45801bdd02"
SRC_URI[pin-utils-0.1.0.sha256sum] = "8b870d8c151b6f2fb93e84a13146138f05d02ed11c7e7c54f8826aaaf7c9f184"
SRC_URI[piper-0.2.3.sha256sum] = "ae1d5c74c9876f070d3e8fd503d748c7d974c3e48da8f41350fa5222ef9b4391"
SRC_URI[polling-3.7.2.sha256sum] = "a3ed00ed3fbf728b5816498ecd316d1716eecaced9c0c8d2c5a6740ca214985b"
SRC_URI[powerfmt-0.2.0.sha256sum] = "439ee305def115ba05938db6eb1644ff94165c5ab5e9420d1c1bcedbba909391"
SRC_URI[ppv-lite86-0.2.17.sha256sum] = "5b40af805b3121feab8a3c29f04d8ad262fa8e0561883e7653e024ae4479e6de"
SRC_URI[pretty_env_logger-0.5.0.sha256sum] = "865724d4dbe39d9f3dd3b52b88d859d66bcb2d6a0acfd5ea68a65fb66d4bdc1c"
SRC_URI[proc-macro-crate-3.1.0.sha256sum] = "6d37c51ca738a55da99dc0c4a34860fd675453b8b36209178c2249bb13651284"
SRC_URI[proc-macro2-1.0.86.sha256sum] = "5e719e8df665df0d1c8fbfd238015744736151d4445ec0836b8e628aae103b77"
SRC_URI[quote-1.0.36.sha256sum] = "0fa76aaf39101c457836aec0ce2316dbdc3ab723cdda1c6bd4e6ad4208acaca7"
SRC_URI[rand-0.8.5.sha256sum] = "34af8d1a0e25924bc5b7c43c079c942339d8f0a8b57c39049bef581b46327404"
SRC_URI[rand_chacha-0.3.1.sha256sum] = "e6c10a63a0fa32252be49d21e7709d4d4baf8d231c2dbce1eaa8141b9b127d88"
SRC_URI[rand_core-0.6.4.sha256sum] = "ec0be4795e2f6a28069bec0b5ff3e2ac9bafc99e6a9a7dc3547996c5c816922c"
SRC_URI[regex-automata-0.4.7.sha256sum] = "38caf58cc5ef2fed281f89292ef23f6365465ed9a41b7a7754eb4e26496c92df"
SRC_URI[regex-syntax-0.8.4.sha256sum] = "7a66a03ae7c801facd77a29370b4faec201768915ac14a721ba36f20bc9c209b"
SRC_URI[regex-1.10.5.sha256sum] = "b91213439dad192326a0d7c6ee3955910425f441d7038e0d6933b0aec5c4517f"
SRC_URI[ring-0.17.8.sha256sum] = "c17fa4cb658e3583423e915b9f3acc01cceaee1860e33d59ebae66adc3a2dc0d"
SRC_URI[rumqttc-0.24.0.sha256sum] = "e1568e15fab2d546f940ed3a21f48bbbd1c494c90c99c4481339364a497f94a9"
SRC_URI[rustc-demangle-0.1.24.sha256sum] = "719b953e2095829ee67db738b3bfa9fa368c94900df327b3f07fe6e794d2fe1f"
SRC_URI[rusticata-macros-4.1.0.sha256sum] = "faf0c4a6ece9950b9abdb62b1cfcf2a68b3b67a10ba445b3bb85be2a293d0632"
SRC_URI[rustix-0.38.34.sha256sum] = "70dc5ec042f7a43c4a73241207cecc9873a06d45debb38b329f8541d85c2730f"
SRC_URI[rustls-native-certs-0.7.1.sha256sum] = "a88d6d420651b496bdd98684116959239430022a115c1240e6c3993be0b15fba"
SRC_URI[rustls-pemfile-2.1.2.sha256sum] = "29993a25686778eb88d4189742cd713c9bce943bc54251a33509dc63cbacf73d"
SRC_URI[rustls-pki-types-1.7.0.sha256sum] = "976295e77ce332211c0d24d92c0e83e50f5c5f046d11082cea19f3df13a3562d"
SRC_URI[rustls-webpki-0.102.5.sha256sum] = "f9a6fccd794a42c2c105b513a2f62bc3fd8f3ba57a4593677ceb0bd035164d78"
SRC_URI[rustls-0.22.4.sha256sum] = "bf4ef73721ac7bcd79b2b315da7779d8fc09718c6b3d2d1b2d94850eb8c18432"
SRC_URI[ryu-1.0.18.sha256sum] = "f3cb5ba0dc43242ce17de99c180e96db90b235b8a9fdc9543c96d2209116bd9f"
SRC_URI[schannel-0.1.23.sha256sum] = "fbc91545643bcf3a0bbb6569265615222618bdf33ce4ffbbd13c4bbd4c093534"
SRC_URI[scopeguard-1.2.0.sha256sum] = "94143f37725109f92c262ed2cf5e59bce7498c01bcc1502d7b9afe439a4e9f49"
SRC_URI[security-framework-sys-2.11.0.sha256sum] = "317936bbbd05227752583946b9e66d7ce3b489f84e11a94a510b4437fef407d7"
SRC_URI[security-framework-2.11.0.sha256sum] = "c627723fd09706bacdb5cf41499e95098555af3c3c29d014dc3c458ef6be11c0"
SRC_URI[serde-1.0.204.sha256sum] = "bc76f558e0cbb2a839d37354c575f1dc3fdc6546b5be373ba43d95f231bf7c12"
SRC_URI[serde_derive-1.0.204.sha256sum] = "e0cd7e117be63d3c3678776753929474f3b04a43a080c744d6b0ae2a8c28e222"
SRC_URI[serde_json-1.0.120.sha256sum] = "4e0d21c9a8cae1235ad58a00c11cb40d4b1e5c784f1ef2c537876ed6ffd8b7c5"
SRC_URI[serde_repr-0.1.19.sha256sum] = "6c64451ba24fc7a6a2d60fc75dd9c83c90903b19028d4eff35e88fc1e86564e9"
SRC_URI[sha1-0.10.6.sha256sum] = "e3bf829a2d51ab4a5ddf1352d8470c140cadc8301b2ae1789db023f01cedd6ba"
SRC_URI[sharded-slab-0.1.7.sha256sum] = "f40ca3c46823713e0d4209592e8d6e826aa57e928f09752619fc696c499637f6"
SRC_URI[signal-hook-registry-1.4.2.sha256sum] = "a9e9e0b4211b72e7b8b6e85c807d36c212bdb33ea8587f7569562a84df5465b1"
SRC_URI[slab-0.4.9.sha256sum] = "8f92a496fb766b417c996b9c5e57daf2f7ad3b0bebe1ccfca4856390e3d3bb67"
SRC_URI[socket2-0.5.7.sha256sum] = "ce305eb0b4296696835b71df73eb912e0f1ffd2556a501fcede6e0c50349191c"
SRC_URI[spin-0.9.8.sha256sum] = "6980e8d7511241f8acf4aebddbb1ff938df5eebe98691418c4468d0b72a96a67"
SRC_URI[static_assertions-1.1.0.sha256sum] = "a2eb9349b6444b326872e140eb1cf5e7c522154d69e7a0ffb0fb81c06b37543f"
SRC_URI[subtle-2.6.1.sha256sum] = "13c2bddecc57b384dee18652358fb23172facb8a2c51ccc10d74c157bdea3292"
SRC_URI[syn-2.0.70.sha256sum] = "2f0209b68b3613b093e0ec905354eccaedcfe83b8cb37cbdeae64026c3064c16"
SRC_URI[synstructure-0.13.1.sha256sum] = "c8af7666ab7b6390ab78131fb5b0fce11d6b7a6951602017c35fa82800708971"
SRC_URI[tempfile-3.10.1.sha256sum] = "85b77fafb263dd9d05cbeac119526425676db3784113aa9295c88498cbf8bff1"
SRC_URI[termcolor-1.4.1.sha256sum] = "06794f8f6c5c898b3275aebefa6b8a1cb24cd2c6c79397ab15774837a0bc5755"
SRC_URI[thiserror-impl-1.0.61.sha256sum] = "46c3384250002a6d5af4d114f2845d37b57521033f30d5c3f46c4d70e1197533"
SRC_URI[thiserror-1.0.61.sha256sum] = "c546c80d6be4bc6a00c0f01730c08df82eaa7a7a61f11d656526506112cc1709"
SRC_URI[thread_local-1.1.8.sha256sum] = "8b9ef9bad013ada3808854ceac7b46812a6465ba368859a37e2100283d2d719c"
SRC_URI[time-core-0.1.2.sha256sum] = "ef927ca75afb808a4d64dd374f00a2adf8d0fcff8e7b184af886c3c87ec4a3f3"
SRC_URI[time-macros-0.2.18.sha256sum] = "3f252a68540fde3a3877aeea552b832b40ab9a69e318efd078774a01ddee1ccf"
SRC_URI[time-0.3.36.sha256sum] = "5dfd88e563464686c916c7e46e623e520ddc6d79fa6641390f2e3fa86e83e885"
SRC_URI[tokio-macros-2.3.0.sha256sum] = "5f5ae998a069d4b5aba8ee9dad856af7d520c3699e6159b185c2acd48155d39a"
SRC_URI[tokio-rustls-0.25.0.sha256sum] = "775e0c0f0adb3a2f22a00c4745d728b479985fc15ee7ca6a2608388c5569860f"
SRC_URI[tokio-1.38.0.sha256sum] = "ba4f4a02a7a80d6f274636f0aa95c7e383b912d41fe721a31f29e29698585a4a"
SRC_URI[toml_datetime-0.6.6.sha256sum] = "4badfd56924ae69bcc9039335b2e017639ce3f9b001c393c1b2d1ef846ce2cbf"
SRC_URI[toml_edit-0.21.1.sha256sum] = "6a8534fd7f78b5405e860340ad6575217ce99f38d4d5c8f2442cb5ecb50090e1"
SRC_URI[tracing-attributes-0.1.27.sha256sum] = "34704c8d6ebcbc939824180af020566b01a7c01f80641264eba0999f6c2b6be7"
SRC_URI[tracing-core-0.1.32.sha256sum] = "c06d3da6113f116aaee68e4d601191614c9053067f9ab7f6edbcb161237daa54"
SRC_URI[tracing-error-0.2.0.sha256sum] = "d686ec1c0f384b1277f097b2f279a2ecc11afe8c133c1aabf036a27cb4cd206e"
SRC_URI[tracing-subscriber-0.3.18.sha256sum] = "ad0f048c97dbd9faa9b7df56362b8ebcaa52adb06b498c050d2f4e32f90a7a8b"
SRC_URI[tracing-0.1.40.sha256sum] = "c3523ab5a71916ccf420eebdf5521fcef02141234bbc0b8a49f2fdc4544364ef"
SRC_URI[typenum-1.17.0.sha256sum] = "42ff0bf0c66b8238c6f3b578df37d0b7848e55df8577b3f74f92a69acceeb825"
SRC_URI[uds_windows-1.1.0.sha256sum] = "89daebc3e6fd160ac4aa9fc8b3bf71e1f74fbf92367ae71fb83a037e8bf164b9"
SRC_URI[unicode-ident-1.0.12.sha256sum] = "3354b9ac3fae1ff6755cb6db53683adb661634f67557942dea4facebec0fee4b"
SRC_URI[untrusted-0.9.0.sha256sum] = "8ecb6da28b8a351d773b68d5825ac39017e680750f980f3a1a85cd8dd28a47c1"
SRC_URI[valuable-0.1.0.sha256sum] = "830b7e5d4d90034032940e4ace0d9a9a057e7a45cd94e6c007832e39edb82f6d"
SRC_URI[version_check-0.9.4.sha256sum] = "49874b5167b65d7193b8aba1567f5c7d93d001cafc34600cee003eda787e483f"
SRC_URI[wasi-0.11.0+wasi-snapshot-preview1.sha256sum] = "9c8d87e72b64a3b4db28d11ce29237c246188f4f51057d65a7eab63b7987e423"
SRC_URI[webpki-roots-0.26.3.sha256sum] = "bd7c23921eeb1713a4e851530e9b9756e4fb0e89978582942612524cf09f01cd"
SRC_URI[winapi-i686-pc-windows-gnu-0.4.0.sha256sum] = "ac3b87c63620426dd9b991e5ce0329eff545bccbbb34f3be09ff6fb6ab51b7b6"
SRC_URI[winapi-util-0.1.8.sha256sum] = "4d4cc384e1e73b93bafa6fb4f1df8c41695c8a91cf9c4c64358067d15a7b6c6b"
SRC_URI[winapi-x86_64-pc-windows-gnu-0.4.0.sha256sum] = "712e227841d057c1ee1cd2fb22fa7e5a5461ae8e48fa2ca79ec42cfc1931183f"
SRC_URI[winapi-0.3.9.sha256sum] = "5c839a674fcd7a98952e593242ea400abe93992746761e38641405d28b00f419"
SRC_URI[windows-sys-0.48.0.sha256sum] = "677d2418bec65e3338edb076e806bc1ec15693c5d0104683f2efe857f61056a9"
SRC_URI[windows-sys-0.52.0.sha256sum] = "282be5f36a8ce781fad8c8ae18fa3f9beff57ec1b52cb3de0789201425d9a33d"
SRC_URI[windows-targets-0.48.5.sha256sum] = "9a2fa6e2155d7247be68c096456083145c183cbbbc2764150dda45a87197940c"
SRC_URI[windows-targets-0.52.6.sha256sum] = "9b724f72796e036ab90c1021d4780d4d3d648aca59e491e6b98e725b84e99973"
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
SRC_URI[x509-parser-0.16.0.sha256sum] = "fcbc162f30700d6f3f82a24bf7cc62ffe7caea42c0b2cba8bf7f3ae50cf51f69"
SRC_URI[xdg-home-1.2.0.sha256sum] = "ca91dcf8f93db085f3a0a29358cd0b9d670915468f4290e8b85d118a34211ab8"
SRC_URI[zbus-4.3.1.sha256sum] = "851238c133804e0aa888edf4a0229481c753544ca12a60fd1c3230c8a500fe40"
SRC_URI[zbus_macros-4.3.1.sha256sum] = "8d5a3f12c20bd473be3194af6b49d50d7bb804ef3192dc70eddedb26b85d9da7"
SRC_URI[zbus_names-3.0.0.sha256sum] = "4b9b1fef7d021261cc16cba64c351d291b715febe0fa10dc3a443ac5a5022e6c"
SRC_URI[zeroize-1.8.1.sha256sum] = "ced3678a2879b30306d323f4542626697a464a97c0a07c9aebf7ebca65cd4dde"
SRC_URI[zvariant-4.1.2.sha256sum] = "1724a2b330760dc7d2a8402d841119dc869ef120b139d29862d6980e9c75bfc9"
SRC_URI[zvariant_derive-4.1.2.sha256sum] = "55025a7a518ad14518fb243559c058a2e5b848b015e31f1d90414f36e3317859"
SRC_URI[zvariant_utils-2.0.0.sha256sum] = "fc242db087efc22bd9ade7aa7809e4ba828132edc312871584a6b4391bdf8786"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/tzn-mqtt.service ${D}${systemd_unitdir}/system/tzn-mqtt.service
}
