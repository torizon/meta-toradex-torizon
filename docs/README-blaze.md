# Blaze (imx6sx-blaze)

`meta-convotherm-torizon` is the Yocto layer for Convotherm's User Interface
Module on Torizon OS: the board support for the `imx6sx-blaze` machine, and
what Convotherm's application needs on it. It builds on an unmodified
`meta-toradex-torizon`.

## Contents

- The `imx6sx-blaze` machine, an i.MX 6SoloX board derived from NXP's
  SABRE-SD: U-Boot built from the SABRE-SD port with the board's control
  device tree, DDR configuration and patches (`u-boot-imx`); the kernel
  device trees for the 7" and 10.1" panels and their drivers (`linux-imx`);
  the eMMC image layout, with U-Boot in the boot0 hardware partition; the
  installation script (`imx6sx-blaze-uuu`); the Wi-Fi module's calibration
  (`wl18xx-conf`); and CAN brought up at boot (`can0-up`).
- GPU userspace from Mesa's etnaviv driver (EGL, GLES2, GBM) for the GC400,
  in place of Torizon's default of no GPU userspace on the root filesystem.
- Qt 5 (eglfs on KMS) and the application's runtime libraries, in
  `torizon-docker`, `torizon-minimal`, `torizon-podman` and their `-dev`
  images (`packagegroup-blaze-qt5-runtime`).
- Platform settings for the application: eglfs environment,
  VM sysctls, NetworkManager scan MAC, `Asia/Beijing` time-zone alias
  (`blaze-qt5-config`).
- A boot logo: U-Boot shows the application's BMP at power-on and the
  kernel keeps it on the panel until the application takes the display; the
  Torizon splash and the kernel's fbdev emulation are off (see Boot logo).
- Noto Sans fonts with the CJK fontconfig rules (`noto-sans-fonts`).
- SDK content for building the application outside Yocto with
  `bitbake <image> -c populate_sdk` (`blaze-sdk-cmake` and the toolchain
  package lists).
- Secure boot: the settings a signed build of this machine needs, and the
  files that fuse and close a unit (see Signed build and Fusing and closing).

## Dependencies

- `meta-toradex-torizon`, unmodified
- `meta-qt5`, branch `scarthgap`: https://github.com/meta-qt5/meta-qt5
- `meta-oe` and `meta-networking` from `meta-openembedded`
- for a signed build, `meta-toradex-security` and `meta-security`

The Blaze manifest, `common-torizon/blaze/default.xml` in
`toradex-manifest`, pins all of them, with `meta-convotherm-torizon`, at
the tested revisions.

`meta-convotherm-torizon` only changes `imx6sx-blaze` builds.

## Setup

Initialise and sync the Blaze manifest. It is based on the Torizon NXP
manifest, adds `meta-qt5`, `meta-toradex-security` and
`meta-convotherm-torizon`, and pins `meta-toradex-torizon` at a revision
that has the `imx6sxsabresd` machine. The `meta-convotherm-torizon`
repository is private, so `repo sync` needs Git credentials for it:

    repo init -u https://git.toradex.com/toradex-manifest.git -b scarthgap-7.x.y -m common-torizon/blaze/default.xml
    repo sync

## Build

`setup-environment` knows only the machines `meta-toradex-torizon` defines,
so create the build directory for the SABRE-SD, the board this one derives
from, and then switch the machine:

    EULA=1 MACHINE=imx6sxsabresd . setup-environment build-imx6sx-blaze

Each time NXP's setup script runs, it restores `conf/local.conf` and
`conf/bblayers.conf` from their `.org` copies. So make the changes below in
both copies. Set the machine:

    sed -i "s/^MACHINE ??= 'imx6sxsabresd'/MACHINE ??= 'imx6sx-blaze'/" conf/local.conf conf/local.conf.org

NXP's setup script also writes `EXTRA_IMAGE_FEATURES ?= "debug-tweaks"`,
which leaves `root` with an empty password and lets it log in on the serial
console and over SSH. Remove it:

    sed -i 's/^EXTRA_IMAGE_FEATURES ?= "debug-tweaks"/# &/' conf/local.conf conf/local.conf.org

Add the two layers. `meta-qt5` replaces `meta-qt6`, and
`meta-nxp-demo-experience` goes too, because its recipes need Qt 6:

    printf '%s\n' \
        'BBLAYERS += "${BSPDIR}/sources/meta-qt5 ${BSPDIR}/sources/meta-convotherm-torizon"' \
        'BBLAYERS:remove = "${BSPDIR}/sources/meta-qt6 ${BSPDIR}/sources/meta-nxp-demo-experience"' \
        | tee -a conf/bblayers.conf >> conf/bblayers.conf.org

