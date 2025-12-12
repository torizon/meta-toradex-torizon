# NOTE: 
#
# This code was originally located in torizon_base_image_type.inc, which is included 
# by the class image_type_torizon.bbclass. As the class is added to IMAGE_CLASSES, it is
# parsed by every image recipe several times. There are 100+ image recipes in the layers
# used by the torizon distro. When building the torizon-minimal image, the function get_layer_revision_information
# was executed 350+ times in the step "Parsing recipes". Parsing took 10 minutes longer than normal.
#
# The function get_layer_revision_information executes the same information for every image
# recipe. For every layer repository, it calculates the commit sha. The commit sha's don't 
# change during a bitbake run. In short, get_layer_revision_information must only be executed
# once for every build.
#
# By inheriting ostree_layer_revision_info (this class) in torizon-base.inc, which is required 
# by the torizon image, the function get_layer_revision_information is executed exactly once.
#
# See also https://community.toradex.com/t/parsing-recipes-takes-10-minutes-longer-due-to-get-layer-revision-information/28529

# Get git hashes from all layers and print it as GLib g_variant_parse() string
# inspiration taken from image-buildinfo
def get_layer_revision_information(d):
    import bb.process
    import subprocess
    try:
        layers = []
        paths = (d.getVar("BBLAYERS" or "")).split()

        for path in paths:
            # Use relative path from ${OEROOT}/layers/ as layer name
            name = os.path.relpath(path, os.path.join(d.getVar('OEROOT'), "layers"))
            rev, _ = bb.process.run('export PSEUDO_UNLOAD=1; git rev-parse HEAD', cwd=path)
            branch, _ = bb.process.run('export PSEUDO_UNLOAD=1; git rev-parse --abbrev-ref HEAD', cwd=path)
            try:
                subprocess.check_output("""cd %s; export PSEUDO_UNLOAD=1; set -e;
                                        git diff --quiet --no-ext-diff
                                        git diff --quiet --no-ext-diff --cached""" % path,
                                        shell=True,
                                        stderr=subprocess.STDOUT)
                modified = ""
            except subprocess.CalledProcessError as ex:
                modified = ":modified"
            # Key/value pair per layer
            layers.append("'{}': '{}:{}{}'".format(name, branch.strip(), rev.strip(), modified))

        # Create GLib dictionary
        return "{" + ",".join(layers) + "}"
    except Exception as e:
        bb.warn("Failed to get layers information. Caused by layer at {}. Exception: {}".format(path, e))

# Use immediate expansion here to avoid calling a somewhat costly function whenever
# EXTRA_OSTREE_COMMIT is expanded.
OSTREE_LAYER_REVISION_INFO := "${@get_layer_revision_information(d)}"
