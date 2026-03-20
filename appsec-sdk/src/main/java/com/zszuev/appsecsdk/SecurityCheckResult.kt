package com.zszuev.appsecsdk

data class SecurityCheckResult(
    val isRooted: Boolean,
    val isEmulator: Boolean,
) {
    val isDeviceTrusted: Boolean
        get() = !isRooted && !isEmulator
}