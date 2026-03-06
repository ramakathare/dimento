package com.dimento.domain.usecase

import com.dimento.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(id: Long) {
        eventRepository.deleteEvent(id)
    }
}
