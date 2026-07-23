Build-environment setup scripts: architecture and how to add a vendor
======================================================================


Overview
--------

```
scripts/setup-environment                          <- dispatcher, sourced by the user
scripts/lib/
  core.sh                                           <- shared tdx_* function library
  machines.conf                                     <- MACHINE -> VENDOR -> DISTRO_POLICY table
  distros.conf                                       <- top-level DISTRO menu (id + description)
  setup-devices/
    setup-environment-toradex                       <- one real vendor script per SoC vendor
    setup-environment-ti
    setup-environment-intel
    setup-environment-imx
    setup-environment-nvidia
    setup-environment-renesas
    setup-environment-stm32mp
    setup-environment-synaptics
    setup-environment-TEMPLATE                      <- copy this to add a new vendor
```

The dispatcher resolves the interactive MACHINE/DISTRO menus (or reads
`$MACHINE`/`$DISTRO` from the environment), looks up the chosen MACHINE's
vendor in `machines.conf`, and sources the matching
`setup-environment-<vendor>` script. Everything these scripts do -
`source`d into the user's live interactive shell, never executed as a
subprocess - so every function in `core.sh` uses `local` for its own
bookkeeping, never calls `exit` (only `return`), and never `cd`s except the
two functions whose documented job is precisely to do that
(`tdx_set_builddir`, and `tdx_compute_oeroot`'s own re-`cd`).

Data files
----------

**`scripts/lib/machines.conf`** - one row per MACHINE, whitespace-delimited:

```
MACHINE  VENDOR  BSP_DISPLAY  DISTRO_POLICY
```

- `MACHINE` - the value the user sets/selects as `$MACHINE`.
- `VENDOR` - which `setup-environment-<VENDOR>` script the dispatcher sources.
- `BSP_DISPLAY` - free-form text shown next to MACHINE in the interactive menu (no whitespace).
- `DISTRO_POLICY` - how the dispatcher treats `$DISTRO` before handing off: `-` (vendor script decides), `=<value>` (force), `?<value>` (default only if unset).
 
**`scripts/lib/distros.conf`** - the top-level DISTRO picker menu, whitespace-delimited:
```
id  description
```
Read by both `tdx_usage` (for `-h`/no-dialog-tool output) and the dispatcher's whiptail/dialog menu, so the two can never drift out of sync.

`core.sh` API tour
------------------

Grouped by concern - see the function's own doc comment in `core.sh` for full parameter details.

- **Bootstrap**: `tdx_compute_oeroot`, `tdx_set_builddir`, `tdx_set_builddir_from_cwd`
- **Table lookups**: `tdx_vendor_for_machine`, `tdx_distro_policy_for_machine`, `tdx_apply_distro_policy`, `tdx_menu_pairs`, `tdx_distro_pairs`, `tdx_usage`
- **PATH/env**: `tdx_setup_path`, `tdx_dedupe_sort`, `tdx_setup_bb_env_passthrough`
- **Native conf generation**: `tdx_native_conf_is_current`, `tdx_write_checksum`, `tdx_conf_from_template`, `tdx_write_auto_conf`, `tdx_write_site_conf`
- **Integration-build detection**: `tdx_is_integration_build`, `tdx_apply_integration_override`
- **Once-only append**: `tdx_needs_once`, `tdx_append_once`
- **EULA/license**: `tdx_license_flag_accept`, `tdx_eula_prompt`, `tdx_append_license_flags`
- **Banner**: `tdx_print_banner`
- **Cleanup**: `tdx_cleanup_internal_state` - unsets every internal helper variable/function via a fixed allowlist once the dispatcher's whole invocation returns, so nothing but `MACHINE`/`DISTRO`/`SDKMACHINE`/`BUILDDIR`/`PATH`/`BB_ENV_PASSTHROUGH_ADDITIONS`/`_TDX_OEROOT` and the public `tdx_*` functions survive in the user's shell.

Two vendor-script families
---------------------------

- **Native family** (`toradex`, `nvidia`, `stm32mp`): the script builds `conf/local.conf`/`conf/bblayers.conf`/`conf/auto.conf`/`conf/site.conf` itself, with checksum-gated caching via `tdx_native_conf_is_current` so re-sourcing with unchanged MACHINE/DISTRO is a fast no-op.
- **Delegating family** (`ti`, `intel`, `imx`, `synaptics`): the script `source`s the vendor's own env-setup tooling first (poky's `oe-init-build-env`, NXP's `imx-setup-release.sh`, etc.), then patches the result with `tdx_needs_once`/`tdx_append_once`/`tdx_apply_integration_override`.
- `renesas` is a documented special case: it assumes a container/kas-based flow has already created a base `/build/conf/*`, so it skips the bootstrap functions entirely and just patches those fixed paths.

How to add a new vendor
------------------------

1. Copy `scripts/lib/setup-devices/setup-environment-TEMPLATE` to `scripts/lib/setup-devices/setup-environment-<vendor>` and fill in every `# TODO:` - it walks through the native-family shape step by step (`tdx_set_builddir` → `tdx_setup_path` → `tdx_setup_bb_env_passthrough` → `tdx_native_conf_is_current` early-return gate → `tdx_write_checksum` → `tdx_conf_from_template` + `tdx_append_once` for bblayers.conf → manifest README symlink → `tdx_write_auto_conf` → `tdx_apply_integration_override` → `tdx_write_site_conf` → optional `tdx_eula_prompt` → `tdx_print_banner`). If your vendor ships its own env-setup tooling instead, read `setup-environment-ti`/`-intel`/`-imx`/`-synaptics` for the delegating-family pattern instead - the template's header comment points to these too.
2. Add a MACHINE row (or block of rows) for the new vendor to `scripts/lib/machines.conf`.

No changes to `scripts/setup-environment` or `core.sh` are needed for either step.
