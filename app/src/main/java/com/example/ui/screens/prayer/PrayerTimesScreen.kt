package com.example.ui.screens.prayer

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.repository.PrayerTimeItem
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.ArabicTextStyle
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaGoldPrimary
import com.example.ui.theme.SafaGoldPrimary
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import com.example.ui.permission.SafaPermissionDialog
import com.example.ui.permission.LocationPermissionBanner
import androidx.compose.material.icons.filled.MyLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    viewModel: PrayerViewModel,
    onNavigateToAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val permissionState by viewModel.permissionState.collectAsState()
    val safaColors = LocalSafaColors.current
    var selectedPreviewPrayer by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Safa",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.goldPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• صفا",
                                style = ArabicTextStyle,
                                fontSize = 16.sp,
                                color = safaColors.goldChampagne
                            )
                        }
                        Text(
                            text = uiState.prayerEntity?.hijriDate ?: "Islamic Calendar",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary
                        )
                    }
                },
                actions = {
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
                    IconButton(
                        onClick = { viewModel.loadData(forceRefresh = true) },
                        modifier = Modifier.testTag("refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
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
                contentPadding = PaddingValues(bottom = 110.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(SafaSpacing.md)
            ) {
                // Location & Hijri Badge
                item {
                    LocationHeaderCard(
                        locationName = "${settings.city}, ${settings.country}",
                        calculationMethod = settings.calculationMethodName,
                        onClickChangeLocation = { viewModel.openCityDialog() }
                    )
                }

                // If location permission is not granted, show prominent luxury banner
                if (!permissionState.hasLocationPermission) {
                    item {
                        LocationPermissionBanner(
                            onGrantClick = { viewModel.openPermissionDialog() }
                        )
                    }
                }

                // Dynamic Prayer Hero Banner (changes photo per prayer)
                item {
                    val nextInfo = uiState.nextPrayerInfo
                    val displayedPrayer = selectedPreviewPrayer?.let { name ->
                        uiState.prayerItems.firstOrNull { it.name.equals(name, ignoreCase = true) }
                    }

                    if (displayedPrayer != null && nextInfo != null) {
                        // User selected a specific prayer to view
                        PrayerHeroCard(
                            prayerName = displayedPrayer.name,
                            arabicName = displayedPrayer.arabicName,
                            prayerTime = displayedPrayer.time12h,
                            badgeText = if (displayedPrayer.name.equals(nextInfo.nextPrayerName, ignoreCase = true)) {
                                nextInfo.formattedRemaining
                            } else {
                                "Tap to view next"
                            },
                            isNext = displayedPrayer.name.equals(nextInfo.nextPrayerName, ignoreCase = true),
                            onCardClick = {
                                selectedPreviewPrayer = null // Reset back to next prayer
                            }
                        )
                    } else if (nextInfo != null) {
                        // Default: Automatically show upcoming prayer with its unique photo
                        PrayerHeroCard(
                            prayerName = nextInfo.nextPrayerName,
                            arabicName = nextInfo.nextPrayerArabicName,
                            prayerTime = nextInfo.nextPrayerTime12h,
                            badgeText = nextInfo.formattedRemaining,
                            isNext = true,
                            onCardClick = {}
                        )
                    } else if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(SafaSpacing.cardRadius)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = safaColors.goldPrimary)
                        }
                    }
                }

                // Fajr Prayer Mat Alarm Banner
                item {
                    FajrAlarmBannerCard(
                        onLaunchAlarm = onNavigateToAlarm
                    )
                }

                // 5 Daily Prayers Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Prayers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        Text(
                            text = "Tap to preview photo",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary
                        )
                    }
                }

                // Prayer List Items
                items(uiState.prayerItems) { prayerItem ->
                    val isSelected = selectedPreviewPrayer?.equals(prayerItem.name, ignoreCase = true) == true
                    PrayerTimeRowCard(
                        item = prayerItem,
                        isSelected = isSelected,
                        onCardClick = {
                            selectedPreviewPrayer = if (isSelected) null else prayerItem.name
                        },
                        onToggleCompleted = { isDone ->
                            viewModel.togglePrayerCompleted(prayerItem.name, isDone)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Fading edge at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                Color.Transparent
                            )
                        )
                    )
                    .align(Alignment.TopCenter)
            )
            
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

            SafaPermissionDialog(
                isOpen = uiState.showPermissionDialog,
                onDismiss = { viewModel.closePermissionDialog() },
                onPermissionsUpdated = { viewModel.fetchGpsLocation() }
            )
        }
    }
}

