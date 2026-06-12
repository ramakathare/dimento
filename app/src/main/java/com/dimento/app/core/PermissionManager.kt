package com.dimento.app.core

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Manages runtime permission requests at app launch.
 * Each permission is requested only once per install.
 */
object PermissionManager {

    private const val PREFS_NAME = "permission_prefs"
    private const val KEY_NOTIFICATION_ASKED = "notification_asked"
    private const val KEY_EXACT_ALARM_ASKED = "exact_alarm_asked"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Returns the list of permissions that should be requested at launch.
     * Only includes permissions that haven't been asked yet.
     */
    fun getPermissionsToRequest(context: Context): List<String> {
        val result = mutableListOf<String>()

        // POST_NOTIFICATIONS — Android 13+ (API 33)
        if (Build.VERSION.SDK_INT >= 33) {
            val alreadyGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            val alreadyAsked = prefs(context).getBoolean(KEY_NOTIFICATION_ASKED, false)
            if (!alreadyGranted && !alreadyAsked) {
                result.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // SCHEDULE_EXACT_ALARM — Android 12+ (API 31)
        if (Build.VERSION.SDK_INT >= 31) {
            val alreadyGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED
            val alreadyAsked = prefs(context).getBoolean(KEY_EXACT_ALARM_ASKED, false)
            if (!alreadyGranted && !alreadyAsked) {
                result.add(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }

        return result
    }

    /**
     * Marks a permission as having been asked so we don't prompt again.
     */
    fun markAsked(context: Context, permission: String) {
        prefs(context).edit().apply {
            when (permission) {
                Manifest.permission.POST_NOTIFICATIONS -> putBoolean(KEY_NOTIFICATION_ASKED, true)
                Manifest.permission.SCHEDULE_EXACT_ALARM -> putBoolean(KEY_EXACT_ALARM_ASKED, true)
            }
            apply()
        }
    }

    /**
     * Resets all permission-asked flags (e.g., after app reinstall).
     */
    fun reset(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
