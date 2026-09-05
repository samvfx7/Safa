package com.example

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.audio.AudioPlayerHelper
import com.example.data.repository.FajrAlarmTestDiagnostics
import com.example.data.repository.FajrAlarmTestResult
import com.example.matdetection.PrayerMatDetector
import com.example.notifications.PrayerAlarmReceiver
import com.example.notifications.PrayerNotificationManager
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.IslamicAppTheme
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import java.util.concurrent.Executors

/**
 * Dedicated full-screen Activity for user-configured Fajr alarms.
 *
 * Configured specifically to:
 * - Turn the screen on and display over the lock screen (showWhenLocked, turnScreenOn).
 * - Ring reliably when Safa is in the foreground, background, or completely closed.
 * - Provide calm, premium Islamic visuals with large, accessible controls.
 * - Support direct alarm dismissal, 10-minute Wudu snooze, and optional Prayer Mat verification.
 */
class FajrAlarmActivity : ComponentActivity() {

    private lateinit var audioPlayer: AudioPlayerHelper
    private var notificationId: Int = PrayerNotificationManager.NOTIFICATION_ID_FAJR

    private val dismissBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PrayerAlarmReceiver.ACTION_DISMISS_NOTIFICATION) {
                audioPlayer.stop()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLockScreenFlags()
        enableEdgeToEdge()

        audioPlayer = AudioPlayerHelper(this)

        val prayerName = intent.getStringExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME) ?: "Fajr"
        val timeString = intent.getStringExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_STRING) ?: "05:15"
        val isTest = intent.getBooleanExtra(PrayerAlarmReceiver.EXTRA_IS_TEST, false)
        val isSnooze = intent.getBooleanExtra(PrayerAlarmReceiver.EXTRA_IS_SNOOZE, false)
        notificationId = intent.getIntExtra(
            PrayerAlarmReceiver.EXTRA_NOTIFICATION_ID,
            if (isTest) PrayerNotificationManager.NOTIFICATION_ID_TEST else PrayerNotificationManager.NOTIFICATION_ID_FAJR
        )

        // Register dismiss receiver to dismiss activity if user taps "Dismiss" on notification shade
        try {
            val filter = IntentFilter(PrayerAlarmReceiver.ACTION_DISMISS_NOTIFICATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(dismissBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("DEPRECATION")
                registerReceiver(dismissBroadcastReceiver, filter)
            }
        } catch (e: Exception) {
            // Ignore receiver registration issue
        }

        val app = application as IslamicApp

        setContent {
            val settings by app.settingsRepository.settingsState.collectAsState()

            IslamicAppTheme(
                selectedTheme = settings.selectedTheme,
                darkTheme = settings.isDarkMode
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("fajr_alarm_activity_root"),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FajrAlarmActivityContent(
                        prayerName = prayerName,
                        timeString = timeString,
                        isTest = isTest,
                        isSnooze = isSnooze,
                        audioPlayer = audioPlayer,
                        onDismissAlarm = { handleDismiss(isTest) },
                        onSnoozeAlarm = { handleSnooze(prayerName, isTest) },
                        onOpenSafa = { handleOpenSafa() }
                    )
                }
            }
        }
    }

    private fun setupLockScreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun handleDismiss(isTest: Boolean) {
        audioPlayer.stop()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        if (isTest) {
            FajrAlarmTestDiagnostics.updateResult(
                FajrAlarmTestResult(
                    isSuccess = true,
                    soundName = "Fajr Adhan",
                    alarmTriggered = true,
                    alarmUiOpened = true,
                    soundLoaded = true,
                    audioPlaying = false,
                    isDismissed = true
                )
            )
        }

        finish()
    }

    private fun handleSnooze(prayerName: String, isTest: Boolean) {
        audioPlayer.stop()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        PrayerAlarmReceiver.scheduleSnoozeAlarm(
            context = this,
            prayerName = prayerName,
            delayMinutes = 10,
            isTest = isTest
        )

        Toast.makeText(this, "Alarm snoozed for 10 minutes. Wudu timer active 💧", Toast.LENGTH_SHORT).show()

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", "wudu_timer")
        }
        startActivity(mainIntent)
        finish()
    }

    private fun handleOpenSafa() {
        audioPlayer.stop()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", "prayer_times")
        }
        startActivity(mainIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(dismissBroadcastReceiver)
        } catch (e: Exception) {
            // Ignore
        }
        audioPlayer.stop()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
