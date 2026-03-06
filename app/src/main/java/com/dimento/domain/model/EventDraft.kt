package com.dimento.domain.model

import java.time.LocalDate

data class EventDraft(
    val id: Long? = null,
    val eventDate: LocalDate,
    val eventText: String,
    val eventType: EventType,
)
