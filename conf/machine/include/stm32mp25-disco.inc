require common-stm32mp.inc

hostname:pn-base-files = "torizon-stm32mp25-disco"

# Enable build eMMC images for the Disco target
BOOTDEVICE_LABELS:append:stm32mp25-disco = " emmc"
STM32MP_DT_FILES_EMMC:stm32mp25-disco    += "stm32mp257f-dk"
