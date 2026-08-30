package com.example.ui.screens.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastingTrackerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFastingToday by remember { mutableStateOf(true) }
    val safaColors = LocalSafaColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fasting & Ramadan Tracker",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = SafaSpacing.screenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SafaSpacing.md)
        ) {
            // Suhoor & Iftar Countdowns Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = if (safaColors.isLuxuryNavy) {
                                        listOf(Color(0xFF16254F), Color(0xFF0B132B), Color(0xFF070D1E))
                                    } else {
                                        listOf(Color(0xFF0B132B), Color(0xFF16254F))
                                    }
                                )
                            )
                            .padding(SafaSpacing.cardContentPadding)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "FASTING COUNTDOWN",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = safaColors.goldChampagne,
                                        letterSpacing = 1.2.sp
                                    )
                                    Text(
                                        text = "Iftar at Maghrib (7:40 PM)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDFBF7)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Remaining: 4 hrs 12 mins",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.goldPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Suhoor ends at Fajr: 05:15 AM",
                                style = MaterialTheme.typography.bodySmall,
                                color = safaColors.goldChampagne.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Today's Fast Status Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFastingToday) {
                            if (safaColors.isLuxuryNavy) Color(0xFF0F261D) else IslamicGreenLight
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isFastingToday) IslamicGreen else safaColors.goldBorder.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SafaSpacing.cardContentPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (isFastingToday) IslamicGreen else safaColors.goldGlow,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isFastingToday) Icons.Filled.CheckCircle else Icons.Outlined.Restaurant,
                                    contentDescription = null,
                                    tint = if (isFastingToday) Color.White else safaColors.goldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = if (isFastingToday) "Fasting Logged Today ✓" else "Not Fasting Today",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFastingToday) IslamicGreen else safaColors.textPrimary
                                )
                                Text(
                                    text = "Tap to change today's fast record",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = safaColors.textSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { isFastingToday = !isFastingToday },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFastingToday) IslamicGreen else safaColors.goldPrimary
                            ),
                            shape = RoundedCornerShape(SafaSpacing.pillRadius)
                        ) {
                            Text(
                                if (isFastingToday) "Done" else "Mark Fast",
                                color = if (isFastingToday) Color.White else SafaNavyDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Iftar & Suhoor Duas Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
                        Text(
                            text = "Dua for Breaking Fast (Iftar)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                            style = ArabicDisplayStyle,
                            fontSize = 20.sp,
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Right,
                            color = safaColors.goldChampagne,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Dhahabadh-dhama'u wabtallatil-'urooqu wa thabatal-ajru in sha Allah",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "\"The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = safaColors.textPrimary
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
