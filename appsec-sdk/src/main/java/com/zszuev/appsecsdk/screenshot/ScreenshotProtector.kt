package com.zszuev.appsecsdk.screenshot

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import com.zszuev.appsecsdk.SecuritySdk

internal object ScreenshotProtector {

    fun startMonitoring(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (!SecuritySdk.getConfig().screenshotProtectionEnabled) return
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }

            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }
}