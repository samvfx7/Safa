package com.example.ui.screens.duas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DuaEntity
import com.example.ui.animation.pressScale
import com.example.ui.animation.staggeredEntrance
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.ArabicTextStyle
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

private val quickSearchKeywords = listOf(
    "Relief", "Morning", "Forgiveness", "Healing", "Protection", "Parents", "Rizq", "Exams", "Gratitude"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasScreen(
    viewModel: DuasViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val safaColors = LocalSafaColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Duas & Adhkar",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• أدعية",
                                style = ArabicTextStyle,
                                fontSize = 17.sp,
                                color = safaColors.goldPrimary
                            )
                        }
                        Text(
                            text = "Authentic Supplications from Quran & Sunnah",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary
                        )
                    }
                },
                actions = {
                    // Quick favorites count badge button
                    Surface(
                        onClick = {
                            viewModel.selectCategory("⭐ Bookmarks")
                        },
                        shape = RoundedCornerShape(SafaSpacing.pillRadius),
                        color = if (uiState.selectedCategory == "⭐ Bookmarks") safaColors.goldPrimary else safaColors.navyElevated,
                        border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("duas_bookmarks_quick_action")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmarks",
                                tint = if (uiState.selectedCategory == "⭐ Bookmarks") {
                                    if (safaColors.isLuxuryNavy) SafaNavyDark else Color(0xFF1E140C)
                                } else safaColors.goldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.favoriteCount}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.selectedCategory == "⭐ Bookmarks") {
                                    if (safaColors.isLuxuryNavy) SafaNavyDark else Color(0xFF1E140C)
                                } else safaColors.textPrimary
                            )
                        }
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
        ) {
            // Search field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search by topic, Arabic, meaning...", color = safaColors.textSecondary, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
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
                    unfocusedBorderColor = safaColors.navyBorder.copy(alpha = 0.35f),
                    focusedTextColor = safaColors.textPrimary,
                    unfocusedTextColor = safaColors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("duas_search_field")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick search suggestions chips (visible when not actively searching)
            if (uiState.searchQuery.isBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickSearchKeywords.forEach { kw ->
                        Surface(
                            onClick = { viewModel.selectQuickTag(kw) },
                            shape = RoundedCornerShape(12.dp),
                            color = safaColors.navyElevated.copy(alpha = 0.6f),
                            border = BorderStroke(0.8.dp, safaColors.navyBorder.copy(alpha = 0.3f)),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "#$kw",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = safaColors.textSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Categories horizontal bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.categories.forEach { category ->
                    val isSelected = category == uiState.selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(category) },
                        label = {
                            Text(
                                text = if (category == "⭐ Bookmarks" && uiState.favoriteCount > 0) {
                                    "⭐ Bookmarks (${uiState.favoriteCount})"
                                } else category,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) {
                                    if (safaColors.isLuxuryNavy) SafaNavyDark else Color(0xFF1E140C)
                                } else {
                                    safaColors.textPrimary
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = safaColors.goldPrimary,
                            selectedLabelColor = if (safaColors.isLuxuryNavy) SafaNavyDark else Color(0xFF1E140C),
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = safaColors.textPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(SafaSpacing.pillRadius),
                        modifier = Modifier.testTag("dua_category_${category.replace(" ", "_")}")
                    )
                }
            }

            // Results count indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        uiState.searchQuery.isNotBlank() -> "Found ${uiState.duasList.size} matching supplications"
                        uiState.selectedCategory == "⭐ Bookmarks" -> "${uiState.duasList.size} saved favorites"
                        uiState.selectedCategory == "All" -> "All Duas (${uiState.duasList.size})"
                        else -> "${uiState.selectedCategory} (${uiState.duasList.size})"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
                if (uiState.searchQuery.isNotBlank()) {
                    Text(
                        text = "Clear search",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.goldPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.onSearchQueryChanged("") }
                    )
                }
            }

            // Duas List & Empty states
            if (uiState.duasList.isEmpty()) {
                EmptyDuasView(
                    isSearch = uiState.searchQuery.isNotBlank(),
                    isBookmarks = uiState.selectedCategory == "⭐ Bookmarks",
                    onClearSearch = { viewModel.onSearchQueryChanged("") },
                    onBrowseAll = { viewModel.selectCategory("All") }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 110.dp, top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
                ) {
                    // Featured Dua Card if on All and no active search
                    if (uiState.selectedCategory == "All" && uiState.searchQuery.isBlank() && uiState.featuredDua != null) {
                        item {
                            FeaturedDuaCard(
                                dua = uiState.featuredDua!!,
                                onToggleFavorite = { viewModel.toggleFavorite(uiState.featuredDua!!) },
                                onCopy = { copyDuaToClipboard(context, uiState.featuredDua!!) },
                                onShare = { shareDua(context, uiState.featuredDua!!) },
                                onClick = { viewModel.openDuaDetail(uiState.featuredDua!!) },
                                modifier = Modifier.staggeredEntrance(0)
                            )
                        }
                    }

                    itemsIndexed(uiState.duasList, key = { _, dua -> dua.id }) { index, dua ->
                        val counter = uiState.repetitionCounters[dua.id] ?: 0
                        val targetReps = parseRepetitions(dua)

                        DuaItemCard(
                            dua = dua,
                            counter = counter,
                            targetReps = targetReps,
                            onIncrementCounter = { max -> viewModel.incrementDuaCounter(dua.id, max) },
                            onResetCounter = { viewModel.resetDuaCounter(dua.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(dua) },
                            onCopy = { copyDuaToClipboard(context, dua) },
                            onShare = { shareDua(context, dua) },
                            onClick = { viewModel.openDuaDetail(dua) },
                            modifier = Modifier.staggeredEntrance(index + (if (uiState.featuredDua != null) 1 else 0))
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }

    // Detail Modal Sheet
    uiState.selectedDuaForDetail?.let { activeDua ->
        DuaDetailSheet(
            dua = activeDua,
            onDismiss = { viewModel.openDuaDetail(null) },
            onToggleFavorite = { viewModel.toggleFavorite(activeDua) },
            onCopy = { copyDuaToClipboard(context, activeDua) },
            onShare = { shareDua(context, activeDua) }
        )
    }
}

@Composable
private fun EmptyDuasView(
    isSearch: Boolean,
    isBookmarks: Boolean,
    onClearSearch: () -> Unit,
    onBrowseAll: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(safaColors.goldGlow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isBookmarks) Icons.Default.BookmarkBorder else Icons.Default.Search,
                    contentDescription = null,
                    tint = safaColors.goldPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isBookmarks) "No Bookmarked Duas Yet" else "No Duas Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = safaColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBookmarks) {
                    "Tap the bookmark icon or heart on any supplication to save it for quick everyday access."
                } else {
                    "Try searching for keywords like 'forgiveness', 'anxiety', 'morning', or check another category."
                },
                style = MaterialTheme.typography.bodySmall,
                color = safaColors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = if (isSearch) onClearSearch else onBrowseAll,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = safaColors.goldPrimary),
                border = BorderStroke(1.dp, safaColors.goldPrimary),
                shape = RoundedCornerShape(SafaSpacing.pillRadius)
            ) {
                Text(if (isSearch) "Clear Search" else "Browse All Duas", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FeaturedDuaCard(
    dua: DuaEntity,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current

    val heartScale by animateFloatAsState(
        targetValue = if (dua.isFavorite) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "featuredFavoriteScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(0.97f)
            .clickable { onClick() }
            .testTag("featured_dua_card"),
        shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.7f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = if (safaColors.isLuxuryNavy) {
                            listOf(safaColors.navyElevated, safaColors.navySurface, safaColors.navyBackground)
                        } else {
                            listOf(safaColors.navyElevated, safaColors.navySurface)
                        }
                    )
                )
                .padding(SafaSpacing.cardContentPadding)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(safaColors.goldGlow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = safaColors.goldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DUA OF THE DAY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldPrimary,
                            letterSpacing = 1.4.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(34.dp)
                                .scale(heartScale)
                        ) {
                            Icon(
                                imageVector = if (dua.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (dua.isFavorite) safaColors.goldPrimary else safaColors.textSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        IconButton(onClick = onCopy, modifier = Modifier.size(34.dp)) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = safaColors.textSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = safaColors.textSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = dua.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Arabic Calligraphy Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            safaColors.navySurface.copy(alpha = 0.4f),
                            RoundedCornerShape(SafaSpacing.cardRadius)
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = dua.arabicText,
                        style = ArabicDisplayStyle,
                        fontSize = 24.sp,
                        lineHeight = 44.sp,
                        color = safaColors.textPrimary,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = dua.transliteration,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else safaColors.textSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dua.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = safaColors.textPrimary,
                    lineHeight = 21.sp
                )

                if (dua.benefit.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(safaColors.goldGlow, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = safaColors.goldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = dua.benefit,
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dua.source,
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Tap for reader mode →",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.goldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun DuaItemCard(
    dua: DuaEntity,
    counter: Int,
    targetReps: Int?,
    onIncrementCounter: (Int) -> Unit,
    onResetCounter: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current

    val heartScale by animateFloatAsState(
        targetValue = if (dua.isFavorite) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "duaFavoriteScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(0.98f)
            .clickable { onClick() }
            .testTag("dua_card_${dua.id}"),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (dua.isFavorite) safaColors.goldPrimary.copy(alpha = 0.5f) else safaColors.navyBorder.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dua.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = safaColors.goldGlow.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = dua.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.goldPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dua.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .scale(heartScale)
                            .testTag("dua_fav_btn_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (dua.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (dua.isFavorite) safaColors.goldPrimary else safaColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = safaColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = safaColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Text Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        safaColors.navyElevated.copy(alpha = 0.35f),
                        RoundedCornerShape(SafaSpacing.cardRadius)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = dua.arabicText,
                    style = ArabicDisplayStyle,
                    fontSize = 23.sp,
                    lineHeight = 42.sp,
                    textAlign = TextAlign.Right,
                    color = safaColors.textPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transliteration
            Text(
                text = dua.transliteration,
                style = MaterialTheme.typography.bodyMedium,
                color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else safaColors.textSecondary,
                lineHeight = 20.sp,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation
            Text(
                text = dua.translation,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = safaColors.textPrimary,
                lineHeight = 21.sp
            )

            // Benefit or Repeat Pill
            if (dua.benefit.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(safaColors.goldGlow, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Benefit: ${dua.benefit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Interactive Repetition Counter (If has recommended repetitions e.g., 3x or 7x)
            if (targetReps != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { onIncrementCounter(targetReps) },
                        shape = RoundedCornerShape(SafaSpacing.pillRadius),
                        color = if (counter >= targetReps) safaColors.goldPrimary else safaColors.navyElevated,
                        border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .pressScale(0.95f)
                            .testTag("dua_counter_${dua.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (counter >= targetReps) Icons.Default.Check else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (counter >= targetReps) {
                                    if (safaColors.isLuxuryNavy) SafaNavyDark else Color(0xFF1E140C)
                                } else safaColors.goldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (counter >= targetReps) "Completed ($counter / $targetReps)" else "Tap to Recite ($counter / $targetReps)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (counter >= targetReps) {
                                    if (safaColors.isLuxuryNavy) SafaNavyDark else Color(0xFF1E140C)
                                } else safaColors.textPrimary
                            )
                        }
                    }

                    if (counter > 0) {
                        IconButton(onClick = onResetCounter, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset counter",
                                tint = safaColors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DuaDetailSheet(
    dua: DuaEntity,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val safaColors = LocalSafaColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = safaColors.goldGlow
                ) {
                    Text(
                        text = dua.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.goldPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (dua.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (dua.isFavorite) safaColors.goldPrimary else safaColors.textSecondary
                        )
                    }
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = safaColors.textSecondary)
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = safaColors.textSecondary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = safaColors.textSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = dua.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = safaColors.textPrimary
            )

            Text(
                text = dua.source,
                style = MaterialTheme.typography.bodySmall,
                color = safaColors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Large Grand Arabic Box
            Surface(
                shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                color = safaColors.navyElevated,
                border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = dua.arabicText,
                        style = ArabicDisplayStyle,
                        fontSize = 28.sp,
                        lineHeight = 52.sp,
                        textAlign = TextAlign.Right,
                        color = safaColors.textPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "TRANSLITERATION",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = safaColors.goldPrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dua.transliteration,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else safaColors.textSecondary,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "TRANSLATION",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = safaColors.goldPrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dua.translation,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = safaColors.textPrimary,
                lineHeight = 26.sp
            )

            if (dua.benefit.isNotBlank()) {
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = safaColors.goldGlow,
                    border = BorderStroke(1.dp, safaColors.goldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = safaColors.goldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Virtue & Benefit",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dua.benefit,
                            style = MaterialTheme.typography.bodyMedium,
                            color = safaColors.textPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

private fun parseRepetitions(dua: DuaEntity): Int? {
    val text = "${dua.title} ${dua.translation} ${dua.arabicText}"
    return when {
        text.contains("7x", ignoreCase = true) || text.contains("7 times", ignoreCase = true) -> 7
        text.contains("3x", ignoreCase = true) || text.contains("3 times", ignoreCase = true) -> 3
        text.contains("100x", ignoreCase = true) || text.contains("100 times", ignoreCase = true) -> 100
        text.contains("33x", ignoreCase = true) || text.contains("33 times", ignoreCase = true) -> 33
        dua.id == "dua_protection_evil" -> 3
        dua.id == "dua_visiting_sick" -> 7
        dua.id == "dua_pain_body" -> 7
        else -> null
    }
}

private fun copyDuaToClipboard(context: Context, dua: DuaEntity) {
    val text = "${dua.title}\n\n${dua.arabicText}\n\n${dua.transliteration}\n\nTranslation: ${dua.translation}\n\nSource: ${dua.source}"
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Dua", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Dua copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun shareDua(context: Context, dua: DuaEntity) {
    val text = "${dua.title}\n\n${dua.arabicText}\n\n${dua.transliteration}\n\nTranslation: ${dua.translation}\n\nSource: ${dua.source} — via Safa Islamic App"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Supplication"))
}
