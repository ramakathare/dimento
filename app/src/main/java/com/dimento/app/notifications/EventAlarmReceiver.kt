package com.dimento.app.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dimento.app.MainActivity
import com.dimento.app.R
import com.dimento.app.notifications.DailyMemoryNotificationWorker.Companion.CHANNEL_ID

/**
 * BroadcastReceiver that receives exact alarm intents from EventNotificationScheduler
 * and displays a notification with Done and Reschedule actions.
 */
class EventAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra(EventNotificationScheduler.EXTRA_EVENT_ID, -1L)
        if (eventId <= 0 || !canNotify(context)) return

        val openIntent = PendingIntent.getActivity(
            context,
            eventId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val doneIntent = NotificationActionReceiver.createDonePendingIntent(context, eventId)
        val rescheduleIntent = NotificationActionReceiver.createReschedulePendingIntent(context, eventId)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle(context.getString(R.string.memory_due_today))
            .setContentText("Event #$eventId")
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                android.R.drawable.checkbox_on_background,
                context.getString(R.string.notification_done),
                doneIntent
            )
            .addAction(
                android.R.drawable.ic_menu_edit,
                context.getString(R.string.notification_reschedule),
                rescheduleIntent
            )
            .build()

        NotificationManagerCompat.from(context).notify(eventId.toInt(), notification)
    }

    private fun canNotify(context: Context): Boolean {
        if (android.os.Build.VERSION.SDK_INT < 33) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
