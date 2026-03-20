package com.zszuev.appsecsdk.root

import android.content.pm.PackageManager
import com.zszuev.appsecsdk.SecuritySdk
import java.io.File

object RootDetector {

    private val suPaths = listOf(
        "/system/bin/su",
        "/system/xbin/su",
        "/sbin/su",
        "/system/su",
        "/system/bin/.ext/.su",
        "/system/usr/we-need-root/su-backup",
        "/system/xbin/mu",
    )

    private val rootPackages = listOf(
        "com.topjohnwu.magisk",
        "com.noshufou.android.su",
        "com.koushikdutta.superuser",
        "eu.chainfire.supersu",
        "com.zachspong.temprootremovejb",
        "com.ramdroid.appquarantine",
    )

    fun isRooted(): Boolean {
        return checkSuBinaries()
                || checkRootPackages()
                || checkWritableSystemPartition()
                || checkBuildTags()
    }

    // Проверка наличия su-бинарей в системных путях
    private fun checkSuBinaries(): Boolean {
        return suPaths.any { File(it).exists() }
    }

    // Проверка установленных root-пакетов
    private fun checkRootPackages(): Boolean {
        val pm = SecuritySdk.application.packageManager
        return rootPackages.any { packageName ->
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    // Проверка возможности записи в /system
    private fun checkWritableSystemPartition(): Boolean {
        return try {
            val file = File("/system/test_root_write")
            file.createNewFile().also { file.delete() }
        } catch (e: Exception) {
            false
        }
    }

    // На рутованных девайсах build tags содержит "test-keys"
    private fun checkBuildTags(): Boolean {
        val tags = android.os.Build.TAGS
        return tags != null && tags.contains("test-keys")
    }
}