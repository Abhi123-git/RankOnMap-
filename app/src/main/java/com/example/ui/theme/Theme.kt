package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmerald,
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFD1FAE5),
    
    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFDBEAFE),
    
    tertiary = AccentCyan,
    onTertiary = Color(0xFF083344),
    
    background = BackgroundObsidian,
    onBackground = Color(0xFFF3F4F6),
    
    surface = SurfaceSlate,
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = SurfaceSlateVariant,
    onSurfaceVariant = Color(0xFFD1D5DB),
    
    error = GlowRed,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = PrimaryEmerald,
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = BackgroundObsidian,
    onBackground = Color(0xFFF3F4F6),
    surface = SurfaceSlate,
    onSurface = Color(0xFFF3F4F6)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark theme for cohesive professional dashboard layout
    dynamicColor: Boolean = false, // Disable system dynamic tint to preserve custom aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
