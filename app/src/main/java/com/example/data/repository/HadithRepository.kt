package com.example.data.repository

import com.example.data.local.dao.HadithDao
import com.example.data.local.entity.HadithEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class HadithRepository(
    private val hadithDao: HadithDao
) {
    val collections = listOf("All", "Sahih Bukhari", "Sahih Muslim", "40 Hadith Nawawi", "Sunan Abi Dawud")

    fun getAllHadiths(): Flow<List<HadithEntity>> = hadithDao.getAllHadiths()

    fun getHadithsByCollection(collection: String): Flow<List<HadithEntity>> {
        return if (collection == "All") {
            hadithDao.getAllHadiths()
        } else {
            hadithDao.getHadithsByCollection(collection)
        }
    }

    fun getFavoriteHadiths(): Flow<List<HadithEntity>> = hadithDao.getFavoriteHadiths()

    fun searchHadiths(query: String): Flow<List<HadithEntity>> = hadithDao.searchHadiths(query)

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        hadithDao.updateFavorite(id, isFavorite)
    }

    suspend fun preloadHadithsIfNeeded() = withContext(Dispatchers.IO) {
        val existing = hadithDao.getAllHadiths().firstOrNull()
        if (existing.isNullOrEmpty()) {
            hadithDao.insertHadiths(initialHadithList)
        }
    }

    fun getHadithOfTheDay(): HadithEntity {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val index = dayOfYear % initialHadithList.size
        return initialHadithList[index]
    }

    val initialHadithList: List<HadithEntity> = listOf(
        HadithEntity(
            id = "hadith_actions_intentions",
            collection = "Sahih Bukhari",
            bookNumber = 1,
            hadithNumber = 1,
            narrator = "Umar ibn Al-Khattab (may Allah be pleased with him)",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            translation = "Actions are judged by motives and intentions, and every person will get the reward according to what he has intended.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Revelation & Intentions"
        ),
        HadithEntity(
            id = "hadith_spread_peace",
            collection = "Sahih Muslim",
            bookNumber = 1,
            hadithNumber = 54,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "لاَ تَدْخُلُونَ الْجَنَّةَ حَتَّى تُؤْمِنُوا وَلاَ تُؤْمِنُوا حَتَّى تَحَابُّوا. أَوَلاَ أَدُلُّكُمْ عَلَى شَىْءٍ إِذَا فَعَلْتُمُوهُ تَحَابَبْتُمْ أَفْشُوا السَّلاَمَ بَيْنَكُمْ",
            translation = "You will not enter Paradise until you believe, and you will not believe until you love one another. Shall I not direct you to a thing which, if you do it, you will love one another? Spread peace among yourselves.",
            authenticity = "Sahih",
            chapter = "Faith & Mutual Love"
        ),
        HadithEntity(
            id = "hadith_kindness",
            collection = "Sahih Muslim",
            bookNumber = 45,
            hadithNumber = 2594,
            narrator = "Aisha (may Allah be pleased with her)",
            arabicText = "إِنَّ الرِّفْقَ لاَ يَكُونُ فِي شَىْءٍ إِلاَّ زَانَهُ وَلاَ يُنْزَعُ مِنْ شَىْءٍ إِلاَّ شَانَهُ",
            translation = "Indeed, gentle kindness is not found in anything except that it beautifies it, and it is not withdrawn from anything except that it blemishes it.",
            authenticity = "Sahih",
            chapter = "Virtue of Gentleness"
        ),
        HadithEntity(
            id = "hadith_best_quran",
            collection = "Sahih Bukhari",
            bookNumber = 66,
            hadithNumber = 5027,
            narrator = "Uthman ibn Affan (may Allah be pleased with him)",
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            translation = "The best of you are those who learn the Qur'an and teach it to others.",
            authenticity = "Sahih",
            chapter = "Virtues of the Qur'an"
        ),
        HadithEntity(
            id = "hadith_smiling_charity",
            collection = "Sunan Abi Dawud",
            bookNumber = 41,
            hadithNumber = 4790,
            narrator = "Abu Dharr (may Allah be pleased with him)",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            translation = "Your smiling in the face of your brother is charity for you.",
            authenticity = "Hasan Sahih",
            chapter = "Good Manners & Charity"
        ),
        HadithEntity(
            id = "hadith_none_believes_brother",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 13,
            narrator = "Anas ibn Malik (may Allah be pleased with him)",
            arabicText = "لا يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            translation = "None of you truly believes until he loves for his brother what he loves for himself.",
            authenticity = "Sahih",
            chapter = "Brotherhood in Faith"
        ),
        HadithEntity(
            id = "hadith_strong_believer",
            collection = "Sahih Muslim",
            bookNumber = 46,
            hadithNumber = 2664,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "الْمُؤْمِنُ الْقَوِيُّ خَيْرٌ وَأَحَبُّ إِلَى اللَّهِ مِنَ الْمُؤْمِنِ الضَّعِيفِ، وَفِي كُلٍّ خَيْرٌ. احْرِصْ عَلَى مَا يَنْفَعُكَ، وَاسْتَعِنْ بِاللَّهِ وَلاَ تَعْجِزْ",
            translation = "A strong believer is better and is more beloved to Allah than a weak believer, though there is goodness in both. Cherish that which gives you benefit in the Hereafter and seek help from Allah and do not lose heart.",
            authenticity = "Sahih",
            chapter = "Divine Decree & Effort"
        ),
        HadithEntity(
            id = "hadith_seeking_knowledge",
            collection = "Sunan Abi Dawud",
            bookNumber = 25,
            hadithNumber = 3641,
            narrator = "Abu Darda (may Allah be pleased with him)",
            arabicText = "مَنْ سَلَكَ طَرِيقًا يَطْلُبُ فِيهِ عِلْمًا سَلَكَ اللَّهُ بِهِ طَرِيقًا مِنْ طُرُقِ الْجَنَّةِ",
            translation = "Whoever treads a path in search of knowledge, Allah will make easy for him a path to Paradise.",
            authenticity = "Sahih",
            chapter = "Seeking Sacred Knowledge"
        )
    )
}
