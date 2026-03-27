package com.dimento.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.presentation.theme.OnPrimary
import com.dimento.app.presentation.theme.Primary
import com.dimento.app.presentation.theme.PrimaryDim
import com.dimento.app.presentation.theme.SurfaceContainerLow
import com.dimento.app.presentation.theme.TertiaryContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventBubble(
    event: MemoryEvent,
    type: EventType,
    onMarkComplete: () -> Unit,
    onDelete: () -> Unit,
    onForward: () -> Unit
) {
    val bubbleModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
    val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val container = when (type) {
        EventType.PAST -> Modifier.background(SurfaceContainerLow)
        EventType.TODAY -> Modifier.background(Brush.linearGradient(listOf(Primary, PrimaryDim)))
        EventType.FUTURE -> Modifier.background(TertiaryContainer)
    }
    val textColor = when (type) {
        EventType.TODAY -> OnPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    Column(
        modifier = bubbleModifier.then(container).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = event.text, color = textColor, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = formatter.format(Date(event.eventDateMillis)),
            color = textColor.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall
        )
        Row {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Mark complete",
                tint = textColor,
                modifier = Modifier.clickable(onClick = onMarkComplete)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete event",
                tint = textColor,
                modifier = Modifier.clickable(onClick = onDelete)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Forward,
                contentDescription = "Forward event",
                tint = textColor,
                modifier = Modifier.clickable(onClick = onForward)
            )
        }
    }
}
