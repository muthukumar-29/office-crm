package dev.muthukumar.anjana_crm.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Anjana Infotech Brand Colors ─────────────────────────────
val BrandMagenta     = Color(0xFFC0297A)   // primary magenta
val BrandPurple      = Color(0xFF6A0572)   // deep purple
val BrandMagentaLight= Color(0xFFF5D0E8)   // light magenta tint
val BrandPurpleLight = Color(0xFFEDD6F0)   // light purple tint
val BrandAccent      = Color(0xFFE91E8C)   // vibrant accent

// Neutrals
val White            = Color(0xFFFFFFFF)
val OffWhite         = Color(0xFFF8F4F9)   // slightly purple-tinted white
val Surface          = Color(0xFFFFFFFF)
val SurfaceVariant   = Color(0xFFF3EBF5)
val Outline          = Color(0xFFD9C5E0)
val OnSurface        = Color(0xFF1A0020)
val OnSurfaceMuted   = Color(0xFF6B5373)
val OnSurfaceHint    = Color(0xFF9E83A8)

// Status
val SuccessGreen     = Color(0xFF1B8A4A)
val WarningAmber     = Color(0xFFB45C00)
val ErrorRed         = Color(0xFFBE1B1B)
val InfoBlue         = Color(0xFF1565C0)

private val LightColorScheme = lightColorScheme(
    primary          = BrandMagenta,
    onPrimary        = White,
    primaryContainer = BrandMagentaLight,
    onPrimaryContainer = BrandPurple,
    secondary        = BrandPurple,
    onSecondary      = White,
    secondaryContainer = BrandPurpleLight,
    onSecondaryContainer = BrandPurple,
    tertiary         = BrandAccent,
    onTertiary       = White,
    background       = OffWhite,
    onBackground     = OnSurface,
    surface          = White,
    onSurface        = OnSurface,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = OnSurfaceMuted,
    outline          = Outline,
    error            = ErrorRed,
    onError          = White,
)

@Composable
fun AnjanaCrmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography(),
        content     = content
    )
}