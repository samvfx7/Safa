package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
data class PrayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: yyyy-MM-dd
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val calculationMethod: String,
    val hijriDate: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "prayer_logs")
data class PrayerLogEntity(
    @PrimaryKey
    val date: String, // Format: yyyy-MM-dd
    val fajrDone: Boolean = false,
    val dhuhrDone: Boolean = false,
    val asrDone: Boolean = false,
    val maghribDone: Boolean = false,
    val ishaDone: Boolean = false,
    val completedCount: Int = 0,
    val streak: Int = 0
)

@Entity(tableName = "tasbih_logs")
data class TasbihLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val dhikrName: String,
    val count: Int,
    val target: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "duas")
data class DuaEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val title: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val source: String,
    val benefit: String = "",
    val audioUrl: String? = null,
    val isFavorite: Boolean = false
)

@Entity(tableName = "hadiths")
data class HadithEntity(
    @PrimaryKey
    val id: String,
    val collection: String,
    val bookNumber: Int = 1,
    val hadithNumber: Int = 1,
    val narrator: String,
    val arabicText: String,
    val translation: String,
    val authenticity: String, // e.g. Sahih, Hasan
    val chapter: String = "",
    val isFavorite: Boolean = false
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "QURAN_AYAH", "DUA", "HADITH"
    val referenceId: String, // e.g. "1:1" for Surah 1 Ayah 1
    val title: String,
    val arabicText: String,
    val translation: String,
    val surahNumber: Int = 0,
    val ayahNumber: Int = 0,
    val savedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_surahs")
data class QuranSurahEntity(
    @PrimaryKey
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String, // "Meccan" or "Medinan"
    val revelationOrder: Int,
    val description: String = "",
    val isDownloaded: Boolean = false,
    val downloadedAt: Long = 0L
)

@Entity(
    tableName = "quran_ayahs",
    indices = [androidx.room.Index(value = ["surahNumber"])]
)
data class QuranAyahEntity(
    @PrimaryKey
    val id: String, // e.g. "1:1"
    val surahNumber: Int,
    val numberInSurah: Int,
    val overallNumber: Int,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val audioUrl: String = "",
    val isOfflineAvailable: Boolean = true
)

