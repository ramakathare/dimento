package com.dimento.app.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dimento.app.domain.model.EventType
import com.dimento.app.presentation.theme.Primary
import com.dimento.app.presentation.theme.Tertiary

@Composable
fun DateHeader(type: EventType) {
    val title = when (type) {
        EventType.PAST -> "PAST"
        EventType.TODAY -> "TODAY"
        EventType.FUTURE -> "FUTURE"
    }
    val color = when (type) {
        EventType.PAST -> MaterialTheme.colorScheme.onSurfaceVariant
        EventType.TODAY -> Primary
        EventType.FUTURE -> Tertiary
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
