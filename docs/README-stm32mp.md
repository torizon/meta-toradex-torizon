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
$ repo init -u git://git.toradex.com/toradex-manifest.git -b scarthgap-7.x.y -m torizon/default.xml
```
5. Sync the repositories:
```
$ repo sync
```
6. Download the ST32MP BSP layer:
```
$ git -C layers clone -b openstlinux-6.6-yocto-scarthgap-mpu-v25.03.19 https://github.com/STMicroelectronics/meta-st-stm32mp
```

Build
======
1. Use the Docker container provided by Toradex to setup the build environment in the work directory ~/yocto-workdir prepared in previous steps:
```
$ docker run --rm -it --name=crops -v ~/yocto-workdir:/workdir --workdir=/workdir torizon/crops:scarthgap-7.x.y /bin/bash
```
2. Repeat the step of configuring the Git user name and e-mail:
```
$$ git config --global user.email "you@example.com"
$$ git config --global user.name "Your Name"
```
3. In the Docker console set up the environment for a specific STM32MP board: `MACHINE=<MACHINE> source setup-environment [BUILDDIR]`, where `MACHINE` is one of the supported STM32MP targets:
 * `stm32mp25-eval` for the STM32MP2 Evaluation kit
 * `stm32mp25-disco` for the STM32MP2 Discovery kit
 * `stm32mp15-disco` for STM32MP157 Discovery kit,
`BUILDDIR` is the directory where you would like to to store the build files. For example:
```
$$ MACHINE=stm32mp25-disco source setup-environment build-stm32mp25-disco
```
4. Build the Torizon images:
```
$$ bitbake torizon-docker
```

Flash the Device
======

1. Go on st.com to download the STM32CubeProgrammer software and install it to the `/opt/st/bin` directory.
2. Add `STM32_Programmer_CLI` to your `PATH`:
```
$ export PATH=$PATH:/opt/st/bin:
$ STM32_Programmer_CLI
      -------------------------------------------------------------------
                        STM32CubeProgrammer v2.14.0
      -------------------------------------------------------------------


Usage :
STM32_Programmer_CLI.exe [command_1] [Arguments_1][[command_2][Arguments_2]...]
```
3. Select the USB Serial Downloader mode. Refer to ST’s WiKi for details on the boot switches for your board:
 * `STM32MP257X-EV1` - https://wiki.st.com/stm32mpu/wiki/STM32MP257x-EV1_-_hardware_description#Boot_related_switches
 * `STM32MP257x-DKx` - https://wiki.st.com/stm32mpu/wiki/STM32MP257x-DKx_-_hardware_description#Boot_switches
 * `STM32MP157x-DKx` - https://wiki.st.com/stm32mpu/wiki/STM32MP157x-DKx_-_hardware_description#Boot_related_switches

4. Pass the `SD card` or `eMMC` Flash Layout file to the `STM32_Programmer_CLI` for programming the images to the boot media: `STM32_Programmer_CLI -c port=usb1 -w flashlayout_torizon-core-docker/optee/FlashLayout_<MEDIA>_<BOARD>-optee.tsv`, where `MEDIA` is one of `sdcard` or `emmc`, `BOARD` is one of `stm32mp257f-ev1`, `stm32mp257f-dk` or `stm32mp157f-dk2`. For example, use the following command to install Torizon to the SD card inserted to the STM32MP2 Discovery Kit board:
```
$ cd ~/yocto-workdir/build-stm32mp25-disco/deploy/images/stm32mp25-disco/
$ STM32_Programmer_CLI -c port=usb1 -w flashlayout_torizon-docker/optee/FlashLayout_sdcard_stm32mp257f-dk-optee.tsv
```

Boot
======

1. Power the target board off.
2. Set the boot mode for booting from `SD` card or `eMMC`.  Refer to the ST’s WiKi for the boot switches details for your board:
 * `STM32MP257X-EV1` - https://wiki.st.com/stm32mpu/wiki/STM32MP257x-EV1_-_hardware_description#Boot_related_switches
 * `STM32MP257x-DKx` - https://wiki.st.com/stm32mpu/wiki/STM32MP257x-DKx_-_hardware_description#Boot_switches
 * `STM32MP157x-DKx` - https://wiki.st.com/stm32mpu/wiki/STM32MP157x-DKx_-_hardware_description#Boot_related_switches
3. Power the target board on.
4. From the serial console terminal, monitor the target boot sequence:
```
NOTICE:  CPU: STM32MP257FAK Rev.Y
NOTICE:  Model: STMicroelectronics STM32MP257F-DK Discovery Board
NOTICE:  Board: MB1605 Var1.0 Rev.C-01
NOTICE:  Reset reason: Pin reset from NRST (0x2034)
INFO:    PMIC2 version = 0x11
INFO:    PMIC2 product ID = 0x20
INFO:    FCONF: Reading TB_FW firmware configuration file from: 0xe011000
INFO:    FCONF: Reading firmware configuration information for: stm32mp_fuse
INFO:    FCONF: Reading firmware configuration information for: stm32mp_io
INFO:    Using SDMMC
...
Common Torizon OS 7.0.0-devel-20250423143254+build.0 torizon-stm32mp25-disco ttySTM0

torizon-stm32mp25-disco login:

```
5. Login to the board using the `torizon/torizon` credentials.
