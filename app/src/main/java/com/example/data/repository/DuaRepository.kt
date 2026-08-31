package com.example.data.repository

import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.DuaDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.DuaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class DuaRepository(
    private val duaDao: DuaDao,
    private val bookmarkDao: BookmarkDao? = null
) {
    val categories = listOf(
        "All",
        "⭐ Bookmarks",
        "Daily Life",
        "Morning & Evening",
        "Anxiety & Relief",
        "Protection & Safety",
        "Forgiveness",
        "Prayer & Wudu",
        "Health & Healing",
        "Family & Parents",
        "Rizq & Sustenance",
        "Knowledge & Studies",
        "Gratitude",
        "Travel",
        "Hajj & Umrah"
    )

    fun getAllDuas(): Flow<List<DuaEntity>> = duaDao.getAllDuas()

    fun getDuasByCategory(category: String): Flow<List<DuaEntity>> {
        return when (category) {
            "All" -> duaDao.getAllDuas()
            "⭐ Bookmarks", "Bookmarks", "Favorites" -> duaDao.getFavoriteDuas()
            else -> duaDao.getDuasByCategory(category)
        }
    }

    fun getFavoriteDuas(): Flow<List<DuaEntity>> = duaDao.getFavoriteDuas()

    fun getFavoriteCount(): Flow<Int> = duaDao.getFavoriteCount()

    fun searchDuas(query: String, category: String = "All"): Flow<List<DuaEntity>> {
        val clean = query.trim()
        return when {
            clean.isBlank() -> getDuasByCategory(category)
            category == "⭐ Bookmarks" || category == "Bookmarks" || category == "Favorites" -> duaDao.searchFavoriteDuas(clean)
            category == "All" -> duaDao.searchDuas(clean)
            else -> duaDao.searchDuasInCategory(category, clean)
        }
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        duaDao.updateFavorite(id, isFavorite)
        if (bookmarkDao != null) {
            try {
                if (isFavorite) {
                    val all = duaDao.getAllDuas().firstOrNull() ?: emptyList()
                    val target = all.firstOrNull { it.id == id }
                    if (target != null) {
                        bookmarkDao.insertBookmark(
                            BookmarkEntity(
                                type = "DUA",
                                referenceId = target.id,
                                title = target.title,
                                arabicText = target.arabicText,
                                translation = target.translation
                            )
                        )
                    }
                } else {
                    bookmarkDao.deleteBookmarkByRef(id, "DUA")
                }
            } catch (_: Exception) { }
        }
    }

    suspend fun preloadDuasIfNeeded() = withContext(Dispatchers.IO) {
        val existing = duaDao.getAllDuas().firstOrNull()
        if (existing.isNullOrEmpty() || existing.size < initialDuasList.size) {
            // Keep existing favorites if reloading
            val favMap = existing?.associate { it.id to it.isFavorite } ?: emptyMap()
            val mergedList = initialDuasList.map { item ->
                val wasFav = favMap[item.id] ?: item.isFavorite
                item.copy(isFavorite = wasFav)
            }
            duaDao.insertDuas(mergedList)
        }
    }

    val initialDuasList: List<DuaEntity> = listOf(
        // Daily Life
        DuaEntity(
            id = "dua_wake_up",
            category = "Daily Life",
            title = "Upon Waking Up",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Alhamdu lillahil-lathee ahyana ba'da ma amatana wa-ilayhin-nushoor",
            translation = "All praise is for Allah who gave us life after having taken it from us and unto Him is the resurrection.",
            source = "Sahih al-Bukhari 6312",
            benefit = "Re-establishes gratitude immediately upon returning to consciousness."
        ),
        DuaEntity(
            id = "dua_sleeping",
            category = "Daily Life",
            title = "Before Sleeping",
            arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            transliteration = "Bismika Rabbi wada'tu janbi, wa bika arfa'uh. Fa-in amsakta nafsi farhamha, wa in arsaltaha fahfadh-ha bima tahfadhu bihi 'ibadakas-saliheen",
            translation = "In Your name my Lord, I lie down and in Your name I arise, so if You should take my soul then have mercy upon it, and if You should return my soul then protect it in the manner You do so with Your righteous servants.",
            source = "Sahih al-Bukhari 6320"
        ),
        DuaEntity(
            id = "dua_entering_home",
            category = "Daily Life",
            title = "Entering the Home",
            arabicText = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى رَبِّنَا تَوَكَّلْنَا",
            transliteration = "Bismillahi walajna, wa bismillahi kharajna, wa 'ala Rabbina tawakkalna",
            translation = "In the name of Allah we enter, and in the name of Allah we leave, and upon our Lord we place our trust.",
            source = "Sunan Abi Dawud 5096"
        ),
        DuaEntity(
            id = "dua_leaving_home",
            category = "Daily Life",
            title = "Leaving the Home",
            arabicText = "بِسْمِ اللَّهِ ، تَوَكَّلْتُ عَلَى اللَّهِ ، وَلا حَوْلَ وَلا قُوَّةَ إِلاَّ بِاللَّهِ",
            transliteration = "Bismillahi tawakkaltu 'alallah, wa la hawla wa la quwwata illa billah",
            translation = "In the name of Allah, I place my trust in Allah; there is no power nor might except with Allah.",
            source = "Sunan Abi Dawud 5095",
            benefit = "It is said to you: You have been guided, defended, and protected, and Satan retreats from you."
        ),
        DuaEntity(
            id = "dua_before_eating",
            category = "Daily Life",
            title = "Before Eating",
            arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            transliteration = "Bismillahir-Rahmanir-Raheem",
            translation = "In the Name of Allah, the Entirely Merciful, the Especially Merciful.",
            source = "Sahih al-Bukhari 5376"
        ),
        DuaEntity(
            id = "dua_after_eating",
            category = "Daily Life",
            title = "After Eating",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            transliteration = "Alhamdu lillahil-lathee at'amani hatha wa razaqanihi min ghayri hawlin minni wa la quwwah",
            translation = "Praise be to Allah Who has fed me this and provided it for me without any might or power from myself.",
            source = "Sunan at-Tirmidhi 3458",
            benefit = "Past minor sins are forgiven."
        ),
        DuaEntity(
            id = "dua_wearing_clothes",
            category = "Daily Life",
            title = "When Putting on Clothes",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي كَسَانِي هَذَا الثَّوْبَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            transliteration = "Alhamdu lillahil-lathee kasani hatha ath-thawba wa razaqanihi min ghayri hawlin minni wa la quwwah",
            translation = "All Praise is for Allah Who has clothed me with this garment and provided it for me, with no power or might from myself.",
            source = "Sunan Abi Dawud 4023"
        ),

        // Morning & Evening
        DuaEntity(
            id = "dua_morning_adhkar",
            category = "Morning & Evening",
            title = "Sayyidul Istighfar (Master of Forgiveness)",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'oodhu bika min sharri ma sana'tu, aboo'u laka bini'matika 'alayya, wa aboo'u bidhanbi faghfir li fa-innahu la yaghfirudh-dhunooba illa Anta",
            translation = "O Allah, You are my Lord, none has the right to be worshiped but You. You created me and I am Your servant, and I abide to Your covenant and promise as best I can. I take refuge in You from the evil which I have committed. I acknowledge Your favor upon me and I acknowledge my sin, so forgive me, for none forgives sins except You.",
            source = "Sahih al-Bukhari 6306",
            benefit = "Whoever recites it in morning/evening with firm faith and dies that day enters Paradise."
        ),
        DuaEntity(
            id = "dua_morning_light",
            category = "Morning & Evening",
            title = "Morning Affirmation of Sovereignty",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ",
            transliteration = "Asbahna wa asbahal-mulku lillah, wal-hamdu lillah, la ilaha illallahu wahdahu la shareeka lah",
            translation = "We have entered the morning and the kingdom belongs to Allah, all praise is due to Allah, none has the right to be worshiped except Allah alone, without partner.",
            source = "Sahih Muslim 2723"
        ),
        DuaEntity(
            id = "dua_evening_peace",
            category = "Morning & Evening",
            title = "Evening Affirmation of Grace",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Amsayna wa amsal-mulku lillahi wal-hamdu lillahi la ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in qadeer",
            translation = "We have reached the evening and the Dominion belongs to Allah, all praise is for Allah, none has the right to be worshiped but Allah alone without partner.",
            source = "Sahih Muslim 2723"
        ),

        // Anxiety & Relief
        DuaEntity(
            id = "dua_anxiety_grief",
            category = "Anxiety & Relief",
            title = "Relief from Anxiety, Sorrow & Debt",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allahumma inni a'oodhu bika minal-hammi wal-hazan, wal-'ajzi wal-kasal, wal-bukhli wal-jubn, wa dala'id-dayni wa ghalabatir-rijal",
            translation = "O Allah, I seek refuge in You from anxiety and grief, helplessness and laziness, stinginess and cowardice, the burden of debt, and the subjugation of people.",
            source = "Sahih al-Bukhari 2893",
            benefit = "A comprehensive daily shield against emotional burden and overwhelm."
        ),
        DuaEntity(
            id = "dua_distress_heart",
            category = "Anxiety & Relief",
            title = "Supplication in Extreme Distress",
            arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
            transliteration = "Ya Hayyu Ya Qayyoom, bi-rahmatika astagheeth, aslih li sha'ni kullahu, wa la takilni ila nafsi tarfata 'ayn",
            translation = "O Ever-Living, O Sustainer of existence, by Your mercy I seek assistance. Rectify for me all of my affairs, and do not leave me to myself even for the blink of an eye.",
            source = "Sunan an-Nasa'i 10405 / Sunan at-Tirmidhi 3524"
        ),
        DuaEntity(
            id = "dua_prophet_yunus_relief",
            category = "Anxiety & Relief",
            title = "Supplication of Prophet Yunus (In Distress)",
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            transliteration = "La ilaha illa Anta subhanaka inni kuntu minaz-zalimeen",
            translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            source = "Surah Al-Anbiya 21:87 / Sunan at-Tirmidhi 3505",
            benefit = "No Muslim invokes Allah with this supplication for any matter except that Allah removes their distress."
        ),
        DuaEntity(
            id = "dua_hasbunallah",
            category = "Anxiety & Relief",
            title = "Sufficiency in Allah Alone",
            arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "Hasbunallahu wa ni'mal-wakeel",
            translation = "Allah is sufficient for us, and He is the best Disposer of affairs.",
            source = "Surah Ali 'Imran 3:173 / Sahih al-Bukhari 4563"
        ),

        // Protection & Safety
        DuaEntity(
            id = "dua_protection_evil",
            category = "Protection & Safety",
            title = "Protection Against All Sudden Harm (3x)",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillahil-lathee la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Samee'ul-'Aleem",
            translation = "In the Name of Allah, with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, the All-Knowing.",
            source = "Sunan Abi Dawud 5088",
            benefit = "Reciting this 3 times morning and evening shields from sudden harm."
        ),
        DuaEntity(
            id = "dua_evil_eye",
            category = "Protection & Safety",
            title = "Seeking Refuge from the Evil Eye and Devils",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّةِ مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ وَمِنْ كُلِّ عَيْنٍ لَامَّةٍ",
            transliteration = "A'oodhu bikalimatil-lahit-tammati min kulli shaytanin wa hammah, wa min kulli 'aynin lammah",
            translation = "I seek refuge in the Perfect Words of Allah from every devil and poisonous reptile, and from every evil envious eye.",
            source = "Sahih al-Bukhari 3371"
        ),
        DuaEntity(
            id = "dua_ayat_al_kursi",
            category = "Protection & Safety",
            title = "Ayat al-Kursi (The Verse of the Throne)",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyoom, la ta'khudhuhu sinatun wa la nawm, lahu ma fis-samawati wa ma fil-ard, man dhal-lathee yashfa'u 'indahu illa bi-idhnih, ya'lamu ma bayna aydeehim wa ma khalfahum, wa la yuheetoona bishay'im-min 'ilmihi illa bima sha'a, wasi'a kursiyyuhus-samawati wal-ard, wa la ya'ooduhu hifdhuhuma, wa Huwal-'Aliyyul-'Adheem",
            translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            source = "Surah Al-Baqarah 2:255",
            benefit = "A guardian angel protects the reciter from Satan throughout the night until morning."
        ),

        // Forgiveness
        DuaEntity(
            id = "dua_forgive_all",
            category = "Forgiveness",
            title = "Supplication for Comprehensive Pardon",
            arabicText = "رَبَّنَا اغْفِرْ لَنَا ذُنُوبَنَا وَإِسْرَافَنَا فِي أَمْرِنَا وَثَبِّتْ أَقْدَامَنَا وَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
            transliteration = "Rabbanagh-fir lana dhunoobana wa israfana fee amrina wa thabbit aqdamana wansurna 'alal-qawmil-kafireen",
            translation = "Our Lord, forgive us our sins and the excess [committed] in our affairs and plant firmly our feet and give us victory over the disbelieving people.",
            source = "Surah Ali 'Imran 3:147"
        ),
        DuaEntity(
            id = "dua_adam_repentance",
            category = "Forgiveness",
            title = "Supplication of Adam & Eve",
            arabicText = "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
            transliteration = "Rabbana thalamna anfusana wa-illam taghfir lana wa tarhamna lanakoonanna minal-khasireen",
            translation = "Our Lord, we have wronged ourselves, and if You do not forgive us and have mercy upon us, we will surely be among the losers.",
            source = "Surah Al-A'raf 7:23"
        ),
        DuaEntity(
            id = "dua_laylatul_qadr",
            category = "Forgiveness",
            title = "Dua for the Night of Decree (Laylatul Qadr)",
            arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            transliteration = "Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'annee",
            translation = "O Allah, You are Forgiving and You love forgiveness, so forgive me.",
            source = "Sunan at-Tirmidhi 3513"
        ),

        // Prayer & Wudu
        DuaEntity(
            id = "dua_entering_masjid",
            category = "Prayer & Wudu",
            title = "Entering the Mosque",
            arabicText = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            transliteration = "Allahummaf-tah li abwaba rahmatika",
            translation = "O Allah, open for me the gates of Your mercy.",
            source = "Sahih Muslim 713"
        ),
        DuaEntity(
            id = "dua_leaving_masjid",
            category = "Prayer & Wudu",
            title = "Leaving the Mosque",
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
            transliteration = "Allahumma inni as'aluka min fadlik",
            translation = "O Allah, I ask You from Your bounty.",
            source = "Sahih Muslim 713"
        ),
        DuaEntity(
            id = "dua_after_wudu",
            category = "Prayer & Wudu",
            title = "After Completing Ablution (Wudu)",
            arabicText = "أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ",
            transliteration = "Ash-hadu an la ilaha illallahu wahdahu la shareeka lahu wa ash-hadu anna Muhammadan 'abduhu wa rasooluh. Allahummaj-'alni minat-tawwabeena waj-'alni minal-mutatahhireen",
            translation = "I bear witness that none has the right to be worshiped except Allah alone without partner, and Muhammad is His slave and Messenger. O Allah, make me of the repentant and make me of the purified.",
            source = "Sunan at-Tirmidhi 55",
            benefit = "All eight gates of Paradise are opened for whoever recites this after wudu."
        ),
        DuaEntity(
            id = "dua_tashahhud_refuge",
            category = "Prayer & Wudu",
            title = "Before Final Salam (4 Trials Refuge)",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ عَذَابِ جَهَنَّمَ، وَمِنْ عَذَابِ الْقَبْرِ، وَمِنْ فِتْنَةِ الْمَحْيَا وَالْمَمَاتِ، وَمِنْ شَرِّ فِتْنَةِ الْمَسِيحِ الدَّجَّالِ",
            transliteration = "Allahumma inni a'oodhu bika min 'adhabi jahannam, wa min 'adhabil-qabr, wa min fitnatil-mahya wal-mamat, wa min sharri fitnatil-maseehid-dajjal",
            translation = "O Allah, I seek refuge in You from the torment of Hell, from the torment of the grave, from the trials of life and death, and from the evil trial of the false Messiah (Dajjal).",
            source = "Sahih Muslim 588"
        ),
        DuaEntity(
            id = "dua_steadfast_prayer",
            category = "Prayer & Wudu",
            title = "Prophet Ibrahim's Prayer for Prayer Devotion",
            arabicText = "رَبِّ اجْعَلْنِي مُقِيمَ الصَّلَاةِ وَمِن ذُرِّيَّتِي ۚ رَبَّنَا وَتَقَبَّلْ دُعَاءِ",
            transliteration = "Rabbij-'alnee muqeemas-Salati wa min dhurriyyatee, Rabbana wa taqabbal du'a'",
            translation = "My Lord, make me an establisher of prayer, and [many] from my descendants. Our Lord, and accept my supplication.",
            source = "Surah Ibrahim 14:40"
        ),

        // Health & Healing
        DuaEntity(
            id = "dua_visiting_sick",
            category = "Health & Healing",
            title = "For Healing the Sick (Recite 7x)",
            arabicText = "أَسْأَلُ اللَّهَ الْعَظِيمَ رَبَّ الْعَرْشِ الْعَظِيمِ أَنْ يَشْفِيَكَ",
            transliteration = "As'alullahal-'Adheema Rabbal-'Arshil-'Adheemi an yashfiyak",
            translation = "I ask Allah the Almighty, the Lord of the Mighty Throne, to heal you.",
            source = "Sunan Abi Dawud 3106",
            benefit = "A recommended Sunnah when visiting any ill person."
        ),
        DuaEntity(
            id = "dua_pain_body",
            category = "Health & Healing",
            title = "When Feeling Bodily Pain",
            arabicText = "بِسْمِ اللَّهِ (3x) • أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (7x)",
            transliteration = "Bismillah (3x) • A'oodhu billahi wa qudratihi min sharri ma ajidu wa uhadhir (7x)",
            translation = "In the Name of Allah (3x). I seek refuge in Allah and in His power from the evil of what I find and of what I fear (7x).",
            source = "Sahih Muslim 2202",
            benefit = "Place hand on the area of pain while reciting."
        ),
        DuaEntity(
            id = "dua_ayyub_healing",
            category = "Health & Healing",
            title = "Supplication of Prophet Ayyub in Illness",
            arabicText = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنتَ أَرْحَمُ الرَّاحِمِينَ",
            transliteration = "Annee massaniyad-durru wa Anta Arhamur-Rahimeen",
            translation = "Indeed, adversity has touched me, and You are the Most Merciful of the merciful.",
            source = "Surah Al-Anbiya 21:83"
        ),

        // Family & Parents
        DuaEntity(
            id = "dua_parents",
            category = "Family & Parents",
            title = "Dua for Parents",
            arabicText = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-hamhuma kama rabbayani sagheera",
            translation = "My Lord, have mercy upon them as they brought me up [when I was] small.",
            source = "Surah Al-Isra 17:24"
        ),
        DuaEntity(
            id = "dua_parents_forgiveness",
            category = "Family & Parents",
            title = "Forgiveness for Parents on Judgment Day",
            arabicText = "رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ",
            transliteration = "Rabbanagh-fir lee wa liwalidayya wa lil-mu'mineena yawma yaqoomul-hisab",
            translation = "Our Lord, forgive me and my parents and the believers the Day the account is established.",
            source = "Surah Ibrahim 14:41"
        ),
        DuaEntity(
            id = "dua_spouse_children",
            category = "Family & Parents",
            title = "For Righteous Spouse and Offspring",
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            transliteration = "Rabbana hab lana min azwajina wa dhurriyyatina qurrata a'yunin waj'alna lil-muttaqeena imama",
            translation = "Our Lord, grant us from among our wives and offspring comfort to our eyes and make us an example for the righteous.",
            source = "Surah Al-Furqan 25:74"
        ),

        // Rizq & Sustenance
        DuaEntity(
            id = "dua_debt_relief_halal",
            category = "Rizq & Sustenance",
            title = "For Halal Provision & Debt Freedom",
            arabicText = "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
            transliteration = "Allahummak-finee bihalalika 'an haramik, wa aghninee bifadlika 'amman siwak",
            translation = "O Allah, suffice me with what You have allowed instead of what You have forbidden, and enrich me with Your grace against all others.",
            source = "Sunan at-Tirmidhi 3563",
            benefit = "Even if one had debt like a mountain, Allah would assist them in clearing it."
        ),
        DuaEntity(
            id = "dua_beneficial_knowledge_rizq",
            category = "Rizq & Sustenance",
            title = "For Beneficial Knowledge, Pure Rizq & Accepted Deeds",
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
            transliteration = "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbala",
            translation = "O Allah, I ask You for knowledge that is of benefit, a good and lawful provision, and deeds that will be accepted.",
            source = "Sunan Ibn Majah 925"
        ),

        // Knowledge & Studies
        DuaEntity(
            id = "dua_increase_knowledge",
            category = "Knowledge & Studies",
            title = "Seeking Increase in Knowledge",
            arabicText = "رَّبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidnee 'ilma",
            translation = "My Lord, increase me in knowledge.",
            source = "Surah Ta-Ha 20:114"
        ),
        DuaEntity(
            id = "dua_ease_tasks",
            category = "Knowledge & Studies",
            title = "When Facing Difficult Exams & Challenges",
            arabicText = "اللَّهُمَّ لاَ سَهْلَ إِلاَّ مَا جَعَلْتَهُ سَهْلاً، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلاً",
            transliteration = "Allahumma la sahla illa ma ja'altahu sahla, wa Anta taj'alul-hazna idha shi'ta sahla",
            translation = "O Allah, there is no ease except in that which You have made easy, and You make the difficulty, if You wish, easy.",
            source = "Sahih Ibn Hibban 327",
            benefit = "Calms examination tension and unlocks ease in complex responsibilities."
        ),
        DuaEntity(
            id = "dua_musa_speech",
            category = "Knowledge & Studies",
            title = "For Eloquence & Opening of the Chest",
            arabicText = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي يَفْقَهُوا قَوْلِي",
            transliteration = "Rabbish-rah lee sadree, wa yassir lee amree, wahlul 'uqdatam-mil-lisanee, yafqahoo qawlee",
            translation = "My Lord, expand for me my chest [with assurance], ease for me my task, and untie the knot from my tongue that they may understand my speech.",
            source = "Surah Ta-Ha 20:25-28"
        ),

        // Gratitude
        DuaEntity(
            id = "dua_gratitude_prophet_sulayman",
            category = "Gratitude",
            title = "Prophet Sulayman's Prayer of Gratitude",
            arabicText = "رَبِّ أَوْزِعْنِي أَنْ أَشْكُرَ نِعْمَتَكَ الَّتِي أَنْعَمْتَ عَلَيَّ وَعَلَىٰ وَالِدَيَّ وَأَنْ أَعْمَلَ صَالِحًا تَرْضَاهُ وَأَدْخِلْنِي بِرَحْمَتِكَ فِي عِبَادِكَ الصَّالِحِينَ",
            transliteration = "Rabbi awzi'ni an ashkura ni'matakal-latee an'amta 'alayya wa 'ala walidayya wa an a'mala salihan tardahu wa adkhilni birahmatika fee 'ibadikas-saliheen",
            translation = "My Lord, enable me to be grateful for Your favor which You have bestowed upon me and upon my parents and to do righteousness of which You approve. And admit me by Your mercy into [the ranks of] Your righteous servants.",
            source = "Surah An-Naml 27:19"
        ),
        DuaEntity(
            id = "dua_sayyid_gratitude",
            category = "Gratitude",
            title = "Morning & Evening Praise of Gratitude",
            arabicText = "اللَّهُمَّ مَا أَصْبَحَ بِي مِنْ نِعْمَةٍ أَوْ بِأَحَدٍ مِنْ خَلْقِكَ فَمِنْكَ وَحْدَكَ لَا شَرِيكَ لَكَ فَلَكَ الْحَمْدُ وَلَكَ الشُّكْرُ",
            transliteration = "Allahumma ma asbaha bee min ni'matin aw bi-ahadim-min khalqika faminka wahdaka la shareeka laka falakal-hamdu wa lakash-shukr",
            translation = "O Allah, whatever blessing I or any of Your creation have risen with, is from You alone, without partner; to You belongs all praise and gratitude.",
            source = "Sunan Abi Dawud 5073",
            benefit = "Whoever recites this in the morning has fulfilled the gratitude owed for that day."
        ),

        // Travel
        DuaEntity(
            id = "dua_travel_mount",
            category = "Travel",
            title = "Boarding a Vehicle / Journey Supplication",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
            transliteration = "Subhanal-lathee sakh-khara lana hatha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon",
            translation = "Glory to Him who has subjected this to us, and we could never have it [by our efforts], and indeed, to our Lord we will return.",
            source = "Surah Az-Zukhruf 43:13-14 / Sahih Muslim 1342"
        ),

        // Hajj & Umrah
        DuaEntity(
            id = "dua_talbiyah",
            category = "Hajj & Umrah",
            title = "The Talbiyah (Here I Am At Your Service)",
            arabicText = "لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لاَ شَرِيكَ لَكَ لَبَّيْكَ، إِنَّ الْحَمْدَ، وَالنِّعْمَةَ، لَكَ وَالْمُلْكَ، لاَ شَرِيكَ لَكَ",
            transliteration = "Labbayk Allahumma labbayk, labbayka la shareeka laka labbayk, innal-hamda wan-ni'mata laka wal-mulk, la shareeka lak",
            translation = "Here I am, O Allah, here I am. Here I am, You have no partner, here I am. Verily all praise, grace, and sovereignty belong to You. You have no partner.",
            source = "Sahih al-Bukhari 1549"
        ),
        DuaEntity(
            id = "dua_arafah",
            category = "Hajj & Umrah",
            title = "Best Supplication of the Day of Arafah",
            arabicText = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ، وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadeer",
            translation = "There is no deity except Allah alone, without partner; to Him belongs sovereignty, to Him belongs praise, and He is over all things competent.",
            source = "Sunan at-Tirmidhi 3585"
        )
    )
}

