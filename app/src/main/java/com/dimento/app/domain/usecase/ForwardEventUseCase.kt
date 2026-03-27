package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class ForwardEventUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(eventId: Long, destinationGroupId: Long, recordedDateMillis: Long): Long {
        return repository.forwardEvent(eventId, destinationGroupId, recordedDateMillis)
    }
}
