Sync
======
1. Create the project folder:
```bash
$ mkdir common-torizon; cd common-torizon
```
2. Clone NXP's BSP layers:
```bash
$ repo init -u https://github.com/nxp-imx/imx-manifest.git -b imx-linux-scarthgap -m imx-6.6.52-2.2.0.xml
$ repo sync -j 10
```
3. Clone `meta-toradex-torizon` layer, and its dependencies:
```bash
$ cd sources
$ git clone https://github.com/torizon/meta-toradex-torizon.git -b scarthgap-7.x.y
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap
```

Build
======
1. Go back into our top folder `common-torizon`
2. Create a symlink to our `setup-environment`:
```bash
$ ln -s sources/meta-toradex-torizon/scripts/setup-environment torizon-setup-environment
```
> [!IMPORTANT]  
> Here we name the link to our script `torizon-setup-environment` to avoid clashing with NXP's `setup-script` (which is also called by our script).
3. Source `setup-environment` to setup build:
```bash
$ MACHINE=imx95-19x19-verdin . torizon-setup-environment build-imx95
```
This will create a build folder named `build-imx95`, where all build artifacts will be stored.
4. Start Common Torizon build:
```bash
$ bitbake torizon-docker
```

All artifacts should be inside `build-imx95/deploy/images/imx95-19x19-verdin`, including the `.wic` file.

Flashing the device
======
1. Change board boot switch to `ON OFF OFF ON` (CM33 Serial Download)
2. Download [uuu](https://github.com/nxp-imx/mfgtools/releases/tag/uuu_1.5.201) or build it from [source](https://github.com/nxp-imx/mfgtools)
3. Download image:
```bash
sudo ./uuu -b emmc_all <your sdcard image>
```
4. Change boot switch to `ON OFF ON OFF` (CM33 eMMC) to boot from eMMC.
