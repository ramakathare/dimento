package com.dimento.app.core

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Manages runtime permission requests and settings navigation.
 *
 * On Android 14+ (API 34+), SCHEDULE_EXACT_ALARM can NO LONGER be requested
 * via the standard runtime permission dialog. Instead, the user must manually
 * grant it in: Settings → Apps → DiMento → Set alarms & reminders.
 *
 * This class provides a helper to open that specific settings screen.
 */
object PermissionManager {

    private const val PREFS_NAME = "permission_prefs"
    private const val KEY_SKIPPED = "permission_skipped"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Returns runtime permissions (those still grantable via dialog).
     * SCHEDULE_EXACT_ALARM is excluded on API 34+ — use [needsExactAlarmSettings] instead.
     */
    fun getPermissionsToRequest(context: Context): List<String> {
        val result = mutableListOf<String>()

        // POST_NOTIFICATIONS — Android 13+ (API 33) — still works via dialog
        if (Build.VERSION.SDK_INT >= 33) {
            val isGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                result.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // SCHEDULE_EXACT_ALARM — On API 34+ the runtime dialog was removed.
        // We still check for pre-granted state, but don't add to request list.
        if (Build.VERSION.SDK_INT in 31..33) {
            val isGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                result.add(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }

        return result
    }

    /**
     * Whether the user needs to manually grant SCHEDULE_EXACT_ALARM in system settings.
     * Only relevant on Android 14+ (API 34+).
     */
    fun needsExactAlarmSettings(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 31) return false
        val isGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.SCHEDULE_EXACT_ALARM
        ) == PackageManager.PERMISSION_GRANTED
        return !isGranted
    }

    /**
     * Returns an Intent that opens the app's "Set alarms & reminders" settings page.
     * Only works on API 34+; on older APIs use the runtime permission dialog instead.
     */
    fun openExactAlarmSettings(context: Context): Intent {
        return Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }

    /**
     * Resets all SharedPreferences state.
     */
    fun reset(context: Context) {
        prefs(context).edit().clear().apply()
    }

    /**
     * Whether the user previously skipped the permission flow.
     */
    fun hasSkipped(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SKIPPED, false)

    /**
     * Mark that the user skipped the permission flow (won't ask again until reinstall).
     */
    fun markSkipped(context: Context) {
        prefs(context).edit().putBoolean(KEY_SKIPPED, true).apply()
    }
}
