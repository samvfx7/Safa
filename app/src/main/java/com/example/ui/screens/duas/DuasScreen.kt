package com.example.ui.screens.duas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DuaEntity
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.ArabicTextStyle
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

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
                                text = "Duas & Supplications",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• أدعية",
                                style = ArabicTextStyle,
                                fontSize = 16.sp,
                                color = safaColors.goldPrimary
                            )
                        }
                        Text(
                            text = "Supplications from Quran & Sunnah",
                            style = MaterialTheme.typography.labelSmall,
                            color = safaColors.textSecondary
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
        ) {
            // Search field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search duas, English, Arabic...", color = safaColors.textSecondary) },
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
                    unfocusedBorderColor = safaColors.navyBorder.copy(alpha = 0.35f),
                    focusedTextColor = safaColors.textPrimary,
                    unfocusedTextColor = safaColors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("duas_search_field")
            )

            Spacer(modifier = Modifier.height(SafaSpacing.sm))

            // Category Chips Row (Horizontal Scroll)
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
                                text = category,
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
                        shape = RoundedCornerShape(SafaSpacing.pillRadius)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SafaSpacing.sm))

            // Duas List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 110.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
            ) {
                // Featured Dua Card if on All and no search
                if (uiState.selectedCategory == "All" && uiState.searchQuery.isBlank() && uiState.featuredDua != null) {
                    item {
                        FeaturedDuaCard(
                            dua = uiState.featuredDua!!,
                            onToggleFavorite = { viewModel.toggleFavorite(uiState.featuredDua!!) },
                            onCopy = { copyDuaToClipboard(context, uiState.featuredDua!!) },
                            onShare = { shareDua(context, uiState.featuredDua!!) }
                        )
                    }
                }

                items(uiState.duasList, key = { it.id }) { dua ->
                    DuaItemCard(
                        dua = dua,
                        onToggleFavorite = { viewModel.toggleFavorite(dua) },
                        onCopy = { copyDuaToClipboard(context, dua) },
                        onShare = { shareDua(context, dua) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun FeaturedDuaCard(
    dua: DuaEntity,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("featured_dua_card"),
        shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = if (safaColors.isLuxuryNavy) {
                            listOf(Color(0xFF16254F), Color(0xFF0B132B), Color(0xFF070D1E))
                        } else {
                            listOf(Color(0xFF2C1E14), Color(0xFF1E140C), Color(0xFF140D07))
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
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = safaColors.goldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DUA OF THE DAY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldChampagne,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Row {
                        IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = safaColors.goldChampagne, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = safaColors.goldChampagne, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = dua.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFDFBF7)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = dua.arabicText,
                    style = ArabicDisplayStyle,
                    fontSize = 22.sp,
                    lineHeight = 38.sp,
                    color = safaColors.goldPrimary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = dua.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFDFBF7).copy(alpha = 0.95f),
                    lineHeight = 20.sp
                )

                if (dua.benefit.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• ${dua.benefit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.goldChampagne,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun DuaItemCard(
    dua: DuaEntity,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dua_card_${dua.id}"),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.navyBorder.copy(alpha = 0.35f))
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
                    Text(
                        text = "${dua.category} • ${dua.source}",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .scale(heartScale)
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

            // Arabic Text
            Text(
                text = dua.arabicText,
                style = ArabicDisplayStyle,
                fontSize = 24.sp,
                lineHeight = 42.sp,
                textAlign = TextAlign.Right,
                color = safaColors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transliteration
            Text(
                text = dua.transliteration,
                style = MaterialTheme.typography.bodyMedium,
                color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne.copy(alpha = 0.9f) else safaColors.textSecondary,
                lineHeight = 20.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation
            Text(
                text = dua.translation,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = safaColors.textPrimary,
                lineHeight = 22.sp
            )

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
        }
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
    val text = "${dua.title}\n\n${dua.arabicText}\n\n${dua.transliteration}\n\nTranslation: ${dua.translation}\n\nSource: ${dua.source} — via Safa App"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Dua"))
}
