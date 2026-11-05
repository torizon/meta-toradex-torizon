# developer.arm.com/GetEula was retired (HTTP 404). Fetch the same EULA
# (identical md5) from the Synaptics SDK release, as upstream meta-synaptics
# does since scarthgap_6.12_v2.5.0. Drop this once meta-synaptics is bumped.

SRC_URI:remove = "https://developer.arm.com/GetEula?Id=c2de6e61-beb6-42ff-8b05-c21abdbe74f7;downloadfilename=EULA;name=eula"
SRC_URI:append = " https://github.com/synaptics-astra/sdk/releases/download/scarthgap_6.12_v2.4.0/EULA;downloadfilename=EULA;name=eula"
