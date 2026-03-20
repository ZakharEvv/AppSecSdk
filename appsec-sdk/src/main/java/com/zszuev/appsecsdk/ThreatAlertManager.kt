package com.zszuev.appsecsdk

object ThreatAlertManager {

    internal var onRootConfirmed: (() -> Unit)? = null
    internal var onEmulatorConfirmed: (() -> Unit)? = null

    fun setCallbacks(
        onRootConfirmed: (() -> Unit)? = null,
        onEmulatorConfirmed: (() -> Unit)? = null,
    ) {
        this.onRootConfirmed = onRootConfirmed
        this.onEmulatorConfirmed = onEmulatorConfirmed
    }
}