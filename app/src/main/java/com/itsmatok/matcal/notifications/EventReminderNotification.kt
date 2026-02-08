package com.itsmatok.matcal.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

internal const val REMINDER_CHANNEL_ID = "event_reminders"
internal const val REMINDER_ACTION = "com.itsmatok.matcal.EVENT_REMINDER"
internal const val EXTRA_EVENT_ID = "extra_event_id"
internal const val EXTRA_EVENT_TITLE = "extra_event_title"
internal const val EXTRA_EVENT_START_TIME = "extra_event_start_time"
internal const val EXTRA_EVENT_LOCATION = "extra_event_location"

internal fun ensureReminderChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val manager = context.getSystemService(NotificationManager::class.java)
    val existing = manager.getNotificationChannel(REMINDER_CHANNEL_ID)
    if (existing != null) return

    val channel = NotificationChannel(
        REMINDER_CHANNEL_ID,
        "Event reminders",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Notifications sent before events start"
    }

    manager.createNotificationChannel(channel)
}
