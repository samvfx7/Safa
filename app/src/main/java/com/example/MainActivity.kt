package com.example

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.notifications.PrayerAlarmReceiver
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.IslamicAppTheme
import com.example.navigation.Screen

class MainActivity : ComponentActivity() {

    private var initialRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as IslamicApp
        
        handleIntent(intent)

        setContent {
            val settings by app.settingsRepository.settingsState.collectAsState()
            IslamicAppTheme(
                selectedTheme = settings.selectedTheme,
                darkTheme = settings.isDarkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(startDestinationOverride = initialRoute)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == PrayerAlarmReceiver.ACTION_SNOOZE_WUDU) {
            val prayerName = intent.getStringExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME) ?: "Prayer"
            val notificationId = intent.getIntExtra(PrayerAlarmReceiver.EXTRA_NOTIFICATION_ID, 0)
            
            // Cancel notification
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)

            // Schedule the snooze broadcast
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val snoozeIntent = Intent(this, PrayerAlarmReceiver::class.java).apply {
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerName)
                putExtra(PrayerAlarmReceiver.EXTRA_IS_SNOOZE, true)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                prayerName.hashCode() + 100,
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
                // Fallback
            }
        }

        val startDest = intent.getStringExtra("START_DESTINATION")
        if (startDest != null) {
            initialRoute = startDest
        }
    }
}
