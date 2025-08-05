#!/bin/bash

# Assign name "ethernetN" to net devices with Toradex OUI.
# Supports Toradex boards with single/dual ethernet interfaces.

if [ $# -ne 2 ]; then
    echo "Usage: $0 OUI KERNEL" >&2
    exit 1
fi

oui="${1}"

dev="${2}"
devpath="/sys/class/net/${dev}"
if [ ! -e "${devpath}" ]
then
  echo "Error: dev path does not exist!" >&2
  exit 1
fi

devaddr="${devpath}/address"

# Toradex OUIs
case $oui in
  00:14:2d)
    max_range=16777216 # 16777215 = 0xFFFFFF, maximum valid serial for MAC OUI 1 (00:14:2D)
    mac0_serial=$(( 10#$(tr '\0' '\n' < /proc/device-tree/serial-number) + 0 ))
    ;;
  8c:06:cb)
    max_range=33554432 # 33554431 = 0x1FFFFFF, maximum valid serial for MAC OUI 2 (8C:06:CB)
    # Correct MAC 0 serial, so we can derive the correct MAC address, by removing the second OUI range offset
    mac0_serial=$(( 10#$(tr '\0' '\n' < /proc/device-tree/serial-number) - 16777216 ))
    ;;
esac

# shellcheck disable=SC2126
devs=$(grep "${oui}" /sys/class/net/*/address | wc -l)
if [ "${devs}" -gt 2 ]
then
  # Limitation: this script can handle up to two devices
  echo "Error: Found ${devs} devices with Toradex OUI!" >&2
  echo "Error: Up to 2 devices are supported at the moment" >&2
  exit 1
fi

# Secondary MAC address is allocated from block
# 0x100000(1048576) higher than the first MAC address
mac1_offset=1048576
# shellcheck disable=SC2034
mac1_serial=$(( 10#$mac0_serial + 10#$mac1_offset ))

for i in $(seq 0 $((devs - 1)))
do
  eval serial="\${mac${i}_serial}"

  # shellcheck disable=SC2154
  if [ "${serial}" -gt "${max_range}" ]
  then
    echo "Error: mac${i} serial is greater than 0xFFFFFF!" >&2
    exit 1
  fi
  mac_serial_hex="$(printf "%x" "${serial}" |  sed -e 's/[0-9a-f]\{2\}/&:/g' -e 's/:$//')"
  currentmac="${oui}:${mac_serial_hex}"

  if [ "$(cat "${devaddr}")" = "${currentmac}" ]
  then
    echo "ethernet${i}"
    exit 0
  fi
done

echo "Error: Could not match MAC address!" >&2
exit 1
