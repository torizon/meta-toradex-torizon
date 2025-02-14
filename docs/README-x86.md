Sync
======
1. Create your build folder structure. Something like:
```bash
$ mkdir common-torizon; cd common-torizon
$ mkdir layers; cd layers
```
2. Clone the layers needed to build x86 Common Torizon:  
  * Download Poky
```bash
$ git clone git://git.yoctoproject.org/poky -b scarthgap
```
  * Download `meta-intel` and `meta-toradex-torizon`:
```bash
$ git clone git://git.yoctoproject.org/meta-intel -b scarthgap
$ git clone https://github.com/torizon/meta-toradex-torizon.git -b scarthgap-7.x.y
```
  * Download `meta-toradex-torizon` dependencies:
```bash
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap
$ git clone https://git.yoctoproject.org/meta-virtualization -b scarthgap
```
  * And finally, download `meta-updater` and `meta-virtualization` dependency: 
```bash
$ git clone https://github.com/openembedded/meta-openembedded -b scarthgap
```
  * Go back into our top folder `common-torizon`
  * Create a symlink to our `setup-environment`:
```bash
$ ln -s layers/meta-toradex-torizon/scripts/setup-environment setup-environment
```

Repo Sync
======
1. If you dont have `repo` tool installed, please refer to this [link](https://developer.toradex.com/torizon/in-depth/build-torizoncore-from-source-with-yocto-projectopenembedded/#download-metadata).
2. Start syncing the manifest
```bash
$ repo init -u git://git.toradex.com/toradex-manifest.git -b scarthgap-7.x.y -m common-torizon/x86/default.xml
$ repo sync -j 10
```
We **strongly recommend** using the `default.xml` manifest. We also have there `integration.xml` and `next.xml`, but these are development manifests used internally and they might be unstable.
`default.xml` is the manifest used for our releases, so they are reliable.  
Since we're now using the manifest, Common Torizon `setup-script` link will be automatically created for you on your top folder.
> [!IMPORTANT]  
> Common Torizon OS is only available on branches `scarthgap-7.x.y` or newer!

Build
======
1. Source `setup-environment` to setup build:
```bash
$ MACHINE=intel-corei7-64 . setup-environment build-corei7-64
```
This will create a build folder named `build-corei7-64`, where all build artifacts will be stored.
2. Start Common Torizon build:
```bash
$ bitbake torizon-docker
```

All artifacts should be inside `build-corei7-64/deploy/images/intel-corei7-64`, including the `.wic` file.

Testing on Virtual Box
======
You could easily setup a virtual machine to test the image built, using the generated `.wic.vdi` or `.wic.vmdk`.

Below is a script for setting up a virtual machine inside Virtual Box, called `Common-Torizon`:
```bash
# Setup new machine Common-Torizon
VBoxManage createvm --name Common-Torizon --ostype "Linux_64" --register --basefolder "$HOME/CommonTorizonVBoxVM"
VBoxManage modifyvm Common-Torizon --firmware efi64
# Attempt at serial connection (cant communicate but can inspect serial logs via 'tail -f /tmp/serial')
VBoxManage modifyvm Common-Torizon --cpus 2 --memory 2048 --vram 256 --graphicscontroller vmsvga --uart1 0x3F8 4 --uartmode1 file /tmp/serial

VBoxManage storagectl Common-Torizon --name "SATA Controller" --add sata --bootable on
VBoxManage storageattach Common-Torizon --storagectl "SATA Controller" --port 0 --device 0 --type hdd --medium "torizon-docker-intel-corei7-64.wic.vdi"
# Port forwarding for SSH on port 2222
VBoxManage modifyvm Common-Torizon --nat-pf1=ssh,tcp,,2222,,3791 --nat-pf1=ssh_tor,tcp,,2223,,22

VBoxManage startvm Common-Torizon
```

Or you could run using QEmu:
```bash
$ qemu-system-x86_64 -drive if=virtio,file=torizon-docker-intel-corei7-64.wic,format=raw -no-reboot -cpu host -nic user,hostfwd=tcp::2222-:22 -machine pc -vga virtio -m 4096 -bios /usr/share/ovmf/OVMF.fd -enable-kvm -serial pty
```