private fun FajrAlarmActivityContent(
    prayerName: String,
    timeString: String,
    isTest: Boolean,
    isSnooze: Boolean,
    audioPlayer: AudioPlayerHelper,
    onDismissAlarm: () -> Unit,
    onSnoozeAlarm: () -> Unit,
    onOpenSafa: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val settingsState by app.settingsRepository.settingsState.collectAsState()
    val safaColors = LocalSafaColors.current

    var soundLoaded by remember { mutableStateOf(false) }
    var audioError by remember { mutableStateOf<String?>(null) }
    var resolvedSoundName by remember { mutableStateOf(settingsState.fajrAlarmSound) }
    var isScanningMode by remember { mutableStateOf(false) }
    var isMatDetected by remember { mutableStateOf(false) }
    var detectedColor by remember { mutableStateOf("") }
    var currentConfidence by remember { mutableFloatStateOf(0f) }

    val matDetector = remember { PrayerMatDetector() }

    // Start playing Adhan alarm
    LaunchedEffect(settingsState.fajrAlarmSound, settingsState.fajrCustomSoundUri) {
        audioPlayer.playAdhanAlarm(
            soundName = settingsState.fajrAlarmSound,
            customUri = settingsState.fajrCustomSoundUri,
            volume = 0.8f,
            onDiagnosticResult = { success, error, resolved ->
                soundLoaded = success
                audioError = error
                resolvedSoundName = resolved

                if (isTest) {
                    FajrAlarmTestDiagnostics.updateResult(
                        FajrAlarmTestResult(
                            isSuccess = success,
                            soundName = settingsState.fajrAlarmSound,
                            soundResolved = resolved,
                            alarmTriggered = true,
                            alarmUiOpened = true,
                            soundLoaded = success,
                            audioPlaying = success,
                            alarmVolumePercent = 80,
                            errorMessage = error,
                            isDismissed = false
                        )
                    )
                }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    if (isMatDetected) {
        // Verification success view
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070D1E))
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Prayer Mat Verified",
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Text(
                    text = "Prayer Mat Detected!",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )

                Text(
                    text = "Recognized $detectedColor with ${(currentConfidence * 100).toInt()}% confidence.\nRise for morning prayer.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = safaColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismissAlarm,
                    colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                    shape = RoundedCornerShape(SafaSpacing.pillRadius),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Complete & Open Safa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SafaNavyDark
                    )
                }
            }
        }
        return
    }

    if (isScanningMode) {
        // Live camera scanner for prayer mat
        FajrMatScanView(
            matDetector = matDetector,
            onMatDetected = { color, confidence ->
                isMatDetected = true
                detectedColor = color
                currentConfidence = confidence
                audioPlayer.stop()
            },
            onCancelScan = { isScanningMode = false }
        )
        return
    }

    // Default Full-Screen Alarm View
    val infiniteTransition = rememberInfiniteTransition(label = "halo_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070D1E))
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar / Status badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isTest) Color(0xFFE5A638).copy(alpha = 0.25f) else safaColors.goldPrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, if (isTest) Color(0xFFE5A638) else safaColors.goldPrimary.copy(alpha = 0.4f))
            ) {
                Text(
                    text = when {
                        isTest -> "🚨 TEST ALARM"
                        isSnooze -> "⏰ SNOOZE ELAPSED"
                        else -> "🌅 DAWN PRAYER"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isTest) Color(0xFFE5A638) else safaColors.goldPrimary
                )
            }

            IconButton(onClick = onDismissAlarm) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = safaColors.textSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Visual: Pulsing Gold Halo + Bell/Crescent
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(pulseScale)
                    .background(safaColors.goldGlow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .background(safaColors.goldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Fajr Alarm Active",
                        tint = SafaNavyDark,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Arabic Calligraphy: Prayer is better than sleep
            Text(
                text = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ",
                style = ArabicDisplayStyle,
                fontSize = 30.sp,
                color = safaColors.goldPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Prayer is better than sleep",
                style = MaterialTheme.typography.bodyMedium,
                color = safaColors.textSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Large Prayer Time Display
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = safaColors.goldPrimary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.35f))
            ) {
                Text(
                    text = "$prayerName • $timeString",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Fajr prayer time has arrived. Rise to meet Allah in the tranquility of dawn.",
                style = MaterialTheme.typography.bodyMedium,
                color = safaColors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Audio track status chip
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (audioError != null) Color(0xFFFFEBEE) else safaColors.goldGlow.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, if (audioError != null) Color.Red else safaColors.goldPrimary.copy(alpha = 0.3f))
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
                        text = if (audioError != null) "Audio: $audioError" else "🎵 $resolvedSoundName",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (audioError != null) Color.Red else safaColors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Large Accessible Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Primary Dismiss Button
            Button(
                onClick = onDismissAlarm,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("fajr_alarm_dismiss_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SafaNavyDark,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dismiss Alarm • I'm Awake",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }

            // Snooze Button (10 min Wudu)
            OutlinedButton(
                onClick = onSnoozeAlarm,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = safaColors.goldPrimary
                ),
                border = BorderStroke(1.5.dp, safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("fajr_alarm_snooze_button")
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I'm going to make Wudu (10m Snooze)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = safaColors.goldPrimary
                )
            }

            // Optional Scan Prayer Mat Verification
            OutlinedButton(
                onClick = { isScanningMode = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = safaColors.goldPrimary.copy(alpha = 0.06f),
                    contentColor = safaColors.goldPrimary
                ),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("fajr_alarm_mat_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify with Prayer Mat Scan",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = safaColors.goldPrimary
                )
            }

            // Open Safa
            Button(
                onClick = onOpenSafa,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = safaColors.textSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fajr_alarm_open_safa_button")
            ) {
                Text(
                    text = "Open Safa Prayer Times",
                    style = MaterialTheme.typography.labelMedium,
                    color = safaColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun FajrMatScanView(
    matDetector: PrayerMatDetector,
    onMatDetected: (String, Float) -> Unit,
    onCancelScan: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val safaColors = LocalSafaColors.current

    var scanAttempts by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        val cameraExecutor = Executors.newSingleThreadExecutor()
                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            try {
                                val bitmap = imageProxy.toBitmap()
                                val result = matDetector.analyzeFrame(bitmap)
                                if (result.isDetected && result.confidence >= 0.65f) {
                                    onMatDetected(result.detectedColorName, result.confidence)
                                }
                            } catch (e: Exception) {
                                // Continue analyzing
                            } finally {
                                imageProxy.close()
                            }
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        // Fallback
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay with target reticle
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
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, safaColors.goldPrimary)
                ) {
                    Text(
                        text = "Point camera at your prayer mat",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                }

                IconButton(onClick = onCancelScan) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel Scan", tint = Color.White)
                }
            }

            // Reticle frame
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CropFree,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(160.dp)
                )
            }

            Button(
                onClick = onCancelScan,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                border = BorderStroke(1.dp, safaColors.goldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Back to Alarm Controls",
                    style = MaterialTheme.typography.titleMedium,
                    color = safaColors.goldPrimary
                )
            }
        }
    }
}