Check the machine and the image features, and build:

    bitbake -e | grep -E '^(MACHINE|EXTRA_IMAGE_FEATURES)='
    bitbake torizon-docker

`EXTRA_IMAGE_FEATURES` must not list `debug-tweaks`.

To return to the build directory in a new shell, run
`. sources/poky/oe-init-build-env build-imx6sx-blaze`.

The installation files are in `deploy/images/imx6sx-blaze`: `u-boot.imx`,
`torizon-docker-imx6sx-blaze.wic` and `imx6sx-blaze.uuu`.

## Signed build

A signed build adds HABv4 signing of U-Boot, a signed FIT image (kernel,
both device trees, initramfs) and a signed OSTree commit with composefs, from
Toradex's `torizon-signed` class. It is switched on per build directory; a
build without it is the unsigned image above, unchanged.

The Blaze manifest syncs `meta-toradex-security` and `meta-security` into
`sources/`. From the build directory, add both to the build and switch
signing on:

    printf '%s\n' 'BBLAYERS += "${BSPDIR}/sources/meta-security ${BSPDIR}/sources/meta-toradex-security"' \
        | tee -a conf/bblayers.conf >> conf/bblayers.conf.org
    printf '%s\n' 'INHERIT += "torizon-signed"' \
        | tee -a conf/local.conf >> conf/local.conf.org

The keys are in the build directory, where Toradex's classes look for them:
`keys/cst` (HABv4), `keys/fit` (FIT image) and `keys/ostree` (OSTree commit).
Make the HABv4 keys before the first signed build, as NXP's CST User Guide
does, with the CST the build compiles from source. In the build directory,
with your own passphrase for the keys:

    git clone https://github.com/toradex/imx-code-signing-tool.git cst-src
    git -C cst-src checkout 8931a2f536205a76c3f1778b02df857426291090
    mkdir -p keys/cst/crts
    cp -r cst-src/keys cst-src/ca keys/cst/
    printf '%s\n' '<passphrase>' '<passphrase>' > keys/cst/keys/key_pass.txt
    (cd keys/cst/keys && ./hab4_pki_tree.sh -existing-ca n -kt rsa -kl 2048 \
        -duration 10 -num-srk 4 -srk-ca y)
    bitbake imx-code-signing-tool-native
    (cd keys/cst/crts && ../../../tmp/sysroots-components/x86_64/imx-code-signing-tool-native/usr/bin/srktool \
        -h 4 -t SRK_1_2_3_4_table.bin -e SRK_1_2_3_4_fuse.bin -d sha256 \
        -c SRK1_sha256_2048_65537_v3_ca_crt.pem,SRK2_sha256_2048_65537_v3_ca_crt.pem,SRK3_sha256_2048_65537_v3_ca_crt.pem,SRK4_sha256_2048_65537_v3_ca_crt.pem)

The first signed build generates the FIT keys (`keys/fit/dev.key`,
`dev.crt`, `dev2.key`, `dev2.crt`) and the OSTree key pair
(`keys/ostree/cfs-dev.sec`, `cfs-dev.pub`). That build warns that it cannot
get the checksum of `keys/fit/dev.key` and `dev.crt`, because it uses them
before it has made them; on that build the warning is expected. Keep every
key outside version control and back it up: a unit fused with the SRK hash of
these keys runs only images signed with them. Make each key set once, not per
build: copy the whole `keys` directory into every later build directory
before its first build. A build directory with no `keys/fit` or
`keys/ostree` generates new keys, and an update built there is refused by
units installed with the earlier keys. Then build as above. In
`deploy/images/imx6sx-blaze`, `u-boot.imx` is then HABv4-signed, and the
directory also holds the signed FIT
`fitImage-initramfs-ostree-torizon-image-imx6sx-blaze-imx6sx-blaze` and
the fuse files for the keys' SRK hash (see Fusing and closing).

Keep two key sets, each made as above: a development set, fused into
development units and used for the images built during development, and a
production set, fused into product units and used only for release images.
A leaked development set exposes only development units; keep the production
set with its owner, off the build hosts used for development. A build
directory signs with the set in its `keys`, and images of one set do not boot
on units of the other.

Build every image, every update included, with Yocto and the `keys` of the
units it is for. TorizonCore Builder re-signs the root filesystem, but its
re-signing of the bootloader and the kernel is listed per Toradex module,
is still in development for the Apalis and Colibri iMX6, and does not
cover this board
(https://developer.toradex.com/torizon/security/secure-boot-on-torizoncore/,
"Related TorizonCore Builder Features"). `keys/cst/keys/key_pass.txt` holds
the HABv4 keys' passphrase in clear; protect it as you protect the keys.

