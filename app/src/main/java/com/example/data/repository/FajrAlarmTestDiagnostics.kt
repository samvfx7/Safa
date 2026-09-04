package com.example.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FajrAlarmTestResult(
    val timestampMs: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = false,
    val soundName: String = "",
    val soundResolved: String = "",
    val alarmTriggered: Boolean = true,
    val alarmUiOpened: Boolean = true,
    val soundLoaded: Boolean = false,
    val audioPlaying: Boolean = false,
    val alarmVolumePercent: Int = 60,
    val errorMessage: String? = null,
    val isDismissed: Boolean = false
)

object FajrAlarmTestDiagnostics {
    private val _latestResult = MutableStateFlow<FajrAlarmTestResult?>(null)
    val latestResult: StateFlow<FajrAlarmTestResult?> = _latestResult.asStateFlow()

    fun updateResult(result: FajrAlarmTestResult) {
        _latestResult.value = result
    }

    fun clear() {
        _latestResult.value = null
    }
}
