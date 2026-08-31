package com.example.ui.screens.prayer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PrayerTimeItem
import com.example.ui.permission.LocationPermissionBanner
import com.example.ui.permission.SafaPermissionDialog
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.ArabicTextStyle
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaGoldPrimary
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    viewModel: PrayerViewModel,
    onNavigateToAlarm: () -> Unit,
    onNavigateToQibla: () -> Unit = {},
    onNavigateToWudu: () -> Unit = {},
    onNavigateToTasbih: () -> Unit = {},
    onNavigateToStreak: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val permissionState by viewModel.permissionState.collectAsState()
    val safaColors = LocalSafaColors.current
    var selectedPreviewPrayer by remember { mutableStateOf<String?>(null) }

    // Smooth spinning animation on refresh
    val refreshInfiniteTransition = rememberInfiniteTransition(label = "refreshSpin")
    val refreshRotation by refreshInfiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refreshRot"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            safaColors.goldPrimary.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(1.dp, safaColors.goldPrimary.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "☪",
                                fontSize = 17.sp,
                                color = safaColors.goldPrimary
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Safa",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.goldPrimary,
                                    letterSpacing = 0.6.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• صفا",
                                    style = ArabicTextStyle,
                                    fontSize = 17.sp,
                                    color = safaColors.goldChampagne
                                )
                            }
                            Text(
                                text = uiState.prayerEntity?.hijriDate ?: "Islamic Calendar",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // GPS Auto Detect Button
                    IconButton(
                        onClick = {
                            if (!permissionState.hasLocationPermission) {
                                viewModel.openPermissionDialog()
                            } else {
                                viewModel.fetchGpsLocation()
                            }
                        },
                        modifier = Modifier.testTag("gps_location_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Auto GPS Location",
                            tint = if (permissionState.hasLocationPermission) safaColors.goldPrimary else safaColors.textSecondary
                        )
                    }

                    // Change City Dialog Button
                    IconButton(
                        onClick = { viewModel.openCityDialog() },
                        modifier = Modifier.testTag("location_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Change Location",
                            tint = safaColors.goldPrimary
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = { viewModel.loadData(forceRefresh = true) },
                        modifier = Modifier
                            .testTag("refresh_button")
                            .then(if (uiState.isLoading) Modifier.rotate(refreshRotation) else Modifier)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Prayer Times",
                            tint = safaColors.goldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = SafaSpacing.screenHorizontalPadding),
                contentPadding = PaddingValues(bottom = 110.dp, top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Luxury Location & Calculation Method Chip Bar
                item {
                    LuxuryLocationBar(
                        locationName = "${settings.city}, ${settings.country}",
                        calculationMethod = settings.calculationMethodName,
                        isHanafi = settings.isHanafiAsr,
                        onClickChange = { viewModel.openCityDialog() }
                    )
                }

                // 2. Location Permission Banner if needed
                if (!permissionState.hasLocationPermission) {
                    item {
                        LocationPermissionBanner(
                            onGrantClick = { viewModel.openPermissionDialog() }
                        )
                    }
                }

                // 3. Ultra-Premium Astrolabe Hero Sanctuary (No photos - Pure Sacred Geometry & Gold Elegance)
                item {
                    val nextInfo = uiState.nextPrayerInfo
                    val displayedPrayer = selectedPreviewPrayer?.let { name ->
                        uiState.prayerItems.firstOrNull { it.name.equals(name, ignoreCase = true) }
                    }

                    val activePrayerName = displayedPrayer?.name ?: nextInfo?.nextPrayerName ?: "Fajr"
                    val activeArabicName = displayedPrayer?.arabicName ?: nextInfo?.nextPrayerArabicName ?: "الفجر"
                    val activeTime = displayedPrayer?.time12h ?: nextInfo?.nextPrayerTime12h ?: "05:15 AM"
                    val isNextPrayer = displayedPrayer == null || displayedPrayer.name.equals(nextInfo?.nextPrayerName, ignoreCase = true)
                    val badgeText = if (isNextPrayer) (nextInfo?.formattedRemaining ?: "Upcoming") else "Selected View"

                    PremiumAstrolabeHero(
                        prayerName = activePrayerName,
                        arabicName = activeArabicName,
                        prayerTime = activeTime,
                        badgeText = badgeText,
                        isNext = isNextPrayer,
                        allPrayers = uiState.prayerItems,
                        activePrayerName = activePrayerName,
                        onSelectPrayer = { prayerName ->
                            selectedPreviewPrayer = if (selectedPreviewPrayer == prayerName) null else prayerName
                        }
                    )
                }

                // 4. Daily 5 Prayers Consistency Tracker
                item {
                    val completedCount = uiState.prayerItems.count { it.isCompleted && it.name != "Sunrise" }
                    val totalObligatory = 5
                    DailyPrayerProgressCard(
                        completedCount = completedCount,
                        totalCount = totalObligatory,
                        items = uiState.prayerItems.filter { it.name != "Sunrise" },
                        onViewStreak = onNavigateToStreak
                    )
                }

                // 5. Quick Spiritual Sanctuary Actions (Fajr Alarm, Qibla Compass, Wudu Coach, Dhikr Beads)
                item {
                    QuickSpiritualUtilitiesRow(
                        onAlarmClick = onNavigateToAlarm,
                        onQiblaClick = onNavigateToQibla,
                        onWuduClick = onNavigateToWudu,
                        onTasbihClick = onNavigateToTasbih
                    )
                }

                // 6. Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Prayer Schedule",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary
                            )
                            Text(
                                text = if (settings.isHanafiAsr) "Hanafi Juristic Standard • 5 Daily Fard" else "Standard Shafi/Maliki/Hanbali • 5 Daily Fard",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textSecondary,
                                fontSize = 11.sp
                            )
                        }

                        if (selectedPreviewPrayer != null) {
                            TextButton(
                                onClick = { selectedPreviewPrayer = null },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Reset View",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = safaColors.goldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 7. Sleek Unified 5 Daily Prayers Schedule Card (Compact, Non-Chunky, High Contrast)
                item {
                    SleekUnifiedPrayerScheduleCard(
                        items = uiState.prayerItems,
                        selectedPrayer = selectedPreviewPrayer,
                        onSelectPrayer = { prayerName ->
                            selectedPreviewPrayer = if (selectedPreviewPrayer == prayerName) null else prayerName
                        },
                        onToggleCompleted = { prayerName, isDone ->
                            viewModel.togglePrayerCompleted(prayerName, isDone)
                        }
                    )
                }

                // 8. Daily Reflection
                item {
                    DailyAyatInspirationCard()
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // City Selection Dialog
            if (uiState.showCityDialog) {
                CitySelectionDialog(
                    currentCity = settings.city,
                    currentCountry = settings.country,
                    onDismiss = { viewModel.closeCityDialog() },
                    onSelectCity = { city, country, lat, lng ->
                        viewModel.updateCity(city, country, lat, lng)
                    },
                    onUseGps = {
                        viewModel.closeCityDialog()
                        if (!permissionState.hasLocationPermission) {
                            viewModel.openPermissionDialog()
                        } else {
                            viewModel.fetchGpsLocation()
                        }
                    }
                )
            }

            // Permission Dialog
            SafaPermissionDialog(
                isOpen = uiState.showPermissionDialog,
                onDismiss = { viewModel.closePermissionDialog() },
                onPermissionsUpdated = { viewModel.fetchGpsLocation() }
            )
        }
    }
}

/**
 * Modern Glassmorphic Location & Calculation Bar
 */
@Composable
private fun LuxuryLocationBar(
    locationName: String,
    calculationMethod: String,
    isHanafi: Boolean,
    onClickChange: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickChange() },
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f)),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = locationName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = safaColors.goldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "$calculationMethod • ${if (isHanafi) "Hanafi Asr" else "Standard Asr"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(safaColors.goldGlow, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Change",
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.goldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Ultra-Premium Islamic Astrolabe & Mihrab Arch Hero (Pure Geometry & Gilded Calligraphy - No Stock Photos)
 */
