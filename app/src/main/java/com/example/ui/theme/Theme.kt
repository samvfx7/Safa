package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ==========================================
// SAFA LUXURY COLOR SCHEMES
// ==========================================

// 0. Safa Warm Sand & Camel (Reference Mockup "Sillar" Design)
val SafaSandColorScheme = lightColorScheme(
    primary = SafaCamelPrimary,
    onPrimary = Color.White,
    primaryContainer = SafaSandSurfaceElevated,
    onPrimaryContainer = SafaTextEspresso,
    secondary = SafaCamelDark,
    onSecondary = Color.White,
    secondaryContainer = SafaSandSurfaceElevated,
    onSecondaryContainer = SafaTextEspresso,
    tertiary = SafaCamelLight,
    onTertiary = SafaTextEspresso,
    background = SafaSandCanvas,
    onBackground = SafaTextEspresso,
    surface = SafaSandSurface,
    onSurface = SafaTextEspresso,
    surfaceVariant = SafaSandSurfaceElevated,
    onSurfaceVariant = SafaTextTaupe,
    outline = SafaSandBorder,
    outlineVariant = SafaSandDivider
)

// 1. Signature Safa Luxury Dark: Midnight Navy & Imperial 24K Gold
val SafaLuxuryDarkColorScheme = darkColorScheme(
    primary = SafaGoldPrimary,
    onPrimary = SafaNavyMidnight,
    primaryContainer = SafaNavySurfaceElevated,
    onPrimaryContainer = SafaGoldChampagne,
    secondary = SafaGoldChampagne,
    onSecondary = SafaNavyDark,
    secondaryContainer = SafaNavySurface,
    onSecondaryContainer = SafaGoldLight,
    tertiary = SafaGoldBright,
    onTertiary = SafaNavyMidnight,
    background = SafaNavyDark,
    onBackground = SafaTextWhite,
    surface = SafaNavySurface,
    onSurface = SafaTextWhite,
    surfaceVariant = SafaNavySurfaceElevated,
    onSurfaceVariant = SafaTextMuted,
    outline = SafaGoldBorder,
    outlineVariant = SafaNavyBorder
)

// 2. Safa Luxury Light: Alabaster Pearl & Royal Navy with Gold Accents
val SafaLuxuryLightColorScheme = lightColorScheme(
    primary = SafaNavyDark,
    onPrimary = SafaGoldChampagne,
    primaryContainer = SafaGoldLight,
    onPrimaryContainer = SafaNavyDark,
    secondary = SafaGoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = SafaLightSurfaceVariant,
    onSecondaryContainer = SafaNavyDark,
    tertiary = SafaGoldDark,
    onTertiary = Color.White,
    background = SafaLightCanvas,
    onBackground = SafaTextDark,
    surface = SafaLightSurface,
    onSurface = SafaTextDark,
    surfaceVariant = SafaLightSurfaceVariant,
    onSurfaceVariant = SafaTextDarkMuted,
    outline = SafaGoldBorder,
    outlineVariant = SafaLightBorder
)

// 3. Safa Royal Midnight: Obsidian Navy & 24K Polished Gold
val SafaRoyalMidnightColorScheme = darkColorScheme(
    primary = SafaGoldBright,
    onPrimary = SafaNavyMidnight,
    primaryContainer = SafaNavyDeep,
    onPrimaryContainer = SafaGoldChampagne,
    secondary = SafaGoldPrimary,
    onSecondary = SafaNavyMidnight,
    secondaryContainer = SafaNavySurface,
    onSecondaryContainer = SafaGoldLight,
    tertiary = SafaEmerald,
    onTertiary = Color.White,
    background = SafaNavyMidnight,
    onBackground = SafaTextWhite,
    surface = SafaNavySurface,
    onSurface = SafaTextWhite,
    surfaceVariant = SafaNavySurfaceElevated,
    onSurfaceVariant = SafaTextMuted,
    outline = SafaGoldPrimary,
    outlineVariant = SafaNavyBorder
)

