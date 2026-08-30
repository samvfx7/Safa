package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Brush
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
import java.util.Calendar

data class IslamicQuote(
    val text: String,
    val source: String,
    val isQuran: Boolean
)

val startupQuotes = listOf(
    IslamicQuote(
        "And He found you lost and guided [you].",
        "Surah Ad-Duha 93:7",
        true
    ),
    IslamicQuote(
        "So verily, with the hardship, there is relief.",
        "Surah Ash-Sharh 94:5",
        true
    ),
    IslamicQuote(
        "Do not lose hope, nor be sad.",
        "Surah Ali 'Imran 3:139",
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
    ),
    IslamicQuote(
        "My mercy encompasses all things.",
        "Surah Al-A'raf 7:156",
        true
    ),
    IslamicQuote(
        "Indeed, with hardship [will be] ease.",
        "Surah Ash-Sharh 94:6",
        true
    ),
    IslamicQuote(
        "And He is with you wherever you are.",
        "Surah Al-Hadid 57:4",
        true
    ),
    IslamicQuote(
        "Allah does not burden a soul beyond that it can bear.",
        "Surah Al-Baqarah 2:286",
        true
    ),
    IslamicQuote(
        "Call upon Me; I will respond to you.",
        "Surah Ghafir 40:60",
        true
    ),
    IslamicQuote(
        "If you are grateful, I will surely increase you [in favor].",
        "Surah Ibrahim 14:7",
        true
    ),
    IslamicQuote(
        "So remember Me; I will remember you.",
        "Surah Al-Baqarah 2:152",
        true
    ),
    IslamicQuote(
        "And rely upon Allah; and sufficient is Allah as Disposer of affairs.",
        "Surah Al-Ahzab 33:3",
        true
    ),
    IslamicQuote(
        "Indeed, the patient will be given their reward without account.",
        "Surah Az-Zumar 39:10",
        true
    ),
    IslamicQuote(
        "Verily, in the remembrance of Allah do hearts find rest.",
        "Surah Ar-Ra'd 13:28",
        true
    ),
    IslamicQuote(
        "O you who have believed, seek help through patience and prayer.",
        "Surah Al-Baqarah 2:153",
        true
    ),
    IslamicQuote(
        "The most beloved of deeds to Allah are those that are most consistent, even if they are small.",
        "Sahih al-Bukhari",
        false
    ),
    IslamicQuote(
        "Whosoever takes a path in search of knowledge, Allah will make easy for him a path to Paradise.",
        "Sahih Muslim",
        false
    ),
    IslamicQuote(
        "Speak that which is good or remain silent.",
        "Sahih al-Bukhari",
        false
    )
)

@Composable
fun StartupQuoteDialog() {
    var showDialog by rememberSaveable { mutableStateOf(true) }
    var visible by remember { mutableStateOf(false) }
    
    // Choose a unique, consistent quote for each calendar day of the year
    val quote = remember {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val index = (dayOfYear + year) % startupQuotes.size
        startupQuotes[index]
    }
    
    val safaColors = LocalSafaColors.current

    LaunchedEffect(Unit) {
        // Soft delay before popping the elegant overlay
        delay(300)
        visible = true
        // Allow enough time to read, then auto-dismiss (6 seconds)
        delay(6000)
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { visible = false },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.90f, animationSpec = tween(500)),
                    exit = fadeOut(tween(400)) + scaleOut(targetScale = 0.90f, animationSpec = tween(400))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .wrapContentHeight()
                            .clickable(enabled = false) { } // Prevent background click from dismissing
                            .padding(16.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.5.dp, safaColors.goldPrimary.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (safaColors.isLuxuryNavy) {
                                safaColors.navyElevated.copy(alpha = 0.96f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Ornament row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .height(1.dp)
                                        .width(32.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    safaColors.goldPrimary,
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .height(1.dp)
                                        .width(32.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    safaColors.goldPrimary,
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = safaColors.goldPrimary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "DAILY INSPIRATION",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    letterSpacing = 2.sp
                                ),
                                fontWeight = FontWeight.Bold,
                                color = safaColors.goldPrimary
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Quotation Box frame
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background low-opacity giant quotes
                                Text(
                                    text = "“",
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = safaColors.goldPrimary.copy(alpha = 0.08f),
                                    modifier = Modifier.align(Alignment.TopStart)
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = quote.text,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = 18.sp,
                                            lineHeight = 28.sp
                                        ),
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = safaColors.textPrimary
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Small elegant divider line
                                    Box(
                                        modifier = Modifier
                                            .height(1.dp)
                                            .width(48.dp)
                                            .background(safaColors.goldPrimary.copy(alpha = 0.3f))
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = quote.source,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = safaColors.goldPrimary.copy(alpha = 0.9f)
                                    )
                                    
                                    if (quote.isQuran) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Holy Qur'an",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Light,
                                            color = safaColors.textSecondary.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Text(
                                    text = "”",
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = safaColors.goldPrimary.copy(alpha = 0.08f),
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // Interactive Beautiful Button
                            Button(
                                onClick = { visible = false },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = safaColors.goldPrimary,
                                    contentColor = if (safaColors.isLuxuryNavy) safaColors.navyBackground else Color.White
                                ),
                                border = BorderStroke(1.dp, safaColors.goldBorder),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "Begin Beautifully",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
            
            // Handle actual dismissal after exit animation finishes
            LaunchedEffect(visible) {
                if (!visible) {
                    delay(400) // wait for exit animation
                    showDialog = false
                }
            }
        }
    }
}
