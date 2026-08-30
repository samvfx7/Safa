package com.example.audio

data class QariReciter(
    val id: String,
    val name: String,
    val englishName: String,
    val fullSurahUrlPattern: String,
    val ayahUrlPattern: String = "https://everyayah.com/data/Alafasy_128kbps/%03d%03d.mp3",
    val description: String = ""
) {
    fun getFullSurahAudioUrl(surahNumber: Int): String {
        return String.format(fullSurahUrlPattern, surahNumber)
    }

    fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int): String {
        return String.format(ayahUrlPattern, surahNumber, ayahNumber)
    }
}

object QuranReciters {
    val RECITERS = listOf(
        QariReciter(
            id = "alafasy",
            name = "مشاري راشد العفاسي",
            englishName = "Mishary Rashid Alafasy",
            fullSurahUrlPattern = "https://server8.mp3quran.net/afs/%03d.mp3",
            ayahUrlPattern = "https://everyayah.com/data/Alafasy_128kbps/%03d%03d.mp3",
            description = "Kuwaiti Qari renowned worldwide for melodious and clear recitation"
        ),
        QariReciter(
            id = "abdulbasit",
            name = "عبد الباسط عبد الصمد",
            englishName = "Abdul Basit (Murattal)",
            fullSurahUrlPattern = "https://server7.mp3quran.net/basit/%03d.mp3",
            ayahUrlPattern = "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/%03d%03d.mp3",
            description = "Legendary Egyptian Qari known for timeless classic Murattal recitation"
        ),
        QariReciter(
            id = "maher",
            name = "ماهر المعيقلي",
            englishName = "Maher Al-Muaiqly",
            fullSurahUrlPattern = "https://server12.mp3quran.net/maher/%03d.mp3",
            ayahUrlPattern = "https://everyayah.com/data/MaherAlMuaiqly128kbps/%03d%03d.mp3",
            description = "Imam of Masjid al-Haram in Makkah with heartfelt recitation"
        ),
        QariReciter(
            id = "ghamdi",
            name = "سعد الغامدي",
            englishName = "Saad Al-Ghamdi",
            fullSurahUrlPattern = "https://server7.mp3quran.net/s_gmd/%03d.mp3",
            ayahUrlPattern = "https://everyayah.com/data/Ghamadi_40kbps/%03d%03d.mp3",
            description = "Distinctive, serene Saudi Qari recitation"
        ),
        QariReciter(
            id = "husary",
            name = "محمود خليل الحصري",
            englishName = "Mahmoud Khalil Al-Husary",
            fullSurahUrlPattern = "https://server13.mp3quran.net/husr/%03d.mp3",
            ayahUrlPattern = "https://everyayah.com/data/Husary_128kbps/%03d%03d.mp3",
            description = "Master of Tajweed and pristine classical articulation"
        ),
        QariReciter(
            id = "shatri",
            name = "أبو بكر الشاطري",
            englishName = "Abu Bakr Al-Shatri",
            fullSurahUrlPattern = "https://server11.mp3quran.net/shatri/%03d.mp3",
            ayahUrlPattern = "https://everyayah.com/data/Abu_Bakr_Ash-Shaatree_128kbps/%03d%03d.mp3",
            description = "Emotional and moving recitation style"
        )
    )

    val DEFAULT_RECITER = RECITERS[0]

    fun getReciterById(id: String): QariReciter {
        return RECITERS.find { it.id == id } ?: DEFAULT_RECITER
    }
}
