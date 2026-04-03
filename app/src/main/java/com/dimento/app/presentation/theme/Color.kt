package com.dimento.app.presentation.theme

import androidx.compose.ui.graphics.Color

// ========== LIGHT THEME ==========
val Surface = Color(0xFFCCCCCC) // 80% white as requested
val OnSurface = Color(0xFF2D3433)
val OnSurfaceVariant = Color(0xFF596060)
val SurfaceContainerLow = Color(0xFFF1F4F3)
val SurfaceContainerHigh = Color(0xFFE4E9E8)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val Primary = Color(0xFF1C6D25)
val PrimaryDim = Color(0xFF096119)
val PrimaryContainer = Color(0xFF9DF197)
val OnPrimary = Color(0xFFEAFFE2)
val TertiaryContainer = Color(0xFFFCFEB9)
val Tertiary = Color(0xFF60622D)
val SecondaryContainer = Color(0xFFD6E8CE)

// ========== DARK THEME ==========
// Background & Surface
val DarkBackground = Color(0xFF333333)  // 80% black as requested
val DarkSurface = Color(0xFF333333)     // Consistent solid background
val DarkSurfaceVariant = Color(0xFF2A3530)  // Container variant
val DarkSurfaceContainerLow = Color(0xFF262E2A)  // Subtle container for PAST events
val DarkSurfaceContainerHigh = Color(0xFF3F4945)

// Text & Content
val DarkOnSurface = Color(0xFFE2E8E6)   // Light text on dark
val DarkOnSurfaceVariant = Color(0xFFB8C0BD)  // Muted text

// Accent Colors (adapted for dark theme)
val DarkPrimary = Color(0xFF7BE58A)     // Brighter green for dark
val DarkPrimaryDim = Color(0xFF5FB073)  // Darker variant
val DarkPrimaryContainer = Color(0xFF2D8035)  // Background for primary container
val DarkOnPrimary = Color(0xFF12381A)   // Dark text on green
val DarkTertiaryContainer = Color(0xFF4A4B2B)  // Subtle yellow for FUTURE
val DarkTertiary = Color(0xFFE8E8B8)    // Light tertiary
val DarkSecondaryContainer = Color(0xFF3B5045)