// 4. Classic Noor Terracotta & Cream
val NoorClassicColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = TextLight,
    primaryContainer = GoldLight,
    onPrimaryContainer = TextDark,
    secondary = AccentGold,
    onSecondary = Color.White,
    secondaryContainer = WarmSand,
    onSecondaryContainer = TextDark,
    tertiary = GoldDark,
    onTertiary = TextLight,
    background = CreamBackground,
    onBackground = TextDark,
    surface = CardBackground,
    onSurface = TextDark,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DividerGold,
    outlineVariant = WarmSand
)

// 5. Safa Imperial Emerald: Midnight Forest & 24K Polished Gold
val SafaEmeraldColorScheme = darkColorScheme(
    primary = SafaGoldPrimary,
    onPrimary = EmeraldCanvas,
    primaryContainer = EmeraldSurfaceElevated,
    onPrimaryContainer = SafaGoldChampagne,
    secondary = SafaGoldChampagne,
    onSecondary = EmeraldCanvas,
    secondaryContainer = EmeraldSurface,
    onSecondaryContainer = SafaGoldLight,
    tertiary = SafaGoldBright,
    onTertiary = EmeraldCanvas,
    background = EmeraldCanvas,
    onBackground = SafaTextWhite,
    surface = EmeraldSurface,
    onSurface = SafaTextWhite,
    surfaceVariant = EmeraldSurfaceElevated,
    onSurfaceVariant = EmeraldTextMuted,
    outline = SafaGoldBorder,
    outlineVariant = EmeraldBorder
)

// 6. Safa Velvet Plum: Deep Aubergine & Shimmering Rose Gold
val SafaRosePlumColorScheme = darkColorScheme(
    primary = RoseGoldPrimary,
    onPrimary = RosePlumCanvas,
    primaryContainer = RosePlumSurfaceElevated,
    onPrimaryContainer = RoseGoldChampagne,
    secondary = RoseGoldChampagne,
    onSecondary = RosePlumCanvas,
    secondaryContainer = RosePlumSurface,
    onSecondaryContainer = RoseGoldChampagne,
    tertiary = SafaGoldBright,
    onTertiary = RosePlumCanvas,
    background = RosePlumCanvas,
    onBackground = SafaTextWhite,
    surface = RosePlumSurface,
    onSurface = SafaTextWhite,
    surfaceVariant = RosePlumSurfaceElevated,
    onSurfaceVariant = RosePlumTextMuted,
    outline = RoseGoldPrimary,
    outlineVariant = RosePlumBorder
)

// 7. Safa Aegean Sapphire: Deep Ocean & Marine Gold
val SafaSapphireColorScheme = darkColorScheme(
    primary = SapphireGoldPrimary,
    onPrimary = SapphireCanvas,
    primaryContainer = SapphireSurfaceElevated,
    onPrimaryContainer = SafaGoldChampagne,
    secondary = SafaGoldChampagne,
    onSecondary = SapphireCanvas,
    secondaryContainer = SapphireSurface,
    onSecondaryContainer = SafaGoldLight,
    tertiary = SafaGoldBright,
    onTertiary = SapphireCanvas,
    background = SapphireCanvas,
    onBackground = SafaTextWhite,
    surface = SapphireSurface,
    onSurface = SafaTextWhite,
    surfaceVariant = SapphireSurfaceElevated,
    onSurfaceVariant = SapphireTextMuted,
    outline = SapphireGoldPrimary,
    outlineVariant = SapphireBorder
)

// 8. Safa Oasis Sage: Linen Sage, Olive & Bronze Gold (Light Luxury)
val SafaSageColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = Color.White,
    primaryContainer = SageSurfaceElevated,
    onPrimaryContainer = SageTextEspresso,
    secondary = SageBronze,
    onSecondary = Color.White,
    secondaryContainer = SageSurfaceElevated,
    onSecondaryContainer = SageTextEspresso,
    tertiary = SafaCamelPrimary,
    onTertiary = Color.White,
    background = SageCanvas,
    onBackground = SageTextEspresso,
    surface = SageSurface,
    onSurface = SageTextEspresso,
    surfaceVariant = SageSurfaceElevated,
    onSurfaceVariant = SageTextMuted,
    outline = SageBorder,
    outlineVariant = SageBorder
)

