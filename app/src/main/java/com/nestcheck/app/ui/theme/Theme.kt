package com.nestcheck.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    secondary = CyberBorderBright,
    onSecondary = White,
    background = CyberBlack,
    onBackground = CyberTextBright,
    surface = CyberCard,
    onSurface = CyberTextBright,
    error = AlertRed,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,
    secondary = Color(0xFF4D5566),
    onSecondary = White,
    background = Color(0xFFF6F7F9),
    onBackground = Color(0xFF0A0C10),
    surface = White,
    onSurface = Color(0xFF0A0C10),
    error = Color(0xFFD32F2F),
    onError = White
)

/**
 * darkTheme defaults to TRUE on purpose: most screens still use the hardcoded Cyber* colors
 * (129 Color(0xFF..) literals), so flipping this to isSystemInDarkTheme() now would give
 * white-on-white text. Migrate screens to MaterialTheme.colorScheme.* first, then switch
 * the default (see roadmap).
 */
@Composable
fun NestCheckTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
