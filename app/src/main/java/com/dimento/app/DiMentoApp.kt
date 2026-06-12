package com.dimento.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dimento.app.core.ServiceLocator
import com.dimento.app.notifications.DailyMemoryNotificationWorker
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiMentoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)

        CoroutineScope(Dispatchers.IO).launch {
            ServiceLocator.container.ensureDefaultGroupUseCase()
        }

        createNotificationChannel()

        val work = PeriodicWorkRequestBuilder<DailyMemoryNotificationWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyMemoryNotificationWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            DailyMemoryNotificationWorker.CHANNEL_ID,
            getString(R.string.channel_due_today_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.channel_due_today_description)
        }
        manager.createNotificationChannel(channel)
    }
}
