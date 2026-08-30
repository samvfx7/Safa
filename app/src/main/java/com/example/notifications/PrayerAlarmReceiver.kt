package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SNOOZE_WUDU = "com.example.notifications.ACTION_SNOOZE_WUDU"
        const val EXTRA_PRAYER_NAME = "PRAYER_NAME"
        const val EXTRA_IS_REMINDER = "IS_REMINDER"
        const val EXTRA_IS_SNOOZE = "IS_SNOOZE"
        const val EXTRA_NOTIFICATION_ID = "NOTIFICATION_ID"
        const val EXTRA_REMINDER_MINUTES = "REMINDER_MINUTES"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer"

        if (intent.action == ACTION_SNOOZE_WUDU) {
            handleSnooze(context, prayerName, intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0))
            return
        }

        val isReminder = intent.getBooleanExtra(EXTRA_IS_REMINDER, false)
        val isSnooze = intent.getBooleanExtra(EXTRA_IS_SNOOZE, false)
        val reminderMinutes = intent.getIntExtra(EXTRA_REMINDER_MINUTES, 10)

        showNotification(context, prayerName, isReminder, isSnooze, reminderMinutes)
    }

    private fun handleSnooze(context: Context, prayerName: String, notificationId: Int) {
        // Cancel the current notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        // Schedule a new alarm for 10 minutes from now
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_IS_SNOOZE, true)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode() + 100, // Unique request code for snooze
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000) // 10 mins

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Fallback if exact alarms not permitted
        }

        // Launch MainActivity directly to Wudu Timer
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", "wudu_timer")
        }
        context.startActivity(activityIntent)
    }

    private fun showNotification(
        context: Context, 
        prayerName: String, 
        isReminder: Boolean, 
        isSnooze: Boolean, 
        reminderMinutes: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = when {
            isSnooze -> "Time to pray $prayerName"
            isReminder -> "Upcoming: $prayerName"
            else -> "Time for $prayerName"
        }

        val message = when {
            isSnooze -> "Ready for $prayerName? (Wudu complete)"
            isReminder -> "$prayerName begins in $reminderMinutes minutes."
            else -> "It's time to pray $prayerName."
        }

        // Use a unique ID based on prayer name and its state
        val notificationId = prayerName.hashCode() + if (isReminder) 1 else if (isSnooze) 2 else 0

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, PrayerNotificationManager.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Add the "Making Wudu" snooze button only for Fajr (and only if it's not already a snooze)
        if (prayerName.equals("Fajr", ignoreCase = true) && !isSnooze) {
            val snoozeIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_SNOOZE_WUDU
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                putExtra("START_DESTINATION", "wudu_timer")
            }
            val snoozePendingIntent = PendingIntent.getActivity(
                context,
                notificationId + 50, // Unique request code for the action button
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            builder.addAction(0, "I'm making Wudu (Snooze 10m)", snoozePendingIntent)
        }

        notificationManager.notify(notificationId, builder.build())
    }
}
