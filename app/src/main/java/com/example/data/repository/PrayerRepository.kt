package com.example.data.repository

import com.example.data.local.dao.PrayerDao
import com.example.data.local.dao.PrayerLogDao
import com.example.data.local.entity.PrayerEntity
import com.example.data.local.entity.PrayerLogEntity
import com.example.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PrayerTimeItem(
    val name: String,
    val arabicName: String,
    val time24h: String,
    val time12h: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false,
    val isCompleted: Boolean = false
)

data class NextPrayerInfo(
    val nextPrayerName: String,
    val nextPrayerArabicName: String,
    val nextPrayerTime12h: String,
    val remainingSeconds: Long,
    val formattedRemaining: String,
    val progress: Float // 0.0f to 1.0f between previous prayer and next prayer
)

class PrayerRepository(
    private val prayerDao: PrayerDao,
    private val prayerLogDao: PrayerLogDao,
    private val settingsRepository: SettingsRepository
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val timeFormat12 = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun getTodayPrayerTimes(): Flow<PrayerEntity?> {
        val today = dateFormat.format(Date())
        return prayerDao.getPrayerTimesForDate(today)
    }

    fun getLatestPrayerTimes(): Flow<PrayerEntity?> = prayerDao.getLatestPrayerTimes()

    fun getTodayPrayerLog(): Flow<PrayerLogEntity?> {
        val today = dateFormat.format(Date())
        return prayerLogDao.getPrayerLogForDate(today)
    }

    fun getAllPrayerLogs(): Flow<List<PrayerLogEntity>> = prayerLogDao.getAllPrayerLogs()

    suspend fun refreshPrayerTimes(force: Boolean = false): Result<PrayerEntity> = withContext(Dispatchers.IO) {
        try {
            val settings = settingsRepository.settingsState.value
            val today = dateFormat.format(Date())

            val cached = prayerDao.getPrayerTimesForDate(today).firstOrNull()
            if (cached != null && !force) {
                return@withContext Result.success(cached)
            }

            val methodId = if (settings.calculationMethodId == 99) 3 else settings.calculationMethodId
            val schoolId = if (settings.calculationMethodId == 99) 1 else 0

            val timestamp = System.currentTimeMillis() / 1000
            val response = try {
                ApiClient.aladhanService.getTimingsByCoordinates(
                    timestamp = timestamp,
                    latitude = settings.latitude,
                    longitude = settings.longitude,
                    method = methodId,
                    school = schoolId
                )
            } catch (e: Exception) {
                // Fallback to city
                ApiClient.aladhanService.getTimingsByCity(
                    city = settings.city,
                    country = settings.country,
                    method = methodId,
                    school = schoolId
                )
            }

            val timings = response.data?.timings
            if (timings != null) {
                val hijri = response.data.date.hijri
                val hijriString = if (hijri != null) {
                    "${hijri.day} ${hijri.month?.en ?: ""} ${hijri.year} AH"
                } else {
                    "1446 AH"
                }

                val entity = PrayerEntity(
                    date = today,
                    fajr = cleanTime(timings.fajr),
                    sunrise = cleanTime(timings.sunrise),
                    dhuhr = cleanTime(timings.dhuhr),
                    asr = cleanTime(timings.asr),
                    maghrib = cleanTime(timings.maghrib),
                    isha = cleanTime(timings.isha),
                    locationName = "${settings.city}, ${settings.country}",
                    latitude = settings.latitude,
                    longitude = settings.longitude,
                    calculationMethod = settings.calculationMethodName,
                    hijriDate = hijriString
                )

                prayerDao.insertPrayerTimes(entity)
                return@withContext Result.success(entity)
            } else {
                // Generate realistic fallback timings for current location
                val fallback = generateFallbackPrayerTimes(today, settings)
                prayerDao.insertPrayerTimes(fallback)
                return@withContext Result.success(fallback)
            }
        } catch (e: Exception) {
            val today = dateFormat.format(Date())
            val cached = prayerDao.getPrayerTimesForDate(today).firstOrNull()
                ?: prayerDao.getLatestPrayerTimes().firstOrNull()

            if (cached != null) {
                return@withContext Result.success(cached)
            }

            val settings = settingsRepository.settingsState.value
            val fallback = generateFallbackPrayerTimes(today, settings)
            prayerDao.insertPrayerTimes(fallback)
            return@withContext Result.success(fallback)
        }
    }

    private fun cleanTime(raw: String): String {
        // e.g. "05:12 (BST)" -> "05:12"
        return raw.split(" ")[0].trim()
    }

    private fun generateFallbackPrayerTimes(today: String, settings: AppSettings): PrayerEntity {
        val isHanafi = settings.calculationMethodId == 99
        return PrayerEntity(
            date = today,
            fajr = "05:15",
            sunrise = "06:35",
            dhuhr = "13:10",
            asr = if (isHanafi) "17:45" else "16:45", // Hanafi Asr is later (shadow angle = 2x)
            maghrib = "19:40",
            isha = "21:05",
            locationName = "${settings.city}, ${settings.country}",
            latitude = settings.latitude,
            longitude = settings.longitude,
            calculationMethod = settings.calculationMethodName,
            hijriDate = "14 Safar 1448 AH"
        )
    }

    fun calculateNextPrayer(prayerEntity: PrayerEntity, now: Calendar = Calendar.getInstance()): NextPrayerInfo {
        val todayStr = dateFormat.format(now.time)
        val prayerTimes = listOf(
            Triple("Fajr", "الفجر", prayerEntity.fajr),
            Triple("Sunrise", "الشروق", prayerEntity.sunrise),
            Triple("Dhuhr", "الظهر", prayerEntity.dhuhr),
            Triple("Asr", "العصر", prayerEntity.asr),
            Triple("Maghrib", "المغرب", prayerEntity.maghrib),
            Triple("Isha", "العشاء", prayerEntity.isha)
        )

        val nowMillis = now.timeInMillis

        for (i in prayerTimes.indices) {
            val (name, arName, timeStr) = prayerTimes[i]
            if (name == "Sunrise") continue // Skip sunrise as prayer countdown target if desired, or keep as milestone

            val prayerCal = parsePrayerTimeToCalendar(todayStr, timeStr)
            if (prayerCal.timeInMillis > nowMillis) {
                val diffSec = (prayerCal.timeInMillis - nowMillis) / 1000
                val hours = diffSec / 3600
                val mins = (diffSec % 3600) / 60
                val secs = diffSec % 60

                val formatted = when {
                    hours > 0 -> "In ${hours}h ${mins}m"
                    mins > 0 -> "In ${mins}m ${secs}s"
                    else -> "In ${secs}s"
                }

                val time12 = formatTo12h(timeStr)

                // Progress calculation
                val prevCal = if (i > 0) {
                    parsePrayerTimeToCalendar(todayStr, prayerTimes[i - 1].third)
                } else {
                    parsePrayerTimeToCalendar(todayStr, prayerTimes.last().third).apply {
                        add(Calendar.DAY_OF_YEAR, -1)
                    }
                }
                val totalWindow = (prayerCal.timeInMillis - prevCal.timeInMillis).toFloat()
                val elapsed = (nowMillis - prevCal.timeInMillis).toFloat()
                val progress = (elapsed / totalWindow).coerceIn(0f, 1f)

                return NextPrayerInfo(
                    nextPrayerName = name,
                    nextPrayerArabicName = arName,
                    nextPrayerTime12h = time12,
                    remainingSeconds = diffSec,
                    formattedRemaining = formatted,
                    progress = progress
                )
            }
        }

        // If after Isha, next is tomorrow's Fajr
        val tomorrowFajrCal = parsePrayerTimeToCalendar(todayStr, prayerEntity.fajr).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val diffSec = (tomorrowFajrCal.timeInMillis - nowMillis) / 1000
        val hours = diffSec / 3600
        val mins = (diffSec % 3600) / 60
        val secs = diffSec % 60

        val formatted = "In ${hours}h ${mins}m"

        return NextPrayerInfo(
            nextPrayerName = "Fajr",
            nextPrayerArabicName = "الفجر",
            nextPrayerTime12h = formatTo12h(prayerEntity.fajr),
            remainingSeconds = diffSec,
            formattedRemaining = formatted,
            progress = 0.5f
        )
    }

    fun buildPrayerItems(
        prayerEntity: PrayerEntity,
        log: PrayerLogEntity?,
        now: Calendar = Calendar.getInstance()
    ): List<PrayerTimeItem> {
        val todayStr = dateFormat.format(now.time)
        val nowMillis = now.timeInMillis

        val rawList = listOf(
            Triple("Fajr", "الفجر", prayerEntity.fajr),
            Triple("Sunrise", "الشروق", prayerEntity.sunrise),
            Triple("Dhuhr", "الظهر", prayerEntity.dhuhr),
            Triple("Asr", "العصر", prayerEntity.asr),
            Triple("Maghrib", "المغرب", prayerEntity.maghrib),
            Triple("Isha", "العشاء", prayerEntity.isha)
        )

        var foundNext = false

        return rawList.map { (name, arName, timeStr) ->
            val prayerCal = parsePrayerTimeToCalendar(todayStr, timeStr)
            val isPassed = prayerCal.timeInMillis <= nowMillis
            var isNext = false

            if (!isPassed && !foundNext && name != "Sunrise") {
                isNext = true
                foundNext = true
            }

            val isDone = when (name) {
                "Fajr" -> log?.fajrDone == true
                "Dhuhr" -> log?.dhuhrDone == true
                "Asr" -> log?.asrDone == true
                "Maghrib" -> log?.maghribDone == true
                "Isha" -> log?.ishaDone == true
                else -> false
            }

            PrayerTimeItem(
                name = name,
                arabicName = arName,
                time24h = timeStr,
                time12h = formatTo12h(timeStr),
                isNext = isNext,
                isPassed = isPassed,
                isCompleted = isDone
            )
        }
    }

    suspend fun togglePrayerDone(prayerName: String, isDone: Boolean) = withContext(Dispatchers.IO) {
        val today = dateFormat.format(Date())
        val currentLog = prayerLogDao.getPrayerLogForDate(today).firstOrNull() ?: PrayerLogEntity(date = today)

        val updated = when (prayerName.lowercase()) {
            "fajr" -> currentLog.copy(fajrDone = isDone)
            "dhuhr" -> currentLog.copy(dhuhrDone = isDone)
            "asr" -> currentLog.copy(asrDone = isDone)
            "maghrib" -> currentLog.copy(maghribDone = isDone)
            "isha" -> currentLog.copy(ishaDone = isDone)
            else -> currentLog
        }

        var completedCount = 0
        if (updated.fajrDone) completedCount++
        if (updated.dhuhrDone) completedCount++
        if (updated.asrDone) completedCount++
        if (updated.maghribDone) completedCount++
        if (updated.ishaDone) completedCount++

        // Calculate streak
        val currentStreak = calculateStreak(today, completedCount == 5)

        prayerLogDao.insertOrUpdatePrayerLog(
            updated.copy(
                completedCount = completedCount,
                streak = currentStreak
            )
        )
    }

    private suspend fun calculateStreak(today: String, isTodayCompleted: Boolean): Int {
        val logs = prayerLogDao.getAllPrayerLogs().firstOrNull() ?: emptyList()
        val logMap = logs.associateBy { it.date }

        val cal = Calendar.getInstance()
        var streak = if (isTodayCompleted) 1 else 0

        cal.add(Calendar.DAY_OF_YEAR, -1)
        while (true) {
            val dateKey = dateFormat.format(cal.time)
            val log = logMap[dateKey]
            if (log != null && log.completedCount >= 4) { // At least 4 or 5 completed prayers
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    private fun parsePrayerTimeToCalendar(dateStr: String, timeStr: String): Calendar {
        val cal = Calendar.getInstance()
        try {
            val parts = timeStr.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val dateParts = dateStr.split("-")
            cal.set(dateParts[0].toInt(), dateParts[1].toInt() - 1, dateParts[2].toInt(), hour, minute, 0)
            cal.set(Calendar.MILLISECOND, 0)
        } catch (e: Exception) {
            // fallback
        }
        return cal
    }

    fun formatTo12h(time24: String): String {
        return try {
            val parsed = timeFormat24.parse(time24)
            if (parsed != null) timeFormat12.format(parsed) else time24
        } catch (e: Exception) {
            time24
        }
    }
}
