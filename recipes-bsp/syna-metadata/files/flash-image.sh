#!/bin/sh

# WARNING: This script DOES not yet work with the Winglet/SL2619 boards, it works
# only with the Astra SL1680 board.
#
echo "Flashing Astra SL1680 board using astra update..."
ID=$(cat astra-usbboot-images/sl1680_suboot/manifest.yaml | grep -Po "(?<=^id:\s)[^ ]+")

sudo ./bin/linux/x86_64/astra-update -c sl1680 -m 4gb -d lpddr4x -t emmc -i $ID
