package com.zszuev.appsecsdk.pincode.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zszuev.appsecsdk.ThreatAlertConfig
import com.zszuev.appsecsdk.pincode.PinCodeConfig
import com.zszuev.appsecsdk.pincode.PinCodeMode
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
    var shakeError by remember { mutableStateOf(false) }

    // Сбрасываем pin при смене режима
    LaunchedEffect(mode) {
        pin = ""
    }

    // Таймер блокировки
    LaunchedEffect(isLockedOut) {
        if (isLockedOut) {
            lockoutSeconds = lockoutSecondsLeft
            while (lockoutSeconds > 0) {
                delay(1000)
                lockoutSeconds--
            }
        }
    }

    // Сбрасываем пин при ошибке
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            shakeError = true
            delay(500)
            pin = ""
            shakeError = false
        }
    }

    val title = when (mode) {
        PinCodeMode.SET -> "Создайте PIN-код"
        PinCodeMode.CONFIRM -> "Подтвердите PIN-код"
        PinCodeMode.ENTER -> config.title
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(config.backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Заголовок и статус
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = config.textColor,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLockedOut && lockoutSeconds > 0 -> Text(
                    text = "Попробуйте через $lockoutSeconds сек.",
                    fontSize = 14.sp,
                    color = Color(0xFFE53935),
                )
                errorMessage != null -> Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFFE53935),
                )
                else -> Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Точки индикатора
        PinDotsRow(
            pinLength = config.pinLength,
            filledCount = pin.length,
            accentColor = config.accentColor,
            hasError = errorMessage != null,
        )

        // Клавиатура
        PinKeyboard(
            enabled = !isLockedOut || lockoutSeconds == 0,
            onDigit = { digit ->
                if (pin.length < config.pinLength) {
                    pin += digit
                    if (pin.length == config.pinLength) {
                        onPinEntered(pin)
                    }
                }
            },
            onDelete = {
                if (pin.isNotEmpty()) pin = pin.dropLast(1)
            },
            accentColor = config.accentColor,
            textColor = config.textColor,
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

@Composable
private fun PinDotsRow(
    pinLength: Int,
    filledCount: Int,
    accentColor: Color,
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
                    hasError -> Color(0xFFE53935)
                    isFilled -> accentColor
                    else -> accentColor.copy(alpha = 0.3f)
                },
                animationSpec = tween(200),
                label = "dotColor"
            )
            val dotSize by animateDpAsState(
                targetValue = if (isFilled) 16.dp else 14.dp,
                animationSpec = tween(150),
                label = "dotSize"
            )
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor)
            )
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
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫"),
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                row.forEach { key ->
                    PinKey(
                        label = key,
                        enabled = enabled && key.isNotEmpty(),
                        accentColor = accentColor,
                        textColor = textColor,
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
    accentColor: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    val bgColor = if (label.isEmpty()) Color.Transparent else accentColor.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(bgColor)
            .then(
                if (enabled) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) textColor else textColor.copy(alpha = 0.3f),
            )
        }
    }
}