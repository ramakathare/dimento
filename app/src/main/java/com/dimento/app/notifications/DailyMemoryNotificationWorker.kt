package com.dimento.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dimento.app.MainActivity
import com.dimento.app.R
import com.dimento.app.core.ServiceLocator

class DailyMemoryNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        ServiceLocator.init(applicationContext)
        val container = ServiceLocator.container
        val dueToday = container.getEventsDueTodayUseCase(System.currentTimeMillis())
        if (dueToday.isEmpty()) return Result.success()

        createChannelIfNeeded()
        if (!canNotify()) return Result.success()

        dueToday.take(MAX_NOTIFICATIONS).forEach { event ->
            val openIntent = PendingIntent.getActivity(
                applicationContext,
                event.id.toInt(),
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val doneIntent = NotificationActionReceiver.createDonePendingIntent(applicationContext, event.id)
            val rescheduleIntent = NotificationActionReceiver.createReschedulePendingIntent(applicationContext, event.id)
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setContentTitle(applicationContext.getString(R.string.memory_due_today))
                .setContentText(event.text)
                .setContentIntent(openIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(android.R.drawable.checkbox_on_background, applicationContext.getString(R.string.notification_done), doneIntent)
                .addAction(android.R.drawable.ic_menu_edit, applicationContext.getString(R.string.notification_reschedule), rescheduleIntent)
                .build()

            NotificationManagerCompat.from(applicationContext).notify(event.id.toInt(), notification)
        }
        return Result.success()
    }

    private fun canNotify(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < 33) return true
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createChannelIfNeeded() {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.channel_due_today_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = applicationContext.getString(R.string.channel_due_today_description)
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "dimento_daily_notifications"
        const val CHANNEL_ID = "dimento_due_today"
        private const val MAX_NOTIFICATIONS = 8
    }
}
