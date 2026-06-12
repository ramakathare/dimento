package com.dimento.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Schedules and cancels exact-time alarms for future event notifications.
 * Uses AlarmManager.setExactAndAllowWhileIdle() when available,
 * falls back to setAlarmClock() which works without SCHEDULE_EXACT_ALARM.
 */
object EventNotificationScheduler {

    private const val ALARM_REQUEST_CODE_PREFIX = 100_000

    fun schedule(context: Context, eventId: Long, eventDateMillis: Long) {
        if (eventDateMillis <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, eventId)

        if (Build.VERSION.SDK_INT >= 31 && alarmManager.canScheduleExactAlarms()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, eventDateMillis, pendingIntent
                )
                return
            } catch (_: SecurityException) { /* fall through */ }
        }

        // setAlarmClock fires exactly without special permission
        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(eventDateMillis, null), pendingIntent
            )
        } catch (_: Exception) {
            // Last resort: inexact alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, eventDateMillis, pendingIntent)
        }
    }

    fun cancel(context: Context, eventId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, eventId)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun buildPendingIntent(context: Context, eventId: Long): PendingIntent {
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra(EXTRA_EVENT_ID, eventId)
        }
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE_PREFIX + eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    internal const val EXTRA_EVENT_ID = "extra_event_id"
}
