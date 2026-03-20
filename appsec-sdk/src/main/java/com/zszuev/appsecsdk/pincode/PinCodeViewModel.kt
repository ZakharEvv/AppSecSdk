package com.zszuev.appsecsdk.pincode

import android.content.Context
import androidx.lifecycle.ViewModel
import com.zszuev.appsecsdk.SecuritySdk
import com.zszuev.appsecsdk.emulator.EmulatorDetector
import com.zszuev.appsecsdk.root.RootDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal class PinCodeViewModel : ViewModel() {

    private val _state = MutableStateFlow(PinCodeState())
    val state: StateFlow<PinCodeState> = _state

    var onSuccess: (() -> Unit)? = null

    fun init(context: Context) {
        val config = SecuritySdk.getConfig()
        val isPinSet = PinCodeManager.isPinSet(context)

        val isRooted = if (config.rootDetectionEnabled) RootDetector.isRooted() else false
        val isEmulator = if (config.emulatorDetectionEnabled) EmulatorDetector.isEmulator() else false

        val threatConfig = when {
            isRooted -> config.rootAlertConfig
            isEmulator -> config.emulatorAlertConfig
            else -> null
        }

        _state.update {
            it.copy(
                config = config.pinCodeConfig,
                mode = if (isPinSet) PinCodeMode.ENTER else PinCodeMode.SET,
                threatAlertConfig = threatConfig,
                isThreatRoot = isRooted,
            )
        }
    }

    fun onThreatConfirmed(onRootConfirmed: (() -> Unit)?, onEmulatorConfirmed: (() -> Unit)?) {
        val isRoot = _state.value.isThreatRoot
        _state.update { it.copy(threatAlertConfig = null) }
        if (isRoot) onRootConfirmed?.invoke() else onEmulatorConfirmed?.invoke()
    }

    fun onPinEntered(context: Context, pin: String) {
        when (_state.value.mode) {
            PinCodeMode.SET -> {
                _state.update { it.copy(
                    mode = PinCodeMode.CONFIRM,
                    pendingPin = pin,
                    errorMessage = null,
                ) }
            }
            PinCodeMode.CONFIRM -> {
                if (pin == _state.value.pendingPin) {
                    PinCodeManager.savePin(context, pin)
                    onSuccess?.invoke()
                } else {
                    _state.update { it.copy(
                        mode = PinCodeMode.SET,
                        pendingPin = null,
                        errorMessage = "PIN-коды не совпадают",
                    ) }
                }
            }
            PinCodeMode.ENTER -> {
                when (val result = PinCodeManager.checkPin(context, pin)) {
                    is PinCheckResult.Success -> onSuccess?.invoke()
                    is PinCheckResult.Wrong -> {
                        _state.update { it.copy(
                            errorMessage = "Неверный PIN. Осталось попыток: ${result.attemptsLeft}",
                        ) }
                    }
                    is PinCheckResult.LockedOut -> {
                        _state.update { it.copy(
                            errorMessage = null,
                            isLockedOut = true,
                            lockoutSecondsLeft = result.secondsLeft,
                        ) }
                    }
                    is PinCheckResult.Error -> {
                        _state.update { it.copy(errorMessage = result.message) }
                    }
                }
            }
        }
    }
}