package com.dimento.app.core

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Manages runtime permission requests.
 * POST_NOTIFICATIONS is requested via dialog (Android 13+).
 * SCHEDULE_EXACT_ALARM is opened via system settings (Android 14+).
 */
object PermissionManager {

    private const val PREFS_NAME = "permission_prefs"
    private const val KEY_SKIPPED = "permission_skipped"
    private const val KEY_COMPLETED = "permission_completed"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Whether the user needs to manually grant SCHEDULE_EXACT_ALARM in system settings.
     */
    fun needsExactAlarmSettings(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 31) return false
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        return !alarmManager.canScheduleExactAlarms()
    }

    /**
     * Returns an Intent that opens the app's "Set alarms & reminders" settings page.
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
     * Whether the permission flow was already completed (user clicked OK).
     */
    fun isCompleted(context: Context): Boolean =
        prefs(context).getBoolean(KEY_COMPLETED, false)

    /**
     * Mark the permission flow as completed (won't show again until reinstall).
     */
    fun markCompleted(context: Context) {
        prefs(context).edit().putBoolean(KEY_COMPLETED, true).apply()
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
