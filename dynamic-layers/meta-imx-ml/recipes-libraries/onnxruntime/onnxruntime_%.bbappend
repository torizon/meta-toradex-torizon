# Replace -mcpu=cortex-a55 (with or without +crypto) with the equivalent
# -march=armv8.2-a in TUNE_CCARGS.
# For GCC it is a conflict it can't resolve when we pass different -mcpu and -march, so it ends up
# failing.
# With this change, we replace -mcpu for its equivalent -march, and the compiler can resolve it without issues.
TUNE_CCARGS:remove = "-mcpu=cortex-a55+crypto"
TUNE_CCARGS:remove = "-mcpu=cortex-a55"
TUNE_CCARGS:append = "${@bb.utils.contains('TUNE_FEATURES', 'cortexa55', ' -march=armv8.2-a', '', d)}"
