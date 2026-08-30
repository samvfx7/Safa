package com.example

import android.app.Application
import com.example.audio.AudioPlayerHelper
import com.example.data.local.NoorDatabase
import com.example.data.repository.BookmarkRepository
import com.example.data.repository.DuaRepository
import com.example.data.repository.HadithRepository
import com.example.data.repository.PrayerRepository
import com.example.data.repository.QuranRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.TasbihRepository
import com.example.notifications.PrayerNotificationManager
import com.example.sensor.QiblaCompassManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class IslamicApp : Application() {

    lateinit var database: NoorDatabase
        private set

    lateinit var prayerRepository: PrayerRepository
        private set

    lateinit var quranRepository: QuranRepository
        private set

    lateinit var duaRepository: DuaRepository
        private set

    lateinit var hadithRepository: HadithRepository
        private set

    lateinit var tasbihRepository: TasbihRepository
        private set

    lateinit var bookmarkRepository: BookmarkRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var audioPlayerHelper: AudioPlayerHelper
        private set

    lateinit var qiblaCompassManager: QiblaCompassManager
        private set

    lateinit var permissionManager: com.example.sensor.PermissionManager
        private set

    lateinit var prayerNotificationManager: PrayerNotificationManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = NoorDatabase.getDatabase(this)
        settingsRepository = SettingsRepository(this)
        prayerNotificationManager = PrayerNotificationManager(this, settingsRepository)
        prayerRepository = PrayerRepository(database.prayerDao(), database.prayerLogDao(), settingsRepository)
        quranRepository = QuranRepository(database.quranDao(), database.bookmarkDao())
        duaRepository = DuaRepository(database.duaDao())
        hadithRepository = HadithRepository(database.hadithDao())
        tasbihRepository = TasbihRepository(database.tasbihDao())
        bookmarkRepository = BookmarkRepository(database.bookmarkDao())
        audioPlayerHelper = AudioPlayerHelper(this)
        qiblaCompassManager = QiblaCompassManager(this)
        permissionManager = com.example.sensor.PermissionManager(this)

        // Reschedule alarms when settings change
        CoroutineScope(Dispatchers.IO).launch {
            settingsRepository.settingsState.collect {
                val latest = prayerRepository.getLatestPrayerTimes().firstOrNull()
                if (latest != null) {
                    prayerNotificationManager.schedulePrayerAlarms(latest)
                }
            }
        }
    }

    companion object {
        lateinit var instance: IslamicApp
            private set
    }
}
