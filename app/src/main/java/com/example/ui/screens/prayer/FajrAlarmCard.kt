package com.example.ui.screens.prayer

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.IslamicApp
import com.example.data.local.entity.PrayerEntity
import com.example.data.repository.AppSettings
import com.example.data.repository.FajrAlarmMode
import com.example.notifications.AlarmStatusType
import com.example.notifications.ScheduledFajrInfo
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import java.util.Locale

/**
 * Premium Material 3 Fajr Alarm Card for Safa.
 * Features:
 * - Large alarm time display
 * - Real-time alarm status pill (Scheduled, Disabled, Permission Required, Error)
 * - Three-way Mode Selector: At Fajr, Before Fajr (5-60m presets), and Custom time (Material time picker)
 * - Clean enable/disable switch that immediately reschedules or cancels
 * - Real-time context: "Alarm will sound at 05:15", "Fajr is at 05:35", "Alarm set for 20 min before Fajr"
 * - One-tap test buttons for full interactive simulation and 10-second OS AlarmManager test
 * - Elegant dark luxury / Safa gold aesthetic
 */
@Composable
fun FajrAlarmCard(
    prayerEntity: PrayerEntity? = null,
    onNavigateToAlarmTest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val settingsRepository = app.settingsRepository
    val permissionManager = app.permissionManager
    val notifManager = app.prayerNotificationManager

    val settings by settingsRepository.settingsState.collectAsState()
    val permState by permissionManager.permissionState.collectAsState()
    val safaColors = LocalSafaColors.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Re-verify permissions when returning from system settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionManager.checkAllPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentMode = FajrAlarmMode.fromString(settings.fajrAlarmMode)
    val isEnabled = settings.notifyFajr

    // Dynamically calculate live scheduled Fajr info based on settings and prayer times
    val scheduledInfo = remember(settings, prayerEntity, permState) {
        try {
            notifManager.getNextScheduledFajrInfo(prayerEntity)
        } catch (e: Exception) {
            ScheduledFajrInfo(
                triggerTimeMillis = 0L,
                dateStr = "",
                timeStr = "",
                isTomorrow = false,
                isExact = false,
                calculationMethod = settings.calculationMethodName,
                mode = currentMode,
                offsetMinutes = settings.fajrAlarmBeforeMinutes,
                fajrTimeStr = "",
                statusType = AlarmStatusType.ERROR,
                summaryText = "Error"
            )
        }
    }

    val hasExactAlarm = permState.hasExactAlarmPermission
    val hasNotif = permState.hasNotificationPermission
    val hasFullScreen = permState.hasFullScreenIntentPermission

    val effectiveStatusType = when {
        !isEnabled -> AlarmStatusType.OFF
        !hasNotif || !hasExactAlarm || !hasFullScreen -> AlarmStatusType.PERMISSION_REQUIRED
        scheduledInfo.statusType == AlarmStatusType.ERROR -> AlarmStatusType.ERROR
        else -> AlarmStatusType.SCHEDULED
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fajr_alarm_card")
            .animateContentSize(),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            when (effectiveStatusType) {
                AlarmStatusType.PERMISSION_REQUIRED -> Color(0xFFE5A638).copy(alpha = 0.6f)
                AlarmStatusType.ERROR -> Color(0xFFEF5350).copy(alpha = 0.6f)
                AlarmStatusType.SCHEDULED -> safaColors.goldPrimary.copy(alpha = 0.45f)
                AlarmStatusType.OFF -> safaColors.navyBorder.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header: Icon, Title, and Master Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (isEnabled) safaColors.goldPrimary.copy(alpha = 0.18f) else safaColors.textSecondary.copy(alpha = 0.12f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEnabled) Icons.Default.Alarm else Icons.Default.AlarmOff,
                            contentDescription = "Fajr Alarm Icon",
                            tint = if (isEnabled) safaColors.goldPrimary else safaColors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Fajr Alarm",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        Text(
                            text = "Morning Prayer Wake-up",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        settingsRepository.updateFajrAlarmEnabled(checked)
                        if (checked) {
                            notifManager.scheduleFajrAlarm(prayerEntity)
                            Toast.makeText(context, "Fajr alarm activated", Toast.LENGTH_SHORT).show()
                        } else {
                            notifManager.cancelFajrAlarms()
                            Toast.makeText(context, "Fajr alarm turned off", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("fajr_alarm_enable_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = safaColors.goldPrimary,
                        checkedTrackColor = safaColors.goldPrimary.copy(alpha = 0.35f),
                        uncheckedThumbColor = safaColors.textSecondary,
                        uncheckedTrackColor = safaColors.navyBorder.copy(alpha = 0.3f)
                    )
                )
            }

            // 2. Large Alarm Time & Context Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isEnabled) safaColors.goldPrimary.copy(alpha = 0.06f) else Color.Transparent,
                        RoundedCornerShape(14.dp)
                    )
                    .border(
                        1.dp,
                        if (isEnabled) safaColors.goldBorder.copy(alpha = 0.25f) else safaColors.navyBorder.copy(alpha = 0.15f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEnabled && scheduledInfo.timeStr.isNotEmpty()) scheduledInfo.timeStr else "--:--",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) safaColors.goldPrimary else safaColors.textSecondary.copy(alpha = 0.5f),
                            letterSpacing = 1.sp,
                            modifier = Modifier.testTag("fajr_alarm_large_time")
                        )

                        // Real-time Status Badge
                        StatusPill(effectiveStatusType, scheduledInfo, isEnabled)
                    }

                    // Contextual explanations
                    if (isEnabled) {
                        val alarmTime = scheduledInfo.timeStr.ifEmpty { "05:15" }
                        val fajrTime = scheduledInfo.fajrTimeStr.ifEmpty { "05:15" }
                        val dayLabel = if (scheduledInfo.isTomorrow) "tomorrow" else "today"

                        val contextMessage = when (currentMode) {
                            FajrAlarmMode.AT_FAJR -> {
                                "Alarm will sound at $alarmTime • Fajr is at $fajrTime ($dayLabel)"
                            }
                            FajrAlarmMode.BEFORE_FAJR -> {
                                "Alarm set for ${settings.fajrAlarmBeforeMinutes} min before Fajr ($fajrTime)"
                            }
                            FajrAlarmMode.CUSTOM_TIME -> {
                                "Custom wake-up time set for $alarmTime • Fajr is at $fajrTime"
                            }
                        }

                        Text(
                            text = contextMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textPrimary.copy(alpha = 0.9f),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Alarm is currently turned off. Switch on above to receive dawn wake-up calls.",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 3. Alarm Time Selector Section (At Fajr, Before Fajr, Custom time)
            AnimatedVisibility(visible = isEnabled) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Alarm Schedule Mode",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )

                    // Three Segmented Mode Options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeSelectCard(
                            title = "At Fajr",
                            subtitle = "Calculated time",
                            isSelected = currentMode == FajrAlarmMode.AT_FAJR,
                            onClick = {
                                settingsRepository.updateFajrAlarmMode(FajrAlarmMode.AT_FAJR)
                                notifManager.scheduleFajrAlarm(prayerEntity)
                            },
                            modifier = Modifier.weight(1f).testTag("fajr_mode_at_fajr")
                        )

                        ModeSelectCard(
                            title = "Before Fajr",
                            subtitle = "${settings.fajrAlarmBeforeMinutes}m early",
                            isSelected = currentMode == FajrAlarmMode.BEFORE_FAJR,
                            onClick = {
                                settingsRepository.updateFajrAlarmMode(FajrAlarmMode.BEFORE_FAJR)
                                notifManager.scheduleFajrAlarm(prayerEntity)
                            },
                            modifier = Modifier.weight(1f).testTag("fajr_mode_before_fajr")
                        )

                        ModeSelectCard(
                            title = "Custom",
                            subtitle = settings.fajrAlarmCustomTime,
                            isSelected = currentMode == FajrAlarmMode.CUSTOM_TIME,
                            onClick = {
                                settingsRepository.updateFajrAlarmMode(FajrAlarmMode.CUSTOM_TIME)
                                notifManager.scheduleFajrAlarm(prayerEntity)
                            },
                            modifier = Modifier.weight(1f).testTag("fajr_mode_custom_time")
                        )
                    }

                    // Sub-controls based on selected mode
                    when (currentMode) {
                        FajrAlarmMode.BEFORE_FAJR -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(safaColors.goldPrimary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .border(1.dp, safaColors.goldBorder.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = safaColors.goldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Minutes before Fajr prayer:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = safaColors.textPrimary
                                    )
                                }

                                val minutePresets = listOf(5, 10, 15, 20, 30, 45, 60)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    minutePresets.forEach { minutes ->
                                        val isChosen = settings.fajrAlarmBeforeMinutes == minutes
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (isChosen) safaColors.goldPrimary else safaColors.goldPrimary.copy(alpha = 0.08f),
                                            border = BorderStroke(
                                                1.dp,
                                                if (isChosen) safaColors.goldPrimary else safaColors.goldBorder.copy(alpha = 0.3f)
                                            ),
                                            modifier = Modifier
                                                .clickable {
                                                    settingsRepository.updateFajrAlarmBeforeMinutes(minutes)
                                                    notifManager.scheduleFajrAlarm(prayerEntity)
                                                }
                                                .testTag("fajr_minute_preset_$minutes")
                                        ) {
                                            Text(
                                                text = "$minutes min",
                                                color = if (isChosen) SafaNavyDark else safaColors.textPrimary,
                                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        FajrAlarmMode.CUSTOM_TIME -> {
                            val timePickerLauncher = rememberTimePickerLauncher(
                                context = context,
                                currentTime = settings.fajrAlarmCustomTime,
                                onTimePicked = { newTime ->
                                    settingsRepository.updateFajrAlarmCustomTime(newTime)
                                    notifManager.scheduleFajrAlarm(prayerEntity)
                                    Toast.makeText(context, "Wake-up time set to $newTime", Toast.LENGTH_SHORT).show()
                                }
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = safaColors.goldPrimary.copy(alpha = 0.05f),
                                border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Wake-up time",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = safaColors.textSecondary
                                        )
                                        Text(
                                            text = "\"${settings.fajrAlarmCustomTime}\"",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = safaColors.goldPrimary,
                                            modifier = Modifier.testTag("fajr_custom_time_display")
                                        )
                                    }

                                    Button(
                                        onClick = { timePickerLauncher() },
                                        colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("fajr_change_custom_time_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = SafaNavyDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Change Time",
                                            color = SafaNavyDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        FajrAlarmMode.AT_FAJR -> {
                            // Automatic explanation
                            Text(
                                text = "✓ Automatically synchronizes daily with astronomical Fajr prayer time.",
                                style = MaterialTheme.typography.bodySmall,
                                color = safaColors.textSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }

            // 4. Permission Warning Banners (if missing permissions)
            if (isEnabled && (!hasNotif || !hasExactAlarm || !hasFullScreen)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2C1E08),
                    border = BorderStroke(1.dp, Color(0xFFE5A638)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFE5A638),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Permission Required",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE5A638),
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = when {
                                !hasNotif -> "Notification permission is required to post the alarm alert."
                                !hasExactAlarm -> "Exact Alarm permission is required on Android 12+ to ring on time while sleeping."
                                !hasFullScreen -> "Full-screen alarm access is required to display over the lock screen on Android 14+."
                                else -> "Alarm permissions are missing."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary,
                            fontSize = 11.5.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    when {
                                        !hasNotif -> permissionManager.openNotificationSettings(context)
                                        !hasExactAlarm -> permissionManager.openExactAlarmSettings(context)
                                        !hasFullScreen -> permissionManager.openFullScreenIntentSettings(context)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5A638)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp).testTag("fajr_grant_permission_button")
                            ) {
                                Text(
                                    text = "Grant Permission",
                                    color = SafaNavyDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. Quick Test Suite Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToAlarmTest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = safaColors.goldPrimary.copy(alpha = 0.15f),
                        contentColor = safaColors.goldPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(40.dp).testTag("fajr_test_interactive_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Alarm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        try {
                            notifManager.scheduleScheduledTestAlarm(10)
                            Toast.makeText(context, "OS alarm set for 10s from now! Lock or close Safa to verify.", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to schedule OS alarm: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f).height(40.dp).testTag("fajr_test_os_button")
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = safaColors.goldPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("10s OS Test", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = safaColors.textPrimary)
                }
            }
        }
    }
}

/**
 * Modern selectable Mode Card for At Fajr, Before Fajr, Custom time.
 */
@Composable
private fun ModeSelectCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) safaColors.goldPrimary.copy(alpha = 0.16f) else safaColors.goldPrimary.copy(alpha = 0.04f),
        border = BorderStroke(
            1.dp,
            if (isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
        ),
        modifier = modifier
            .clickable { onClick() }
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) safaColors.goldPrimary else safaColors.textPrimary,
                fontSize = 13.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = safaColors.textSecondary,
                fontSize = 10.5.sp
            )
        }
    }
}

