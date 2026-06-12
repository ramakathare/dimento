package com.dimento.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dimento.app.core.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Re-schedules exact alarms for all incomplete future events after device reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ServiceLocator.init(context)
                val container = ServiceLocator.container
                val events = container.repository.getAllEventsWithGroupNames()

                val now = System.currentTimeMillis()
                events.forEach { (event, _) ->
                    if (event.completedDateMillis == null && event.eventDateMillis > now) {
                        EventNotificationScheduler.schedule(context, event.id, event.eventDateMillis)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
