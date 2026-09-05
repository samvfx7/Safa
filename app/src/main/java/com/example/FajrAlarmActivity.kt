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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.notifications.FajrAlarmFlowState
import com.example.notifications.FajrAlarmStateManager
import com.example.notifications.PrayerAlarmReceiver
import com.example.notifications.PrayerNotificationManager
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.IslamicAppTheme
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * Dedicated full-screen Activity for user-configured Fajr alarms.
 *
 * Provides a live, continuous spiritual flow:
 * ALARM_RINGING → WUDU → READY_TO_PRAY → COMPLETED
 *
 * - Wakes the screen and displays over the lock screen.
 * - Tapping "I'm going to make Wudu" transitions immediately into the live Wudu state inside this Activity.
 * - Stops intrusive alarm audio when entering Wudu without dismissing or scheduling a 10-minute snooze.
 * - Displays live elapsed time and step-by-step progress.
 * - Preserves state across Activity recreations using FajrAlarmStateManager.
 */
class FajrAlarmActivity : ComponentActivity() {

    private lateinit var audioPlayer: AudioPlayerHelper
    private lateinit var stateManager: FajrAlarmStateManager
    private var notificationId: Int = PrayerNotificationManager.NOTIFICATION_ID_FAJR

    private val dismissBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PrayerAlarmReceiver.ACTION_DISMISS_NOTIFICATION) {
                audioPlayer.stop()
                stateManager.resetState()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLockScreenFlags()
        enableEdgeToEdge()

        audioPlayer = AudioPlayerHelper(this)
        stateManager = FajrAlarmStateManager(this)

        val prayerName = intent.getStringExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME) ?: "Fajr"
        val timeString = intent.getStringExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME_STRING) ?: "05:15"
        val isTest = intent.getBooleanExtra(PrayerAlarmReceiver.EXTRA_IS_TEST, false)
        val isSnooze = intent.getBooleanExtra(PrayerAlarmReceiver.EXTRA_IS_SNOOZE, false)
        val startStateExtra = intent.getStringExtra(PrayerAlarmReceiver.EXTRA_START_STATE)

        notificationId = intent.getIntExtra(
            PrayerAlarmReceiver.EXTRA_NOTIFICATION_ID,
            if (isTest) PrayerNotificationManager.NOTIFICATION_ID_TEST else PrayerNotificationManager.NOTIFICATION_ID_FAJR
        )

        // If intent specifies starting in WUDU state (e.g. from notification "Make Wudu" action)
        if (startStateExtra == FajrAlarmFlowState.WUDU.name) {
            stateManager.transitionTo(FajrAlarmFlowState.WUDU)
        } else if (savedInstanceState == null && !isSnooze && stateManager.getSavedState() == FajrAlarmFlowState.COMPLETED) {
            stateManager.transitionTo(FajrAlarmFlowState.ALARM_RINGING)
        }

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
            val currentFlowState by stateManager.flowState.collectAsState()

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
                    FajrAlarmLiveContent(
                        prayerName = prayerName,
                        timeString = timeString,
                        isTest = isTest,
                        isSnooze = isSnooze,
                        flowState = currentFlowState,
                        stateManager = stateManager,
                        audioPlayer = audioPlayer,
                        onMakeWudu = {
                            audioPlayer.stop()
                            stateManager.transitionTo(FajrAlarmFlowState.WUDU)
                        },
                        onImBack = {
                            stateManager.transitionTo(FajrAlarmFlowState.READY_TO_PRAY)
                        },
                        onStartFajr = {
                            handleStartFajr(isTest)
                        },
                        onOpenPrayerGuide = {
                            handleOpenPrayerGuide()
                        },
                        onRemindLater = {
                            handleRemindLater(prayerName, isTest)
                        },
                        onClose = {
                            audioPlayer.stop()
                            stateManager.resetState()
                            finish()
                        }
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

    private fun handleStartFajr(isTest: Boolean) {
        audioPlayer.stop()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val app = application as IslamicApp
        val prayerRepo = app.prayerRepository
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                prayerRepo.togglePrayerDone("fajr", true)
            } catch (e: Exception) {
                // Ignore DB error
            }
        }

        stateManager.transitionTo(FajrAlarmFlowState.COMPLETED)

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

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", "prayer_times")
        }
        startActivity(mainIntent)
        finish()
    }

    private fun handleOpenPrayerGuide() {
        audioPlayer.stop()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        stateManager.transitionTo(FajrAlarmFlowState.COMPLETED)

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("START_DESTINATION", "islamic_learning")
        }
        startActivity(mainIntent)
        finish()
    }

    private fun handleRemindLater(prayerName: String, isTest: Boolean) {
        audioPlayer.stop()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        PrayerAlarmReceiver.scheduleSnoozeAlarm(
            context = this,
            prayerName = prayerName,
            delayMinutes = 10,
            isTest = isTest
        )

        stateManager.resetState()

        Toast.makeText(this, "Fajr reminder set for 10 minutes from now ⏰", Toast.LENGTH_SHORT).show()
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
private fun FajrAlarmLiveContent(
    prayerName: String,
    timeString: String,
    isTest: Boolean,
    isSnooze: Boolean,
    flowState: FajrAlarmFlowState,
    stateManager: FajrAlarmStateManager,
    audioPlayer: AudioPlayerHelper,
    onMakeWudu: () -> Unit,
    onImBack: () -> Unit,
    onStartFajr: () -> Unit,
    onOpenPrayerGuide: () -> Unit,
    onRemindLater: () -> Unit,
    onClose: () -> Unit
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

    // Start playing audio only when in ALARM_RINGING state
    LaunchedEffect(flowState, settingsState.fajrAlarmSound, settingsState.fajrCustomSoundUri) {
        if (flowState == FajrAlarmFlowState.ALARM_RINGING) {
            audioPlayer.playAdhanAlarm(
                soundName = settingsState.fajrAlarmSound,
                customUri = settingsState.fajrCustomSoundUri,
                volume = 0.85f,
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
                                alarmVolumePercent = 85,
                                errorMessage = error,
                                isDismissed = false
                            )
                        )
                    }
                }
            )
        } else {
            // Immediately silence audio when transitioning to Wudu or Ready to Pray
            audioPlayer.stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop()
        }
    }

    if (isMatDetected) {
        // Verification success overlay
        MatDetectedView(
            detectedColor = detectedColor,
            confidence = currentConfidence,
            onContinue = {
                isMatDetected = false
                onStartFajr()
            }
        )
        return
    }

    if (isScanningMode) {
        // Camera scanner for prayer mat
        FajrMatScanView(
            matDetector = matDetector,
            onMatDetected = { color, confidence ->
                isMatDetected = true
                detectedColor = color
                currentConfidence = confidence
                isScanningMode = false
                audioPlayer.stop()
            },
            onCancelScan = { isScanningMode = false }
        )
        return
    }

    // Main Live Flow Container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF091124),
                        Color(0xFF0D1730),
                        Color(0xFF070C1A)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header with Step Flow Progress
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                            flowState == FajrAlarmFlowState.WUDU -> "💧 LIVE WUDU FLOW"
                            flowState == FajrAlarmFlowState.READY_TO_PRAY -> "🕌 READY TO PRAY"
                            else -> "🌅 DAWN PRAYER"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isTest) Color(0xFFE5A638) else safaColors.goldPrimary
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = safaColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Progress Indicator
            FajrFlowStepIndicator(currentState = flowState)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Animated State Content (Ringing -> Wudu -> Ready to Pray)
        AnimatedContent(
            targetState = flowState,
            transitionSpec = {
                (slideInVertically { height -> (height * 0.15f).toInt() } + fadeIn(tween(250)))
                    .togetherWith(slideOutVertically { height -> -(height * 0.15f).toInt() } + fadeOut(tween(200)))
            },
            label = "FajrFlowStateAnimation"
        ) { targetState ->
            when (targetState) {
                FajrAlarmFlowState.ALARM_RINGING -> {
                    RingingStateContent(
                        prayerName = prayerName,
                        timeString = timeString,
                        resolvedSoundName = resolvedSoundName,
                        audioError = audioError,
                        onMakeWudu = onMakeWudu,
                        onRemindLater = onRemindLater,
                        onScanMat = { isScanningMode = true }
                    )
                }

                FajrAlarmFlowState.WUDU -> {
                    WuduStateContent(
                        wuduStartTime = stateManager.getWuduStartTime(),
                        onImBack = onImBack,
                        onRemindLater = onRemindLater
                    )
                }

                FajrAlarmFlowState.READY_TO_PRAY, FajrAlarmFlowState.COMPLETED -> {
                    ReadyToPrayStateContent(
                        prayerName = prayerName,
                        timeString = timeString,
                        onStartFajr = onStartFajr,
                        onOpenPrayerGuide = onOpenPrayerGuide,
                        onScanMat = { isScanningMode = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtle footer
        Text(
            text = "Safa • Pure Dawn Experience",
            style = MaterialTheme.typography.labelSmall,
            color = safaColors.textSecondary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Visual 3-step Progress Indicator:
 * 1. Make Wudu
 * 2. Return to prayer
 * 3. Pray Fajr
 */
@Composable
private fun FajrFlowStepIndicator(currentState: FajrAlarmFlowState) {
    val safaColors = LocalSafaColors.current

    val currentStep = when (currentState) {
        FajrAlarmFlowState.ALARM_RINGING -> 1
        FajrAlarmFlowState.WUDU -> 1
        FajrAlarmFlowState.READY_TO_PRAY -> 2
        FajrAlarmFlowState.COMPLETED -> 3
    }

    val steps = listOf("Make Wudu", "Return", "Pray Fajr")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, stepLabel ->
            val stepNumber = index + 1
            val isDone = stepNumber < currentStep || (stepNumber == 1 && currentState == FajrAlarmFlowState.READY_TO_PRAY)
            val isActive = (stepNumber == 1 && (currentState == FajrAlarmFlowState.ALARM_RINGING || currentState == FajrAlarmFlowState.WUDU)) ||
                    (stepNumber == 2 && currentState == FajrAlarmFlowState.READY_TO_PRAY) ||
                    (stepNumber == 3 && currentState == FajrAlarmFlowState.COMPLETED)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            when {
                                isDone -> safaColors.goldPrimary
                                isActive -> safaColors.goldPrimary.copy(alpha = 0.25f)
                                else -> safaColors.navyBorder.copy(alpha = 0.3f)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SafaNavyDark,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "$stepNumber",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) safaColors.goldPrimary else safaColors.textSecondary
                        )
                    }
                }

                Text(
                    text = stepLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive) safaColors.goldPrimary else safaColors.textSecondary
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (stepNumber < currentStep || (stepNumber == 1 && currentState == FajrAlarmFlowState.READY_TO_PRAY)) {
                                safaColors.goldPrimary.copy(alpha = 0.6f)
                            } else {
                                safaColors.navyBorder.copy(alpha = 0.25f)
                            }
                        )
                )
            }
        }
    }
}

