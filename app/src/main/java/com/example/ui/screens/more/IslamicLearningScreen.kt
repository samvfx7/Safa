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
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
    val tabs = listOf("5 Pillars", "Wudu Guide", "Salah Guide", "Articles of Faith")
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

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = safaColors.goldPrimary,
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
