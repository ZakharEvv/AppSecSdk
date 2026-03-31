package com.zszuev.appsecsdk

import com.zszuev.appsecsdk.biometric.BiometricConfig
import com.zszuev.appsecsdk.pincode.models.PinCodeConfig

data class SdkConfig(
    val pinCodeConfig: PinCodeConfig = PinCodeConfig(),
    val biometricConfig: BiometricConfig = BiometricConfig(),
    val screenshotProtectionEnabled: Boolean = true,
    val clipboardProtectionEnabled: Boolean = true,
    val rootDetectionEnabled: Boolean = true,
    val emulatorDetectionEnabled: Boolean = true,
    val inactivityTimeoutSeconds: Int = 60,
    val rootAlertConfig: ThreatAlertConfig = ThreatAlertConfig(
        title = "Устройство скомпрометировано",
        message = "Обнаружен root-доступ. Использование приложения небезопасно.",
        buttonText = "Понятно",
    ),
    val emulatorAlertConfig: ThreatAlertConfig = ThreatAlertConfig(
        title = "Виртуальное устройство",
        message = "Запуск на эмуляторе не поддерживается.",
        buttonText = "Понятно",
    ),
) {
    class Builder {
        var pinCodeConfig: PinCodeConfig = PinCodeConfig()
        var biometricConfig: BiometricConfig = BiometricConfig()
        var screenshotProtectionEnabled: Boolean = true
        var clipboardProtectionEnabled: Boolean = true
        var rootDetectionEnabled: Boolean = true
        var emulatorDetectionEnabled: Boolean = true
        var inactivityTimeoutSeconds: Int = 60
        var rootAlertConfig: ThreatAlertConfig = ThreatAlertConfig(
            title = "Устройство скомпрометировано",
            message = "Обнаружен root-доступ. Использование приложения небезопасно.",
            buttonText = "Понятно",
        )
        var emulatorAlertConfig: ThreatAlertConfig = ThreatAlertConfig(
            title = "Виртуальное устройство",
            message = "Запуск на эмуляторе не поддерживается.",
            buttonText = "Понятно",
        )

        fun build() = SdkConfig(
            pinCodeConfig = pinCodeConfig,
            biometricConfig = biometricConfig,
            screenshotProtectionEnabled = screenshotProtectionEnabled,
            clipboardProtectionEnabled = clipboardProtectionEnabled,
            rootDetectionEnabled = rootDetectionEnabled,
            emulatorDetectionEnabled = emulatorDetectionEnabled,
            inactivityTimeoutSeconds = inactivityTimeoutSeconds,
            rootAlertConfig = rootAlertConfig,
            emulatorAlertConfig = emulatorAlertConfig,
        )
    }
}