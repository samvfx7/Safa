package com.example.ui.screens.more

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.local.entity.HadithEntity
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    viewModel: MoreViewModel,
    onBack: () -> Unit,
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
                    Text(
                        text = "Hadith Collection",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
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
                value = uiState.hadithSearchQuery,
                onValueChange = { viewModel.onHadithSearchQueryChanged(it) },
                placeholder = { Text("Search hadiths, narrators, text...", color = safaColors.textSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = safaColors.goldPrimary
                    )
                },
                trailingIcon = {
                    if (uiState.hadithSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onHadithSearchQueryChanged("") }) {
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
                    unfocusedBorderColor = safaColors.navyBorder.copy(alpha = 0.3f),
                    focusedTextColor = safaColors.textPrimary,
                    unfocusedTextColor = safaColors.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hadith_search_field")
            )

            Spacer(modifier = Modifier.height(SafaSpacing.sm))

            // Collection filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.hadithCollections.forEach { coll ->
                    val isSelected = coll == uiState.selectedHadithCollection
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectHadithCollection(coll) },
                        label = {
                            Text(
                                text = coll,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = safaColors.goldPrimary,
                            selectedLabelColor = SafaNavyDark,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = safaColors.textPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(SafaSpacing.pillRadius)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SafaSpacing.sm))

            // Hadith List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(SafaSpacing.sm)
            ) {
                // Hadith of the Day
                if (uiState.selectedHadithCollection == "All" && uiState.hadithSearchQuery.isBlank() && uiState.hadithOfTheDay != null) {
                    item {
                        HadithOfTheDayCard(
                            hadith = uiState.hadithOfTheDay!!,
                            onCopy = { copyHadithToClipboard(context, uiState.hadithOfTheDay!!) },
                            onShare = { shareHadith(context, uiState.hadithOfTheDay!!) }
                        )
                    }
                }

                items(uiState.hadithList, key = { it.id }) { hadith ->
                    HadithItemCard(
                        hadith = hadith,
                        onToggleFavorite = { viewModel.toggleHadithFavorite(hadith) },
                        onCopy = { copyHadithToClipboard(context, hadith) },
                        onShare = { shareHadith(context, hadith) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HadithOfTheDayCard(
    hadith: HadithEntity,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hadith_of_the_day_card"),
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
                            listOf(Color(0xFF0B132B), Color(0xFF16254F))
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
                            text = "HADITH OF THE DAY",
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
                    text = hadith.arabicText,
                    style = ArabicDisplayStyle,
                    fontSize = 22.sp,
                    lineHeight = 38.sp,
                    color = safaColors.goldPrimary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "\"${hadith.translation}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFDFBF7),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "— ${hadith.narrator} • ${hadith.collection}",
                    style = MaterialTheme.typography.labelSmall,
                    color = safaColors.goldChampagne,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun HadithItemCard(
    hadith: HadithEntity,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val safaColors = LocalSafaColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hadith_card_${hadith.id}"),
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
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
                        text = hadith.collection,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
                    )
                    Text(
                        text = "Hadith #${hadith.hadithNumber} • ${hadith.chapter}",
                        style = MaterialTheme.typography.labelSmall,
                        color = safaColors.textSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(if (safaColors.isLuxuryNavy) Color(0xFF0F261D) else Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = hadith.authenticity,
                            style = MaterialTheme.typography.labelSmall,
                            color = IslamicGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (hadith.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (hadith.isFavorite) safaColors.goldPrimary else safaColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = safaColors.textSecondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = safaColors.textSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic text
            Text(
                text = hadith.arabicText,
                style = ArabicDisplayStyle,
                fontSize = 22.sp,
                lineHeight = 38.sp,
                textAlign = TextAlign.Right,
                color = safaColors.goldChampagne,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // English translation
            Text(
                text = "\"${hadith.translation}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = safaColors.textPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Narrated by: ${hadith.narrator}",
                style = MaterialTheme.typography.bodySmall,
                color = safaColors.textSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

private fun copyHadithToClipboard(context: Context, hadith: HadithEntity) {
    val text = "${hadith.collection} - #${hadith.hadithNumber}\n\n${hadith.arabicText}\n\n\"${hadith.translation}\"\n\nNarrated by: ${hadith.narrator} (${hadith.authenticity})"
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Hadith", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun shareHadith(context: Context, hadith: HadithEntity) {
    val text = "${hadith.collection} - #${hadith.hadithNumber}\n\n${hadith.arabicText}\n\n\"${hadith.translation}\"\n\nNarrated by: ${hadith.narrator} (${hadith.authenticity}) — via Safa App"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Hadith"))
}
