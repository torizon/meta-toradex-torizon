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
$ curl https://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
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

`BUILDDIR` is the directory where you would like to store the build files.
`EULA=1` accepts the commercial license of the proprietary Mali DDK, which the
BSP selects by default. For example:
```
$$ EULA=1 MACHINE=lec-mtk-i1200-ufs source setup-environment build-lec-mtk-i1200
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
The Genio target ships as an `aiotflash.tar` wrapping an Android-sparse WIC,
which TorizonCore Builder's (TCB's) raw-image path can't read directly:
`genio2img` unwraps and unsparses the tarball so a released
`torizoncore-builder` can run against the system image directly, and
`img2genio` re-sparses and repacks the result — only the rootfs changed,
partition layout preserved. Rootfs-level customizations (filesystem overlays,
preloaded containers) are supported; device-tree, kernel-argument, U-Boot-env,
and splash edits are not (TCB rejects them on raw/WIC images). The Genio
system image is 4Kn, so it needs a `torizoncore-builder` release that can open
4096-byte-sector raw images, and `android-sdk-libsparse-utils` on the host.

Host prerequisites
------
`simg2img`/`img2simg` (`android-sdk-libsparse-utils`) for `genio2img`/
`img2genio` themselves, and a `torizoncore-builder` release that can open
4096-byte-sector raw images to customize between them — requested by the
`sector-size` and `base-sector-size` keys in the tcbuild configuration, or by
`--raw-sector-size` when driving the command line directly.

Customization classes
------
`torizoncore-builder`, run against `genio2img`'s staged `input.wic`, delivers
rootfs-level content into the OSTree rootfs:

* File and directory overlays — a `changes/` tree staged by `genio2img -c`
  (default `./changes`), applied per `tcbuild-genio.yaml`'s
  `customization.filesystem`.
* Yocto packages delivered as installed files (e.g. `usr/bin/`, `usr/lib/`),
  through the same overlay mechanism.
* Prebuilt kernel modules dropped in as `.ko` files.
* Preloaded containers, from a `docker-compose` bundle staged by
  `genio2img -b` (default `./docker-compose.yml`; see below).

Command-line reference
------
Kept in sync with `genio2img -h`/`img2genio -h`; update both together when a
flag changes.

```
$ ./genio2img [-d WORKDIR] [-f TCBUILD_YAML] [-c CHANGES_DIR] [-b COMPOSE_FILE] INPUT_TAR
```

* `INPUT_TAR` — the `aiotflash.tar` produced by the Yocto build.
* `-d WORKDIR` — working directory to create (default: `INPUT_TAR`'s
  basename, `.aiotflash.tar`/`.tar` stripped). Must not already exist.
* `-f TCBUILD_YAML` — the tcbuild config to stage (default: `tcbuild-genio.yaml`
  beside the script).
* `-c CHANGES_DIR` — a directory of files to stage as the rootfs overlay
  (default: `./changes` if present).
* `-b COMPOSE_FILE` — the `docker-compose` file for a container-preload bundle
  (default: `./docker-compose.yml` if present). Its basename must match the
  tcbuild config's `bundle.compose-file`.

```
$ ./img2genio [-o OUTPUT_TAR] WORKDIR
```

* `WORKDIR` — the directory `genio2img` created (must still hold `unpack/`
  and a customised `output.wic` — `torizoncore-builder`'s
  `output.raw-image.local`, run from `WORKDIR`).
* `-o OUTPUT_TAR` — repacked tarball (default: `WORKDIR-custom.tar`).

Performing image customization
------
Prepare your customization — a `changes/` overlay, a container bundle, or both
(see Customization classes above).

The image build deploys the tools beside the flashing artifacts; unpack them
there:
```
$ cd ~/yocto-workdir/build-lec-mtk-i1200/deploy/images/lec-mtk-i1200-ufs/
$ tar xf genio2img-tools.tar
$ cp genio2img-tools/genio2img genio2img-tools/img2genio genio2img-tools/tcbuild-genio.yaml .
```

1. **Convert** the tarball to a raw WIC and stage a customization run:
```
$ ./genio2img -d work torizon-docker-lec-mtk-i1200-ufs.aiotflash.tar
$ cd work
```
   Drop overlay files into `changes/` and/or add a `bundle:` block to
   `tcbuild-genio.yaml` (staged from the copy beside the scripts).

2. **Customize** with `torizoncore-builder`, from inside `work/`:
```
$ torizoncore-builder build --file tcbuild-genio.yaml
```

3. **Convert back** and flash:
```
$ cd ..
$ ./img2genio work
$ tar xf work-custom.tar
$ cd torizon-docker-lec-mtk-i1200-ufs-*/
$ genio-flash system
```

`work/` can be reused for more than one customization from the same base
image — rerun steps 2–3 with different overlay/compose content without
re-running `genio2img`. A preloaded container bundle larger than the base
rootfs partition's free space (about 0.8 GB on the default image) is handled
by `torizoncore-builder` growing the output image itself, so it is not a
limit on the bundle — but the grow makes the run's disk appetite scale with
the bundle.

Host free space
------

Each `genio2img` `WORKDIR` holds the unpacked tarball, the unsparsed input
image, `torizoncore-builder`'s output image, the re-sparsed image and
`img2genio`'s output tarball at the same time. Budget

    10 x <tarball> + 2 x <unpacked bundle>

on that filesystem. Without a bundle that is the familiar ten-times-the-tarball
figure; with one the bundle term dominates — measure the bundle's unpacked
size rather than estimating it (it is roughly three times the pull), e.g. with
`docker create`/`docker export | wc -c` per image. A 0.70 GiB tarball with an
8.13 GiB bundle peaked at 18.9 GiB in measurement — against the 7.0 GiB the
tarball alone would suggest — for which this rule budgets 23.2 GiB.

To work on a different disk, pass `genio2img -d` a `WORKDIR` path there — if
running `torizoncore-builder` as a container, it must be one the Docker daemon
can also reach, so with snap-installed Docker keep it under your home.

The container images themselves are fetched into Docker's own storage, which is
usually on a different filesystem (`docker info` reports `Docker Root Dir`);
allow for the bundle there as well.

References
======
* MediaTek IoT Yocto developer guide: https://mediatek.gitlab.io/aiot/doc/aiot-dev-guide/master/
* IoT Yocto v25.0 release notes: https://mediatek.gitlab.io/aiot/doc/aiot-dev-guide/master/sw/yocto/release-notes/iot-yocto-v25.0-release-note.html
* Adlink meta-adlink-mtk: https://github.com/ADLINK/meta-adlink-mtk
* I-Pi SMARC 1200 documentation: https://docs.ipi.wiki/smarc/ipi-smarc-1200/
