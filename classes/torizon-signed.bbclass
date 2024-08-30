python() {
    bb.fatal("The rootfs protection provided by torizon-signed requires "
             "Torizon OS 7 or newer (i.e. the torizon-signed class "
             "functionality is not available on kirkstone).")
}
