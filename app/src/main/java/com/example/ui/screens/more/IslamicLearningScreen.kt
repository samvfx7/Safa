package com.example.ui.screens.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Brush
import com.example.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

data class LearningTopic(
    val title: String,
    val arabic: String,
    val description: String,
    val steps: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicLearningScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("5 Pillars", "Wudu Guide", "Salah Guide", "Articles of Faith", "Spiritual Quiz")
    val safaColors = LocalSafaColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Islamic Learning & Guides",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = safaColors.goldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hadith of the Day Hero Banner with Mosque Arch
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SafaSpacing.screenHorizontalPadding, vertical = 8.dp),
                shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mosque_arch_learning_1788032682357),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xE61E140C),
                                        Color(0xB32C1E14),
                                        Color(0x661A120B)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "HADITH OF THE DAY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldChampagne,
                            letterSpacing = 1.2.sp
                        )

                        Text(
                            text = "“Patience is illumination (Diya).”",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDFBF7)
                        )

                        Text(
                            text = "— Sahih Muslim 223",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2D6C6)
                        )
                    }
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = safaColors.goldPrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = safaColors.goldPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (selectedTab == index) safaColors.goldPrimary else safaColors.textSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(SafaSpacing.sm))

            when (selectedTab) {
                0 -> FivePillarsList()
                1 -> WuduGuideList()
                2 -> SalahGuideList()
                3 -> SixArticlesList()
                4 -> IslamicQuizView()
            }
        }
    }
}

