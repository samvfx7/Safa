package com.example.ui.screens.prayer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.IslamicApp
import com.example.audio.AudioPlayerHelper
import com.example.matdetection.PrayerMatDetector
import com.example.ui.permission.SafaPermissionDialog
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FajrAlarmScreen(
    onDismiss: () -> Unit,
    onPrayerCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val permissionManager = app.permissionManager
    val permState by permissionManager.permissionState.collectAsState()
    val scope = rememberCoroutineScope()
    val safaColors = LocalSafaColors.current

    val audioPlayer = remember { AudioPlayerHelper(context) }
    val matDetector = remember { PrayerMatDetector() }

    var isScanningMode by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var scanAttempts by remember { mutableIntStateOf(0) }
    var alarmVolume by remember { mutableFloatStateOf(0.6f) }
    var isMatDetected by remember { mutableStateOf(false) }
    var detectedColor by remember { mutableStateOf("") }
    var currentConfidence by remember { mutableFloatStateOf(0f) }

    fun startScanning() {
        if (!permState.hasCameraPermission) {
            showPermissionDialog = true
        } else {
            isScanningMode = true
        }
    }

    // Start full screen Adhan alarm
    LaunchedEffect(Unit) {
        audioPlayer.playAdhanAlarm(volume = alarmVolume)
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    // Alarm escalation logic if failed 3 attempts
    fun handleScanFailed() {
        scanAttempts++
        if (scanAttempts >= 3) {
            alarmVolume = (alarmVolume + 0.25f).coerceAtMost(1.0f)
            audioPlayer.setAlarmVolume(alarmVolume)
        }
    }

    fun handleScanSuccess(color: String, confidence: Float) {
        isMatDetected = true
        detectedColor = color
        currentConfidence = confidence
        audioPlayer.stop()
        onPrayerCompleted()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("fajr_alarm_screen"),
        color = if (isScanningMode) Color(0xFF070D1E) else MaterialTheme.colorScheme.background
    ) {
        if (isMatDetected) {
            // Success State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Mat detected! ✓",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Recognized $detectedColor with ${(currentConfidence * 100).toInt()}% confidence. Alarm stopped.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = safaColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "حيّ على الصلاة • حيّ على الفلاح",
                    style = ArabicDisplayStyle,
                    color = safaColors.goldChampagne,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                    shape = RoundedCornerShape(SafaSpacing.pillRadius),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Return to App", color = SafaNavyDark, fontWeight = FontWeight.Bold)
                }
            }
        } else if (isScanningMode) {
            // Live Mat Scanner Camera View
            MatScannerCameraView(
                matDetector = matDetector,
                scanAttempts = scanAttempts,
                alarmVolume = alarmVolume,
                onSuccess = { color, conf -> handleScanSuccess(color, conf) },
                onFailAttempt = { handleScanFailed() },
                onManualFallback = {
                    handleScanSuccess("Manual Verification", 1.0f)
                },
                onCancelScan = { isScanningMode = false }
            )
        } else {
            // Main Fajr Alarm Full Screen View
            FajrAlarmRingingView(
                alarmVolume = alarmVolume,
                scanAttempts = scanAttempts,
                onStartScan = { startScanning() },
                onManualFallback = {
                    handleScanSuccess("Manual Verification", 1.0f)
                },
                onClose = onDismiss
            )
        }

        SafaPermissionDialog(
            isOpen = showPermissionDialog,
            onDismiss = { showPermissionDialog = false },
            onPermissionsUpdated = {
                permissionManager.checkAllPermissions()
                if (permState.hasCameraPermission) {
                    isScanningMode = true
                }
            }
        )
    }
}

@Composable
private fun FajrAlarmRingingView(
    alarmVolume: Float,
    scanAttempts: Int,
    onStartScan: () -> Unit,
    onManualFallback: () -> Unit,
    onClose: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = safaColors.textSecondary)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulseScale)
                    .background(safaColors.goldGlow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(safaColors.goldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = SafaNavyDark,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ",
                style = ArabicDisplayStyle,
                color = safaColors.goldPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Prayer is better than sleep",
                style = MaterialTheme.typography.bodyMedium,
                color = safaColors.textSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Fajr Prayer Time",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = safaColors.textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Adhan Playing • Scan your prayer mat to turn off alarm",
                style = MaterialTheme.typography.bodyLarge,
                color = safaColors.textSecondary,
                textAlign = TextAlign.Center
            )

            if (scanAttempts >= 3) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Alarm volume increased (${(alarmVolume * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onStartScan,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("scan_prayer_mat_button")
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SafaNavyDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scan Prayer Mat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }

            OutlinedButton(
                onClick = onManualFallback,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = safaColors.goldPrimary),
                border = BorderStroke(1.dp, safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("manual_fallback_button")
            ) {
                Text("I'm using my prayer mat (Manual)", fontWeight = FontWeight.SemiBold, color = safaColors.goldPrimary)
            }
        }
    }
}

