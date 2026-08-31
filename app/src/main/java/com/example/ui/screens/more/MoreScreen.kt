package com.example.ui.screens.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigation.Screen
import com.example.ui.animation.pressScale
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    viewModel: MoreViewModel,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val safaColors = LocalSafaColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "More & Utilities",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
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
            contentPadding = PaddingValues(bottom = 110.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(SafaSpacing.md)
        ) {
            // Quick Streak / Prayer Stat Card
            item {
                StreakSummaryHeaderCard(
                    streak = uiState.currentStreak,
                    totalPrayers = uiState.totalPrayersOffered,
                    onClick = { onNavigateToRoute(Screen.PrayerStreak.route) }
                )
            }

            item {
                Text(
                    text = "WORSHIP",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.2.sp
                )
            }

            // Worship Items
            item {
                MoreMenuItemCard(
                    icon = Icons.Outlined.SelfImprovement,
                    iconTint = safaColors.goldPrimary,
                    title = "Digital Tasbih",
                    subtitle = "Tap-to-count Dhikr with targets & haptics",
                    badge = "Counter",
                    onClick = { onNavigateToRoute(Screen.Tasbih.route) }
                )
            }

            item {
                MoreMenuItemCard(
                    icon = Icons.Default.MenuBook,
                    iconTint = safaColors.goldPrimary,
                    title = "Hadith Collection",
                    subtitle = "Bukhari, Muslim, 40 Hadith Nawawi",
                    badge = "Authentic",
                    onClick = { onNavigateToRoute(Screen.Hadith.route) }
                )
            }

            item {
                MoreMenuItemCard(
                    icon = Icons.Outlined.School,
                    iconTint = safaColors.goldPrimary,
                    title = "Islamic Learning",
                    subtitle = "Pillars of Islam, Wudu & Salah Guides",
                    badge = "Guides",
                    onClick = { onNavigateToRoute(Screen.IslamicLearning.route) }
                )
            }

            item {
                Text(
                    text = "SEASONAL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.2.sp
                )
            }

            // Seasonal Items
            item {
                MoreMenuItemCard(
                    icon = Icons.Default.NightsStay,
                    iconTint = safaColors.goldPrimary,
                    title = "Fasting & Ramadan",
                    subtitle = "Suhoor/Iftar countdowns and fast tracker",
                    badge = "Tracker",
                    onClick = { onNavigateToRoute(Screen.FastingTracker.route) }
                )
            }

            item {
                Text(
                    text = "TOOLS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.2.sp
                )
            }

            // Tools Items
            item {
                MoreMenuItemCard(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconTint = safaColors.goldPrimary,
                    title = "Zakat Calculator",
                    subtitle = "Nisab thresholds for Gold, Silver & Cash",
                    badge = "2.5%",
                    onClick = { onNavigateToRoute(Screen.ZakatCalculator.route) }
                )
            }

            item {
                MoreMenuItemCard(
                    icon = Icons.Outlined.Mosque,
                    iconTint = safaColors.goldPrimary,
                    title = "Masjid Finder",
                    subtitle = "Nearby mosques with directions & distance",
                    badge = "Nearby",
                    onClick = { onNavigateToRoute(Screen.MasjidFinder.route) }
                )
            }

            item {
                MoreMenuItemCard(
                    icon = Icons.Default.AccountCircle,
                    iconTint = safaColors.goldPrimary,
                    title = "Account & Streak Sync",
                    subtitle = "Google sign in, email login & guest backup",
                    badge = "Streak",
                    onClick = { onNavigateToRoute(Screen.Auth.route) }
                )
            }

            item {
                MoreMenuItemCard(
                    icon = Icons.Default.Settings,
                    iconTint = safaColors.goldPrimary,
                    title = "Settings",
                    subtitle = "Themes, calculation methods, Adhan notifications, ML mat scanner",
                    badge = null,
                    onClick = { onNavigateToRoute(Screen.Settings.route) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun StreakSummaryHeaderCard(
    streak: Int,
    totalPrayers: Int,
    onClick: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(0.97f)
            .clickable { onClick() }
            .testTag("streak_summary_card"),
        shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.5f))
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
                        .size(46.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = if (streak > 0) "$streak Day Prayer Streak!" else "Prayer Consistency",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )
                    Text(
                        text = "$totalPrayers total prayers logged • Tap to view streak",
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

@Composable
private fun MoreMenuItemCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    badge: String?,
    onClick: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(0.98f)
            .clickable { onClick() }
            .testTag("menu_item_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
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
                        .size(42.dp)
                        .background(safaColors.goldGlow, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        if (badge != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(safaColors.goldPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = safaColors.goldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = safaColors.textSecondary,
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = safaColors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
