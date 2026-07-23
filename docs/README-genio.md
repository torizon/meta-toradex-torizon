MediaTek Genio 1200 (Adlink LEC-MTK-i1200 / I-Pi SMARC 1200)
======

This describes how to build Common Torizon OS for the Adlink LEC-MTK-i1200 SoM
(MediaTek Genio 1200 / MT8395 SoC) on the I-Pi SMARC 1200 carrier board.

The MediaTek and Adlink dependency layers are cloned manually after
`repo sync`. They come from MediaTek's IoT Yocto **v25.0** release, which is the
**scarthgap** line (kernel 6.6) and matches the scarthgap Torizon base.

Setup
======
1. Set up the default git user and e-mail:
```
$ git config --global user.email "you@example.com"
$ git config --global user.name "Your Name"
```
2. Install the repo utility to the development host:
```
$ mkdir ~/bin
$ PATH=~/bin:$PATH
$ curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
$ chmod a+x ~/bin/repo
```
3. Create a working directory for the Yocto build, go into that directory:
```
$ cd ~
$ mkdir ~/yocto-workdir
$ cd ~/yocto-workdir
```
4. Initialize the Torizon repository:
```
$ repo init -u https://git.toradex.com/toradex-manifest.git -b scarthgap-7.x.y -m torizon/default.xml
```
5. Sync the repositories:
```
$ repo sync
```
6. Download the MediaTek and Adlink BSP layers into `layers/`. All of these
track MediaTek IoT Yocto v25.0 (scarthgap):
```
# MediaTek core BSP for MT8395: machine, kernel 6.6, TF-A v2.6, U-Boot 2022.10,
# OP-TEE 3.19, Mali DDK r48 / Panfrost (gpu-provider.inc)
$ git -C layers clone -b rity-scarthgap-v25.0 https://gitlab.com/mediatek/aiot/rity/meta-mediatek-bsp.git

# Adlink board layer: lec-mtk-i1200-ufs machine + SMARC DTS
$ git -C layers clone -b rity-scarthgap-v25.0 https://github.com/ADLINK/meta-adlink-mtk.git

# Clang toolchain required by parts of the MediaTek graphics/multimedia stack
$ git -C layers clone -b scarthgap https://github.com/kraj/meta-clang.git
```

Build
======
1. Use the Docker container provided by Toradex to set up the build environment
   in the work directory `~/yocto-workdir` prepared in the previous steps:
```
$ docker run --rm -it --name=crops -v ~/yocto-workdir:/workdir --workdir=/workdir torizon/crops:scarthgap-7.x.y /bin/bash
```
2. Repeat the step of configuring the Git user name and e-mail:
```
$$ git config --global user.email "you@example.com"
$$ git config --global user.name "Your Name"
```
3. In the Docker console set up the environment for the target board:
   `MACHINE=<MACHINE> source setup-environment [BUILDDIR]`, where `MACHINE` is:
 * `lec-mtk-i1200-ufs` - LEC-MTK-i1200 booting from UFS

`BUILDDIR` is the directory where you would like to store the build files. For
example:
```
$$ MACHINE=lec-mtk-i1200-ufs source setup-environment build-lec-mtk-i1200
```

4. Build the Torizon images:
```
$$ bitbake torizon-docker
```

