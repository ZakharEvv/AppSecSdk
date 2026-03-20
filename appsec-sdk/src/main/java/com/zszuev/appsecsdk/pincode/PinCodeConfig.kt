package com.zszuev.appsecsdk.pincode

import androidx.compose.ui.graphics.Color

data class PinCodeConfig(
    val pinLength: Int = 4,
    val title: String = "Введите PIN-код",
    val errorMessage: String = "Неверный PIN-код",
    val accentColor: Color = Color(0xFF6200EE),
    val backgroundColor: Color = Color(0xFFFFFFFF),
    val textColor: Color = Color(0xFF000000),
    val maxAttempts: Int = 5,
    val lockoutDurationSeconds: Int = 30,
)