@Composable
private fun LocationHeaderCard(
    locationName: String,
    calculationMethod: String,
    onClickChangeLocation: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickChangeLocation() },
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
                        .size(38.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )
                    Text(
                        text = calculationMethod,
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textSecondary,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = "Change",
                style = MaterialTheme.typography.labelSmall,
                color = safaColors.goldPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PrayerHeroCard(
    prayerName: String,
    arabicName: String,
    prayerTime: String,
    badgeText: String,
    isNext: Boolean,
    onCardClick: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isNext) 1.018f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val photoRes = getPrayerPhotoResource(prayerName)
    val gradientColors = getPrayerAtmosphereGradient(prayerName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pulseScale)
            .clickable(enabled = !isNext) { onCardClick() }
            .shadow(10.dp, RoundedCornerShape(SafaSpacing.cardRadiusLarge)),
        shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.5.dp, safaColors.goldPrimary.copy(alpha = if (isNext) 0.7f else 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Animated Crossfade between prayer photos
            Crossfade(
                targetState = photoRes,
                animationSpec = tween(500),
                label = "prayerPhotoFade"
            ) { targetRes ->
                Image(
                    painter = painterResource(id = targetRes),
                    contentDescription = "$prayerName photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Atmospheric Lighting Gradient Overlay tailored to prayer time
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(colors = gradientColors)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SafaSpacing.cardContentPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = if (isNext) "$prayerName in" else prayerName,
                            style = MaterialTheme.typography.labelMedium,
                            color = safaColors.goldChampagne.copy(alpha = 0.95f),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = prayerTime,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDFBF7)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                safaColors.goldPrimary.copy(alpha = 0.35f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        AnimatedContent(
                            targetState = badgeText,
                            transitionSpec = {
                                fadeIn(tween(300)) togetherWith fadeOut(tween(250))
                            },
                            label = "countdownTextAnim"
                        ) { targetText ->
                            Text(
                                text = targetText,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.goldChampagne
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = if (isNext) "Next: $prayerName Prayer" else "$prayerName Prayer",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEDE0D1)
                        )
                        if (!isNext) {
                            Text(
                                text = "Tap card to return to next",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.goldChampagne.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = arabicName,
                        style = ArabicDisplayStyle,
                        color = safaColors.goldPrimary,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@DrawableRes
private fun getPrayerPhotoResource(prayerName: String): Int {
    return when (prayerName.lowercase().trim()) {
        "fajr" -> R.drawable.img_prayer_fajr_1788083073929
        "sunrise", "shuruq" -> R.drawable.img_prayer_sunrise_1788083086402
        "dhuhr", "zuhr" -> R.drawable.img_prayer_dhuhr_1788083100162
        "asr" -> R.drawable.img_prayer_asr_1788083110729
        "maghrib" -> R.drawable.img_prayer_maghrib_1788083125689
        "isha", "ishaa" -> R.drawable.img_prayer_isha_1788083137794
        else -> R.drawable.mosque_sunset_hero_1788032664370
    }
}

private fun getPrayerAtmosphereGradient(prayerName: String): List<Color> {
    return when (prayerName.lowercase().trim()) {
        "fajr" -> listOf(
            Color(0x660B132B),
            Color(0xAA1C2541),
            Color(0xEE0B132B)
        )
        "sunrise", "shuruq" -> listOf(
            Color(0x4D2A1805),
            Color(0x994A2C0F),
            Color(0xEE2C1A09)
        )
        "dhuhr", "zuhr" -> listOf(
            Color(0x400D253A),
            Color(0x88133E60),
            Color(0xEE091A2B)
        )
        "asr" -> listOf(
            Color(0x552E1D10),
            Color(0x99482C15),
            Color(0xEE26170B)
        )
        "maghrib" -> listOf(
            Color(0x55381318),
            Color(0x994D1B22),
            Color(0xEE240E14)
        )
        "isha", "ishaa" -> listOf(
            Color(0x66080E1E),
            Color(0xAA0D1730),
            Color(0xEE060A14)
        )
        else -> listOf(
            Color(0x661A120B),
            Color(0xAA2C1E14),
            Color(0xE61E140C)
        )
    }
}

@Composable
private fun FajrAlarmBannerCard(
    onLaunchAlarm: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLaunchAlarm() },
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(safaColors.goldGlow, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Alarm,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Fajr Prayer Mat Alarm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )
                    Text(
                        text = "ML Vision Mat Scanner & Adhan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = safaColors.textSecondary
                    )
                }
            }

            Button(
                onClick = onLaunchAlarm,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Test", color = SafaNavyDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PrayerTimeRowCard(
    item: PrayerTimeItem,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    val isNext = item.isNext
    val isSunrise = item.name == "Sunrise"
    val safaColors = LocalSafaColors.current
    val photoRes = getPrayerPhotoResource(item.name)

    val targetBackgroundColor = when {
        isSelected -> if (safaColors.isLuxuryNavy) safaColors.navyElevated else Color(0xFFFFF3E0)
        isNext -> if (safaColors.isLuxuryNavy) safaColors.navyElevated else Color(0xFFFFF7ED)
        item.isCompleted -> if (safaColors.isLuxuryNavy) Color(0xFF0F261D) else IslamicGreenLight
        else -> MaterialTheme.colorScheme.surface
    }

    val targetBorderColor = when {
        isSelected -> safaColors.goldPrimary
        isNext -> safaColors.goldPrimary
        item.isCompleted -> IslamicGreen
        else -> safaColors.navyBorder.copy(alpha = 0.4f)
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(300),
        label = "rowCardBgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(300),
        label = "rowCardBorderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("prayer_card_${item.name.lowercase()}"),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(if (isNext || isSelected) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Prayer Photo Thumbnail with subtle rounded border
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            if (isNext || isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = photoRes),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0x66000000))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isNext || isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isNext || isSelected) safaColors.goldPrimary else safaColors.textPrimary
                        )
                        if (isNext) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(safaColors.goldPrimary, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "NEXT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = SafaNavyDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(safaColors.goldGlow, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VIEWING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = safaColors.goldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = item.arabicName,
                        style = ArabicTextStyle,
                        fontSize = 15.sp,
                        color = safaColors.textSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.time12h,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isNext || isSelected) safaColors.goldPrimary else safaColors.textPrimary
                )

                if (!isSunrise) {
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { onToggleCompleted(!item.isCompleted) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = "Mark completed",
                            tint = if (item.isCompleted) IslamicGreen else safaColors.textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
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
                // GPS Auto-Detect Button
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
                    modifier = Modifier.height(180.dp),
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

                Spacer(modifier = Modifier.height(6.dp))

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
