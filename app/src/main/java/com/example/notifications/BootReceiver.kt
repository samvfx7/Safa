package com.example.notifications

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.NoorDatabase
import com.example.data.repository.PrayerRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "BootReceiver triggered with action: $action")

        val isRelevantAction = action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                action == Intent.ACTION_TIME_CHANGED ||
                action == Intent.ACTION_TIMEZONE_CHANGED ||
                action == Intent.ACTION_DATE_CHANGED ||
                action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED

        if (isRelevantAction) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "BootReceiver: Rescheduling prayer and Fajr alarms for event $action")

                    val db = NoorDatabase.getDatabase(context)
                    val settingsRepo = SettingsRepository(context)
                    val prayerRepo = PrayerRepository(db.prayerDao(), db.prayerLogDao(), settingsRepo)
                    val notificationManager = PrayerNotificationManager(context, settingsRepo)

                    // 1. Always ensure Fajr alarm is authoritatively scheduled (uses SolarPrayerCalculator if entity not yet loaded)
                    val scheduledFajr = notificationManager.scheduleFajrAlarm()
                    Log.d(TAG, "BootReceiver: Fajr alarm verified and scheduled: $scheduledFajr")

                    // 2. Fetch or load cached prayer times for all other prayers
                    val prayerEntity = prayerRepo.getLatestPrayerTimes().firstOrNull()
                        ?: prayerRepo.refreshPrayerTimes(force = false).getOrNull()

                    if (prayerEntity != null) {
                        notificationManager.schedulePrayerAlarms(prayerEntity)
                        Log.d(TAG, "BootReceiver: Daily prayer schedule active for date ${prayerEntity.date}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "BootReceiver failed to reschedule alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val TAG = "SafaFajrNotification"
    }
}
