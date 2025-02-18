Sync
======
1. Create the directory structure for the Yocto layers e.g.:
```bash
$ mkdir common-torizon; cd common-torizon
$ mkdir sources; cd sources
```
2. Clone the necessary layers to build the Common Torizon image for the TI boards:
  * Download Bitbake:
```bash
$ git clone https://git.openembedded.org/bitbake -b 2.8
$ cd bitbake && git checkout 6c2641f7a9 && cd ..
```
  * Download `meta-yocto`:
```bash
$ git clone https://git.yoctoproject.org/meta-yocto -b scarthgap
```
  * Download `meta-ti` and its dependencies:
```bash
$ git clone https://git.yoctoproject.org/meta-ti -b scarthgap
$ git clone https://git.yoctoproject.org/meta-arm -b scarthgap
$ git clone https://git.yoctoproject.org/openembedded-core -b scarthgap oe-core
```
  * Download `meta-toradex-torizon` and its dependencies:
```bash
$ git clone https://github.com/torizon/meta-toradex-torizon.git -b scarthgap-7.x.y
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap
$ git clone https://git.yoctoproject.org/meta-virtualization -b scarthgap
```
  * And finally, download the dependency for `meta-updater` and `meta-virtualization`:
```bash
$ git clone https://github.com/openembedded/meta-openembedded -b scarthgap
```
  * Go back to our top folder `common-torizon`;
  * Create a symlink to our `setup-environment`:
```bash
$ ln -s sources/meta-toradex-torizon/scripts/setup-environment setup-environment
```

Repo Sync
======
Instead of manually creating the directory structure and cloning each layer like instructed above, it's possible to use the `repo` tool to look for our remote manifest files and automatically download all the necessary Yocto layers with a few commands.
1. If you don't have `repo` installed, please refer to this [link](https://developer.toradex.com/torizon/in-depth/build-torizoncore-from-source-with-yocto-projectopenembedded/#download-metadata).
2. Start syncing the manifest:
```bash
$ repo init -u git://git.toradex.com/toradex-manifest.git -b scarthgap-7.x.y -m common-torizon/ti/default.xml
$ repo sync -j 10
```
We **strongly recommend** using the `default.xml` manifest. We also have there `integration.xml` and `next.xml`, but these are development manifests used internally and they might be unstable.
`default.xml` is the manifest used for our releases, so they are reliable.  
Since we're now using the manifest, the `setup-environment` symlink will be automatically created for you on your top folder.
> [!IMPORTANT]  
> Common Torizon OS is only available on branches `scarthgap-7.x.y` or newer!

Build
======
1. Source `setup-environment` to setup build, specifying the machine to build with the MACHINE variable e.g.:
```bash
$ MACHINE=am62xx-evm . setup-environment <build-directory>
```
If a build directory is not given, the script will create one named `build-ti-${DISTRO}`, where all build artifacts will be stored. By default `DISTRO` is automatically set to `common-torizon` with the TI boards.

2. Inside the build directory, start the build e.g.:
```bash
$ bitbake torizon-docker
```
All artifacts should be inside `<build-directory>/deploy/images/${MACHINE}`, including the `.wic` file.

Booting the image from an SD card
======
1. Flash the generated .wic artifact file to a micro SD card and insert it into the board. For example, if using `dd` to flash the image, double-check the device name of the SD card with `lsblk`, make sure it's unmounted, then run the command below:
```bash
$ sudo dd if=<image-name>.wic of=/dev/<sdcard-device-name> bs=4M status=progress
```
2. Configure the board for SD card boot and connect the UART interface to the host PC. For example, for the AM62x SK EVM you can follow the instructions on the [TI Resource Explorer website](https://dev.ti.com/tirex/explore/node?node=A__AZatcAgHClq18snt16Hmfg__PROCESSORS-DEVTOOLS__FUz-xrs__LATEST).
3. Establish a serial connection with the board e.g. using `picocom`:
```bash
$ picocom -b 115200 /dev/ttyUSB0
```
