package com.dimento.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.absoluteValue

/**
 * Reusable theme utility functions for consistent dark/light theme handling
 * across the presentation layer. Centralizes all theme-aware color decisions.
 */

/**
 * Get the appropriate container background color for different event types
 * Respects system dark/light theme automatically
 */
@Composable
fun getEventContainerColor(eventType: com.dimento.app.domain.model.EventType): Color {
    return when (eventType) {
        com.dimento.app.domain.model.EventType.PAST -> 
            MaterialTheme.colorScheme.surfaceContainerLow
        com.dimento.app.domain.model.EventType.TODAY -> 
            MaterialTheme.colorScheme.primary
        com.dimento.app.domain.model.EventType.FUTURE -> 
            MaterialTheme.colorScheme.tertiaryContainer
    }
}

/**
 * Get the appropriate text color for event bubbles
 * Ensures sufficient contrast in both light and dark themes
 */
@Composable
fun getEventTextColor(eventType: com.dimento.app.domain.model.EventType): Color {
    return when (eventType) {
        com.dimento.app.domain.model.EventType.TODAY -> 
            MaterialTheme.colorScheme.onPrimary
        com.dimento.app.domain.model.EventType.FUTURE -> 
            MaterialTheme.colorScheme.onTertiaryContainer
        else -> 
            MaterialTheme.colorScheme.onSurface
    }
}

/**
 * Get muted text color for secondary information (like timestamps)
 * Provides reduced opacity for visual hierarchy
 */
@Composable
fun getMutedTextColor(): Color {
    return MaterialTheme.colorScheme.onSurfaceVariant
}

/**
 * Get background color that respects dark/light theme
 * Used for full-screen backgrounds and containers
 */
@Composable
fun getBackgroundColor(): Color {
    return MaterialTheme.colorScheme.background
}

/**
 * Get surface color that respects dark/light theme
 * Used for cards, dialogs, and elevated surfaces
 */
@Composable
fun getSurfaceColor(): Color {
    return MaterialTheme.colorScheme.surface
}

/**
 * Get background color for group icons based on the group name.
 * Falls back to surfaceVariant for empty names.
 */
@Composable
fun getGroupIconBackgroundColor(name: String): Color {
    return if (name.isBlank()) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        val hash = name.hashCode()
        val hue = (hash.absoluteValue % 360).toFloat()
        // Using high value and low saturation for consistent pastel look
        Color.hsv(hue, 0.25f, 0.95f)
    }
}

/**
 * Check if dark theme is active
 * Useful for theme-specific logic beyond color scheme
 */
@Composable
fun isDarkThemeActive(): Boolean {
    return isSystemInDarkTheme()
}

/**
 * Semantic color palette for future events
 * Ensures future events are visually distinct but not jarring
 */
@Composable
fun getFutureEventIndicatorColor(): Color {
    return MaterialTheme.colorScheme.tertiary
}

/**
 * Subtle surface color for small UI 'islands' like search bars and input areas.
 * This ensures consistency across the app for secondary containers.
 */
@Composable
fun getSubtleSurfaceColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant
}

/**
 * Return a high-contrast content color (dark or light) for a given background color.
 * Uses luminance to choose a readable foreground color. This ensures initials
 * and clipart icons remain readable when the background is a light pastel.
 */
fun getContrastColor(background: Color): Color {
    // luminance() is 0..1 where higher is lighter
    return if (background.luminance() > 0.5f) {
        Color(0xFF111111) // Dark text on light background
    } else {
        Color.White // Light text on dark background
    }
}