// 9. Safa Mocha Royale: Espresso Roast, Cocoa & Bronze Gold (Dark Luxury)
val SafaMochaColorScheme = darkColorScheme(
    primary = MochaBronzePrimary,
    onPrimary = MochaCanvas,
    primaryContainer = MochaSurfaceElevated,
    onPrimaryContainer = MochaBronzeLight,
    secondary = MochaBronzeLight,
    onSecondary = MochaCanvas,
    secondaryContainer = MochaSurface,
    onSecondaryContainer = MochaBronzeLight,
    tertiary = SafaGoldBright,
    onTertiary = MochaCanvas,
    background = MochaCanvas,
    onBackground = SafaTextWhite,
    surface = MochaSurface,
    onSurface = SafaTextWhite,
    surfaceVariant = MochaSurfaceElevated,
    onSurfaceVariant = MochaTextMuted,
    outline = MochaBronzePrimary,
    outlineVariant = MochaBorder
)

// ==========================================
// SAFA CUSTOM COLOR EXTENSION SYSTEM
// ==========================================
@Immutable
data class SafaCustomColors(
    val goldPrimary: Color,
    val goldChampagne: Color,
    val goldGlow: Color,
    val goldBorder: Color,
    val navyBackground: Color,
    val navySurface: Color,
    val navyElevated: Color,
    val navyBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textGold: Color,
    val isLuxuryNavy: Boolean = true
)

val SafaSandCustomColors = SafaCustomColors(
    goldPrimary = SafaCamelPrimary,
    goldChampagne = SafaCamelLight,
    goldGlow = SafaCamelGlow,
    goldBorder = SafaSandBorder,
    navyBackground = SafaSandCanvas,
    navySurface = SafaSandSurface,
    navyElevated = SafaSandSurfaceElevated,
    navyBorder = SafaSandBorder,
    textPrimary = SafaTextEspresso,
    textSecondary = SafaTextTaupe,
    textGold = SafaCamelDark,
    isLuxuryNavy = false
)

val SafaDarkCustomColors = SafaCustomColors(
    goldPrimary = SafaGoldPrimary,
    goldChampagne = SafaGoldChampagne,
    goldGlow = SafaGoldGlow,
    goldBorder = SafaGoldBorder,
    navyBackground = SafaNavyDark,
    navySurface = SafaNavySurface,
    navyElevated = SafaNavySurfaceElevated,
    navyBorder = SafaNavyBorder,
    textPrimary = SafaTextWhite,
    textSecondary = SafaTextMuted,
    textGold = SafaTextGold,
    isLuxuryNavy = true
)

val SafaLightCustomColors = SafaCustomColors(
    goldPrimary = SafaGoldPrimary,
    goldChampagne = SafaGoldChampagne,
    goldGlow = SafaGoldGlow,
    goldBorder = SafaGoldBorder,
    navyBackground = SafaLightCanvas,
    navySurface = SafaLightSurface,
    navyElevated = SafaLightSurfaceVariant,
    navyBorder = SafaLightBorder,
    textPrimary = SafaTextDark,
    textSecondary = SafaTextDarkMuted,
    textGold = SafaGoldDark,
    isLuxuryNavy = false
)

val SafaEmeraldCustomColors = SafaCustomColors(
    goldPrimary = SafaGoldPrimary,
    goldChampagne = SafaGoldChampagne,
    goldGlow = EmeraldGlow,
    goldBorder = SafaGoldBorder,
    navyBackground = EmeraldCanvas,
    navySurface = EmeraldSurface,
    navyElevated = EmeraldSurfaceElevated,
    navyBorder = EmeraldBorder,
    textPrimary = SafaTextWhite,
    textSecondary = EmeraldTextMuted,
    textGold = SafaGoldChampagne,
    isLuxuryNavy = true
)

val SafaRosePlumCustomColors = SafaCustomColors(
    goldPrimary = RoseGoldPrimary,
    goldChampagne = RoseGoldChampagne,
    goldGlow = RoseGoldGlow,
    goldBorder = RoseGoldPrimary,
    navyBackground = RosePlumCanvas,
    navySurface = RosePlumSurface,
    navyElevated = RosePlumSurfaceElevated,
    navyBorder = RosePlumBorder,
    textPrimary = SafaTextWhite,
    textSecondary = RosePlumTextMuted,
    textGold = RoseGoldChampagne,
    isLuxuryNavy = true
)

