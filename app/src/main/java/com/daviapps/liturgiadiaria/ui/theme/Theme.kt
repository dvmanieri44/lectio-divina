package com.daviapps.liturgiadiaria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = Gold40,
    onPrimary        = White,
    primaryContainer = Gold90,
    onPrimaryContainer = Gold10,
    secondary        = Gold60,
    onSecondary      = White,
    secondaryContainer = Gold95,
    onSecondaryContainer = Gold20,
    tertiary         = Gold50,
    onTertiary       = White,
    tertiaryContainer = Gold90,
    onTertiaryContainer = Gold10,
    background       = OffWhite,
    onBackground     = Gold10,
    surface          = White,
    onSurface        = Gold10,
    surfaceVariant   = Gold95,
    onSurfaceVariant = Gold20,
    outline          = Gold80,
    error            = Error40,
    onError          = White,
    errorContainer   = Error90,
    onErrorContainer = Error40,
)

@Composable
fun LiturgiaDiariaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
