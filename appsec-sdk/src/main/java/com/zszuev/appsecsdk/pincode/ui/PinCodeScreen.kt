package com.zszuev.appsecsdk.pincode.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zszuev.appsecsdk.ThreatAlertConfig
import com.zszuev.appsecsdk.pincode.models.DotStyle
import com.zszuev.appsecsdk.pincode.models.KeyShape
import com.zszuev.appsecsdk.pincode.models.PinCodeConfig
import com.zszuev.appsecsdk.pincode.models.PinCodeMode
import kotlinx.coroutines.delay

@Composable
internal fun PinCodeScreen(
    mode: PinCodeMode,
    config: PinCodeConfig,
    onPinEntered: (String) -> Unit,
    errorMessage: String? = null,
    isLockedOut: Boolean = false,
    lockoutSecondsLeft: Int = 0,
    threatAlertConfig: ThreatAlertConfig? = null,
    isThreatRoot: Boolean = false,
    onThreatConfirmed: () -> Unit = {},
) {
    var pin by remember { mutableStateOf("") }
    var lockoutSeconds by remember { mutableStateOf(lockoutSecondsLeft) }

    LaunchedEffect(mode) { pin = "" }

    LaunchedEffect(isLockedOut) {
        if (isLockedOut) {
            lockoutSeconds = lockoutSecondsLeft
            while (lockoutSeconds > 0) {
                delay(1000)
                lockoutSeconds--
            }
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            delay(500)
            pin = ""
        }
    }

    val title = when (mode) {
        PinCodeMode.SET -> "Создайте PIN-код"
        PinCodeMode.CONFIRM -> "Подтвердите PIN-код"
        PinCodeMode.ENTER -> config.title
    }

    // Фон — градиент, изображение или цвет
    val backgroundModifier = when {
        config.backgroundImageResId != null -> Modifier.fillMaxSize()
        config.backgroundGradient != null -> Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(config.backgroundGradient))
        else -> Modifier
            .fillMaxSize()
            .background(config.backgroundColor)
    }

    Box(modifier = backgroundModifier) {

        // Фоновое изображение
        config.backgroundImageResId?.let { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Логотип
                config.logoResId?.let { resId ->
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Logo",
                        modifier = Modifier.size(config.logoSize),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Заголовок
                Text(
                    text = title,
                    fontSize = config.titleFontSize,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = config.fontFamily,
                    color = config.textColor,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Статус / ошибка
                when {
                    isLockedOut && lockoutSeconds > 0 -> Text(
                        text = "Попробуйте через $lockoutSeconds сек.",
                        fontSize = 14.sp,
                        color = config.errorColor,
                    )
                    errorMessage != null -> Text(
                        text = errorMessage,
                        fontSize = 14.sp,
                        color = config.errorColor,
                    )
                    else -> Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Точки индикатора
            PinDotsRow(
                pinLength = config.pinLength,
                filledCount = pin.length,
                accentColor = config.accentColor,
                errorColor = config.errorColor,
                dotSize = config.dotSize,
                dotStyle = config.dotStyle,
                hasError = errorMessage != null,
            )

            // Клавиатура
            PinKeyboard(
                enabled = !isLockedOut || lockoutSeconds == 0,
                onDigit = { digit ->
                    if (pin.length < config.pinLength) {
                        pin += digit
                        if (pin.length == config.pinLength) onPinEntered(pin)
                    }
                },
                onDelete = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
                accentColor = config.accentColor,
                textColor = config.textColor,
                keyShape = config.keyShape,
                keySize = config.keySize,
                keyBackgroundColor = config.keyBackgroundColor
                    ?: config.accentColor.copy(alpha = 0.1f),
                fontFamily = config.fontFamily,
                keyFontSize = config.keyFontSize,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (threatAlertConfig != null) {
            ThreatAlertDialog(
                config = threatAlertConfig,
                isRoot = isThreatRoot,
                onConfirmed = onThreatConfirmed,
            )
        }
    }
}

@Composable
private fun PinDotsRow(
    pinLength: Int,
    filledCount: Int,
    accentColor: Color,
    errorColor: Color,
    dotSize: androidx.compose.ui.unit.Dp,
    dotStyle: DotStyle,
    hasError: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pinLength) { index ->
            val isFilled = index < filledCount
            val dotColor by animateColorAsState(
                targetValue = when {
                    hasError -> errorColor
                    isFilled -> accentColor
                    else -> accentColor.copy(alpha = 0.3f)
                },
                animationSpec = tween(200),
                label = "dotColor",
            )
            val animatedSize by animateDpAsState(
                targetValue = if (isFilled) dotSize + 2.dp else dotSize,
                animationSpec = tween(150),
                label = "dotSize",
            )

            when (dotStyle) {
                DotStyle.Filled -> Box(
                    modifier = Modifier
                        .size(animatedSize)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                DotStyle.Outlined -> Box(
                    modifier = Modifier
                        .size(animatedSize)
                        .clip(CircleShape)
                        .border(2.dp, dotColor, CircleShape)
                        .background(if (isFilled) dotColor else Color.Transparent)
                )
                DotStyle.Dash -> Box(
                    modifier = Modifier
                        .width(animatedSize * 2)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(dotColor)
                )
            }
        }
    }
}

@Composable
private fun PinKeyboard(
    enabled: Boolean,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    accentColor: Color,
    textColor: Color,
    keyShape: KeyShape,
    keySize: androidx.compose.ui.unit.Dp,
    keyBackgroundColor: Color,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    keyFontSize: androidx.compose.ui.unit.TextUnit,
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫"),
    )

    val shape = when (keyShape) {
        KeyShape.Circle -> CircleShape
        KeyShape.Square -> RectangleShape
        KeyShape.RoundedSquare -> RoundedCornerShape(16.dp)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                row.forEach { key ->
                    PinKey(
                        label = key,
                        enabled = enabled && key.isNotEmpty(),
                        shape = shape,
                        keySize = keySize,
                        keyBackgroundColor = keyBackgroundColor,
                        textColor = textColor,
                        fontFamily = fontFamily,
                        keyFontSize = keyFontSize,
                        onClick = {
                            when (key) {
                                "⌫" -> onDelete()
                                "" -> Unit
                                else -> onDigit(key)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PinKey(
    label: String,
    enabled: Boolean,
    shape: androidx.compose.ui.graphics.Shape,
    keySize: androidx.compose.ui.unit.Dp,
    keyBackgroundColor: Color,
    textColor: Color,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    keyFontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit,
) {
    val bgColor = if (label.isEmpty()) Color.Transparent else keyBackgroundColor

    Box(
        modifier = Modifier
            .size(keySize)
            .clip(shape)
            .background(bgColor)
            .then(
                if (enabled) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = keyFontSize,
                fontWeight = FontWeight.Medium,
                fontFamily = fontFamily,
                color = if (enabled) textColor else textColor.copy(alpha = 0.3f),
            )
        }
    }
}