package com.example.ui.screens.more

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.IslamicApp
import com.example.data.repository.SettingsRepository
import com.example.ui.permission.SafaPermissionDialog
import com.example.ui.theme.ArabicDisplayStyle
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaGoldPrimary
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing
import com.example.ui.theme.TerracottaPrimary

data class ThemeOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val isLuxury: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by settingsRepository.settingsState.collectAsState()
    val safaColors = LocalSafaColors.current
    var showMethodDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp
    val permissionManager = app.permissionManager
    val permState by permissionManager.permissionState.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }

    val themeOptions = listOf(
        ThemeOption(
            id = "safa_sand",
            title = "Safa Desert Sand & Camel",
            subtitle = "Warm Sand Cream, Camel Gold & Espresso Brown (Mockup)",
            primaryColor = Color(0xFFC59E58),
            backgroundColor = Color(0xFFF5EFE6),
            surfaceColor = Color(0xFFFAF6F0),
            isLuxury = true
        ),
        ThemeOption(
            id = "safa_luxury",
            title = "Safa Signature Luxury",
            subtitle = "Midnight Navy & Radiant Imperial 24K Gold",
            primaryColor = Color(0xFFD4AF37),
            backgroundColor = Color(0xFF0B132B),
            surfaceColor = Color(0xFF111E3E)
        ),
        ThemeOption(
            id = "safa_royal",
            title = "Safa Royal Obsidian",
            subtitle = "Deep Obsidian & Polished 24K Gold",
            primaryColor = Color(0xFFFFDF73),
            backgroundColor = Color(0xFF070D1E),
            surfaceColor = Color(0xFF101B39)
        ),
        ThemeOption(
            id = "safa_light",
            title = "Safa Alabaster Pearl",
            subtitle = "Pearl Ivory Canvas & Royal Navy with Gold",
            primaryColor = Color(0xFF0B132B),
            backgroundColor = Color(0xFFF7F5F0),
            surfaceColor = Color(0xFFFFFFFF)
        ),
        ThemeOption(
            id = "safa_emerald",
            title = "Safa Imperial Emerald",
            subtitle = "Midnight Forest Velvet Emerald & 24K Polished Gold",
            primaryColor = Color(0xFFD4AF37),
            backgroundColor = Color(0xFF061A11),
            surfaceColor = Color(0xFF0D2A1D),
            isLuxury = true
        ),
        ThemeOption(
            id = "safa_rose_gold",
            title = "Safa Velvet Plum & Rose Gold",
            subtitle = "Royal Damask Aubergine & Shimmering Rose Gold",
            primaryColor = Color(0xFFE5A696),
            backgroundColor = Color(0xFF160A18),
            surfaceColor = Color(0xFF231227),
            isLuxury = true
        ),
        ThemeOption(
            id = "safa_sapphire",
            title = "Safa Aegean Sapphire",
            subtitle = "Mediterranean Abyss Sapphire & Marine Gold",
            primaryColor = Color(0xFFE2BA4B),
            backgroundColor = Color(0xFF061420),
            surfaceColor = Color(0xFF0B2134),
            isLuxury = true
        ),
        ThemeOption(
            id = "safa_sage",
            title = "Safa Oasis Sage & Olive",
            subtitle = "Linen Sage Canvas, Olive & Camel Bronze",
            primaryColor = Color(0xFF2B5437),
            backgroundColor = Color(0xFFF1F4EE),
            surfaceColor = Color(0xFFFAFBF8),
            isLuxury = true
        ),
        ThemeOption(
            id = "safa_mocha",
            title = "Safa Mocha Royale",
            subtitle = "Dark Roast Espresso Cocoa & Antique Bronze Gold",
            primaryColor = Color(0xFFDCA45A),
            backgroundColor = Color(0xFF140D09),
            surfaceColor = Color(0xFF201610),
            isLuxury = true
        ),
        ThemeOption(
            id = "classic_warm",
            title = "Noor Classic Warm",
            subtitle = "Terracotta & Warm Cream Sand",
            primaryColor = Color(0xFFD4745C),
            backgroundColor = Color(0xFFF5E6D3),
            surfaceColor = Color(0xFFFAFAFA),
            isLuxury = false
        )
    )

    val calculationMethods = listOf(
        Pair(2, "Islamic Society of North America (ISNA)"),
        Pair(3, "Muslim World League (MWL)"),
        Pair(4, "Umm Al-Qura University, Makkah"),
        Pair(5, "Egyptian General Authority of Survey"),
        Pair(1, "University of Islamic Sciences, Karachi"),
        Pair(7, "Institute of Geophysics, University of Tehran"),
        Pair(12, "Union Des Organisations Islamiques De France"),
        Pair(13, "Diyanet İşleri Başkanlığı, Turkey"),
        Pair(99, "Hanafi")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Identity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
            // SAFA BRAND IDENTITY & THEME STUDIO
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = safaColors.goldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAFA BRAND IDENTITY & LUXURY THEME",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            // Theme Options List
            items(themeOptions) { option ->
                val isSelected = settings.selectedTheme == option.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(SafaSpacing.cardRadius))
                        .clickable { settingsRepository.updateTheme(option.id) }
                        .testTag("theme_card_${option.id}"),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            if (safaColors.isLuxuryNavy) safaColors.navyElevated else MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) safaColors.goldPrimary else safaColors.navyBorder.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SafaSpacing.cardContentPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Theme Color Palette Swatch
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .borderStrokeOrNone(isSelected, safaColors.goldPrimary)
                                    .padding(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp, 36.dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                        .background(option.backgroundColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp, 36.dp)
                                        .background(option.surfaceColor)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp, 36.dp)
                                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                        .background(option.primaryColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) safaColors.goldPrimary else safaColors.textPrimary
                                )
                                Text(
                                    text = option.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = safaColors.textSecondary
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(safaColors.goldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = SafaNavyDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Material 3 Typography & Refined Spacing Showcase
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(SafaSpacing.cardContentPadding)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = null,
                                tint = safaColors.goldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Material3 Typography & Craft",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = ArabicDisplayStyle,
                            color = safaColors.goldPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Safa combines majestic Serif headings, high-legibility body rhythm, and optical 1.2sp letter tracking for an elevated spiritual experience.",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(safaColors.goldGlow, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REFINED SPACING",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.goldPrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "20dp Grid • Dual Hairline Stroke",
                                style = MaterialTheme.typography.labelSmall,
                                color = safaColors.textPrimary
                            )
                        }
                    }
                }
            }

            // Calculation Method Card
            item {
                Text(
                    text = "PRAYER CALCULATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMethodDialog = true }
                        .testTag("calc_method_setting_card"),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
                        Text(
                            text = "Calculation Method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = settings.calculationMethodName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = safaColors.goldPrimary
                        )
                    }
                }
            }

            // Prayer Alerts & Notifications
            item {
                Text(
                    text = "PRAYER ALERTS & NOTIFICATIONS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
                        PrayerNotificationToggleRow("Fajr Adhan Alert", settings.notifyFajr, safaColors.goldPrimary, safaColors.textPrimary) {
                            settingsRepository.updateNotificationSetting("fajr", it)
                        }
                        PrayerNotificationToggleRow("Dhuhr Adhan Alert", settings.notifyDhuhr, safaColors.goldPrimary, safaColors.textPrimary) {
                            settingsRepository.updateNotificationSetting("dhuhr", it)
                        }
                        PrayerNotificationToggleRow("Asr Adhan Alert", settings.notifyAsr, safaColors.goldPrimary, safaColors.textPrimary) {
                            settingsRepository.updateNotificationSetting("asr", it)
                        }
                        PrayerNotificationToggleRow("Maghrib Adhan Alert", settings.notifyMaghrib, safaColors.goldPrimary, safaColors.textPrimary) {
                            settingsRepository.updateNotificationSetting("maghrib", it)
                        }
                        PrayerNotificationToggleRow("Isha Adhan Alert", settings.notifyIsha, safaColors.goldPrimary, safaColors.textPrimary) {
                            settingsRepository.updateNotificationSetting("isha", it)
                        }
                    }
                }
            }

            // Smart Features
            item {
                Text(
                    text = "SMART FEATURES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
                        FeatureToggleRow(
                            icon = Icons.Default.CameraAlt,
                            title = "ML Prayer Mat Verification",
                            subtitle = "Require scanning prayer mat to turn off Fajr alarm",
                            checked = settings.prayerMatDetectionEnabled,
                            accentColor = safaColors.goldPrimary,
                            textColor = safaColors.textPrimary,
                            subtextColor = safaColors.textSecondary,
                            onCheckedChange = { settingsRepository.updateMatDetection(it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FeatureToggleRow(
                            icon = Icons.Default.Vibration,
                            title = "Haptic Vibration Feedback",
                            subtitle = "Tactile pulse during Tasbih counts and Qibla alignment",
                            checked = settings.hapticFeedbackEnabled,
                            accentColor = safaColors.goldPrimary,
                            textColor = safaColors.textPrimary,
                            subtextColor = safaColors.textSecondary,
                            onCheckedChange = { settingsRepository.updateHaptics(it) }
                        )
                    }
                }
            }

            // Permissions & Access
            item {
                Text(
                    text = "APP PERMISSIONS & ACCESS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(SafaSpacing.cardContentPadding),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Location Status Row
                        PermissionStatusRow(
                            icon = Icons.Default.LocationOn,
                            title = "GPS & Fine Location",
                            subtitle = "Used for real-time prayer calculations & Qibla compass",
                            isGranted = permState.hasLocationPermission,
                            accentColor = safaColors.goldPrimary,
                            textColor = safaColors.textPrimary,
                            subtextColor = safaColors.textSecondary
                        )

                        // Notification Status Row
                        PermissionStatusRow(
                            icon = Icons.Default.Notifications,
                            title = "Adhan Alerts & Notifications",
                            subtitle = "Delivers prayer calls, alarms & daily reflections",
                            isGranted = permState.hasNotificationPermission,
                            accentColor = safaColors.goldPrimary,
                            textColor = safaColors.textPrimary,
                            subtextColor = safaColors.textSecondary
                        )

                        // Camera Status Row
                        PermissionStatusRow(
                            icon = Icons.Default.CameraAlt,
                            title = "Camera & Mat Detection",
                            subtitle = "Used for AR Qibla and Fajr prayer mat scanner",
                            isGranted = permState.hasCameraPermission,
                            accentColor = safaColors.goldPrimary,
                            textColor = safaColors.textPrimary,
                            subtextColor = safaColors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showPermissionDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = safaColors.goldPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Manage Permissions",
                                    color = SafaNavyDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { permissionManager.openAppSettings(context) },
                                modifier = Modifier.height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, safaColors.navyBorder.copy(alpha = 0.4f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = safaColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Settings",
                                    color = safaColors.textSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        SafaPermissionDialog(
            isOpen = showPermissionDialog,
            onDismiss = { showPermissionDialog = false },
            onPermissionsUpdated = { permissionManager.checkAllPermissions() }
        )

        if (showMethodDialog) {
            AlertDialog(
                onDismissRequest = { showMethodDialog = false },
                title = {
                    Text(
                        text = "Select Calculation Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.textPrimary
                    )
                },
                text = {
                    LazyColumn(
                        modifier = Modifier.height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(calculationMethods) { (id, name) ->
                            val isSelected = id == settings.calculationMethodId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        settingsRepository.updateCalculationMethod(id, name)
                                        showMethodDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        settingsRepository.updateCalculationMethod(id, name)
                                        showMethodDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = safaColors.goldPrimary,
                                        unselectedColor = safaColors.textSecondary
                                    )
                                )
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) safaColors.goldPrimary else safaColors.textPrimary
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showMethodDialog = false }) {
                        Text("Done", color = safaColors.goldPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun PrayerNotificationToggleRow(
    label: String,
    checked: Boolean,
    accentColor: Color,
    textColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor
            )
        )
    }
}

@Composable
private fun FeatureToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    accentColor: Color,
    textColor: Color,
    subtextColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val iconTint by animateColorAsState(
        targetValue = if (checked) accentColor else subtextColor,
        animationSpec = tween(280),
        label = "toggleIconTint"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (checked) 1.12f else 1.0f,
        animationSpec = tween(280),
        label = "toggleIconScale"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(24.dp)
                    .scale(iconScale)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = subtextColor
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor
            )
        )
    }
}

@Composable
private fun PermissionStatusRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isGranted: Boolean,
    accentColor: Color,
    textColor: Color,
    subtextColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (isGranted) accentColor.copy(alpha = 0.15f) else subtextColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) accentColor else subtextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = subtextColor,
                    fontSize = 11.5.sp
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isGranted) accentColor.copy(alpha = 0.15f) else subtextColor.copy(alpha = 0.12f),
            border = BorderStroke(
                1.dp,
                if (isGranted) accentColor.copy(alpha = 0.4f) else subtextColor.copy(alpha = 0.25f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = if (isGranted) "Active" else "Off",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isGranted) accentColor else subtextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun Modifier.borderStrokeOrNone(isSelected: Boolean, color: Color): Modifier {
    return if (isSelected) {
        this.then(Modifier.background(color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)))
    } else {
        this
    }
}
