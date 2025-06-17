package com.projects.aware.main.settings

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// SUNSET: Orange & Warm
val sunsetLight = generateColorScheme(Color(0xFFFF6F00), isDark = false)
val sunsetDark = generateColorScheme(Color(0xFFEF6C00), isDark = true)

// NEON: Vivid Purple
val neonLight = generateColorScheme(Color(0xFFD500F9), isDark = false)
val neonDark = generateColorScheme(Color(0xFFAA00FF), isDark = true)

// AQUA: Refreshing Teal
val aquaLight = generateColorScheme(Color(0xFF00E5FF), isDark = false)
val aquaDark = generateColorScheme(Color(0xFF00ACC1), isDark = true)

// LUXURY: Royal Blue
val luxuryLight = generateColorScheme(Color(0xFF536DFE), isDark = false)
val luxuryDark = generateColorScheme(Color(0xFF3D5AFE), isDark = true)

val emeraldDark = generateColorScheme(primary = Color(0xFF2E7D32), isDark = true)
val emeraldLight = generateColorScheme(primary = Color(0xFF66BB6A), isDark = false)

val amoledDarkTheme = darkColorScheme(
    // ✅ Key Colors
    primary = Color(0xFF00C853),               // Vivid green
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1B5E20),      // Darker green
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF00E5FF),             // Cyan accent
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color.White,

    tertiary = Color(0xFFFF4081),              // Pink accent
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF880E4F),
    onTertiaryContainer = Color.White,

    // ❌ Error Colors (kept default from M3 dark theme)
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),

    // 🌑 AMOLED Background and Surface
    background = Color(0xFF000000),            // True black
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF0D0D0D),               // Almost black for elevation feel
    onSurface = Color(0xFFE6E1E5),

    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFB0AEB2),

    outline = Color(0xFF737373),
    outlineVariant = Color(0xFF4D4D4D),

    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF69F0AE),         // Light green

    surfaceTint = Color(0xFF00C853),           // Same as primary for elevation
    scrim = Color.Black.copy(alpha = 0.5f)
)


