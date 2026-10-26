#!/bin/bash
# Action handler for a secondary this image registers but cannot update. It leaves
# get-firmware-info to aktualizr and refuses every update, without touching the device.

case "$1" in
    install|complete-install|download-install)
        printf '{"status": "failed", "message": "%s: update not supported by this image"}\n' \
               "${SECONDARY_HARDWARE_ID:-secondary}"
        exit 0
        ;;
    *)
        # Normal processing: with no firmware file, aktualizr reports "noimage".
        exit 64
        ;;
esac
