package com.example.data.util

import android.util.Log
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

/**
 * Robust, deterministic astronomical prayer time calculator.
 * Used for offline scheduling, tomorrow's date calculations, and fallback
 * when network is unavailable or app process is woken by AlarmManager.
 */
object SolarPrayerCalculator {

    private const val TAG = "SafaFajrNotification"

    data class PrayerCalculationResult(
        val date: String,
        val fajr: String,
        val sunrise: String,
        val dhuhr: String,
        val asr: String,
        val maghrib: String,
        val isha: String
    )

    /**
     * Calculates the exact Fajr time (HH:mm) for any date, latitude, longitude, and calculation method.
     */
    fun calculateFajrTime(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        methodId: Int = 2,
        timeZoneId: String = TimeZone.getDefault().id
    ): String {
        val result = calculatePrayerTimes(calendar, latitude, longitude, methodId, isHanafi = false, timeZoneId)
        return result.fajr
    }

    /**
     * Calculates all daily prayer times for the specified date calendar.
     */
    fun calculatePrayerTimes(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        methodId: Int = 2,
        isHanafi: Boolean = false,
        timeZoneId: String = TimeZone.getDefault().id
    ): PrayerCalculationResult {
        val tz = TimeZone.getTimeZone(timeZoneId)
        val cal = Calendar.getInstance(tz).apply {
            timeInMillis = calendar.timeInMillis
        }

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Timezone offset in hours for this exact day (accounting for DST automatically)
        val tzOffsetHours = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / (1000.0 * 60.0 * 60.0)

        val jd = computeJulianDate(year, month, day)
        val d = jd - 2451545.0

        // Sun parameters
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(degToRad(g)) + 0.020 * sin(degToRad(2.0 * g)))
        val e = 23.439 - 0.00000036 * d

        val ra = fixAngle(radToDeg(atan2(cos(degToRad(e)) * sin(degToRad(l)), cos(degToRad(l)))))
        val delta = asin(sin(degToRad(e)) * sin(degToRad(l))) // declination in radians

        // Equation of time in hours
        val eqT = (q / 15.0) - (ra / 15.0)

        // Solar noon in local hours
        val noon = 12.0 + tzOffsetHours - (longitude / 15.0) - eqT

        // Twilight angle for Fajr based on calculation method
        val fajrAngle = getFajrAngle(methodId)
        val ishaAngle = getIshaAngle(methodId)

        // Fajr hour angle
        val fajrHourAngle = computeHourAngle(latitude, delta, fajrAngle)
        val fajrHours = if (fajrHourAngle != null) {
            noon - (fajrHourAngle / 15.0)
        } else {
            // High-latitude fallback (1/7th of night before sunrise)
            noon - 5.5
        }

        // Sunrise hour angle (center of sun 0.833 degrees below horizon)
        val sunriseAngle = 0.833
        val sunriseHourAngle = computeHourAngle(latitude, delta, sunriseAngle)
        val sunriseHours = if (sunriseHourAngle != null) {
            noon - (sunriseHourAngle / 15.0)
        } else {
            noon - 4.5
        }

        // Sunset / Maghrib hour angle
        val maghribHours = if (sunriseHourAngle != null) {
            noon + (sunriseHourAngle / 15.0)
        } else {
            noon + 4.5
        }

        // Asr hour angle (Shafi shadow = 1, Hanafi shadow = 2)
        val asrFactor = if (isHanafi) 2.0 else 1.0
        val asrAngleRad = -atan(1.0 / (asrFactor + tan(abs(degToRad(latitude) - delta))))
        val asrAngle = -radToDeg(asrAngleRad)
        val asrHourAngle = computeHourAngle(latitude, delta, asrAngle)
        val asrHours = if (asrHourAngle != null) {
            noon + (asrHourAngle / 15.0)
        } else {
            noon + 3.0
        }

        // Isha hour angle
        val ishaHours = if (methodId == 4 || methodId == 8 || methodId == 10) {
            // Makkah, Gulf, Qatar: Fixed 90 minutes after Maghrib
            maghribHours + 1.5
        } else {
            val ishaHourAngle = computeHourAngle(latitude, delta, ishaAngle)
            if (ishaHourAngle != null) {
                noon + (ishaHourAngle / 15.0)
            } else {
                maghribHours + 1.5
            }
        }

        val dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month, day)

        return PrayerCalculationResult(
            date = dateStr,
            fajr = formatHoursToTime(fajrHours),
            sunrise = formatHoursToTime(sunriseHours),
            dhuhr = formatHoursToTime(noon),
            asr = formatHoursToTime(asrHours),
            maghrib = formatHoursToTime(maghribHours),
            isha = formatHoursToTime(ishaHours)
        )
    }

    private fun getFajrAngle(methodId: Int): Double {
        return when (methodId) {
            1 -> 18.0 // Karachi
            2 -> 15.0 // ISNA
            3 -> 18.0 // MWL
            4 -> 18.5 // Umm Al-Qura, Makkah
            5 -> 19.5 // Egyptian General Authority
            7 -> 17.7 // Tehran
            8 -> 19.5 // Gulf
            9 -> 18.0 // Kuwait
            10 -> 18.0 // Qatar
            11 -> 20.0 // Singapore
            12 -> 12.0 // France UOIF
            13 -> 18.0 // Turkey
            14 -> 16.0 // Russia
            else -> 15.0 // Default ISNA
        }
    }

    private fun getIshaAngle(methodId: Int): Double {
        return when (methodId) {
            1 -> 18.0
            2 -> 15.0
            3 -> 17.0
            5 -> 17.5
            7 -> 14.0
            9 -> 17.5
            11 -> 18.0
            12 -> 12.0
            13 -> 17.0
            14 -> 15.0
            else -> 15.0
        }
    }

    private fun computeHourAngle(lat: Double, declinationRad: Double, angleDeg: Double): Double? {
        val latRad = degToRad(lat)
        val angleRad = degToRad(angleDeg)
        val cosH = (-sin(angleRad) - sin(latRad) * sin(declinationRad)) / (cos(latRad) * cos(declinationRad))
        return if (cosH in -1.0..1.0) {
            radToDeg(acos(cosH))
        } else {
            null
        }
    }

    private fun computeJulianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle - 360.0 * floor(angle / 360.0)
        if (a < 0.0) a += 360.0
        return a
    }

    private fun degToRad(degrees: Double): Double = degrees * (PI / 180.0)
    private fun radToDeg(radians: Double): Double = radians * (180.0 / PI)

    private fun formatHoursToTime(hoursRaw: Double): String {
        var hours = hoursRaw
        while (hours < 0.0) hours += 24.0
        while (hours >= 24.0) hours -= 24.0

        var h = floor(hours).toInt()
        var m = round((hours - floor(hours)) * 60.0).toInt()

        if (m >= 60) {
            m = 0
            h = (h + 1) % 24
        }

        return String.format(Locale.US, "%02d:%02d", h, m)
    }
}
