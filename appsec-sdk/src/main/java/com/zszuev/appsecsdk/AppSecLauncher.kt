package com.zszuev.appsecsdk

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.zszuev.appsecsdk.pincode.ui.PinCodeActivity
import com.zszuev.appsecsdk.pincode.PinCodeManager

internal object AppSecLauncher {

    fun startMonitoring(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            private var isStarted = false

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (isStarted) return
                if (activity is PinCodeActivity) return

                isStarted = true
                PinCodeActivity.start(activity)
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