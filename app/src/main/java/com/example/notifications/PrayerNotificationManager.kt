package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.entity.PrayerEntity
import com.example.data.repository.SettingsRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrayerNotificationManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Prayer Times"
            val descriptionText = "Notifications for prayer times and reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun schedulePrayerAlarms(prayerEntity: PrayerEntity) {
        // Cancel all existing alarms first
        cancelAllAlarms()
        
        val settings = settingsRepository.settingsState.value

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            // Define prayers and their notification toggles
            val prayers = listOf(
                PrayerData("Fajr", prayerEntity.fajr, settings.notifyFajr),
                PrayerData("Dhuhr", prayerEntity.dhuhr, settings.notifyDhuhr),
                PrayerData("Asr", prayerEntity.asr, settings.notifyAsr),
                PrayerData("Maghrib", prayerEntity.maghrib, settings.notifyMaghrib),
                PrayerData("Isha", prayerEntity.isha, settings.notifyIsha)
            )

            for ((index, prayer) in prayers.withIndex()) {
                if (!prayer.isEnabled) continue

                val dateTimeString = "${prayerEntity.date} ${prayer.time}"
                val date = dateFormat.parse(dateTimeString) ?: continue

                val timeInMillis = date.time
                val now = System.currentTimeMillis()

                // Only schedule if time is in the future
                if (timeInMillis > now) {
                    scheduleExactAlarm(
                        timeInMillis,
                        prayer.name,
                        isReminder = false,
                        requestCode = index * 2
                    )

                    // Pre-prayer reminder
                    val reminderMillis = timeInMillis - (settings.reminderMinutesBefore * 60 * 1000)
                    if (reminderMillis > now && settings.reminderMinutesBefore > 0) {
                        scheduleExactAlarm(
                            reminderMillis,
                            prayer.name,
                            isReminder = true,
                            requestCode = (index * 2) + 1,
                            reminderMinutes = settings.reminderMinutesBefore
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PrayerNotificationManager", "Error scheduling alarms", e)
        }
    }

    private fun scheduleExactAlarm(
        timeInMillis: Long,
        prayerName: String,
        isReminder: Boolean,
        requestCode: Int,
        reminderMinutes: Int = 10
    ) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("PRAYER_NAME", prayerName)
            putExtra("IS_REMINDER", isReminder)
            putExtra("REMINDER_MINUTES", reminderMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Use exact alarms if permitted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("PrayerNotificationManager", "Exact alarm permission missing", e)
        }
    }

    private fun cancelAllAlarms() {
        val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
        for ((index, name) in prayerNames.withIndex()) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", name)
                putExtra("IS_REMINDER", false)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index * 2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)

            val reminderIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", name)
                putExtra("IS_REMINDER", true)
            }
            val reminderPendingIntent = PendingIntent.getBroadcast(
                context,
                (index * 2) + 1,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(reminderPendingIntent)
        }
    }

    private data class PrayerData(
        val name: String,
        val time: String,
        val isEnabled: Boolean
    )

    companion object {
        const val CHANNEL_ID = "prayer_times_channel"
    }
}
