package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class DeleteEventUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(eventId: Long) = repository.deleteEvent(eventId)
}
