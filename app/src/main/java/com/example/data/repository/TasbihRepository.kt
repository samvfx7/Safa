package com.example.data.repository

import com.example.data.local.dao.TasbihDao
import com.example.data.local.entity.TasbihLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DhikrPreset(
    val name: String,
    val arabic: String,
    val translation: String,
    val defaultTarget: Int
)

data class TasbihState(
    val selectedDhikr: DhikrPreset,
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val totalLaps: Int = 0,
    val isCompleted: Boolean = false
)

class TasbihRepository(
    private val tasbihDao: TasbihDao
) {
    val presets = listOf(
        DhikrPreset("Subhanallah", "سُبْحَانَ اللَّهِ", "Glory be to Allah", 33),
        DhikrPreset("Alhamdulillah", "الْحَمْدُ لِلَّهِ", "All praise is for Allah", 33),
        DhikrPreset("Allahu Akbar", "اللَّهُ أَكْبَرُ", "Allah is the Greatest", 33),
        DhikrPreset("La ilaha illallah", "لَا إِلَٰهَ إِلَّا اللَّهُ", "There is no deity except Allah", 100),
        DhikrPreset("Astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", "I seek forgiveness from Allah", 100),
        DhikrPreset("Salawat", "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ", "Blessings upon Prophet Muhammad", 100)
    )

    private val _state = MutableStateFlow(
        TasbihState(
            selectedDhikr = presets[0],
            currentCount = 0,
            targetCount = 33
        )
    )
    val state: StateFlow<TasbihState> = _state.asStateFlow()

    fun selectPreset(preset: DhikrPreset) {
        _state.value = _state.value.copy(
            selectedDhikr = preset,
            currentCount = 0,
            targetCount = preset.defaultTarget,
            totalLaps = 0,
            isCompleted = false
        )
    }

    suspend fun increment(): Boolean = withContext(Dispatchers.IO) {
        val current = _state.value
        val newCount = current.currentCount + 1
        val isNowCompleted = newCount >= current.targetCount

        val laps = if (isNowCompleted) current.totalLaps + 1 else current.totalLaps
        val finalCount = if (isNowCompleted) 0 else newCount

        _state.value = current.copy(
            currentCount = finalCount,
            totalLaps = laps,
            isCompleted = isNowCompleted
        )

        // Log completion to database
        if (isNowCompleted) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            tasbihDao.insertTasbihLog(
                TasbihLogEntity(
                    date = today,
                    dhikrName = current.selectedDhikr.name,
                    count = current.targetCount,
                    target = current.targetCount
                )
            )
        }

        isNowCompleted
    }

    fun reset() {
        _state.value = _state.value.copy(
            currentCount = 0,
            totalLaps = 0,
            isCompleted = false
        )
    }

    fun setTarget(newTarget: Int) {
        if (newTarget > 0) {
            _state.value = _state.value.copy(targetCount = newTarget)
        }
    }

    fun getAllHistory(): Flow<List<TasbihLogEntity>> = tasbihDao.getAllTasbihLogs()
}
