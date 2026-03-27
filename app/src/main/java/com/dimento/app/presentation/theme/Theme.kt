package com.dimento.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Light = lightColorScheme(
    background = Surface,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondaryContainer = SecondaryContainer,
    tertiaryContainer = TertiaryContainer,
    tertiary = Tertiary
)

private val Dark = darkColorScheme(
    background = OnSurface,
    surface = OnSurface,
    onSurface = Surface,
    onSurfaceVariant = SurfaceContainerHigh,
    primary = PrimaryContainer,
    onPrimary = OnSurface,
    primaryContainer = Primary,
    secondaryContainer = PrimaryDim,
    tertiaryContainer = Tertiary,
    tertiary = TertiaryContainer
)

@Composable
fun DiMentoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Light,
        typography = AppTypography,
        content = content
    )
}
