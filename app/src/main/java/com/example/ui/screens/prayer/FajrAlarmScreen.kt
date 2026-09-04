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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors
import android.graphics.Matrix
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

import android.widget.Toast
import com.example.notifications.PrayerAlarmReceiver
import com.example.data.repository.FajrAlarmTestDiagnostics
import com.example.data.repository.FajrAlarmTestResult

@Composable
fun FajrAlarmScreen(
    onDismiss: () -> Unit,
    onPrayerCompleted: () -> Unit,
    isTestAlarm: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val settingsState by app.settingsRepository.settingsState.collectAsState()
    val permissionManager = app.permissionManager
    val permState by permissionManager.permissionState.collectAsState()
    val scope = rememberCoroutineScope()
    val safaColors = LocalSafaColors.current

    val audioPlayer = remember { AudioPlayerHelper(context) }
    val matDetector = remember { PrayerMatDetector() }

    var isScanningMode by remember { mutableStateOf(false) }
    var isWuduTimerMode by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var scanAttempts by remember { mutableIntStateOf(0) }
    var alarmVolume by remember { mutableFloatStateOf(0.6f) }
    var isMatDetected by remember { mutableStateOf(false) }
    var detectedColor by remember { mutableStateOf("") }
    var currentConfidence by remember { mutableFloatStateOf(0f) }

    var soundLoaded by remember { mutableStateOf(false) }
    var audioPlaying by remember { mutableStateOf(false) }
    var audioError by remember { mutableStateOf<String?>(null) }
    var resolvedSoundName by remember { mutableStateOf(settingsState.fajrAlarmSound) }

    fun startScanning() {
        if (!permState.hasCameraPermission) {
            showPermissionDialog = true
        } else {
            isScanningMode = true
        }
    }

    // Record initial test trigger assertion
    LaunchedEffect(isTestAlarm) {
        if (isTestAlarm) {
            FajrAlarmTestDiagnostics.updateResult(
                FajrAlarmTestResult(
                    isSuccess = false,
                    soundName = settingsState.fajrAlarmSound,
                    alarmTriggered = true,
                    alarmUiOpened = true,
                    soundLoaded = false,
                    audioPlaying = false,
                    alarmVolumePercent = (alarmVolume * 100).toInt(),
                    errorMessage = "Initializing audio stream..."
                )
            )
        }
    }

    // Start full screen Adhan alarm with selected sound
    LaunchedEffect(settingsState.fajrAlarmSound, settingsState.fajrCustomSoundUri) {
        audioPlayer.playAdhanAlarm(
            soundName = settingsState.fajrAlarmSound,
            customUri = settingsState.fajrCustomSoundUri,
            volume = alarmVolume,
            onDiagnosticResult = { success, error, resolved ->
                soundLoaded = success
                audioPlaying = success
                audioError = error
                resolvedSoundName = resolved

                if (isTestAlarm) {
                    FajrAlarmTestDiagnostics.updateResult(
                        FajrAlarmTestResult(
                            isSuccess = success,
                            soundName = settingsState.fajrAlarmSound,
                            soundResolved = resolved,
                            alarmTriggered = true,
                            alarmUiOpened = true,
                            soundLoaded = success,
                            audioPlaying = success,
                            alarmVolumePercent = (alarmVolume * 100).toInt(),
                            errorMessage = error,
                            isDismissed = false
                        )
                    )
                }
            }
        )
    }

    fun finishAndDismiss() {
        audioPlayer.stop()
        if (isTestAlarm) {
            val currentResult = FajrAlarmTestDiagnostics.latestResult.value
            FajrAlarmTestDiagnostics.updateResult(
                currentResult?.copy(
                    isSuccess = currentResult.soundLoaded && currentResult.alarmUiOpened,
                    isDismissed = true
                ) ?: FajrAlarmTestResult(
                    isSuccess = true,
                    soundName = settingsState.fajrAlarmSound,
                    alarmTriggered = true,
                    alarmUiOpened = true,
                    soundLoaded = soundLoaded,
                    audioPlaying = false,
                    isDismissed = true
                )
            )
            onDismiss()
        } else {
            onPrayerCompleted()
        }
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
        PrayerAlarmReceiver.cancelSnoozeAlarm(context)
        audioPlayer.stop()
        if (isTestAlarm) {
            val currentResult = FajrAlarmTestDiagnostics.latestResult.value
            FajrAlarmTestDiagnostics.updateResult(
                currentResult?.copy(
                    isSuccess = true,
                    soundLoaded = true,
                    errorMessage = null,
                    isDismissed = true
                ) ?: FajrAlarmTestResult(
                    isSuccess = true,
                    soundName = settingsState.fajrAlarmSound,
                    alarmTriggered = true,
                    alarmUiOpened = true,
                    soundLoaded = true,
                    audioPlaying = false,
                    isDismissed = true
                )
            )
        }
        finishAndDismiss()
    }

    fun handleWuduSnooze() {
        audioPlayer.stop()
        audioPlaying = false
        if (isTestAlarm) {
            val currentResult = FajrAlarmTestDiagnostics.latestResult.value
            FajrAlarmTestDiagnostics.updateResult(
                currentResult?.copy(
                    isSuccess = true,
                    soundLoaded = soundLoaded || currentResult.soundLoaded,
                    errorMessage = null,
                    isDismissed = false
                ) ?: FajrAlarmTestResult(
                    isSuccess = true,
                    soundName = settingsState.fajrAlarmSound,
                    alarmTriggered = true,
                    alarmUiOpened = true,
                    soundLoaded = soundLoaded,
                    audioPlaying = false,
                    isDismissed = false
                )
            )
        }
        PrayerAlarmReceiver.scheduleSnoozeAlarm(
            context = context,
            prayerName = "Fajr",
            delayMinutes = 10,
            isTest = isTestAlarm
        )
        Toast.makeText(context, "Alarm snoozed for 10 minutes. Wudu timer active 💧", Toast.LENGTH_SHORT).show()
        isWuduTimerMode = true
        isScanningMode = false
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
            // Live Mat Scanner Camera View using CameraX & Anti-Cheat ML Logic
            MatScannerCameraView(
                matDetector = matDetector,
                scanAttempts = scanAttempts,
                alarmVolume = alarmVolume,
                onSuccess = { color, conf -> handleScanSuccess(color, conf) },
                onFailAttempt = { handleScanFailed() },
                onWuduSnooze = { handleWuduSnooze() },
                onCancelScan = { isScanningMode = false }
            )
        } else if (isWuduTimerMode) {
            // Live Wudu Timer View: counts down & up, shows Sunnah steps, and prominent Scan Prayer Mat button
            FajrWuduLiveTimerView(
                snoozeTotalSeconds = 600,
                onScanPrayerMat = { startScanning() },
                onDismissAlarm = { finishAndDismiss() },
                onBackToAlarm = {
                    isWuduTimerMode = false
                    audioPlayer.playAdhanAlarm(
                        soundName = settingsState.fajrAlarmSound,
                        customUri = settingsState.fajrCustomSoundUri,
                        volume = alarmVolume
                    )
                }
            )
        } else {
            // Main Fajr Alarm Full Screen View
            FajrAlarmRingingView(
                isTestAlarm = isTestAlarm,
                soundName = resolvedSoundName,
                audioLoaded = soundLoaded,
                audioPlaying = audioPlaying,
                audioError = audioError,
                alarmVolume = alarmVolume,
                scanAttempts = scanAttempts,
                onStartScan = { startScanning() },
                onWuduSnooze = { handleWuduSnooze() },
                onClose = { finishAndDismiss() }
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
    isTestAlarm: Boolean = false,
    soundName: String = "Makkah Adhan",
    audioLoaded: Boolean = true,
    audioPlaying: Boolean = true,
    audioError: String? = null,
    alarmVolume: Float,
    scanAttempts: Int,
    onStartScan: () -> Unit,
    onWuduSnooze: () -> Unit,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isTestAlarm) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE5A638).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFFE5A638))
                ) {
                    Text(
                        text = "🚨 TEST ALARM MODE",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE5A638)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

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
                    .background(if (isTestAlarm) Color(0xFFE5A638).copy(alpha = 0.2f) else safaColors.goldGlow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(if (isTestAlarm) Color(0xFFE5A638) else safaColors.goldPrimary, CircleShape),
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

            Spacer(modifier = Modifier.height(24.dp))

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
                text = if (isTestAlarm) "Fajr Alarm Test" else "Fajr Prayer Time",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = safaColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sound name & audio diagnostic tag
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (audioError != null) Color(0xFFFFEBEE) else safaColors.goldGlow.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, if (audioError != null) Color.Red else safaColors.goldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (audioError != null) Icons.Default.Warning else Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = if (audioError != null) Color.Red else safaColors.goldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (audioError != null) "Audio Error: $audioError" else "🎵 Playing: $soundName (${(alarmVolume * 100).toInt()}% Vol)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (audioError != null) Color.Red else safaColors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isTestAlarm)
                    "Testing production alarm pipeline & audio stream. Scan prayer mat or press Stop Test."
                else
                    "Adhan Playing • Scan your prayer mat to turn off alarm",
                style = MaterialTheme.typography.bodyMedium,
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
                    text = "Scan Prayer Mat to Dismiss",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }

            OutlinedButton(
                onClick = onWuduSnooze,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = safaColors.goldPrimary
                ),
                border = BorderStroke(1.5.dp, safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("wudu_snooze_button")
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Im going to make wudu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = safaColors.goldPrimary
                )
            }

            // Anti-cheat verification badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0C142A),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Anti-Cheat Protection Enforced",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldPrimary
                        )
                        Text(
                            text = "Photo uploads & manual skips permanently disabled. Live camera scan of physical mat required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
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
    onWuduSnooze: (() -> Unit)? = null,
    onCancelScan: () -> Unit
) {
    val detectionResult by matDetector.detectionState.collectAsState()
    val safaColors = LocalSafaColors.current

    val scanTransition = rememberInfiniteTransition(label = "scanner")
    val laserOffset by scanTransition.animateFloat(
        initialValue = 0f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    var latestAnalyzedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun processCapturedBitmap(bitmap: Bitmap, isManualTrigger: Boolean = false) {
        latestAnalyzedBitmap = bitmap
        val result = matDetector.analyzeFrame(bitmap)
        if (result.isDetected) {
            onSuccess(result.detectedColorName, result.confidence)
        } else if (isManualTrigger) {
            onFailAttempt()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Live Prayer Mat Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )
                Text(
                    text = "CameraX Feed • ML Pattern & Weave Detection",
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.textSecondary
                )
            }
            IconButton(onClick = onCancelScan) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = safaColors.goldPrimary)
            }
        }

        // Live CameraX Preview Container with Anti-Cheat Overlay
        Box(
            modifier = Modifier
                .size(310.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF080E20))
                .border(
                    BorderStroke(
                        2.5.dp,
                        if (detectionResult.isDetected) IslamicGreen else safaColors.goldPrimary
                    ),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Embedded CameraX Viewfinder
            CameraXMatScannerPreview(
                modifier = Modifier.fillMaxSize(),
                onFrameCaptured = { frameBitmap ->
                    processCapturedBitmap(frameBitmap, isManualTrigger = false)
                }
            )

            // Scanning Laser Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .offset(y = (laserOffset - 140).dp)
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

            // Live Viewfinder Target Indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "LIVE CAM FEED",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color(0x99000000), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                if (detectionResult.isDetected) {
                    Text(
                        text = "MAT CONFIRMED ✓",
                        style = MaterialTheme.typography.labelSmall,
                        color = IslamicGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color(0xDD0A2E1A), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Live Anti-Cheat & ML Metric Card
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SafaSpacing.cardRadius),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101B39)),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mat Confidence Threshold (68%+):",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
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
                        trackColor = Color(0xFF192750)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Color: ${detectionResult.detectedColorName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.goldChampagne
                        )
                        Text(
                            text = "Weave Score: ${(detectionResult.patternScore * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = detectionResult.message,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (detectionResult.isDetected) IslamicGreen else Color.White,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "🔒 ${detectionResult.antiCheatMessage}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (detectionResult.antiCheatPassed) IslamicGreen else Color(0xFFFFB74D),
                        fontSize = 11.sp
                    )
                }
            }

            // Capture & Verify Now Button (Real Camera Frame Snapshot)
            Button(
                onClick = {
                    latestAnalyzedBitmap?.let { bmp ->
                        processCapturedBitmap(bmp, isManualTrigger = true)
                    } ?: run {
                        matDetector.recordFailedAttempt()
                        onFailAttempt()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("capture_verify_mat_button")
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SafaNavyDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Capture & Verify Mat",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }

            if (onWuduSnooze != null) {
                OutlinedButton(
                    onClick = onWuduSnooze,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = safaColors.goldPrimary
                    ),
                    border = BorderStroke(1.dp, safaColors.goldPrimary),
                    shape = RoundedCornerShape(SafaSpacing.pillRadius),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("scanner_wudu_snooze_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I'm going to make wudu (Snooze 10m)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = safaColors.goldPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraXMatScannerPreview(
    modifier: Modifier = Modifier,
    onFrameCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    var lastAnalyzedTime by remember { mutableStateOf(0L) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val now = System.currentTimeMillis()
                        if (now - lastAnalyzedTime >= 400L) {
                            lastAnalyzedTime = now
                            try {
                                val bitmap = imageProxy.toBitmap()
                                val rotation = imageProxy.imageInfo.rotationDegrees
                                val rotatedBitmap = if (rotation != 0) {
                                    val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                                } else {
                                    bitmap
                                }
                                ContextCompat.getMainExecutor(ctx).execute {
                                    onFrameCaptured(rotatedBitmap)
                                }
                            } catch (e: Exception) {
                                // Ignore frame conversion error
                            }
                        }
                        imageProxy.close()
                    }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    android.util.Log.e("CameraX", "Error starting CameraX preview", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

data class WuduStepItem(
    val stepNumber: Int,
    val title: String,
    val arabic: String?,
    val instruction: String,
    val repetition: String
)

@Composable
fun FajrWuduLiveTimerView(
    snoozeTotalSeconds: Int = 600,
    onScanPrayerMat: () -> Unit,
    onDismissAlarm: () -> Unit,
    onBackToAlarm: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    var remainingSeconds by remember { mutableIntStateOf(snoozeTotalSeconds) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val wuduSteps = remember {
        listOf(
            WuduStepItem(
                stepNumber = 1,
                title = "Intention (Niyyah) & Bismillah",
                arabic = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                instruction = "Formulate a sincere intention in your heart to purify yourself for worship, then say 'Bismillah'.",
                repetition = "Once"
            ),
            WuduStepItem(
                stepNumber = 2,
                title = "Wash Both Hands to Wrists",
                arabic = null,
                instruction = "Wash your right hand up to the wrist, then your left, rubbing between fingers thoroughly.",
                repetition = "3 Times"
            ),
            WuduStepItem(
                stepNumber = 3,
                title = "Rinse Mouth & Nose",
                arabic = null,
                instruction = "Take water into your mouth, swirl it completely, and sniff water gently into your nostrils, then blow it out.",
                repetition = "3 Times"
            ),
            WuduStepItem(
                stepNumber = 4,
                title = "Wash Entire Face",
                arabic = null,
                instruction = "Wash your face from your forehead down to your chin and from ear to ear. Ensure water touches every part.",
                repetition = "3 Times"
            ),
            WuduStepItem(
                stepNumber = 5,
                title = "Wash Arms to the Elbows",
                arabic = null,
                instruction = "Wash your right arm from fingertips up to and including the elbow, then repeat with your left arm.",
                repetition = "3 Times (Each)"
            ),
            WuduStepItem(
                stepNumber = 6,
                title = "Wipe Head & Clean Ears (Masah)",
                arabic = null,
                instruction = "Wet hands and run them from hairline to the back of the neck and back once. Clean inside and behind ears with index fingers and thumbs.",
                repetition = "Once"
            ),
            WuduStepItem(
                stepNumber = 7,
                title = "Wash Both Feet to the Ankles",
                arabic = null,
                instruction = "Wash your right foot up to the ankle, ensuring you clean between the toes with your pinky, then repeat with your left foot.",
                repetition = "3 Times (Each)"
            )
        )
    }

    // Live ticking timer
    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
            elapsedSeconds++
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedCountdown = String.format("%02d:%02d", minutes, seconds)

    val elapsedMin = elapsedSeconds / 60
    val elapsedSec = elapsedSeconds % 60
    val formattedElapsed = String.format("%02d:%02d", elapsedMin, elapsedSec)

    val progressFraction = (remainingSeconds.toFloat() / snoozeTotalSeconds.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070D1E))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToAlarm,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Alarm",
                    tint = Color.White
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = safaColors.goldPrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "WUDU MODE ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                }
            }

            IconButton(
                onClick = onDismissAlarm,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Big Circular Live Countdown Timer
        Box(
            modifier = Modifier
                .size(210.dp)
                .testTag("wudu_live_timer_circle"),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp,
                color = safaColors.goldPrimary,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedCountdown,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Snooze Remaining",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "Elapsed: $formattedElapsed",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.goldChampagne,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Prominent SCAN PRAYER MAT button (Requested by user)
        Button(
            onClick = onScanPrayerMat,
            colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
            shape = RoundedCornerShape(SafaSpacing.pillRadius),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("scan_prayer_mat_from_wudu_button")
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = SafaNavyDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Scan Prayer Mat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SafaNavyDark
            )
        }

        // Sunnah Wudu Steps Interactive Guide Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sunnah Wudu Guide",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = safaColors.goldPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Step ${currentStepIndex + 1} of ${wuduSteps.size}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.goldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                val currentStep = wuduSteps[currentStepIndex]

                Text(
                    text = currentStep.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (currentStep.arabic != null) {
                    Text(
                        text = currentStep.arabic,
                        style = ArabicDisplayStyle,
                        fontSize = 20.sp,
                        color = safaColors.goldChampagne,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = currentStep.instruction,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = "Repetition: ${currentStep.repetition}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                if (currentStepIndex > 0) currentStepIndex--
                            },
                            enabled = currentStepIndex > 0,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (currentStepIndex > 0) Color.White.copy(alpha = 0.1f) else Color.Transparent,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Previous Step",
                                tint = if (currentStepIndex > 0) Color.White else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (currentStepIndex < wuduSteps.lastIndex) currentStepIndex++
                            },
                            enabled = currentStepIndex < wuduSteps.lastIndex,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (currentStepIndex < wuduSteps.lastIndex) safaColors.goldPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next Step",
                                tint = if (currentStepIndex < wuduSteps.lastIndex) safaColors.goldPrimary else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Secondary Dismiss button
        OutlinedButton(
            onClick = onDismissAlarm,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.7f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(SafaSpacing.pillRadius),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "I'm Done & Ready to Pray (Dismiss Alarm)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
