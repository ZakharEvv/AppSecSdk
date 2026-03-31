package com.zszuev.appsecsdk.pincode.models

sealed class PinCheckResult {
    object Success : PinCheckResult()
    data class Wrong(val attemptsLeft: Int) : PinCheckResult()
    data class LockedOut(val secondsLeft: Int) : PinCheckResult()
    data class Error(val message: String) : PinCheckResult()
}