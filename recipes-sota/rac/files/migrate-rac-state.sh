#!/bin/sh
# Migrate pre-existing RAC runtime state (device key, TUF trust root, SSH
# host keys, etc.) when the RAC privilege-drop user/home changed from
# "torizon" to "rac".
#
# Two situations this needs to handle:
#  1. RUNRAC_FOLDER kept its new default (/home/rac/run/rac) but a device
#     upgraded from an older RAC that stored its state under the old
#     default (/home/torizon/run/rac): move the old state into the new
#     location so the device keeps its existing device key / TUF trust
#     anchor instead of silently re-registering from scratch.
#  2. RUNRAC_FOLDER is overridden back to the old path (or any path that
#     already has torizon-owned content from before this change): the
#     directory and its pre-existing contents need to be re-owned by rac,
#     since RAC now runs as rac instead of torizon.
#
# This runs as root (ExecStartPre, before RAC drops privileges), so it can
# read/move/chown regardless of current ownership.

set -e

OLD_RUNRAC_FOLDER="@@OLD_RUNRAC_FOLDER@@"
RUNRAC_FOLDER="@@RUNRAC_FOLDER@@"

# Early exit if RUNRAC_FOLDER is already fully owned by rac:rac.
owner="$(stat -c '%U:%G' "$RUNRAC_FOLDER" 2>/dev/null || echo '')"
if [ "$owner" = "rac:rac" ]; then
	exit 0
fi

# Ensure the runtime folder exists.
mkdir -p "$RUNRAC_FOLDER"

# Case 1: moving from the old default path to a new, still-empty one.
if [ "$OLD_RUNRAC_FOLDER" != "$RUNRAC_FOLDER" ] && \
   [ -d "$OLD_RUNRAC_FOLDER" ] && [ -n "$(ls -A "$OLD_RUNRAC_FOLDER" 2>/dev/null)" ] && \
   [ ! -d "$RUNRAC_FOLDER" -o -z "$(ls -A "$RUNRAC_FOLDER" 2>/dev/null)" ]; then
    echo "rac: migrating existing RAC state from $OLD_RUNRAC_FOLDER to $RUNRAC_FOLDER"
    # Remove the empty folder so mv will not move the old as a subfolder.
    [ -d "$RUNRAC_FOLDER" ] && rmdir "$RUNRAC_FOLDER"
    mv "$OLD_RUNRAC_FOLDER" "$RUNRAC_FOLDER"
fi

# Case 2: whatever ended up at RUNRAC_FOLDER (freshly created, migrated, or
# pre-existing from an override) must be owned by rac, recursively -- a
# plain `install -d -o rac -g rac` only fixes the directory itself, not
# files left behind with the old (torizon) ownership.
chown -R rac:rac "$RUNRAC_FOLDER"
chmod 0700 "$RUNRAC_FOLDER"
