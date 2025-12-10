Setup
======
1. If you don't have the `repo` tool installed, please refer to [Build Torizon OS From Source with Yocto Project](https://developer.toradex.com/torizon/in-depth/build-torizoncore-from-source-with-yocto-projectopenembedded/#download-metadata).
2. Initialize and sync the repo manifest for Synaptics:
```bash
$ mkdir common-torizon; cd common-torizon
$ repo init -u git://git.toradex.com/toradex-manifest.git -b scarthgap-7.x.y -m common-torizon/syn/integration.xml
$ repo sync -j 10
```

> [!IMPORTANT]
> Until an official release of Common Torizon OS, only the `integration.xml` manifest is suitable for end-users to build. After an official release, users will be able to use the `default.xml` manifest.

Note that `integration.xml` is a development manifest used internally and it might contain development features and thus be considered unstable.

> [!IMPORTANT]
> Common Torizon OS is only available on branches `scarthgap-7.x.y` or newer!

Alternatively, you can manually clone all layers one by one. Refer to the section [_Manual Setup_](#manual-setup) at the end of this document to learn how.

Build Environment
======
You can use Synaptics Crops container to also build Common Torizon
```bash
$ docker run --rm -it --name=syn-crops-common -v <path/to/common-torizon>:/workdir --workdir=/workdir ghcr.io/synaptics-astra/crops:1.1.0
```

Build
======

| Board | MACHINE | Status |
|---|---|---|
| Winglet | winglet | Supported  |
| Astra SL1680 | sl1680 | Supported |

1. Source `setup-environment`, specifying the machine to build with the MACHINE variable e.g.:
```bash
$ MACHINE=sl1680 . setup-environment <build-directory>
```
If a build directory is not given, the script will create one named `build-${DISTRO}`, where all build artifacts will be stored. By default `DISTRO` is automatically set to `common-torizon` with the Synaptics boards.

2. Inside the build directory, start the build e.g.:
```bash
$ bitbake torizon-docker
```
All artifacts should be inside `<build-directory>/deploy/images/${MACHINE}`, including the `SYNAIMG` folder.

Recovery Mode
======
#### Astra SL1680
On the device, press the `USB-Boot` button, and while holding it down either power on the device or press the `RESET` button.

#### Winglet
To the right of the USB-C connector there is a jumper, that's the board `USB-Boot` jumper. Power off the device on the `Power Switch`, short this jumper and then power it on again.
After the flashing is done, remove the short on that jumper so the device can boot normally.

Flashing eMMC
======
For flashing, there are two possible ways: use the helper script `flash-image.sh` or doing the steps manually.

> [!WARNING]  
> This helper script does NOT yet work with the Winglet board, it only works with the Astra SL1680 board.

1. The easiest way is to use the helper script:

    a. After the build is done, get the file called `SYNAIMG-flash.tar.gz` from the `deploy/images/${MACHINE}` folder.  
    b. Unpack it into a proper folder, using the tar command: `tar -xf SYNAIMG-flash.tar.gz`.  
    c. With the board in recovery mode, execute the script: `./flash-image.sh`.  

2. Another option is to do all the steps manually:

    a. For flashing, you'll need the latest release of Synaptics' `usb-tool`. Please refer to their [usb-tool GitHub repository](https://github.com/synaptics-astra/usb-tool/releases) and download the latest `astra-update`.
    On `Winglet`, make sure to check the _important note_ at the bottom of this section.

    b. Unpack its contents somewhere, go into that folder and copy `SYNAIMG` from the generated artifacts of your build.
    ```bash
    $ cd usb-tool
    $ cp -r <build-directory>/deploy/images/${MACHINE}/SYNAIMG SYNAIMG
    ```
     * Or make a symlink to it
    ```bash
    $ cd usb-tool
    $ ln -s <build-directory>/deploy/images/${MACHINE}/SYNAIMG SYNAIMG
    ```
    c. Grab the manifest ID:
    ```bash
    $ cat astra-usbboot-images/sl1680_suboot/manifest.yaml | grep ^id:
    ```
    d. Run `astra-update`
    ```bash
    $ sudo ./bin/linux/x86_64/astra-update -c sl1680 -m 4gb -d lpddr4x -t emmc -i <manifest ID from previous step>
    ```
    Or, if you like you could make it in one line using `shyaml`
    ```bash
    $ pip install shyaml
    $ sudo ./bin/linux/x86_64/astra-update -c sl1680 -m 4gb -d lpddr4x -t emmc -i "$(cat astra-usbboot-images/sl1680_suboot/manifest.yaml | shyaml get-value id)"
    ```

> [!IMPORTANT]
> For `Winglet`, you'll need to replace the folder `astra-usbboot-images` with the one provided here.
> Just rename the current one something else, for instance `astra-usbboot-images-sl1680` and add the provided `astra-usbboot-images` in `usb-tool`
> And if you'll need to flash Astra SL1680, you'll need to switch back to the original binaries, so keep that in mind!
> TODO: Make Winglet specific binaries available

Manual Setup
======
1. Create the directory structure for the Yocto layers e.g.:
```bash
$ mkdir common-torizon; cd common-torizon
```
2. Clone the necessary layers to build the Common Torizon image for the Synaptics boards:
  * Download Synaptics SDK:
```bash
$ git clone https://github.com/synaptics-astra/sdk.git -b scarthgap_6.12_v2.0.0 layers
```
  * Download `meta-toradex-torizon` and its dependencies:
```bash
$ cd layers
$ git clone https://github.com/torizon/meta-toradex-torizon.git -b scarthgap-7.x.y
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap
```
  * Go back to our top folder `common-torizon`;
  * Create a symlink to our `setup-environment`:
```bash
$ ln -s layers/meta-toradex-torizon/scripts/setup-environment setup-environment
```