val SafaSapphireCustomColors = SafaCustomColors(
    goldPrimary = SapphireGoldPrimary,
    goldChampagne = SafaGoldChampagne,
    goldGlow = SapphireGlow,
    goldBorder = SapphireGoldPrimary,
    navyBackground = SapphireCanvas,
    navySurface = SapphireSurface,
    navyElevated = SapphireSurfaceElevated,
    navyBorder = SapphireBorder,
    textPrimary = SafaTextWhite,
    textSecondary = SapphireTextMuted,
    textGold = SafaGoldChampagne,
    isLuxuryNavy = true
)

val SafaSageCustomColors = SafaCustomColors(
    goldPrimary = SagePrimary,
    goldChampagne = SageBronze,
    goldGlow = SageGlow,
    goldBorder = SageBorder,
    navyBackground = SageCanvas,
    navySurface = SageSurface,
    navyElevated = SageSurfaceElevated,
    navyBorder = SageBorder,
    textPrimary = SageTextEspresso,
    textSecondary = SageTextMuted,
    textGold = SageBronze,
    isLuxuryNavy = false
)

val SafaMochaCustomColors = SafaCustomColors(
    goldPrimary = MochaBronzePrimary,
    goldChampagne = MochaBronzeLight,
    goldGlow = MochaGlow,
    goldBorder = MochaBronzePrimary,
    navyBackground = MochaCanvas,
    navySurface = MochaSurface,
    navyElevated = MochaSurfaceElevated,
    navyBorder = MochaBorder,
    textPrimary = SafaTextWhite,
    textSecondary = MochaTextMuted,
    textGold = MochaBronzeLight,
    isLuxuryNavy = true
)

val LocalSafaColors = staticCompositionLocalOf { SafaSandCustomColors }

// ==========================================
// MAIN APP THEME COMPOSABLE
// ==========================================
@Composable
fun IslamicAppTheme(
    selectedTheme: String = "safa_sand",
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val (colorScheme: ColorScheme, customColors: SafaCustomColors) = when (selectedTheme) {
        "safa_sand" -> Pair(SafaSandColorScheme, SafaSandCustomColors)
        "safa_luxury" -> Pair(SafaLuxuryDarkColorScheme, SafaDarkCustomColors)
        "safa_royal" -> Pair(SafaRoyalMidnightColorScheme, SafaDarkCustomColors.copy(navyBackground = SafaNavyMidnight))
        "safa_light" -> Pair(SafaLuxuryLightColorScheme, SafaLightCustomColors)
        "safa_emerald" -> Pair(SafaEmeraldColorScheme, SafaEmeraldCustomColors)
        "safa_rose_gold" -> Pair(SafaRosePlumColorScheme, SafaRosePlumCustomColors)
        "safa_sapphire" -> Pair(SafaSapphireColorScheme, SafaSapphireCustomColors)
        "safa_sage" -> Pair(SafaSageColorScheme, SafaSageCustomColors)
        "safa_mocha" -> Pair(SafaMochaColorScheme, SafaMochaCustomColors)
        "classic_warm" -> Pair(NoorClassicColorScheme, SafaLightCustomColors.copy(
            goldPrimary = TerracottaPrimary,
            navyBackground = CreamBackground,
            navySurface = CardBackground,
            navyElevated = CreamSurfaceVariant,
            textPrimary = TextDark,
            textSecondary = TextMuted,
            textGold = AccentGold,
            isLuxuryNavy = false
        ))
        else -> Pair(SafaSandColorScheme, SafaSandCustomColors)
    }

    CompositionLocalProvider(
        LocalSafaColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SafaTypography,
            content = content
        )
    }
}

// Backwards compatibility wrappers
@Composable
fun SafaTheme(
    selectedTheme: String = "safa_luxury",
    content: @Composable () -> Unit
) {
    IslamicAppTheme(selectedTheme = selectedTheme, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    IslamicAppTheme(
        selectedTheme = if (darkTheme) "safa_luxury" else "safa_luxury",
        darkTheme = darkTheme,
        content = content
    )
}
