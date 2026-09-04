package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(
    val city: String = "London",
    val country: String = "United Kingdom",
    val latitude: Double = 51.5074,
    val longitude: Double = -0.1278,
    val calculationMethodId: Int = 2, // 2 = ISNA, 1 = Karachi, 3 = MWL, 4 = Makkah, 5 = Egyptian
    val calculationMethodName: String = "Islamic Society of North America (ISNA)",
    val isHanafiAsr: Boolean = false,
    val notifyFajr: Boolean = true,
    val notifyDhuhr: Boolean = true,
    val notifyAsr: Boolean = true,
    val notifyMaghrib: Boolean = true,
    val notifyIsha: Boolean = true,
    val reminderMinutesBefore: Int = 10, // 15, 10, 5
    val prayerMatDetectionEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val audioRecitationEnabled: Boolean = true,
    val language: String = "English",
    val isDarkMode: Boolean = false,
    val selectedTheme: String = "safa_sand", // "safa_sand", "safa_luxury", "safa_royal", "safa_light", "classic_warm"
    val fajrAlarmSound: String = "Makkah Adhan", // "Makkah Adhan", "Madinah Adhan", "Mishary Alafasy Adhan", "Soft Morning Chime", "Custom Sound"
    val fajrCustomSoundUri: String? = null
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("noor_app_settings", Context.MODE_PRIVATE)

    private val _settingsState = MutableStateFlow(loadSettings())
    val settingsState: StateFlow<AppSettings> = _settingsState.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            city = prefs.getString("city", "London") ?: "London",
            country = prefs.getString("country", "United Kingdom") ?: "United Kingdom",
            latitude = prefs.getFloat("lat", 51.5074f).toDouble(),
            longitude = prefs.getFloat("lng", -0.1278f).toDouble(),
            calculationMethodId = prefs.getInt("calc_method_id", 2),
            calculationMethodName = prefs.getString("calc_method_name", "Islamic Society of North America (ISNA)")
                ?: "Islamic Society of North America (ISNA)",
            isHanafiAsr = prefs.getBoolean("hanafi_asr", false),
            notifyFajr = prefs.getBoolean("notify_fajr", true),
            notifyDhuhr = prefs.getBoolean("notify_dhuhr", true),
            notifyAsr = prefs.getBoolean("notify_asr", true),
            notifyMaghrib = prefs.getBoolean("notify_maghrib", true),
            notifyIsha = prefs.getBoolean("notify_isha", true),
            reminderMinutesBefore = prefs.getInt("reminder_minutes", 10),
            prayerMatDetectionEnabled = prefs.getBoolean("mat_detection", true),
            hapticFeedbackEnabled = prefs.getBoolean("haptics", true),
            audioRecitationEnabled = prefs.getBoolean("audio", true),
            language = prefs.getString("language", "English") ?: "English",
            isDarkMode = prefs.getBoolean("dark_mode", false),
            selectedTheme = prefs.getString("selected_theme", "safa_sand") ?: "safa_sand",
            fajrAlarmSound = prefs.getString("fajr_alarm_sound", "Makkah Adhan") ?: "Makkah Adhan",
            fajrCustomSoundUri = prefs.getString("fajr_custom_sound_uri", null)
        )
    }

    fun updateFajrAlarmSound(soundName: String, customUri: String? = null) {
        prefs.edit()
            .putString("fajr_alarm_sound", soundName)
            .putString("fajr_custom_sound_uri", customUri)
            .apply()
        _settingsState.value = _settingsState.value.copy(
            fajrAlarmSound = soundName,
            fajrCustomSoundUri = customUri
        )
    }

    fun updateLocation(city: String, country: String, lat: Double, lng: Double) {
        prefs.edit()
            .putString("city", city)
            .putString("country", country)
            .putFloat("lat", lat.toFloat())
            .putFloat("lng", lng.toFloat())
            .apply()
        _settingsState.value = _settingsState.value.copy(
            city = city,
            country = country,
            latitude = lat,
            longitude = lng
        )
    }

    fun updateCalculationMethod(id: Int, name: String) {
        prefs.edit()
            .putInt("calc_method_id", id)
            .putString("calc_method_name", name)
            .apply()
        _settingsState.value = _settingsState.value.copy(
            calculationMethodId = id,
            calculationMethodName = name
        )
    }

    fun updateAsrMethod(isHanafi: Boolean) {
        prefs.edit()
            .putBoolean("hanafi_asr", isHanafi)
            .apply()
        _settingsState.value = _settingsState.value.copy(
            isHanafiAsr = isHanafi
        )
    }

    fun updateNotificationSetting(prayerName: String, enabled: Boolean) {
        val editor = prefs.edit()
        val current = _settingsState.value
        val updated = when (prayerName.lowercase()) {
            "fajr" -> {
                editor.putBoolean("notify_fajr", enabled)
                current.copy(notifyFajr = enabled)
            }
            "dhuhr" -> {
                editor.putBoolean("notify_dhuhr", enabled)
                current.copy(notifyDhuhr = enabled)
            }
            "asr" -> {
                editor.putBoolean("notify_asr", enabled)
                current.copy(notifyAsr = enabled)
            }
            "maghrib" -> {
                editor.putBoolean("notify_maghrib", enabled)
                current.copy(notifyMaghrib = enabled)
            }
            "isha" -> {
                editor.putBoolean("notify_isha", enabled)
                current.copy(notifyIsha = enabled)
            }
            else -> current
        }
        editor.apply()
        _settingsState.value = updated
    }

    fun updateReminderTiming(minutes: Int) {
        prefs.edit().putInt("reminder_minutes", minutes).apply()
        _settingsState.value = _settingsState.value.copy(reminderMinutesBefore = minutes)
    }

    fun updateMatDetection(enabled: Boolean) {
        prefs.edit().putBoolean("mat_detection", enabled).apply()
        _settingsState.value = _settingsState.value.copy(prayerMatDetectionEnabled = enabled)
    }

    fun updateHaptics(enabled: Boolean) {
        prefs.edit().putBoolean("haptics", enabled).apply()
        _settingsState.value = _settingsState.value.copy(hapticFeedbackEnabled = enabled)
    }

    fun updateLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        _settingsState.value = _settingsState.value.copy(language = lang)
    }

    fun updateDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean("dark_mode", isDark).apply()
        _settingsState.value = _settingsState.value.copy(isDarkMode = isDark)
    }

    fun updateTheme(themeName: String) {
        prefs.edit().putString("selected_theme", themeName).apply()
        _settingsState.value = _settingsState.value.copy(selectedTheme = themeName)
    }
}
