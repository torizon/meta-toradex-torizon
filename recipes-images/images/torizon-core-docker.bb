SUMMARY = "TorizonCore"
DESCRIPTION = "TorizonCore Linux with no containers pre-provisioned."

require torizon-core-common.inc
require torizon-core-container.inc

VIRTUAL-RUNTIME_container_engine = "docker-ce"
IMAGE_VARIANT = "Docker"

inherit extrausers

EXTRA_USERS_PARAMS += "\
usermod -a -G docker torizon; \
"
