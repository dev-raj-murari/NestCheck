package com.nestcheck.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun NestCheckTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
