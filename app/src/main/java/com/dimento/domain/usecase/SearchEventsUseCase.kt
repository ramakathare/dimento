package com.dimento.domain.usecase

import com.dimento.domain.model.Event
import com.dimento.domain.model.EventType
import com.dimento.domain.repository.EventRepository
import javax.inject.Inject

class SearchEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(
        query: String,
        typeFilter: EventType?,
    ): List<Event> {
        val allByType = typeFilter?.let { eventRepository.getEventsByType(it) } ?: eventRepository.getAllEventsOnce()
        if (query.isBlank()) {
            return allByType.sortedByDescending { it.eventDate }
        }
        val ids = eventRepository.searchEventIds(query)
        return allByType.filter { it.id in ids }.sortedByDescending { it.eventDate }
    }
}