## Password

Torizon gives the user `torizon` the password `torizon`, which is publicly
known; a product sets its own before it ships. `root` has no password it can
log in with, once `debug-tweaks` is removed as in Build. In the build
directory, put the hash of the new password in `conf/local.conf` and its
`.org` copy, and build:

    H=$(openssl passwd -6 '<new password>')
    printf "BLAZE_TORIZON_PASSWORD_HASH = '%s'\n" "$H" \
        | tee -a conf/local.conf >> conf/local.conf.org

On an unsigned image Torizon still asks for a new password at the first
login. On a signed image `/etc` is reset at every boot, so this password is
the one the unit has after every boot; `passwd` changes it only until the
next reboot. A signed image keeps its SSH host keys under
`/var/rootdirs/etc/ssh`, so they stay the same across reboots and across
updates between signed images; the first boot of a signed image, after an
unsigned one, makes new ones.

## Installation

The board is written through itself over USB, with NXP's `uuu` 1.4.182 or
later.

1. Close boot switch `SW2200` position 1 (Serial Download), leave positions 2
   and 4 open, and connect the USB OTG port on `J1300` to the host. The board
   enumerates as `15a2:0071`. Position 4 grounds the OTG ID pin: closed, the
   port is a host and the serial downloader does not appear.
2. Download [uuu](https://github.com/nxp-imx/mfgtools/releases/tag/uuu_1.5.201)
   or build it from [source](https://github.com/nxp-imx/mfgtools).
3. From `<build-directory>/deploy/images/imx6sx-blaze`, write the bootloader
   and the image through the board:

       sudo /path/to/uuu imx6sx-blaze.uuu

   The script reads `u-boot.imx` and `torizon-docker-imx6sx-blaze.wic` from
   that directory. It writes U-Boot into the eMMC boot0 partition, overwrites
   the eMMC user area and erases the U-Boot environment.

   A signed build deploys a script of its own under the same name, which
   also reads `u-boot.imx-sdp`, a U-Boot signed for loading over USB. It
   loads that U-Boot first and runs `hab_status`. If the boot ROM reports a
   HAB event other than the i.MX 6SoloX's RNG self-test warning, the script
   stops before it writes anything.
4. Open boot switch `SW2200` position 1 (Internal Boot) and power-cycle the
   board.

## Fusing and closing

**Fuses are written once and cannot be changed or erased.** Once closed, a
unit whose fuses hold the SRK hash of a set of keys runs only images signed
with that set, for the rest of its life. Fuse a development unit only with
the development keys and a product unit only with the production keys, and
close it only after the checks below.

This procedure has not yet been run on a unit of this board.

The i.MX 6SoloX takes the SRK hash, eight 32-bit words, in fuse bank 3,
words 0 to 7, and is closed by `SEC_CONFIG[1]`, the value `0x00000002` in
bank 0, word 6. Torizon OS writes them from U-Boot's boot script: it writes
the words, resets the unit, compares the words on the next boot and runs
`hab_status`, and closes the unit only if both pass and closing was asked
for. No other fuse is written: the SRK hash is not locked, and JTAG stays
enabled. With JTAG enabled, someone with physical access to the board's
debug connector can still take control of a closed unit; disabling JTAG is
a further fuse that cannot be undone, and is the key owner's decision, made
before units are closed.

A signed build writes these files to `deploy/images/imx6sx-blaze`, generated
from the keys in `keys/cst`; the build fails if their words differ from the
keys' SRK hash:

| File | Use |
| --- | --- |
| `fuse-cmds.txt` | U-Boot's fuse commands, for reference. Do not run them by hand. |
| `imx6sx-blaze-fuse-dryrun.uuu` | Installs the unit, as `imx6sx-blaze.uuu` does, with a dry run armed: the next boots print the fuse commands and write nothing. |
| `imx6sx-blaze-fuse-srk.uuu` | Installs the unit with the SRK hash armed: the next boot writes it and leaves the unit open. |
| `imx6sx-blaze-fuse-close.uuu` | Installs the unit with the SRK hash and the close armed. |
| `u-boot-boot0-fuse-dryrun.bin`, `-srk.bin`, `-close.bin` | Read by the three scripts above: U-Boot with an environment that arms the fusing. They are made for an eMMC boot0 partition of 4 MiB, `BLAZE_EMMC_BOOT0_SIZE`; on another size the install succeeds but arms nothing, so set the variable to the eMMC's boot0 size. |
| `fuse-firstboot-dryrun.env`, `-srk.env`, `-close.env` | The same three settings, to write into the environment of a unit already installed. |
| `fuse-dryrun.yml` | A fuse package of eight zero words that closes nothing, to try the Torizon Cloud route. |
| `fuse-srk.yml` | A fuse package that writes the SRK hash and leaves the unit open. |
| `fuse-close.yml` | A fuse package that writes the SRK hash and closes the unit. |

A unit is armed in one of three ways, each setting the same variables of
U-Boot's environment:

- In the factory, by installing it with one of the `imx6sx-blaze-fuse-*.uuu`
  scripts instead of `imx6sx-blaze.uuu`.
- On a unit already installed, by writing a `fuse-firstboot-*.env` file into
  its environment from Linux.
- Over Torizon Cloud, by deploying a fuse package to the unit.

Arm only a unit installed with this build: an older U-Boot without the
`setexpr` command would write every word to word 0. Arm only a unit whose
bank 3 is blank or already holds this hash: the words are added to the bits
already set, and a unit fused with another hash can never be closed.

Fuse in two steps: first the SRK hash, leaving the unit open, then the close.
In between the unit still boots any image, and a mistake can still be found.
On a blank unit, `imx6sx-blaze-fuse-close.uuu` and `fuse-close.yml` write
the hash and close in one go; use them that way only on units of a product
whose keys have already been fused and closed on a test unit.

### Before fusing

1. Check that the words are those of the unit's key set. In the build
   directory, the two commands must print the same eight numbers:

       hexdump -v -e '8/4 "0x%x " "\n"' keys/cst/crts/SRK_1_2_3_4_fuse.bin
       sed -n 's/^fuse_prog_list=//p' deploy/images/imx6sx-blaze/fuse-firstboot-srk.env

2. Install the unit with this build, as in Installation, and boot it from the
   eMMC. On the serial console, press a key at `Hit any key to stop
   autoboot`, and run:

       hab_status; echo rc=$?

   It must print `Secure boot disabled` and `rc=0`. `rc=0` means the boot ROM
   reported no event but the i.MX 6SoloX's RNG self-test warning (`STS =
   HAB_WARNING`, `ENG = HAB_ENG_CAAM`), which `hab_status` still prints. Then
   run `boot` and log in.

3. Check that the unit is open and that bank 3 is blank:

       N=/sys/bus/nvmem/devices/imx-ocotp0/nvmem
       sudo hexdump -v -e '"0x%x\n"' -s 0x18 -n 4 $N
       sudo hexdump -v -e '8/4 "0x%x " "\n"' -s 0x60 -n 32 $N

   Bit `0x2` of the first value must be clear, and the eight words `0x0`.

4. Run a dry run: install the unit with `imx6sx-blaze-fuse-dryrun.uuu`, as
   in Installation, and boot it from the eMMC. On the serial console, the
   first boot prints `DRY RUN: fuse prog 3 0 <word>` to `3 7 <word>` with the
   eight words of step 1, in order, and resets; the next prints `DRY RUN:
   fuse cmp` for each word, `hab_status` and `DRY RUN: Closing is set`. No
   fuse is written. After the login, `sudo fw_printenv fuse_status` prints
   `fuse_status=success`.

### Writing the SRK hash

1. Install the unit with `imx6sx-blaze-fuse-srk.uuu`, as in Installation,
   and keep it powered until it reaches the login prompt. U-Boot writes the
   words, resets the unit, compares the words and runs `hab_status`.

   On a unit already installed, write the settings into U-Boot's
   environment instead, check them, and reboot:

       scp deploy/images/imx6sx-blaze/fuse-firstboot-srk.env torizon@<unit>:
       ssh torizon@<unit>
       sudo fw_setenv -s fuse-firstboot-srk.env
       sudo fw_printenv fuse_prog_list fuse_prog_close fuse_status fuse_dry_run
       sudo reboot

   The `fw_printenv` command must print the file's four lines exactly. If it
   does not, clear the four variables and stop:

       sudo fw_setenv fuse_prog_list; sudo fw_setenv fuse_prog_close
       sudo fw_setenv fuse_status; sudo fw_setenv fuse_dry_run

2. Log in and check the result. A successful installation alone does not
   show that the unit was fused:

       sudo fw_printenv fuse_status
       N=/sys/bus/nvmem/devices/imx-ocotp0/nvmem
       sudo hexdump -v -e '8/4 "0x%x " "\n"' -s 0x60 -n 32 $N
       sudo hexdump -v -e '"0x%x\n"' -s 0x18 -n 4 $N

   `fuse_status` must be `success`; with any other value, do not close the
   unit. The second command prints the fused SRK hash, which must equal the
   words of Before fusing, step 1. The third prints bank 0 word 6, whose bit
   `0x2` must still be clear: the unit is open.

3. Install the unit again, as in Installation. With the hash fused, the boot
   ROM now checks `u-boot.imx-sdp` against it, and the installation must pass
   its `hab_status` step. This is the path that recovers a closed unit, so it
   is checked before closing.

### Closing

1. Install the unit with `imx6sx-blaze-fuse-close.uuu`, as in Installation,
   and keep it powered until it reaches the login prompt. U-Boot writes the
   same words again, which changes no bit, resets the unit, compares the
   words, runs `hab_status`, and closes the unit only if both pass.

   On a unit already installed, write `fuse-firstboot-close.env` into its
   environment and reboot, as in Writing the SRK hash, step 1.

2. Log in, check that `sudo fw_printenv fuse_status` prints
   `fuse_status=success`, and reboot: the closed state reads back only after
   the next reset. Then check the unit:

       sudo fw_printenv fuse_val_close
       sudo hexdump -v -e '"0x%x\n"' -s 0x18 -n 4 /sys/bus/nvmem/devices/imx-ocotp0/nvmem

   `fuse_val_close` must be `1`, and bit `0x2` of the second value set. On a
   closed unit U-Boot's command line is off, and `hab_status` on the
   installation's serial console reads `HAB Configuration: 0xcc, HAB State:
   0x99`.

### Fusing over Torizon Cloud

A unit already provisioned can take the hash and the close as fuse packages,
delivered as updates through Torizon Cloud, which needs its commercial plan
and the account's `credentials.zip`. A unit in the field takes them last,
after the operating-system update and the bootloader update that bring it to
a signed image of its keys.

1. Provision the unit, and check on it that it has the fuse component:

       grep imx6sx-blaze-fuses /usr/lib/sota/secondaries.json
       grep ^fuse_num= /boot/loader/uEnv.txt

2. Push the packages. TorizonCore Builder's own fuse handling knows only
   Toradex's modules, so each file is pushed as a generic package, as it is,
   under a package name of its own:

       torizoncore-builder platform push deploy/images/imx6sx-blaze/fuse-srk.yml \
           --package-name <name> --hardwareid imx6sx-blaze-fuses \
           --credentials credentials.zip

   Push `fuse-close.yml` the same way. To try the route first, push and
   deploy `fuse-dryrun.yml`, which writes nothing.

3. Deploy `fuse-srk.yml` to the unit alone, and keep it powered until it has
   rebooted twice and reaches the login prompt. Check it as in Writing the
   SRK hash, steps 2 and 3.

4. Deploy `fuse-close.yml` the same way, and check the unit as in Closing,
   step 2. Torizon Cloud reports success only when the words compared and
   `hab_status` passed; the unit itself is the proof.

## Recovering a closed unit

A closed unit whose eMMC holds software that does not boot is installed
again as above, over the serial downloader, with the files of a signed build
made with the unit's `keys`. The boot ROM of a closed unit runs only a
U-Boot signed with the keys whose SRK hash is in its fuses, so the files of
a build with any other keys stop at the first step. This path has not yet
been tested on a closed unit.

## Wi-Fi calibration

The Wi-Fi module's calibration files, one per display panel, are the board
vendor's and are carried in `meta-convotherm-torizon` (`wl18xx-conf`). The
board's 7" and 10.1" variants use different 2.4 GHz transmit-power limits. At
boot the image uses the file for the panel the bootloader detected.

## Boot logo

U-Boot shows a boot logo from `/var/lib/splash/splash.bmp` on the board's
eMMC, if the file is there, and keeps it on the panel until the first program
that sets the display mode, such as the Qt application on `eglfs`, takes the
panel. The image ships no logo, and without one the panel stays dark until
then. The Torizon splash does not run, and the kernel's fbdev emulation is off,
so there is no `/dev/fb0` and no framebuffer console.

The file must be an uncompressed 8-bit indexed BMP no larger than the panel:
1024x600 for the 7" panel, 1280x800 for the 10.1" one. It is centred and drawn
as is, in the panel's native landscape, so draw the picture for the way the
unit is mounted; for a 90° or 270° mounting, turn the picture inside a
landscape file. U-Boot does not replay the filesystem journal and sees a
change only once it has been synced. The directory belongs to root:

    sudo cp logo.bmp /var/lib/splash/splash.bmp && sync

Replacing or removing the file needs the same sync. The file is kept across
updates and rollbacks. U-Boot reports a file it cannot use on the console with a
`Logo:` line, and the panel stays dark.