/**
 * State 1: ALARM_RINGING
 *
 * Small: FAJR
 * Large: Fajr Prayer
 * Large prayer time: 05:XX
 * Status: "Fajr time has arrived"
 *
 * Primary: "I’m going to make Wudu"
 * Secondary: "Remind me later"
 * Optional: "Verify with Prayer Mat Scan"
 */
@Composable
private fun RingingStateContent(
    prayerName: String,
    timeString: String,
    resolvedSoundName: String,
    audioError: String?,
    onMakeWudu: () -> Unit,
    onRemindLater: () -> Unit,
    onScanMat: () -> Unit
) {
    val safaColors = LocalSafaColors.current

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
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pulsing Gold Halo with Notification Bell
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
                    contentDescription = "Fajr Alarm Active",
                    tint = SafaNavyDark,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Small badge: FAJR
        Text(
            text = "FAJR",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = safaColors.goldPrimary,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Large title: Fajr Prayer
        Text(
            text = "Fajr Prayer",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = safaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Large Prayer Time Card: 05:XX
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = safaColors.goldPrimary.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.35f))
        ) {
            Text(
                text = timeString,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = safaColors.goldPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status text: "Fajr time has arrived"
        Text(
            text = "Fajr time has arrived",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = safaColors.textSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Arabic Calligraphy: Prayer is better than sleep
        Text(
            text = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ",
            style = ArabicDisplayStyle,
            fontSize = 28.sp,
            color = safaColors.goldPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Prayer is better than sleep",
            style = MaterialTheme.typography.bodySmall,
            color = safaColors.textSecondary,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )

        // Audio status indicator
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (audioError != null) Color(0xFFFFEBEE) else safaColors.goldGlow.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, if (audioError != null) Color.Red else safaColors.goldPrimary.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (audioError != null) Icons.Default.Warning else Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = if (audioError != null) Color.Red else safaColors.goldPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = if (audioError != null) "Audio: $audioError" else "Adhan: $resolvedSoundName",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (audioError != null) Color.Red else safaColors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Actions: Primary "I'm going to make Wudu", Secondary "Remind me later"
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primary Action: "I'm going to make Wudu"
            Button(
                onClick = onMakeWudu,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("fajr_alarm_wudu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = SafaNavyDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I’m going to make Wudu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }

            // Secondary Action: "Remind me later" (Snooze 10 min)
            OutlinedButton(
                onClick = onRemindLater,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = safaColors.goldPrimary
                ),
                border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("fajr_alarm_remind_later_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remind me later",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = safaColors.goldPrimary
                )
            }

            // Optional: Prayer Mat Camera Verification
            OutlinedButton(
                onClick = onScanMat,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = safaColors.goldPrimary.copy(alpha = 0.05f),
                    contentColor = safaColors.goldPrimary
                ),
                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("fajr_alarm_mat_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify with Prayer Mat Scan",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = safaColors.goldPrimary
                )
            }
        }
    }
}

