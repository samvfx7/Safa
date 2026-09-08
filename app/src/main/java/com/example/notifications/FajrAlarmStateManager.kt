package com.example.notifications

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator
import com.example.audio.AudioPlayerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * State machine representing the live states of the Fajr Alarm experience:
 * IDLE -> ALARM_RINGING -> WUDU -> READY_TO_PRAY -> COMPLETED
 */
enum class FajrAlarmFlowState {
    IDLE,
    ALARM_RINGING,
    WUDU,
    READY_TO_PRAY,
    COMPLETED
}

/**
 * Persistent state manager for the live Fajr Alarm flow.
 * Ensures state is preserved across configuration changes, process death, and Activity recreation.
 */
class FajrAlarmStateManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _flowState = MutableStateFlow(getSavedState())
    val flowState: StateFlow<FajrAlarmFlowState> = _flowState.asStateFlow()

    fun getSavedState(): FajrAlarmFlowState {
        val stateName = prefs.getString(KEY_FLOW_STATE, FajrAlarmFlowState.IDLE.name)
        val state = try {
            FajrAlarmFlowState.valueOf(stateName ?: FajrAlarmFlowState.IDLE.name)
        } catch (e: Exception) {
            FajrAlarmFlowState.IDLE
        }

        // Safeguard against abandoned/stale Wudu sessions:
        // If WUDU was started over 2 hours ago, auto-reset to IDLE so the app is ready for future alarms
        if (state == FajrAlarmFlowState.WUDU) {
            val wuduStart = getWuduStartTime()
            if (wuduStart > 0 && (System.currentTimeMillis() - wuduStart) > 2 * 60 * 60 * 1000L) {
                resetState()
                return FajrAlarmFlowState.IDLE
            }
        }
        return state
    }

    fun getWuduStartTime(): Long {
        return prefs.getLong(KEY_WUDU_START_TIME, 0L)
    }

    fun isWuduActive(): Boolean {
        return getSavedState() == FajrAlarmFlowState.WUDU
    }

    fun transitionTo(newState: FajrAlarmFlowState, wuduStartTimeMs: Long? = null) {
        prefs.edit().apply {
            putString(KEY_FLOW_STATE, newState.name)
            if (newState == FajrAlarmFlowState.WUDU) {
                val startTime = wuduStartTimeMs ?: System.currentTimeMillis()
                putLong(KEY_WUDU_START_TIME, startTime)
            } else if (newState == FajrAlarmFlowState.COMPLETED || newState == FajrAlarmFlowState.IDLE) {
                putLong(KEY_WUDU_START_TIME, 0L)
            }
            apply()
        }
        _flowState.value = newState
    }

    /**
     * Authoritatively silences the active Fajr alarm and transitions to WUDU state:
     * 1. Stops audio player immediately
     * 2. Cancels hardware vibration
     * 3. Dismisses the active ringing notifications
     * 4. Cancels any pending snooze alarms in AlarmManager
     * 5. Persists the state transition to WUDU
     */
    fun silenceAndEnterWudu(
        audioPlayer: AudioPlayerHelper? = null,
        notificationId: Int? = null
    ) {
        // 1. Immediately stop audio
        try {
            audioPlayer?.stop()
        } catch (e: Exception) {
            // ignore
        }

        // 2. Immediately stop vibration
        try {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.cancel()
        } catch (e: Exception) {
            // ignore
        }

        // 3. Immediately dismiss ringing notification
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (notificationId != null && notificationId != 0) {
                notificationManager?.cancel(notificationId)
            }
            notificationManager?.cancel(PrayerNotificationManager.NOTIFICATION_ID_FAJR)
            notificationManager?.cancel(PrayerNotificationManager.NOTIFICATION_ID_TEST)
        } catch (e: Exception) {
            // ignore
        }

        // 4. Cancel any pending snooze alarm in AlarmManager
        try {
            PrayerAlarmReceiver.cancelSnoozeAlarm(context, "Fajr")
        } catch (e: Exception) {
            // ignore
        }

        // 5. Persist transition
        transitionTo(FajrAlarmFlowState.WUDU)
    }

    fun resetState() {
        prefs.edit().clear().apply()
        _flowState.value = FajrAlarmFlowState.IDLE
    }

    companion object {
        private const val PREF_NAME = "safa_fajr_alarm_state"
        private const val KEY_FLOW_STATE = "key_fajr_flow_state"
        private const val KEY_WUDU_START_TIME = "key_wudu_start_time"
    }
}
