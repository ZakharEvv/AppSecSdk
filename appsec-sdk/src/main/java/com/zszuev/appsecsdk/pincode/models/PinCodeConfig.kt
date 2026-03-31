package com.zszuev.appsecsdk.pincode.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PinCodeConfig(
    // Основные настройки
    val pinLength: Int = 4,
    val title: String = "Введите PIN-код",
    val errorMessage: String = "Неверный PIN-код",
    val maxAttempts: Int = 5,
    val lockoutDurationSeconds: Int = 30,

    // Цвета
    val accentColor: Color = Color(0xFF6200EE),
    val backgroundColor: Color = Color(0xFFFFFFFF),
    val textColor: Color = Color(0xFF000000),
    val errorColor: Color = Color(0xFFE53935),

    // Типографика
    val titleFontSize: TextUnit = 22.sp,
    val keyFontSize: TextUnit = 24.sp,
    val fontFamily: FontFamily = FontFamily.Default,

    // Клавиатура
    val keyShape: KeyShape = KeyShape.Circle,
    val keySize: Dp = 72.dp,
    val keyBackgroundColor: Color? = null,

    // Индикатор точек
    val dotSize: Dp = 14.dp,
    val dotStyle: DotStyle = DotStyle.Filled,

    // Фон
    val backgroundGradient: List<Color>? = null,
    val backgroundImageResId: Int? = null,

    // Логотип
    val logoResId: Int? = null,
    val logoSize: Dp = 64.dp,
)

enum class KeyShape {
    Circle,
    Square,
    RoundedSquare,
}

enum class DotStyle {
    Filled,   // ●
    Outlined, // ○
    Dash,     // —
}