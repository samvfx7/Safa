package com.example.ui.screens.more

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicApp
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.PrayerLogEntity
import com.example.data.local.entity.TasbihLogEntity
import com.example.data.repository.AppSettings
import com.example.data.repository.DhikrPreset
import com.example.data.repository.HadithRepository
import com.example.data.repository.PrayerRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.TasbihRepository
import com.example.data.repository.TasbihState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MoreUiState(
    val hadithList: List<HadithEntity> = emptyList(),
    val hadithCollections: List<String> = emptyList(),
    val selectedHadithCollection: String = "All",
    val hadithSearchQuery: String = "",
    val hadithOfTheDay: HadithEntity? = null,
    val prayerLogs: List<PrayerLogEntity> = emptyList(),
    val currentStreak: Int = 0,
    val totalPrayersOffered: Int = 0,
    val tasbihHistory: List<TasbihLogEntity> = emptyList()
)

data class ZakatCalculation(
    val cashOnHand: Double = 0.0,
    val goldValue: Double = 0.0,
    val silverValue: Double = 0.0,
    val investments: Double = 0.0,
    val liabilities: Double = 0.0,
    val goldNisabThreshold: Double = 6800.0, // e.g. 85g gold ~ $6,800
    val isEligible: Boolean = false,
    val totalZakatableWealth: Double = 0.0,
    val zakatPayable: Double = 0.0
)

class MoreViewModel(application: Application) : AndroidViewModel(application) {

    private val hadithRepository: HadithRepository = (application as IslamicApp).hadithRepository
    private val tasbihRepository: TasbihRepository = (application as IslamicApp).tasbihRepository
    private val prayerRepository: PrayerRepository = (application as IslamicApp).prayerRepository
    val settingsRepository: SettingsRepository = (application as IslamicApp).settingsRepository

    val settings: StateFlow<AppSettings> = settingsRepository.settingsState
    val tasbihState: StateFlow<TasbihState> = tasbihRepository.state

    private val _uiState = MutableStateFlow(
        MoreUiState(
            hadithCollections = hadithRepository.collections,
            hadithOfTheDay = hadithRepository.getHadithOfTheDay()
        )
    )
    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    private val _zakatState = MutableStateFlow(ZakatCalculation())
    val zakatState: StateFlow<ZakatCalculation> = _zakatState.asStateFlow()

    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    init {
        viewModelScope.launch {
            hadithRepository.preloadHadithsIfNeeded()
            loadHadiths()
        }

        viewModelScope.launch {
            prayerRepository.getAllPrayerLogs().collect { logs ->
                var total = 0
                for (log in logs) total += log.completedCount
                val streak = logs.firstOrNull()?.streak ?: 0

                _uiState.value = _uiState.value.copy(
                    prayerLogs = logs,
                    currentStreak = streak,
                    totalPrayersOffered = total
                )
            }
        }

        viewModelScope.launch {
            tasbihRepository.getAllHistory().collect { history ->
                _uiState.value = _uiState.value.copy(tasbihHistory = history)
            }
        }
    }

    // Hadith Logic
    fun selectHadithCollection(collection: String) {
        _uiState.value = _uiState.value.copy(selectedHadithCollection = collection)
        loadHadiths()
    }

    fun onHadithSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(hadithSearchQuery = query)
        loadHadiths()
    }

    private fun loadHadiths() {
        viewModelScope.launch {
            val query = _uiState.value.hadithSearchQuery.trim()
            val collection = _uiState.value.selectedHadithCollection

            val flow = if (query.isNotBlank()) {
                hadithRepository.searchHadiths(query)
            } else {
                hadithRepository.getHadithsByCollection(collection)
            }

            flow.collectLatest { list ->
                _uiState.value = _uiState.value.copy(hadithList = list)
            }
        }
    }

    fun toggleHadithFavorite(hadith: HadithEntity) {
        viewModelScope.launch {
            hadithRepository.toggleFavorite(hadith.id, !hadith.isFavorite)
        }
    }

    // Tasbih Logic
    val tasbihPresets = tasbihRepository.presets

    fun selectTasbihPreset(preset: DhikrPreset) {
        tasbihRepository.selectPreset(preset)
    }

    fun incrementTasbih() {
        viewModelScope.launch {
            val isCompleted = tasbihRepository.increment()
            if (settings.value.hapticFeedbackEnabled) {
                triggerHaptic(if (isCompleted) 120 else 30)
            }
        }
    }

    fun resetTasbih() {
        tasbihRepository.reset()
    }

    private fun triggerHaptic(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    // Zakat Calculator
    fun updateZakatInputs(
        cash: Double = _zakatState.value.cashOnHand,
        gold: Double = _zakatState.value.goldValue,
        silver: Double = _zakatState.value.silverValue,
        investments: Double = _zakatState.value.investments,
        liabilities: Double = _zakatState.value.liabilities
    ) {
        val totalAssets = cash + gold + silver + investments
        val netWealth = (totalAssets - liabilities).coerceAtLeast(0.0)
        val isEligible = netWealth >= _zakatState.value.goldNisabThreshold
        val payable = if (isEligible) netWealth * 0.025 else 0.0

        _zakatState.value = _zakatState.value.copy(
            cashOnHand = cash,
            goldValue = gold,
            silverValue = silver,
            investments = investments,
            liabilities = liabilities,
            totalZakatableWealth = netWealth,
            isEligible = isEligible,
            zakatPayable = payable
        )
    }
}
