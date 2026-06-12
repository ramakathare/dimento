package com.dimento.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.dimento.app.core.ServiceLocator
import com.dimento.app.notifications.DailyMemoryNotificationWorker.Companion.CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that receives exact alarm intents from EventNotificationScheduler
 * and displays a notification for the event.
 */
class EventAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra(EventNotificationScheduler.EXTRA_EVENT_ID, -1L)
        if (eventId <= 0) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ServiceLocator.init(context)
                val container = ServiceLocator.container

                val event = container.repository.getEvent(eventId)
                if (event == null || event.completedDateMillis != null) {
                    pendingResult.finish()
                    return@launch
                }

                createChannelIfNeeded(context)
                if (!canNotify(context)) {
                    pendingResult.finish()
                    return@launch
                }

                val openIntent = PendingIntent.getActivity(
                    context,
                    event.id.toInt(),
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val completeIntent = NotificationActionReceiver.createMarkCompletePendingIntent(context, event.id)
                val deleteIntent = NotificationActionReceiver.createDeletePendingIntent(context, event.id)

                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                    .setContentTitle(context.getString(R.string.memory_due_today))
                    .setContentText(event.text)
                    .setContentIntent(openIntent)
                    .setAutoCancel(true)
                    .addAction(
                        android.R.drawable.checkbox_on_background,
                        context.getString(R.string.mark_complete),
                        completeIntent
                    )
                    .addAction(
                        android.R.drawable.ic_delete,
                        context.getString(R.string.delete_label),
                        deleteIntent
                    )
                    .build()

                NotificationManagerCompat.from(context).notify(event.id.toInt(), notification)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun canNotify(context: Context): Boolean {
        if (android.os.Build.VERSION.SDK_INT < 33) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createChannelIfNeeded(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.channel_due_today_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.channel_due_today_description)
        }
        manager.createNotificationChannel(channel)
    }

    companion object
}
