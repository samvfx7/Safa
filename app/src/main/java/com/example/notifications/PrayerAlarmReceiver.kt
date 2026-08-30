package com.example.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "Prayer"
        val isReminder = intent.getBooleanExtra("IS_REMINDER", false)
        val reminderMinutes = intent.getIntExtra("REMINDER_MINUTES", 10)

        showNotification(context, prayerName, isReminder, reminderMinutes)
    }

    private fun showNotification(context: Context, prayerName: String, isReminder: Boolean, reminderMinutes: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = if (isReminder) {
            "Upcoming: $prayerName"
        } else {
            "Time for $prayerName"
        }

        val message = if (isReminder) {
            "$prayerName begins in $reminderMinutes minutes."
        } else {
            "It's time to pray $prayerName."
        }

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PrayerNotificationManager.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Or an outline icon if available
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Use a unique ID based on prayer name and whether it's a reminder
        val notificationId = prayerName.hashCode() + if (isReminder) 1 else 0
        notificationManager.notify(notificationId, notification)
    }
}
