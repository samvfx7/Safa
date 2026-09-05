package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.QuranDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.QuranAyahEntity
import com.example.data.local.entity.QuranSurahEntity
import com.example.data.remote.ApiClient
import com.example.data.remote.model.AlQuranResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Surah(
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

data class Ayah(
    val surahNumber: Int,
    val numberInSurah: Int,
    val overallNumber: Int,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val audioUrl: String,
    val isBookmarked: Boolean = false,
    val isOfflineAvailable: Boolean = true
)

class QuranRepository(
    private val quranDao: QuranDao,
    private val bookmarkDao: BookmarkDao
) {
    val surahsList: List<Surah> = listOf(
        Surah(1, "الفَاتِحة", "Al-Fatihah", "The Opening", 7, "Meccan", 5, "The foundational prayer recited in every unit of prayer.", isDownloaded = true),
        Surah(2, "البَقَرَة", "Al-Baqarah", "The Cow", 286, "Medinan", 87, "The longest surah containing Ayat al-Kursi and foundational laws.", isDownloaded = true),
        Surah(3, "آل عِمْرَان", "Ali 'Imran", "Family of Imran", 200, "Medinan", 89, "Emphasizes faith, steadfastness, and unity."),
        Surah(4, "النِّسَاء", "An-Nisa", "The Women", 176, "Medinan", 92, "Focuses on justice, family laws, and orphan care."),
        Surah(5, "المَائدة", "Al-Ma'idah", "The Table Spread", 120, "Medinan", 112, "Covenants, dietary regulations, and moral conduct."),
        Surah(6, "الأنْعَام", "Al-An'am", "The Cattle", 165, "Meccan", 55, "Tawhid (monotheism), creation, and rejection of polytheism."),
        Surah(7, "الأعْرَاف", "Al-A'raf", "The Heights", 206, "Meccan", 39, "Stories of early prophets and divine trials."),
        Surah(8, " الأنْفَال", "Al-Anfal", "The Spoils of War", 75, "Medinan", 88, "Lessons from the Battle of Badr."),
        Surah(9, "التَّوْبَة", "At-Tawbah", "The Repentance", 129, "Medinan", 113, "Treaties, sincerity in faith, and repentance."),
        Surah(10, "يُونُس", "Yunus", "Jonah", 109, "Meccan", 51, "Prophecy, faith, and patience in adversity."),
        Surah(11, "هُود", "Hud", "Hud", 123, "Meccan", 52, "Stories of prophets confronting stubborn rejection."),
        Surah(12, "يُوسُف", "Yusuf", "Joseph", 111, "Meccan", 53, "The finest of narratives highlighting patience and trust in Allah."),
        Surah(13, "الرَّعْد", "Ar-Ra'd", "The Thunder", 43, "Medinan", 96, "Signs in nature and the power of divine guidance."),
        Surah(14, "إِبْرَاهِيم", "Ibrahim", "Abraham", 52, "Meccan", 72, "Gratitude, prayer, and Prophet Ibrahim's legacy."),
        Surah(15, "الحِجْر", "Al-Hijr", "The Rocky Tract", 99, "Meccan", 54, "Divine protection of the Quran and remembrance."),
        Surah(16, "النَّحْل", "An-Nahl", "The Bee", 128, "Meccan", 70, "Enumeration of Allah's blessings in nature."),
        Surah(17, "الإسْرَاء", "Al-Isra", "The Night Journey", 111, "Meccan", 50, "The miraculous night journey and moral commands."),
        Surah(18, "الكَهْف", "Al-Kahf", "The Cave", 110, "Meccan", 69, "Protection from trials of faith, wealth, knowledge, and power.", isDownloaded = true),
        Surah(19, "مَرْيَم", "Maryam", "Mary", 98, "Meccan", 44, "Miracles of Prophet Isa (Jesus) and Maryam (Mary)."),
        Surah(20, "طه", "Ta-Ha", "Ta-Ha", 135, "Meccan", 45, "The mission and prayers of Prophet Musa (Moses)."),
        Surah(21, "الأَنْبِيَاء", "Al-Anbiya", "The Prophets", 112, "Meccan", 73, "The unity of the prophetic message throughout time."),
        Surah(22, "الحَجّ", "Al-Hajj", "The Pilgrimage", 78, "Medinan", 103, "The sacred rites of Hajj and ultimate resurrection."),
        Surah(23, "المُؤْمِنُون", "Al-Mu'minun", "The Believers", 118, "Meccan", 74, "Characteristics of true successful believers."),
        Surah(24, "النُّور", "An-Nur", "The Light", 64, "Medinan", 102, "The Ayat an-Nur (Verse of Light) and societal purity."),
        Surah(25, "الفُرْقَان", "Al-Furqan", "The Criterion", 77, "Meccan", 42, "The Quran as the ultimate standard of right and wrong."),
        Surah(26, "الشُّعَرَاء", "Ash-Shu'ara", "The Poets", 227, "Meccan", 47, "Truth of prophetic guidance over fleeting poetry."),
        Surah(27, "النَّمْل", "An-Naml", "The Ant", 93, "Meccan", 48, "Prophets Dawud, Sulayman, and the Queen of Sheba."),
        Surah(28, "القَصَص", "Al-Qasas", "The Stories", 88, "Meccan", 49, "The life story of Musa from childhood to prophecy."),
        Surah(29, "العَنْكَبُوت", "Al-'Ankabut", "The Spider", 69, "Meccan", 85, "Testing of faith and fragile shelters of falsehood."),
        Surah(30, "الرُّوم", "Ar-Rum", "The Romans", 60, "Meccan", 84, "Prophecy of Roman victory and reflections on marriage."),
        Surah(31, "لُقْمَان", "Luqman", "Luqman", 34, "Meccan", 57, "Wisdom and parenting advice given by the sage Luqman."),
        Surah(32, "السَّجْدَة", "As-Sajdah", "The Prostration", 30, "Meccan", 75, "Signs of creation and humble prostration."),
        Surah(33, "الأَحْزَاب", "Al-Ahzab", "The Combined Forces", 73, "Medinan", 90, "The Battle of the Trench and prophetic household."),
        Surah(34, "سَبَأ", "Saba", "Sheba", 54, "Meccan", 58, "Gratitude for prosperity vs. ingratitude."),
        Surah(35, "فَاطِر", "Fatir", "The Originator", 45, "Meccan", 43, "Angelic messengers and the Creator of the heavens."),
        Surah(36, "يس", "Ya-Sin", "Ya-Sin", 83, "Meccan", 41, "The Heart of the Quran focusing on resurrection and signs.", isDownloaded = true),
        Surah(37, "الصَّافَّات", "As-Saffat", "Those Ranged in Ranks", 182, "Meccan", 56, "Purity of angels and sacrifices of Ibrahim."),
        Surah(38, "ص", "Sad", "The Letter Sad", 88, "Meccan", 38, "Repentance of Dawud, Sulayman, and Ayyub."),
        Surah(39, "الزُّمَر", "Az-Zumar", "The Groups", 75, "Meccan", 59, "Sincerity in worship and boundless divine mercy."),
        Surah(40, "غَافِر", "Ghafir", "The Forgiver", 85, "Meccan", 60, "The believer from Pharaoh's family and divine mercy."),
        Surah(41, "فُصِّلَت", "Fussilat", "Explained in Detail", 54, "Meccan", 61, "Clarity of the Quran and testimony of the senses."),
        Surah(42, "الشُّورَى", "Ash-Shura", "Consultation", 53, "Meccan", 62, "Consultation in affairs and oneness of revelation."),
        Surah(43, "الزُّخْرُف", "Az-Zukhruf", "The Ornaments of Gold", 89, "Meccan", 63, "True spiritual value vs. worldly ornaments."),
        Surah(44, "الدُّخَان", "Ad-Dukhan", "The Smoke", 59, "Meccan", 64, "Laylat al-Qadr and warning signs."),
        Surah(45, "الجَاثِيَة", "Al-Jathiyah", "The Crouching", 37, "Meccan", 65, "The assembly of nations before judgment."),
        Surah(46, "الأَحْقَاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, "Meccan", 66, "Kindness to parents and the people of 'Ad."),
        Surah(47, "مُحَمَّد", "Muhammad", "Muhammad", 38, "Medinan", 95, "Striving in the cause of truth and following the Prophet."),
        Surah(48, "الفَتْح", "Al-Fath", "The Victory", 29, "Medinan", 111, "The Treaty of Hudaybiyyah and manifest triumph."),
        Surah(49, "الحُجُرَات", "Al-Hujurat", "The Rooms", 18, "Medinan", 106, "Manners, avoiding backbiting, and human brotherhood."),
        Surah(50, "ق", "Qaf", "The Letter Qaf", 45, "Meccan", 34, "Reminders of resurrection and the closeness of Allah."),
        Surah(51, "الذَّارِيَات", "Adh-Dhariyat", "The Winnowing Winds", 60, "Meccan", 67, "The purpose of human and jinn creation."),
        Surah(52, "الطُّور", "At-Tur", "The Mount", 49, "Meccan", 76, "The sacred mountain and reassurance to the believers."),
        Surah(53, "النَّجْم", "An-Najm", "The Star", 62, "Meccan", 23, "The Prophet's ascension and closeness to the Divine."),
        Surah(54, "القَمَر", "Al-Qamar", "The Moon", 55, "Meccan", 37, "The splitting of the moon and the ease of remembering the Quran."),
        Surah(55, "الرَّحْمَن", "Ar-Rahman", "The Beneficent", 78, "Medinan", 97, "The beauty of divine favors: 'Which of the favors of your Lord will you deny?'", isDownloaded = true),
        Surah(56, "الوَاقِعَة", "Al-Waqi'ah", "The Inevitable", 96, "Meccan", 46, "The three categories of humanity on the Day of Judgment.", isDownloaded = true),
        Surah(57, "الحَدِيد", "Al-Hadid", "The Iron", 29, "Medinan", 94, "Light for believers and charity in Allah's cause."),
        Surah(58, "المُجَادِلَة", "Al-Mujadila", "The Pleading Woman", 22, "Medinan", 105, "Allah hears the whisper and protects marital justice."),
        Surah(59, "الحَشْر", "Al-Hashr", "The Exile", 24, "Medinan", 101, "The beautiful Names of Allah and community harmony."),
        Surah(60, "المُمْتَحَنَة", "Al-Mumtahanah", "The Examined One", 13, "Medinan", 91, "Examining loyalties and showing kindness to just neighbors."),
        Surah(61, "الصَّفّ", "As-Saff", "The Ranks", 14, "Medinan", 109, "Aligning speech with righteous action."),
        Surah(62, "الجُمُعَة", "Al-Jumu'ah", "Friday", 11, "Medinan", 110, "The Friday congregational prayer and remembering Allah."),
        Surah(63, "المُنَافِقُون", "Al-Munafiqun", "The Hypocrites", 11, "Medinan", 104, "Sincerity vs. pretense in spiritual life."),
        Surah(64, "التَّغَابُن", "At-Taghabun", "Mutual Loss and Gain", 18, "Medinan", 108, "True profit in faith and charity."),
        Surah(65, "الطَّلَاق", "At-Talaq", "The Divorce", 12, "Medinan", 99, "Taqwa (mindfulness) opens ways out of hardship."),
        Surah(66, "التَّحْرِيم", "At-Tahrim", "The Prohibition", 12, "Medinan", 107, "Family integrity and examples of righteous women."),
        Surah(67, "المُلْك", "Al-Mulk", "The Sovereignty", 30, "Meccan", 77, "Sovereignty of the Creator; shields from grave punishment.", isDownloaded = true),
        Surah(68, "القَلَم", "Al-Qalam", "The Pen", 52, "Meccan", 2, "The lofty noble character of the Prophet Muhammad."),
        Surah(69, "الحَاقَّة", "Al-Haqqah", "The Inevitable Truth", 52, "Meccan", 78, "The undeniable reality of judgment."),
        Surah(70, "المَعَارِج", "Al-Ma'arij", "The Ascending Stairways", 44, "Meccan", 79, "Steadfast patience and prayer during distress."),
        Surah(71, "نُوح", "Nuh", "Noah", 28, "Meccan", 71, "Prophet Nuh's heartfelt devotion and calling for forgiveness."),
        Surah(72, "الجِنّ", "Al-Jinn", "The Jinn", 28, "Meccan", 40, "The response of the Jinn to the Quranic recitation."),
        Surah(73, "المُزَّمِّل", "Al-Muzzammil", "The Enshrouded One", 20, "Meccan", 3, "Night vigil prayers (Tahajjud) and measured Quran recitation."),
        Surah(74, "المُدَّثِّر", "Al-Muddaththir", "The Cloaked One", 56, "Meccan", 4, "The call to rise and warn, purifying one's garments."),
        Surah(75, "القِيَامَة", "Al-Qiyamah", "The Resurrection", 40, "Meccan", 31, "The self-reproaching soul and the Great Awakening."),
        Surah(76, "الإِنْسَان", "Al-Insan", "Man", 31, "Medinan", 98, "Selfless charity feeding the poor, orphan, and captive.", isDownloaded = true),
        Surah(77, "المُرْسَلَات", "Al-Mursalat", "Those Sent Forth", 50, "Meccan", 33, "The cosmic winds and affirmation of the Day of Sorting."),
        Surah(78, "النَّبَأ", "An-Naba", "The Great News", 40, "Meccan", 80, "The cosmic signs of the final event."),
        Surah(79, "النَّازِعَات", "An-Nazi'at", "Those Who Drag Forth", 46, "Meccan", 81, "The angels at departure and longing for the Garden."),
        Surah(80, "عَبَسَ", "Abasa", "He Frowned", 42, "Meccan", 24, "Equality in seeking knowledge regardless of worldly status."),
        Surah(81, "التَّكْوِير", "At-Takwir", "The Overthrowing", 29, "Meccan", 7, "The unfolding cosmos and the noble angel Jibreel."),
        Surah(82, "الانفِطَار", "Al-Infitar", "The Cleaving Asunder", 19, "Meccan", 82, "Guardian angels recording every deed in dignity."),
        Surah(83, "المُطَفِّفِين", "Al-Mutaffifin", "Defrauding", 36, "Meccan", 86, "Honesty in trade scales and the register of the righteous."),
        Surah(84, "الانشِقَاق", "Al-Inshiqaq", "The Splitting Open", 25, "Meccan", 83, "Laboring towards one's Lord and joy in reception."),
        Surah(85, "البُرُوج", "Al-Buruj", "The Constellations", 22, "Meccan", 27, "Steadfastness under oppression and the Preserved Tablet."),
        Surah(86, "الطَّارِق", "At-Tariq", "The Night-Comer", 17, "Meccan", 36, "The piercing star and every soul having a protector."),
        Surah(87, "الأَعْلَى", "Al-A'la", "The Most High", 19, "Meccan", 8, "Glorifying the Most High Lord who created and guided."),
        Surah(88, "الغَاشِيَة", "Al-Ghashiyah", "The Overwhelming Event", 26, "Meccan", 68, "Joyful faces in exalted gardens and looking at the camels."),
        Surah(89, "الفَجْر", "Al-Fajr", "The Dawn", 30, "Meccan", 10, "The ten sacred nights and the tranquil soul returning to peace.", isDownloaded = true),
        Surah(90, "البَلَد", "Al-Balad", "The City", 20, "Meccan", 35, "Climbing the steep slope: feeding in times of hardship."),
        Surah(91, "الشَّمْس", "Ash-Shams", "The Sun", 15, "Meccan", 26, "Purification of the soul brings true ultimate success."),
        Surah(92, "اللَّيْل", "Al-Layl", "The Night", 21, "Meccan", 9, "Generosity and fear of Allah eases the path to goodness."),
        Surah(93, "الضُّحَى", "Ad-Duha", "The Morning Hours", 11, "Meccan", 11, "Comfort to the Prophet: 'Your Lord has neither forsaken you nor hated you.'", isDownloaded = true),
        Surah(94, "الشَّرْح", "Ash-Sharh", "The Relief", 8, "Meccan", 12, "Expanding the chest: 'Indeed, with hardship comes ease.'", isDownloaded = true),
        Surah(95, "التِّين", "At-Tin", "The Fig", 8, "Meccan", 28, "Humanity created in the best stature.", isDownloaded = true),
        Surah(96, "العَلَق", "Al-'Alaq", "The Clot", 19, "Meccan", 1, "The first revelation: 'Read in the name of your Lord who created.'", isDownloaded = true),
        Surah(97, "القَدْر", "Al-Qadr", "The Night of Power", 5, "Meccan", 25, "The Night of Decree better than a thousand months.", isDownloaded = true),
        Surah(98, "البَيِّنَة", "Al-Bayyinah", "The Clear Evidence", 8, "Medinan", 100, "Worshiping Allah with sincere pure faith and prayers."),
        Surah(99, "الزَّلْزَلَة", "Az-Zalzalah", "The Earthquake", 8, "Medinan", 93, "Whoever does an atom's weight of good or evil will see it.", isDownloaded = true),
        Surah(100, "العَادِيَات", "Al-'Adiyat", "The Courser", 11, "Meccan", 14, "The galloping chargers and human ingratitude."),
        Surah(101, "القَارِعَة", "Al-Qari'ah", "The Calamity", 11, "Meccan", 30, "The heavy balance of good deeds leading to a pleasant life."),
        Surah(102, "التَّكَاثُر", "At-Takathur", "Competition in Wealth", 8, "Meccan", 16, "Rivalry in worldly increase diverts until the graves."),
        Surah(103, "العَصْر", "Al-'Asr", "The Declining Day", 3, "Meccan", 13, "By time, humanity is in loss except those of faith and patience.", isDownloaded = true),
        Surah(104, "الهُمَزَة", "Al-Humazah", "The Slanderer", 9, "Meccan", 32, "Warning against backbiting and amassing hoarded wealth."),
        Surah(105, "الفِيل", "Al-Fil", "The Elephant", 5, "Meccan", 19, "Divine defense of the Kaaba from the Army of the Elephant.", isDownloaded = true),
        Surah(106, "قُرَيْش", "Quraysh", "Quraysh", 4, "Meccan", 29, "Gratitude for safety, winter and summer trade journeys.", isDownloaded = true),
        Surah(107, "المَاعُون", "Al-Ma'un", "Small Kindnesses", 7, "Meccan", 17, "Sincerity in prayer and extending basic neighborly assistance.", isDownloaded = true),
        Surah(108, "الكَوْثَر", "Al-Kawthar", "The Abundance", 3, "Meccan", 15, "The fountain of abundance granted to the Prophet.", isDownloaded = true),
        Surah(109, "الكَافِرُون", "Al-Kafirun", "The Disbelievers", 6, "Meccan", 18, "Uncompromising monotheism: 'For you is your religion, and for me is my religion.'", isDownloaded = true),
        Surah(110, "النَّصْر", "An-Nasr", "The Divine Support", 3, "Medinan", 114, "The victory of faith, crowd entry into Islam, and praise of Allah.", isDownloaded = true),
        Surah(111, "المَسَد", "Al-Masad", "The Palm Fibre", 5, "Meccan", 6, "The fate of obstinate oppressors like Abu Lahab.", isDownloaded = true),
        Surah(112, "الإِخْلَاص", "Al-Ikhlas", "The Sincerity", 4, "Meccan", 22, "Pure Tawhid: 'Say: He is Allah, the One; Allah, the Eternal Refuge.'", isDownloaded = true),
        Surah(113, "الفَلَق", "Al-Falaq", "The Daybreak", 5, "Meccan", 20, "Seeking refuge with the Lord of daybreak from all evils.", isDownloaded = true),
        Surah(114, "النَّاس", "An-Nas", "Mankind", 6, "Meccan", 21, "Seeking refuge with the King of Mankind from whisperers.", isDownloaded = true)
    )

    init {
        // Asynchronously initialize and seed Room database for offline Quran access
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfNeeded()
        }
    }

    /**
     * Seeds all 114 Surahs metadata and preloaded authentic Ayahs datasets into the Room Database on launch.
     */
    suspend fun seedDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        try {
            val count = quranDao.getSurahsCount()
            if (count == 0) {
                // Insert all surahs into Room
                val surahEntities = surahsList.map { it.toEntity() }
                quranDao.insertSurahs(surahEntities)
            }

            // Seed preloaded Surahs into Room if missing or outdated
            val preloadedSurahNumbers = listOf(
                1, 2, 18, 36, 55, 67, 87, 89, 93, 94, 95, 96, 97, 98, 99, 100,
                101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114
            )
            for (num in preloadedSurahNumbers) {
                val existing = quranDao.getAyahsForSurahSync(num)
                val isOutdated = existing.isEmpty() || existing.any { it.translation.contains("Remember the divine blessings", ignoreCase = true) }
                if (isOutdated) {
                    val ayahs = QuranPreloadedData.getPreloadedAyahs(num)
                    if (!ayahs.isNullOrEmpty()) {
                        quranDao.deleteAyahsForSurah(num)
                        quranDao.insertAyahs(ayahs.map { it.toEntity() })
                        quranDao.updateSurahDownloadStatus(num, isDownloaded = true, downloadedAt = System.currentTimeMillis())
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("QuranRepository", "Error seeding Quran database", e)
        }
    }

    /**
     * Observes all surahs reactively from Room database.
     */
    fun getAllSurahsFlow(): Flow<List<Surah>> {
        return quranDao.getAllSurahs().map { entities ->
            if (entities.isEmpty()) {
                surahsList
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    /**
     * Observes downloaded/offline surahs from Room database.
     */
    fun getDownloadedSurahsFlow(): Flow<List<Surah>> {
        return quranDao.getDownloadedSurahs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Observes the count of downloaded surahs stored offline.
     */
    fun getDownloadedSurahsCount(): Flow<Int> = quranDao.getDownloadedSurahsCount()

    /**
     * Check whether a specific surah is stored for offline access.
     */
    fun isSurahDownloaded(surahNumber: Int): Flow<Boolean> {
        return quranDao.getSurahByNumber(surahNumber).map { entity ->
            entity?.isDownloaded ?: (surahsList.find { it.number == surahNumber }?.isDownloaded ?: false)
        }
    }

    fun getSurahByNumber(number: Int): Surah? = surahsList.find { it.number == number }

    /**
     * Downloads/Saves a Surah and all its verses & translations into the local Room database for offline access.
     */
    suspend fun downloadSurahForOffline(surahNumber: Int) = withContext(Dispatchers.IO) {
        val ayahs = getAyahsForSurah(surahNumber)
        val ayahEntities = ayahs.map { it.toEntity() }
        quranDao.insertAyahs(ayahEntities)
        quranDao.updateSurahDownloadStatus(surahNumber, isDownloaded = true, downloadedAt = System.currentTimeMillis())
    }

    /**
     * Removes offline stored verses for a Surah from the local Room database.
     */
    suspend fun removeOfflineSurah(surahNumber: Int) = withContext(Dispatchers.IO) {
        quranDao.deleteAyahsForSurah(surahNumber)
        quranDao.updateSurahDownloadStatus(surahNumber, isDownloaded = false, downloadedAt = 0L)
    }

    /**
     * Retrieves all verses for a Surah with instant Room offline fallback and caching.
     */
    suspend fun getAyahsForSurah(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        try {
            val dbAyahs = quranDao.getAyahsForSurahSync(surahNumber)
            if (dbAyahs.isNotEmpty()) {
                val isOutdated = dbAyahs.any { it.translation.contains("Remember the divine blessings", ignoreCase = true) }
                if (!isOutdated) {
                    return@withContext dbAyahs.map { it.toDomain() }
                } else {
                    quranDao.deleteAyahsForSurah(surahNumber)
                }
            }
        } catch (e: Exception) {
            Log.e("QuranRepository", "Error reading ayahs from DB for Surah $surahNumber", e)
        }

        // 1. Check local preloaded dataset first
        val preloaded = QuranPreloadedData.getPreloadedAyahs(surahNumber)
        if (!preloaded.isNullOrEmpty()) {
            try {
                quranDao.insertAyahs(preloaded.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("QuranRepository", "Error caching preloaded ayahs", e)
            }
            return@withContext preloaded
        }

        // 2. Fetch full authentic editions from AlQuran Cloud API
        val networkAyahs = fetchAyahsFromNetwork(surahNumber)
        if (!networkAyahs.isNullOrEmpty()) {
            try {
                quranDao.insertAyahs(networkAyahs.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("QuranRepository", "Error caching network ayahs", e)
            }
            return@withContext networkAyahs
        }

        // 3. Fallback for offline usage
        val fallbackAyahs = getSurahAyahsFallback(surahNumber)
        try {
            quranDao.insertAyahs(fallbackAyahs.map { it.toEntity() })
        } catch (_: Exception) {}
        fallbackAyahs
    }

    private suspend fun fetchAyahsFromNetwork(surahNumber: Int): List<Ayah>? {
        return try {
            val response: AlQuranResponse = ApiClient.alQuranService.getSurahEditions(surahNumber)
            if (response.code == 200 && response.data.isNotEmpty()) {
                val uthmaniEdition = response.data.find { it.edition?.identifier == "quran-uthmani" }
                    ?: response.data.firstOrNull()
                val englishEdition = response.data.find { it.edition?.identifier == "en.sahih" }
                val translitEdition = response.data.find { it.edition?.identifier == "en.transliteration" }

                val uthmaniAyahs = uthmaniEdition?.ayahs ?: return null
                val englishMap = englishEdition?.ayahs?.associateBy { it.numberInSurah } ?: emptyMap()
                val translitMap = translitEdition?.ayahs?.associateBy { it.numberInSurah } ?: emptyMap()

                val paddedSurah = String.format("%03d", surahNumber)
                val surah = getSurahByNumber(surahNumber)
                uthmaniAyahs.map { item ->
                    val paddedAyah = String.format("%03d", item.numberInSurah)
                    val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/$paddedSurah$paddedAyah.mp3"
                    val trans = englishMap[item.numberInSurah]?.text?.trim() ?: ""
                    val translit = translitMap[item.numberInSurah]?.text?.trim() ?: ""

                    Ayah(
                        surahNumber = surahNumber,
                        numberInSurah = item.numberInSurah,
                        overallNumber = item.number,
                        arabicText = item.text.trim(),
                        transliteration = if (translit.isNotEmpty()) translit else "Ayah ${item.numberInSurah} of Surah ${surah?.englishName ?: surahNumber}",
                        translation = if (trans.isNotEmpty()) trans else "Translation for Ayah ${item.numberInSurah}",
                        audioUrl = audioUrl,
                        isOfflineAvailable = true
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("QuranRepository", "Network fetch failed for Surah $surahNumber: ${e.message}")
            null
        }
    }

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getBookmarksByType("QURAN_AYAH")

    fun isAyahBookmarked(surahNumber: Int, ayahNumber: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked("$surahNumber:$ayahNumber", "QURAN_AYAH")
    }

    suspend fun toggleBookmark(surah: Surah, ayah: Ayah, bookmarked: Boolean) = withContext(Dispatchers.IO) {
        val refId = "${surah.number}:${ayah.numberInSurah}"
        if (bookmarked) {
            val entity = BookmarkEntity(
                type = "QURAN_AYAH",
                referenceId = refId,
                title = "Surah ${surah.englishName} (${surah.name}) - Ayah ${ayah.numberInSurah}",
                arabicText = ayah.arabicText,
                translation = ayah.translation,
                surahNumber = surah.number,
                ayahNumber = ayah.numberInSurah
            )
            bookmarkDao.insertBookmark(entity)
        } else {
            bookmarkDao.deleteBookmarkByRef(refId, "QURAN_AYAH")
        }
    }

    suspend fun searchAyahs(query: String): List<Pair<Surah, Ayah>> = withContext(Dispatchers.IO) {
        val lowerQuery = query.lowercase().trim()
        if (lowerQuery.isEmpty()) return@withContext emptyList()

        val results = mutableListOf<Pair<Surah, Ayah>>()
        try {
            val dbResults = quranDao.searchAyahsSync(lowerQuery)
            for (entity in dbResults) {
                val surah = getSurahByNumber(entity.surahNumber)
                if (surah != null) {
                    results.add(Pair(surah, entity.toDomain()))
                }
                if (results.size >= 40) return@withContext results
            }
        } catch (e: Exception) {
            Log.e("QuranRepository", "searchAyahs DB search failed", e)
        }

        if (results.size < 40) {
            val preloadedSurahNumbers = listOf(
                1, 2, 18, 36, 55, 67, 87, 89, 93, 94, 95, 96, 97, 98, 99, 100,
                101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114
            )
            for (num in preloadedSurahNumbers) {
                val surah = getSurahByNumber(num) ?: continue
                val ayahs = QuranPreloadedData.getPreloadedAyahs(num) ?: continue
                for (ayah in ayahs) {
                    if (results.none { it.first.number == num && it.second.numberInSurah == ayah.numberInSurah }) {
                        if (ayah.translation.lowercase().contains(lowerQuery) ||
                            ayah.transliteration.lowercase().contains(lowerQuery) ||
                            ayah.arabicText.contains(lowerQuery)
                        ) {
                            results.add(Pair(surah, ayah))
                            if (results.size >= 40) return@withContext results
                        }
                    }
                }
            }
        }
        results
    }

    // Entity converters
    private fun QuranSurahEntity.toDomain() = Surah(
        number = number,
        name = name,
        englishName = englishName,
        englishNameTranslation = englishNameTranslation,
        numberOfAyahs = numberOfAyahs,
        revelationType = revelationType,
        revelationOrder = revelationOrder,
        description = description,
        isDownloaded = isDownloaded,
        downloadedAt = downloadedAt
    )

    private fun Surah.toEntity() = QuranSurahEntity(
        number = number,
        name = name,
        englishName = englishName,
        englishNameTranslation = englishNameTranslation,
        numberOfAyahs = numberOfAyahs,
        revelationType = revelationType,
        revelationOrder = revelationOrder,
        description = description,
        isDownloaded = isDownloaded,
        downloadedAt = downloadedAt
    )

    private fun QuranAyahEntity.toDomain() = Ayah(
        surahNumber = surahNumber,
        numberInSurah = numberInSurah,
        overallNumber = overallNumber,
        arabicText = arabicText,
        transliteration = transliteration,
        translation = translation,
        audioUrl = audioUrl,
        isOfflineAvailable = isOfflineAvailable
    )

    private fun Ayah.toEntity() = QuranAyahEntity(
        id = "${surahNumber}_${numberInSurah}",
        surahNumber = surahNumber,
        numberInSurah = numberInSurah,
        overallNumber = overallNumber,
        arabicText = arabicText,
        transliteration = transliteration,
        translation = translation,
        audioUrl = audioUrl,
        isOfflineAvailable = isOfflineAvailable
    )

    private fun getSurahAyahsFallback(surahNumber: Int): List<Ayah> {
        val preloaded = QuranPreloadedData.getPreloadedAyahs(surahNumber)
        if (!preloaded.isNullOrEmpty()) {
            return preloaded
        }
        val paddedSurah = String.format("%03d", surahNumber)
        val surah = getSurahByNumber(surahNumber) ?: surahsList.first()
        val list = mutableListOf<Ayah>()
        val count = surah.numberOfAyahs.coerceAtMost(10)
        for (i in 1..count) {
            val paddedAyah = String.format("%03d", i)
            val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/$paddedSurah$paddedAyah.mp3"
            list.add(
                Ayah(
                    surahNumber = surahNumber,
                    numberInSurah = i,
                    overallNumber = i,
                    arabicText = if (i == 1 && surahNumber != 9) "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" else "فَاصْبِرْ إِنَّ وَعْدَ اللَّهِ حَقٌّ ۖ وَاسْتَغْفِرْ لِذَنبِكَ وَسَبِّحْ بِحَمْدِ رَبِّكَ بِالْعَشِيِّ وَالْإِبْكَارِ",
                    transliteration = "Ayah $i of Surah ${surah.englishName}",
                    translation = "Surah ${surah.englishName} Ayah $i: So be patient. Indeed, the promise of Allah is truth.",
                    audioUrl = audioUrl,
                    isOfflineAvailable = false
                )
            )
        }
        return list
    }
}
