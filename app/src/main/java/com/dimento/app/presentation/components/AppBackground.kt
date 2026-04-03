package com.dimento.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AppBackground(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) {
        // Dark theme: 80% black (dark gray)
        Color(0xFF333333)
    } else {
        // Light theme: 80% white (light gray)
        Color(0xFFCCCCCC)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    )
}
