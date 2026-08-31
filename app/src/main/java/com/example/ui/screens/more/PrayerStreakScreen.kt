package com.example.ui.screens.more

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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.IslamicApp
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerStreakScreen(
    viewModel: MoreViewModel,
    onBack: () -> Unit,
    onNavigateToAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val safaColors = LocalSafaColors.current
    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val authUser by app.authRepository.currentUser.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Prayer Consistency & Streak",
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
            // Hero Streak Card
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
                                        listOf(safaColors.navyElevated, safaColors.navySurface, safaColors.navyBackground)
                                    } else {
                                        listOf(safaColors.navyElevated, safaColors.navySurface)
                                    }
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(safaColors.goldGlow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "${uiState.currentStreak} Days",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (safaColors.isLuxuryNavy) safaColors.goldPrimary else safaColors.textGold
                            )
                            Text(
                                text = "Current Consecutive Prayer Streak",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne.copy(alpha = 0.9f) else safaColors.textSecondary
                            )
                        }
                    }
                }
            }

            // Streak Account & Backup Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(SafaSpacing.cardRadius))
                        .clickable { onNavigateToAuth() }
                        .testTag("streak_auth_sync_card"),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SafaSpacing.cardContentPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(safaColors.goldGlow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (authUser != null) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (authUser != null) {
                                        "Streak Synced: ${authUser?.displayName ?: "Active"}"
                                    } else {
                                        "Backup & Protect Your Streak"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.textPrimary
                                )
                                Text(
                                    text = if (authUser != null) {
                                        "Linked via ${authUser?.provider?.name ?: "Account"} • Preserved safely"
                                    } else {
                                        "Sign in with Google, Email, or Guest"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = safaColors.textSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = safaColors.goldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBoxCard(
                        title = "Total Prayers",
                        value = "${uiState.totalPrayersOffered}",
                        modifier = Modifier.weight(1f)
                    )
                    StatBoxCard(
                        title = "Completed Days",
                        value = "${uiState.prayerLogs.count { it.completedCount == 5 }}",
                        modifier = Modifier.weight(1f)
                    )
                    StatBoxCard(
                        title = "Consistency",
                        value = if (uiState.prayerLogs.isEmpty()) "100%" else "${(uiState.prayerLogs.count { it.completedCount >= 4 } * 100 / uiState.prayerLogs.size.coerceAtLeast(1))}%",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Recent Prayer Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary
                )
            }

            if (uiState.prayerLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(SafaSpacing.cardRadius),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No logged prayer days yet. Tap on the daily prayer circles in Prayer Times to record your prayers!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = safaColors.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(uiState.prayerLogs) { log ->
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = log.date,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.textPrimary
                                )
                                Text(
                                    text = "Prayers: ${log.completedCount} / 5 completed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = safaColors.textSecondary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                PrayerDot(name = "F", isDone = log.fajrDone)
                                PrayerDot(name = "D", isDone = log.dhuhrDone)
                                PrayerDot(name = "A", isDone = log.asrDone)
                                PrayerDot(name = "M", isDone = log.maghribDone)
                                PrayerDot(name = "I", isDone = log.ishaDone)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatBoxCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = safaColors.goldPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = safaColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PrayerDot(name: String, isDone: Boolean) {
    val safaColors = LocalSafaColors.current
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                if (isDone) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDone) SafaNavyDark else safaColors.textSecondary,
            fontSize = 11.sp
        )
    }
}
