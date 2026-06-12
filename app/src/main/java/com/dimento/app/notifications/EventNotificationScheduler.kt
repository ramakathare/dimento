package com.dimento.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Schedules and cancels exact-time alarms for future event notifications.
 * Uses AlarmManager.setExact() for precise delivery at the event's date/time.
 *
 * On Android 12+, requires SCHEDULE_EXACT_ALARM permission.
 */
object EventNotificationScheduler {

    private const val ALARM_REQUEST_CODE_PREFIX = 100000

    fun schedule(context: Context, eventId: Long, eventDateMillis: Long) {
        val now = System.currentTimeMillis()
        if (eventDateMillis <= now) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra(EXTRA_EVENT_ID, eventId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE_PREFIX + eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, eventDateMillis, pendingIntent)
        } catch (e: SecurityException) {
            // SCHEDULE_EXACT_ALARM permission not granted — fall back to set()
            alarmManager.set(AlarmManager.RTC_WAKEUP, eventDateMillis, pendingIntent)
        }
    }

    fun cancel(context: Context, eventId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra(EXTRA_EVENT_ID, eventId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE_PREFIX + eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    internal const val EXTRA_EVENT_ID = "extra_event_id"
}
