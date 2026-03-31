package com.zszuev.appsecsdk.inactivity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window
import com.zszuev.appsecsdk.SecuritySdk
import com.zszuev.appsecsdk.ThreatAlertManager
import com.zszuev.appsecsdk.pincode.ui.PinCodeActivity
import com.zszuev.appsecsdk.pincode.PinCodeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

internal object InactivityTracker {

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private var currentActivityRef: WeakReference<Activity>? = null
    private var currentActivity: Activity?
        get() = currentActivityRef?.get()
        set(value) {
            currentActivityRef = if (value != null) WeakReference(value) else null
        }

    fun startMonitoring(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityResumed(activity: Activity) {
                if (activity is PinCodeActivity) return
                currentActivity = activity
                wrapWindowCallback(activity)
                startTimer()
            }

            override fun onActivityPaused(activity: Activity) {
                if (activity is PinCodeActivity) return
                stopTimer()
                currentActivity = null
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    private fun wrapWindowCallback(activity: Activity) {
        val original = activity.window.callback
        if (original is InactivityWindowCallback) return

        activity.window.callback = InactivityWindowCallback(
            delegate = original,
            onUserInteraction = { resetTimer() },
        )
    }

    fun resetTimer() {
        if (currentActivity != null) startTimer()
    }

    private fun startTimer() {
        stopTimer()
        val timeoutSeconds = SecuritySdk.getConfig().inactivityTimeoutSeconds
        timerJob = scope.launch {
            delay(timeoutSeconds * 1000L)
            onInactivityTimeout()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun onInactivityTimeout() {
        val activity = currentActivity ?: return
        PinCodeActivity.start(activity)
    }
}

private class InactivityWindowCallback(
    private val delegate: Window.Callback,
    private val onUserInteraction: () -> Unit,
) : Window.Callback by delegate {

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        onUserInteraction()
        return delegate.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        onUserInteraction()
        return delegate.dispatchKeyEvent(event)
    }
}