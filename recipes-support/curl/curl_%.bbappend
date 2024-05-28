EXTRA_OECONF:append:sota = " \
    --with-ca-path=${sysconfdir}/ssl/certs \
"

PACKAGECONFIG:append:class-native = " basic-auth"
PACKAGECONFIG:append:class-nativesdk = " basic-auth"
