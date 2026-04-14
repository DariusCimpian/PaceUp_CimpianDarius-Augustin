package com.example.paceup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PaceUpColorScheme = darkColorScheme(
    primary = PaceGreen,
    secondary = PaceOrange,
    tertiary = PacePurple,
    background = PaceDark,
    surface = PaceCardDark,
    onPrimary = PaceWhite,
    onSecondary = PaceWhite,
    onBackground = PaceWhite,
    onSurface = PaceWhite,
)

@Composable
fun PaceUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PaceUpColorScheme,
        typography = Typography,
        content = content
    )
}