package com.zszuev.secapp

import android.app.Application
import com.zszuev.appsecsdk.SecuritySdk
import com.zszuev.appsecsdk.ThreatAlertManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SecuritySdk.init(this) {
            rootDetectionEnabled = true
            emulatorDetectionEnabled = true
            screenshotProtectionEnabled = true
            clipboardProtectionEnabled = true
            inactivityTimeoutSeconds = 60
        }
        ThreatAlertManager.setCallbacks(
            onRootConfirmed = { /* например: exitProcess(0) */ },
            onEmulatorConfirmed = { /* например: exitProcess(0) */ },
        )
    }
}
