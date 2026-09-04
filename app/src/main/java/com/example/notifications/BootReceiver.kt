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
        Log.d("FajrAlarmPipeline", "BootReceiver triggered with action: $action")

        val isRelevantAction = action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                action == Intent.ACTION_MY_PACKAGE_REPLACED ||
                action == Intent.ACTION_TIME_CHANGED ||
                action == Intent.ACTION_TIMEZONE_CHANGED ||
                action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED

        if (isRelevantAction) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("FajrAlarmPipeline", "BootReceiver: Rescheduling prayer alarms for action $action")

                    val db = NoorDatabase.getDatabase(context)
                    val settingsRepo = SettingsRepository(context)
                    val prayerRepo = PrayerRepository(db.prayerDao(), db.prayerLogDao(), settingsRepo)
                    val notificationManager = PrayerNotificationManager(context, settingsRepo)

                    val prayerEntity = prayerRepo.refreshPrayerTimes(force = true).getOrNull()
                        ?: prayerRepo.getLatestPrayerTimes().firstOrNull()

                    if (prayerEntity != null) {
                        notificationManager.schedulePrayerAlarms(prayerEntity)
                        Log.d("FajrAlarmPipeline", "BootReceiver successfully rescheduled alarms for date ${prayerEntity.date}")
                    } else {
                        Log.w("FajrAlarmPipeline", "BootReceiver: No cached or fetched prayer entity found to schedule")
                    }
                } catch (e: Exception) {
                    Log.e("FajrAlarmPipeline", "BootReceiver failed to reschedule alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
