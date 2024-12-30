# Assign/change a config variable
# $1 - config variable to be set
# $2 - value [n/y/value]
# $3 - config file
#
kconfig_configure_variable() {
	# Remove the original config, to avoid reassigning it.
	sed -i -e "/CONFIG_$1[ =]/d" $3

	# Assign the config value
	if [ "$2" = "n" ]; then
		echo "# CONFIG_$1 is not set" >> $3
	else
		echo "CONFIG_$1=$2" >> $3
	fi
}

# Assign a config variable in ${B}/.config.
# Should be called in do_configure:append only.
#
# $1 - config variable to be set
# $2 - value [n/y/value]
#
kernel_configure_variable() {
	kconfig_configure_variable $1 $2 ${B}/.config
}
