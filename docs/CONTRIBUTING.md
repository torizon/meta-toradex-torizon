How to Contribute
===========================================
This document details guidelines and common practices which should be followed when contributing code to this repository, whether the person is part of the Toradex team or an external contributor.

Code Contributor Workflow
========
- Make a fork of this repository;
- Create a new development branch in it;
- Make your changes in the new branch and commit them;
- Open a pull request to the default branch (currently `scarthgap-7.x.y`).
- Open a second pull request to the `master` branch, unless not applicable.

Commit Guidelines
========
All commits must be signed-off i.e. have a 'Signed-off-by' line at the end of their messages, similar to this example:
```
Update README.md

Signed-off-by: Your Name <your-e-mail@your-provider.com>
```

This line can be added automatically with the `-s` option of `git commit`. It certifies the authorship of your own commit or that you have the right to pass it under the same license of this repository, as stated in https://developercertificate.org/ .

Commit messages should generally follow the format below:
```
scope: brief one line description with up to 72 characters

[optional] Detailed description, with multiple lines. Each one should
have up to 72 characters.

Signed-off-by: Your Name <your-e-mail@your-provider.com>
```

`scope` can be a specific file or recipe being changed (e.g. `images/torizon-base.inc`, `u-boot-distro-boot`) or a general idea of where the change is if multiple files/recipes are changed, such as `intel-corei7-64` or `conf/distro`.

The first line can also be a single sentence, usually starting with a verb in the imperative present tense, that describes a general change to the code if dealing with multiple files e.g.
```
Show spinner loading animation when processing raw images
```

Or if simple changes are done to a single file e.g:
```
Add README.md
```
