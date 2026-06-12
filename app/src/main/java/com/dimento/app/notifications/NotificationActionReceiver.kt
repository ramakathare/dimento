package com.dimento.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.dimento.app.core.ServiceLocator

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ServiceLocator.init(context)
        val eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        if (eventId <= 0) return
        val action = intent.action ?: return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            val container = ServiceLocator.container
            when (action) {
                ACTION_MARK_COMPLETE -> {
                    container.markEventCompleteUseCase(eventId, System.currentTimeMillis())
                    EventNotificationScheduler.cancel(context, eventId)
                }
                ACTION_DONE -> {
                    container.markEventCompleteUseCase(eventId, System.currentTimeMillis())
                    EventNotificationScheduler.cancel(context, eventId)
                    androidx.core.app.NotificationManagerCompat.from(context).cancel(eventId.toInt())
                }
                ACTION_DELETE -> {
                    container.deleteEventUseCase(eventId)
                    EventNotificationScheduler.cancel(context, eventId)
                }
            }
            pendingResult.finish()
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        private const val ACTION_MARK_COMPLETE = "com.dimento.app.action.MARK_COMPLETE"
        private const val ACTION_DONE = "com.dimento.app.action.DONE"
        private const val ACTION_DELETE = "com.dimento.app.action.DELETE_EVENT"
        const val ACTION_EDIT_EVENT = "com.dimento.app.action.EDIT_EVENT"
        const val EXTRA_EDIT_EVENT_ID = "edit_event_id"

        fun createMarkCompletePendingIntent(context: Context, eventId: Long): PendingIntent {
            val intent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = ACTION_MARK_COMPLETE
                putExtra(EXTRA_EVENT_ID, eventId)
            }
            return PendingIntent.getBroadcast(
                context,
                ("complete_$eventId").hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun createDonePendingIntent(context: Context, eventId: Long): PendingIntent {
            val intent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = ACTION_DONE
                putExtra(EXTRA_EVENT_ID, eventId)
            }
            return PendingIntent.getBroadcast(
                context,
                ("done_$eventId").hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun createDeletePendingIntent(context: Context, eventId: Long): PendingIntent {
            val intent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = ACTION_DELETE
                putExtra(EXTRA_EVENT_ID, eventId)
            }
            return PendingIntent.getBroadcast(
                context,
                ("delete_$eventId").hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun createReschedulePendingIntent(context: Context, eventId: Long): PendingIntent {
            val intent = Intent(context, com.dimento.app.MainActivity::class.java).apply {
                action = ACTION_EDIT_EVENT
                putExtra(EXTRA_EDIT_EVENT_ID, eventId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(
                context,
                ("reschedule_$eventId").hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
