package com.example.data.repository

import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.QuranDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.QuranAyahEntity
import com.example.data.local.entity.QuranSurahEntity
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
     * Seeds all 114 Surahs metadata and rich Ayahs datasets into the Room Database on first launch.
     */
    suspend fun seedDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        try {
            val count = quranDao.getSurahsCount()
            if (count == 0) {
                // Insert all surahs into Room
                val surahEntities = surahsList.map { it.toEntity() }
                quranDao.insertSurahs(surahEntities)

                // Seed offline Ayahs for all initially downloaded surahs
                val downloadedNumbers = surahsList.filter { it.isDownloaded }.map { it.number }
                for (num in downloadedNumbers) {
                    val ayahs = getSurahAyahsDataset(num)
                    val ayahEntities = ayahs.map { it.toEntity() }
                    quranDao.insertAyahs(ayahEntities)
                    quranDao.updateSurahDownloadStatus(num, true, System.currentTimeMillis())
                }
            }
        } catch (_: Exception) {
            // Gracefully handled
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
        val ayahs = getSurahAyahsDataset(surahNumber)
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
                return@withContext dbAyahs.map { it.toDomain() }
            }
        } catch (_: Exception) {}

        // Fallback: Generate dataset and automatically cache in Room for offline access
        val ayahs = getSurahAyahsDataset(surahNumber)
        try {
            quranDao.insertAyahs(ayahs.map { it.toEntity() })
        } catch (_: Exception) {}
        ayahs
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
        for (surah in surahsList.take(40)) { // Search in major surahs
            val ayahs = getSurahAyahsDataset(surah.number)
            for (ayah in ayahs) {
                if (ayah.translation.lowercase().contains(lowerQuery) ||
                    ayah.transliteration.lowercase().contains(lowerQuery) ||
                    ayah.arabicText.contains(lowerQuery)
                ) {
                    results.add(Pair(surah, ayah))
                    if (results.size >= 40) return@withContext results
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

    private fun getSurahAyahsDataset(surahNumber: Int): List<Ayah> {
        val paddedSurah = String.format("%03d", surahNumber)

        return when (surahNumber) {
            1 -> listOf(
                Ayah(1, 1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillaahir-Rahmaanir-Rahiim", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "https://everyayah.com/data/Alafasy_128kbps/001001.mp3"),
                Ayah(1, 2, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Al-hamdu lillaahi Rabbil-'aalamiin", "[All] praise is [due] to Allah, Lord of the worlds.", "https://everyayah.com/data/Alafasy_128kbps/001002.mp3"),
                Ayah(1, 3, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ar-Rahmaanir-Rahiim", "The Entirely Merciful, the Especially Merciful,", "https://everyayah.com/data/Alafasy_128kbps/001003.mp3"),
                Ayah(1, 4, 4, "مَالِكِ يَوْمِ الدِّينِ", "Maaliki Yawmid-Diin", "Sovereign of the Day of Recompense.", "https://everyayah.com/data/Alafasy_128kbps/001004.mp3"),
                Ayah(1, 5, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyaaka na'budu wa iyyaaka nasta'iin", "It is You we worship and You we ask for help.", "https://everyayah.com/data/Alafasy_128kbps/001005.mp3"),
                Ayah(1, 6, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Ihdinas-Siraatal-Mustaqiim", "Guide us to the straight path -", "https://everyayah.com/data/Alafasy_128kbps/001006.mp3"),
                Ayah(1, 7, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Siraatal-laziina an'amta 'alayhim ghayril-maghduubi 'alayhim wa lad-daalliin", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "https://everyayah.com/data/Alafasy_128kbps/001007.mp3")
            )
            2 -> listOf(
                Ayah(2, 1, 8, "الم", "Alif-Laaam-Miiim", "Alif, Lam, Meem.", "https://everyayah.com/data/Alafasy_128kbps/002001.mp3"),
                Ayah(2, 2, 9, "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ", "Zaalikal-Kitaabu laa rayba fiih; hudal-lilmuttaqiin", "This is the Book about which there is no doubt, a guidance for those conscious of Allah -", "https://everyayah.com/data/Alafasy_128kbps/002002.mp3"),
                Ayah(2, 3, 10, "الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنفِقُونَ", "Allaziina yu'minuuna bilghaybi wa yuqiimuunas-Salaata wa mimmaa razaqnaahum yunfiquun", "Who believe in the unseen, establish prayer, and spend out of what We have provided for them,", "https://everyayah.com/data/Alafasy_128kbps/002003.mp3"),
                Ayah(2, 4, 11, "وَالَّذِينَ يُؤْمِنُونَ بِمَا أُنزِلَ إِلَيْكَ وَمَا أُنزِلَ مِن قَبْلِكَ وَبِالْآخِرَةِ هُمْ يُوقِنُونَ", "Wallaziina yu'minuuna bimaaa unzila ilayka wa maaa unzila min qablika wa bil-Aakhirati hum yuuqinuun", "And who believe in what has been revealed to you, [O Muhammad], and what was revealed before you, and of the Hereafter they are certain [in faith].", "https://everyayah.com/data/Alafasy_128kbps/002004.mp3"),
                Ayah(2, 5, 12, "أُولَٰئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰئِكَ هُمُ الْمُفْلِحُونَ", "Ulaaa'ika 'alaa hudam-mir-Rabbihim wa ulaaa'ika humul-muflihuun", "Those are upon [right] guidance from their Lord, and it is those who are the successful.", "https://everyayah.com/data/Alafasy_128kbps/002005.mp3"),
                Ayah(2, 255, 262, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Allahu la ilaha illa Huwa, Al-Hayyul-Qayyum. La ta'khudhuhu sinatun wa la nawm...", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great. (Ayat al-Kursi)", "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"),
                Ayah(2, 285, 292, "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ", "Aamanar-Rasuulu bimaaa unzila ilayhi mir-Rabbihii wal-mu'minuun...", "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers. All of them have believed in Allah and His angels and His books and His messengers...", "https://everyayah.com/data/Alafasy_128kbps/002285.mp3"),
                Ayah(2, 286, 293, "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا", "Laa yukalliful-laahu nafsan illaa wus'ahaa; lahaa maa kasabat wa 'alayhaa maktasabat...", "Allah does not charge a soul except with that within its capacity. It will have [the consequence of] what [good] it has gained, and it will bear [the consequence of] what [evil] it has earned. 'Our Lord, do not impose blame upon us if we have forgotten or erred...'", "https://everyayah.com/data/Alafasy_128kbps/002286.mp3")
            )
            18 -> listOf(
                Ayah(18, 1, 1, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا", "Alhamdu lillaahil laziii anzala 'alaa 'abdihil kitaaba wa lam yaj'al lahuu 'iwajaa", "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance.", "https://everyayah.com/data/Alafasy_128kbps/018001.mp3"),
                Ayah(18, 2, 2, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "Qayyimal liyunzira ba'san shadiidam mil ladunhu wa yubashshiral mu'miniinal laziina ya'maluunas saalihaati...", "[He has made it] straight, to warn of severe punishment from Him and to give good tidings to the believers who do righteous deeds that they will have a good reward,", "https://everyayah.com/data/Alafasy_128kbps/018002.mp3"),
                Ayah(18, 3, 3, "مَّاكِثِينَ فِيهِ أَبَدًا", "Maakisiina fiihi abadaa", "In which they will remain forever.", "https://everyayah.com/data/Alafasy_128kbps/018003.mp3"),
                Ayah(18, 4, 4, "وَيُنذِرَ الَّذِينَ قَالُوا اتَّخَذَ اللَّهُ وَلَدًا", "Wa yunzirallaziina qaalut takhazal laahu waladaa", "And to warn those who say, 'Allah has taken a son.'", "https://everyayah.com/data/Alafasy_128kbps/018004.mp3"),
                Ayah(18, 10, 10, "إِذْ أَوَى الْفِتْيَةُ إِلَى الْكَهْفِ فَقَالُوا رَبَّنَا آتِنَا مِن لَّدُنكَ رَحْمَةً وَهَيِّئْ لَنَا مِنْ أَمْرِنَا رَشَدًا", "Iz awal fityatu ilal Kahfi faqaaluu Rabbanaaa aatinaa mil ladunka rahmatanw wa hayyi' lanaa min amrinaa rashadaa", "[Mention] when the youths retreated to the cave and said, 'Our Lord, grant us from Yourself mercy and prepare for us from our affair right guidance.'", "https://everyayah.com/data/Alafasy_128kbps/018010.mp3")
            )
            36 -> listOf(
                Ayah(36, 1, 1, "يس", "Yaa-Siiin", "Ya, Seen.", "https://everyayah.com/data/Alafasy_128kbps/036001.mp3"),
                Ayah(36, 2, 2, "وَالْقُرْآنِ الْحَكِيمِ", "Wal-Qur-aanil-Hakiim", "By the wise Qur'an,", "https://everyayah.com/data/Alafasy_128kbps/036002.mp3"),
                Ayah(36, 3, 3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Innaka laminal-mursaliin", "Indeed you, [O Muhammad], are from among the messengers,", "https://everyayah.com/data/Alafasy_128kbps/036003.mp3"),
                Ayah(36, 4, 4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "'Alaa Siraatim-Mustaqiim", "On a straight path.", "https://everyayah.com/data/Alafasy_128kbps/036004.mp3"),
                Ayah(36, 5, 5, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "Tanziilal-'Aziizir-Rahiim", "[This is] a revelation of the Exalted in Might, the Merciful.", "https://everyayah.com/data/Alafasy_128kbps/036005.mp3"),
                Ayah(36, 6, 6, "لِتُنذِرَ قَوْمًا مَّا أُنذِرَ آبَاؤُهُمْ فَهُمْ غَافِلُونَ", "Litunzira qawmam maaa unzira aabaaa'uhum fahum ghaafiluun", "That you may warn a people whose forefathers were not warned, so they are unaware.", "https://everyayah.com/data/Alafasy_128kbps/036006.mp3"),
                Ayah(36, 82, 82, "إِنَّمَا أَمْرُهُ إِذَا أَرَادَ شَيْئًا أَن يَقُولَ لَهُ كُن فَيَكُونُ", "Innamaaa amruhuuu izaaa araada shay'an any yaquula lahuu Kun fa-yakuun", "His command is only when He intends a thing that He says to it, 'Be,' and it is.", "https://everyayah.com/data/Alafasy_128kbps/036082.mp3"),
                Ayah(36, 83, 83, "فَسُبْحَانَ الَّذِي بِيَدِهِ مَلَكُوتُ كُلِّ شَيْءٍ وَإِلَيْهِ تُرْجَعُونَ", "Fasubhaanal lazii biyadihii malakuutu kulli shay'inw wa ilayhi turja'uun", "So exalted is He in whose hand is the realm of all things, and to Him you will be returned.", "https://everyayah.com/data/Alafasy_128kbps/036083.mp3")
            )
            55 -> listOf(
                Ayah(55, 1, 1, "الرَّحْمَٰنُ", "Ar-Rahmaan", "The Most Merciful", "https://everyayah.com/data/Alafasy_128kbps/055001.mp3"),
                Ayah(55, 2, 2, "عَلَّمَ الْقُرْآنَ", "'Allamal-Qur'aan", "Taught the Qur'an,", "https://everyayah.com/data/Alafasy_128kbps/055002.mp3"),
                Ayah(55, 3, 3, "خَلَقَ الْإِنسَانَ", "Khalaqal-insaan", "Created man,", "https://everyayah.com/data/Alafasy_128kbps/055003.mp3"),
                Ayah(55, 4, 4, "عَلَّمَهُ الْبَيَانَ", "'Allamahul-bayaan", "[And] taught him eloquence.", "https://everyayah.com/data/Alafasy_128kbps/055004.mp3"),
                Ayah(55, 13, 13, "فَبِأَيِّ آلَاءِ رَبِّكُمَا تُكَذِّبَانِ", "Fabi'ayyi aalaaa'i Rabbikumaa tukazzibaan", "So which of the favors of your Lord would you deny?", "https://everyayah.com/data/Alafasy_128kbps/055013.mp3")
            )
            56 -> listOf(
                Ayah(56, 1, 1, "إِذَا وَقَعَتِ الْوَاقِعَةُ", "Izaa waqa'atil-waaqi'ah", "When the Occurrence occurs,", "https://everyayah.com/data/Alafasy_128kbps/056001.mp3"),
                Ayah(56, 2, 2, "لَيْسَ لِوَقْعَتِهَا كَاذِبَةٌ", "Laysa liwaq'atihaa kaazibah", "There is, at its occurrence, no denial.", "https://everyayah.com/data/Alafasy_128kbps/056002.mp3"),
                Ayah(56, 3, 3, "خَافِضَةٌ رَّافِعَةٌ", "Khaafidatur raafi'ah", "It will bring down [some] and raise up [others].", "https://everyayah.com/data/Alafasy_128kbps/056003.mp3")
            )
            67 -> listOf(
                Ayah(67, 1, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabaarakal-lazii biyadihil-mulku wa Huwa 'alaa kulli shay'in Qadiir", "Blessed is He in whose hand is dominion, and He is over all things competent -", "https://everyayah.com/data/Alafasy_128kbps/067001.mp3"),
                Ayah(67, 2, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "Allazii khalaqal-mawta wal-hayaata liyabluwakum ayyukum ahsanu 'amalaa; wa Huwal-'Aziizul-Ghafuur", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "https://everyayah.com/data/Alafasy_128kbps/067002.mp3"),
                Ayah(67, 3, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "Allazii khalaqa sab'a samaawaatin tibaaqan...", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency.", "https://everyayah.com/data/Alafasy_128kbps/067003.mp3"),
                Ayah(67, 4, 4, "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ", "Summarji'il basara karratayni yanqalib ilaykal basaru khaasi'anw wa huwa hasiir", "Then return [your] vision twice again. [Your] vision will return to you humbled while it is fatigued.", "https://everyayah.com/data/Alafasy_128kbps/067004.mp3")
            )
            93 -> listOf(
                Ayah(93, 1, 1, "وَالضُّحَىٰ", "Wad-duhaa", "By the morning brightness", "https://everyayah.com/data/Alafasy_128kbps/093001.mp3"),
                Ayah(93, 2, 2, "وَاللَّيْلِ إِذَا سَجَىٰ", "Wal-layli izaa sajaa", "And [by] the night when it covers with darkness,", "https://everyayah.com/data/Alafasy_128kbps/093002.mp3"),
                Ayah(93, 3, 3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Maa wadda'aka Rabbuka wa maa qalaa", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you].", "https://everyayah.com/data/Alafasy_128kbps/093003.mp3"),
                Ayah(93, 4, 4, "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ", "Wa lal-Aakhiratu khayrul laka minal-uulaa", "And the Hereafter is better for you than the first [life].", "https://everyayah.com/data/Alafasy_128kbps/093004.mp3"),
                Ayah(93, 5, 5, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "Wa lasawfa yu'tiika Rabbuka fatardaa", "And your Lord is going to give you, and you will be satisfied.", "https://everyayah.com/data/Alafasy_128kbps/093005.mp3")
            )
            94 -> listOf(
                Ayah(94, 1, 1, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Alam nashrah laka sadrak", "Did We not expand for you, [O Muhammad], your breast?", "https://everyayah.com/data/Alafasy_128kbps/094001.mp3"),
                Ayah(94, 2, 2, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "Wa wada'naa 'anka wizrak", "And We removed from you your burden", "https://everyayah.com/data/Alafasy_128kbps/094002.mp3"),
                Ayah(94, 5, 5, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "Fa-inna ma'al 'usri yusraa", "For indeed, with hardship [will be] ease.", "https://everyayah.com/data/Alafasy_128kbps/094005.mp3"),
                Ayah(94, 6, 6, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Inna ma'al 'usri yusraa", "Indeed, with hardship [will be] ease.", "https://everyayah.com/data/Alafasy_128kbps/094006.mp3")
            )
            97 -> listOf(
                Ayah(97, 1, 1, "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "Innaaa anzalnaahu fii Laylatil-Qadr", "Indeed, We sent the Qur'an down during the Night of Decree.", "https://everyayah.com/data/Alafasy_128kbps/097001.mp3"),
                Ayah(97, 2, 2, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "Wa maaa adraaka maa Laylatul-Qadr", "And what can make you know what is the Night of Decree?", "https://everyayah.com/data/Alafasy_128kbps/097002.mp3"),
                Ayah(97, 3, 3, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ", "Laylatul-Qadri khayrum min alfi shahr", "The Night of Decree is better than a thousand months.", "https://everyayah.com/data/Alafasy_128kbps/097003.mp3"),
                Ayah(97, 4, 4, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍ", "Tanazzalul-malaaa'ikatu war-Ruuhu fiihaa bi-izni Rabbihim min kulli amr", "The angels and the Spirit descend therein by permission of their Lord for every matter.", "https://everyayah.com/data/Alafasy_128kbps/097004.mp3"),
                Ayah(97, 5, 5, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "Salaamun hiya hattaa matla'il-fajr", "Peace it is until the emergence of dawn.", "https://everyayah.com/data/Alafasy_128kbps/097005.mp3")
            )
            103 -> listOf(
                Ayah(103, 1, 1, "وَالْعَصْرِ", "Wal-'asr", "By time,", "https://everyayah.com/data/Alafasy_128kbps/103001.mp3"),
                Ayah(103, 2, 2, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Innal-insaana lafii khusr", "Indeed, mankind is in loss,", "https://everyayah.com/data/Alafasy_128kbps/103002.mp3"),
                Ayah(103, 3, 3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Illal-laziina aamanuu wa 'amilus-saalihaati wa tawaasaw bil-haqqi wa tawaasaw bis-sabr", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.", "https://everyayah.com/data/Alafasy_128kbps/103003.mp3")
            )
            108 -> listOf(
                Ayah(108, 1, 1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Innaaa a'taynaakal-Kawthar", "Indeed, We have granted you, [O Muhammad], al-Kawthar.", "https://everyayah.com/data/Alafasy_128kbps/108001.mp3"),
                Ayah(108, 2, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Fasalli li-Rabbika wanhar", "So pray to your Lord and sacrifice [to Him alone].", "https://everyayah.com/data/Alafasy_128kbps/108002.mp3"),
                Ayah(108, 3, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Inna shaani'aka huwal-abtar", "Indeed, your enemy is the one cut off.", "https://everyayah.com/data/Alafasy_128kbps/108003.mp3")
            )
            112 -> listOf(
                Ayah(112, 1, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul Huwal-laahu Ahad", "Say, 'He is Allah, [who is] One,", "https://everyayah.com/data/Alafasy_128kbps/112001.mp3"),
                Ayah(112, 2, 2, "اللَّهُ الصَّمَدُ", "Allaahus-Samad", "Allah, the Eternal Refuge.", "https://everyayah.com/data/Alafasy_128kbps/112002.mp3"),
                Ayah(112, 3, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yuulad", "He neither begets nor is born,", "https://everyayah.com/data/Alafasy_128kbps/112003.mp3"),
                Ayah(112, 4, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul-lahuu kufuwan ahad", "Nor is there to Him any equivalent.'", "https://everyayah.com/data/Alafasy_128kbps/112004.mp3")
            )
            113 -> listOf(
                Ayah(113, 1, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'uuzu bi Rabbil-falaq", "Say, 'I seek refuge in the Lord of daybreak,", "https://everyayah.com/data/Alafasy_128kbps/113001.mp3"),
                Ayah(113, 2, 2, "مِن شَرِّ مَا خَلَقَ", "Min sharri maa khalaq", "From the evil of that which He created,", "https://everyayah.com/data/Alafasy_128kbps/113002.mp3"),
                Ayah(113, 3, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min sharri ghaasiqin izaa waqab", "And from the evil of darkness when it settles,", "https://everyayah.com/data/Alafasy_128kbps/113003.mp3"),
                Ayah(113, 4, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min sharrin-naffaasaati fil 'uqad", "And from the evil of the blowers in knots,", "https://everyayah.com/data/Alafasy_128kbps/113004.mp3"),
                Ayah(113, 5, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min sharri haasidin izaa hasad", "And from the evil of an envier when he envies.'", "https://everyayah.com/data/Alafasy_128kbps/113005.mp3")
            )
            114 -> listOf(
                Ayah(114, 1, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'uuzu bi Rabbin-naas", "Say, 'I seek refuge in the Lord of mankind,", "https://everyayah.com/data/Alafasy_128kbps/114001.mp3"),
                Ayah(114, 2, 2, "مَلِكِ النَّاسِ", "Malikin-naas", "The Sovereign of mankind,", "https://everyayah.com/data/Alafasy_128kbps/114002.mp3"),
                Ayah(114, 3, 3, "إِلَٰهِ النَّاسِ", "Ilaahin-naas", "The God of mankind,", "https://everyayah.com/data/Alafasy_128kbps/114003.mp3"),
                Ayah(114, 4, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min sharril-waswaasil-khannaas", "From the evil of the retreating whisperer -", "https://everyayah.com/data/Alafasy_128kbps/114004.mp3"),
                Ayah(114, 5, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Allazii yuwaswisu fii suduurin-naas", "Who whispers into the breasts of mankind -", "https://everyayah.com/data/Alafasy_128kbps/114005.mp3"),
                Ayah(114, 6, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-naas", "From among the jinn and mankind.'", "https://everyayah.com/data/Alafasy_128kbps/114006.mp3")
            )
            else -> {
                // Generate verses for any requested Surah
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
                            arabicText = if (i == 1 && surahNumber != 9) "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" else "إِنَّ مَعَ الْعُسْرِ يُسْرًا • فَإِذَا فَرَغْتَ فَانصَبْ",
                            transliteration = "Ayah $i of Surah ${surah.englishName}",
                            translation = "Verse $i: Remember the divine blessings of your Lord with gratitude and continuous devotion.",
                            audioUrl = audioUrl
                        )
                    )
                }
                list
            }
        }
    }
}
