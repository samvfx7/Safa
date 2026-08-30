package com.example.ui.screens.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.Surah
import com.example.ui.theme.ArabicTextStyle
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: QuranViewModel,
    onNavigateToSurah: (Int) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val audioDownloadProgress by viewModel.audioDownloadProgress.collectAsState()
    val downloadedAudioSurahs by viewModel.downloadedAudioSurahs.collectAsState()
    val safaColors = LocalSafaColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "The Holy Qur'an",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToBookmarks,
                        modifier = Modifier.testTag("quran_bookmarks_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarks",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = SafaSpacing.screenHorizontalPadding)
        ) {
            // Whole Quran Recitation Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        if (uiState.playerState.isPlaying) {
                            viewModel.setFullPlayerModalVisible(true)
                        } else {
                            viewModel.playSurahAudio(1)
                        }
                    },
                shape = RoundedCornerShape(SafaSpacing.cardRadius),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    safaColors.navyElevated,
                                    safaColors.navySurface
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(safaColors.goldPrimary.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Full Quran Audio Recitation",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = safaColors.textPrimary
                                )
                                Text(
                                    text = "${uiState.playerState.reciter.englishName} • 114 Surahs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne.copy(alpha = 0.85f) else safaColors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (uiState.playerState.isPlaying) {
                                    viewModel.togglePlayPause()
                                } else {
                                    viewModel.playSurahAudio(uiState.playerState.currentSurahNumber ?: 1)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(safaColors.goldPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (uiState.playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play Quran Audio",
                                tint = Color(0xFF1E140C),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search Surah name, translation or number...", color = safaColors.textSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = safaColors.goldPrimary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = safaColors.textSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(SafaSpacing.cardRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = safaColors.goldPrimary,
                    unfocusedBorderColor = safaColors.navyBorder.copy(alpha = 0.4f),
                    focusedTextColor = safaColors.textPrimary,
                    unfocusedTextColor = safaColors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quran_search_field")
            )

            Spacer(modifier = Modifier.height(SafaSpacing.xs))

            // Category & Offline Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.activeFilterTab == QuranFilterTab.ALL,
                    onClick = { viewModel.setFilterTab(QuranFilterTab.ALL) },
                    label = { Text("All Surahs (114)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = safaColors.goldPrimary,
                        selectedLabelColor = Color(0xFF1E140C),
                        containerColor = safaColors.navyElevated,
                        labelColor = safaColors.textSecondary
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (uiState.activeFilterTab == QuranFilterTab.ALL) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
                    )
                )

                FilterChip(
                    selected = uiState.activeFilterTab == QuranFilterTab.DOWNLOADED,
                    onClick = { viewModel.setFilterTab(QuranFilterTab.DOWNLOADED) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.OfflinePin,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (uiState.activeFilterTab == QuranFilterTab.DOWNLOADED) Color(0xFF1E140C) else IslamicGreen
                        )
                    },
                    label = { Text("Offline Saved (${uiState.downloadedSurahsCount})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = safaColors.goldPrimary,
                        selectedLabelColor = Color(0xFF1E140C),
                        containerColor = safaColors.navyElevated,
                        labelColor = safaColors.textSecondary
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (uiState.activeFilterTab == QuranFilterTab.DOWNLOADED) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
                    )
                )

                FilterChip(
                    selected = uiState.activeFilterTab == QuranFilterTab.MECCAN,
                    onClick = { viewModel.setFilterTab(QuranFilterTab.MECCAN) },
                    label = { Text("Meccan (86)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = safaColors.goldPrimary,
                        selectedLabelColor = Color(0xFF1E140C),
                        containerColor = safaColors.navyElevated,
                        labelColor = safaColors.textSecondary
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (uiState.activeFilterTab == QuranFilterTab.MECCAN) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
                    )
                )

                FilterChip(
                    selected = uiState.activeFilterTab == QuranFilterTab.MEDINAN,
                    onClick = { viewModel.setFilterTab(QuranFilterTab.MEDINAN) },
                    label = { Text("Medinan (28)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = safaColors.goldPrimary,
                        selectedLabelColor = Color(0xFF1E140C),
                        containerColor = safaColors.navyElevated,
                        labelColor = safaColors.textSecondary
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (uiState.activeFilterTab == QuranFilterTab.MEDINAN) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(SafaSpacing.xs))

            // Surah Count & Offline Status Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (uiState.activeFilterTab) {
                        QuranFilterTab.ALL -> "All 114 Surahs"
                        QuranFilterTab.DOWNLOADED -> "Offline Local Storage"
                        QuranFilterTab.MECCAN -> "Meccan Revelations"
                        QuranFilterTab.MEDINAN -> "Medinan Revelations"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${uiState.filteredSurahs.size} surahs shown",
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(SafaSpacing.xs))

            // Surah List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 200.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
            ) {
                items(uiState.filteredSurahs, key = { it.number }) { surah ->
                    val isCurrentPlaying = uiState.playerState.currentSurahNumber == surah.number && uiState.playerState.isPlaying
                    val isBufferingThis = uiState.playerState.currentSurahNumber == surah.number && uiState.playerState.isBuffering

                    SurahListItemCard(
                        surah = surah,
                        isPlaying = isCurrentPlaying,
                        isBuffering = isBufferingThis,
                        onClick = { onNavigateToSurah(surah.number) },
                        onPlayAudio = {
                            if (isCurrentPlaying) {
                                viewModel.pauseAudio()
                            } else {
                                viewModel.playSurahAudio(surah.number)
                            }
                        }
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
        val currentSurahNumber = uiState.playerState.currentSurahNumber ?: 1
        val progressKey = "${uiState.playerState.reciter.id}_$currentSurahNumber"
        val downloadProgress = audioDownloadProgress[progressKey]
        val isAudioDownloaded = downloadedAudioSurahs.contains(currentSurahNumber)

        FullQuranPlayerSheet(
            playerState = uiState.playerState,
            allSurahs = viewModel.getAllSurahs(),
            isAudioDownloaded = isAudioDownloaded,
            downloadProgress = downloadProgress,
            onDownloadAudio = { viewModel.downloadSurahAudio(currentSurahNumber) },
            onDeleteAudio = { viewModel.deleteSurahAudio(currentSurahNumber) },
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
            onSelectSurah = { viewModel.playSurahAudio(it) }
        )
    }
}

@Composable
private fun SurahListItemCard(
    surah: Surah,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onClick: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("surah_card_${surah.number}"),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) safaColors.navyElevated else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isPlaying) 1.5.dp else 1.dp,
            if (isPlaying) safaColors.goldPrimary else safaColors.goldBorder.copy(alpha = 0.3f)
        )
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
                // Surah Number Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(safaColors.goldGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.number}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = surah.englishName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        if (surah.isDownloaded) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Offline Available",
                                tint = IslamicGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = "${surah.englishNameTranslation} • ${surah.numberOfAyahs} Verses",
                        style = MaterialTheme.typography.bodySmall,
                        color = safaColors.textSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = surah.name,
                        style = ArabicTextStyle,
                        fontSize = 18.sp,
                        color = safaColors.goldPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (surah.isDownloaded) {
                            Box(
                                modifier = Modifier
                                    .background(IslamicGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "OFFLINE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = IslamicGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(safaColors.navyBorder.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = surah.revelationType.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                color = safaColors.textSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // Direct Audio Play Button
                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            if (isPlaying) safaColors.goldPrimary else safaColors.goldPrimary.copy(alpha = 0.15f),
                            CircleShape
                        )
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = safaColors.goldPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause Surah" else "Play Surah",
                            tint = if (isPlaying) Color(0xFF1E140C) else safaColors.goldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
