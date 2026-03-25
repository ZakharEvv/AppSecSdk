package com.zszuev.appsecsdk.biometric

import android.content.Context
import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

internal object BiometricManager {

    private const val PREFS_FILE = "appsec_biometric_prefs"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

    fun isBiometricAvailable(context: Context): Boolean {
        val manager = AndroidBiometricManager.from(context)
        return manager.canAuthenticate(
            AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == AndroidBiometricManager.BIOMETRIC_SUCCESS
    }

    fun isBiometricEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
            .getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String = "Вход по биометрии",
        subtitle: String = "Приложите палец для входа",
        negativeButtonText: String = "Войти по PIN",
        onSuccess: () -> Unit,
        onError: () -> Unit,
        onFallback: () -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_USER_CANCELED -> onFallback()
                    else -> onError()
                }
            }

            override fun onAuthenticationFailed() {
                // Неверный отпечаток — BiometricPrompt сам показывает ошибку
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}