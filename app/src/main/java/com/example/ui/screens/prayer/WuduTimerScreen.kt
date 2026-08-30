package com.example.ui.screens.prayer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaSpacing
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.SafaNavyDark
import kotlinx.coroutines.delay

data class WuduStep(
    val title: String,
    val arabic: String?,
    val instruction: String,
    val repetition: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WuduTimerScreen(
    onScanPrayerMatClick: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    
    // Elapsed wudu timer state
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    // Wudu Step Indexing
    var currentStepIndex by remember { mutableIntStateOf(0) }
    
    // Auto advance feature for hands-free use
    var autoAdvanceEnabled by remember { mutableStateOf(false) }
    var autoAdvanceSecondsRemaining by remember { mutableIntStateOf(15) }

    val wuduSteps = remember {
        listOf(
            WuduStep(
                title = "Intention (Niyyah) & Bismillah",
                arabic = "بِسْمِ اللَّهِ",
                instruction = "Formulate a sincere intention in your heart to purify yourself for worship, then say 'Bismillah' (In the name of Allah) aloud or silently.",
                repetition = "Once"
            ),
            WuduStep(
                title = "Wash Both Hands",
                arabic = null,
                instruction = "Thoroughly wash your right hand, then your left hand, up to the wrists three times. Be sure to rub between all of your fingers.",
                repetition = "3 Times"
            ),
            WuduStep(
                title = "Rinse Your Mouth",
                arabic = null,
                instruction = "Take water into your mouth with your right hand, swirl it completely to clean your teeth and tongue, and expel it.",
                repetition = "3 Times"
            ),
            WuduStep(
                title = "Inhale Water into Nose",
                arabic = null,
                instruction = "Sniff water gently into your nostrils using your right hand, then blow it out using your left hand to clear the passages.",
                repetition = "3 Times"
            ),
            WuduStep(
                title = "Wash Your Whole Face",
                arabic = null,
                instruction = "Wash your entire face from your forehead down to your chin, and from ear to ear. Ensure water touches every part.",
                repetition = "3 Times"
            ),
            WuduStep(
                title = "Wash Arms to the Elbows",
                arabic = null,
                instruction = "Wash your right arm from your fingertips up to and including the elbow, then repeat the same for your left arm.",
                repetition = "3 Times (Each)"
            ),
            WuduStep(
                title = "Wipe Your Head (Masah)",
                arabic = null,
                instruction = "Wet both hands, shake off excess water, and run them from the front of your hairline to the back, then bring them back to the front once.",
                repetition = "Once"
            ),
            WuduStep(
                title = "Wipe Your Ears",
                arabic = null,
                instruction = "Insert your wet index fingers into the crevices of your ears while using your thumbs to clean behind your ear lobes.",
                repetition = "Once"
            ),
            WuduStep(
                title = "Wash Both Feet to Ankles",
                arabic = null,
                instruction = "Wash your right foot up to the ankle, ensuring you clean between the toes with your pinky, then repeat with your left foot.",
                repetition = "3 Times (Each)"
            ),
            WuduStep(
                title = "Recite Shahada Supplication",
                arabic = "أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
                instruction = "Look towards the heavens and recite the Shahada to complete your wudu: 'I bear witness that there is no deity but Allah, and Muhammad is His servant and messenger.'",
                repetition = "Once"
            )
        )
    }

    // Main Timer Effect
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            secondsElapsed++
        }
    }

    // Auto Advance Effect (Hands-Free countdown timer)
    LaunchedEffect(currentStepIndex, autoAdvanceEnabled, isRunning) {
        if (autoAdvanceEnabled && isRunning && currentStepIndex < wuduSteps.lastIndex) {
            autoAdvanceSecondsRemaining = 15
            while (autoAdvanceSecondsRemaining > 0) {
                delay(1000L)
                autoAdvanceSecondsRemaining--
            }
            if (currentStepIndex < wuduSteps.lastIndex) {
                currentStepIndex++
            }
        }
    }

    val minutes = secondsElapsed / 60
    val seconds = secondsElapsed % 60
    val totalTimeString = String.format("%02d:%02d", minutes, seconds)
    val activeStep = wuduSteps[currentStepIndex]

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wudu Companion",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
                },
                actions = {
                    // Total Time Counter Box
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(safaColors.goldGlow, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = safaColors.goldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = totalTimeString,
                                style = MaterialTheme.typography.titleSmall,
                                color = safaColors.goldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                .padding(start = SafaSpacing.screenHorizontalPadding, end = SafaSpacing.screenHorizontalPadding, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // 1. Dynamic Progress Pill Indicators (Horizontal row indicating progress)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    wuduSteps.forEachIndexed { idx, _ ->
                        val isPast = idx < currentStepIndex
                        val isCurrent = idx == currentStepIndex
                        val pillColor = when {
                            isCurrent -> safaColors.goldPrimary
                            isPast -> safaColors.goldPrimary.copy(alpha = 0.6f)
                            else -> safaColors.navyBorder.copy(alpha = 0.3f)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(pillColor)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STEP ${currentStepIndex + 1} OF ${wuduSteps.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 1.sp
                    )
                    
                    Text(
                        text = "Goal: ${activeStep.repetition}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textSecondary
                    )
                }
            }

            // 2. Animated Step Content Card (Central interactive view)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SafaSpacing.cardContentPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        
                        // Icon & Goal Label
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(safaColors.goldGlow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Wudu Step",
                                    tint = safaColors.goldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            if (autoAdvanceEnabled && currentStepIndex < wuduSteps.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .background(safaColors.goldPrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Auto-advancing in ${autoAdvanceSecondsRemaining}s",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = safaColors.goldPrimary
                                    )
                                }
                            }
                        }

                        // Text content with smooth crossfades
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = activeStep.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            if (activeStep.arabic != null) {
                                Text(
                                    text = activeStep.arabic,
                                    style = ArabicDisplayStyle,
                                    fontSize = 26.sp,
                                    lineHeight = 42.sp,
                                    color = safaColors.goldPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Text(
                                text = activeStep.instruction,
                                style = MaterialTheme.typography.bodyMedium,
                                color = safaColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        }

                        // Tips Alert Banner at bottom of card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(safaColors.navyBorder.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Tip",
                                tint = safaColors.goldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Prophetic Sunnah: Conserve water and ensure every limb is thoroughly rubbed to create complete coverage.",
                                style = MaterialTheme.typography.bodySmall,
                                color = safaColors.textSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 3. Hands-Free Control Toggles (Preventing phone touch with wet hands)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SafaSpacing.cardRadius),
                colors = CardDefaults.cardColors(containerColor = safaColors.navyBorder.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hands-Free Auto Advance",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        Text(
                            text = "Automatically switches steps every 15 seconds",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary
                        )
                    }
                    
                    Switch(
                        checked = autoAdvanceEnabled,
                        onCheckedChange = { autoAdvanceEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = safaColors.goldPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Primary Progression Controllers (Back / Next / Complete buttons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Step Button
                OutlinedButton(
                    onClick = {
                        if (currentStepIndex > 0) {
                            currentStepIndex--
                        }
                    },
                    enabled = currentStepIndex > 0,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(SafaSpacing.pillRadius),
                    border = BorderStroke(1.dp, safaColors.goldPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        tint = if (currentStepIndex > 0) safaColors.goldPrimary else safaColors.textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Back",
                        color = if (currentStepIndex > 0) safaColors.textPrimary else safaColors.textSecondary.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Next Step or Complete Button
                val isLastStep = currentStepIndex == wuduSteps.lastIndex
                Button(
                    onClick = {
                        if (isLastStep) {
                            isRunning = false
                            onScanPrayerMatClick()
                        } else {
                            currentStepIndex++
                        }
                    },
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                    shape = RoundedCornerShape(SafaSpacing.pillRadius)
                ) {
                    if (isLastStep) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            tint = SafaNavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Done & Perfected",
                            color = SafaNavyDark,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Next Step",
                            color = SafaNavyDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = SafaNavyDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
