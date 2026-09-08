package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.FajrAlarmActivity
import com.example.MainActivity
import com.example.data.local.entity.PrayerEntity
import com.example.data.repository.AppSettings
import com.example.data.repository.FajrAlarmMode
import com.example.data.repository.SettingsRepository
import com.example.data.util.SolarPrayerCalculator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class AlarmStatusType {
    SCHEDULED,          // 🟢 Alarm scheduled
    OFF,                // ⚪ Alarm off
    PERMISSION_REQUIRED,// 🟠 Permission required
    ERROR               // 🔴 Scheduling error
}

data class ScheduledFajrInfo(
    val triggerTimeMillis: Long,
    val dateStr: String,
    val timeStr: String,
    val isTomorrow: Boolean,
    val isExact: Boolean,
    val calculationMethod: String,
    val mode: FajrAlarmMode = FajrAlarmMode.AT_FAJR,
    val offsetMinutes: Int = 0,
    val fajrTimeStr: String = "",
    val statusType: AlarmStatusType = AlarmStatusType.SCHEDULED,
    val summaryText: String = ""
)

internal data class FajrAlarmTarget(
    val triggerTimeMillis: Long,
    val targetDateStr: String,
    val alarmTimeStr: String,
    val fajrTimeStr: String,
    val isTomorrow: Boolean,
    val mode: FajrAlarmMode,
    val offsetMinutes: Int,
    val summaryText: String
)

class PrayerNotificationManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    /**
     * Creates dedicated high-importance notification channels for Fajr and daily prayers.
     * Respects Android's channel architecture and user-configured customizations.
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Dedicated High Priority Channel for Fajr Prayer
            val fajrChannel = NotificationChannel(
                FAJR_CHANNEL_ID,
                "Safa — Fajr Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority notifications and alarms for Fajr morning prayer"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
                enableLights(true)
                lightColor = 0xFFC5A059.toInt() // Safa Gold
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), audioAttributes)
            }
            notificationManager.createNotificationChannel(fajrChannel)

            // Legacy Fajr channel migration/compatibility
            val legacyFajrChannel = NotificationChannel(
                LEGACY_FAJR_CHANNEL_ID,
                "Fajr Adhan & Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Legacy alarms and Adhan alerts for Fajr prayer"
                enableVibration(true)
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(legacyFajrChannel)

            // Standard Channel for Other Daily Prayers
            val prayerChannel = NotificationChannel(
                PRAYER_CHANNEL_ID,
                "Daily Prayer Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications and reminders for daily prayers"
                enableVibration(true)
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(prayerChannel)
        }
    }

    /**
     * Centralized target calculator for Fajr alarms.
     * Accurately handles:
     * - AT_FAJR (astronomical Fajr time)
     * - BEFORE_FAJR (offset minutes before calculated Fajr)
     * - CUSTOM_TIME (exact clock time, independent of prayer calculation)
     * - Crossing midnight, upcoming day transitions, and timezone preservation.
     */
    internal fun calculateFajrAlarmTarget(
        settings: AppSettings,
        prayerEntity: PrayerEntity? = null,
        forceTomorrow: Boolean = false,
        now: Long = System.currentTimeMillis()
    ): FajrAlarmTarget {
        val mode = FajrAlarmMode.fromString(settings.fajrAlarmMode)
        val tzId = prayerEntity?.timezone?.ifEmpty { TimeZone.getDefault().id } ?: TimeZone.getDefault().id
        val tz = TimeZone.getTimeZone(tzId)
        val calNow = Calendar.getInstance(tz).apply { timeInMillis = now }

        val todayDateStr = String.format(Locale.US, "%04d-%02d-%02d",
            calNow.get(Calendar.YEAR), calNow.get(Calendar.MONTH) + 1, calNow.get(Calendar.DAY_OF_MONTH))

        val calTomorrow = Calendar.getInstance(tz).apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val tomorrowDateStr = String.format(Locale.US, "%04d-%02d-%02d",
            calTomorrow.get(Calendar.YEAR), calTomorrow.get(Calendar.MONTH) + 1, calTomorrow.get(Calendar.DAY_OF_MONTH))

        // Calculate astronomical Fajr for today & tomorrow
        val todayFajrTimeStr = if (prayerEntity != null && prayerEntity.date == todayDateStr && prayerEntity.fajr.isNotEmpty()) {
            cleanTime(prayerEntity.fajr)
        } else {
            SolarPrayerCalculator.calculateFajrTime(calNow, settings.latitude, settings.longitude, settings.calculationMethodId, tzId)
        }
        val todayFajrMillis = parsePrayerTimeToMillis(todayDateStr, todayFajrTimeStr, tz)
            ?: (calNow.apply {
                set(Calendar.HOUR_OF_DAY, 5)
                set(Calendar.MINUTE, 15)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis)

        val tomorrowFajrTimeStr = SolarPrayerCalculator.calculateFajrTime(
            calTomorrow,
            settings.latitude,
            settings.longitude,
            settings.calculationMethodId,
            tzId
        )
        val tomorrowFajrMillis = parsePrayerTimeToMillis(tomorrowDateStr, tomorrowFajrTimeStr, tz)
            ?: (calTomorrow.apply {
                set(Calendar.HOUR_OF_DAY, 5)
                set(Calendar.MINUTE, 15)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis)

        return when (mode) {
            FajrAlarmMode.AT_FAJR -> {
                val isTodayPassed = forceTomorrow || (todayFajrMillis <= now)
                val targetMillis = if (isTodayPassed) tomorrowFajrMillis else todayFajrMillis
                val targetDateStr = if (isTodayPassed) tomorrowDateStr else todayDateStr
                val targetTimeStr = if (isTodayPassed) tomorrowFajrTimeStr else todayFajrTimeStr
                val isTomorrow = isTodayPassed
                val summary = if (isTomorrow) "Tomorrow at $targetTimeStr" else "Today at $targetTimeStr"

                FajrAlarmTarget(
                    triggerTimeMillis = targetMillis,
                    targetDateStr = targetDateStr,
                    alarmTimeStr = targetTimeStr,
                    fajrTimeStr = targetTimeStr,
                    isTomorrow = isTomorrow,
                    mode = mode,
                    offsetMinutes = 0,
                    summaryText = summary
                )
            }

            FajrAlarmMode.BEFORE_FAJR -> {
                val offsetMinutes = settings.fajrAlarmBeforeMinutes.coerceAtLeast(1)
                val offsetMillis = offsetMinutes * 60 * 1000L
                val todayAlarmMillis = todayFajrMillis - offsetMillis
                val isTodayPassed = forceTomorrow || (todayAlarmMillis <= now)

                val targetMillis = if (isTodayPassed) (tomorrowFajrMillis - offsetMillis) else todayAlarmMillis
                val targetDateStr = if (isTodayPassed) tomorrowDateStr else todayDateStr
                val isTomorrow = isTodayPassed

                val alarmCal = Calendar.getInstance(tz).apply { timeInMillis = targetMillis }
                val targetTimeStr = String.format(Locale.US, "%02d:%02d",
                    alarmCal.get(Calendar.HOUR_OF_DAY), alarmCal.get(Calendar.MINUTE))
                val fajrTimeStr = if (isTomorrow) tomorrowFajrTimeStr else todayFajrTimeStr
                val dayLabel = if (isTomorrow) "Tomorrow" else "Today"
                val summary = "$offsetMinutes min before Fajr ($dayLabel at $targetTimeStr)"

                FajrAlarmTarget(
                    triggerTimeMillis = targetMillis,
                    targetDateStr = targetDateStr,
                    alarmTimeStr = targetTimeStr,
                    fajrTimeStr = fajrTimeStr,
                    isTomorrow = isTomorrow,
                    mode = mode,
                    offsetMinutes = offsetMinutes,
                    summaryText = summary
                )
            }

            FajrAlarmMode.CUSTOM_TIME -> {
                val parts = (settings.fajrAlarmCustomTime.ifEmpty { "05:30" }).split(":")
                val customHour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 5
                val customMinute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 30

                val calCustomToday = Calendar.getInstance(tz).apply {
                    set(Calendar.YEAR, calNow.get(Calendar.YEAR))
                    set(Calendar.MONTH, calNow.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calNow.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, customHour)
                    set(Calendar.MINUTE, customMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val isTodayPassed = forceTomorrow || (calCustomToday.timeInMillis <= now)
                val targetMillis: Long
                val targetDateStr: String
                val isTomorrow: Boolean

                if (isTodayPassed) {
                    val calCustomTomorrow = Calendar.getInstance(tz).apply {
                        timeInMillis = calCustomToday.timeInMillis
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                    targetMillis = calCustomTomorrow.timeInMillis
                    targetDateStr = tomorrowDateStr
                    isTomorrow = true
                } else {
                    targetMillis = calCustomToday.timeInMillis
                    targetDateStr = todayDateStr
                    isTomorrow = false
                }

                val targetTimeStr = String.format(Locale.US, "%02d:%02d", customHour, customMinute)
                val summary = if (isTomorrow) "Tomorrow at $targetTimeStr" else "Today at $targetTimeStr"
                val fajrTimeStr = if (isTomorrow) tomorrowFajrTimeStr else todayFajrTimeStr

                FajrAlarmTarget(
                    triggerTimeMillis = targetMillis,
                    targetDateStr = targetDateStr,
                    alarmTimeStr = targetTimeStr,
                    fajrTimeStr = fajrTimeStr,
                    isTomorrow = isTomorrow,
                    mode = mode,
                    offsetMinutes = 0,
                    summaryText = summary
                )
            }
        }
    }

    /**
     * Authoritative scheduling for the Fajr notification.
     * Guarantees that only ONE active Fajr alarm exists in AlarmManager.
     * Correctly handles:
     * - Today's remaining Fajr time
     * - Tomorrow's calculated Fajr time (calculated for tomorrow's date, never hardcoded or +24h)
     * - Custom wake-up times and pre-Fajr offsets
     * - Timezones, DST transitions, and clock changes
     * - Exact vs inexact fallback
     */
    fun scheduleFajrAlarm(prayerEntity: PrayerEntity? = null, forceTomorrow: Boolean = false): ScheduledFajrInfo? {
        val settings = settingsRepository.settingsState.value
        val now = System.currentTimeMillis()

        if (!settings.notifyFajr) {
            cancelAlarm(REQ_FAJR_EXACT)
            cancelAlarm(REQ_FAJR_REMINDER)
            Log.d(TAG, "Fajr reminder is disabled in user settings. Cancelled all scheduled Fajr alarms.")
            return ScheduledFajrInfo(
                triggerTimeMillis = 0L,
                dateStr = "",
                timeStr = "",
                isTomorrow = false,
                isExact = false,
                calculationMethod = settings.calculationMethodName,
                mode = FajrAlarmMode.fromString(settings.fajrAlarmMode),
                offsetMinutes = settings.fajrAlarmBeforeMinutes,
                fajrTimeStr = "",
                statusType = AlarmStatusType.OFF,
                summaryText = "Alarm off"
            )
        }

        return try {
            val target = calculateFajrAlarmTarget(settings, prayerEntity, forceTomorrow, now)

            // Prevent duplicate alarms: ALWAYS cancel old pending alarm first
            cancelAlarm(REQ_FAJR_EXACT)
            cancelAlarm(REQ_FAJR_REMINDER)

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = PrayerAlarmReceiver.ACTION_FAJR_ALARM
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "Fajr")
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_MILLIS, target.triggerTimeMillis)
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_STRING, target.alarmTimeStr)
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_DATE_STRING, target.targetDateStr)
                putExtra(PrayerAlarmReceiver.EXTRA_ALARM_MODE, target.mode.name)
                putExtra(PrayerAlarmReceiver.EXTRA_ALARM_OFFSET_MINUTES, target.offsetMinutes)
                putExtra(PrayerAlarmReceiver.EXTRA_FAJR_TIME_STRING, target.fajrTimeStr)
                putExtra(PrayerAlarmReceiver.EXTRA_IS_REMINDER, false)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQ_FAJR_EXACT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val showIntent = Intent(context, FajrAlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "Fajr")
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_STRING, target.alarmTimeStr)
                putExtra(PrayerAlarmReceiver.EXTRA_ALARM_MODE, target.mode.name)
                putExtra(PrayerAlarmReceiver.EXTRA_FAJR_TIME_STRING, target.fajrTimeStr)
            }
            val showPendingIntent = PendingIntent.getActivity(
                context,
                REQ_FAJR_EXACT + 1000,
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            var isExact = false
            try {
                if (canScheduleExact) {
                    val clockInfo = AlarmManager.AlarmClockInfo(target.triggerTimeMillis, showPendingIntent)
                    alarmManager.setAlarmClock(clockInfo, pendingIntent)
                    isExact = true
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.triggerTimeMillis, pendingIntent)
                    isExact = false
                    Log.w(TAG, "Exact alarm access unavailable. Used setAndAllowWhileIdle fallback.")
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException while setting AlarmClock, falling back to setAndAllowWhileIdle", e)
                try {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.triggerTimeMillis, pendingIntent)
                    isExact = false
                } catch (fallbackError: Exception) {
                    Log.e(TAG, "Fallback alarm scheduling failed", fallbackError)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error setting Fajr alarm", e)
            }

            // Schedule Pre-Fajr Reminder if configured and in AT_FAJR mode
            if (target.mode == FajrAlarmMode.AT_FAJR) {
                val reminderMinutes = settings.reminderMinutesBefore
                if (reminderMinutes > 0) {
                    val reminderTimeMillis = target.triggerTimeMillis - (reminderMinutes * 60 * 1000L)
                    if (reminderTimeMillis > now) {
                        scheduleReminderAlarm(
                            timeInMillis = reminderTimeMillis,
                            prayerName = "Fajr",
                            reminderMinutes = reminderMinutes,
                            requestCode = REQ_FAJR_REMINDER
                        )
                    }
                }
            }

            val status = if (!canScheduleExact) {
                AlarmStatusType.PERMISSION_REQUIRED
            } else {
                AlarmStatusType.SCHEDULED
            }

            Log.d(TAG, "=== Safa Fajr Reminder Scheduled ===")
            Log.d(TAG, "Mode: ${target.mode.displayName}, Target Date: ${target.targetDateStr}")
            Log.d(TAG, "Alarm Time: ${target.alarmTimeStr}, Millis: ${target.triggerTimeMillis}")
            Log.d(TAG, "Exact Alarm: $isExact, Status: $status")
            Log.d(TAG, "=====================================")

            ScheduledFajrInfo(
                triggerTimeMillis = target.triggerTimeMillis,
                dateStr = target.targetDateStr,
                timeStr = target.alarmTimeStr,
                isTomorrow = target.isTomorrow,
                isExact = isExact,
                calculationMethod = settings.calculationMethodName,
                mode = target.mode,
                offsetMinutes = target.offsetMinutes,
                fajrTimeStr = target.fajrTimeStr,
                statusType = status,
                summaryText = target.summaryText
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling Fajr alarm", e)
            ScheduledFajrInfo(
                triggerTimeMillis = 0L,
                dateStr = "",
                timeStr = "",
                isTomorrow = false,
                isExact = false,
                calculationMethod = settings.calculationMethodName,
                mode = FajrAlarmMode.fromString(settings.fajrAlarmMode),
                offsetMinutes = settings.fajrAlarmBeforeMinutes,
                fajrTimeStr = "",
                statusType = AlarmStatusType.ERROR,
                summaryText = "Scheduling error"
            )
        }
    }

    /**
     * Cancels any scheduled Fajr alarm and active reminder.
     */
    fun cancelFajrAlarms() {
        cancelAlarm(REQ_FAJR_EXACT)
        cancelAlarm(REQ_FAJR_REMINDER)
        notificationManager.cancel(NOTIFICATION_ID_FAJR)
        notificationManager.cancel(NOTIFICATION_ID_FAJR_REMINDER)
        Log.d(TAG, "Cancelled all active Fajr alarms and notifications.")
    }

    /**
     * Schedules prayer alarms for all daily prayers.
     * Uses dedicated Fajr schedule for Fajr, and calculated times for others.
     */
    fun schedulePrayerAlarms(prayerEntity: PrayerEntity) {
        val settings = settingsRepository.settingsState.value
        val now = System.currentTimeMillis()
        val tzId = prayerEntity.timezone.ifEmpty { TimeZone.getDefault().id }
        val tz = TimeZone.getTimeZone(tzId)

        Log.d(TAG, "Scheduling prayer alarms for date: ${prayerEntity.date}, city: ${settings.city}")

        // 1. Authoritative Fajr Scheduling
        scheduleFajrAlarm(prayerEntity)

        // 2. Other Daily Prayers
        val otherPrayers = listOf(
            PrayerScheduleConfig("Dhuhr", prayerEntity.dhuhr, settings.notifyDhuhr, REQ_DHUHR_EXACT, REQ_DHUHR_REMINDER),
            PrayerScheduleConfig("Asr", prayerEntity.asr, settings.notifyAsr, REQ_ASR_EXACT, REQ_ASR_REMINDER),
            PrayerScheduleConfig("Maghrib", prayerEntity.maghrib, settings.notifyMaghrib, REQ_MAGHRIB_EXACT, REQ_MAGHRIB_REMINDER),
            PrayerScheduleConfig("Isha", prayerEntity.isha, settings.notifyIsha, REQ_ISHA_EXACT, REQ_ISHA_REMINDER)
        )

        for (config in otherPrayers) {
            cancelAlarm(config.exactRequestCode)
            cancelAlarm(config.reminderRequestCode)

            if (!config.isEnabled) continue

            val cleanedTime = cleanTime(config.timeStr)
            val todayTargetMillis = parsePrayerTimeToMillis(prayerEntity.date, cleanedTime, tz) ?: continue

            val scheduledTimeMillis: Long
            if (todayTargetMillis <= now) {
                // Calculate tomorrow's time accurately
                val tomorrowCal = Calendar.getInstance(tz).apply {
                    timeInMillis = now
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                val tomorrowTimes = SolarPrayerCalculator.calculatePrayerTimes(
                    tomorrowCal,
                    settings.latitude,
                    settings.longitude,
                    settings.calculationMethodId,
                    settings.isHanafiAsr,
                    tzId
                )
                val tomorrowTimeStr = when (config.name) {
                    "Dhuhr" -> tomorrowTimes.dhuhr
                    "Asr" -> tomorrowTimes.asr
                    "Maghrib" -> tomorrowTimes.maghrib
                    "Isha" -> tomorrowTimes.isha
                    else -> cleanedTime
                }
                val tomorrowDateStr = tomorrowTimes.date
                scheduledTimeMillis = parsePrayerTimeToMillis(tomorrowDateStr, tomorrowTimeStr, tz)
                    ?: (todayTargetMillis + (24 * 60 * 60 * 1000L))
            } else {
                scheduledTimeMillis = todayTargetMillis
            }

            scheduleGeneralPrayerAlarm(
                timeInMillis = scheduledTimeMillis,
                prayerName = config.name,
                requestCode = config.exactRequestCode
            )

            val reminderMinutes = settings.reminderMinutesBefore
            if (reminderMinutes > 0) {
                val reminderTime = scheduledTimeMillis - (reminderMinutes * 60 * 1000L)
                if (reminderTime > now) {
                    scheduleReminderAlarm(
                        timeInMillis = reminderTime,
                        prayerName = config.name,
                        reminderMinutes = reminderMinutes,
                        requestCode = config.reminderRequestCode
                    )
                }
            }
        }
    }

    /**
     * Schedules a REAL test alarm that triggers via AlarmManager & PrayerAlarmReceiver
     * after delaySeconds. Perfect for verifying that alarms trigger when the app is closed
     * or screen is locked.
     */
    fun scheduleScheduledTestAlarm(delaySeconds: Int): Long {
        val triggerTime = System.currentTimeMillis() + (delaySeconds * 1000L)
        val requestCode = REQ_TEST_ALARM

        cancelAlarm(requestCode)

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_FAJR_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "Fajr")
            putExtra(PrayerAlarmReceiver.EXTRA_IS_TEST, true)
            putExtra(PrayerAlarmReceiver.EXTRA_IS_REMINDER, false)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_STRING, SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(triggerTime)))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, FajrAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, "Fajr")
            putExtra(PrayerAlarmReceiver.EXTRA_IS_TEST, true)
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            requestCode + 1000,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d(TAG, "Scheduling REAL OS Test Alarm in $delaySeconds seconds (at $triggerTime)")

        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                val clockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent)
                alarmManager.setAlarmClock(clockInfo, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling test alarm", e)
        }

        return triggerTime
    }

    /**
     * Inspects the next scheduled Fajr info for live UI display without altering alarm state.
     */
    fun getNextScheduledFajrInfo(prayerEntity: PrayerEntity?): ScheduledFajrInfo {
        val settings = settingsRepository.settingsState.value
        if (!settings.notifyFajr) {
            return ScheduledFajrInfo(
                triggerTimeMillis = 0L,
                dateStr = "",
                timeStr = "",
                isTomorrow = false,
                isExact = false,
                calculationMethod = settings.calculationMethodName,
                mode = FajrAlarmMode.fromString(settings.fajrAlarmMode),
                offsetMinutes = settings.fajrAlarmBeforeMinutes,
                fajrTimeStr = "",
                statusType = AlarmStatusType.OFF,
                summaryText = "Alarm off"
            )
        }

        return try {
            val target = calculateFajrAlarmTarget(settings, prayerEntity, false, System.currentTimeMillis())
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else true

            val status = if (!canScheduleExact) {
                AlarmStatusType.PERMISSION_REQUIRED
            } else {
                AlarmStatusType.SCHEDULED
            }

            ScheduledFajrInfo(
                triggerTimeMillis = target.triggerTimeMillis,
                dateStr = target.targetDateStr,
                timeStr = target.alarmTimeStr,
                isTomorrow = target.isTomorrow,
                isExact = canScheduleExact,
                calculationMethod = settings.calculationMethodName,
                mode = target.mode,
                offsetMinutes = target.offsetMinutes,
                fajrTimeStr = target.fajrTimeStr,
                statusType = status,
                summaryText = target.summaryText
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error computing next scheduled Fajr info", e)
            ScheduledFajrInfo(
                triggerTimeMillis = 0L,
                dateStr = "",
                timeStr = "",
                isTomorrow = false,
                isExact = false,
                calculationMethod = settings.calculationMethodName,
                mode = FajrAlarmMode.fromString(settings.fajrAlarmMode),
                offsetMinutes = settings.fajrAlarmBeforeMinutes,
                fajrTimeStr = "",
                statusType = AlarmStatusType.ERROR,
                summaryText = "Scheduling error"
            )
        }
    }

    private fun scheduleGeneralPrayerAlarm(timeInMillis: Long, prayerName: String, requestCode: Int) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerName)
            putExtra(PrayerAlarmReceiver.EXTRA_IS_REMINDER, false)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else true

            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting alarm for $prayerName", e)
        }
    }

    private fun scheduleReminderAlarm(timeInMillis: Long, prayerName: String, reminderMinutes: Int, requestCode: Int) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerName)
            putExtra(PrayerAlarmReceiver.EXTRA_IS_REMINDER, true)
            putExtra(PrayerAlarmReceiver.EXTRA_REMINDER_MINUTES, reminderMinutes)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else true

            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reminder alarm for $prayerName", e)
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

    private fun cleanTime(raw: String): String {
        return raw.split(" ")[0].trim()
    }

    private fun parsePrayerTimeToMillis(dateStr: String, timeStr: String, tz: TimeZone): Long? {
        return try {
            val parts = timeStr.trim().split(" ")[0].split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val dateParts = dateStr.trim().split("-")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1
            val day = dateParts[2].toInt()

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
            Log.e(TAG, "Error parsing prayer time $dateStr $timeStr tz=${tz.id}", e)
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
        const val TAG = "SafaFajrNotification"

        const val FAJR_CHANNEL_ID = "safa_fajr_prayer_channel"
        const val LEGACY_FAJR_CHANNEL_ID = "fajr_alarm_channel"
        const val PRAYER_CHANNEL_ID = "prayer_times_channel"

        const val REQ_TEST_ALARM = 999
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

        const val NOTIFICATION_ID_FAJR = 1001
        const val NOTIFICATION_ID_FAJR_REMINDER = 1002
        const val NOTIFICATION_ID_TEST = 9999
    }
}
