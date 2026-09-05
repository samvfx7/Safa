package com.example.data.repository

import com.example.data.local.entity.HadithEntity

object HadithData {

    val allHadiths: List<HadithEntity> = listOf(
        // ==========================================
        // 40 HADITH NAWAWI (1 to 42)
        // ==========================================
        HadithEntity(
            id = "nawawi_1",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 1,
            narrator = "Umar ibn Al-Khattab (may Allah be pleased with him)",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى اللَّهِ وَرَسُولِهِ فَهِجْرَتُهُ إِلَى اللَّهِ وَرَسُولِهِ، وَمَنْ كَانَتْ هِجْرَتُهُ لِدُنْيَا يُصِيبُهَا أَوِ امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ",
            translation = "Actions are judged by motives and intentions, and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Actions & Intentions"
        ),
        HadithEntity(
            id = "nawawi_2",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 2,
            narrator = "Umar ibn Al-Khattab (may Allah be pleased with him)",
            arabicText = "بَيْنَمَا نَحْنُ جُلُوسٌ عِنْدَ رَسُولِ اللَّهِ إِذْ طَلَعَ عَلَيْنَا رَجُلٌ شَدِيدُ بَيَاضِ الثِّيَابِ شَدِيدُ سَوَادِ الشَّعَرِ... قَالَ: يَا مُحَمَّدُ أَخْبِرْنِي عَنِ الإِسْلاَمِ... قَالَ: أَنْ تَشْهَدَ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاَةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ... قَالَ: فَأَخْبِرْنِي عَنِ الإِيمَانِ... قَالَ: أَنْ تُؤْمِنَ بِاللَّهِ، وَمَلاَئِكَتِهِ، وَكُتُبِهِ، وَرُسُلِهِ، وَالْيَوْمِ الآخِرِ، وَتُؤْمِنَ بِالْقَدَرِ خَيْرِهِ وَشَرِّهِ... قَالَ: فَأَخْبِرْنِي عَنِ الإِحْسَانِ... قَالَ: أَنْ تَعْبُدَ اللَّهَ كَأَنَّكَ تَرَاهُ فَإِنْ لَمْ تَكُنْ تَرَاهُ فَإِنَّهُ يَرَاكَ",
            translation = "While we were sitting with the Messenger of Allah, there appeared before us a man whose clothes were exceedingly white and whose hair was exceedingly black... He said: 'O Muhammad, tell me about Islam.' The Messenger said: 'Islam is to testify that there is no deity worthy of worship except Allah and that Muhammad is His Messenger, to establish prayer, to give zakat, to fast Ramadan, and to perform pilgrimage to the House if you are able.' He asked: 'Tell me about Iman.' The Prophet replied: 'It is to believe in Allah, His angels, His books, His messengers, the Last Day, and to believe in divine destiny, its good and its evil.' He asked: 'Tell me about Ihsan.' The Prophet replied: 'It is to worship Allah as if you see Him, for if you do not see Him, He surely sees you.' (Hadith Jibril)",
            authenticity = "Sahih Muslim",
            chapter = "Islam, Iman, and Ihsan"
        ),
        HadithEntity(
            id = "nawawi_3",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 3,
            narrator = "Abdullah ibn Umar (may Allah be pleased with them both)",
            arabicText = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَحَجِّ الْبَيْتِ، وَصَوْمِ رَمَضَانَ",
            translation = "Islam is built upon five pillars: testifying that there is no deity worthy of worship except Allah and that Muhammad is the Messenger of Allah, establishing the prayer, paying the zakat, pilgrimage to the House, and fasting during Ramadan.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "The Five Pillars of Islam"
        ),
        HadithEntity(
            id = "nawawi_4",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 4,
            narrator = "Abdullah ibn Mas'ud (may Allah be pleased with him)",
            arabicText = "إِنَّ أَحَدَكُمْ يُجْمَعُ خَلْقُهُ فِي بَطْنِ أُمِّهِ أَرْبَعِينَ يَوْمًا نُطْفَةً، ثُمَّ يَكُونُ عَلَقَةً مِثْلَ ذَلِكَ، ثُمَّ يَكُونُ مُضْغَةً مِثْلَ ذَلِكَ، ثُمَّ يُرْسَلُ إِلَيْهِ الْمَلَكُ فَيَنْفُخُ فِيهِ الرُّوحَ، وَيُؤْمَرُ بِأَرْبَعِ كَلِمَاتٍ: بِكَتْبِ رِزْقِهِ، وَأَجَلِهِ، وَعَمَلِهِ، وَشَقِيٌّ أَوْ سَعِيدٌ",
            translation = "Each one of you is constituted in the womb of his mother for forty days, and then he becomes a clot of thick blood for a similar period, and then a piece of flesh for a similar period. Then an angel is sent who breathes the soul into him and is commanded with four decrees: his provision, his lifespan, his deeds, and whether he will be wretched or blessed.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Creation of Man & Divine Decree"
        ),
        HadithEntity(
            id = "nawawi_5",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 5,
            narrator = "Aisha (may Allah be pleased with her)",
            arabicText = "مَنْ أَحْدَثَ فِي أَمْرِنَا هَذَا مَا لَيْسَ مِنْهُ فَهُوَ رَدٌّ",
            translation = "Whoever introduces into this matter of ours that which is not part of it, it will be rejected.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Preserving the Religion"
        ),
        HadithEntity(
            id = "nawawi_6",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 6,
            narrator = "An-Nu'man ibn Bashir (may Allah be pleased with him)",
            arabicText = "إِنَّ الْحَلاَلَ بَيِّنٌ وَإِنَّ الْحَرَامَ بَيِّنٌ، وَبَيْنَهُمَا أُمُورٌ مُشْتَبِهَاتٌ لاَ يَعْلَمُهُنَّ كَثِيرٌ مِنَ النَّاسِ... أَلاَ وَإِنَّ فِي الْجَسَدِ مُضْغَةً إِذَا صَلَحَتْ صَلَحَ الْجَسَدُ كُلُّهُ، وَإِذَا فَسَدَتْ فَسَدَ الْجَسَدُ كُلُّهُ، أَلاَ وَهِيَ الْقَلْبُ",
            translation = "That which is lawful is clear and that which is unlawful is clear, and between the two of them are doubtful matters about which many people do not know. Whoever avoids doubtful matters clears himself in regard to his religion and his honor... Truly, in the body there is a morsel of flesh; if it is sound, the whole body is sound, and if it is corrupted, the whole body is corrupted. Truly, it is the heart.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Doubtful Matters & Purification of Heart"
        ),
        HadithEntity(
            id = "nawawi_7",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 7,
            narrator = "Tamim ad-Dari (may Allah be pleased with him)",
            arabicText = "الدِّينُ النَّصِيحَةُ. قُلْنَا: لِمَنْ؟ قَالَ: لِلَّهِ وَلِكِتَابِهِ وَلِرَسُولِهِ وَلأَئِمَّةِ الْمُسْلِمِينَ وَعَامَّتِهِمْ",
            translation = "The religion is sincerity and sincere advice. We said: 'To whom?' He said: 'To Allah, His Book, His Messenger, and to the leaders of the Muslims and their common folk.'",
            authenticity = "Sahih Muslim",
            chapter = "The Essence of Religion"
        ),
        HadithEntity(
            id = "nawawi_8",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 8,
            narrator = "Abdullah ibn Umar (may Allah be pleased with them both)",
            arabicText = "أُمِرْتُ أَنْ أُقَاتِلَ النَّاسَ حَتَّى يَشْهَدُوا أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَيُقِيمُوا الصَّلاَةَ، وَيُؤْتُوا الزَّكَاةَ، فَإِذَا فَعَلُوا ذَلِكَ عَصَمُوا مِنِّي دِمَاءَهُمْ وَأَمْوَالَهُمْ إِلاَّ بِحَقِّ الإِسْلاَمِ، وَحِسَابُهُمْ عَلَى اللَّهِ",
            translation = "I have been commanded to call people until they testify that there is no deity worthy of worship except Allah and that Muhammad is the Messenger of Allah, and they establish prayer and pay zakat. If they do so, their blood and wealth are sacred from me except by the right of Islam, and their account is with Allah.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "The Sanctity of a Muslim"
        ),
        HadithEntity(
            id = "nawawi_9",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 9,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "مَا نَهَيْتُكُمْ عَنْهُ فَاجْتَنِبُوهُ، وَمَا أَمَرْتُكُمْ بِهِ فَأْتُوا مِنْهُ مَا اسْتَطَعْتُمْ، فَإِنَّمَا أَهْلَكَ الَّذِينَ مِنْ قَبْلِكُمْ كَثْرَةُ مَسَائِلِهِمْ وَاخْتِلاَفُهُمْ عَلَى أَنْبِيَائِهِمْ",
            translation = "What I have forbidden you, avoid; and what I have ordered you to do, do as much of it as you can. For what destroyed those before you was excessive questioning and disputing with their prophets.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Obedience & Avoiding Excessive Questions"
        ),
        HadithEntity(
            id = "nawawi_10",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 10,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "إِنَّ اللَّهَ تَعَالَى طَيِّبٌ لاَ يَقْبَلُ إِلاَّ طَيِّبًا... ثُمَّ ذَكَرَ الرَّجُلَ يُطِيلُ السَّفَرَ أَشْعَثَ أَغْبَرَ يَمُدُّ يَدَيْهِ إِلَى السَّمَاءِ: يَا رَبِّ يَا رَبِّ، وَمَطْعَمُهُ حَرَامٌ، وَمَشْرَبُهُ حَرَامٌ، وَمَلْبَسُهُ حَرَامٌ، وَغُذِيَ بِالْحَرَامِ، فَأَنَّى يُسْتَجَابُ لِذَلِكَ",
            translation = "Allah the Almighty is Pure and accepts only that which is pure... Then the Prophet mentioned a traveler on a long journey, disheveled and dusty, who stretches out his hands to the sky saying, 'O Lord, O Lord!' while his food is unlawful, his drink is unlawful, his clothing is unlawful, and he is nourished by the unlawful; so how can his supplication be answered?",
            authenticity = "Sahih Muslim",
            chapter = "Pure Earnings & Acceptance of Supplication"
        ),
        HadithEntity(
            id = "nawawi_11",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 11,
            narrator = "Al-Hasan ibn Ali (may Allah be pleased with them both)",
            arabicText = "دَعْ مَا يَرِيبُكَ إِلَى مَا لاَ يَرِيبُكَ",
            translation = "Leave that which causes you doubt for that which does not cause you doubt.",
            authenticity = "Sunan at-Tirmidhi & an-Nasa'i (Sahih)",
            chapter = "Leaving Doubtful Matters"
        ),
        HadithEntity(
            id = "nawawi_12",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 12,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "مِنْ حُسْنِ إِسْلاَمِ الْمَرْءِ تَرْكُهُ مَا لاَ يَعْنِيهِ",
            translation = "Part of the perfection of a person's Islam is his leaving that which does not concern him.",
            authenticity = "Sunan at-Tirmidhi (Hasan)",
            chapter = "Mindfulness & Focusing on What Matters"
        ),
        HadithEntity(
            id = "nawawi_13",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 13,
            narrator = "Anas ibn Malik (may Allah be pleased with him)",
            arabicText = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            translation = "None of you truly believes until he loves for his brother what he loves for himself.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "True Brotherhood in Faith"
        ),
        HadithEntity(
            id = "nawawi_14",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 14,
            narrator = "Abdullah ibn Mas'ud (may Allah be pleased with him)",
            arabicText = "لاَ يَحِلُّ دَمُ امْرِئٍ مُسْلِمٍ يَشْهَدُ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنِّي رَسُولُ اللَّهِ إِلاَّ بِإِحْدَى ثَلاَثٍ: الثَّيِّبُ الزَّانِي، وَالنَّفْسُ بِالنَّفْسِ، وَالتَّارِكُ لِدِينِهِ الْمُفَارِقُ لِلْجَمَاعَةِ",
            translation = "The blood of a Muslim who testifies that there is no deity worthy of worship except Allah and that I am the Messenger of Allah is not lawful to shed except in one of three cases: the married person who commits adultery, a life for a life, and the one who abandons his religion and splits away from the community.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "The Inviolability of Life"
        ),
        HadithEntity(
            id = "nawawi_15",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 15,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ جَارَهُ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ ضَيْفَهُ",
            translation = "Whoever believes in Allah and the Last Day, let him speak good or remain silent. Whoever believes in Allah and the Last Day, let him honor his neighbor. Whoever believes in Allah and the Last Day, let him honor his guest.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Good Speech, Neighbors, and Guests"
        ),
        HadithEntity(
            id = "nawawi_16",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 16,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "أَنَّ رَجُلاً قَالَ لِلنَّبِيِّ: أَوْصِنِي. قَالَ: لاَ تَغْضَبْ. فَرَدَّدَ مِرَارًا، قَالَ: لاَ تَغْضَبْ",
            translation = "A man said to the Prophet: 'Advise me.' The Prophet said: 'Do not become angry.' The man repeated his request several times, and each time the Prophet said: 'Do not become angry.'",
            authenticity = "Sahih Bukhari",
            chapter = "Controlling Anger"
        ),
        HadithEntity(
            id = "nawawi_17",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 17,
            narrator = "Shaddad ibn Aws (may Allah be pleased with him)",
            arabicText = "إِنَّ اللَّهَ كَتَبَ الإِحْسَانَ عَلَى كُلِّ شَىْءٍ، فَإِذَا قَتَلْتُمْ فَأَحْسِنُوا الْقِتْلَةَ، وَإِذَا ذَبَحْتُمْ فَأَحْسِنُوا الذِّبْحَةَ، وَلْيُحِدَّ أَحَدُكُمْ شَفْرَتَهُ، وَلْيُرِحْ ذَبِيحَتَهُ",
            translation = "Verily Allah has prescribed excellence and gentleness in all things. So when you execute, do so properly; and when you slaughter, do so humanely. Let each one of you sharpen his blade and give comfort to the animal being slaughtered.",
            authenticity = "Sahih Muslim",
            chapter = "Excellence and Compassion in All Things"
        ),
        HadithEntity(
            id = "nawawi_18",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 18,
            narrator = "Abu Dharr and Mu'adh ibn Jabal (may Allah be pleased with them)",
            arabicText = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ",
            translation = "Fear Allah wherever you may be; follow up an evil deed with a good one which will wipe it out; and treat people with good character.",
            authenticity = "Sunan at-Tirmidhi (Hasan)",
            chapter = "Piety, Repentance, and Good Character"
        ),
        HadithEntity(
            id = "nawawi_19",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 19,
            narrator = "Abdullah ibn Abbas (may Allah be pleased with them both)",
            arabicText = "يَا غُلاَمُ إِنِّي أُعَلِّمُكَ كَلِمَاتٍ: احْفَظِ اللَّهَ يَحْفَظْكَ، احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ، إِذَا سَأَلْتَ فَاسْأَلِ اللَّهَ، وَإِذَا اسْتَعَنْتَ فَاسْتَعِنْ بِاللَّهِ، وَاعْلَمْ أَنَّ الأُمَّةَ لَوِ اجْتَمَعَتْ عَلَى أَنْ يَنْفَعُوكَ بِشَىْءٍ لَمْ يَنْفَعُوكَ إِلاَّ بِشَىْءٍ قَدْ كَتَبَهُ اللَّهُ لَكَ، وَلَوِ اجْتَمَعُوا عَلَى أَنْ يَضُرُّوكَ بِشَىْءٍ لَمْ يَضُرُّوكَ إِلاَّ بِشَىْءٍ قَدْ كَتَبَهُ اللَّهُ عَلَيْكَ، رُفِعَتِ الأَقْلاَمُ وَجَفَّتِ الصُّحُفُ",
            translation = "O young boy, I will teach you some words: Be mindful of Allah and He will protect you. Be mindful of Allah and you will find Him in front of you. If you ask, ask of Allah; and if you seek help, seek help from Allah. And know that if the whole nation were to gather together to benefit you with something, they could not benefit you except with that which Allah has already written for you. And if they gather to harm you, they could not harm you except with that which Allah has already decreed against you. The pens have been lifted and the pages have dried.",
            authenticity = "Sunan at-Tirmidhi (Hasan Sahih)",
            chapter = "Reliance on Allah and Divine Decree"
        ),
        HadithEntity(
            id = "nawawi_20",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 20,
            narrator = "Abu Mas'ud Uqbah ibn Amr (may Allah be pleased with him)",
            arabicText = "إِنَّ مِمَّا أَدْرَكَ النَّاسُ مِنْ كَلاَمِ النُّبُوَّةِ الأُولَى: إِذَا لَمْ تَسْتَحِ فَاصْنَعْ مَا شِئْتَ",
            translation = "Among that which the people understood from the early words of prophecy: If you feel no shame, then do whatever you wish.",
            authenticity = "Sahih Bukhari",
            chapter = "The Virtue of Modesty and Shame"
        ),
        HadithEntity(
            id = "nawawi_21",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 21,
            narrator = "Sufyan ibn Abdullah (may Allah be pleased with him)",
            arabicText = "قُلْتُ: يَا رَسُولَ اللَّهِ، قُلْ لِي فِي الإِسْلاَمِ قَوْلاً لاَ أَسْأَلُ عَنْهُ أَحَدًا غَيْرَكَ. قَالَ: قُلْ: آمَنْتُ بِاللَّهِ، ثُمَّ اسْتَقِمْ",
            translation = "I said: 'O Messenger of Allah, tell me something in Islam about which I will never have to ask anyone other than you.' The Prophet said: 'Say: I believe in Allah, and then remain steadfast upon that.'",
            authenticity = "Sahih Muslim",
            chapter = "Steadfastness in Faith"
        ),
        HadithEntity(
            id = "nawawi_22",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 22,
            narrator = "Jabir ibn Abdullah (may Allah be pleased with him)",
            arabicText = "أَرَأَيْتَ إِذَا صَلَّيْتُ الْمَكْتُوبَاتِ، وَصُمْتُ رَمَضَانَ، وَأَحْلَلْتُ الْحَلاَلَ، وَحَرَّمْتُ الْحَرَامَ، وَلَمْ أَزِدْ عَلَى ذَلِكَ شَيْئًا، أَأَدْخُلُ الْجَنَّةَ؟ قَالَ: نَعَمْ",
            translation = "A man asked the Messenger: 'Tell me, if I perform the obligatory prayers, fast Ramadan, treat the lawful as lawful and the unlawful as unlawful, and do nothing beyond that, will I enter Paradise?' The Prophet replied: 'Yes.'",
            authenticity = "Sahih Muslim",
            chapter = "Fulfilling Obligations Leads to Paradise"
        ),
        HadithEntity(
            id = "nawawi_23",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 23,
            narrator = "Abu Malik Al-Harith ibn Asim Al-Ash'ari (may Allah be pleased with him)",
            arabicText = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ - أَوْ تَمْلأُ - مَا بَيْنَ السَّمَاءِ وَالأَرْضِ، وَالصَّلاَةُ نُورٌ، وَالصَّدَقَةُ بُرْهَانٌ، وَالصَّبْرُ ضِيَاءٌ، وَالْقُرْآنُ حُجَّةٌ لَكَ أَوْ عَلَيْكَ، كُلُّ النَّاسِ يَغْدُو فَبَائِعٌ نَفْسَهُ فَمُعْتِقُهَا أَوْ مُوبِقُهَا",
            translation = "Purity is half of faith. 'Alhamdulillah' (Praise be to Allah) fills the scale. 'SubhanAllah' (Glory be to Allah) and 'Alhamdulillah' fill what is between heaven and earth. Prayer is light, charity is proof, patience is illumination, and the Qur'an is an argument for you or against you. Every person departs in the morning and sells himself, either liberating himself or destroying himself.",
            authenticity = "Sahih Muslim",
            chapter = "Purity, Prayer, and Remembrance"
        ),
        HadithEntity(
            id = "nawawi_24",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 24,
            narrator = "Abu Dharr Al-Ghifari (may Allah be pleased with him)",
            arabicText = "يَا عِبَادِي إِنِّي حَرَّمْتُ الظُّلْمَ عَلَى نَفْسِي وَجَعَلْتُهُ بَيْنَكُمْ مُحَرَّمًا فَلاَ تَظَالَمُوا، يَا عِبَادِي كُلُّكُمْ ضَالٌّ إِلاَّ مَنْ هَدَيْتُهُ فَاسْتَهْدُونِي أَهْدِكُمْ، يَا عِبَادِي كُلُّكُمْ جَائِعٌ إِلاَّ مَنْ أَطْعَمْتُهُ فَاسْتَطْعِمُونِي أُطْعِمْكُمْ...",
            translation = "Allah the Almighty said: 'O My servants, I have forbidden injustice for Myself and I have made it forbidden amongst you, so do not oppress one another. O My servants, all of you are astray except those whom I have guided, so seek guidance from Me and I will guide you. O My servants, all of you are hungry except those whom I have fed, so ask food of Me and I will feed you...' (Hadith Qudsi)",
            authenticity = "Sahih Muslim",
            chapter = "The Prohibition of Oppression"
        ),
        HadithEntity(
            id = "nawawi_25",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 25,
            narrator = "Abu Dharr (may Allah be pleased with him)",
            arabicText = "إِنَّ بِكُلِّ تَسْبِيحَةٍ صَدَقَةً، وَكُلِّ تَكْبِيرَةٍ صَدَقَةً، وَكُلِّ تَحْمِيدَةٍ صَدَقَةً، وَكُلِّ تَهْلِيلَةٍ صَدَقَةً، وَأَمْرٌ بِالْمَعْرُوفِ صَدَقَةٌ، وَنَهْىٌ عَنْ مُنْكَرٍ صَدَقَةٌ",
            translation = "In every glorification of Allah (SubhanAllah) is charity, in every declaration of His Greatness (Allahu Akbar) is charity, in every praise (Alhamdulillah) is charity, in every declaration of His Oneness (La ilaha illallah) is charity, enjoining good is charity, and forbidding evil is charity.",
            authenticity = "Sahih Muslim",
            chapter = "Charity in Every Good Deed"
        ),
        HadithEntity(
            id = "nawawi_26",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 26,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "كُلُّ سُلاَمَى مِنَ النَّاسِ عَلَيْهِ صَدَقَةٌ كُلَّ يَوْمٍ تَطْلُعُ فِيهِ الشَّمْسُ: تَعْدِلُ بَيْنَ الاِثْنَيْنِ صَدَقَةٌ، وَتُعِينُ الرَّجُلَ فِي دَابَّتِهِ فَتَحْمِلُهُ عَلَيْهَا أَوْ تَرْفَعُ لَهُ عَلَيْهَا مَتَاعَهُ صَدَقَةٌ، وَالْكَلِمَةُ الطَّيِّبَةُ صَدَقَةٌ، وَبِكُلِّ خَطْوَةٍ تَمْشِيهَا إِلَى الصَّلاَةِ صَدَقَةٌ، وَتُمِيطُ الأَذَى عَنِ الطَّرِيقِ صَدَقَةٌ",
            translation = "Every joint of a person must perform a charity each day that the sun rises: judging justly between two people is charity, helping a man with his mount by lifting him onto it or hoisting his goods upon it is charity, a good word is charity, every step taken to prayer is charity, and removing a harmful obstacle from the road is charity.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Daily Charity & Reconciliation"
        ),
        HadithEntity(
            id = "nawawi_27",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 27,
            narrator = "An-Nawwas ibn Sam'an (may Allah be pleased with him)",
            arabicText = "الْبِرُّ حُسْنُ الْخُلُقِ، وَالإِثْمُ مَا حَاكَ فِي صَدْرِكَ وَكَرِهْتَ أَنْ يَطَّلِعَ عَلَيْهِ النَّاسُ",
            translation = "Righteousness is good character, and sin is that which wavers within your chest and which you would dislike for people to discover.",
            authenticity = "Sahih Muslim",
            chapter = "Righteousness and Inner Conscience"
        ),
        HadithEntity(
            id = "nawawi_28",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 28,
            narrator = "Al-Irbad ibn Sariyah (may Allah be pleased with him)",
            arabicText = "وَعَظَنَا رَسُولُ اللَّهِ مَوْعِظَةً وَجِلَتْ مِنْهَا الْقُلُوبُ وَذَرَفَتْ مِنْهَا الْعُيُونُ... قَالَ: عَلَيْكُمْ بِسُنَّتِي وَسُنَّةِ الْخُلَفَاءِ الرَّاشِدِينَ الْمَهْدِيِّينَ، عَضُّوا عَلَيْهَا بِالنَّوَاجِذِ، وَإِيَّاكُمْ وَمُحْدَثَاتِ الأُمُورِ",
            translation = "The Messenger gave us a profound sermon that caused hearts to tremble and eyes to shed tears... He said: 'Hold fast to my Sunnah and the Sunnah of the rightly-guided caliphs; cling to it with your back teeth. And beware of newly invented matters in religion.'",
            authenticity = "Abu Dawud & at-Tirmidhi (Sahih)",
            chapter = "Adhering to the Prophetic Sunnah"
        ),
        HadithEntity(
            id = "nawawi_29",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 29,
            narrator = "Mu'adh ibn Jabal (may Allah be pleased with him)",
            arabicText = "قُلْتُ: يَا رَسُولَ اللَّهِ، أَخْبِرْنِي بِعَمَلٍ يُدْخِلُنِي الْجَنَّةَ وَيُبَاعِدُنِي عَنِ النَّارِ... فَأَخَذَ بِلِسَانِهِ وَقَالَ: كُفَّ عَلَيْكَ هَذَا. قُلْتُ: وَإِنَّا لَمُؤَاخَذُونَ بِمَا نَتَكَلَّمُ بِهِ؟ فَقَالَ: ثَكِلَتْكَ أُمُّكَ يَا مُعَاذُ، وَهَلْ يَكُبُّ النَّاسَ فِي النَّارِ عَلَى وُجُوهِهِمْ إِلاَّ حَصَائِدُ أَلْسِنَتِهِمْ",
            translation = "I said: 'O Messenger of Allah, inform me of an action that will admit me to Paradise and distance me from the Fire...' The Prophet took hold of his tongue and said: 'Restrain this.' I said: 'Will we be held accountable for what we utter?' He said: 'May your mother be bereaved of you, O Mu'adh! Does anything topple people on their faces in the Fire except the harvests of their tongues?'",
            authenticity = "Sunan at-Tirmidhi (Hasan Sahih)",
            chapter = "Guarding the Tongue"
        ),
        HadithEntity(
            id = "nawawi_30",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 30,
            narrator = "Abu Tha'labah Al-Khushani (may Allah be pleased with him)",
            arabicText = "إِنَّ اللَّهَ تَعَالَى فَرَضَ فَرَائِضَ فَلاَ تُضَيِّعُوهَا، وَحَدَّ حُدُودًا فَلاَ تَعْتَدُوهَا، وَحَرَّمَ أَشْيَاءَ فَلاَ تَنْتَهِكُوهَا، وَسَكَتَ عَنْ أَشْيَاءَ رَحْمَةً لَكُمْ غَيْرَ نِسْيَانٍ فَلاَ تَبْحَثُوا عَنْهَا",
            translation = "Allah the Exalted has prescribed obligatory duties, so do not neglect them; He has set boundaries, so do not transgress them; He has prohibited things, so do not violate them; and He remained silent about things out of mercy for you, not out of forgetfulness, so do not search deeply into them.",
            authenticity = "Sunan ad-Daraqutni (Hasan)",
            chapter = "The Divine Boundaries"
        ),
        HadithEntity(
            id = "nawawi_31",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 31,
            narrator = "Sahl ibn Sa'd As-Sa'idi (may Allah be pleased with him)",
            arabicText = "جَاءَ رَجُلٌ إِلَى النَّبِيِّ فَقَالَ: يَا رَسُولَ اللَّهِ، دُلَّنِي عَلَى عَمَلٍ إِذَا عَمِلْتُهُ أَحَبَّنِي اللَّهُ وَأَحَبَّنِي النَّاسُ. فَقَالَ: ازْهَدْ فِي الدُّنْيَا يُحِبَّكَ اللَّهُ، وَازْهَدْ فِيمَا فِي أَيْدِي النَّاسِ يُحِبَّكَ النَّاسُ",
            translation = "A man came to the Prophet and said: 'O Messenger of Allah, direct me to an act which, if I perform it, Allah will love me and people will love me.' The Prophet said: 'Renounce attachment to worldly desires and Allah will love you; and renounce that which people possess and people will love you.'",
            authenticity = "Sunan Ibn Majah (Hasan)",
            chapter = "Asceticism & Moderation"
        ),
        HadithEntity(
            id = "nawawi_32",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 32,
            narrator = "Abu Sa'id Al-Khudri (may Allah be pleased with him)",
            arabicText = "لاَ ضَرَرَ وَلاَ ضِرَارَ",
            translation = "There should be neither harming nor reciprocating harm.",
            authenticity = "Sunan Ibn Majah & ad-Daraqutni (Hasan)",
            chapter = "Causing No Harm"
        ),
        HadithEntity(
            id = "nawawi_33",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 33,
            narrator = "Abdullah ibn Abbas (may Allah be pleased with them both)",
            arabicText = "لَوْ يُعْطَى النَّاسُ بِدَعْوَاهُمْ لاَدَّعَى رِجَالٌ أَمْوَالَ قَوْمٍ وَدِمَاءَهُمْ، لَكِنِ الْبَيِّنَةُ عَلَى الْمُدَّعِي، وَالْيَمِينُ عَلَى مَنْ أَنْكَرَ",
            translation = "Were people given according to their claims, men would claim the wealth and lives of other people; but the burden of proof is upon the claimant, and the oath is upon the one who denies.",
            authenticity = "Al-Bayhaqi (Hasan Sahih)",
            chapter = "Justice and Legal Proof"
        ),
        HadithEntity(
            id = "nawawi_34",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 34,
            narrator = "Abu Sa'id Al-Khudri (may Allah be pleased with him)",
            arabicText = "مَنْ رَأَى مِنْكُمْ مُنْكَرًا فَلْيُغَيِّرْهُ بِيَدِهِ، فَإِنْ لَمْ يَسْتَطِعْ فَبِلِسَانِهِ، فَإِنْ لَمْ يَسْتَطِعْ فَبِقَلْبِهِ، وَذَلِكَ أَضْعَفُ الإِيمَانِ",
            translation = "Whoever of you sees an evil, let him change it with his hand; and if he is not able to do so, then with his tongue; and if he is not able to do so, then with his heart - and that is the weakest of faith.",
            authenticity = "Sahih Muslim",
            chapter = "Preventing Evil"
        ),
        HadithEntity(
            id = "nawawi_35",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 35,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "لاَ تَحَاسَدُوا، وَلاَ تَنَاجَشُوا، وَلاَ تَبَاغَضُوا، وَلاَ تَدَابَرُوا، وَلاَ يَبِعْ بَعْضُكُمْ عَلَى بَيْعِ بَعْضٍ، وَكُونُوا عِبَادَ اللَّهِ إِخْوَانًا... الْمُسْلِمُ أَخُو الْمُسْلِمِ لاَ يَظْلِمُهُ وَلاَ يَخْذُلُهُ وَلاَ يَحْقِرُهُ... التَّقْوَى هَاهُنَا - وَيُشِيرُ إِلَى صَدْرِهِ ثَلاَثَ مَرَّاتٍ",
            translation = "Do not envy one another, do not artificially inflate prices, do not hate one another, do not turn your backs on one another, and do not undercut one another in sales. Be, O servants of Allah, brothers. A Muslim is the brother of a Muslim: he does not oppress him, nor does he forsake him, nor does he look down upon him. Piety is right here - and the Prophet pointed to his chest three times.",
            authenticity = "Sahih Muslim",
            chapter = "Islamic Brotherhood and Compassion"
        ),
        HadithEntity(
            id = "nawawi_36",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 36,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "مَنْ نَفَّسَ عَنْ مُؤْمِنٍ كُرْبَةً مِنْ كُرَبِ الدُّنْيَا نَفَّسَ اللَّهُ عَنْهُ كُرْبَةً مِنْ كُرَبِ يَوْمِ الْقِيَامَةِ، وَمَنْ يَسَّرَ عَلَى مُعْسِرٍ يَسَّرَ اللَّهُ عَلَيْهِ فِي الدُّنْيَا وَالآخِرَةِ، وَمَنْ سَتَرَ مُسْلِمًا سَتَرَهُ اللَّهُ فِي الدُّنْيَا وَالآخِرَةِ، وَاللَّهُ فِي عَوْنِ الْعَبْدِ مَا كَانَ الْعَبْدُ فِي عَوْنِ أَخِيهِ",
            translation = "Whoever relieves a believer of a hardship in this world, Allah will relieve him of a hardship on the Day of Resurrection. Whoever eases the hardship of an indebted person, Allah will ease his hardship in this world and the Hereafter. Whoever covers the faults of a Muslim, Allah will cover his faults in this world and the Hereafter. Allah remains in aid of His servant so long as the servant remains in aid of his brother.",
            authenticity = "Sahih Muslim",
            chapter = "Relieving Distress and Helping Others"
        ),
        HadithEntity(
            id = "nawawi_37",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 37,
            narrator = "Abdullah ibn Abbas (may Allah be pleased with them both)",
            arabicText = "إِنَّ اللَّهَ كَتَبَ الْحَسَنَاتِ وَالسَّيِّئَاتِ ثُمَّ بَيَّنَ ذَلِكَ: فَمَنْ هَمَّ بِحَسَنَةٍ فَلَمْ يَعْمَلْهَا كَتَبَهَا اللَّهُ عِنْدَهُ حَسَنَةً كَامِلَةً، وَإِنْ هَمَّ بِهَا فَعَمِلَهَا كَتَبَهَا اللَّهُ عِنْدَهُ عَشْرَ حَسَنَاتٍ إِلَى سَبْعِمِائَةِ ضِعْفٍ إِلَى أَضْعَافٍ كَثِيرَةٍ",
            translation = "Allah has recorded good deeds and evil deeds, then clarified it: Whoever intends to perform a good deed and does not do it, Allah records it with Him as a complete good deed. If he intends it and does it, Allah records it with Him as ten good deeds up to seven hundred times, or even many times more.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "The Grace of Allah in Good Intentions"
        ),
        HadithEntity(
            id = "nawawi_38",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 38,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "مَنْ عَادَى لِي وَلِيًّا فَقَدْ آذَنْتُهُ بِالْحَرْبِ، وَمَا تَقَرَّبَ إِلَىَّ عَبْدِي بِشَىْءٍ أَحَبَّ إِلَىَّ مِمَّا افْتَرَضْتُ عَلَيْهِ، وَمَا يَزَالُ عَبْدِي يَتَقَرَّبُ إِلَىَّ بِالنَّوَافِلِ حَتَّى أُحِبَّهُ، فَإِذَا أَحْبَبْتُهُ كُنْتُ سَمْعَهُ الَّذِي يَسْمَعُ بِهِ، وَبَصَرَهُ الَّذِي يُبْصِرُ بِهِ، وَيَدَهُ الَّتِي يَبْطِشُ بِهَا، وَرِجْلَهُ الَّتِي يَمْشِي بِهَا، وَإِنْ سَأَلَنِي لأُعْطِيَنَّهُ، وَلَئِنِ اسْتَعَاذَنِي لأُعِيذَنَّهُ",
            translation = "Allah the Almighty said: 'Whoever shows enmity to a devoted friend of Mine, I declare war upon him. And My servant does not draw near to Me with anything more beloved to Me than the duties I have obligated upon him. And My servant continues to draw near to Me with voluntary good deeds until I love him. When I love him, I become his hearing with which he hears, his sight with which he sees, his hand with which he strikes, and his foot with which he walks. Were he to ask of Me, I would surely give it to him; and were he to seek refuge in Me, I would surely grant it to him.' (Hadith Qudsi)",
            authenticity = "Sahih Bukhari",
            chapter = "Nearness to Allah and Divine Love"
        ),
        HadithEntity(
            id = "nawawi_39",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 39,
            narrator = "Abdullah ibn Abbas (may Allah be pleased with them both)",
            arabicText = "إِنَّ اللَّهَ تَجَاوَزَ لِي عَنْ أُمَّتِي الْخَطَأَ وَالنِّسْيَانَ وَمَا اسْتُكْرِهُوا عَلَيْهِ",
            translation = "Verily Allah has pardoned for my Ummah their mistakes, their forgetfulness, and that which they are forced to do under compulsion.",
            authenticity = "Sunan Ibn Majah (Sahih)",
            chapter = "Pardon for Mistakes and Forgetfulness"
        ),
        HadithEntity(
            id = "nawawi_40",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 40,
            narrator = "Abdullah ibn Umar (may Allah be pleased with them both)",
            arabicText = "أَخَذَ رَسُولُ اللَّهِ بِمَنْكِبِي فَقَالَ: كُنْ فِي الدُّنْيَا كَأَنَّكَ غَرِيبٌ أَوْ عَابِرُ سَبِيلٍ",
            translation = "The Messenger took me by the shoulder and said: 'Be in this world as though you were a stranger or a wayfarer traveler.' And Ibn Umar used to say: 'When evening comes, do not anticipate the morning; and when morning comes, do not anticipate the evening. Take from your health for your illness, and from your life for your death.'",
            authenticity = "Sahih Bukhari",
            chapter = "Life as a Traveler"
        ),
        HadithEntity(
            id = "nawawi_41",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 41,
            narrator = "Abdullah ibn Amr ibn Al-As (may Allah be pleased with them both)",
            arabicText = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يَكُونَ هَوَاهُ تَبَعًا لِمَا جِئْتُ بِهِ",
            translation = "None of you truly believes until his desires and inclination conform to that which I have brought.",
            authenticity = "Al-Baghawi in Sharh as-Sunnah (Sahih)",
            chapter = "Submitting Desires to Revelation"
        ),
        HadithEntity(
            id = "nawawi_42",
            collection = "40 Hadith Nawawi",
            bookNumber = 1,
            hadithNumber = 42,
            narrator = "Anas ibn Malik (may Allah be pleased with him)",
            arabicText = "يَا ابْنَ آدَمَ إِنَّكَ مَا دَعَوْتَنِي وَرَجَوْتَنِي غَفَرْتُ لَكَ عَلَى مَا كَانَ فِيكَ وَلاَ أُبَالِي، يَا ابْنَ آدَمَ لَوْ بَلَغَتْ ذُنُوبُكَ عَنَانَ السَّمَاءِ ثُمَّ اسْتَغْفَرْتَنِي غَفَرْتُ لَكَ، يَا ابْنَ آدَمَ إِنَّكَ لَوْ أَتَيْتَنِي بِقُرَابِ الأَرْضِ خَطَايَا ثُمَّ لَقِيتَنِي لاَ تُشْرِكُ بِي شَيْئًا لأَتَيْتُكَ بِقُرَابِهَا مَغْفِرَةً",
            translation = "Allah the Almighty said: 'O son of Adam, so long as you call upon Me and hope in Me, I shall forgive you for what you have done, and I do not mind. O son of Adam, were your sins to reach the clouds of the sky and were you then to ask forgiveness of Me, I would forgive you. O son of Adam, were you to come to Me with sins nearly as great as the earth and were you then to meet Me without associating any partner with Me, I would bring you forgiveness nearly as great as it.' (Hadith Qudsi)",
            authenticity = "Sunan at-Tirmidhi (Hasan Sahih)",
            chapter = "The Boundless Forgiveness of Allah"
        ),

        // ==========================================
        // SAHIH BUKHARI GEMS
        // ==========================================
        HadithEntity(
            id = "bukhari_best_quran",
            collection = "Sahih Bukhari",
            bookNumber = 66,
            hadithNumber = 5027,
            narrator = "Uthman ibn Affan (may Allah be pleased with him)",
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            translation = "The best of you are those who learn the Qur'an and teach it to others.",
            authenticity = "Sahih Bukhari",
            chapter = "Virtues of the Qur'an"
        ),
        HadithEntity(
            id = "bukhari_seven_shade",
            collection = "Sahih Bukhari",
            bookNumber = 10,
            hadithNumber = 660,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "سَبْعَةٌ يُظِلُّهُمُ اللَّهُ فِي ظِلِّهِ يَوْمَ لاَ ظِلَّ إِلاَّ ظِلُّهُ: إِمَامٌ عَادِلٌ، وَشَابٌّ نَشَأَ فِي عِبَادَةِ اللَّهِ، وَرَجُلٌ قَلْبُهُ مُعَلَّقٌ فِي الْمَسَاجِدِ، وَرَجُلاَنِ تَحَابَّا فِي اللَّهِ اجْتَمَعَا عَلَيْهِ وَتَفَرَّقَا عَلَيْهِ، وَرَجُلٌ دَعَتْهُ امْرَأَةٌ ذَاتُ مَنْصِبٍ وَجَمَالٍ فَقَالَ إِنِّي أَخَافُ اللَّهَ، وَرَجُلٌ تَصَدَّقَ بِصَدَقَةٍ فَأَخْفَاهَا حَتَّى لاَ تَعْلَمَ شِمَالُهُ مَا تُنْفِقُ يَمِينُهُ، وَرَجُلٌ ذَكَرَ اللَّهَ خَالِيًا فَفَاضَتْ عَيْنَاهُ",
            translation = "There are seven whom Allah will shade in His shade on the Day when there will be no shade except His shade: a just ruler, a youth who grew up in the worship of Allah, a man whose heart is attached to the mosques, two people who love each other for the sake of Allah meeting and parting upon that, a man whom a woman of beauty and status invited but he said 'Indeed I fear Allah', a person who gives in charity so secretly that his left hand does not know what his right hand gave, and a person who remembered Allah in solitude and his eyes overflowed with tears.",
            authenticity = "Sahih Bukhari",
            chapter = "The Shade of the Throne"
        ),
        HadithEntity(
            id = "bukhari_one_body",
            collection = "Sahih Bukhari",
            bookNumber = 78,
            hadithNumber = 6011,
            narrator = "An-Nu'man ibn Bashir (may Allah be pleased with him)",
            arabicText = "مَثَلُ الْمُؤْمِنِينَ فِي تَوَادِّهِمْ وَتَرَاحُمِهِمْ وَتَعَاطُفِهِمْ مَثَلُ الْجَسَدِ إِذَا اشْتَكَى مِنْهُ عُضْوٌ تَدَاعَى لَهُ سَائِرُ الْجَسَدِ بِالسَّهَرِ وَالْحُمَّى",
            translation = "The similitude of believers in regard to their mutual love, affection, and empathy is that of one single body: when any limb aches, the whole body responds to it with wakefulness and fever.",
            authenticity = "Sahih Bukhari",
            chapter = "Compassion among Believers"
        ),
        HadithEntity(
            id = "bukhari_upper_hand",
            collection = "Sahih Bukhari",
            bookNumber = 24,
            hadithNumber = 1429,
            narrator = "Hakim ibn Hizam (may Allah be pleased with him)",
            arabicText = "الْيَدُ الْعُلْيَا خَيْرٌ مِنَ الْيَدِ السُّفْلَى، وَابْدَأْ بِمَنْ تَعُولُ، وَخَيْرُ الصَّدَقَةِ عَنْ ظَهْرِ غِنًى",
            translation = "The upper hand (that gives) is better than the lower hand (that receives). Start with those who are your dependents, and the best of charity is that given out of sufficiency.",
            authenticity = "Sahih Bukhari",
            chapter = "Generosity and Self-Sufficiency"
        ),
        HadithEntity(
            id = "bukhari_muslim_safety",
            collection = "Sahih Bukhari",
            bookNumber = 2,
            hadithNumber = 10,
            narrator = "Abdullah ibn Amr (may Allah be pleased with him)",
            arabicText = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ، وَالْمُهَاجِرُ مَنْ هَجَرَ مَا نَهَى اللَّهُ عَنْهُ",
            translation = "A true Muslim is the one from whose tongue and hands the Muslims are safe; and the true emigrant is the one who abandons what Allah has forbidden.",
            authenticity = "Sahih Bukhari",
            chapter = "Definition of a True Believer"
        ),
        HadithEntity(
            id = "bukhari_mother_companion",
            collection = "Sahih Bukhari",
            bookNumber = 78,
            hadithNumber = 5971,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "جَاءَ رَجُلٌ إِلَى رَسُولِ اللَّهِ فَقَالَ: يَا رَسُولَ اللَّهِ مَنْ أَحَقُّ النَّاسِ بِحُسْنِ صَحَابَتِي؟ قَالَ: أُمُّكَ. قَالَ: ثُمَّ مَنْ؟ قَالَ: ثُمَّ أُمُّكَ. قَالَ: ثُمَّ مَنْ؟ قَالَ: ثُمَّ أُمُّكَ. قَالَ: ثُمَّ مَنْ؟ قَالَ: ثُمَّ أَبُوكَ",
            translation = "A man came to the Messenger of Allah and said: 'O Messenger of Allah, who among the people is most deserving of my good companionship?' The Prophet said: 'Your mother.' The man asked: 'Then who?' He said: 'Your mother.' The man asked: 'Then who?' He said: 'Your mother.' The man asked: 'Then who?' The Prophet said: 'Then your father.'",
            authenticity = "Sahih Bukhari",
            chapter = "Honoring Parents"
        ),
        HadithEntity(
            id = "bukhari_two_words",
            collection = "Sahih Bukhari",
            bookNumber = 80,
            hadithNumber = 6406,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "كَلِمَتَانِ خَفِيفَتَانِ عَلَى اللِّسَانِ، ثَقِيلَتَانِ فِي الْمِيزَانِ، حَبِيبَتَانِ إِلَى الرَّحْمَنِ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            translation = "Two words are light on the tongue, heavy on the scales, and beloved to the Most Merciful: 'SubhanAllahi wa bihamdihi, SubhanAllahil-Azeem' (Glory be to Allah and His is the praise, Glory be to Allah the Tremendous).",
            authenticity = "Sahih Bukhari",
            chapter = "The Remembrance of Allah"
        ),
        HadithEntity(
            id = "bukhari_faith_branches",
            collection = "Sahih Bukhari",
            bookNumber = 2,
            hadithNumber = 9,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "الإِيمَانُ بِضْعٌ وَسِتُّونَ شُعْبَةً، وَالْحَيَاءُ شُعْبَةٌ مِنَ الإِيمَانِ",
            translation = "Faith has over sixty branches, and modesty (Haya) is a significant branch of faith.",
            authenticity = "Sahih Bukhari",
            chapter = "Branches of Faith"
        ),

        // ==========================================
        // SAHIH MUSLIM GEMS
        // ==========================================
        HadithEntity(
            id = "muslim_spread_peace",
            collection = "Sahih Muslim",
            bookNumber = 1,
            hadithNumber = 54,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "لاَ تَدْخُلُونَ الْجَنَّةَ حَتَّى تُؤْمِنُوا وَلاَ تُؤْمِنُوا حَتَّى تَحَابُّوا. أَوَلاَ أَدُلُّكُمْ عَلَى شَىْءٍ إِذَا فَعَلْتُمُوهُ تَحَابَبْتُمْ أَفْشُوا السَّلاَمَ بَيْنَكُمْ",
            translation = "You will not enter Paradise until you believe, and you will not believe until you love one another. Shall I not direct you to a thing which, if you do it, you will love one another? Spread peace among yourselves.",
            authenticity = "Sahih Muslim",
            chapter = "Faith & Spreading Peace"
        ),
        HadithEntity(
            id = "muslim_kindness_beautifies",
            collection = "Sahih Muslim",
            bookNumber = 45,
            hadithNumber = 2594,
            narrator = "Aisha (may Allah be pleased with her)",
            arabicText = "إِنَّ الرِّفْقَ لاَ يَكُونُ فِي شَىْءٍ إِلاَّ زَانَهُ وَلاَ يُنْزَعُ مِنْ شَىْءٍ إِلاَّ شَانَهُ",
            translation = "Indeed, gentleness is not found in anything except that it beautifies it, and it is not removed from anything except that it blemishes it.",
            authenticity = "Sahih Muslim",
            chapter = "The Virtue of Gentleness"
        ),
        HadithEntity(
            id = "muslim_strong_believer",
            collection = "Sahih Muslim",
            bookNumber = 46,
            hadithNumber = 2664,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "الْمُؤْمِنُ الْقَوِيُّ خَيْرٌ وَأَحَبُّ إِلَى اللَّهِ مِنَ الْمُؤْمِنِ الضَّعِيفِ، وَفِي كُلٍّ خَيْرٌ. احْرِصْ عَلَى مَا يَنْفَعُكَ، وَاسْتَعِنْ بِاللَّهِ وَلاَ تَعْجِزْ",
            translation = "A strong believer is better and is more beloved to Allah than a weak believer, though there is goodness in both. Cherish that which gives you benefit in the Hereafter, seek help from Allah, and do not lose heart.",
            authenticity = "Sahih Muslim",
            chapter = "Effort & Striving"
        ),
        HadithEntity(
            id = "muslim_hearts_deeds",
            collection = "Sahih Muslim",
            bookNumber = 45,
            hadithNumber = 2564,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "إِنَّ اللَّهَ لاَ يَنْظُرُ إِلَى صُوَرِكُمْ وَأَمْوَالِكُمْ وَلَكِنْ يَنْظُرُ إِلَى قُلُوبِكُمْ وَأَعْمَالِكُمْ",
            translation = "Verily Allah does not look at your appearances or your wealth, but He looks at your hearts and your deeds.",
            authenticity = "Sahih Muslim",
            chapter = "Sincerity of the Heart"
        ),
        HadithEntity(
            id = "muslim_three_after_death",
            collection = "Sahih Muslim",
            bookNumber = 25,
            hadithNumber = 1631,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "إِذَا مَاتَ الإِنْسَانُ انْقَطَعَ عَنْهُ عَمَلُهُ إِلاَّ مِنْ ثَلاَثَةٍ: إِلاَّ مِنْ صَدَقَةٍ جَارِيَةٍ، أَوْ عِلْمٍ يُنْتَفَعُ بِهِ، أَوْ وَلَدٍ صَالِحٍ يَدْعُو لَهُ",
            translation = "When a person dies, all his good deeds come to an end except three: continuous charity (Sadaqah Jariyah), beneficial knowledge that remains, or a righteous child who prays for him.",
            authenticity = "Sahih Muslim",
            chapter = "Ongoing Charity and Legacy"
        ),
        HadithEntity(
            id = "muslim_world_prison",
            collection = "Sahih Muslim",
            bookNumber = 55,
            hadithNumber = 2956,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "الدُّنْيَا سِجْنُ الْمُؤْمِنِ وَجَنَّةُ الْكَافِرِ",
            translation = "The world is a prison for the believer and a paradise for the disbeliever.",
            authenticity = "Sahih Muslim",
            chapter = "Perspective on Worldly Life"
        ),
        HadithEntity(
            id = "muslim_repentance_joy",
            collection = "Sahih Muslim",
            bookNumber = 50,
            hadithNumber = 2747,
            narrator = "Anas ibn Malik (may Allah be pleased with him)",
            arabicText = "لَلَّهُ أَشَدُّ فَرِحًا بِتَوْبَةِ عَبْدِهِ حِينَ يَتُوبُ إِلَيْهِ مِنْ أَحَدِكُمْ كَانَ عَلَى رَاحِلَتِهِ بِأَرْضِ فَلاَةٍ فَانْفَلَتَتْ مِنْهُ وَعَلَيْهَا طَعَامُهُ وَشَرَابُهُ...",
            translation = "Allah is more joyful with the repentance of His servant when he returns to Him than one of you who was on his mount in a desert land and it escaped him carrying his food and drink, so he despairingly lay down under a tree, then suddenly found it standing right beside him.",
            authenticity = "Sahih Muslim",
            chapter = "The Mercy and Joy of Repentance"
        ),

        // ==========================================
        // SUNAN ABI DAWUD GEMS
        // ==========================================
        HadithEntity(
            id = "abidawud_smiling_charity",
            collection = "Sunan Abi Dawud",
            bookNumber = 41,
            hadithNumber = 4790,
            narrator = "Abu Dharr (may Allah be pleased with him)",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            translation = "Your smiling in the face of your brother is a charitable deed for you.",
            authenticity = "Hasan Sahih",
            chapter = "Good Manners and Warmth"
        ),
        HadithEntity(
            id = "abidawud_seeking_knowledge",
            collection = "Sunan Abi Dawud",
            bookNumber = 25,
            hadithNumber = 3641,
            narrator = "Abu Darda (may Allah be pleased with him)",
            arabicText = "مَنْ سَلَكَ طَرِيقًا يَطْلُبُ فِيهِ عِلْمًا سَلَكَ اللَّهُ بِهِ طَرِيقًا مِنْ طُرُقِ الْجَنَّةِ، وَإِنَّ الْمَلاَئِكَةَ لَتَضَعُ أَجْنِحَتَهَا رِضًا لِطَالِبِ الْعِلْمِ",
            translation = "Whoever treads a path seeking knowledge, Allah makes easy for him a path to Paradise. Indeed the angels lower their wings in pleasure for the seeker of knowledge.",
            authenticity = "Sahih",
            chapter = "Seeking Sacred Knowledge"
        ),
        HadithEntity(
            id = "abidawud_merciful_shown_mercy",
            collection = "Sunan Abi Dawud",
            bookNumber = 43,
            hadithNumber = 4941,
            narrator = "Abdullah ibn Amr (may Allah be pleased with them both)",
            arabicText = "الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَنُ، ارْحَمُوا مَنْ فِي الأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ",
            translation = "Those who are merciful will be shown mercy by the Most Merciful. Be merciful to those on the earth, and the One in the heavens will be merciful to you.",
            authenticity = "Sahih",
            chapter = "Mercy to All Creation"
        ),
        HadithEntity(
            id = "abidawud_truthfulness",
            collection = "Sunan Abi Dawud",
            bookNumber = 43,
            hadithNumber = 4989,
            narrator = "Abdullah ibn Mas'ud (may Allah be pleased with him)",
            arabicText = "عَلَيْكُمْ بِالصِّدْقِ فَإِنَّ الصِّدْقَ يَهْدِي إِلَى الْبِرِّ، وَإِنَّ الْبِرَّ يَهْدِي إِلَى الْجَنَّةِ",
            translation = "Adhere strictly to truthfulness, for truthfulness leads to righteousness, and righteousness leads to Paradise.",
            authenticity = "Sahih",
            chapter = "Truthfulness in Speech"
        ),
        HadithEntity(
            id = "abidawud_worker_wages",
            collection = "Sunan Abi Dawud",
            bookNumber = 23,
            hadithNumber = 3450,
            narrator = "Abdullah ibn Umar (may Allah be pleased with him)",
            arabicText = "أَعْطُوا الأَجِيرَ أَجْرَهُ قَبْلَ أَنْ يَجِفَّ عَرَقُهُ",
            translation = "Give the laborer his wages before his sweat dries.",
            authenticity = "Hasan Sahih",
            chapter = "Honesty and Justice in Labor"
        ),

        // ==========================================
        // JAMI` AT-TIRMIDHI GEMS
        // ==========================================
        HadithEntity(
            id = "tirmidhi_best_to_family",
            collection = "Jami` at-Tirmidhi",
            bookNumber = 49,
            hadithNumber = 3895,
            narrator = "Aisha (may Allah be pleased with her)",
            arabicText = "خَيْرُكُمْ خَيْرُكُمْ لأَهْلِهِ، وَأَنَا خَيْرُكُمْ لأَهْلِي",
            translation = "The best of you are those who are best to their families, and I am the best among you to my family.",
            authenticity = "Hasan Sahih",
            chapter = "Family Kindness and Good Conduct"
        ),
        HadithEntity(
            id = "tirmidhi_dua_worship",
            collection = "Jami` at-Tirmidhi",
            bookNumber = 48,
            hadithNumber = 3372,
            narrator = "An-Nu'man ibn Bashir (may Allah be pleased with him)",
            arabicText = "الدُّعَاءُ هُوَ الْعِبَادَةُ",
            translation = "Supplication (Dua) is the essence of worship itself.",
            authenticity = "Sahih",
            chapter = "The Power of Supplication"
        ),
        HadithEntity(
            id = "tirmidhi_consistent_deeds",
            collection = "Jami` at-Tirmidhi",
            bookNumber = 45,
            hadithNumber = 2856,
            narrator = "Aisha (may Allah be pleased with her)",
            arabicText = "أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ",
            translation = "The most beloved deeds to Allah are those done consistently, even if they are small.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Consistency in Good Deeds"
        ),
        HadithEntity(
            id = "tirmidhi_generous_person",
            collection = "Jami` at-Tirmidhi",
            bookNumber = 27,
            hadithNumber = 1961,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "السَّخِيُّ قَرِيبٌ مِنَ اللَّهِ، قَرِيبٌ مِنَ الْجَنَّةِ، قَرِيبٌ مِنَ النَّاسِ، بَعِيدٌ مِنَ النَّارِ",
            translation = "A generous person is close to Allah, close to Paradise, close to people, and far from the Hellfire.",
            authenticity = "Hasan",
            chapter = "The Blessings of Generosity"
        ),

        // ==========================================
        // RIYAD AS-SALIHIN GEMS
        // ==========================================
        HadithEntity(
            id = "riyad_patience_first_strike",
            collection = "Riyad as-Salihin",
            bookNumber = 1,
            hadithNumber = 29,
            narrator = "Anas ibn Malik (may Allah be pleased with him)",
            arabicText = "إِنَّمَا الصَّبْرُ عِنْدَ الصَّدْمَةِ الأُولَى",
            translation = "True patience is demonstrated at the initial impact of a trial.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Patience during Afflictions"
        ),
        HadithEntity(
            id = "riyad_tawakkul_birds",
            collection = "Riyad as-Salihin",
            bookNumber = 1,
            hadithNumber = 79,
            narrator = "Umar ibn Al-Khattab (may Allah be pleased with him)",
            arabicText = "لَوْ أَنَّكُمْ كُنْتُمْ تَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرُزِقْتُمْ كَمَا تُرزَقُ الطَّيْرُ، تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
            translation = "If you were to rely upon Allah with true reliance, He would provide for you just as He provides for the birds: they go out in the morning with empty stomachs and return at dusk full and nourished.",
            authenticity = "Sunan at-Tirmidhi (Sahih)",
            chapter = "True Reliance on Allah (Tawakkul)"
        ),
        HadithEntity(
            id = "riyad_caring_widow_poor",
            collection = "Riyad as-Salihin",
            bookNumber = 1,
            hadithNumber = 265,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "السَّاعِي عَلَى الأَرْمَلَةِ وَالْمِسْكِينِ كَالْمُجَاهِدِ فِي سَبِيلِ اللَّهِ، أَوِ الْقَائِمِ اللَّيْلَ الصَّائِمِ النَّهَارَ",
            translation = "The one who cares for the widow and the poor is like the one who strives in the cause of Allah, or like the one who stands the entire night in prayer and fasts throughout the day.",
            authenticity = "Sahih (Muttafaqun 'Alayh)",
            chapter = "Caring for the Vulnerable"
        ),
        HadithEntity(
            id = "riyad_gratitude_people",
            collection = "Riyad as-Salihin",
            bookNumber = 5,
            hadithNumber = 712,
            narrator = "Abu Hurairah (may Allah be pleased with him)",
            arabicText = "لاَ يَشْكُرُ اللَّهَ مَنْ لاَ يَشْكُرُ النَّاسَ",
            translation = "He who does not thank people does not thank Allah.",
            authenticity = "Sunan Abi Dawud & at-Tirmidhi (Sahih)",
            chapter = "Expressing Gratitude"
        )
    )
}
