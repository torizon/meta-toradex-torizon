Setup
======
1. Clone Renesas BSP layer:
```
$ git clone https://github.com/renesas-rz/rz-community-bsp.git -b scarthgap
```
2. Clone meta-toradex-torizon into `rz-community-bsp` folder
```
$ git clone https://github.com/torizon/meta-toradex-torizon.git -b scarthgap-7.x.y
```
3. Clone Torizon dependencies into `rz-community-bsp`
```
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap
```
4. Create a symlink to our `setup-environment`:
```bash
$ ln -s meta-toradex-torizon/scripts/setup-environment setup-environment
```

Build
======
1. Build with the following commands, inside `rz-community-bsp`
```
$ ./kas-container shell
$ cd /repo
$ MACHINE=smarc-rzv2l . setup-environment
$ bitbake torizon-docker
```

All artifacts should be inside `build/deploy/images/smarc-rzv2l`

Flash the Device
======
1. With `SW11` set to `OFF ON OFF ON`, run (assuming ttyUSB0)
```
cat Flash_Writer_SCIF_RZV2L_SMARC_PMIC_DDR4_2GB_1PCS.mot > /dev/ttyUSB0
```
2. Now following https://www.renesas.com/en/document/gde/smarc-evk-rzv2l-linux-start-gude-rev102?r=1569131, flash the bootloader
```
> xls2
...
===== Please Input Program Top Address ============
Please Input : H'11E00
===== Please Input Qspi Save Address ===
Please Input : H'00000
```
then
```
cat bl2_bp-smarc-rzv2l.srec > /dev/ttyUSB0
```
And again
```
> xls2
...
===== Please Input Program Top Address ============
Please Input : H'00000
===== Please Input Qspi Save Address ===
Please Input : H'1D200
```
then
```
cat fip-smarc-rzv2l.srec > /dev/ttyUSB0
```
3. And finally copy the rootfs to the SD card
Have the SD card in a single partition and run (assuming SD card is `sdc`)
```
$ sudo dd if=torizon-docker-smarc-rzv2l-7.0.0-devel-20250127163415+build.0.ota-ext4 of=/dev/sdc1 oflag=direct bs=4M status=progress conv=fsync
```

Boot
======
With both steps above finished, just move `sw11` to `OFF OFF OFF ON`, insert the SD card into `CN10` and power on the device.  
Press `Enter` on keyboard so you land on u-boot shell, and then run
```
=> env default -a
=> saveenv
```
This will reset the u-boot environment variables to their default values. After that just run
```
=> run bootcmd
```
It should boot straight into Common Torizon OS.
> [!NOTE]  
> These steps in the bootloader should only be performed on the first boot of the device with Common Torizon OS. Subsequent boots or flashes should not require it.
