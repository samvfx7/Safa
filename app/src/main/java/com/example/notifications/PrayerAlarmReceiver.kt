package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.NoorDatabase
import com.example.data.repository.PrayerRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SNOOZE_WUDU = "com.example.notifications.ACTION_SNOOZE_WUDU"
        const val EXTRA_PRAYER_NAME = "PRAYER_NAME"
        const val EXTRA_IS_REMINDER = "IS_REMINDER"
        const val EXTRA_IS_SNOOZE = "IS_SNOOZE"
        const val EXTRA_IS_TEST = "IS_TEST"
        const val EXTRA_NOTIFICATION_ID = "NOTIFICATION_ID"
        const val EXTRA_REMINDER_MINUTES = "REMINDER_MINUTES"

        fun scheduleSnoozeAlarm(
            context: Context,
            prayerName: String = "Fajr",
            delayMinutes: Int = 10,
            isTest: Boolean = false
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_IS_SNOOZE, true)
                putExtra(EXTRA_IS_TEST, isTest)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayerName.hashCode() + 777,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val snoozeTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)

            try {
                val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms()
                } else {
                    true
                }

                if (canScheduleExact) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                }
                Log.d("FajrAlarmPipeline", "Snoozed $prayerName for $delayMinutes minutes (isTest=$isTest)")
            } catch (e: Exception) {
                Log.e("FajrAlarmPipeline", "Error scheduling snooze for $prayerName", e)
            }
        }

        fun cancelSnoozeAlarm(context: Context, prayerName: String = "Fajr") {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    putExtra(EXTRA_PRAYER_NAME, prayerName)
                    putExtra(EXTRA_IS_SNOOZE, true)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    prayerName.hashCode() + 777,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
                Log.d("FajrAlarmPipeline", "Cancelled snooze alarm for $prayerName")
            } catch (e: Exception) {
                Log.e("FajrAlarmPipeline", "Error cancelling snooze alarm for $prayerName", e)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Fajr"
        Log.d("FajrAlarmPipeline", "PrayerAlarmReceiver triggered for prayer: $prayerName, action: ${intent.action}")

        if (intent.action == ACTION_SNOOZE_WUDU) {
            handleSnooze(context, prayerName, intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0))
            return
        }

        val isReminder = intent.getBooleanExtra(EXTRA_IS_REMINDER, false)
        val isSnooze = intent.getBooleanExtra(EXTRA_IS_SNOOZE, false)
        val isTest = intent.getBooleanExtra(EXTRA_IS_TEST, false)
        val reminderMinutes = intent.getIntExtra(EXTRA_REMINDER_MINUTES, 10)

        // Display the Adhan / Reminder Notification
        showNotification(context, prayerName, isReminder, isSnooze, isTest, reminderMinutes)

        // Self-sustaining alarm chain: Automatically trigger rescheduling for upcoming prayers/tomorrow (only for real alarms)
        if (!isTest) {
            autoRescheduleAlarms(context)
        }
    }

    private fun autoRescheduleAlarms(context: Context) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("FajrAlarmPipeline", "Auto-rescheduling next prayer alarms after alarm trigger")
                val db = NoorDatabase.getDatabase(context)
                val settingsRepo = SettingsRepository(context)
                val prayerRepo = PrayerRepository(db.prayerDao(), db.prayerLogDao(), settingsRepo)
                val notificationManager = PrayerNotificationManager(context, settingsRepo)

                val entity = prayerRepo.refreshPrayerTimes(force = false).getOrNull()
                    ?: prayerRepo.getLatestPrayerTimes().firstOrNull()

                if (entity != null) {
                    notificationManager.schedulePrayerAlarms(entity)
                }
            } catch (e: Exception) {
                Log.e("FajrAlarmPipeline", "Error during auto-rescheduling in receiver", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleSnooze(context: Context, prayerName: String, notificationId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationId != 0) {
            notificationManager.cancel(notificationId)
        }

        scheduleSnoozeAlarm(context, prayerName, delayMinutes = 10, isTest = false)

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
        isTest: Boolean,
        reminderMinutes: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val isFajr = prayerName.equals("Fajr", ignoreCase = true)
        val channelId = if (isFajr) PrayerNotificationManager.FAJR_CHANNEL_ID else PrayerNotificationManager.PRAYER_CHANNEL_ID

        val title = when {
            isTest -> "🚨 [TEST ALARM] Fajr Alarm Test Triggered"
            isSnooze -> "Time to pray $prayerName"
            isReminder -> "Upcoming: $prayerName"
            isFajr -> "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ • Fajr Prayer"
            else -> "Time for $prayerName Prayer"
        }

        val message = when {
            isTest -> "Tap to open Fajr Alarm Test & test your selected alarm audio stream."
            isSnooze -> "Ready for $prayerName? (10-min Wudu snooze elapsed)"
            isReminder -> "$prayerName prayer begins in $reminderMinutes minutes."
            isFajr -> "It's time for Fajr prayer. Scan your prayer mat in Safa to complete."
            else -> "It's time to pray $prayerName."
        }

        val notificationId = if (isTest) 9999 else (prayerName.hashCode() + if (isReminder) 1 else if (isSnooze) 2 else 0)

        val targetDestination = when {
            isTest -> "fajr_alarm?isTest=true"
            isFajr -> "fajr_alarm"
            else -> "prayer_times"
        }

        // Main Tap Action: Opens full alarm screen for Fajr or prayer times
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", targetDestination)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, isFajr && !isReminder)

        // Add "I'm making Wudu (Snooze 10m)" action for Fajr
        if (isFajr && !isSnooze) {
            val snoozeIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_SNOOZE_WUDU
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                putExtra("START_DESTINATION", "wudu_timer")
            }
            val snoozePendingIntent = PendingIntent.getActivity(
                context,
                notificationId + 50,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.addAction(0, "I'm making Wudu (Snooze 10m)", snoozePendingIntent)
        }

        try {
            notificationManager.notify(notificationId, builder.build())
            Log.d("FajrAlarmPipeline", "Notification displayed successfully for $prayerName (id=$notificationId)")
        } catch (e: Exception) {
            Log.e("FajrAlarmPipeline", "Failed to show notification for $prayerName", e)
        }
    }
}
