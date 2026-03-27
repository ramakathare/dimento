package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class UpdateEventUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(
        eventId: Long,
        text: String,
        eventDateMillis: Long,
        voicePath: String?
    ) {
        require(text.isNotBlank()) { "Event text cannot be blank." }
        require(text.length <= 200) { "Event text must be at most 200 characters." }
        repository.updateEvent(eventId, text.trim(), eventDateMillis, voicePath)
    }
}
