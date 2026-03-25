package com.zszuev.appsecsdk.biometric

data class BiometricConfig(
    val enabled: Boolean = true,
    val title: String = "Вход по биометрии",
    val subtitle: String = "Приложите палец для входа",
    val negativeButtonText: String = "Войти по PIN",
    val offerEnableTitle: String = "Использовать биометрию?",
    val offerEnableMessage: String = "Вы можете входить в приложение с помощью отпечатка пальца",
    val offerEnableConfirm: String = "Включить",
    val offerEnableDismiss: String = "Не сейчас",
)
