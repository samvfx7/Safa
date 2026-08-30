package com.example.ui.screens.prayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.IslamicApp
import com.example.data.local.entity.PrayerEntity
import com.example.data.local.entity.PrayerLogEntity
import com.example.data.repository.AppSettings
import com.example.data.repository.NextPrayerInfo
import com.example.data.repository.PrayerRepository
import com.example.data.repository.PrayerTimeItem
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class PrayerUiState(
    val prayerEntity: PrayerEntity? = null,
    val prayerItems: List<PrayerTimeItem> = emptyList(),
    val nextPrayerInfo: NextPrayerInfo? = null,
    val todayLog: PrayerLogEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showCityDialog: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val isFetchingGps: Boolean = false
)

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val prayerRepository: PrayerRepository = (application as IslamicApp).prayerRepository
    private val settingsRepository: SettingsRepository = (application as IslamicApp).settingsRepository
    private val permissionManager: com.example.sensor.PermissionManager = (application as IslamicApp).permissionManager
    private val qiblaCompassManager: com.example.sensor.QiblaCompassManager = (application as IslamicApp).qiblaCompassManager

    val settings: StateFlow<AppSettings> = settingsRepository.settingsState
    val permissionState: StateFlow<com.example.sensor.AppPermissionState> = permissionManager.permissionState

    private val _uiState = MutableStateFlow(
        PrayerUiState(
            isLoading = true,
            showPermissionDialog = permissionManager.shouldShowInitialPrompt()
        )
    )
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    init {
        loadData()
        startLiveCountdownTicker()
    }

    fun loadData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = prayerRepository.refreshPrayerTimes(forceRefresh)

            result.onSuccess { entity ->
                val now = Calendar.getInstance()
                val nextInfo = prayerRepository.calculateNextPrayer(entity, now)
                val items = prayerRepository.buildPrayerItems(entity, _uiState.value.todayLog, now)
                
                // Schedule local notifications for prayers
                val app = getApplication<IslamicApp>()
                app.prayerNotificationManager.schedulePrayerAlarms(entity)

                _uiState.value = _uiState.value.copy(
                    prayerEntity = entity,
                    prayerItems = items,
                    nextPrayerInfo = nextInfo,
                    isLoading = false
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.localizedMessage ?: "Failed to load prayer times"
                )
            }
        }

        // Observe prayer logs
        viewModelScope.launch {
            prayerRepository.getTodayPrayerLog().collect { log ->
                val entity = _uiState.value.prayerEntity
                if (entity != null) {
                    val now = Calendar.getInstance()
                    val items = prayerRepository.buildPrayerItems(entity, log, now)
                    _uiState.value = _uiState.value.copy(
                        todayLog = log,
                        prayerItems = items
                    )
                }
            }
        }
    }

    private fun startLiveCountdownTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val entity = _uiState.value.prayerEntity
                if (entity != null) {
                    val now = Calendar.getInstance()
                    val nextInfo = prayerRepository.calculateNextPrayer(entity, now)
                    val items = prayerRepository.buildPrayerItems(entity, _uiState.value.todayLog, now)
                    _uiState.value = _uiState.value.copy(
                        nextPrayerInfo = nextInfo,
                        prayerItems = items
                    )
                }
            }
        }
    }

    fun togglePrayerCompleted(prayerName: String, isCompleted: Boolean) {
        viewModelScope.launch {
            prayerRepository.togglePrayerDone(prayerName, isCompleted)
        }
    }

    fun openCityDialog() {
        _uiState.value = _uiState.value.copy(showCityDialog = true)
    }

    fun closeCityDialog() {
        _uiState.value = _uiState.value.copy(showCityDialog = false)
    }

    fun openPermissionDialog() {
        _uiState.value = _uiState.value.copy(showPermissionDialog = true)
    }

    fun closePermissionDialog() {
        permissionManager.markInitialPromptShown()
        _uiState.value = _uiState.value.copy(showPermissionDialog = false)
    }

    fun updateCity(city: String, country: String, lat: Double, lng: Double) {
        settingsRepository.updateLocation(city, country, lat, lng)
        qiblaCompassManager.updateCoordinates(lat, lng)
        closeCityDialog()
        loadData(forceRefresh = true)
    }

    fun fetchGpsLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFetchingGps = true)
            val result = permissionManager.getDeviceCurrentLocation()
            if (result != null) {
                settingsRepository.updateLocation(
                    city = result.city,
                    country = result.country,
                    lat = result.latitude,
                    lng = result.longitude
                )
                qiblaCompassManager.updateCoordinates(result.latitude, result.longitude)
                loadData(forceRefresh = true)
            }
            _uiState.value = _uiState.value.copy(isFetchingGps = false)
        }
    }
}
