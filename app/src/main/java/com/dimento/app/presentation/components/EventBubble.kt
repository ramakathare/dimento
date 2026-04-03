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
import androidx.compose.ui.res.stringResource
import com.dimento.app.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.presentation.theme.getEventContainerColor
import com.dimento.app.presentation.theme.getEventTextColor
import com.dimento.app.core.DateFormats

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
    val container = Modifier.background(getEventContainerColor(type))
    val textColor = getEventTextColor(type)
    Column(
        modifier = bubbleModifier.then(container).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = event.text, color = textColor, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = DateFormats.eventDateTimeMillis(event.eventDateMillis),
            color = textColor.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall
        )
        Row {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(id = R.string.mark_complete),
                tint = textColor,
                modifier = Modifier.clickable(onClick = onMarkComplete)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.delete_event),
                tint = textColor,
                modifier = Modifier.clickable(onClick = onDelete)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Forward,
                contentDescription = stringResource(id = R.string.forward_event),
                tint = textColor,
                modifier = Modifier.clickable(onClick = onForward)
            )
        }
    }
}
