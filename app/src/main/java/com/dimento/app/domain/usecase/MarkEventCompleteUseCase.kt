package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class MarkEventCompleteUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(eventId: Long, completedDateMillis: Long) {
        repository.markEventCompleted(eventId, completedDateMillis)
    }
}
