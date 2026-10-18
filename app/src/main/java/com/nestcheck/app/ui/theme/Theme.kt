package com.nestcheck.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NestCheckLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = White,
    primaryContainer = PrimaryBlueContainer,
    onPrimaryContainer = PrimaryBlueDark,
    secondary = AccentBlue,
    onSecondary = White,
    background = LightBg,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextMuted,
    outline = BorderLight,
    outlineVariant = Color(0xFFCBD5E1),
    error = AlertRed,
    onError = White,
    errorContainer = AlertRedContainer,
    onErrorContainer = AlertRed
)

@Composable
fun NestCheckTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NestCheckLightColorScheme,
        typography = Typography,
        content = content
    )
}
