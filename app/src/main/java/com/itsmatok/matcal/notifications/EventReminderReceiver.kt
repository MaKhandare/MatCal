package com.itsmatok.matcal.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.itsmatok.matcal.MainActivity
import com.itsmatok.matcal.R

class EventReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!hasNotificationPermission(context)) return

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, 0)
        val title = intent.getStringExtra(EXTRA_EVENT_TITLE).orEmpty().ifBlank { "Upcoming event" }
        val startTime = intent.getStringExtra(EXTRA_EVENT_START_TIME).orEmpty()
        val location = intent.getStringExtra(EXTRA_EVENT_LOCATION).orEmpty()
        val contentText = buildString {
            append("Starts at ")
            append(startTime.ifBlank { "soon" })
            if (location.isNotBlank()) {
                append(" • ")
                append(location)
            }
        }

        ensureReminderChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            eventId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        sendNotification(context, eventId, notification)
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(
        context: Context,
        eventId: Int,
        notification: android.app.Notification
    ) {
        NotificationManagerCompat.from(context).notify(eventId, notification)
    }
}
