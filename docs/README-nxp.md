Setup
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
$ git clone https://github.com/torizon/meta-toradex-torizon.git -b scarthgap-7.x.y sources/meta-toradex-torizon
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap sources/meta-updater
```

Additional Setup for i.MX93 FRDM boards
======
1. Clone NXP's BSP layers specific to the i.MX93 FRDM board:
```bash
$ git clone https://github.com/nxp-imx-support/meta-imx-frdm.git -b imx-frdm-4.0 sources/meta-imx-frdm
$ ln -s sources/meta-imx-frdm/tools/imx-frdm-setup.sh imx-frdm-setup.sh
```

Build
======
| Board | MACHINE | Status |
|---|---|---|
| FRDM i.MX 93 | imx93frdm  | Supported  |
| Verdin i.MX95 EVK  | imx95-19x19-verdin  | Supported |

1. Create a symlink to our `setup-environment`:
```bash
$ ln -s sources/meta-toradex-torizon/scripts/setup-environment torizon-setup-environment
```
> [!IMPORTANT]  
> Here we name the link to our script `torizon-setup-environment` to avoid clashing with NXP's `setup-script` (which is also called by our script).
2. Source `torizon-setup-environment` to setup build:
```bash
$ MACHINE=<MACHINE> . torizon-setup-environment build
```
where '<MACHINE>' is the value from the table above.

This will create a build folder named `build`, where all build artifacts will be stored.
3. Start Common Torizon build:
```bash
$ bitbake torizon-docker
```

All artifacts should be inside `build/deploy/images/<MACHINE>`, including the `.wic` file.

Flash the Device (Verdin i.MX95 EVK)
======
1. Change board boot switch to `ON OFF OFF ON` (CM33 Serial Download)
2. Download [uuu](https://github.com/nxp-imx/mfgtools/releases/tag/uuu_1.5.201) or build it from [source](https://github.com/nxp-imx/mfgtools)
3. Download image:
```bash
sudo ./uuu -b emmc_all <bootloader> <wic image>
```
For example, flashing a local build we've generated:
```bash
sudo ./uuu -v -b emmc_all imx-boot-imx95-19x19-verdin-sd.bin-flash_all torizon-docker-imx95-19x19-verdin-7.0.0-devel-20250602173442+build.0.wic.zst
```
4. Change boot switch to `ON OFF ON OFF` (CM33 eMMC) to boot from eMMC.

Flash the Device (FRDM i.MX93 SDCard)
======
1. Change board boot switch to `ON ON OFF OFF` to boot from SDCard.
2. Flash the wic image to an SDCard.
```bash
zstdcat if=torizon-docker-imx93-11x11-lpddr4x-frdm-7.0.0-devel-20250602173442+build.0.wic.zst | sudo dd of=<sdcard-device-node>
```
3. Insert the SDCard.
4. Power on the board.

Flash the Device (FRDM i.MX93 eMMC)
======
Coming Soon
