package com.zszuev.appsecsdk.pincode

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.zszuev.appsecsdk.pincode.models.PinCheckResult

internal object PinCodeManager {

    private const val PREFS_FILE = "appsec_pincode_prefs"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_ATTEMPTS = "pin_attempts"
    private const val KEY_LOCKOUT_UNTIL = "pin_lockout_until"

    private fun getPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun isPinSet(context: Context): Boolean {
        return getPrefs(context).contains(KEY_PIN_HASH)
    }

    fun savePin(context: Context, pin: String) {
        val hash = pin.hashWithSalt()
        getPrefs(context).edit().putString(KEY_PIN_HASH, hash).apply()
        resetAttempts(context)
    }

    fun checkPin(context: Context, pin: String): PinCheckResult {
        val prefs = getPrefs(context)

        // Проверяем не заблокирован ли ввод
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        if (System.currentTimeMillis() < lockoutUntil) {
            val secondsLeft = ((lockoutUntil - System.currentTimeMillis()) / 1000).toInt()
            return PinCheckResult.LockedOut(secondsLeft)
        }

        val savedHash = prefs.getString(KEY_PIN_HASH, null)
            ?: return PinCheckResult.Error("PIN не установлен")

        return if (pin.hashWithSalt() == savedHash) {
            resetAttempts(context)
            PinCheckResult.Success
        } else {
            val attempts = prefs.getInt(KEY_ATTEMPTS, 0) + 1
            val config = com.zszuev.appsecsdk.SecuritySdk.getConfig().pinCodeConfig
            prefs.edit().putInt(KEY_ATTEMPTS, attempts).apply()

            if (attempts >= config.maxAttempts) {
                val lockoutUntilMs = System.currentTimeMillis() +
                        config.lockoutDurationSeconds * 1000L
                prefs.edit().putLong(KEY_LOCKOUT_UNTIL, lockoutUntilMs).apply()
                PinCheckResult.LockedOut(config.lockoutDurationSeconds)
            } else {
                PinCheckResult.Wrong(attemptsLeft = config.maxAttempts - attempts)
            }
        }
    }

    fun resetAttempts(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_ATTEMPTS)
            .remove(KEY_LOCKOUT_UNTIL)
            .apply()
    }

    fun clearPin(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    internal var onPinSuccess: (() -> Unit)? = null

    private fun String.hashWithSalt(): String {
        val salt = "appsec_sdk_salt"
        val input = this + salt
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}