/**
 * Status Pill showing exact live state.
 */
@Composable
private fun StatusPill(
    statusType: AlarmStatusType,
    scheduledInfo: ScheduledFajrInfo,
    isEnabled: Boolean
) {
    val safaColors = LocalSafaColors.current

    val (pillColor, textColor, text) = when {
        !isEnabled -> Triple(
            safaColors.textSecondary.copy(alpha = 0.15f),
            safaColors.textSecondary,
            "⚪ Disabled"
        )
        statusType == AlarmStatusType.PERMISSION_REQUIRED -> Triple(
            Color(0xFFE5A638).copy(alpha = 0.2f),
            Color(0xFFE5A638),
            "🟠 Permission Required"
        )
        statusType == AlarmStatusType.ERROR -> Triple(
            Color(0xFFEF5350).copy(alpha = 0.2f),
            Color(0xFFEF5350),
            "🔴 Error"
        )
        else -> {
            val dayLabel = if (scheduledInfo.isTomorrow) "Tomorrow" else "Today"
            val time = scheduledInfo.timeStr.ifEmpty { "05:15" }
            Triple(
                safaColors.goldPrimary.copy(alpha = 0.2f),
                safaColors.goldPrimary,
                "🟢 Set for $dayLabel at $time"
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = pillColor,
        modifier = Modifier.testTag("fajr_alarm_status_pill")
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 11.sp
        )
    }
}

/**
 * Native Android Material TimePickerDialog launcher for exact clock time selection.
 */
@Composable
private fun rememberTimePickerLauncher(
    context: Context,
    currentTime: String,
    onTimePicked: (String) -> Unit
): () -> Unit {
    return remember(context, currentTime, onTimePicked) {
        {
            val parts = currentTime.split(":")
            val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 5
            val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 30

            val dialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val formatted = String.format(Locale.US, "%02d:%02d", hourOfDay, minute)
                    onTimePicked(formatted)
                },
                initialHour,
                initialMinute,
                DateFormat.is24HourFormat(context)
            )
            dialog.show()
        }
    }
}