fun generateColorScheme(
    primary: Color,
    isDark: Boolean,
    contrastLevel: Float = 1f
): ColorScheme {
    val seedHsl = primary.toHsl()
    val hue = seedHsl[0]
    val sat = seedHsl[1].coerceIn(0.4f, 1f)
    val light = seedHsl[2].coerceIn(0.3f, 0.7f)

    val secondaryHue = (hue + 45f + (0..15).random()) % 360
    val tertiaryHue = (hue + 90f + (0..30).random()) % 360

    return if (isDark) {
        darkColorScheme(
            primary = Color.hsl(hue, sat, 0.75f),
            onPrimary = Color.Black,
            primaryContainer = Color.hsl(hue, sat, 0.3f),
            onPrimaryContainer = Color.White,

            secondary = Color.hsl(secondaryHue, (sat * 0.9f).coerceIn(0.4f, 0.95f), 0.65f),
            onSecondary = Color.Black,
            secondaryContainer = Color.hsl(secondaryHue, sat, 0.25f),
            onSecondaryContainer = Color.White,

            tertiary = Color.hsl(tertiaryHue, (sat * 0.85f).coerceIn(0.3f, 0.9f), 0.7f),
            onTertiary = Color.Black,
            tertiaryContainer = Color.hsl(tertiaryHue, sat, 0.25f),
            onTertiaryContainer = Color.White,

            error = Color(0xFFF28B82),
            onError = Color(0xFF370000),
            errorContainer = Color(0xFF8C1D18),
            onErrorContainer = Color(0xFFFFDAD6),

            background = Color.hsl(hue, 0.1f, 0.1f),
            onBackground = Color.White,
            surface = Color.hsl(hue, 0.1f, 0.15f),
            onSurface = Color.White,
            surfaceVariant = Color.hsl(hue, 0.2f, 0.25f),
            onSurfaceVariant = Color(0xFFDAD6E0),
            outline = Color(0xFF9E9E9E),
            outlineVariant = Color(0xFF444444),
            scrim = Color.Black.copy(alpha = 0.4f),
            inverseSurface = Color(0xFFE0E0E0),
            inverseOnSurface = Color(0xFF2C2C2C),
            inversePrimary = Color.hsl(hue, sat, 0.45f),
            surfaceTint = Color.hsl(hue, sat, 0.75f),
        )
    } else {
        val bgLightness = 0.97f
        val surfaceLightness = 0.94f
        val variantLightness = 0.90f

        val tintSat = 0.1f + (sat * 0.1f) // subtle, non-white backgrounds

        lightColorScheme(
            primary = Color.hsl(hue, sat, 0.4f),
            onPrimary = Color.White,
            primaryContainer = Color.hsl(hue, sat * 0.8f, 0.9f),
            onPrimaryContainer = Color.hsl(hue, min(sat * 1.5f, 1f), 0.2f),

            secondary = Color.hsl((hue + 30) % 360, max(sat * 0.9f, 0.4f), 0.4f),
            onSecondary = Color.White,
            secondaryContainer = Color.hsl((hue + 30) % 360, max(sat * 0.8f, 0.3f), 0.9f),
            onSecondaryContainer = Color.hsl((hue + 30) % 360, min(sat * 1.5f, 1f), 0.2f),

            tertiary = Color.hsl((hue + 60) % 360, max(sat * 0.8f, 0.3f), 0.4f),
            onTertiary = Color.White,
            tertiaryContainer = Color.hsl((hue + 60) % 360, max(sat * 0.7f, 0.2f), 0.9f),
            onTertiaryContainer = Color.hsl((hue + 60) % 360, min(sat * 1.5f, 1f), 0.2f),

            error = Color(0xFFBA1A1A),
            onError = Color.White,
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            background = Color.hsl(hue, tintSat, bgLightness),
            onBackground = Color(0xFF1C1B1F),
            surface = Color.hsl(hue, tintSat + 0.05f, surfaceLightness),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color.hsl(hue, tintSat + 0.1f, variantLightness),
            onSurfaceVariant = Color(0xFF49454F),

            outline = Color(0xFF79747E),
            outlineVariant = Color(0xFFCAC4D0),
            scrim = Color.Black.copy(alpha = 0.5f),
            inverseSurface = Color(0xFF313033),
            inverseOnSurface = Color(0xFFF4EFF4),
            inversePrimary = Color.hsl(hue, sat * 0.8f, 0.8f),
            surfaceTint = Color.hsl(hue, sat, 0.4f),
        )
    }
}


// HSL conversion extension
fun Color.toHsl(): FloatArray {
    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val h = when {
        delta == 0f -> 0f
        max == r -> ((g - b) / delta) % 6f
        max == g -> ((b - r) / delta) + 2f
        else -> ((r - g) / delta) + 4f
    } * 60f

    val l = (max + min) / 2f
    val s = if (delta == 0f) 0f else delta / (1f - abs(2f * l - 1f))

    return floatArrayOf(
        if (h < 0) h + 360f else h,
        s.coerceIn(0f, 1f),
        l.coerceIn(0f, 1f)
    )
}


enum class AppTheme {
    AMOLED,
    SUNSET_DARK,
    NEON_DARK,
    AQUA_DARK,
    LUXURY_DARK,
    SUNSET_LIGHT,
    NEON_LIGHT,
    AQUA_LIGHT,
    LUXURY_LIGHT
}

fun AppTheme.toColorScheme(): ColorScheme = when (this) {
    AppTheme.SUNSET_LIGHT -> sunsetLight
    AppTheme.SUNSET_DARK -> sunsetDark

    AppTheme.NEON_LIGHT -> neonLight
    AppTheme.NEON_DARK -> neonDark

    AppTheme.AQUA_LIGHT -> aquaLight
    AppTheme.AQUA_DARK -> aquaDark

    AppTheme.LUXURY_LIGHT -> luxuryLight
    AppTheme.LUXURY_DARK -> luxuryDark

    AppTheme.AMOLED -> amoledDarkTheme
}


data class ThemeSpec(
    val name: String,
    val colorScheme: ColorScheme,
    val description: String? = null
)
