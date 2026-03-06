package com.dimento.domain.usecase

import com.dimento.domain.model.Event
import com.dimento.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    operator fun invoke(id: Long): Flow<Event?> = eventRepository.observeEvent(id)
}
