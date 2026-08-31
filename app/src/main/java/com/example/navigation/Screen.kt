package com.example.navigation

sealed class Screen(val route: String, val title: String) {
    // 5 Main Tabs
    data object PrayerTimes : Screen("prayer_times", "Prayer Times")
    data object Quran : Screen("quran", "Quran")
    data object Qibla : Screen("qibla", "Qibla")
    data object Duas : Screen("duas", "Duas")
    data object More : Screen("more", "More")

    // Sub-screens & Features
    data object FajrAlarm : Screen("fajr_alarm", "Fajr Alarm")
    data object SurahDetail : Screen("surah_detail/{surahNumber}", "Surah") {
        fun createRoute(surahNumber: Int) = "surah_detail/$surahNumber"
    }
    data object Hadith : Screen("hadith", "Hadith Collection")
    data object Tasbih : Screen("tasbih", "Digital Tasbih")
    data object PrayerStreak : Screen("prayer_streak", "Prayer Streak")
    data object IslamicLearning : Screen("islamic_learning", "Islamic Learning")
    data object ZakatCalculator : Screen("zakat_calculator", "Zakat Calculator")
    data object FastingTracker : Screen("fasting_tracker", "Fasting & Ramadan")
    data object MasjidFinder : Screen("masjid_finder", "Masjid Finder")
    data object Settings : Screen("settings", "Settings")
    data object Bookmarks : Screen("bookmarks", "Saved Bookmarks")
    data object WuduTimer : Screen("wudu_timer", "Wudu Timer")
    data object Auth : Screen("auth", "Account & Streak Sync")
}
