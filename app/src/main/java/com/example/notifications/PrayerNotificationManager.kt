package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.MainActivity
import com.example.data.local.entity.PrayerEntity
import com.example.data.repository.SettingsRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class PrayerNotificationManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Dedicated High Priority Channel for Fajr Adhan & Full Alarm
            val fajrChannel = NotificationChannel(
                FAJR_CHANNEL_ID,
                "Fajr Adhan & Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority alarms and Adhan alerts for Fajr prayer"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 600, 300, 600, 1000)
                enableLights(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(fajrChannel)

            // Standard Channel for Other Prayer Adhan Alerts
            val prayerChannel = NotificationChannel(
                PRAYER_CHANNEL_ID,
                "Prayer Adhan Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications and reminders for daily prayers"
                enableVibration(true)
                enableLights(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(prayerChannel)
        }
    }

    /**
     * Schedules exact prayer alarms for today and automatically rolled over to tomorrow
     * if today's prayer time has already passed.
     */
    fun schedulePrayerAlarms(prayerEntity: PrayerEntity) {
        val settings = settingsRepository.settingsState.value
        val now = System.currentTimeMillis()

        Log.d("FajrAlarmPipeline", "Scheduling prayer alarms for entity date: ${prayerEntity.date}, city: ${settings.city}")

        val prayers = listOf(
            PrayerScheduleConfig("Fajr", prayerEntity.fajr, settings.notifyFajr, REQ_FAJR_EXACT, REQ_FAJR_REMINDER),
            PrayerScheduleConfig("Dhuhr", prayerEntity.dhuhr, settings.notifyDhuhr, REQ_DHUHR_EXACT, REQ_DHUHR_REMINDER),
            PrayerScheduleConfig("Asr", prayerEntity.asr, settings.notifyAsr, REQ_ASR_EXACT, REQ_ASR_REMINDER),
            PrayerScheduleConfig("Maghrib", prayerEntity.maghrib, settings.notifyMaghrib, REQ_MAGHRIB_EXACT, REQ_MAGHRIB_REMINDER),
            PrayerScheduleConfig("Isha", prayerEntity.isha, settings.notifyIsha, REQ_ISHA_EXACT, REQ_ISHA_REMINDER)
        )

        for (config in prayers) {
            // Cancel any old alarm first
            cancelAlarm(config.exactRequestCode)
            cancelAlarm(config.reminderRequestCode)

            if (!config.isEnabled) {
                Log.d("FajrAlarmPipeline", "${config.name} alert is disabled by user setting")
                continue
            }

            val targetTimeMillis = parsePrayerTimeInMillis(prayerEntity.date, config.timeStr, prayerEntity.timezone)
            if (targetTimeMillis == null) {
                Log.e("FajrAlarmPipeline", "Could not parse time string: ${config.timeStr}")
                continue
            }

            // If today's prayer has already passed, roll over to TOMORROW
            val scheduledTimeMillis = if (targetTimeMillis <= now) {
                val tomorrowMillis = targetTimeMillis + (24 * 60 * 60 * 1000L)
                Log.d("FajrAlarmPipeline", "${config.name} time for today ($targetTimeMillis) has passed. Rolling over to tomorrow: $tomorrowMillis")
                tomorrowMillis
            } else {
                targetTimeMillis
            }

            // 1. Schedule Exact Adhan Alarm
            scheduleExactAlarm(
                timeInMillis = scheduledTimeMillis,
                prayerName = config.name,
                isReminder = false,
                requestCode = config.exactRequestCode
            )

            // 2. Schedule Pre-Prayer Reminder (if enabled)
            val reminderMinutes = settings.reminderMinutesBefore
            if (reminderMinutes > 0) {
                val reminderTimeMillis = scheduledTimeMillis - (reminderMinutes * 60 * 1000L)
                if (reminderTimeMillis > now) {
                    scheduleExactAlarm(
                        timeInMillis = reminderTimeMillis,
                        prayerName = config.name,
                        isReminder = true,
                        requestCode = config.reminderRequestCode,
                        reminderMinutes = reminderMinutes
                    )
                }
            }
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
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerName)
            putExtra(PrayerAlarmReceiver.EXTRA_IS_REMINDER, isReminder)
            putExtra(PrayerAlarmReceiver.EXTRA_REMINDER_MINUTES, reminderMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Show intent when tapping status bar alarm clock on Android
        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", if (prayerName.equals("Fajr", true)) "fajr_alarm" else "prayer_times")
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            requestCode + 1000,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val humanReadable = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(timeInMillis)
        Log.d("FajrAlarmPipeline", "Setting alarm for $prayerName (isReminder=$isReminder) at $humanReadable (reqCode=$requestCode)")

        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                if (prayerName.equals("Fajr", ignoreCase = true) && !isReminder) {
                    // For Fajr exact adhan alarm, use setAlarmClock for ultimate OS priority & wakeup
                    val clockInfo = AlarmManager.AlarmClockInfo(timeInMillis, showPendingIntent)
                    alarmManager.setAlarmClock(clockInfo, pendingIntent)
                    Log.d("FajrAlarmPipeline", "Set AlarmClock for Fajr at $humanReadable")
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                // Fallback if exact alarm permission is missing
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Log.w("FajrAlarmPipeline", "Exact alarm permission missing. Using setAndAllowWhileIdle fallback for $prayerName")
            }
        } catch (e: SecurityException) {
            Log.e("FajrAlarmPipeline", "SecurityException scheduling alarm for $prayerName, falling back", e)
            try {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } catch (e2: Exception) {
                Log.e("FajrAlarmPipeline", "Fallback alarm scheduling failed", e2)
            }
        } catch (e: Exception) {
            Log.e("FajrAlarmPipeline", "Error setting alarm for $prayerName", e)
        }
    }

    private fun cancelAlarm(requestCode: Int) {
        try {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        } catch (e: Exception) {
            // Ignore cancel errors
        }
    }

    private fun parsePrayerTimeInMillis(dateStr: String, timeStr: String, timezoneStr: String): Long? {
        return try {
            val parts = timeStr.trim().split(" ")[0].split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val dateParts = dateStr.trim().split("-")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1
            val day = dateParts[2].toInt()

            val tz = TimeZone.getTimeZone(timezoneStr.ifEmpty { TimeZone.getDefault().id })
            val cal = Calendar.getInstance(tz).apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        } catch (e: Exception) {
            Log.e("FajrAlarmPipeline", "Error parsing prayer time $dateStr $timeStr tz=$timezoneStr", e)
            null
        }
    }

    private data class PrayerScheduleConfig(
        val name: String,
        val timeStr: String,
        val isEnabled: Boolean,
        val exactRequestCode: Int,
        val reminderRequestCode: Int
    )

    companion object {
        const val FAJR_CHANNEL_ID = "fajr_alarm_channel"
        const val PRAYER_CHANNEL_ID = "prayer_times_channel"

        const val REQ_FAJR_EXACT = 100
        const val REQ_FAJR_REMINDER = 101
        const val REQ_DHUHR_EXACT = 200
        const val REQ_DHUHR_REMINDER = 201
        const val REQ_ASR_EXACT = 300
        const val REQ_ASR_REMINDER = 301
        const val REQ_MAGHRIB_EXACT = 400
        const val REQ_MAGHRIB_REMINDER = 401
        const val REQ_ISHA_EXACT = 500
        const val REQ_ISHA_REMINDER = 501
    }
}