/**
 * State 2: WUDU
 *
 * Heading: "Wudu" / "Make Wudu"
 * Reminder: "Take your time. Fajr is waiting."
 * Live elapsed-time indicator
 * Prominent "I'm back" / "Continue" action
 */
@Composable
private fun WuduStateContent(
    wuduStartTime: Long,
    onImBack: () -> Unit,
    onRemindLater: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    // Live elapsed time updater
    var elapsedSeconds by remember {
        mutableLongStateOf(
            if (wuduStartTime > 0) (System.currentTimeMillis() - wuduStartTime) / 1000 else 0L
        )
    }

    LaunchedEffect(wuduStartTime) {
        val start = if (wuduStartTime > 0) wuduStartTime else System.currentTimeMillis()
        while (true) {
            elapsedSeconds = ((System.currentTimeMillis() - start) / 1000).coerceAtLeast(0L)
            delay(1000)
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedElapsed = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "water_ripple")
    val waterPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waterPulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Serene Water Droplet Visual with Gold Glow
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(waterPulse)
                .background(safaColors.goldGlow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .background(safaColors.goldPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Wudu Active",
                    tint = SafaNavyDark,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Heading: Wudu
        Text(
            text = "Wudu",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = safaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subheading: "Take your time. Fajr is waiting."
        Text(
            text = "Take your time. Fajr is waiting.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = safaColors.goldPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Live Elapsed Time Indicator Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = safaColors.goldPrimary.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ELAPSED TIME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedElapsed,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Alarm remains open and ready for your return",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = safaColors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hadith quote on Wudu
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = safaColors.navyBorder.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, safaColors.navyBorder.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "«مَنْ تَوَضَّأَ فَأَحْسَنَ الْوُضُوءَ خَرَجَتْ خَطَايَاهُ مِنْ جَسَدِهِ»",
                    style = ArabicDisplayStyle,
                    fontSize = 20.sp,
                    color = safaColors.goldPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"Whoever performs Wudu thoroughly, their sins depart from their body.\" — Sahih Muslim",
                    style = MaterialTheme.typography.bodySmall,
                    color = safaColors.textSecondary,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Actions: Primary "I'm back", Secondary "Remind me later"
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prominent "I'm back" / "Continue" button
            Button(
                onClick = onImBack,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("fajr_alarm_im_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = SafaNavyDark,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I’m back • Continue to Prayer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }

            // Remind me later option
            OutlinedButton(
                onClick = onRemindLater,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = safaColors.textSecondary
                ),
                border = BorderStroke(1.dp, safaColors.navyBorder.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("fajr_alarm_wudu_snooze_button")
            ) {
                Text(
                    text = "Remind me later",
                    style = MaterialTheme.typography.bodyMedium,
                    color = safaColors.textSecondary
                )
            }
        }
    }
}

/**
 * State 3: READY_TO_PRAY
 *
 * Heading: "Ready for Fajr?"
 * Fajr prayer time
 * Short reminder
 * Primary: "Start Fajr"
 * Secondary: "Open Prayer Guide"
 */
@Composable
private fun ReadyToPrayStateContent(
    prayerName: String,
    timeString: String,
    onStartFajr: () -> Unit,
    onOpenPrayerGuide: () -> Unit,
    onScanMat: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Serene Gold Crest with Checkmark
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(safaColors.goldGlow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(safaColors.goldPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ready for Fajr",
                    tint = SafaNavyDark,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Ready for Fajr?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = safaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Large Prayer Time Badge
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = safaColors.goldPrimary.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.35f))
        ) {
            Text(
                text = "$prayerName • $timeString",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = safaColors.goldPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Spiritual Reminder
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = safaColors.navyBorder.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, safaColors.navyBorder.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "«رَكْعَتَا الْفَجْرِ خَيْرٌ مِنَ الدُّنْيَا وَمَا فِيهَا»",
                    style = ArabicDisplayStyle,
                    fontSize = 20.sp,
                    color = safaColors.goldPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"The two sunnah rak'ahs of Fajr are better than the world and all it contains.\" — Prophet Muhammad ﷺ",
                    style = MaterialTheme.typography.bodySmall,
                    color = safaColors.textSecondary,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons: Primary "Start Fajr", Secondary "Open Prayer Guide"
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primary Action: "Start Fajr"
            Button(
                onClick = onStartFajr,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("fajr_alarm_start_fajr_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SafaNavyDark,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Start Fajr",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }

            // Secondary Action: "Open Prayer Guide"
            OutlinedButton(
                onClick = onOpenPrayerGuide,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = safaColors.goldPrimary.copy(alpha = 0.05f),
                    contentColor = safaColors.goldPrimary
                ),
                border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("fajr_alarm_prayer_guide_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open Prayer Guide",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = safaColors.goldPrimary
                )
            }

            // Optional: Prayer Mat Verification
            OutlinedButton(
                onClick = onScanMat,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = safaColors.textSecondary
                ),
                border = BorderStroke(1.dp, safaColors.navyBorder.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("fajr_alarm_ready_mat_scan_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = safaColors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify on Prayer Mat",
                    style = MaterialTheme.typography.bodyMedium,
                    color = safaColors.textSecondary
                )
            }
        }
    }
}

/**
 * Verification view shown when a physical prayer mat is scanned successfully
 */
@Composable
private fun MatDetectedView(
    detectedColor: String,
    confidence: Float,
    onContinue: () -> Unit
) {
    val safaColors = LocalSafaColors.current

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
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = safaColors.goldPrimary
            )

            Text(
                text = "Recognized $detectedColor with ${(confidence * 100).toInt()}% confidence.\nRise for morning prayer.",
                style = MaterialTheme.typography.bodyLarge,
                color = safaColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Continue to Prayer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafaNavyDark
                )
            }
        }
    }
}

/**
 * Camera preview view for prayer mat scanning
 */
@Composable
private fun FajrMatScanView(
    matDetector: PrayerMatDetector,
    onMatDetected: (String, Float) -> Unit,
    onCancelScan: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val safaColors = LocalSafaColors.current

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

        // Scanner overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "POINT AT PRAYER MAT",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                }

                IconButton(
                    onClick = onCancelScan,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                }
            }

            // Target frame
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .border(2.dp, safaColors.goldPrimary, RoundedCornerShape(20.dp))
                    .background(safaColors.goldPrimary.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Live camera scanning physical prayer mat texture",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
