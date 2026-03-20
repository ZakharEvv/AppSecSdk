package com.zszuev.appsecsdk.pincode

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zszuev.appsecsdk.SecuritySdk
import com.zszuev.appsecsdk.ThreatAlertManager
import com.zszuev.appsecsdk.ThreatAlertConfig
import com.zszuev.appsecsdk.emulator.EmulatorDetector
import com.zszuev.appsecsdk.pincode.ui.PinCodeScreen
import com.zszuev.appsecsdk.root.RootDetector

class PinCodeActivity : ComponentActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(
                Intent(context, PinCodeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    private val viewModel: PinCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(this)

        viewModel.onSuccess = {
            PinCodeManager.onPinSuccess?.invoke()
            finish()
        }

        setContent {
            val state by viewModel.state.collectAsState()
            MaterialTheme {
                PinCodeScreen(
                    mode = state.mode,
                    config = state.config,
                    onPinEntered = { pin -> viewModel.onPinEntered(this, pin) },
                    errorMessage = state.errorMessage,
                    isLockedOut = state.isLockedOut,
                    lockoutSecondsLeft = state.lockoutSecondsLeft,
                    threatAlertConfig = state.threatAlertConfig,
                    isThreatRoot = state.isThreatRoot,
                    onThreatConfirmed = {
                        viewModel.onThreatConfirmed(
                            onRootConfirmed = ThreatAlertManager.onRootConfirmed,
                            onEmulatorConfirmed = ThreatAlertManager.onEmulatorConfirmed,
                        )
                    },
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // блокируем кнопку назад
    }
}