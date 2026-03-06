package com.dimento.domain.usecase

import com.dimento.domain.model.Event
import com.dimento.domain.model.EventType
import com.dimento.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    operator fun invoke(typeFilter: EventType?): Flow<List<Event>> =
        eventRepository.observeEvents().map { events ->
            val filtered = typeFilter?.let { type -> events.filter { it.eventType == type } } ?: events
            filtered.sortedByDescending { it.eventDate }
        }
}
