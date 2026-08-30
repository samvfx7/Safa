package com.example.data.repository

import com.example.data.local.dao.DuaDao
import com.example.data.local.entity.DuaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class DuaRepository(
    private val duaDao: DuaDao
) {
    val categories = listOf(
        "All",
        "Daily",
        "Morning & Evening",
        "Protection & Safety",
        "Prayer & Wudu",
        "Forgiveness",
        "Health & Recovery",
        "Family & Home",
        "Travel",
        "Work & Studies",
        "Gratitude",
        "Hajj & Umrah"
    )

    fun getAllDuas(): Flow<List<DuaEntity>> = duaDao.getAllDuas()

    fun getDuasByCategory(category: String): Flow<List<DuaEntity>> {
        return if (category == "All") {
            duaDao.getAllDuas()
        } else {
            duaDao.getDuasByCategory(category)
        }
    }

    fun getFavoriteDuas(): Flow<List<DuaEntity>> = duaDao.getFavoriteDuas()

    fun searchDuas(query: String): Flow<List<DuaEntity>> = duaDao.searchDuas(query)

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        duaDao.updateFavorite(id, isFavorite)
    }

    suspend fun preloadDuasIfNeeded() = withContext(Dispatchers.IO) {
        val existing = duaDao.getAllDuas().firstOrNull()
        if (existing.isNullOrEmpty()) {
            duaDao.insertDuas(initialDuasList)
        }
    }

    val initialDuasList: List<DuaEntity> = listOf(
        // Daily
        DuaEntity(
            id = "dua_wake_up",
            category = "Daily",
            title = "Upon Waking Up",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Alhamdu lillahil-lathee ahyana ba'da ma amatana wa-ilayhin-nushoor",
            translation = "All praise is for Allah who gave us life after having taken it from us and unto Him is the resurrection.",
            source = "Sahih al-Bukhari 6312",
            benefit = "Re-establishes gratitude immediately upon returning to consciousness."
        ),
        DuaEntity(
            id = "dua_sleeping",
            category = "Daily",
            title = "Before Sleeping",
            arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            transliteration = "Bismika Rabbi wada'tu janbi, wa bika arfa'uh. Fa-in amsakta nafsi farhamha, wa in arsaltaha fahfadh-ha bima tahfadhu bihi 'ibadakas-saliheen",
            translation = "In Your name my Lord, I lie down and in Your name I arise, so if You should take my soul then have mercy upon it, and if You should return my soul then protect it in the manner You do so with Your righteous servants.",
            source = "Sahih al-Bukhari 6320"
        ),
        DuaEntity(
            id = "dua_entering_home",
            category = "Daily",
            title = "Entering the Home",
            arabicText = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى رَبِّنَا تَوَكَّلْنَا",
            transliteration = "Bismillahi walajna, wa bismillahi kharajna, wa 'ala Rabbina tawakkalna",
            translation = "In the name of Allah we enter, and in the name of Allah we leave, and upon our Lord we place our trust.",
            source = "Sunan Abi Dawud 5096"
        ),
        DuaEntity(
            id = "dua_leaving_home",
            category = "Daily",
            title = "Leaving the Home",
            arabicText = "بِسْمِ اللَّهِ ، تَوَكَّلْتُ عَلَى اللَّهِ ، وَلا حَوْلَ وَلا قُوَّةَ إِلاَّ بِاللَّهِ",
            transliteration = "Bismillahi tawakkaltu 'alallah, wa la hawla wa la quwwata illa billah",
            translation = "In the name of Allah, I place my trust in Allah; there is no power nor might except with Allah.",
            source = "Sunan Abi Dawud 5095"
        ),
        DuaEntity(
            id = "dua_before_eating",
            category = "Daily",
            title = "Before Eating",
            arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            transliteration = "Bismillahir-Rahmanir-Raheem",
            translation = "In the Name of Allah, the Entirely Merciful, the Especially Merciful.",
            source = "Sahih al-Bukhari 5376"
        ),
        DuaEntity(
            id = "dua_after_eating",
            category = "Daily",
            title = "After Eating",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
            transliteration = "Alhamdu lillahil-lathee at'amani hatha wa razaqanihi min ghayri hawlin minni wa la quwwah",
            translation = "Praise be to Allah Who has fed me this and provided it for me without any might or power from myself.",
            source = "Sunan at-Tirmidhi 3458"
        ),

        // Morning & Evening
        DuaEntity(
            id = "dua_morning_adhkar",
            category = "Morning & Evening",
            title = "Morning Master Supplication (Sayyidul Istighfar)",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'oodhu bika min sharri ma sana'tu, aboo'u laka bini'matika 'alayya, wa aboo'u bidhanbi faghfir li fa-innahu la yaghfirudh-dhunooba illa Anta",
            translation = "O Allah, You are my Lord, none has the right to be worshiped but You. You created me and I am Your servant, and I abide to Your covenant and promise as best I can. I take refuge in You from the evil which I have committed. I acknowledge Your favor upon me and I acknowledge my sin, so forgive me, for none forgives sins except You.",
            source = "Sahih al-Bukhari 6306",
            benefit = "Whoever recites it during the day with firm faith and dies before evening will be among the people of Paradise."
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

        // Protection & Safety
        DuaEntity(
            id = "dua_protection_evil",
            category = "Protection & Safety",
            title = "Protection Against All Harm (3x)",
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
            source = "Sunan at-Tirmidhi 55"
        ),

        // Forgiveness & Repentance
        DuaEntity(
            id = "dua_prophet_yunus",
            category = "Forgiveness",
            title = "Supplication of Prophet Yunus (In Distress)",
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            transliteration = "La ilaha illa Anta subhanaka inni kuntu minaz-zalimeen",
            translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            source = "Surah Al-Anbiya 21:87 / Sunan at-Tirmidhi 3505",
            benefit = "No Muslim supplication is made with these words for any distress except that Allah responds."
        ),
        DuaEntity(
            id = "dua_forgive_all",
            category = "Forgiveness",
            title = "Comprehensive Forgiveness",
            arabicText = "رَبَّنَا اغْفِرْ لَنَا ذُنُوبَنَا وَإِسْرَافَنَا فِي أَمْرِنَا وَثَبِّتْ أَقْدَامَنَا وَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
            transliteration = "Rabbanagh-fir lana dhunoobana wa israfana fee amrina wa thabbit aqdamana wansurna 'alal-qawmil-kafireen",
            translation = "Our Lord, forgive us our sins and the excess [committed] in our affairs and plant firmly our feet and give us victory over the disbelieving people.",
            source = "Surah Ali 'Imran 3:147"
        ),

        // Health & Recovery
        DuaEntity(
            id = "dua_visiting_sick",
            category = "Health & Recovery",
            title = "For Healing the Sick",
            arabicText = "أَسْأَلُ اللَّهَ الْعَظِيمَ رَبَّ الْعَرْشِ الْعَظِيمِ أَنْ يَشْفِيَكَ",
            transliteration = "As'alullahal-'Adheema Rabbal-'Arshil-'Adheemi an yashfiyak",
            translation = "I ask Allah the Almighty, the Lord of the Mighty Throne, to heal you. (Recite 7x)",
            source = "Sunan Abi Dawud 3106"
        ),
        DuaEntity(
            id = "dua_pain_body",
            category = "Health & Recovery",
            title = "When Feeling Pain in the Body",
            arabicText = "بِسْمِ اللَّهِ (3x) • أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (7x)",
            transliteration = "Bismillah (3x) • A'oodhu billahi wa qudratihi min sharri ma ajidu wa uhadhir (7x)",
            translation = "In the Name of Allah (3x). I seek refuge in Allah and in His power from the evil of what I find and of what I fear (7x).",
            source = "Sahih Muslim 2202"
        ),

        // Family & Home
        DuaEntity(
            id = "dua_parents",
            category = "Family & Home",
            title = "Dua for Parents",
            arabicText = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-hamhuma kama rabbayani sagheera",
            translation = "My Lord, have mercy upon them as they brought me up [when I was] small.",
            source = "Surah Al-Isra 17:24"
        ),
        DuaEntity(
            id = "dua_spouse_children",
            category = "Family & Home",
            title = "For Righteous Spouse and Offspring",
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            transliteration = "Rabbana hab lana min azwajina wa dhurriyyatina qurrata a'yunin waj'alna lil-muttaqeena imama",
            translation = "Our Lord, grant us from among our wives and offspring comfort to our eyes and make us an example for the righteous.",
            source = "Surah Al-Furqan 25:74"
        ),

        // Travel
        DuaEntity(
            id = "dua_travel_mount",
            category = "Travel",
            title = "Boarding a Vehicle / Travel",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
            transliteration = "Subhanal-lathee sakh-khara lana hatha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon",
            translation = "Glory to Him who has subjected this to us, and we could never have it [by our efforts], and indeed, to our Lord we will return.",
            source = "Surah Az-Zukhruf 43:13-14 / Sahih Muslim 1342"
        ),

        // Work & Studies
        DuaEntity(
            id = "dua_increase_knowledge",
            category = "Work & Studies",
            title = "Seeking Increase in Knowledge",
            arabicText = "رَّبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidnee 'ilma",
            translation = "My Lord, increase me in knowledge.",
            source = "Surah Ta-Ha 20:114"
        ),
        DuaEntity(
            id = "dua_ease_tasks",
            category = "Work & Studies",
            title = "When Facing Difficult Tasks",
            arabicText = "اللَّهُمَّ لاَ سَهْلَ إِلاَّ مَا جَعَلْتَهُ سَهْلاً، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلاً",
            transliteration = "Allahumma la sahla illa ma ja'altahu sahla, wa Anta taj'alul-hazna idha shi'ta sahla",
            translation = "O Allah, there is no ease except in that which You have made easy, and You make the difficulty, if You wish, easy.",
            source = "Sahih Ibn Hibban 327"
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
