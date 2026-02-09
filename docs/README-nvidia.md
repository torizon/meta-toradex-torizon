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
6. Download the `meta-tegra` layer:
```
$ git -C layers clone -b scarthgap https://github.com/OE4T/meta-tegra.git
```

Build
======
1. Use the Docker container provided by Toradex to setup the build environment in the work directory `~/yocto-workdir` prepared in previous steps:
```
$ docker run --rm -it --name=crops -v ~/yocto-workdir:/workdir --workdir=/workdir torizon/crops:scarthgap-7.x.y /bin/bash
```
2. Repeat the step of configuring the Git user name and e-mail:
```
$$ git config --global user.email "you@example.com"
$$ git config --global user.name "Your Name"
```
3. In the Docker console set up the environment for the Jetson Orin Nano DevKit: `MACHINE=<MACHINE> source setup-environment [BUILDDIR]`, where `MACHINE` is one of the following:
 * `jetson-orin-nano-devkit` - Jetson Orin Nano DevKit with SD card boot option
 * `jetson-orin-nano-devkit-nvme` - Jetson Orin Nano DevKit with NVME SSD boot option

`BUILDDIR` is the directory where you would like to to store the build files. For example:
```
$$ MACHINE=jetson-orin-nano-devkit-nvme source setup-environment build-jetson-orin-nano
```
4. Build the Torizon images:
```
$$ bitbake torizon-docker
```

Flash the Device
======
1. When build is complete the `tegraflash` image shall appear in the deploy directory. This is the tarball containing the NVIDIA installer for the Tegra platform (which is a special build of Linux with initrd to be launched on the target board via USB), the bootloader image to be installed to the QSPI flash and the Ostree-based Torizon image to be installed to SSD or SD-card depending on the selected configuration. To launch the NVIDIA installer, unpack the tegraflash tarball and execute the `doflash.sh` script from it. Suggest to automate this process as follows.

2. Save the following script to `deploy.sh` in the working directory with the Torizon build:
```
#!/bin/bash

image=$1
machine=$2

scriptdir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
deployfile=${image}-${machine}.tegraflash.tar.gz
tmpdir=`mktemp`

rm -rf $tmpdir
mkdir -p $tmpdir
echo "Using temp directory $tmpdir"
pushd $tmpdir
cp $scriptdir/build-jetson-orin-nano/deploy/images/${machine}/$deployfile .
tar -xvf $deployfile
set -e
sudo ./doflash.sh
popd
echo "Removing temp directory $tmpdir"
rm -rf $tmpdir
```

3. Connect the USB port to the host machine and put the target board into the recovery mode: short the `FORCE_RECOVERY` pin (labeled as `FC_REC` on the carrier board) to the ground, this can be done by installing a wired jumper to the contacts `9-10` of the `J50` `button` header on the carrier board (refer to the Jetson Orin Nano Developer Kit Carrier Board Specification), then power on the board.

4. Run the deployment script:
```
$ sudo ./deploy.sh <IMAGE> <MACHINE>
```
for example:
```
$ sudo ./deploy.sh torizon-docker jetson-orin-nano-devkit-nvme
```

Boot
======
1. Use the TTL to USB converter cable to connect the serial console (refer to JetsonHacks video: https://www.youtube.com/watch?v=Kwpxhw41W50).

2. Remove the recovery mode jumper and power the target board on.

3. From the serial console terminal, monitor the target boot sequence:
```
Jetson System firmware version v36.4.4 date 1970-01-01T00:00:00+00:00
ESC   to enter Setup.
...
Common Torizon OS 7.5.0-devel-20251205093706+build.0 jetson-orin-nano-devkit-nvme ttyTCU0

jetson-orin-nano-devkit-nvme login:

```
4. Login to the board using the `torizon/torizon` credentials.

Run NVIDIA docker
======

The NVIDIA container runtime is included in the `jetson-orin-nano-devkit` and `jetson-orin-nano-devkit-nvme` configurations for Torizon.

Use `docker run --runtime nvidia` to run containerized NVIDIA applications. For example, to run the `stable-diffusion` tutorial from the NVIDIA Jetson AI Lab (https://www.jetson-ai-lab.com/archive/tutorial_stable-diffusion.html) use the following command in Torizon:

```
sudo docker run --runtime nvidia -it --rm --network=host dustynv/stable-diffusion-webui:r36.2.0
```
