# Fusing support inside uEnv.txt:
#
# - FUSE_SUPPORT_SIGNED_BUILD: internal usage (automatically set to "1" on tdx-signed builds).
# - FUSE_SUPPORT_DEFAULT: internal usage (default value of FUSE_SUPPORT)
# - FUSE_SUPPORT: whether or not to add support for fusing in uEnv.txt.
# - FUSE_SUPPORT_TYPE: type of support to add (allowed value: "NXP").
#
FUSE_SUPPORT_SIGNED_BUILD = "0"
FUSE_SUPPORT_SIGNED_BUILD:tdx-signed = "1"
FUSE_SUPPORT_DEFAULT = "0"
FUSE_SUPPORT ?= "${FUSE_SUPPORT_DEFAULT}"
FUSE_SUPPORT_TYPE = ""

# Machine specific values related to HAB/AHAB fusing (i.MX specific)
BANK_WORD = ""
FUSE_NUM = ""
STATUS_CMD = ""
CLOSE_CMD = ""

IMX6_COMMON_BANK_WORD = "\
    (3, 0), \
    (3, 1), \
    (3, 2), \
    (3, 3), \
    (3, 4), \
    (3, 5), \
    (3, 6), \
    (3, 7) \
"

IMX7_IMX8M_COMMON_BANK_WORD = "\
    (6, 0), \
    (6, 1), \
    (6, 2), \
    (6, 3), \
    (7, 0), \
    (7, 1), \
    (7, 2), \
    (7, 3) \
"

FUSE_NUM_8 = "1 2 3 4 5 6 7 8"
FUSE_NUM_16 = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16"

HAB_STATUS_CMD = "hab_status"
AHAB_STATUS_CMD = "ahab_status"

IMX6_COMMON_CLOSE_CMD = "fuse prog -y 0 6 0x00000002"
IMX7_IMX8M_COMMON_CLOSE_CMD = "fuse prog -y 1 3 0x02000000"
AHAB_CLOSE_CMD = "ahab_close"

BANK_WORD:mx6ull-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6ull-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6ull-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6ull-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx6ull-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx6ull-generic-bsp = "NXP"

BANK_WORD:mx6q-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6q-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6q-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6q-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx6q-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx6q-generic-bsp = "NXP"

BANK_WORD:mx6dl-generic-bsp = "${IMX6_COMMON_BANK_WORD}"
FUSE_NUM:mx6dl-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx6dl-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx6dl-generic-bsp = "${IMX6_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx6dl-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx6dl-generic-bsp = "NXP"

BANK_WORD:mx7-generic-bsp = "${IMX7_IMX8M_COMMON_BANK_WORD}"
FUSE_NUM:mx7-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx7-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx7-generic-bsp = "${IMX7_IMX8M_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx7-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx7-generic-bsp = "NXP"

BANK_WORD:mx8m-generic-bsp = "${IMX7_IMX8M_COMMON_BANK_WORD}"
FUSE_NUM:mx8m-generic-bsp = "${FUSE_NUM_8}"
STATUS_CMD:mx8m-generic-bsp = "${HAB_STATUS_CMD}"
CLOSE_CMD:mx8m-generic-bsp = "${IMX7_IMX8M_COMMON_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx8m-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx8m-generic-bsp = "NXP"

BANK_WORD:mx8qm-generic-bsp = "\
    (0, 722), \
    (0, 723), \
    (0, 724), \
    (0, 725), \
    (0, 726), \
    (0, 727), \
    (0, 728), \
    (0, 729), \
    (0, 730), \
    (0, 731), \
    (0, 732), \
    (0, 733), \
    (0, 734), \
    (0, 735), \
    (0, 736), \
    (0, 737) \
"
FUSE_NUM:mx8qm-generic-bsp = "${FUSE_NUM_16}"
STATUS_CMD:mx8qm-generic-bsp = "${AHAB_STATUS_CMD}"
CLOSE_CMD:mx8qm-generic-bsp = "${AHAB_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx8qm-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx8qm-generic-bsp = "NXP"

BANK_WORD:mx8x-generic-bsp = "\
    (0, 730), \
    (0, 731), \
    (0, 732), \
    (0, 733), \
    (0, 734), \
    (0, 735), \
    (0, 736), \
    (0, 737), \
    (0, 738), \
    (0, 739), \
    (0, 740), \
    (0, 741), \
    (0, 742), \
    (0, 743), \
    (0, 744), \
    (0, 745) \
"
FUSE_NUM:mx8x-generic-bsp = "${FUSE_NUM_16}"
STATUS_CMD:mx8x-generic-bsp = "${AHAB_STATUS_CMD}"
CLOSE_CMD:mx8x-generic-bsp = "${AHAB_CLOSE_CMD}"
FUSE_SUPPORT_DEFAULT:mx8x-generic-bsp = "${FUSE_SUPPORT_SIGNED_BUILD}"
FUSE_SUPPORT_TYPE:mx8x-generic-bsp = "NXP"
