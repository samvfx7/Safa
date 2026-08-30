package com.example.ui.screens.more

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    viewModel: MoreViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasbihState by viewModel.tasbihState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val safaColors = LocalSafaColors.current

    val progress = (tasbihState.currentCount.toFloat() / tasbihState.targetCount.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "tasbihProgress"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Digital Tasbih",
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
                actions = {
                    IconButton(
                        onClick = { viewModel.resetTasbih() },
                        modifier = Modifier.testTag("reset_tasbih_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Counter",
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
                .padding(horizontal = SafaSpacing.screenHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Preset Selection Horizontal Chips
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.tasbihPresets.forEach { preset ->
                        val isSelected = preset.name == tasbihState.selectedDhikr.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectTasbihPreset(preset) },
                            label = {
                                Text(
                                    text = "${preset.name} (${preset.defaultTarget})",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = safaColors.goldPrimary,
                                selectedLabelColor = SafaNavyDark,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = safaColors.textPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(SafaSpacing.pillRadius)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SafaSpacing.sm))

                // Current Selected Dhikr Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SafaSpacing.cardContentPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tasbihState.selectedDhikr.arabic,
                            style = ArabicDisplayStyle,
                            fontSize = 28.sp,
                            color = safaColors.goldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tasbihState.selectedDhikr.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        Text(
                            text = tasbihState.selectedDhikr.translation,
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary
                        )
                    }
                }
            }

            // Giant Tap Counter Button with Progress Ring
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.94f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "tasbihButtonScale"
            )

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .shadow(
                        elevation = if (isPressed) 6.dp else 16.dp,
                        shape = CircleShape,
                        spotColor = safaColors.goldPrimary.copy(alpha = 0.4f)
                    )
                    .clip(CircleShape)
                    .background(if (safaColors.isLuxuryNavy) Color(0xFF0F1A3B) else Color(0xFFFAFAFA))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(bounded = true, color = safaColors.goldPrimary.copy(alpha = 0.3f)),
                        onClick = { viewModel.incrementTasbih() }
                    )
                    .testTag("tasbih_tap_button"),
                contentAlignment = Alignment.Center
            ) {
                // Background and Progress Arc
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    val stroke = 10.dp.toPx()
                    drawCircle(
                        color = safaColors.navyBorder.copy(alpha = 0.3f),
                        style = Stroke(width = stroke)
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(safaColors.goldChampagne, safaColors.goldPrimary, safaColors.goldChampagne)
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = tasbihState.currentCount,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInVertically { height -> height / 2 } + fadeIn(tween(140))) togetherWith
                                        (slideOutVertically { height -> -height / 2 } + fadeOut(tween(100)))
                            } else {
                                (slideInVertically { height -> -height / 2 } + fadeIn(tween(140))) togetherWith
                                        (slideOutVertically { height -> height / 2 } + fadeOut(tween(100)))
                            }
                        },
                        label = "tasbihCountAnimated"
                    ) { count ->
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldPrimary
                        )
                    }
                    Text(
                        text = "Target: ${tasbihState.targetCount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = safaColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TAP TO COUNT",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        color = safaColors.goldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Stats / Laps Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SafaSpacing.cardContentPadding),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${tasbihState.totalLaps}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.goldPrimary
                            )
                            Text(
                                text = "Completed Laps",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(safaColors.navyBorder.copy(alpha = 0.4f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.tasbihHistory.size}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary
                            )
                            Text(
                                text = "History Entries",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textSecondary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetTasbih() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = safaColors.textPrimary),
                        border = BorderStroke(1.dp, safaColors.goldPrimary),
                        shape = RoundedCornerShape(SafaSpacing.cardRadius),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = safaColors.goldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset", color = safaColors.textPrimary)
                    }
                }
            }
        }
    }
}
