package com.dimento.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.dimento.app.core.PermissionManager
import com.dimento.app.presentation.DiMentoAppRoot
import com.dimento.app.presentation.theme.DiMentoTheme

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        requestExactAlarmIfNeeded()
    }

    private val exactAlarmSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Returned from exact alarm settings
    }

    var pendingRescheduleEventId by mutableStateOf(-1L)
    var sharedText: String? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkRescheduleIntent()
        handleShareIntent(intent)
        enableEdgeToEdge()
        setContent {
            DiMentoTheme {
                DiMentoAppRoot()
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: android.content.Intent?) {
        if (intent?.action == android.content.Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(android.content.Intent.EXTRA_TEXT)
            if (!text.isNullOrBlank()) {
                sharedText = text
            }
        }
    }

    fun startPermissionFlow() {
        if (Build.VERSION.SDK_INT >= 33) {
            val isGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        requestExactAlarmIfNeeded()
    }

    private fun requestExactAlarmIfNeeded() {
        if (PermissionManager.needsExactAlarmSettings(this)) {
            try {
                val intent = PermissionManager.openExactAlarmSettings(this)
                exactAlarmSettingsLauncher.launch(intent)
            } catch (_: Exception) {
                // Failed to open exact alarm settings
            }
        }
    }

    fun checkRescheduleIntent() {
        val eventId = intent?.getLongExtra(
            com.dimento.app.notifications.NotificationActionReceiver.EXTRA_EDIT_EVENT_ID, -1L
        ) ?: -1L
        if (eventId > 0) {
            pendingRescheduleEventId = eventId
            intent?.removeExtra(com.dimento.app.notifications.NotificationActionReceiver.EXTRA_EDIT_EVENT_ID)
        }
    }
}
