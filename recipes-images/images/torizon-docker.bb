SUMMARY = "Torizon OS"
DESCRIPTION = "Torizon OS Linux with no containers pre-provisioned."

require torizon-base.inc
require torizon-container.inc

VIRTUAL-RUNTIME_container_engine = "docker"
IMAGE_VARIANT = "Docker"

inherit extrausers

EXTRA_USERS_PARAMS += "\
usermod -a -G docker torizon; \
"
