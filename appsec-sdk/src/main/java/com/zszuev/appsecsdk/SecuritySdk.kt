package com.zszuev.appsecsdk

import android.app.Application

object SecuritySdk {

    private var config: SdkConfig = SdkConfig()
    internal lateinit var application: Application
        private set

    fun init(application: Application, block: SdkConfig.Builder.() -> Unit = {}) {
        this.application = application
        this.config = SdkConfig.Builder().apply(block).build()
        ThreatAlertManager.startMonitoring(application)
    }

    fun getConfig(): SdkConfig = config

    fun checkDevice(): SecurityCheckResult {
        requireInit()
        return SecurityCheckResult(
            isRooted = if (config.rootDetectionEnabled) com.zszuev.appsecsdk.root.RootDetector.isRooted() else false,
            isEmulator = if (config.emulatorDetectionEnabled) com.zszuev.appsecsdk.emulator.EmulatorDetector.isEmulator() else false,
        )
    }

    internal fun requireInit() {
        check(::application.isInitialized) {
            "SecuritySdk не инициализирован. Вызови SecuritySdk.init() в Application.onCreate()"
        }
    }
}