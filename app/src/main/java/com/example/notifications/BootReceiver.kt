package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.data.local.NoorDatabase
import com.example.data.repository.PrayerRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.firstOrNull

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted, rescheduling prayer alarms")
            
            val db = NoorDatabase.getDatabase(context)
            val settingsRepo = SettingsRepository(context)
            val prayerRepo = PrayerRepository(db.prayerDao(), db.prayerLogDao(), settingsRepo)
            val notificationManager = PrayerNotificationManager(context, settingsRepo)
            
            CoroutineScope(Dispatchers.IO).launch {
                val prayerEntity = prayerRepo.getLatestPrayerTimes().firstOrNull()
                if (prayerEntity != null) {
                    notificationManager.schedulePrayerAlarms(prayerEntity)
                }
            }
        }
    }
}
