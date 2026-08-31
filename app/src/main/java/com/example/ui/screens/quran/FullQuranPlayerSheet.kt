package com.example.ui.screens.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.ui.animation.EaseOutCubic
import com.example.ui.animation.pressScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.QariReciter
import com.example.audio.QuranAudioPlayerState
import com.example.audio.QuranReciters
import com.example.audio.QuranRepeatMode
import com.example.data.repository.Surah
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.ArabicTextStyle
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullQuranPlayerSheet(
    playerState: QuranAudioPlayerState,
    allSurahs: List<Surah>,
    isAudioDownloaded: Boolean,
    downloadProgress: Float?,
    onDownloadAudio: () -> Unit,
    onDeleteAudio: () -> Unit,
    onDismiss: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNextSurah: () -> Unit,
    onPreviousSurah: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekForward10: () -> Unit,
    onSeekRewind10: () -> Unit,
    onSelectReciter: (QariReciter) -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onToggleRepeatMode: () -> Unit,
    onSetSleepTimer: (Int?) -> Unit,
    onSelectSurah: (Int) -> Unit
) {
    val safaColors = LocalSafaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showRecitersDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showSurahListDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(safaColors.goldPrimary.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Repeat Mode Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(safaColors.navyElevated)
                        .clickable { onToggleRepeatMode() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (playerState.repeatMode == QuranRepeatMode.REPEAT_SURAH || playerState.repeatMode == QuranRepeatMode.REPEAT_AYAH) {
                            Icons.Default.RepeatOne
                        } else {
                            Icons.Default.Repeat
                        },
                        contentDescription = "Repeat Mode",
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (playerState.repeatMode) {
                            QuranRepeatMode.CONTINUOUS_WHOLE_QURAN -> "Continuous Quran"
                            QuranRepeatMode.REPEAT_SURAH -> "Repeat Surah"
                            QuranRepeatMode.REPEAT_AYAH -> "Repeat Ayah"
                            QuranRepeatMode.OFF -> "Repeat Off"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize Player",
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Center Calligraphy Art Card
            Card(
                modifier = Modifier
                    .size(200.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.5.dp, safaColors.goldPrimary.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF2C1E14),
                                    Color(0xFF1E140C),
                                    Color(0xFF0F0B06)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(safaColors.goldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${playerState.currentSurahNumber ?: 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.goldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = playerState.currentSurahArabicName ?: "الفَاتِحة",
                            style = ArabicDisplayStyle,
                            fontSize = 32.sp,
                            color = safaColors.goldChampagne,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Holy Qur'an Audio",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            color = safaColors.goldPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Surah Title & Reciter Info
            Text(
                text = "${playerState.currentSurahNumber ?: 1}. ${playerState.currentSurahEnglishName ?: "Al-Fatihah"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = safaColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(safaColors.navyElevated)
                    .clickable { showRecitersDialog = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = playerState.reciter.englishName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = safaColors.goldPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = safaColors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Scrubber Slider
            var isUserDragging by remember { mutableStateOf(false) }
            var dragPosition by remember { mutableFloatStateOf(0f) }

            val currentSliderPos = if (isUserDragging) {
                dragPosition
            } else if (playerState.durationMs > 0) {
                (playerState.currentPositionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Slider(
                value = currentSliderPos,
                onValueChange = {
                    isUserDragging = true
                    dragPosition = it
                },
                onValueChangeFinished = {
                    val targetMs = (dragPosition * playerState.durationMs).toLong()
                    onSeekTo(targetMs)
                    isUserDragging = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = safaColors.goldPrimary,
                    activeTrackColor = safaColors.goldPrimary,
                    inactiveTrackColor = safaColors.goldPrimary.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_player_slider")
            )

            // Duration timestamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentDisplayMs = if (isUserDragging) {
                    (dragPosition * playerState.durationMs).toLong()
                } else {
                    playerState.currentPositionMs
                }

                Text(
                    text = formatDuration(currentDisplayMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = formatDuration(playerState.durationMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Controls Row (SkipPrev, Rewind10, Play/Pause, Forward10, SkipNext)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Surah
                IconButton(
                    onClick = onPreviousSurah,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Surah",
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Rewind 10s
                IconButton(
                    onClick = onSeekRewind10,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind 10 Seconds",
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Main Play / Pause Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .pressScale()
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(safaColors.goldPrimary, safaColors.goldChampagne)
                            )
                        )
                        .clickable { onTogglePlayPause() }
                        .testTag("full_player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (playerState.isBuffering) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        AnimatedContent(
                            targetState = playerState.isPlaying,
                            transitionSpec = {
                                (scaleIn(animationSpec = tween(220, easing = EaseOutCubic)) + fadeIn(tween(140))) togetherWith
                                (scaleOut(animationSpec = tween(180, easing = EaseOutCubic)) + fadeOut(tween(140)))
                            },
                            label = "fullPlayPauseIconAnim"
                        ) { isPlaying ->
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF1E140C),
                                modifier = Modifier.size(36.dp)
                              )
                        }
                    }
                }

                // Forward 10s
                IconButton(
                    onClick = onSeekForward10,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 10 Seconds",
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Next Surah
                IconButton(
                    onClick = onNextSurah,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Surah",
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Quick Action Buttons (Surah List, Speed, Sleep Timer)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Surah Jumper
                PlayerActionButton(
                    icon = Icons.Default.FormatListNumbered,
                    label = "114 Surahs",
                    onClick = { showSurahListDialog = true }
                )

                // Speed
                PlayerActionButton(
                    icon = Icons.Default.Speed,
                    label = "${playerState.playbackSpeed}x",
                    onClick = { showSpeedDialog = true }
                )

                // Sleep Timer
                PlayerActionButton(
                    icon = Icons.Default.AccessTime,
                    label = if (playerState.sleepTimerMinutesRemaining != null) {
                        "${playerState.sleepTimerMinutesRemaining}m"
                    } else "Timer",
                    onClick = { showSleepTimerDialog = true },
                    isActive = playerState.sleepTimerMinutesRemaining != null
                )

                // Download/Offline Audio status
                if (downloadProgress != null) {
                    PlayerActionButton(
                        icon = Icons.Default.CloudDownload,
                        label = "${(downloadProgress * 100).toInt()}%",
                        onClick = { /* Non-interactive during progress */ },
                        isActive = true
                    )
                } else if (isAudioDownloaded) {
                    PlayerActionButton(
                        icon = Icons.Default.CheckCircle,
                        label = "Saved Offline",
                        onClick = onDeleteAudio,
                        isActive = true
                    )
                } else {
                    PlayerActionButton(
                        icon = Icons.Default.CloudDownload,
                        label = "Download",
                        onClick = onDownloadAudio
                    )
                }
            }
        }
    }

    // Reciters Selector Dialog
    if (showRecitersDialog) {
        AlertDialog(
            onDismissRequest = { showRecitersDialog = false },
            title = {
                Text(
                    text = "Select Reciter (Qari)",
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(QuranReciters.RECITERS) { reciter ->
                        val isSelected = reciter.id == playerState.reciter.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) safaColors.navyElevated else Color.Transparent)
                                .clickable {
                                    onSelectReciter(reciter)
                                    showRecitersDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reciter.englishName,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.textPrimary
                                )
                                Text(
                                    text = reciter.name,
                                    style = ArabicTextStyle,
                                    fontSize = 14.sp,
                                    color = safaColors.goldPrimary
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = safaColors.goldPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRecitersDialog = false }) {
                    Text("Done", color = safaColors.goldPrimary)
                }
            }
        )
    }

    // Playback Speed Dialog
    if (showSpeedDialog) {
        val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = {
                Text(
                    text = "Playback Speed",
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )
            },
            text = {
                Column {
                    speeds.forEach { speed ->
                        val isSelected = playerState.playbackSpeed == speed
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) safaColors.navyElevated else Color.Transparent)
                                .clickable {
                                    onSetPlaybackSpeed(speed)
                                    showSpeedDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${speed}x ${if (speed == 1.0f) "(Normal)" else ""}",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) safaColors.goldPrimary else safaColors.textPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = safaColors.goldPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Close", color = safaColors.goldPrimary)
                }
            }
        )
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        val timerOptions = listOf(
            Pair("Turn Off Timer", null),
            Pair("15 Minutes", 15),
            Pair("30 Minutes", 30),
            Pair("45 Minutes", 45),
            Pair("60 Minutes (1 hour)", 60),
            Pair("90 Minutes", 90)
        )
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = {
                Text(
                    text = "Sleep Timer",
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )
            },
            text = {
                Column {
                    timerOptions.forEach { (label, minutes) ->
                        val isSelected = playerState.sleepTimerMinutesRemaining == minutes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) safaColors.navyElevated else Color.Transparent)
                                .clickable {
                                    onSetSleepTimer(minutes)
                                    showSleepTimerDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) safaColors.goldPrimary else safaColors.textPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = safaColors.goldPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Close", color = safaColors.goldPrimary)
                }
            }
        )
    }

    // 114 Surahs Quick Jumper Dialog
    if (showSurahListDialog) {
        AlertDialog(
            onDismissRequest = { showSurahListDialog = false },
            title = {
                Text(
                    text = "Jump to Surah (1 - 114)",
                    fontWeight = FontWeight.Bold,
                    color = safaColors.goldPrimary
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    items(allSurahs) { surah ->
                        val isCurrent = surah.number == playerState.currentSurahNumber
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) safaColors.navyElevated else Color.Transparent)
                                .clickable {
                                    onSelectSurah(surah.number)
                                    showSurahListDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${surah.number}.",
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.goldPrimary,
                                    modifier = Modifier.width(32.dp)
                                )
                                Column {
                                    Text(
                                        text = surah.englishName,
                                        fontWeight = FontWeight.SemiBold,
                                        color = safaColors.textPrimary
                                    )
                                    Text(
                                        text = "${surah.numberOfAyahs} Verses",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = safaColors.textSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Text(
                                text = surah.name,
                                style = ArabicTextStyle,
                                fontSize = 16.sp,
                                color = safaColors.goldPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSurahListDialog = false }) {
                    Text("Done", color = safaColors.goldPrimary)
                }
            }
        )
    }
}

@Composable
private fun PlayerActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    val safaColors = LocalSafaColors.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) safaColors.goldPrimary.copy(alpha = 0.2f) else safaColors.navyElevated)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) safaColors.goldPrimary else safaColors.textSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) safaColors.goldPrimary else safaColors.textPrimary
        )
    }
}
