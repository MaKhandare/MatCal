package com.itsmatok.matcal.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventNotificationScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    fun schedule(event: CalendarEvent) {
        val reminderMinutes = event.reminderMinutes ?: return
        if (event.id <= 0 || reminderMinutes <= 0) return

        val reminderTimeMillis = event.date.atTime(event.startTime)
            .minusMinutes(reminderMinutes.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (reminderTimeMillis <= System.currentTimeMillis()) {
            cancel(event.id)
            return
        }

        val pendingIntent = buildPendingIntent(
            eventId = event.id,
            title = event.title,
            startTime = event.startTime.format(timeFormatter),
            location = event.location
        )
        alarmManager.cancel(pendingIntent)
        ensureReminderChannel(context)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            }
        }
    }

    fun cancel(eventId: Int) {
        if (eventId <= 0) return
        alarmManager.cancel(
            buildPendingIntent(
                eventId = eventId,
                title = "",
                startTime = "",
                location = null
            )
        )
    }

    private fun buildPendingIntent(
        eventId: Int,
        title: String,
        startTime: String,
        location: String?
    ): PendingIntent {
        val intent = Intent(context, EventReminderReceiver::class.java).apply {
            action = REMINDER_ACTION
            putExtra(EXTRA_EVENT_ID, eventId)
            putExtra(EXTRA_EVENT_TITLE, title)
            putExtra(EXTRA_EVENT_START_TIME, startTime)
            putExtra(EXTRA_EVENT_LOCATION, location.orEmpty())
        }

        return PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
