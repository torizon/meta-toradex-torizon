meta-toradex-torizon
===========================================
Torizon OS is an embedded Linux distribution for the Torizon platform. It
features, among other essential services, a container runtime and components
for secure remote over-the-air (OTA) updates.

This layer provides the necessary metadata to build the Torizon OS Linux
distribution on top of Toradex's BSP Linux reference image as well as Common Torizon OS with third-party BSP.

Building Torizon OS
========
To build Torizon OS, see the following article:

https://developer.toradex.com/knowledge-base/build-torizoncore

Building Common Torizon OS
========
Download the BSP layers for your machine, the `meta-toradex-torizon` layer, and other layers that are required to build Common Torizon.
To build Common Torizon on the `scarthgap` branch:
```bash
$ git clone https://github.com/torizon/meta-toradex-torizon.git -b scarthgap-7.x.y
$ git clone https://github.com/uptane/meta-updater.git -b scarthgap
$ git clone https://git.yoctoproject.org/git/meta-virtualization -b scarthgap
```

Then, source whatever script your BSP uses to setup the build environment and edit your `conf/bblayers.conf` to add `meta-toradex-torizon` layer and its dependencies (and any other that might be missing).

After that, edit your `conf/local.conf` file to set the MACHINE you want to build (if not set already), and change the DISTRO to `DISTRO='common-torizon'`.

Start building one of the available Torizon images:
* torizon-docker
* torizon-minimal
* torizon-podman (**experimental**)

Below you'll find documentation for some devices to which Common Torizon was already ported, with more details on how to fetch, setup and build them.

* [Texas Instruments AM62x SK EVM](./docs/README-ti.md)
* [Toradex i.MX95 Verdin EVK](./docs/README-imx95.md)
* [Renesas RZ/V2L EVKIT](./docs/README-rzv2l.md)
* [Intel x86](./docs/README-x86.md)

Reporting Issues
================
If you find any problems when using Torizon, feel free to open a new issue here on GitHub or create a new Technical Support topic on the Toradex Developer Community: https://community.toradex.com/.

Contributing
============
You may also choose to actively correct issues/bugs or possibly port Common Torizon on new devices. For more details, see [CONTRIBUTING.md](./docs/CONTRIBUTING.md).

Development Process
===================
Torizon is maintained by the Toradex R&D team. Development now happens directly on this repository, including issues, PRs, and discussions. We have an internal structure where we run our CI/CD pipelines.

We also track TOR issues, bugs and features internally. Because of this, some of the commits and PRs made by members of the team may contain references to internal ticket identifiers e.g. 'Related-to: TOR-3705'.

License
=======
All metadata is MIT licensed unless otherwise stated. Source code and
binaries included in tree for individual recipes is under the LICENSE
stated in each recipe (.bb file) unless otherwise stated.

This README document is Copyright (C) 2019 Toradex AG.
