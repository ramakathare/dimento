package com.dimento.app.presentation.model

import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.model.MemoryEvent

sealed interface TimelineItem {
    data class Header(val type: EventType) : TimelineItem
    data class EventRow(val event: MemoryEvent, val type: EventType) : TimelineItem
}