@Composable
private fun PremiumAstrolabeHero(
    prayerName: String,
    arabicName: String,
    prayerTime: String,
    badgeText: String,
    isNext: Boolean,
    allPrayers: List<PrayerTimeItem>,
    activePrayerName: String,
    onSelectPrayer: (String) -> Unit
) {
    val safaColors = LocalSafaColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isNext) 1.012f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Dynamic background gradient per prayer atmospheric phase
    val heroGradient = getPremiumHeroGradient(prayerName, safaColors.isLuxuryNavy)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pulseScale)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                spotColor = safaColors.goldPrimary.copy(alpha = 0.28f)
            ),
        shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = if (isNext) 1.5.dp else 1.dp,
            brush = Brush.linearGradient(
                listOf(
                    safaColors.goldPrimary.copy(alpha = if (isNext) 0.9f else 0.5f),
                    safaColors.goldChampagne.copy(alpha = 0.3f),
                    safaColors.goldPrimary.copy(alpha = if (isNext) 0.8f else 0.4f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = heroGradient))
        ) {
            // Sacred Islamic Astrolabe & Geometric Arch Canvas
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                val w = size.width
                val h = size.height

                // Draw subtle concentric celestial orbits
                val centerOffset = Offset(w * 0.88f, h * 0.35f)
                val goldStroke = safaColors.goldPrimary.copy(alpha = 0.08f)
                drawCircle(color = goldStroke, radius = w * 0.22f, center = centerOffset, style = Stroke(1.dp.toPx()))
                drawCircle(color = goldStroke, radius = w * 0.38f, center = centerOffset, style = Stroke(1.dp.toPx()))
                drawCircle(color = goldStroke, radius = w * 0.54f, center = centerOffset, style = Stroke(1.dp.toPx()))

                // 8-Point Islamic Star rays from center
                for (i in 0 until 8) {
                    val angle = (i * 45f) * (Math.PI / 180f)
                    val r1 = w * 0.15f
                    val r2 = w * 0.45f
                    val x1 = centerOffset.x + (r1 * cos(angle)).toFloat()
                    val y1 = centerOffset.y + (r1 * sin(angle)).toFloat()
                    val x2 = centerOffset.x + (r2 * cos(angle)).toFloat()
                    val y2 = centerOffset.y + (r2 * sin(angle)).toFloat()
                    drawLine(
                        color = safaColors.goldPrimary.copy(alpha = 0.05f),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Subtle Mihrab Arch outline on the left background
                val archPath = Path().apply {
                    moveTo(w * 0.04f, h)
                    lineTo(w * 0.04f, h * 0.35f)
                    cubicTo(
                        w * 0.04f, h * 0.12f,
                        w * 0.22f, h * 0.04f,
                        w * 0.35f, h * 0.04f
                    )
                    cubicTo(
                        w * 0.48f, h * 0.04f,
                        w * 0.66f, h * 0.12f,
                        w * 0.66f, h * 0.35f
                    )
                    lineTo(w * 0.66f, h)
                }
                drawPath(
                    path = archPath,
                    color = safaColors.goldPrimary.copy(alpha = 0.04f),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Foreground Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Row: Status badge & Dynamic Remaining Countdown Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (safaColors.isLuxuryNavy) safaColors.goldPrimary.copy(alpha = 0.22f) else Color(0xFFF3E7CA),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (safaColors.isLuxuryNavy) safaColors.goldPrimary.copy(alpha = 0.6f) else Color(0xFFC59E3F),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isNext) "UPCOMING PRAYER" else "VIEWING DETAILS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else Color(0xFF7A580B),
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Text(
                            text = "• $prayerName",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = safaColors.textPrimary
                        )
                    }

                    // Countdown pill with glowing radial sheen
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (safaColors.isLuxuryNavy) safaColors.goldPrimary.copy(alpha = 0.28f) else Color(0xFFF5EACF),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (safaColors.isLuxuryNavy) safaColors.goldPrimary.copy(alpha = 0.7f) else Color(0xFFC59E3F),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        AnimatedContent(
                            targetState = badgeText,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(250)) },
                            label = "countdownTicker"
                        ) { text ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else Color(0xFF7A580B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else Color(0xFF7A580B),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Middle: Prayer Time Display & Royal Arabic Calligraphy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = prayerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else Color(0xFF8A6510),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = prayerTime,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = safaColors.textPrimary,
                            fontSize = 36.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Arabic Calligraphic Title in High-Contrast Gilded Display
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = arabicName,
                            style = ArabicDisplayStyle,
                            color = if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFF8A6510),
                            fontSize = 36.sp
                        )
                        Text(
                            text = getPrayerSpiritualMeaning(prayerName),
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Bottom: Interactive Astrolabe Celestial Orbit Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (safaColors.isLuxuryNavy) SafaNavyDark.copy(alpha = 0.75f) else Color(0xFFF6EEDF),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val mainPrayers = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
                        mainPrayers.forEach { name ->
                            val isCurrentActive = name.equals(activePrayerName, ignoreCase = true)
                            val icon = getPrayerSymbol(name)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectPrayer(name) }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isCurrentActive) {
                                                if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFFC59E3F)
                                            } else {
                                                if (safaColors.isLuxuryNavy) safaColors.goldPrimary.copy(alpha = 0.08f) else Color(0xFFECE1CB)
                                            },
                                            CircleShape
                                        )
                                        .border(
                                            1.dp,
                                            if (isCurrentActive) safaColors.goldChampagne else safaColors.goldBorder.copy(alpha = 0.25f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = name,
                                        tint = if (isCurrentActive) {
                                            if (safaColors.isLuxuryNavy) SafaNavyDark else Color.White
                                        } else {
                                            if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFF7A580B)
                                        },
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = name.take(3),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrentActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrentActive) (if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFF7A580B)) else safaColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Daily 5 Prayers Consistency Progress Ring Card
 */
