#!/bin/sh

# WARNING: This script DOES not yet work with the SL2619 board, it works
# only with the Astra SL1680 and Luna SL1680 boards.
#
usage() {
    echo "Usage: $0 [-h] [-p <usb-port-path>]"
}

PORT=""
while getopts "hp:" opt; do
    case $opt in
        p) PORT="$OPTARG" ;;
        h) usage; exit 0 ;;
        *) usage >&2; exit 1 ;;
    esac
done

echo "Flashing board using astra update..."
ID=$(cat astra-usbboot-images/sl1680_suboot/manifest.yaml | grep -Po "(?<=^id:\s)[^ ]+")

sudo ./bin/linux/x86_64/astra-update -c sl1680 -m 4gb -d lpddr4x -t emmc -i "$ID" ${PORT:+-p "$PORT"}
