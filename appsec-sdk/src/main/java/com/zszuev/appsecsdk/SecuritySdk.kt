package com.zszuev.appsecsdk

import android.app.Application
import com.zszuev.appsecsdk.emulator.EmulatorDetector
import com.zszuev.appsecsdk.inactivity.InactivityTracker
import com.zszuev.appsecsdk.pincode.PinCodeManager
import com.zszuev.appsecsdk.root.RootDetector
import com.zszuev.appsecsdk.screenshot.ScreenshotProtector

object SecuritySdk {

    private var config: SdkConfig = SdkConfig()
    internal lateinit var application: Application
        private set

    fun init(application: Application, block: SdkConfig.Builder.() -> Unit = {}) {
        this.application = application
        this.config = SdkConfig.Builder().apply(block).build()
        AppSecLauncher.startMonitoring(application)
        InactivityTracker.startMonitoring(application)
        ScreenshotProtector.startMonitoring(application)
    }

    fun getConfig(): SdkConfig = config

    fun checkDevice(): SecurityCheckResult {
        requireInit()
        return SecurityCheckResult(
            isRooted = if (config.rootDetectionEnabled) RootDetector.isRooted() else false,
            isEmulator = if (config.emulatorDetectionEnabled) EmulatorDetector.isEmulator() else false,
        )
    }

    /**
        Сброс таймера неактивности пользователя
     **/
    fun resetInactivityTimer() {
        requireInit()
        InactivityTracker.resetTimer()
    }

    /**
        Коллбек успешного ввода пин-кода
     **/
    fun onPinSuccess(callback: () -> Unit) {
        PinCodeManager.onPinSuccess = callback
    }

    internal fun requireInit() {
        check(::application.isInitialized) {
            "SecuritySdk не инициализирован. Вызови SecuritySdk.init() в Application.onCreate()"
        }
    }
}