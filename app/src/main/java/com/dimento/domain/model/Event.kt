package com.dimento.domain.model

import java.time.LocalDate

data class Event(
    val id: Long = 0L,
    val recordedDate: LocalDate,
    val eventDate: LocalDate,
    val completedDate: LocalDate? = null,
    val eventText: String,
    val eventType: EventType,
)
