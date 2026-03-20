package com.zszuev.appsecsdk.emulator

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import com.zszuev.appsecsdk.SecuritySdk

object EmulatorDetector {

    fun isEmulator(): Boolean {
        return checkBuildFields()
                || checkSensors()
                || checkTelephony()
    }

    // Проверка характерных для эмулятора значений Build-полей
    private fun checkBuildFields(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val product = Build.PRODUCT.lowercase()

        return fingerprint.contains("generic")
                || fingerprint.contains("unknown")
                || fingerprint.startsWith("google/sdk")
                || model.contains("google_sdk")
                || model.contains("emulator")
                || model.contains("android sdk built for x86")
                || manufacturer.contains("genymotion")
                || brand.startsWith("generic")
                || device.contains("emulator")
                || product.contains("sdk_gphone")
                || product.contains("vbox86p")
                || product.contains("emulator")
                || product.contains("simulator")
    }

    // На эмуляторах обычно нет акселерометра и гироскопа
    private fun checkSensors(): Boolean {
        val sensorManager = SecuritySdk.application
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val hasAccelerometer = sensorManager
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        val hasGyroscope = sensorManager
            .getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        return !hasAccelerometer && !hasGyroscope
    }

    // Эмуляторы часто возвращают характерные значения оператора
    private fun checkTelephony(): Boolean {
        return try {
            val tm = SecuritySdk.application
                .getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
            val networkOperator = tm.networkOperatorName.lowercase()
            networkOperator == "android" || networkOperator == "sdk"
        } catch (e: Exception) {
            false
        }
    }
}