@Composable
private fun FivePillarsList() {
    val pillars = listOf(
        LearningTopic(
            "1. Shahadah (Declaration of Faith)",
            "الشَّهَادَةُ",
            "Bearing witness that there is no god worthy of worship except Allah, and Muhammad (pbuh) is the Messenger of Allah. The cornerstone of Islamic monotheism."
        ),
        LearningTopic(
            "2. Salah (Ritual Prayer)",
            "الصَّلَاةُ",
            "Establishing the five daily prayers in devotion and mindfulness: Fajr (Dawn), Dhuhr (Noon), Asr (Afternoon), Maghrib (Sunset), and Isha (Night)."
        ),
        LearningTopic(
            "3. Zakat (Purifying Almsgiving)",
            "الزَّكَاةُ",
            "An obligatory annual 2.5% contribution of accumulated surplus wealth to purify wealth and support the poor, needy, and community."
        ),
        LearningTopic(
            "4. Sawm (Fasting Ramadan)",
            "الصَّوْمُ",
            "Fasting from dawn until sunset throughout the sacred month of Ramadan, abstaining from food, drink, and desires to cultivate Taqwa (God-consciousness)."
        ),
        LearningTopic(
            "5. Hajj (Pilgrimage to Mecca)",
            "الحَجُّ",
            "The sacred pilgrimage to the Holy Kaaba in Mecca, mandatory once in a lifetime for every Muslim who has the physical and financial ability."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SafaSpacing.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
    ) {
        items(pillars) { topic ->
            ExpandableTopicCard(topic = topic)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun WuduGuideList() {
    val wuduSteps = listOf(
        LearningTopic(
            "1. Intention (Niyyah) & Bismillah",
            "النيّة والتسمية",
            "Make sincere internal intention to purify yourself for prayer, and say 'Bismillahir-Rahmanir-Raheem'."
        ),
        LearningTopic(
            "2. Wash Hands (3x)",
            "غسل اليدين",
            "Wash both hands up to the wrists thoroughly 3 times, ensuring water reaches between all fingers."
        ),
        LearningTopic(
            "3. Rinse Mouth (3x)",
            "المضمضة",
            "Take water into the mouth with the right hand, rinse thoroughly, and expel it 3 times."
        ),
        LearningTopic(
            "4. Sniff Water into Nose (3x)",
            "الاستنشاق",
            "Sniff water gently into the nostrils with the right hand, and blow it out using the left hand 3 times."
        ),
        LearningTopic(
            "5. Wash Face (3x)",
            "غسل الوجه",
            "Wash the entire face from hairline to below the chin, and ear-to-ear, 3 times."
        ),
        LearningTopic(
            "6. Wash Arms to the Elbows (3x)",
            "غسل اليدين إلى المرفقين",
            "Wash the right forearm from fingertips including the elbow 3 times, then repeat for the left arm."
        ),
        LearningTopic(
            "7. Wipe Head & Ears (1x)",
            "مسح الرأس والأذنين",
            "Wipe wet hands over the top of the head from front to back and forward again, then use index fingers for inside ears and thumbs for back of ears."
        ),
        LearningTopic(
            "8. Wash Feet to the Ankles (3x)",
            "غسل الرجلين إلى الكعبين",
            "Wash the right foot including between the toes up to the ankle 3 times, then repeat for the left foot."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SafaSpacing.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
    ) {
        items(wuduSteps) { step ->
            ExpandableTopicCard(topic = step)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun SalahGuideList() {
    val salahSteps = listOf(
        LearningTopic(
            "1. Takbiratul Ihram (Opening Takbir)",
            "تكبيرة الإحرام",
            "Face the Qibla with sincere intention, raise hands to earlobes/shoulders, and say 'Allahu Akbar' (Allah is the Greatest)."
        ),
        LearningTopic(
            "2. Qiyam & Recitation of Surah Al-Fatihah",
            "القيام وقراءة الفاتحة",
            "Place right hand over left on the chest/navel. Recite opening supplication, Surah Al-Fatihah, and an additional short Surah."
        ),
        LearningTopic(
            "3. Ruku (Bowing)",
            "الركوع",
            "Say 'Allahu Akbar' and bow with back straight, hands on knees. Say 'Subhana Rabbiyal Azeem' (Glory be to my Lord the Almighty) 3 times."
        ),
        LearningTopic(
            "4. Rising from Bowing (I'tidal)",
            "الاعتدال",
            "Rise upright saying 'Sami' Allahu liman hamidah' (Allah hears whoever praises Him), followed by 'Rabbana wa lakal-hamd'."
        ),
        LearningTopic(
            "5. Sujud (Prostration 2x)",
            "السجود",
            "Say 'Allahu Akbar' and prostrate on 7 points: forehead & nose, both palms, both knees, toes. Say 'Subhana Rabbiyal A'la' (Glory be to my Lord the Most High) 3 times."
        ),
        LearningTopic(
            "6. Jalsah (Sitting between Prostrations)",
            "الجلوس بين السجدتين",
            "Rise to sitting saying 'Allahu Akbar'. Supplicate: 'Rabbighfir li' (My Lord forgive me) twice, then perform second prostration."
        ),
        LearningTopic(
            "7. Tashahhud & Final Tasleem",
            "التشهد والتسليم",
            "Sit and recite At-Tahiyyat, send Salawat upon the Prophet, make dua, and conclude by turning right saying 'Assalamu alaykum wa rahmatullah', then left."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SafaSpacing.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
    ) {
        items(salahSteps) { step ->
            ExpandableTopicCard(topic = step)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun SixArticlesList() {
    val articles = listOf(
        LearningTopic("1. Belief in Allah", "الإيمان بالله", "Belief in Allah's Oneness, Names, Attributes, and absolute Sovereignty without partners."),
        LearningTopic("2. Belief in His Angels", "الإيمان بالملائكة", "Belief in the noble, obedient creations of light including Jibreel, Mika'il, Israfil, and Malik."),
        LearningTopic("3. Belief in His Revealed Books", "الإيمان بالكتب", "Belief in divine revelations: Tawrat to Musa, Zabur to Dawud, Injeel to Isa, and the Holy Qur'an to Muhammad (pbuh)."),
        LearningTopic("4. Belief in His Prophets", "الإيمان بالرسل", "Belief in all prophets from Adam, Nuh, Ibrahim, Musa, Isa to the final seal, Muhammad (pbuh)."),
        LearningTopic("5. Belief in the Day of Judgment", "الإيمان باليوم الآخر", "Belief in the Resurrection, Reckoning, Scale of deeds, and eternal Paradise or Hellfire."),
        LearningTopic("6. Belief in Divine Decree (Qadar)", "الإيمان بالقدر", "Belief that everything occurs by Allah's divine knowledge, decree, will, and supreme wisdom.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SafaSpacing.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
    ) {
        items(articles) { article ->
            ExpandableTopicCard(topic = article)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ExpandableTopicCard(topic: LearningTopic) {
    var isExpanded by remember { mutableStateOf(false) }
    val safaColors = LocalSafaColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                    Text(
                        text = topic.arabic,
                        style = ArabicDisplayStyle,
                        fontSize = 18.sp,
                        color = safaColors.goldChampagne
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = safaColors.goldPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = topic.description,
                style = MaterialTheme.typography.bodyMedium,
                color = safaColors.textPrimary,
                lineHeight = 22.sp
            )
        }
    }
}

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Composable
private fun IslamicQuizView() {
    val safaColors = LocalSafaColors.current
    val questions = remember {
        listOf(
            QuizQuestion(
                id = 1,
                question = "What is the first pillar of Islam, declaring absolute Oneness of Allah?",
                options = listOf(
                    "Salah (Daily Prayer)",
                    "Shahadah (Declaration of Faith)",
                    "Zakat (Purifying Charity)",
                    "Sawm (Fasting Ramadan)"
                ),
                correctIndex = 1,
                explanation = "Shahadah is the testimony of faith: 'La ilaha illa Allah, Muhammadun Rasul Allah' (There is no god but Allah, Muhammad is His messenger). It is the gateway to Islam."
            ),
            QuizQuestion(
                id = 2,
                question = "Which Surah is the 'Mother of the Quran' and recited in every unit of prayer?",
                options = listOf(
                    "Surah Al-Ikhlas (Sincerity)",
                    "Surah Al-Fatihah (The Opening)",
                    "Surah Al-Baqarah (The Cow)",
                    "Surah Al-Yasin"
                ),
                correctIndex = 1,
                explanation = "Surah Al-Fatihah is a mandatory pillar of prayer. No prayer is valid without reciting it in every single Rak'ah (unit)."
            ),
            QuizQuestion(
                id = 3,
                question = "What standard percentage of surplus wealth is due for annual Zakat (Almsgiving)?",
                options = listOf(
                    "1.0%",
                    "2.5%",
                    "5.0%",
                    "10.0%"
                ),
                correctIndex = 1,
                explanation = "Surplus wealth (exceeding the Nisab threshold) held for one lunar year requires a Zakat payment of 2.5% to purify assets and aid the needy."
            ),
            QuizQuestion(
                id = 4,
                question = "During which sacred month was the Quran first revealed to Prophet Muhammad (pbuh)?",
                options = listOf(
                    "Muharram",
                    "Rajab",
                    "Ramadan",
                    "Dhul-Hijjah"
                ),
                correctIndex = 2,
                explanation = "The Quran was revealed during Ramadan on Laylat al-Qadr (the Night of Decree). Ramadan is the month of intense spiritual focus and fasting."
            ),
            QuizQuestion(
                id = 5,
                question = "The direction of Qibla points Muslims worldwide toward which sacred building?",
                options = listOf(
                    "The Al-Aqsa Mosque",
                    "The Prophet's Mosque in Medina",
                    "The Holy Kaaba in Mecca",
                    "The Dome of the Rock"
                ),
                correctIndex = 2,
                explanation = "The Qibla is the direction pointing towards the Kaaba in Mecca, Saudi Arabia, establishing unity of worship for Muslims globally."
            )
        )
    }

    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var selectedOptionIdx by remember { mutableStateOf<Int?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var showResults by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SafaSpacing.screenHorizontalPadding)
    ) {
        if (!showResults) {
            val q = questions[currentQuestionIdx]

            // Top Status Block
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUESTION ${currentQuestionIdx + 1} OF ${questions.size}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary
                )
            }

            // Question Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(safaColors.navyBorder.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(((currentQuestionIdx + 1).toFloat() / questions.size.toFloat()))
                        .height(4.dp)
                        .background(safaColors.goldPrimary, RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Question Text Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SafaSpacing.cardRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
            ) {
                Text(
                    text = q.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Options List
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                q.options.forEachIndexed { idx, optionText ->
                    val isSelected = selectedOptionIdx == idx
                    val isCorrect = idx == q.correctIndex
                    val isIncorrect = isSelected && !isCorrect

                    val optionColor = when {
                        isAnswerChecked && isCorrect -> Color(0xFF2E7D32).copy(alpha = 0.15f) // Green background
                        isAnswerChecked && isIncorrect -> Color(0xFFC62828).copy(alpha = 0.15f) // Red background
                        isSelected -> safaColors.goldPrimary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isAnswerChecked && isCorrect -> Color(0xFF2E7D32)
                        isAnswerChecked && isIncorrect -> Color(0xFFC62828)
                        isSelected -> safaColors.goldPrimary
                        else -> safaColors.navyBorder.copy(alpha = 0.3f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswerChecked) {
                                selectedOptionIdx = idx
                            },
                        shape = RoundedCornerShape(SafaSpacing.cardRadius),
                        colors = CardDefaults.cardColors(containerColor = optionColor),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A'.code + idx).toChar().toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SafaNavyDark else safaColors.textPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = safaColors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Answer explanation banner
                if (isAnswerChecked) {
                    val wasCorrect = selectedOptionIdx == q.correctIndex
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (wasCorrect) Color(0xFFE8F5E9).copy(alpha = 0.08f) else Color(0xFFFFEBEE).copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.dp, if (wasCorrect) Color(0xFF2E7D32).copy(alpha = 0.3f) else Color(0xFFC62828).copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (wasCorrect) "✓ Correct Answer" else "✗ Incorrect",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (wasCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = q.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = safaColors.textSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Action buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isAnswerChecked) {
                    Button(
                        onClick = {
                            if (selectedOptionIdx != null) {
                                isAnswerChecked = true
                                if (selectedOptionIdx == q.correctIndex) {
                                    score++
                                }
                            }
                        },
                        enabled = selectedOptionIdx != null,
                        colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                        shape = RoundedCornerShape(SafaSpacing.pillRadius),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Check Answer",
                            color = SafaNavyDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (currentQuestionIdx < questions.lastIndex) {
                                currentQuestionIdx++
                                selectedOptionIdx = null
                                isAnswerChecked = false
                            } else {
                                showResults = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                        shape = RoundedCornerShape(SafaSpacing.pillRadius),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentQuestionIdx == questions.lastIndex) "View Results" else "Next Question",
                            color = SafaNavyDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Results View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$score/${questions.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = when {
                        score == questions.size -> "Subhan'Allah! Perfect Score!"
                        score >= 3 -> "Mash'Allah! Excellent Knowledge!"
                        else -> "Keep Learning and Reflecting!"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You answered $score out of ${questions.size} questions correctly. Seek knowledge to illuminate your heart.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = safaColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        currentQuestionIdx = 0
                        selectedOptionIdx = null
                        isAnswerChecked = false
                        score = 0
                        showResults = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                    shape = RoundedCornerShape(SafaSpacing.pillRadius)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = SafaNavyDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Take Quiz Again",
                        color = SafaNavyDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
