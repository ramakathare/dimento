package com.dimento.app

import android.app.Application
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

        val work = PeriodicWorkRequestBuilder<DailyMemoryNotificationWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyMemoryNotificationWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }
}
