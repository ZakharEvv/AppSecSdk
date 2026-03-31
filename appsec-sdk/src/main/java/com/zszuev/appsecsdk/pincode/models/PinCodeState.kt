package com.zszuev.appsecsdk.pincode.models

import com.zszuev.appsecsdk.ThreatAlertConfig

internal data class PinCodeState(
    val mode: PinCodeMode = PinCodeMode.ENTER,
    val config: PinCodeConfig = PinCodeConfig(),
    val pendingPin: String? = null,
    val errorMessage: String? = null,
    val isLockedOut: Boolean = false,
    val lockoutSecondsLeft: Int = 0,
    val threatAlertConfig: ThreatAlertConfig? = null,
    val isThreatRoot: Boolean = false,
    val showBiometricOffer: Boolean = false,
    val showBiometricPrompt: Boolean = false,
)