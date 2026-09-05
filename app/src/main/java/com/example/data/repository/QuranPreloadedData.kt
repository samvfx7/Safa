package com.example.data.repository

object QuranPreloadedData {

    fun getPreloadedAyahs(surahNumber: Int): List<Ayah>? {
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
                Ayah(2, 255, 262, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Allahu laa ilaaha illaa Huwal-Hayyul-Qayyuum; laa ta'khuzuhuu sinatunw-wa laa nawm; lahuu maa fis-samaawaati wa maa fil-ard; man zal-lazii yashfa'u 'indahuuu illaa bi-iznih; ya'lamu maa bayna aydiihim wa maa khalfahum wa laa yuhiituuna bishay'im-min 'ilmihiii illaa bimaa shaaa'; wasi'a Kursiyyuhus-samaawaati wal-arda wa laa ya'uuduhuu hifzuhumaa; wa Huwal-'Aliyyul-'Aziim", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great. (Ayat al-Kursi)", "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"),
                Ayah(2, 285, 292, "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ", "Aamanar-Rasuulu bimaaa unzila ilayhi mir-Rabbihii wal-mu'minuun; kullun aamana billaahi wa malaaa'ikatihii wa kutubihii wa rusulih...", "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers. All of them have believed in Allah and His angels and His books and His messengers, [saying], 'We make no distinction between any of His messengers.' And they say, 'We hear and we obey. [We seek] Your forgiveness, our Lord, and to You is the [final] destination.'", "https://everyayah.com/data/Alafasy_128kbps/002285.mp3"),
                Ayah(2, 286, 293, "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ", "Laa yukalliful-laahu nafsan illaa wus'ahaa; lahaa maa kasabat wa 'alayhaa maktasabat; Rabbanaa laa tu'aakhiznaaa in nasiinaaa aw akhta'naa...", "Allah does not charge a soul except [with that within] its capacity. It will have [the consequence of] what [good] it has gained, and it will bear [the consequence of] what [evil] it has earned. 'Our Lord, do not impose blame upon us if we have forgotten or erred. Our Lord, and lay not upon us a burden like that which You laid upon those before us. Our Lord, and burden us not with that which we have no ability to bear. And pardon us; and forgive us; and have mercy upon us. You are our protector, so give us victory over the disbelieving people.'", "https://everyayah.com/data/Alafasy_128kbps/002286.mp3")
            )
            18 -> listOf(
                Ayah(18, 1, 2141, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا", "Alhamdu lillaahil laziii anzala 'alaa 'abdihil kitaaba wa lam yaj'al lahuu 'iwajaa", "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance.", "https://everyayah.com/data/Alafasy_128kbps/018001.mp3"),
                Ayah(18, 2, 2142, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "Qayyimal liyunzira ba'san shadiidam mil ladunhu wa yubashshiral mu'miniinal laziina ya'maluunas saalihaati anna lahum ajran hasanaa", "[He has made it] straight, to warn of severe punishment from Him and to give good tidings to the believers who do righteous deeds that they will have a good reward,", "https://everyayah.com/data/Alafasy_128kbps/018002.mp3"),
                Ayah(18, 3, 2143, "مَّاكِثِينَ فِيهِ أَبَدًا", "Maakisiina fiihi abadaa", "In which they will remain forever.", "https://everyayah.com/data/Alafasy_128kbps/018003.mp3"),
                Ayah(18, 4, 2144, "وَيُنذِرَ الَّذِينَ قَالُوا اتَّخَذَ اللَّهُ وَلَدًا", "Wa yunzirallaziina qaalut takhazal laahu waladaa", "And to warn those who say, 'Allah has taken a son.'", "https://everyayah.com/data/Alafasy_128kbps/018004.mp3"),
                Ayah(18, 10, 2150, "إِذْ أَوَى الْفِتْيَةُ إِلَى الْكَهْفِ فَقَالُوا رَبَّنَا آتِنَا مِن لَّدُنكَ رَحْمَةً وَهَيِّئْ لَنَا مِنْ أَمْرِنَا رَشَدًا", "Iz awal fityatu ilal Kahfi faqaaluu Rabbanaaa aatinaa mil ladunka rahmatanw wa hayyi' lanaa min amrinaa rashadaa", "[Mention] when the youths retreated to the cave and said, 'Our Lord, grant us from Yourself mercy and prepare for us from our affair right guidance.'", "https://everyayah.com/data/Alafasy_128kbps/018010.mp3"),
                Ayah(18, 107, 2247, "إِنَّ الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ كَانَتْ لَهُمْ جَنَّاتُ الْفِرْدَوْسِ نُزُلًا", "Innal laziina aamanuu wa 'amilus saalihaati kaanat lahum Jannaatul Firdawsi nuzulaa", "Indeed, those who have believed and done righteous deeds - they will have the Gardens of Paradise as a lodging,", "https://everyayah.com/data/Alafasy_128kbps/018107.mp3"),
                Ayah(18, 108, 2248, "خَالِدِينَ فِيهَا لَا يَبْغُونَ عَنْهَا حِوَلًا", "Khaalidiina fiihaa laa yabghuuna 'anhaa hiwalaa", "Wherein they abide eternally. They will not desire therefrom any transfer.", "https://everyayah.com/data/Alafasy_128kbps/018108.mp3"),
                Ayah(18, 109, 2249, "قُل لَّوْ كَانَ الْبَحْرُ مِدَادًا لِّكَلِمَاتِ رَبِّي لَنَفِدَ الْبَحْرُ قَبْلَ أَن تَنفَدَ كَلِمَاتُ رَبِّي وَلَوْ جِئْنَا بِمِثْلِهِ مَدَدًا", "Qul law kaanal bahru midaadal likalimaati Rabbii lanafidal bahru qabla an tanfada Kalimaatu Rabbii...", "Say, 'If the sea were ink for [writing] the words of my Lord, the sea would be exhausted before the words of my Lord were exhausted, even if We brought the like of it as a supplement.'", "https://everyayah.com/data/Alafasy_128kbps/018109.mp3"),
                Ayah(18, 110, 2250, "قُلْ إِنَّمَا أَنَا بَشَرٌ مِّثْلُكُمْ يُوحَىٰ إِلَيَّ أَنَّمَا إِلَٰهُكُمْ إِلَٰهٌ وَاحِدٌ ۖ فَمَن كَانَ يَرْجُو لِقَاءَ رَبِّهِ فَلْيَعْمَلْ عَمَلًا صَالِحًا وَلَا يُشْرِكْ بِعِبَادَةِ رَبِّهِ أَحَدًا", "Qul innamaaa ana basharum mislukum yuuhaaa ilayya annamaaa Ilaahukum Ilaahunw Waahid...", "Say, 'I am only a man like you, to whom has been revealed that your god is one God. So whoever would hope for the meeting with his Lord - let him do righteous work and not associate in the worship of his Lord anyone.'", "https://everyayah.com/data/Alafasy_128kbps/018110.mp3")
            )
            36 -> listOf(
                Ayah(36, 1, 3706, "يس", "Yaa-Siiin", "Ya, Seen.", "https://everyayah.com/data/Alafasy_128kbps/036001.mp3"),
                Ayah(36, 2, 3707, "وَالْقُرْآنِ الْحَكِيمِ", "Wal-Qur-aanil-Hakiim", "By the wise Qur'an,", "https://everyayah.com/data/Alafasy_128kbps/036002.mp3"),
                Ayah(36, 3, 3708, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Innaka laminal-mursaliin", "Indeed you, [O Muhammad], are from among the messengers,", "https://everyayah.com/data/Alafasy_128kbps/036003.mp3"),
                Ayah(36, 4, 3709, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "'Alaa Siraatim-Mustaqiim", "On a straight path.", "https://everyayah.com/data/Alafasy_128kbps/036004.mp3"),
                Ayah(36, 5, 3710, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "Tanziilal-'Aziizir-Rahiim", "[This is] a revelation of the Exalted in Might, the Merciful.", "https://everyayah.com/data/Alafasy_128kbps/036005.mp3"),
                Ayah(36, 6, 3711, "لِتُنذِرَ قَوْمًا مَّا أُنذِرَ آبَاؤُهُمْ فَهُمْ غَافِلُونَ", "Litunzira qawmam maaa unzira aabaaa'uhum fahum ghaafiluun", "That you may warn a people whose forefathers were not warned, so they are unaware.", "https://everyayah.com/data/Alafasy_128kbps/036006.mp3"),
                Ayah(36, 58, 3763, "سَلَامٌ قَوْلًا مِّن رَّبٍّ رَّحِيمٍ", "Salaamun qawlam mir Rabbir Rahiim", "[And] 'Peace,' a word from a Merciful Lord.", "https://everyayah.com/data/Alafasy_128kbps/036058.mp3"),
                Ayah(36, 82, 3787, "إِنَّمَا أَمْرُهُ إِذَا أَرَادَ شَيْئًا أَن يَقُولَ لَهُ كُن فَيَكُونُ", "Innamaaa amruhuuu izaaa araada shay'an any yaquula lahuu Kun fa-yakuun", "His command is only when He intends a thing that He says to it, 'Be,' and it is.", "https://everyayah.com/data/Alafasy_128kbps/036082.mp3"),
                Ayah(36, 83, 3788, "فَسُبْحَانَ الَّذِي بِيَدِهِ مَلَكُوتُ كُلِّ شَيْءٍ وَإِلَيْهِ تُرْجَعُونَ", "Fasubhaanal lazii biyadihii malakuutu kulli shay'inw wa ilayhi turja'uun", "So exalted is He in whose hand is the realm of all things, and to Him you will be returned.", "https://everyayah.com/data/Alafasy_128kbps/036083.mp3")
            )
            55 -> listOf(
                Ayah(55, 1, 4902, "الرَّحْمَٰنُ", "Ar-Rahmaan", "The Most Merciful", "https://everyayah.com/data/Alafasy_128kbps/055001.mp3"),
                Ayah(55, 2, 4903, "عَلَّمَ الْقُرْآنَ", "'Allamal-Qur-aan", "Taught the Qur'an,", "https://everyayah.com/data/Alafasy_128kbps/055002.mp3"),
                Ayah(55, 3, 4904, "خَلَقَ الْإِنسَانَ", "Khalaqal-insaan", "Created man,", "https://everyayah.com/data/Alafasy_128kbps/055003.mp3"),
                Ayah(55, 4, 4905, "عَلَّمَهُ الْبَيَانَ", "'Allamahul-bayaan", "[And] taught him eloquence.", "https://everyayah.com/data/Alafasy_128kbps/055004.mp3"),
                Ayah(55, 13, 4914, "فَبِأَيِّ آلَاءِ رَبِّكُمَا تُكَذِّبَانِ", "Fabi'ayyi aalaaa'i Rabbikumaa tukazzibaan", "So which of the favors of your Lord would you deny?", "https://everyayah.com/data/Alafasy_128kbps/055013.mp3"),
                Ayah(55, 60, 4961, "هَلْ جَزَاءُ الْإِحْسَانِ إِلَّا الْإِحْسَانُ", "Hal jazaaa'ul-ihsaani illal-ihsaan", "Is the reward for good [anything] but good?", "https://everyayah.com/data/Alafasy_128kbps/055060.mp3")
            )
            67 -> listOf(
                Ayah(67, 1, 5242, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabaarakal-lazii biyadihil-mulku wa Huwa 'alaa kulli shay'in Qadiir", "Blessed is He in whose hand is dominion, and He is over all things competent -", "https://everyayah.com/data/Alafasy_128kbps/067001.mp3"),
                Ayah(67, 2, 5243, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "Allazii khalaqal-mawta wal-hayaata liyabluwakum ayyukum ahsanu 'amalaa; wa Huwal-'Aziizul-Ghafuur", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "https://everyayah.com/data/Alafasy_128kbps/067002.mp3"),
                Ayah(67, 3, 5244, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "Allazii khalaqa sab'a samaawaatin tibaaqan...", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency.", "https://everyayah.com/data/Alafasy_128kbps/067003.mp3"),
                Ayah(67, 4, 5245, "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ", "Summarji'il basara karratayni yanqalib ilaykal basaru khaasi'anw wa huwa hasiir", "Then return [your] vision twice again. [Your] vision will return to you humbled while it is fatigued.", "https://everyayah.com/data/Alafasy_128kbps/067004.mp3")
            )
            87 -> listOf(
                Ayah(87, 1, 5979, "سَبِّحِ اسْمَ رَبِّكَ الْأَعْلَى", "Sabbihisma Rabbikal-A'laa", "Exalt the name of your Lord, the Most High,", "https://everyayah.com/data/Alafasy_128kbps/087001.mp3"),
                Ayah(87, 2, 5980, "الَّذِي خَلَقَ فَسَوَّىٰ", "Allazii khalaqa fasawwaa", "Who created and proportioned,", "https://everyayah.com/data/Alafasy_128kbps/087002.mp3"),
                Ayah(87, 3, 5981, "وَالَّذِي قَدَّرَ فَهَدَىٰ", "Wallazii qaddara fahadaa", "And who destined and [then] guided,", "https://everyayah.com/data/Alafasy_128kbps/087003.mp3"),
                Ayah(87, 4, 5982, "وَالَّذِي أَخْرَجَ الْمَرْعَىٰ", "Wallaziii akhrajal-mar'aa", "And who brings out the pasture,", "https://everyayah.com/data/Alafasy_128kbps/087004.mp3"),
                Ayah(87, 5, 5983, "فَجَعَلَهُ غُثَاءً أَحْوَىٰ", "Faja'alahuu ghusaaa'an ahwaa", "And [then] makes it black stubble.", "https://everyayah.com/data/Alafasy_128kbps/087005.mp3"),
                Ayah(87, 14, 5992, "قَدْ أَفْلَحَ مَن تَزَكَّىٰ", "Qad aflaha man tazakkaa", "He has certainly succeeded who purifies himself", "https://everyayah.com/data/Alafasy_128kbps/087014.mp3"),
                Ayah(87, 15, 5993, "وَذَكَرَ اسْمَ رَبِّهِ فَصَلَّىٰ", "Wa zakaras ma Rabbihii fasallaa", "And mentions the name of his Lord and prays.", "https://everyayah.com/data/Alafasy_128kbps/087015.mp3")
            )
            89 -> listOf(
                Ayah(89, 1, 6004, "وَالْفَجْرِ", "Wal-Fajr", "By the dawn,", "https://everyayah.com/data/Alafasy_128kbps/089001.mp3"),
                Ayah(89, 2, 6005, "وَلَيَالٍ عَشْرٍ", "Wa layaalin 'ashr", "And [by] the ten nights,", "https://everyayah.com/data/Alafasy_128kbps/089002.mp3"),
                Ayah(89, 3, 6006, "وَالشَّفْعِ وَالْوَتْرِ", "Wash-shaf'i wal-watr", "And [by] the even and the odd,", "https://everyayah.com/data/Alafasy_128kbps/089003.mp3"),
                Ayah(89, 4, 6007, "وَاللَّيْلِ إِذَا يَسْرِ", "Wal-layli izaa yasr", "And [by] the night when it passes,", "https://everyayah.com/data/Alafasy_128kbps/089004.mp3"),
                Ayah(89, 27, 6030, "يَا أَيَّتُهَا النَّفْسُ الْمُطْمَئِنَّةُ", "Yaaa ayyatuhan-nafsul-mutma'innah", "[To the righteous it will be said], 'O reassured soul,", "https://everyayah.com/data/Alafasy_128kbps/089027.mp3"),
                Ayah(89, 28, 6031, "ارْجِعِي إِلَىٰ رَبِّكِ رَاضِيَةً مَّرْضِيَّةً", "Irji'iii ilaa Rabbiki raadiyatam mardiyyah", "Return to your Lord, well-pleased and pleasing [to Him],", "https://everyayah.com/data/Alafasy_128kbps/089028.mp3"),
                Ayah(89, 29, 6032, "فَادْخُلِي فِي عِبَادِي", "Fadkhulii fii 'ibaadii", "And enter among My [righteous] servants,", "https://everyayah.com/data/Alafasy_128kbps/089029.mp3"),
                Ayah(89, 30, 6033, "وَادْخُلِي جَنَّتِي", "Wadkhulii Jannatii", "And enter My Paradise.'", "https://everyayah.com/data/Alafasy_128kbps/089030.mp3")
            )
            93 -> listOf(
                Ayah(93, 1, 6080, "وَالضُّحَىٰ", "Wad-duhaa", "By the morning brightness", "https://everyayah.com/data/Alafasy_128kbps/093001.mp3"),
                Ayah(93, 2, 6081, "وَاللَّيْلِ إِذَا سَجَىٰ", "Wal-layli izaa sajaa", "And [by] the night when it covers with darkness,", "https://everyayah.com/data/Alafasy_128kbps/093002.mp3"),
                Ayah(93, 3, 6082, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Maa wadda'aka Rabbuka wa maa qalaa", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you].", "https://everyayah.com/data/Alafasy_128kbps/093003.mp3"),
                Ayah(93, 4, 6083, "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ", "Wa lal-Aakhiratu khayrul laka minal-uulaa", "And the Hereafter is better for you than the first [life].", "https://everyayah.com/data/Alafasy_128kbps/093004.mp3"),
                Ayah(93, 5, 6084, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "Wa lasawfa yu'tiika Rabbuka fatardaa", "And your Lord is going to give you, and you will be satisfied.", "https://everyayah.com/data/Alafasy_128kbps/093005.mp3"),
                Ayah(93, 6, 6085, "أَلَمْ يَجِدْكَ يَتِيمًا فَآوَىٰ", "Alam yajidka yatiiman fa-aawaa", "Did He not find you an orphan and give [you] refuge?", "https://everyayah.com/data/Alafasy_128kbps/093006.mp3"),
                Ayah(93, 7, 6086, "وَوَجَدَكَ ضَالًّا فَهَدَىٰ", "Wa wajadaka daaallan fahadaa", "And He found you lost and guided [you],", "https://everyayah.com/data/Alafasy_128kbps/093007.mp3"),
                Ayah(93, 8, 6087, "وَوَجَدَكَ عَائِلًا فَأَغْنَىٰ", "Wa wajadaka 'aa'ilan fa-aghnaa", "And He found you poor and made [you] self-sufficient.", "https://everyayah.com/data/Alafasy_128kbps/093008.mp3"),
                Ayah(93, 9, 6088, "فَأَمَّا الْيَتِيمَ فَلَا تَقْهَرْ", "Fa-ammal-yatiima falaa taqhar", "So as for the orphan, do not oppress [him].", "https://everyayah.com/data/Alafasy_128kbps/093009.mp3"),
                Ayah(93, 10, 6089, "وَأَمَّا السَّائِلَ فَلَا تَنْهَرْ", "Wa ammas-saaa'ila falaa tanhar", "And as for the petitioner, do not repel [him].", "https://everyayah.com/data/Alafasy_128kbps/093010.mp3"),
                Ayah(93, 11, 6090, "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ", "Wa ammaa bini'mati Rabbika fahaddis", "But as for the favor of your Lord, report [it].", "https://everyayah.com/data/Alafasy_128kbps/093011.mp3")
            )
            94 -> listOf(
                Ayah(94, 1, 6091, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Alam nashrah laka sadrak", "Did We not expand for you, [O Muhammad], your breast?", "https://everyayah.com/data/Alafasy_128kbps/094001.mp3"),
                Ayah(94, 2, 6092, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "Wa wada'naa 'anka wizrak", "And We removed from you your burden", "https://everyayah.com/data/Alafasy_128kbps/094002.mp3"),
                Ayah(94, 3, 6093, "الَّذِي أَنقَضَ ظَهْرَكَ", "Allaziii anqada zahrak", "Which had weighed upon your back", "https://everyayah.com/data/Alafasy_128kbps/094003.mp3"),
                Ayah(94, 4, 6094, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "Wa rafa'naa laka zikrak", "And raised high for you your repute.", "https://everyayah.com/data/Alafasy_128kbps/094004.mp3"),
                Ayah(94, 5, 6095, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "Fa-inna ma'al 'usri yusraa", "For indeed, with hardship [will be] ease.", "https://everyayah.com/data/Alafasy_128kbps/094005.mp3"),
                Ayah(94, 6, 6096, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Inna ma'al 'usri yusraa", "Indeed, with hardship [will be] ease.", "https://everyayah.com/data/Alafasy_128kbps/094006.mp3"),
                Ayah(94, 7, 6097, "فَإِذَا فَرَغْتَ فَانصَبْ", "Fa-izaa faraghta fansab", "So when you have finished [your duties], then stand up [for worship].", "https://everyayah.com/data/Alafasy_128kbps/094007.mp3"),
                Ayah(94, 8, 6098, "وَإِلَىٰ رَبِّكَ فَارْغَب", "Wa ilaa Rabbika farghab", "And to your Lord direct [your] longing.", "https://everyayah.com/data/Alafasy_128kbps/094008.mp3")
            )
            95 -> listOf(
                Ayah(95, 1, 6099, "وَالتِّينِ وَالزَّيْتُونِ", "Wat-tiini waz-zaytuun", "By the fig and the olive", "https://everyayah.com/data/Alafasy_128kbps/095001.mp3"),
                Ayah(95, 2, 6100, "وَطُورِ سِينِينَ", "Wa Tuuri Siiniin", "And [by] Mount Sinai", "https://everyayah.com/data/Alafasy_128kbps/095002.mp3"),
                Ayah(95, 3, 6101, "وَهَٰذَا الْبَلَدِ الْأَمِينِ", "Wa haazal-baladil-amiin", "And [by] this secure city [Makkah],", "https://everyayah.com/data/Alafasy_128kbps/095003.mp3"),
                Ayah(95, 4, 6102, "لَقَدْ خَلَقْنَا الْإِنسَانَ فِي أَحْسَنِ تَقْوِيمٍ", "Laqad khalaqnal-insaana fii ahsani taqwiim", "We have certainly created man in the best of stature;", "https://everyayah.com/data/Alafasy_128kbps/095004.mp3"),
                Ayah(95, 5, 6103, "ثُمَّ رَدَدْنَاهُ أَسْفَلَ سَافِلِينَ", "Summa radadnaahu asfala saafiliin", "Then We return him to the lowest of the low,", "https://everyayah.com/data/Alafasy_128kbps/095005.mp3"),
                Ayah(95, 6, 6104, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ فَلَهُمْ أَجْرٌ غَيْرُ مَمْنُونٍ", "Illal-laziina aamanuu wa 'amilus-saalihaati falahum ajrun ghayru mamnuun", "Except for those who believe and do righteous deeds, for they will have a reward uninterrupted.", "https://everyayah.com/data/Alafasy_128kbps/095006.mp3"),
                Ayah(95, 7, 6105, "فَمَا يُكَذِّبُكَ بَعْدُ بِالدِّينِ", "Famaa yukazzibuka ba'du bid-diin", "So what yet causes you to deny the Recompense?", "https://everyayah.com/data/Alafasy_128kbps/095007.mp3"),
                Ayah(95, 8, 6106, "أَلَيْسَ اللَّهُ بِأَحْكَمِ الْحَاكِمِينَ", "Alaysal-laahu bi-ahkamil-haakimiin", "Is not Allah the most just of judges?", "https://everyayah.com/data/Alafasy_128kbps/095008.mp3")
            )
            96 -> listOf(
                Ayah(96, 1, 6107, "اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ", "Iqra' bismi Rabbikal-lazii khalaq", "Recite in the name of your Lord who created -", "https://everyayah.com/data/Alafasy_128kbps/096001.mp3"),
                Ayah(96, 2, 6108, "خَلَقَ الْإِنسَانَ مِنْ عَلَقٍ", "Khalaqal-insaana min 'alaq", "Created man from a clinging substance.", "https://everyayah.com/data/Alafasy_128kbps/096002.mp3"),
                Ayah(96, 3, 6109, "اقْرَأْ وَرَبُّكَ الْأَكْرَمُ", "Iqra' wa Rabbukal-Akram", "Recite, and your Lord is the most Generous -", "https://everyayah.com/data/Alafasy_128kbps/096003.mp3"),
                Ayah(96, 4, 6110, "الَّذِي عَلَّمَ بِالْقَلَمِ", "Allazii 'allama bil-qalam", "Who taught by the pen -", "https://everyayah.com/data/Alafasy_128kbps/096004.mp3"),
                Ayah(96, 5, 6111, "عَلَّمَ الْإِنسَانَ مَا لَمْ يَعْلَمْ", "'Allamal-insaana maa lam ya'lam", "Taught man that which he knew not.", "https://everyayah.com/data/Alafasy_128kbps/096005.mp3")
            )
            97 -> listOf(
                Ayah(97, 1, 6126, "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "Innaaa anzalnaahu fii Laylatil-Qadr", "Indeed, We sent the Qur'an down during the Night of Decree.", "https://everyayah.com/data/Alafasy_128kbps/097001.mp3"),
                Ayah(97, 2, 6127, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "Wa maaa adraaka maa Laylatul-Qadr", "And what can make you know what is the Night of Decree?", "https://everyayah.com/data/Alafasy_128kbps/097002.mp3"),
                Ayah(97, 3, 6128, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ", "Laylatul-Qadri khayrum min alfi shahr", "The Night of Decree is better than a thousand months.", "https://everyayah.com/data/Alafasy_128kbps/097003.mp3"),
                Ayah(97, 4, 6129, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍ", "Tanazzalul-malaaa'ikatu war-Ruuhu fiihaa bi-izni Rabbihim min kulli amr", "The angels and the Spirit descend therein by permission of their Lord for every matter.", "https://everyayah.com/data/Alafasy_128kbps/097004.mp3"),
                Ayah(97, 5, 6130, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "Salaamun hiya hattaa matla'il-fajr", "Peace it is until the emergence of dawn.", "https://everyayah.com/data/Alafasy_128kbps/097005.mp3")
            )
            98 -> listOf(
                Ayah(98, 5, 6135, "وَمَا أُمِرُوا إِلَّا لِيَعْبُدُوا اللَّهَ مُخْلِصِينَ لَهُ الدِّينَ حُنَفَاءَ وَيُقِيمُوا الصَّلَاةَ وَيُؤْتُوا الزَّكَاةَ ۚ وَذَٰلِكَ دِينُ الْقَيِّمَةِ", "Wa maaa umiruuu illaa liya'budul-laaha mukhlisiina lahud-diina hunafaaa'a wa yuqiimus-Salaata wa yu'tuz-Zakaata; wa zaalika diinul-qayyimah", "And they were not commanded except to worship Allah, [being] sincere to Him in religion, inclining to truth, and to establish prayer and to give zakah. And that is the correct religion.", "https://everyayah.com/data/Alafasy_128kbps/098005.mp3"),
                Ayah(98, 7, 6137, "إِنَّ الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ أُولَٰئِكَ هُمْ خَيْرُ الْبَرِيَّةِ", "Innal-laziina aamanuu wa 'amilus-saalihaati ulaaa'ika hum khayrul-bariyyah", "Indeed, they who have believed and done righteous deeds - those are the best of creatures.", "https://everyayah.com/data/Alafasy_128kbps/098007.mp3"),
                Ayah(98, 8, 6138, "جَزَاؤُهُمْ عِندَ رَبِّهِمْ جَنَّاتُ عَدْنٍ تَجْرِي مِن تَحْتِهَا الْأَنْهَارُ خَالِدِينَ فِيهَا أَبَدًا", "Jazaaa'uhum 'inda Rabbihim Jannaatu 'Adnin tajrii min tahtihal-anhaaru khaalidiina fiihaaa abadaa", "Their reward with their Lord will be gardens of perpetual residence beneath which rivers flow, wherein they will abide forever.", "https://everyayah.com/data/Alafasy_128kbps/098008.mp3")
            )
            99 -> listOf(
                Ayah(99, 1, 6139, "إِذَا زُلْزِلَتِ الْأَرْضُ زِلْزَالَهَا", "Izaa zulzilatil-ardu zilzaalahaa", "When the earth is shaken with its [final] earthquake", "https://everyayah.com/data/Alafasy_128kbps/099001.mp3"),
                Ayah(99, 2, 6140, "وَأَخْرَجَتِ الْأَرْضُ أَثْقَالَهَا", "Wa akhrajatil-ardu asqaalahaa", "And the earth discharges its burdens", "https://everyayah.com/data/Alafasy_128kbps/099002.mp3"),
                Ayah(99, 3, 6141, "وَقَالَ الْإِنسَانُ مَا لَهَا", "Wa qaalal-insaanu maa lahaa", "And man says, 'What is [wrong] with it?' -", "https://everyayah.com/data/Alafasy_128kbps/099003.mp3"),
                Ayah(99, 4, 6142, "يَوْمَئِذٍ تُحَدِّثُ أَخْبَارَهَا", "Yawma'izin tuhaddisu akhbaarahaa", "That Day, it will report its news", "https://everyayah.com/data/Alafasy_128kbps/099004.mp3"),
                Ayah(99, 5, 6143, "بِأَنَّ رَبَّكَ أَوْحَىٰ لَهَا", "Bi-anna Rabbaka awhaa lahaa", "Because your Lord has commanded it.", "https://everyayah.com/data/Alafasy_128kbps/099005.mp3"),
                Ayah(99, 6, 6144, "يَوْمَئِذٍ يَصْدُرُ النَّاسُ أَشْتَاتًا لِّيُرَوْا أَعْمَالَهُمْ", "Yawma'iziny-yasdurun-naasu ashtaatal-liyuraw a'maalahum", "That Day, the people will depart separated [into categories] to be shown [the result of] their deeds.", "https://everyayah.com/data/Alafasy_128kbps/099006.mp3"),
                Ayah(99, 7, 6145, "فَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ خَيْرًا يَرَهُ", "Famay-ya'mal misqaala zarratin khayray-yarah", "So whoever does an atom's weight of good will see it,", "https://everyayah.com/data/Alafasy_128kbps/099007.mp3"),
                Ayah(99, 8, 6146, "وَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ شَرًّا يَرَهُ", "Wa may-ya'mal misqaala zarratin sharray-yarah", "And whoever does an atom's weight of evil will see it.", "https://everyayah.com/data/Alafasy_128kbps/099008.mp3")
            )
            100 -> listOf(
                Ayah(100, 1, 6147, "وَالْعَادِيَاتِ ضَبْحًا", "Wal-'aadiyaati dabhaa", "By the racers, panting,", "https://everyayah.com/data/Alafasy_128kbps/100001.mp3"),
                Ayah(100, 2, 6148, "فَالْمُورِيَاتِ قَدْحًا", "Fal-muuriyaati qadhaa", "And the producers of sparks [when] striking", "https://everyayah.com/data/Alafasy_128kbps/100002.mp3"),
                Ayah(100, 3, 6149, "فَالْمُغِيرَاتِ صُبْحًا", "Fal-mughiiraati subhaa", "And the chargers at dawn,", "https://everyayah.com/data/Alafasy_128kbps/100003.mp3"),
                Ayah(100, 4, 6150, "فَأَثَرْنَ بِهِ نَقْعًا", "Fa-asarna bihii naq'aa", "Stirring up thereby [clouds of] dust,", "https://everyayah.com/data/Alafasy_128kbps/100004.mp3"),
                Ayah(100, 5, 6151, "فَوَسَطْنَ بِهِ جَمْعًا", "Fawasatna bihii jam'aa", "Arriving in the center collectively,", "https://everyayah.com/data/Alafasy_128kbps/100005.mp3"),
                Ayah(100, 6, 6152, "إِنَّ الْإِنسَانَ لِرَبِّهِ لَكَنُودٌ", "Innal-insaana li-Rabbihii lakanuud", "Indeed mankind, to his Lord, is ungrateful.", "https://everyayah.com/data/Alafasy_128kbps/100006.mp3"),
                Ayah(100, 7, 6153, "وَإِنَّهُ عَلَىٰ ذَٰلِكَ لَشَهِيدٌ", "Wa innahuu 'alaa zaalika lashahiid", "And indeed, he to that is a witness.", "https://everyayah.com/data/Alafasy_128kbps/100007.mp3"),
                Ayah(100, 8, 6154, "وَإِنَّهُ لِحُبِّ الْخَيْرِ لَشَدِيدٌ", "Wa innahuu lihubbil-khayri lashadiid", "And indeed he is, in love of wealth, intense.", "https://everyayah.com/data/Alafasy_128kbps/100008.mp3"),
                Ayah(100, 9, 6155, "أَفَلَا يَعْلَمُ إِذَا بُعْثِرَ مَا فِي الْقُبُورِ", "Afalaa ya'lamu izaa bu'sira maa fil-qubuur", "Does he not know that when the contents of the graves are scattered", "https://everyayah.com/data/Alafasy_128kbps/100009.mp3"),
                Ayah(100, 10, 6156, "وَحُصِّلَ مَا فِي الصُّدُورِ", "Wa hussila maa fis-suduur", "And that within the breasts is obtained,", "https://everyayah.com/data/Alafasy_128kbps/100010.mp3"),
                Ayah(100, 11, 6157, "إِنَّ رَبَّهُم بِهِمْ يَوْمَئِذٍ لَّخَبِيرٌ", "Inna Rabbahum bihim Yawma'izil-la-Khabiir", "Indeed, their Lord with them, that Day, is [fully] Acquainted.", "https://everyayah.com/data/Alafasy_128kbps/100011.mp3")
            )
            101 -> listOf(
                Ayah(101, 1, 6158, "الْقَارِعَةُ", "Al-qaari'ah", "The Striking Calamity -", "https://everyayah.com/data/Alafasy_128kbps/101001.mp3"),
                Ayah(101, 2, 6159, "مَا الْقَارِعَةُ", "Mal-qaari'ah", "What is the Striking Calamity?", "https://everyayah.com/data/Alafasy_128kbps/101002.mp3"),
                Ayah(101, 3, 6160, "وَمَا أَدْرَاكَ مَا الْقَارِعَةُ", "Wa maaa adraaka mal-qaari'ah", "And what can make you know what is the Striking Calamity?", "https://everyayah.com/data/Alafasy_128kbps/101003.mp3"),
                Ayah(101, 4, 6161, "يَوْمَ يَكُونُ النَّاسُ كَالْفَرَاشِ الْمَبْثُوثِ", "Yawma yakuunun-naasu kal-faraashil-mabsuus", "It is the Day when people will be like moths dispersed,", "https://everyayah.com/data/Alafasy_128kbps/101004.mp3"),
                Ayah(101, 5, 6162, "وَتَكُونُ الْجِبَالُ كَالْعِهْنِ الْمَنفُوشِ", "Wa takuunul-jibaalu kal'ihnil-manfuush", "And the mountains will be like fluffed wool.", "https://everyayah.com/data/Alafasy_128kbps/101005.mp3"),
                Ayah(101, 6, 6163, "فَأَمَّا مَن ثَقُلَتْ مَوَازِينُهُ", "Fa-ammaa man saqulat mawaaziinuh", "Then as for one whose scales are heavy [with good deeds],", "https://everyayah.com/data/Alafasy_128kbps/101006.mp3"),
                Ayah(101, 7, 6164, "فَهُوَ فِي عِيشَةٍ رَّاضِيَةٍ", "Fahuwa fii 'iishatir raadiyah", "He will be in a pleasant life.", "https://everyayah.com/data/Alafasy_128kbps/101007.mp3"),
                Ayah(101, 8, 6165, "وَأَمَّا مَنْ خَفَّتْ مَوَازِينُهُ", "Wa ammaa man khaffat mawaaziinuh", "But as for one whose scales are light,", "https://everyayah.com/data/Alafasy_128kbps/101008.mp3"),
                Ayah(101, 9, 6166, "فَأُمُّهُ هَاوِيَةٌ", "Fa-ummuhuu haawiyah", "His refuge will be an abyss.", "https://everyayah.com/data/Alafasy_128kbps/101009.mp3"),
                Ayah(101, 10, 6167, "وَمَا أَدْرَاكَ مَا هِيَهْ", "Wa maaa adraaka maa hiyah", "And what can make you know what that is?", "https://everyayah.com/data/Alafasy_128kbps/101010.mp3"),
                Ayah(101, 11, 6168, "نَارٌ حَامِيَةٌ", "Naarun haamiyah", "It is a Fire, intensely hot.", "https://everyayah.com/data/Alafasy_128kbps/101011.mp3")
            )
            102 -> listOf(
                Ayah(102, 1, 6169, "أَلْهَاكُمُ التَّكَاثُرُ", "Alhaakumut-takaasur", "Competition in [worldly] increase diverts you", "https://everyayah.com/data/Alafasy_128kbps/102001.mp3"),
                Ayah(102, 2, 6170, "حَتَّىٰ زُرْتُمُ الْمَقَابِرَ", "Hattaa zurtumul-maqaabir", "Until you visit the graveyards.", "https://everyayah.com/data/Alafasy_128kbps/102002.mp3"),
                Ayah(102, 3, 6171, "كَلَّا سَوْفَ تَعْلَمُونَ", "Kallaa sawfa ta'lamuun", "No! You are going to know.", "https://everyayah.com/data/Alafasy_128kbps/102003.mp3"),
                Ayah(102, 4, 6172, "ثُمَّ كَلَّا سَوْفَ تَعْلَمُونَ", "Summa kallaa sawfa ta'lamuun", "Then no! You are going to know.", "https://everyayah.com/data/Alafasy_128kbps/102004.mp3"),
                Ayah(102, 5, 6173, "كَلَّا لَوْ تَعْلَمُونَ عِلْمَ الْيَقِينِ", "Kallaa law ta'lamuuna 'ilmal-yaqiin", "No! If you only knew with knowledge of certainty...", "https://everyayah.com/data/Alafasy_128kbps/102005.mp3"),
                Ayah(102, 6, 6174, "لَتَرَوُنَّ الْجَحِيمَ", "Latarawunnal-Jahiim", "You will surely see the Hellfire.", "https://everyayah.com/data/Alafasy_128kbps/102006.mp3"),
                Ayah(102, 7, 6175, "ثُمَّ لَتَرَوُنَّهَا عَيْنَ الْيَقِينِ", "Summa latarawunnahaa 'aynal-yaqiin", "Then you will surely see it with the eye of certainty.", "https://everyayah.com/data/Alafasy_128kbps/102007.mp3"),
                Ayah(102, 8, 6176, "ثُمَّ لَتُسْأَلُنَّ يَوْمَئِذٍ عَنِ النَّعِيمِ", "Summa latus'alunna Yawma'izin 'anin-na'iim", "Then you will surely be asked that Day about pleasure.", "https://everyayah.com/data/Alafasy_128kbps/102008.mp3")
            )
            103 -> listOf(
                Ayah(103, 1, 6177, "وَالْعَصْرِ", "Wal-'asr", "By time,", "https://everyayah.com/data/Alafasy_128kbps/103001.mp3"),
                Ayah(103, 2, 6178, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Innal-insaana lafii khusr", "Indeed, mankind is in loss,", "https://everyayah.com/data/Alafasy_128kbps/103002.mp3"),
                Ayah(103, 3, 6179, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Illal-laziina aamanuu wa 'amilus-saalihaati wa tawaasaw bil-haqqi wa tawaasaw bis-sabr", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.", "https://everyayah.com/data/Alafasy_128kbps/103003.mp3")
            )
            104 -> listOf(
                Ayah(104, 1, 6180, "وَيْلٌ لِّكُلِّ هُمَزَةٍ لُّمَزَةٍ", "Waylul-likulli humazatil-lumazah", "Woe to every scorner and mocker", "https://everyayah.com/data/Alafasy_128kbps/104001.mp3"),
                Ayah(104, 2, 6181, "الَّذِي جَمَعَ مَالًا وَعَدَّدَهُ", "Allazii jama'a maalanw-wa 'addadah", "Who collects wealth and [continuously] counts it.", "https://everyayah.com/data/Alafasy_128kbps/104002.mp3"),
                Ayah(104, 3, 6182, "يَحْسَبُ أَنَّ مَالَهُ أَخْلَدَهُ", "Yahsabu anna maalahuuu akhladah", "He thinks that his wealth will make him immortal.", "https://everyayah.com/data/Alafasy_128kbps/104003.mp3"),
                Ayah(104, 4, 6183, "كَلَّا ۖ لَيُنبَذَنَّ فِي الْحُطَمَةِ", "Kallaa; layumbazanna fil-Hutamah", "No! He will surely be thrown into the Crusher.", "https://everyayah.com/data/Alafasy_128kbps/104004.mp3"),
                Ayah(104, 5, 6184, "وَمَا أَدْرَاكَ مَا الْحُطَمَةُ", "Wa maaa adraaka mal-Hutamah", "And what can make you know what is the Crusher?", "https://everyayah.com/data/Alafasy_128kbps/104005.mp3"),
                Ayah(104, 6, 6185, "نَارُ اللَّهِ الْمُوقَدَةُ", "Naarul-laahil-muuqadah", "It is the fire of Allah, [eternally] fueled,", "https://everyayah.com/data/Alafasy_128kbps/104006.mp3"),
                Ayah(104, 7, 6186, "الَّتِي تَطَّلِعُ عَلَى الْأَفْئِدَةِ", "Allatii tattali'u 'alal-af'idah", "Which mounts directed at the hearts.", "https://everyayah.com/data/Alafasy_128kbps/104007.mp3"),
                Ayah(104, 8, 6187, "إِنَّهَا عَلَيْهِم مُّؤْصَدَةٌ", "Innahaa 'alayhim mu'sadah", "Indeed, it will be closed down upon them", "https://everyayah.com/data/Alafasy_128kbps/104008.mp3"),
                Ayah(104, 9, 6188, "فِي عَمَدٍ مُّمَدَّدَةٍ", "Fii 'amadim-mumaddadah", "In extended columns.", "https://everyayah.com/data/Alafasy_128kbps/104009.mp3")
            )
            105 -> listOf(
                Ayah(105, 1, 6189, "أَلَمْ تَرَ كَيْفَ فَعَلَ رَبُّكَ بِأَصْحَابِ الْفِيلِ", "Alam tara kayfa fa'ala Rabbuka bi As-haabil-Fiil", "Have you not considered, [O Muhammad], how your Lord dealt with the companions of the elephant?", "https://everyayah.com/data/Alafasy_128kbps/105001.mp3"),
                Ayah(105, 2, 6190, "أَلَمْ يَجْعَلْ كَيْدَهُمْ فِي تَضْلِيلٍ", "Alam yaj'al kaydahum fii tadliil", "Did He not make their plan into misguidance?", "https://everyayah.com/data/Alafasy_128kbps/105002.mp3"),
                Ayah(105, 3, 6191, "وَأَرْسَلَ عَلَيْهِمْ طَيْرًا أَبَابِيلَ", "Wa arsala 'alayhim tayran abaabiil", "And He sent against them birds in flocks,", "https://everyayah.com/data/Alafasy_128kbps/105003.mp3"),
                Ayah(105, 4, 6192, "تَرْمِيهِم بِحِجَارَةٍ مِّن سِجِّيلٍ", "Tarmiihim bihijaaratim-min sijjiil", "Striking them with stones of hard clay,", "https://everyayah.com/data/Alafasy_128kbps/105004.mp3"),
                Ayah(105, 5, 6193, "فَجَعَلَهُمْ كَعَصْفٍ مَّأْكُولٍ", "Faja'alahum ka'asfim-ma'kuul", "And He made them like eaten straw.", "https://everyayah.com/data/Alafasy_128kbps/105005.mp3")
            )
            106 -> listOf(
                Ayah(106, 1, 6194, "لِإِيلَافِ قُرَيْشٍ", "Li-iilaafi Quraysh", "For the accustomed security of the Quraysh -", "https://everyayah.com/data/Alafasy_128kbps/106001.mp3"),
                Ayah(106, 2, 6195, "إِيلَافِهِمْ رِحْلَةَ الشِّتَاءِ وَالصَّيْفِ", "Iilaafihim rihlatash-shitaaa'i was-sayf", "Their accustomed security [in] the caravan of winter and summer -", "https://everyayah.com/data/Alafasy_128kbps/106002.mp3"),
                Ayah(106, 3, 6196, "فَلْيَعْبُدُوا رَبَّ هَٰذَا الْبَيْتِ", "Falya'buduu Rabba haazal-Bayt", "Let them worship the Lord of this House,", "https://everyayah.com/data/Alafasy_128kbps/106003.mp3"),
                Ayah(106, 4, 6197, "الَّذِي أَطْعَمَهُم مِّن جُوعٍ وَآمَنَهُم مِّنْ خَوْفٍ", "Allaziii at'amahum min juu'inw-wa aamanahum min khawf", "Who has fed them, [saving them] from hunger and made them safe, [saving them] from fear.", "https://everyayah.com/data/Alafasy_128kbps/106004.mp3")
            )
            107 -> listOf(
                Ayah(107, 1, 6198, "أَرَأَيْتَ الَّذِي يُكَذِّبُ بِالدِّينِ", "Ara'aytal-lazii yukazzibu bid-diin", "Have you seen the one who denies the Recompense?", "https://everyayah.com/data/Alafasy_128kbps/107001.mp3"),
                Ayah(107, 2, 6199, "فَذَٰلِكَ الَّذِي يَدُعُّ الْيَتِيمَ", "Fazalikal-lazii yadu''ul-yatiim", "For that is the one who drives away the orphan", "https://everyayah.com/data/Alafasy_128kbps/107002.mp3"),
                Ayah(107, 3, 6200, "وَلَا يَحُضُّ عَلَىٰ طَعَامِ الْمِسْكِينِ", "Wa laa yahuddu 'alaa ta'aamil-miskiin", "And does not encourage the feeding of the poor.", "https://everyayah.com/data/Alafasy_128kbps/107003.mp3"),
                Ayah(107, 4, 6201, "فَوَيْلٌ لِّلْمُصَلِّينَ", "Fawaylul-lil-musalliin", "So woe to those who pray", "https://everyayah.com/data/Alafasy_128kbps/107004.mp3"),
                Ayah(107, 5, 6202, "الَّذِينَ هُمْ عَن صَلَاتِهِمْ سَاهُونَ", "Allaziina hum 'an salaatihim saahuun", "[But] who are heedless of their prayer -", "https://everyayah.com/data/Alafasy_128kbps/107005.mp3"),
                Ayah(107, 6, 6203, "الَّذِينَ هُمْ يُرَاءُونَ", "Allaziina hum yuraaa'uun", "Those who make a show [of their deeds]", "https://everyayah.com/data/Alafasy_128kbps/107006.mp3"),
                Ayah(107, 7, 6204, "وَيَمْنَعُونَ الْمَاعُونَ", "Wa yamna'uunal-maa'uun", "And withhold [simple] assistance.", "https://everyayah.com/data/Alafasy_128kbps/107007.mp3")
            )
            108 -> listOf(
                Ayah(108, 1, 6205, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Innaaa a'taynaakal-Kawthar", "Indeed, We have granted you, [O Muhammad], al-Kawthar.", "https://everyayah.com/data/Alafasy_128kbps/108001.mp3"),
                Ayah(108, 2, 6206, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Fasalli li-Rabbika wanhar", "So pray to your Lord and sacrifice [to Him alone].", "https://everyayah.com/data/Alafasy_128kbps/108002.mp3"),
                Ayah(108, 3, 6207, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Inna shaani'aka huwal-abtar", "Indeed, your enemy is the one cut off.", "https://everyayah.com/data/Alafasy_128kbps/108003.mp3")
            )
            109 -> listOf(
                Ayah(109, 1, 6208, "قُلْ يَا أَيُّهَا الْكَافِرُونَ", "Qul yaaa-ayyuhal-kaafiruun", "Say, 'O disbelievers,", "https://everyayah.com/data/Alafasy_128kbps/109001.mp3"),
                Ayah(109, 2, 6209, "لَا أَعْبُدُ مَا تَعْبُدُونَ", "Laaa a'budu maa ta'buduun", "I do not worship what you worship.", "https://everyayah.com/data/Alafasy_128kbps/109002.mp3"),
                Ayah(109, 3, 6210, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "Wa laaa antum 'aabiduuna maaa a'bud", "Nor are you worshippers of what I worship.", "https://everyayah.com/data/Alafasy_128kbps/109003.mp3"),
                Ayah(109, 4, 6211, "وَلَا أَنَا عَابِدٌ مَّا عَبَدتُّمْ", "Wa laaa ana 'aabidum-maa 'abattum", "Nor will I be a worshipper of what you worship.", "https://everyayah.com/data/Alafasy_128kbps/109004.mp3"),
                Ayah(109, 5, 6212, "وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ", "Wa laaa antum 'aabiduuna maaa a'bud", "Nor will you be worshippers of what I worship.", "https://everyayah.com/data/Alafasy_128kbps/109005.mp3"),
                Ayah(109, 6, 6213, "لَكُمْ دِينُكُمْ وَلِيَ دِينِ", "Lakum diinukum wa liya diin", "For you is your religion, and for me is my religion.'", "https://everyayah.com/data/Alafasy_128kbps/109006.mp3")
            )
            110 -> listOf(
                Ayah(110, 1, 6214, "إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ", "Izaa jaaa'a nasrul-laahi wal-fath", "When the victory of Allah has come and the conquest,", "https://everyayah.com/data/Alafasy_128kbps/110001.mp3"),
                Ayah(110, 2, 6215, "وَرَأَيْتَ النَّاسَ يَدْخُلُونَ فِي دِينِ اللَّهِ أَفْوَاجًا", "Wa ra'aytan-naasa yadkhuluuna fii diinil-laahi afwaajaa", "And you see the people entering into the religion of Allah in multitudes,", "https://everyayah.com/data/Alafasy_128kbps/110002.mp3"),
                Ayah(110, 3, 6216, "فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ ۚ إِنَّهُ كَانَ تَوَّابًا", "Fasabbih bihamdi Rabbika wastaghfirh; innahuu kaana Tawwaabaa", "Then exalt [Him] with praise of your Lord and ask forgiveness of Him. Indeed, He is ever Accepting of repentance.", "https://everyayah.com/data/Alafasy_128kbps/110003.mp3")
            )
            111 -> listOf(
                Ayah(111, 1, 6217, "تَبَّتْ يَدَا أَبِي لَهَبٍ وَتَبَّ", "Tabbat yadaaa Abii Lahabinw-wa tabb", "May the hands of Abu Lahab be ruined, and ruined is he.", "https://everyayah.com/data/Alafasy_128kbps/111001.mp3"),
                Ayah(111, 2, 6218, "مَا أَغْنَىٰ عَنْهُ مَالُهُ وَمَا كَسَبَ", "Maaa aghnaa 'anhu maaluhuu wa maa kasab", "His wealth will not avail him or that which he gained.", "https://everyayah.com/data/Alafasy_128kbps/111002.mp3"),
                Ayah(111, 3, 6219, "سَيَصْلَىٰ نَارًا ذَاتَ لَهَبٍ", "Sayaslaa naaran zaata lahab", "He will [enter to] burn in a Fire of [blazing] flame", "https://everyayah.com/data/Alafasy_128kbps/111003.mp3"),
                Ayah(111, 4, 6220, "وَامْرَأَتُهُ حَمَّالَةَ الْحَطَبِ", "Wamra-atuhuu hammaalatal-hatab", "And his wife [as well] - the carrier of firewood.", "https://everyayah.com/data/Alafasy_128kbps/111004.mp3"),
                Ayah(111, 5, 6221, "فِي جِيدِهَا حَبْلٌ مِّن مَّسَدٍ", "Fii jiidihaa hablum-mim-masad", "Around her neck is a rope of [twisted] fiber.", "https://everyayah.com/data/Alafasy_128kbps/111005.mp3")
            )
            112 -> listOf(
                Ayah(112, 1, 6222, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul Huwal-laahu Ahad", "Say, 'He is Allah, [who is] One,", "https://everyayah.com/data/Alafasy_128kbps/112001.mp3"),
                Ayah(112, 2, 6223, "اللَّهُ الصَّمَدُ", "Allaahus-Samad", "Allah, the Eternal Refuge.", "https://everyayah.com/data/Alafasy_128kbps/112002.mp3"),
                Ayah(112, 3, 6224, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yuulad", "He neither begets nor is born,", "https://everyayah.com/data/Alafasy_128kbps/112003.mp3"),
                Ayah(112, 4, 6225, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul-lahuu kufuwan ahad", "Nor is there to Him any equivalent.'", "https://everyayah.com/data/Alafasy_128kbps/112004.mp3")
            )
            113 -> listOf(
                Ayah(113, 1, 6226, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'uuzu bi Rabbil-falaq", "Say, 'I seek refuge in the Lord of daybreak,", "https://everyayah.com/data/Alafasy_128kbps/113001.mp3"),
                Ayah(113, 2, 6227, "مِن شَرِّ مَا خَلَقَ", "Min sharri maa khalaq", "From the evil of that which He created,", "https://everyayah.com/data/Alafasy_128kbps/113002.mp3"),
                Ayah(113, 3, 6228, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min sharri ghaasiqin izaa waqab", "And from the evil of darkness when it settles,", "https://everyayah.com/data/Alafasy_128kbps/113003.mp3"),
                Ayah(113, 4, 6229, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min sharrin-naffaasaati fil 'uqad", "And from the evil of the blowers in knots,", "https://everyayah.com/data/Alafasy_128kbps/113004.mp3"),
                Ayah(113, 5, 6230, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min sharri haasidin izaa hasad", "And from the evil of an envier when he envies.'", "https://everyayah.com/data/Alafasy_128kbps/113005.mp3")
            )
            114 -> listOf(
                Ayah(114, 1, 6231, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'uuzu bi Rabbin-naas", "Say, 'I seek refuge in the Lord of mankind,", "https://everyayah.com/data/Alafasy_128kbps/114001.mp3"),
                Ayah(114, 2, 6232, "مَلِكِ النَّاسِ", "Malikin-naas", "The Sovereign of mankind,", "https://everyayah.com/data/Alafasy_128kbps/114002.mp3"),
                Ayah(114, 3, 6233, "إِلَٰهِ النَّاسِ", "Ilaahin-naas", "The God of mankind,", "https://everyayah.com/data/Alafasy_128kbps/114003.mp3"),
                Ayah(114, 4, 6234, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min sharril-waswaasil-khannaas", "From the evil of the retreating whisperer -", "https://everyayah.com/data/Alafasy_128kbps/114004.mp3"),
                Ayah(114, 5, 6235, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Allazii yuwaswisu fii suduurin-naas", "Who whispers into the breasts of mankind -", "https://everyayah.com/data/Alafasy_128kbps/114005.mp3"),
                Ayah(114, 6, 6236, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-naas", "From among the jinn and mankind.'", "https://everyayah.com/data/Alafasy_128kbps/114006.mp3")
            )
            else -> null
        }
    }
}
