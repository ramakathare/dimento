package com.dimento.app.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.presentation.theme.getEventContainerColor
import com.dimento.app.presentation.theme.getEventTextColor
import com.dimento.app.core.DateFormats

private val DATE_COLUMN_WIDTH = 64.dp
private val BUBBLE_SHAPE = RoundedCornerShape(10.dp)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventBubble(
    event: MemoryEvent,
    type: EventType,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = onClick
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        getEventContainerColor(type)
    }
    val textColor = getEventTextColor(type)

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(BUBBLE_SHAPE)
                .background(backgroundColor)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 8.dp)
                    .width(DATE_COLUMN_WIDTH)
            ) {
                Text(
                    text = DateFormats.eventDateOnlyMillis(event.eventDateMillis),
                    color = if (type == com.dimento.app.domain.model.EventType.TODAY)
                        textColor.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
                Text(
                    text = DateFormats.eventTimeOnlyMillis(event.eventDateMillis),
                    color = textColor.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}
