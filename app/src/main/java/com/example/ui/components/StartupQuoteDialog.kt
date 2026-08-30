package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.LocalSafaColors
import kotlinx.coroutines.delay

data class IslamicQuote(
    val text: String,
    val source: String,
    val isQuran: Boolean
)

val startupQuotes = listOf(
    IslamicQuote(
        "And He found you lost and guided [you].",
        "Quran 93:7",
        true
    ),
    IslamicQuote(
        "So verily, with the hardship, there is relief.",
        "Quran 94:5",
        true
    ),
    IslamicQuote(
        "Do not lose hope, nor be sad.",
        "Quran 3:139",
        true
    ),
    IslamicQuote(
        "The best among you are those who have the best manners and character.",
        "Sahih al-Bukhari",
        false
    ),
    IslamicQuote(
        "Richness is not having many possessions, but richness is being content with oneself.",
        "Sahih Muslim",
        false
    ),
    IslamicQuote(
        "Indeed, Allah does not look at your appearance or wealth, but He looks at your hearts and your deeds.",
        "Sahih Muslim",
        false
    )
)

@Composable
fun StartupQuoteDialog() {
    var showDialog by rememberSaveable { mutableStateOf(true) }
    var visible by remember { mutableStateOf(false) }
    
    val quote = remember { startupQuotes.random() }
    val safaColors = LocalSafaColors.current

    LaunchedEffect(Unit) {
        // Slight delay to allow app to render before popping the dialog
        delay(300)
        visible = true
        // Auto dismiss after 3.5 seconds
        delay(3500)
        visible = false
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { 
                visible = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.95f, animationSpec = tween(400)),
                exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.95f, animationSpec = tween(300))
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = androidx.compose.ui.graphics.RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (safaColors.isLuxuryNavy) safaColors.navyElevated else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = safaColors.goldPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = safaColors.goldPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Daily Inspiration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = safaColors.goldPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "\"${quote.text}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "- ${quote.source}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Handle actual dismissal after exit animation finishes
            LaunchedEffect(visible) {
                if (!visible) {
                    delay(300) // wait for exit animation
                    showDialog = false
                }
            }
        }
    }
}
