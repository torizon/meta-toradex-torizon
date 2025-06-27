require recipes-bsp/u-boot/u-boot-ota.inc
require recipes-bsp/u-boot/u-boot-rollback.inc

# Dummy implementation: prevent conflict with main version file.
deploy_version_file:k3r5 () {
    bbdebug 1 "u-boot-version file generation skipped for 'k3r5'"
}

# Dummy implementation: prevent conflict with main environment file.
deploy_environ_file:k3r5 () {
    bbdebug 1 "u-boot-initial-env.raw file generation skipped for 'k3r5'"
}
