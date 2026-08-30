package com.example.ui.screens.qibla

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Mosque
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.IslamicApp
import com.example.sensor.QiblaCompassState
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import com.example.ui.permission.SafaPermissionDialog
import com.example.ui.permission.LocationPermissionBanner
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val compassManager = app.qiblaCompassManager
    val settingsRepo = app.settingsRepository
    val permissionManager = app.permissionManager
    val scope = rememberCoroutineScope()

    val compassState by compassManager.compassState.collectAsState()
    val settings by settingsRepo.settingsState.collectAsState()
    val permState by permissionManager.permissionState.collectAsState()
    val safaColors = LocalSafaColors.current

    var showPermissionDialog by remember { mutableStateOf(false) }

    fun refreshGps() {
        if (!permState.hasLocationPermission) {
            showPermissionDialog = true
        } else {
            scope.launch {
                val loc = permissionManager.getDeviceCurrentLocation()
                if (loc != null) {
                    settingsRepo.updateLocation(loc.city, loc.country, loc.latitude, loc.longitude)
                    compassManager.updateCoordinates(loc.latitude, loc.longitude)
                    app.prayerRepository.refreshPrayerTimes(force = true)
                }
            }
        }
    }

    LaunchedEffect(settings.latitude, settings.longitude) {
        compassManager.updateCoordinates(settings.latitude, settings.longitude)
        compassManager.startListening()
    }

    DisposableEffect(Unit) {
        compassManager.startListening()
        onDispose {
            compassManager.stopListening()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Qibla Direction",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = { refreshGps() },
                        modifier = Modifier.testTag("qibla_gps_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Auto GPS Compass Alignment",
                            tint = if (permState.hasLocationPermission) safaColors.goldPrimary else safaColors.textSecondary
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
                .padding(horizontal = SafaSpacing.screenHorizontalPadding)
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Location and Alignment Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!permState.hasLocationPermission) {
                    LocationPermissionBanner(
                        onGrantClick = { showPermissionDialog = true }
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { refreshGps() },
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = if (compassState.isAlignedWithQibla) {
                            if (safaColors.isLuxuryNavy) Color(0xFF0F261D) else IslamicGreenLight
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (compassState.isAlignedWithQibla) IslamicGreen else safaColors.goldBorder.copy(alpha = 0.3f)
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
                            Icon(
                                imageVector = if (compassState.isAlignedWithQibla) Icons.Filled.CheckCircle else Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = if (compassState.isAlignedWithQibla) IslamicGreen else safaColors.goldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (compassState.isAlignedWithQibla) "Facing the Kaaba! ✓" else "${settings.city}, ${settings.country}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (compassState.isAlignedWithQibla) IslamicGreen else safaColors.textPrimary
                                )
                                Text(
                                    text = "Qibla Angle: ${compassState.qiblaBearingDegrees.toInt()}° • Kaaba ${compassState.distanceToMeccaKm.toInt()} km",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = safaColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Central Luxury Compass Dial
            QiblaCompassDial(
                azimuth = compassState.azimuthDegrees,
                qiblaBearing = compassState.qiblaBearingDegrees,
                isAligned = compassState.isAlignedWithQibla
            )

            // Bottom Metrics Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
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
                            AnimatedContent(
                                targetState = "${compassState.azimuthDegrees.toInt()}°",
                                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(180)) },
                                label = "azimuthTextAnim"
                            ) { deg ->
                                Text(
                                    text = deg,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.goldPrimary
                                )
                            }
                            Text(
                                text = "Device Heading",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(safaColors.navyBorder.copy(alpha = 0.4f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedContent(
                                targetState = "${compassState.qiblaBearingDegrees.toInt()}°",
                                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(180)) },
                                label = "qiblaBearingTextAnim"
                            ) { deg ->
                                Text(
                                    text = deg,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.goldChampagne
                                )
                            }
                            Text(
                                text = "Qibla Bearing",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(safaColors.navyBorder.copy(alpha = 0.4f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${compassState.distanceToMeccaKm.toInt()} km",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary
                            )
                            Text(
                                text = "Distance to Mecca",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textSecondary
                            )
                        }
                    }
                }

                Text(
                    text = "Rotate your phone until the golden Kaaba needle aligns with the top indicator",
                    style = MaterialTheme.typography.bodySmall,
                    color = safaColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        SafaPermissionDialog(
            isOpen = showPermissionDialog,
            onDismiss = { showPermissionDialog = false },
            onPermissionsUpdated = { refreshGps() }
        )
    }
}

@Composable
private fun QiblaCompassDial(
    azimuth: Float,
    qiblaBearing: Float,
    isAligned: Boolean
) {
    val safaColors = LocalSafaColors.current
    val animatedAzimuth by animateFloatAsState(
        targetValue = -azimuth,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "compassAzimuth"
    )

    val animatedQiblaAngle by animateFloatAsState(
        targetValue = qiblaBearing - azimuth,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "qiblaAngle"
    )

    val dialBg = if (safaColors.isLuxuryNavy) Color(0xFF0F1A3B) else Color(0xFFFAF7F2)

    Box(
        modifier = Modifier
            .size(310.dp)
            .shadow(16.dp, CircleShape)
            .background(dialBg, CircleShape)
            .testTag("qibla_compass_dial"),
        contentAlignment = Alignment.Center
    ) {
        // Rotating Compass Rose & Degrees
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .rotate(animatedAzimuth)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Outer golden ring
            drawCircle(
                color = safaColors.goldPrimary,
                radius = radius,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Inner subtle ring
            drawCircle(
                color = safaColors.goldBorder.copy(alpha = 0.4f),
                radius = radius * 0.75f,
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Draw 36 tick marks
            for (i in 0 until 360 step 10) {
                val isMajor = i % 90 == 0
                val isMedium = i % 30 == 0
                val tickLength = when {
                    isMajor -> 18.dp.toPx()
                    isMedium -> 12.dp.toPx()
                    else -> 6.dp.toPx()
                }

                val strokeWidth = if (isMajor) 3.dp.toPx() else 1.5.dp.toPx()
                val tickColor = if (isMajor) safaColors.goldPrimary else safaColors.goldBorder.copy(alpha = 0.4f)

                val rad = Math.toRadians(i.toDouble())
                val startX = center.x + (radius - tickLength) * sin(rad).toFloat()
                val startY = center.y - (radius - tickLength) * cos(rad).toFloat()
                val endX = center.x + radius * sin(rad).toFloat()
                val endY = center.y - radius * cos(rad).toFloat()

                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth
                )
            }
        }

        // Qibla Pointer & Kaaba Icon needle
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .rotate(animatedQiblaAngle)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Top Kaaba Needle Pointer
            val needlePath = Path().apply {
                moveTo(center.x, center.y - radius * 0.85f)
                lineTo(center.x - 14.dp.toPx(), center.y - 10.dp.toPx())
                lineTo(center.x, center.y)
                lineTo(center.x + 14.dp.toPx(), center.y - 10.dp.toPx())
                close()
            }

            drawPath(
                path = needlePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        if (isAligned) IslamicGreen else safaColors.goldPrimary,
                        if (isAligned) Color(0xFF1B5E20) else safaColors.goldChampagne
                    )
                )
            )

            // Bottom opposite counter-pointer
            val southPath = Path().apply {
                moveTo(center.x, center.y + radius * 0.65f)
                lineTo(center.x - 10.dp.toPx(), center.y + 8.dp.toPx())
                lineTo(center.x, center.y)
                lineTo(center.x + 10.dp.toPx(), center.y + 8.dp.toPx())
                close()
            }

            drawPath(
                path = southPath,
                color = safaColors.navyBorder.copy(alpha = 0.5f)
            )

            // Center gold hub
            drawCircle(
                color = if (isAligned) IslamicGreen else safaColors.goldPrimary,
                radius = 16.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 7.dp.toPx(),
                center = center
            )
        }

        // Fixed Top Alignment Pointer
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp, 22.dp)
                    .background(
                        if (isAligned) IslamicGreen else safaColors.goldPrimary,
                        RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
            )
        }

        // Center Kaaba Emblem
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(
                    if (isAligned) IslamicGreenLight else safaColors.goldGlow,
                    CircleShape
                )
                .shadow(2.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Mosque,
                contentDescription = "Kaaba",
                tint = if (isAligned) IslamicGreen else safaColors.goldPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
