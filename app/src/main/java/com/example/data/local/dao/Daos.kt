package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.DuaEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.PrayerEntity
import com.example.data.local.entity.PrayerLogEntity
import com.example.data.local.entity.QuranAyahEntity
import com.example.data.local.entity.QuranSurahEntity
import com.example.data.local.entity.TasbihLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_times WHERE date = :date LIMIT 1")
    fun getPrayerTimesForDate(date: String): Flow<PrayerEntity?>

    @Query("SELECT * FROM prayer_times ORDER BY timestamp DESC LIMIT 1")
    fun getLatestPrayerTimes(): Flow<PrayerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTimes(prayer: PrayerEntity)

    @Query("DELETE FROM prayer_times WHERE date < :beforeDate")
    suspend fun clearOldPrayerTimes(beforeDate: String)
}

@Dao
interface PrayerLogDao {
    @Query("SELECT * FROM prayer_logs WHERE date = :date LIMIT 1")
    fun getPrayerLogForDate(date: String): Flow<PrayerLogEntity?>

    @Query("SELECT * FROM prayer_logs ORDER BY date DESC")
    fun getAllPrayerLogs(): Flow<List<PrayerLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayerLog(log: PrayerLogEntity)

    @Query("SELECT COUNT(*) FROM prayer_logs WHERE fajrDone = 1 AND dhuhrDone = 1 AND asrDone = 1 AND maghribDone = 1 AND ishaDone = 1")
    fun getFullPrayerCompletedDaysCount(): Flow<Int>
}

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_logs ORDER BY timestamp DESC")
    fun getAllTasbihLogs(): Flow<List<TasbihLogEntity>>

    @Query("SELECT * FROM tasbih_logs WHERE date = :date")
    fun getTasbihLogsForDate(date: String): Flow<List<TasbihLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbihLog(log: TasbihLogEntity)
}

@Dao
interface DuaDao {
    @Query("SELECT * FROM duas")
    fun getAllDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE category = :category")
    fun getDuasByCategory(category: String): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE isFavorite = 1")
    fun getFavoriteDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE title LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'")
    fun searchDuas(query: String): Flow<List<DuaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuas(duas: List<DuaEntity>)

    @Query("UPDATE duas SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}

@Dao
interface HadithDao {
    @Query("SELECT * FROM hadiths")
    fun getAllHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE collection = :collection")
    fun getHadithsByCollection(collection: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE isFavorite = 1")
    fun getFavoriteHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE translation LIKE '%' || :query || '%' OR narrator LIKE '%' || :query || '%'")
    fun searchHadiths(query: String): Flow<List<HadithEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadiths(hadiths: List<HadithEntity>)

    @Query("UPDATE hadiths SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY savedDate DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE type = :type ORDER BY savedDate DESC")
    fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE referenceId = :refId AND type = :type)")
    fun isBookmarked(refId: String, type: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE referenceId = :refId AND type = :type")
    suspend fun deleteBookmarkByRef(refId: String, type: String)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)
}

@Dao
interface QuranDao {
    @Query("SELECT * FROM quran_surahs ORDER BY number ASC")
    fun getAllSurahs(): Flow<List<QuranSurahEntity>>

    @Query("SELECT * FROM quran_surahs WHERE isDownloaded = 1 ORDER BY number ASC")
    fun getDownloadedSurahs(): Flow<List<QuranSurahEntity>>

    @Query("SELECT * FROM quran_surahs WHERE number = :number LIMIT 1")
    fun getSurahByNumber(number: Int): Flow<QuranSurahEntity?>

    @Query("SELECT * FROM quran_surahs WHERE number = :number LIMIT 1")
    suspend fun getSurahByNumberSync(number: Int): QuranSurahEntity?

    @Query("SELECT * FROM quran_ayahs WHERE surahNumber = :surahNumber ORDER BY numberInSurah ASC")
    fun getAyahsForSurah(surahNumber: Int): Flow<List<QuranAyahEntity>>

    @Query("SELECT * FROM quran_ayahs WHERE surahNumber = :surahNumber ORDER BY numberInSurah ASC")
    suspend fun getAyahsForSurahSync(surahNumber: Int): List<QuranAyahEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<QuranSurahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<QuranAyahEntity>)

    @Query("UPDATE quran_surahs SET isDownloaded = :isDownloaded, downloadedAt = :downloadedAt WHERE number = :number")
    suspend fun updateSurahDownloadStatus(number: Int, isDownloaded: Boolean, downloadedAt: Long)

    @Query("DELETE FROM quran_ayahs WHERE surahNumber = :surahNumber")
    suspend fun deleteAyahsForSurah(surahNumber: Int)

    @Query("SELECT COUNT(*) FROM quran_surahs WHERE isDownloaded = 1")
    fun getDownloadedSurahsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM quran_surahs")
    suspend fun getSurahsCount(): Int

    @Query("SELECT * FROM quran_ayahs WHERE translation LIKE '%' || :query || '%' OR transliteration LIKE '%' || :query || '%' OR arabicText LIKE '%' || :query || '%' LIMIT 50")
    fun searchAyahs(query: String): Flow<List<QuranAyahEntity>>
}