Flash the Device
======
The Genio 1200 is flashed over USB in download mode using MediaTek's
[genio-tools](https://gitlab.com/mediatek/aiot/bsp/genio-tools). The build
leaves the bootloader (BL2/FIP), GPT layout, the Ostree-based Torizon image and
the `rity.json` flash descriptor in the deploy directory, and also packs the
same set into a portable genio-flash tarball,
`torizon-docker-lec-mtk-i1200-ufs.aiotflash.tar`. Flash from the deploy
directory when the build host is also the flash host; use the tarball to carry
the image to a separate flash host without copying the whole deploy directory.

Prerequisites
------
Install `genio-tools` on the development host:
```
$ pip3 install genio-tools
```

1. Set the 4-position boot DIP switch to `1001` (positions 1-4, where `1` = ON
   and `0` = OFF, i.e. ON-OFF-OFF-ON) to boot from UFS. This same setting is
   kept for flashing - the board re-enters USB download mode briefly on each
   reset - so it is not changed for the Boot step below. Confirm the switch
   labelling/orientation against the I-Pi SMARC 1200 documentation:
   https://docs.ipi.wiki/smarc/ipi-smarc-1200/
2. Connect the board's micro-USB (OTG) port to the host, then run `genio-flash`.
   On the build host, run it directly from the deploy directory — all partition
   images and `rity.json` are already unpacked there:
```
$ cd ~/yocto-workdir/build-lec-mtk-i1200/deploy/images/lec-mtk-i1200-ufs/
$ genio-flash
```
   To flash from a separate host instead, copy just the tarball across, unpack
   it, and run `genio-flash` from the version-stamped directory it creates:
```
$ tar -xf torizon-docker-lec-mtk-i1200-ufs.aiotflash.tar
$ cd torizon-docker-lec-mtk-i1200-ufs-*/
$ genio-flash
```
`genio-flash` waits for the board's SoC to appear on USB; press the carrier
reset button (or power-cycle) while it waits, so the board re-enters its brief
USB download window. The tool's automatic reset is not available on this
carrier, so this reset is manual. `genio-flash` then writes the bootloader and
root filesystem to the on-board storage.

Boot
======
1. Leave the boot DIP switch at `1001` (the UFS boot-device setting used for
   flashing above) and power-cycle the board; no switch change is needed.
2. Connect the serial console (refer to the carrier board documentation for the
   debug UART header) at `921600` baud.
3. Power the target board on and monitor the boot sequence from the serial
   console:
```
...
Common Torizon OS 7.x.y-devel-<timestamp> torizon-lec-mtk-i1200-ufs ttyS0

torizon-lec-mtk-i1200-ufs login:

```
4. Login to the board using the `torizon/torizon` credentials.

Customizing with TorizonCore Builder
======
The `tcb-genio-bridge` wrapper
(`dynamic-layers/meta-mediatek-bsp/recipes-support/tcb-genio-bridge/`) applies a
TorizonCore Builder customization to the genio-flash image. The Genio target
ships as an `aiotflash.tar` wrapping an Android-sparse WIC, which TCB's raw-image
path can't read directly, so the bridge unwraps and unsparses the tarball, runs
TCB against the system image, re-sparses, and repacks it — only the rootfs
changed, partition layout preserved. Rootfs-level customizations (filesystem
overlays, preloaded containers) are supported; device-tree, kernel-argument,
U-Boot-env, and splash edits are not (TCB rejects them on raw/WIC images).

Host prerequisites: Docker and `simg2img`/`img2simg`
(`android-sdk-libsparse-utils`). The bridge runs TCB from its
`torizon/torizoncore-builder` container image, pulled on first run — no separate
`torizoncore-builder` install.

Deploy the bridge and collect it beside the image:
```
$$ bitbake tcb-genio-bridge
$ cd ~/yocto-workdir/build-lec-mtk-i1200/deploy/images/lec-mtk-i1200-ufs/
$ cp tcb-genio-bridge/tcb-genio-bridge tcb-genio-bridge/tcbuild-genio.yaml .
```

Prepare your customization as usual — a `changes/` overlay and/or a container
`bundle` in `tcbuild-genio.yaml` — then run the bridge against the tarball:
```
$ ./tcb-genio-bridge -o custom.tar torizon-docker-lec-mtk-i1200-ufs.aiotflash.tar
```
For a preloaded container, uncomment the `bundle:` block in `tcbuild-genio.yaml`
and set `platform: linux/arm64`; the bridge auto-detects `./docker-compose.yml`.

Flash the customized tarball and boot:
```
$ tar xf custom.tar
$ cd torizon-docker-lec-mtk-i1200-ufs-*/
$ genio-flash system
```

The bridge rewrites the rootfs partition in place, so the customization must fit
its free space (about 1 GB on the default image).

References
======
* MediaTek IoT Yocto developer guide: https://mediatek.gitlab.io/aiot/doc/aiot-dev-guide/master/
* IoT Yocto v25.0 release notes: https://mediatek.gitlab.io/aiot/doc/aiot-dev-guide/master/sw/yocto/release-notes/iot-yocto-v25.0-release-note.html
* Adlink meta-adlink-mtk: https://github.com/ADLINK/meta-adlink-mtk
* I-Pi SMARC 1200 documentation: https://docs.ipi.wiki/smarc/ipi-smarc-1200/
