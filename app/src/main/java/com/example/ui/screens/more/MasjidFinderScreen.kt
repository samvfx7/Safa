package com.example.ui.screens.more

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

data class MasjidInfo(
    val name: String,
    val address: String,
    val distanceKm: Double,
    val phone: String,
    val facilitatesJumuah: Boolean = true,
    val coordinates: Pair<Double, Double>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasjidFinderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val safaColors = LocalSafaColors.current

    val nearbyMasjids = listOf(
        MasjidInfo(
            name = "Central Islamic Mosque & Cultural Centre",
            address = "146 Park Road, Central City",
            distanceKm = 0.8,
            phone = "+44 20 7724 3363",
            facilitatesJumuah = true,
            coordinates = Pair(51.5283, -0.1658)
        ),
        MasjidInfo(
            name = "Al-Rahman Mosque & Community Hall",
            address = "82 High Street, West End",
            distanceKm = 1.4,
            phone = "+44 20 7935 6078",
            facilitatesJumuah = true,
            coordinates = Pair(51.5155, -0.1419)
        ),
        MasjidInfo(
            name = "East London Islamic Academy & Masjid",
            address = "88-92 Whitechapel Road",
            distanceKm = 2.7,
            phone = "+44 20 7650 3000",
            facilitatesJumuah = true,
            coordinates = Pair(51.5186, -0.0654)
        ),
        MasjidInfo(
            name = "Baitul Mukarram Cultural Masjid",
            address = "34 Oxford Avenue",
            distanceKm = 3.9,
            phone = "+44 20 8902 4432",
            facilitatesJumuah = true,
            coordinates = Pair(51.5033, -0.1195)
        )
    )

    fun openMaps(coords: Pair<Double, Double>, label: String) {
        val gmmIntentUri = Uri.parse("geo:${coords.first},${coords.second}?q=${Uri.encode(label)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${coords.first},${coords.second}"))
            context.startActivity(webIntent)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nearby Masjids",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = SafaSpacing.screenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SafaSpacing.md)
        ) {
            item {
                Text(
                    text = "Showing verified mosques near your active location:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = safaColors.textSecondary
                )
            }

            items(nearbyMasjids) { masjid ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(safaColors.goldGlow, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Mosque,
                                        contentDescription = null,
                                        tint = safaColors.goldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = masjid.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = safaColors.textPrimary
                                    )
                                    Text(
                                        text = masjid.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = safaColors.textSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(safaColors.goldGlow, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${masjid.distanceKm} km",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = safaColors.goldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { openMaps(masjid.coordinates, masjid.name) },
                                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                                shape = RoundedCornerShape(SafaSpacing.pillRadius),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = SafaNavyDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Directions", color = SafaNavyDark, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${masjid.phone}"))
                                    context.startActivity(callIntent)
                                },
                                border = BorderStroke(1.dp, safaColors.goldPrimary),
                                shape = RoundedCornerShape(SafaSpacing.pillRadius)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = safaColors.goldPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
