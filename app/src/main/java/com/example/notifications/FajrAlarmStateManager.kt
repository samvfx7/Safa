package com.example.notifications

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * State machine representing the live states of the Fajr Alarm experience.
 */
enum class FajrAlarmFlowState {
    ALARM_RINGING,
    WUDU,
    READY_TO_PRAY,
    COMPLETED
}

/**
 * Persistent state manager for the live Fajr Alarm flow.
 * Ensures state is preserved across configuration changes, process death, and Activity recreation.
 */
class FajrAlarmStateManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _flowState = MutableStateFlow(getSavedState())
    val flowState: StateFlow<FajrAlarmFlowState> = _flowState.asStateFlow()

    fun getSavedState(): FajrAlarmFlowState {
        val stateName = prefs.getString(KEY_FLOW_STATE, FajrAlarmFlowState.ALARM_RINGING.name)
        return try {
            FajrAlarmFlowState.valueOf(stateName ?: FajrAlarmFlowState.ALARM_RINGING.name)
        } catch (e: Exception) {
            FajrAlarmFlowState.ALARM_RINGING
        }
    }

    fun getWuduStartTime(): Long {
        return prefs.getLong(KEY_WUDU_START_TIME, 0L)
    }

    fun transitionTo(newState: FajrAlarmFlowState, wuduStartTimeMs: Long? = null) {
        prefs.edit().apply {
            putString(KEY_FLOW_STATE, newState.name)
            if (newState == FajrAlarmFlowState.WUDU) {
                val startTime = wuduStartTimeMs ?: System.currentTimeMillis()
                putLong(KEY_WUDU_START_TIME, startTime)
            } else if (newState == FajrAlarmFlowState.COMPLETED || newState == FajrAlarmFlowState.ALARM_RINGING) {
                if (newState == FajrAlarmFlowState.COMPLETED) {
                    putLong(KEY_WUDU_START_TIME, 0L)
                }
            }
            apply()
        }
        _flowState.value = newState
    }

    fun resetState() {
        prefs.edit().clear().apply()
        _flowState.value = FajrAlarmFlowState.ALARM_RINGING
    }

    companion object {
        private const val PREF_NAME = "safa_fajr_alarm_state"
        private const val KEY_FLOW_STATE = "key_fajr_flow_state"
        private const val KEY_WUDU_START_TIME = "key_wudu_start_time"
    }
}
