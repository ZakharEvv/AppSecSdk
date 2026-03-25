package com.zszuev.secapp

import android.app.Application
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.zszuev.appsecsdk.SecuritySdk
import com.zszuev.appsecsdk.ThreatAlertManager
import com.zszuev.appsecsdk.biometric.BiometricConfig
import com.zszuev.appsecsdk.pincode.DotStyle
import com.zszuev.appsecsdk.pincode.KeyShape
import com.zszuev.appsecsdk.pincode.PinCodeConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SecuritySdk.init(this) {
            rootDetectionEnabled = true
            emulatorDetectionEnabled = true
            screenshotProtectionEnabled = true
            clipboardProtectionEnabled = true
            inactivityTimeoutSeconds = 60
            pinCodeConfig = PinCodeConfig(
                accentColor = Color(0xFF1976D2),
                backgroundColor = Color(0xFF0D0D0D),
                textColor = Color.White,
                keyShape = KeyShape.Circle,
                dotStyle = DotStyle.Outlined,
                titleFontSize = 20.sp,
                logoResId = R.drawable.ic_launcher_foreground
            )
            biometricConfig = BiometricConfig()
        }
        ThreatAlertManager.setCallbacks(
            onRootConfirmed = {  },
            onEmulatorConfirmed = {  },
        )
        SecuritySdk.onPinSuccess {
            // пользователь успешно вошёл
        }
    }
}
