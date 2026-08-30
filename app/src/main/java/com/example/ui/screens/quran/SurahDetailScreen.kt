package com.example.ui.screens.quran

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.Ayah
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    surahNumber: Int,
    viewModel: QuranViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val safaColors = LocalSafaColors.current

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    val surah = uiState.currentSurah
    val isFullSurahPlaying = uiState.playerState.currentSurahNumber == surahNumber && uiState.playerState.isPlaying && uiState.playerState.currentAyahNumber == null
    val isFullSurahBuffering = uiState.playerState.currentSurahNumber == surahNumber && uiState.playerState.isBuffering && uiState.playerState.currentAyahNumber == null
    val isDownloaded = uiState.isCurrentSurahDownloaded

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = surah?.englishName ?: "Surah",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldPrimary
                        )
                        Text(
                            text = "${surah?.englishNameTranslation ?: ""} • ${surah?.numberOfAyahs ?: 0} Verses",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = safaColors.goldPrimary
                        )
                    }
                },
                actions = {
                    // Offline Save/Download Button for Room DB
                    IconButton(
                        onClick = { viewModel.toggleOfflineDownload(surahNumber) },
                        modifier = Modifier.testTag("surah_offline_download_button")
                    ) {
                        if (uiState.isDownloadingSurah) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = safaColors.goldPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.OfflinePin else Icons.Default.CloudDownload,
                                contentDescription = if (isDownloaded) "Remove Offline Storage" else "Save Offline to Room DB",
                                tint = if (isDownloaded) IslamicGreen else safaColors.goldPrimary
                            )
                        }
                    }

                    // Play full audio
                    IconButton(
                        onClick = {
                            if (isFullSurahPlaying) {
                                viewModel.pauseAudio()
                            } else {
                                viewModel.playSurahAudio(surahNumber)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFullSurahPlaying) Icons.Default.Pause else Icons.Default.Headphones,
                            contentDescription = "Play Full Surah",
                            tint = safaColors.goldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(bottom = 104.dp)) {
                QuranAudioBottomBar(
                    playerState = uiState.playerState,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onNextSurah = { viewModel.playNextSurah() },
                    onPreviousSurah = { viewModel.playPreviousSurah() },
                    onOpenFullPlayer = { viewModel.setFullPlayerModalVisible(true) }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoadingAyahs || surah == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = safaColors.goldPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = SafaSpacing.screenHorizontalPadding),
                contentPadding = PaddingValues(bottom = 200.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(SafaSpacing.md)
            ) {
                // Surah Header Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF2C1E14),
                                            Color(0xFF1E140C),
                                            Color(0xFF140D07)
                                        )
                                    )
                                )
                                .padding(SafaSpacing.cardContentPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = surah.name,
                                    style = ArabicDisplayStyle,
                                    color = safaColors.goldPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${surah.englishName} • ${surah.englishNameTranslation}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDFBF7)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${surah.revelationType.uppercase()} • ${surah.numberOfAyahs} AYAHS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = safaColors.goldChampagne.copy(alpha = 0.8f),
                                        letterSpacing = 1.sp
                                    )
                                    if (isDownloaded) {
                                        Box(
                                            modifier = Modifier
                                                .background(IslamicGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "SAVED OFFLINE (ROOM DB)",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                color = IslamicGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Play Full Surah Button
                                    Button(
                                        onClick = {
                                            if (isFullSurahPlaying) {
                                                viewModel.pauseAudio()
                                            } else {
                                                viewModel.playSurahAudio(surah.number)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = safaColors.goldPrimary,
                                            contentColor = Color(0xFF1E140C)
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.testTag("play_full_surah_button")
                                    ) {
                                        if (isFullSurahBuffering) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                color = Color(0xFF1E140C),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = if (isFullSurahPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isFullSurahPlaying) "Pause" else "Play Recitation",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    // Toggle Offline Storage Button
                                    OutlinedButton(
                                        onClick = { viewModel.toggleOfflineDownload(surah.number) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (isDownloaded) IslamicGreen else safaColors.goldPrimary
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isDownloaded) IslamicGreen else safaColors.goldPrimary.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = if (isDownloaded) IslamicGreen else safaColors.goldPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isDownloaded) "Offline Saved" else "Save Offline",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                if (surah.number != 1 && surah.number != 9) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        style = ArabicDisplayStyle,
                                        fontSize = 24.sp,
                                        color = safaColors.goldChampagne
                                    )
                                }
                            }
                        }
                    }
                }

                // Ayahs List
                itemsIndexed(uiState.ayahsList, key = { _, item -> "${item.surahNumber}:${item.numberInSurah}" }) { index, ayah ->
                    val isPlaying = uiState.currentPlayingAyahIndex == index && uiState.playerState.isPlaying
                    val isBookmarked = viewModel.isAyahBookmarked(ayah.surahNumber, ayah.numberInSurah)

                    AyahCard(
                        ayah = ayah,
                        isPlaying = isPlaying,
                        isBookmarked = isBookmarked,
                        onPlayAudio = {
                            if (isPlaying) {
                                viewModel.pauseAudio()
                            } else {
                                viewModel.playAyahAudio(index)
                            }
                        },
                        onToggleBookmark = { viewModel.toggleBookmark(ayah) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }

    // Full Screen Audio Player Sheet
    if (uiState.showFullPlayerModal) {
        FullQuranPlayerSheet(
            playerState = uiState.playerState,
            allSurahs = viewModel.getAllSurahs(),
            onDismiss = { viewModel.setFullPlayerModalVisible(false) },
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onNextSurah = { viewModel.playNextSurah() },
            onPreviousSurah = { viewModel.playPreviousSurah() },
            onSeekTo = { viewModel.seekTo(it) },
            onSeekForward10 = { viewModel.seekForward10s() },
            onSeekRewind10 = { viewModel.seekRewind10s() },
            onSelectReciter = { viewModel.selectReciter(it) },
            onSetPlaybackSpeed = { viewModel.setPlaybackSpeed(it) },
            onToggleRepeatMode = { viewModel.toggleRepeatMode() },
            onSetSleepTimer = { viewModel.setSleepTimer(it) },
            onSelectSurah = {
                viewModel.playSurahAudio(it)
                viewModel.loadSurah(it)
            }
        )
    }
}

@Composable
private fun AyahCard(
    ayah: Ayah,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    val containerColor by animateColorAsState(
        targetValue = if (isPlaying) safaColors.navyElevated else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "ayahContainerColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isPlaying) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "ayahBorderColor"
    )

    val bookmarkScale by animateFloatAsState(
        targetValue = if (isBookmarked) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bookmarkScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card_${ayah.numberInSurah}"),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isPlaying) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
            // Header: Ayah number & Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${ayah.numberInSurah}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Audio Recitation button
                    IconButton(onClick = onPlayAudio, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause Recitation" else "Play Recitation",
                            tint = safaColors.goldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Bookmark button with scale animation
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(36.dp)
                            .scale(bookmarkScale)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Ayah",
                            tint = if (isBookmarked) safaColors.goldPrimary else safaColors.textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Text
            Text(
                text = ayah.arabicText,
                style = ArabicDisplayStyle,
                fontSize = 26.sp,
                lineHeight = 44.sp,
                textAlign = TextAlign.Right,
                color = if (isPlaying) safaColors.goldChampagne else safaColors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transliteration
            Text(
                text = ayah.transliteration,
                style = MaterialTheme.typography.bodyMedium,
                color = safaColors.goldChampagne.copy(alpha = 0.9f),
                lineHeight = 20.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            // English Translation
            Text(
                text = ayah.translation,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = safaColors.textPrimary,
                lineHeight = 22.sp
            )
        }
    }
}
