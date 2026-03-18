package dev.muthukumar.anjana_crm.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFF0A50B4),
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF1E3A5F),
    secondary        = Color(0xFFE66414),
    onSecondary      = Color.White,
    background       = Color(0xFF0F172A),
    onBackground     = Color.White,
    surface          = Color(0xFF1E293B),
    onSurface        = Color.White,
    surfaceVariant   = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline          = Color(0xFF334155),
    error            = Color(0xFFEF4444),
    onError          = Color.White,
)

@Composable
fun AnjanaCrmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography(),
        content     = content
    )
}