@Composable
private fun DailyPrayerProgressCard(
    completedCount: Int,
    totalCount: Int,
    items: List<PrayerTimeItem>,
    onViewStreak: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewStreak() },
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(IslamicGreen.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, IslamicGreen.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = IslamicGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Consistency",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        Text(
                            text = "$completedCount of $totalCount Fard Prayers Completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Streak Log",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.goldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5 Jewel nodes for Fajr, Dhuhr, Asr, Maghrib, Isha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
                prayerNames.forEach { pName ->
                    val isDone = items.firstOrNull { it.name.equals(pName, ignoreCase = true) }?.isCompleted == true
                    val nodeBg by animateColorAsState(
                        targetValue = if (isDone) IslamicGreen else safaColors.navyBorder.copy(alpha = 0.2f),
                        animationSpec = tween(300),
                        label = "jewelBg"
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(nodeBg, CircleShape)
                                .border(
                                    1.dp,
                                    if (isDone) IslamicGreen else safaColors.navyBorder.copy(alpha = 0.4f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = pName.take(1),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.textSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = pName,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isDone) safaColors.textPrimary else safaColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * 4 Quick Spiritual Sanctuary Utilities (Fajr Alarm, Qibla Compass, Wudu Guide, Digital Tasbih)
 */
@Composable
private fun QuickSpiritualUtilitiesRow(
    onAlarmClick: () -> Unit,
    onQiblaClick: () -> Unit,
    onWuduClick: () -> Unit,
    onTasbihClick: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    val actions = listOf(
        QuickActionItem("Fajr Alarm", "Mat Scanner", Icons.Filled.Alarm, safaColors.goldPrimary, onAlarmClick),
        QuickActionItem("Qibla Finder", "Mecca Compass", Icons.Filled.Explore, safaColors.goldPrimary, onQiblaClick),
        QuickActionItem("Wudu Timer", "Sunnah Coach", Icons.Outlined.WaterDrop, Color(0xFF0288D1), onWuduClick),
        QuickActionItem("Tasbih Beads", "Daily Dhikr", Icons.Filled.Star, Color(0xFF7B1FA2), onTasbihClick)
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(actions) { action ->
            Card(
                modifier = Modifier
                    .width(135.dp)
                    .clickable { action.onClick() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(action.tint.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.title,
                            tint = action.tint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = safaColors.textSecondary,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color,
    val onClick: () -> Unit
)

/**
 * Sleek Unified 5 Daily Prayers Schedule Card (Compact, Non-Chunky, High Contrast)
 */
@Composable
private fun SleekUnifiedPrayerScheduleCard(
    items: List<PrayerTimeItem>,
    selectedPrayer: String?,
    onSelectPrayer: (String) -> Unit,
    onToggleCompleted: (String, Boolean) -> Unit
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedPrayer?.equals(item.name, ignoreCase = true) == true
                val isNext = item.isNext
                val isSunrise = item.name.equals("Sunrise", ignoreCase = true)
                val symbol = getPrayerSymbol(item.name)
                val sunnahInfo = getSunnahInfo(item.name)

                // Background tint for active / next / selected prayer
                val rowBackground = when {
                    isSelected -> if (safaColors.isLuxuryNavy) safaColors.navyElevated else Color(0xFFFFF7ED)
                    isNext -> if (safaColors.isLuxuryNavy) safaColors.goldPrimary.copy(alpha = 0.12f) else Color(0xFFFFFBEB)
                    item.isCompleted -> if (safaColors.isLuxuryNavy) Color(0xFF0D2419) else Color(0xFFF0FDF4)
                    else -> Color.Transparent
                }

                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        thickness = 0.6.dp,
                        color = safaColors.goldBorder.copy(alpha = 0.18f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBackground)
                        .clickable { onSelectPrayer(item.name) }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                        .testTag("prayer_card_${item.name.lowercase()}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Left active indicator
                        if (isNext) {
                            Box(
                                modifier = Modifier
                                    .size(width = 3.5.dp, height = 22.dp)
                                    .background(
                                        if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFFB8860B),
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Compact Crafted Medallion Icon
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    color = when {
                                        item.isCompleted -> IslamicGreen.copy(alpha = 0.15f)
                                        isNext || isSelected -> safaColors.goldPrimary.copy(alpha = 0.18f)
                                        else -> if (safaColors.isLuxuryNavy) safaColors.goldPrimary.copy(alpha = 0.08f) else Color(0xFFF6F0E4)
                                    },
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        item.isCompleted -> IslamicGreen.copy(alpha = 0.5f)
                                        isNext || isSelected -> safaColors.goldPrimary
                                        else -> safaColors.goldBorder.copy(alpha = 0.3f)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = symbol,
                                contentDescription = item.name,
                                tint = when {
                                    item.isCompleted -> IslamicGreen
                                    isNext || isSelected -> if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFF946B12)
                                    else -> if (safaColors.isLuxuryNavy) safaColors.goldChampagne else Color(0xFF6B5324)
                                },
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(11.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isNext || isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isNext || isSelected) (if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFF8A6510)) else safaColors.textPrimary,
                                    fontSize = 15.sp
                                )

                                if (isNext) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFFC59E3F),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    ) {
                                        Text(
                                            text = "NEXT",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (safaColors.isLuxuryNavy) SafaNavyDark else Color.White,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.arabicName,
                                    style = ArabicTextStyle,
                                    fontSize = 13.sp,
                                    color = safaColors.textSecondary
                                )

                                if (sunnahInfo != null) {
                                    Text(
                                        text = "• $sunnahInfo",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne.copy(alpha = 0.85f) else Color(0xFF8D733F)
                                    )
                                }
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.time12h,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isNext || isSelected) (if (safaColors.isLuxuryNavy) safaColors.goldPrimary else Color(0xFF8A6510)) else safaColors.textPrimary
                        )

                        if (!isSunrise) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onToggleCompleted(item.name, !item.isCompleted) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = "Mark completed",
                                    tint = if (item.isCompleted) IslamicGreen else safaColors.textSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(32.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun getSunnahInfo(prayerName: String): String? {
    return when (prayerName.lowercase().trim()) {
        "fajr" -> "2 Sunnah Before"
        "dhuhr", "zuhr" -> "4 Before + 2 After"
        "asr" -> "4 Sunnah Before"
        "maghrib" -> "2 Sunnah After"
        "isha", "ishaa" -> "2 After + 3 Witr"
        else -> null
    }
}

private fun getPrayerSpiritualMeaning(prayerName: String): String {
    return when (prayerName.lowercase().trim()) {
        "fajr" -> "Dawn Awakening"
        "sunrise", "shuruq" -> "Morning Light"
        "dhuhr", "zuhr" -> "Midday Clarity"
        "asr" -> "Afternoon Shadow"
        "maghrib" -> "Sunset Gratitude"
        "isha", "ishaa" -> "Nightfall Peace"
        else -> "Sacred Remembrance"
    }
}

private fun getPrayerSymbol(prayerName: String): ImageVector {
    return when (prayerName.lowercase().trim()) {
        "fajr" -> Icons.Filled.Nightlight
        "sunrise", "shuruq" -> Icons.Filled.WbTwilight
        "dhuhr", "zuhr" -> Icons.Filled.WbSunny
        "asr" -> Icons.Filled.WbSunny
        "maghrib" -> Icons.Filled.WbTwilight
        "isha", "ishaa" -> Icons.Filled.Nightlight
        else -> Icons.Filled.WbSunny
    }
}

private fun getPremiumHeroGradient(prayerName: String, isNavy: Boolean): List<Color> {
    return if (isNavy) {
        when (prayerName.lowercase().trim()) {
            "fajr" -> listOf(Color(0xFF0D1829), Color(0xFF13223A), Color(0xFF09101C))
            "sunrise", "shuruq" -> listOf(Color(0xFF1A1208), Color(0xFF281C0E), Color(0xFF120C05))
            "dhuhr", "zuhr" -> listOf(Color(0xFF0E1A2D), Color(0xFF16253F), Color(0xFF0A111E))
            "asr" -> listOf(Color(0xFF18130B), Color(0xFF261D11), Color(0xFF100D07))
            "maghrib" -> listOf(Color(0xFF1C1016), Color(0xFF2B1922), Color(0xFF120A0E))
            "isha", "ishaa" -> listOf(Color(0xFF0A101C), Color(0xFF10192C), Color(0xFF070B13))
            else -> listOf(Color(0xFF101A2B), Color(0xFF18263D), Color(0xFF0A101C))
        }
    } else {
        // Luxury Warm Ivory, Champagne Gold, and Rich Parchment palette (No Blue)
        when (prayerName.lowercase().trim()) {
            "fajr" -> listOf(Color(0xFFFFFDF9), Color(0xFFF7F0E3), Color(0xFFEBE0CD))
            "sunrise", "shuruq" -> listOf(Color(0xFFFFFBF0), Color(0xFFFDF0DA), Color(0xFFF5DEBA))
            "dhuhr", "zuhr" -> listOf(Color(0xFFFFFDF7), Color(0xFFFAF2E4), Color(0xFFEFE4C9))
            "asr" -> listOf(Color(0xFFFFFDF8), Color(0xFFF7EFE0), Color(0xFFEEDEC2))
            "maghrib" -> listOf(Color(0xFFFFFBF5), Color(0xFFFCEFE5), Color(0xFFF5DDCE))
            "isha", "ishaa" -> listOf(Color(0xFFFFFDF9), Color(0xFFF6F0E4), Color(0xFFECE0CA))
            else -> listOf(Color(0xFFFFFDF7), Color(0xFFFAF2E4), Color(0xFFEFE4C9))
        }
    }
}

/**
 * Daily Ayat & Hadith Reflection of the Day Card
 */
@Composable
private fun DailyAyatInspirationCard() {
    val safaColors = LocalSafaColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "DAILY REFLECTION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "«حَافِظُوا عَلَى الصَّلَوَاتِ وَالصَّلَاةِ الْوُسْطَىٰ وَقُومُوا لِلَّهِ قَانِتِينَ»",
                style = ArabicDisplayStyle,
                fontSize = 18.sp,
                color = safaColors.textPrimary,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"Guard strictly your prayers, especially the middle prayer, and stand before Allah with devotion.\"",
                style = MaterialTheme.typography.bodyMedium,
                color = safaColors.textSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Surah Al-Baqarah (2:238)",
                style = MaterialTheme.typography.labelSmall,
                color = safaColors.goldPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CitySelectionDialog(
    currentCity: String,
    currentCountry: String,
    onDismiss: () -> Unit,
    onSelectCity: (String, String, Double, Double) -> Unit,
    onUseGps: () -> Unit = {}
) {
    var customCity by remember { mutableStateOf("") }
    var customCountry by remember { mutableStateOf("") }
    val safaColors = LocalSafaColors.current

    val popularCities = listOf(
        Triple("Mecca", "Saudi Arabia", Pair(21.4225, 39.8262)),
        Triple("Medina", "Saudi Arabia", Pair(24.5247, 39.5692)),
        Triple("Jerusalem", "Palestine", Pair(31.7683, 35.2137)),
        Triple("London", "United Kingdom", Pair(51.5074, -0.1278)),
        Triple("New York", "United States", Pair(40.7128, -74.0060)),
        Triple("Istanbul", "Turkey", Pair(41.0082, 28.9784)),
        Triple("Cairo", "Egypt", Pair(30.0444, 31.2357)),
        Triple("Dubai", "United Arab Emirates", Pair(25.2048, 55.2708)),
        Triple("Karachi", "Pakistan", Pair(24.8607, 67.0011)),
        Triple("Kuala Lumpur", "Malaysia", Pair(3.1390, 101.6869)),
        Triple("Jakarta", "Indonesia", Pair(-6.2088, 106.8456)),
        Triple("Toronto", "Canada", Pair(43.6532, -79.3832))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Prayer Location",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = safaColors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Auto GPS Location option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUseGps() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = safaColors.goldGlow.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(1.dp, safaColors.goldPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = safaColors.goldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Auto-Detect My GPS Location",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary
                            )
                            Text(
                                text = "Uses device sensors for exact coordinates",
                                style = MaterialTheme.typography.bodySmall,
                                color = safaColors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Text(
                    text = "Popular Locations:",
                    style = MaterialTheme.typography.labelMedium,
                    color = safaColors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                LazyColumn(
                    modifier = Modifier.height(170.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(popularCities) { (city, country, coords) ->
                        val isSelected = city.equals(currentCity, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectCity(city, country, coords.first, coords.second)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) safaColors.goldGlow else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$city, $country",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) safaColors.goldPrimary else safaColors.textPrimary
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = safaColors.goldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Or Enter Custom City:",
                    style = MaterialTheme.typography.labelMedium,
                    color = safaColors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = customCity,
                    onValueChange = { customCity = it },
                    label = { Text("City Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customCountry,
                    onValueChange = { customCountry = it },
                    label = { Text("Country") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customCity.isNotBlank()) {
                        onSelectCity(customCity.trim(), customCountry.ifBlank { "World" }.trim(), 0.0, 0.0)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                enabled = customCity.isNotBlank()
            ) {
                Text("Set Custom City", color = SafaNavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = safaColors.textSecondary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