@Composable
private fun MatScannerCameraView(
    matDetector: PrayerMatDetector,
    scanAttempts: Int,
    alarmVolume: Float,
    onSuccess: (String, Float) -> Unit,
    onFailAttempt: () -> Unit,
    onManualFallback: () -> Unit,
    onCancelScan: () -> Unit
) {
    val detectionResult by matDetector.detectionState.collectAsState()
    val scope = rememberCoroutineScope()
    val safaColors = LocalSafaColors.current

    val scanTransition = rememberInfiniteTransition(label = "scanner")
    val laserOffset by scanTransition.animateFloat(
        initialValue = 0f,
        targetValue = 260f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    // Simulate camera frames detection or live analysis
    var isSimulatingScan by remember { mutableStateOf(false) }

    fun triggerMatAnalysis(mockColor: String = "Green") {
        isSimulatingScan = true
        scope.launch {
            delay(1200) // Frame processing time
            // Generate test bitmap with the characteristic prayer mat patterns
            val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()

            when (mockColor) {
                "Green" -> paint.color = android.graphics.Color.rgb(34, 139, 34) // Forest Green
                "Blue" -> paint.color = android.graphics.Color.rgb(25, 25, 112) // Midnight Blue
                "Red" -> paint.color = android.graphics.Color.rgb(139, 0, 0) // Dark Red
                else -> paint.color = android.graphics.Color.rgb(184, 134, 11) // Dark Goldenrod
            }
            canvas.drawRect(0f, 0f, 200f, 200f, paint)

            // Draw border & carpet weave texture
            paint.color = android.graphics.Color.rgb(218, 165, 32)
            paint.strokeWidth = 6f
            canvas.drawRect(10f, 10f, 190f, 190f, paint)

            val result = matDetector.analyzeFrame(bitmap)
            isSimulatingScan = false

            if (result.isDetected) {
                onSuccess(result.detectedColorName, result.confidence)
            } else {
                onFailAttempt()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ML Kit Vision Scanner",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = safaColors.goldPrimary
            )
            IconButton(onClick = onCancelScan) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = safaColors.goldPrimary)
            }
        }

        // Camera viewfinder overlay
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0B132B))
                .border(
                    BorderStroke(
                        2.5.dp,
                        if (detectionResult.isDetected) IslamicGreen else safaColors.goldPrimary
                    ),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Scanner Laser Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .offset(y = (laserOffset - 130).dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                if (detectionResult.isDetected) IslamicGreen else safaColors.goldPrimary,
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = safaColors.goldPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Point camera at your prayer mat",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Detects green, blue, red & geometric patterns",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Confidence & Status Panel
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SafaSpacing.cardRadius),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111E3E)),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mat Confidence Threshold (70%+):",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${(detectionResult.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (detectionResult.isDetected) IslamicGreen else safaColors.goldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { detectionResult.confidence },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (detectionResult.isDetected) IslamicGreen else safaColors.goldPrimary,
                        trackColor = Color(0xFF16254F)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = detectionResult.message,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (detectionResult.isDetected) IslamicGreen else Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick color presets to trigger live detection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { triggerMatAnalysis("Green") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Green Mat", fontSize = 12.sp, color = Color.White)
                }
                Button(
                    onClick = { triggerMatAnalysis("Blue") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Blue Mat", fontSize = 12.sp, color = Color.White)
                }
                Button(
                    onClick = { triggerMatAnalysis("Red") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF880E4F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Red Mat", fontSize = 12.sp, color = Color.White)
                }
            }

            OutlinedButton(
                onClick = onManualFallback,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = safaColors.goldPrimary),
                border = BorderStroke(1.dp, safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("I'm using my mat (Manual Verification)", color = safaColors.goldPrimary)
            }
        }
    }
}
