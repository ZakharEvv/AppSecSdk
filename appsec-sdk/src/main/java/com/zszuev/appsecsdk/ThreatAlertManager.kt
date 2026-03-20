package com.zszuev.appsecsdk

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.zszuev.appsecsdk.emulator.EmulatorDetector
import com.zszuev.appsecsdk.root.RootDetector


object ThreatAlertManager {

    private var onRootConfirmed: (() -> Unit)? = null
    private var onEmulatorConfirmed: (() -> Unit)? = null

    fun setCallbacks(
        onRootConfirmed: (() -> Unit)? = null,
        onEmulatorConfirmed: (() -> Unit)? = null,
    ) {
        this.onRootConfirmed = onRootConfirmed
        this.onEmulatorConfirmed = onEmulatorConfirmed
    }

    internal fun startMonitoring(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            private var isAlertShown = false

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                android.util.Log.d("ThreatAlert", "onActivityCreated: ${activity::class.simpleName}")
                android.util.Log.d("ThreatAlert", "isFragmentActivity: ${activity is FragmentActivity}")
                if (isAlertShown) return

                val config = SecuritySdk.getConfig()
                val isRooted = if (config.rootDetectionEnabled) RootDetector.isRooted() else false
                val isEmulator = if (config.emulatorDetectionEnabled) EmulatorDetector.isEmulator() else false

                android.util.Log.d("ThreatAlert", "isRooted: $isRooted")
                android.util.Log.d("ThreatAlert", "isEmulator: $isEmulator")

                when {
                    isRooted -> {
                        isAlertShown = true
                        showAlert(activity, config.rootAlertConfig, onRootConfirmed)
                    }
                    isEmulator -> {
                        isAlertShown = true
                        showAlert(activity, config.emulatorAlertConfig, onEmulatorConfirmed)
                    }
                }
            }

            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    private fun showAlert(
        activity: Activity,
        alertConfig: ThreatAlertConfig,
        onConfirmed: (() -> Unit)?,
    ) {
        AlertDialog.Builder(activity)
            .setTitle(alertConfig.title)
            .setMessage(alertConfig.message)
            .setCancelable(false)
            .setPositiveButton(alertConfig.buttonText) { dialog, _ ->
                dialog.dismiss()
                onConfirmed?.invoke()
            }
            .show()
    }
}