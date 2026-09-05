package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.FajrAlarmActivity
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

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "Fajr"
        val isFajr = prayerName.equals("Fajr", ignoreCase = true)

        Log.d(TAG, "PrayerAlarmReceiver triggered - action: $action, prayer: $prayerName")

        // Acquire a short partial wake lock so the CPU stays awake while building and posting notification
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "safa:fajr_alarm_receiver")
        try {
            wakeLock?.acquire(5000L)
        } catch (e: Exception) {
            // Ignore wakelock error
        }

        // 1. Handle user dismissal action
        if (action == ACTION_DISMISS_NOTIFICATION) {
            val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, PrayerNotificationManager.NOTIFICATION_ID_FAJR)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
            Log.d(TAG, "Fajr notification dismissed by user action (id=$notificationId)")
            wakeLock?.let { if (it.isHeld) it.release() }
            return
        }

        // 2. Handle Remind Later / Snooze action
        if (action == ACTION_REMIND_LATER || action == ACTION_SNOOZE_WUDU) {
            handleSnooze(context, prayerName, intent.getIntExtra(EXTRA_NOTIFICATION_ID, PrayerNotificationManager.NOTIFICATION_ID_FAJR))
            wakeLock?.let { if (it.isHeld) it.release() }
            return
        }

        val isReminder = intent.getBooleanExtra(EXTRA_IS_REMINDER, false)
        val isSnooze = intent.getBooleanExtra(EXTRA_IS_SNOOZE, false)
        val isTest = intent.getBooleanExtra(EXTRA_IS_TEST, false)
        val reminderMinutes = intent.getIntExtra(EXTRA_REMINDER_MINUTES, 10)
        val timeString = intent.getStringExtra(EXTRA_PRAYER_TIME_STRING) ?: ""

        // 3. Display the Adhan / Prayer Notification
        showNotification(context, prayerName, isReminder, isSnooze, isTest, reminderMinutes, timeString)

        // 4. Automatically reschedule the NEXT alarm (guaranteeing one authoritative scheduled alarm)
        if (!isTest && !isReminder && !isSnooze) {
            autoRescheduleNextAlarms(context, isFajr)
        }

        wakeLock?.let { if (it.isHeld) it.release() }
    }

    private fun showNotification(
        context: Context,
        prayerName: String,
        isReminder: Boolean,
        isSnooze: Boolean,
        isTest: Boolean,
        reminderMinutes: Int,
        timeString: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isFajr = prayerName.equals("Fajr", ignoreCase = true)

        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                Log.w(TAG, "Notification permission (POST_NOTIFICATIONS) is not granted. Cannot display Fajr notification.")
                return
            }
        }

        val channelId = if (isFajr) PrayerNotificationManager.FAJR_CHANNEL_ID else PrayerNotificationManager.PRAYER_CHANNEL_ID

        val notificationId = when {
            isTest -> PrayerNotificationManager.NOTIFICATION_ID_TEST
            isReminder -> if (isFajr) PrayerNotificationManager.NOTIFICATION_ID_FAJR_REMINDER else (prayerName.hashCode() + 1)
            else -> if (isFajr) PrayerNotificationManager.NOTIFICATION_ID_FAJR else prayerName.hashCode()
        }

        val displayTime = if (timeString.isNotEmpty()) timeString else "05:15"

        // Clear, recognizable titles as requested
        val title = when {
            isTest -> "Fajr — It's time to pray [Test Alert]"
            isSnooze -> "Fajr — It's time to pray (Snooze Elapsed)"
            isReminder -> "Fajr — Starts in $reminderMinutes minutes"
            isFajr -> "Fajr — It's time to pray"
            else -> "Time for $prayerName Prayer"
        }

        // Informative concise content
        val content = when {
            isTest -> "Fajr • $displayTime • Alarm and notification test triggered successfully"
            isSnooze -> "Fajr • $displayTime • 10-minute Wudu snooze elapsed. Rise for morning prayer."
            isReminder -> "Fajr begins in $reminderMinutes minutes ($displayTime). Prepare for prayer."
            isFajr -> "Fajr • $displayTime • الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ"
            else -> "$prayerName • $displayTime • It's time for prayer."
        }

        val bigText = when {
            isTest -> "Fajr • $displayTime\nSafa test alarm has triggered on schedule.\nAudio and high-priority notification channels are active."
            isSnooze -> "Fajr • $displayTime\nYour 10-minute Wudu timer has completed.\nBegin your prayer in peace and devotion."
            isReminder -> "Fajr • $displayTime\nFajr prayer starts in $reminderMinutes minutes.\nTake a moment to make Wudu and prepare."
            isFajr -> "Fajr • $displayTime\nالصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ — Prayer is better than sleep.\nRise to meet Allah in the peace and tranquility of dawn."
            else -> "$prayerName • $displayTime\nTake a mindful break to offer your prayer on time."
        }

        // Main Tap Action & Full Screen Intent
        val fullScreenIntent = Intent(context, FajrAlarmActivity::class.java).apply {
            action = ACTION_FAJR_FULL_SCREEN_ALARM
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_PRAYER_TIME_STRING, displayTime)
            putExtra(EXTRA_IS_TEST, isTest)
            putExtra(EXTRA_IS_SNOOZE, isSnooze)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 50,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val targetDestination = if (isFajr) "fajr_alarm" else "prayer_times"
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", targetDestination)
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: Dismiss
        val dismissIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_DISMISS_NOTIFICATION
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 10,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: Open Safa Alarm UI / Prayer Times
        val openSafaPendingIntent = if (isFajr) {
            fullScreenPendingIntent
        } else {
            val openSafaIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("START_DESTINATION", "prayer_times")
            }
            PendingIntent.getActivity(
                context,
                notificationId + 20,
                openSafaIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Action 3: View Prayer Times
        val timesIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", "prayer_times")
        }
        val timesPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 30,
            timesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_safa_notification)
            .setColor(0xFFC5A059.toInt()) // Safa Gold
            .setContentTitle(title)
            .setContentText(content)
            .setSubText("Safa • Prayer Alert")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .setSummaryText("Safa • Fajr Prayer")
                    .bigText(bigText)
            )
            .setAutoCancel(true)
            .setOngoing(false) // Dismissible by user swipe or dismiss button
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (isFajr) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
            builder.setContentIntent(fullScreenPendingIntent)

            // Direct Wudu flow intent from notification
            val wuduIntent = Intent(context, FajrAlarmActivity::class.java).apply {
                action = ACTION_FAJR_FULL_SCREEN_ALARM
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_PRAYER_TIME_STRING, displayTime)
                putExtra(EXTRA_IS_TEST, isTest)
                putExtra(EXTRA_IS_SNOOZE, isSnooze)
                putExtra(EXTRA_START_STATE, FajrAlarmFlowState.WUDU.name)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }
            val wuduPendingIntent = PendingIntent.getActivity(
                context,
                notificationId + 40,
                wuduIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_REMIND_LATER
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId + 45,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.addAction(0, "Dismiss", dismissPendingIntent)
            builder.addAction(0, "Open Safa", openSafaPendingIntent)
            builder.addAction(0, "Make Wudu", wuduPendingIntent)
            builder.addAction(0, "Remind Later (10m)", snoozePendingIntent)
        } else {
            builder.setContentIntent(mainPendingIntent)
            builder.addAction(0, "Dismiss", dismissPendingIntent)
            builder.addAction(0, "Open Safa", openSafaPendingIntent)
            builder.addAction(0, "Prayer Times", timesPendingIntent)
        }

        try {
            notificationManager.notify(notificationId, builder.build())
            Log.d(TAG, "Notification successfully posted: id=$notificationId, title=\"$title\", time=$displayTime")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying notification for $prayerName", e)
        }
    }

    private fun handleSnooze(context: Context, prayerName: String, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationId != 0) {
            notificationManager.cancel(notificationId)
        }

        scheduleSnoozeAlarm(context, prayerName, delayMinutes = 10, isTest = false)
        Toast.makeText(context, "Fajr reminder set for 10 minutes from now ⏰", Toast.LENGTH_SHORT).show()
    }

    private fun autoRescheduleNextAlarms(context: Context, isFajr: Boolean) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Auto-rescheduling next prayer alarm. isFajr=$isFajr")
                val db = NoorDatabase.getDatabase(context)
                val settingsRepo = SettingsRepository(context)
                val prayerRepo = PrayerRepository(db.prayerDao(), db.prayerLogDao(), settingsRepo)
                val notificationManager = PrayerNotificationManager(context, settingsRepo)

                if (isFajr) {
                    // Fajr just triggered! Calculate and schedule TOMORROW'S Fajr accurately
                    val result = notificationManager.scheduleFajrAlarm(forceTomorrow = true)
                    Log.d(TAG, "Tomorrow's Fajr rescheduled successfully: $result")
                } else {
                    val entity = prayerRepo.refreshPrayerTimes(force = false).getOrNull()
                        ?: prayerRepo.getLatestPrayerTimes().firstOrNull()
                    if (entity != null) {
                        notificationManager.schedulePrayerAlarms(entity)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during auto-rescheduling in receiver", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val TAG = "SafaFajrNotification"

        const val ACTION_FAJR_ALARM = "com.example.notifications.ACTION_FAJR_ALARM"
        const val ACTION_FAJR_FULL_SCREEN_ALARM = "com.example.notifications.ACTION_FAJR_FULL_SCREEN_ALARM"
        const val ACTION_REMIND_LATER = "com.example.notifications.ACTION_REMIND_LATER"
        const val ACTION_SNOOZE_WUDU = "com.example.notifications.ACTION_SNOOZE_WUDU"
        const val ACTION_DISMISS_NOTIFICATION = "com.example.notifications.ACTION_DISMISS_NOTIFICATION"

        const val EXTRA_PRAYER_NAME = "PRAYER_NAME"
        const val EXTRA_PRAYER_TIME_MILLIS = "PRAYER_TIME_MILLIS"
        const val EXTRA_PRAYER_TIME_STRING = "PRAYER_TIME_STRING"
        const val EXTRA_PRAYER_DATE_STRING = "PRAYER_DATE_STRING"
        const val EXTRA_START_STATE = "START_STATE"
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
                action = ACTION_FAJR_ALARM
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
                } else true

                if (canScheduleExact) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
                }
                Log.d(TAG, "Snoozed $prayerName for $delayMinutes minutes")
            } catch (e: Exception) {
                Log.e(TAG, "Error scheduling snooze for $prayerName", e)
            }
        }

        fun cancelSnoozeAlarm(context: Context, prayerName: String = "Fajr") {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = ACTION_FAJR_ALARM
                    putExtra(EXTRA_PRAYER_NAME, prayerName)
                    putExtra(EXTRA_IS_SNOOZE, true)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    prayerName.hashCode() + 777,
                    snoozeIntent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
                Log.d(TAG, "Cancelled snooze alarm for $prayerName")
            } catch (e: Exception) {
                Log.e(TAG, "Error cancelling snooze alarm for $prayerName", e)
            }
        }
    }
}
