package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class CreateEventUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(
        groupId: Long,
        text: String,
        eventDateMillis: Long,
        recordedDateMillis: Long,
        voicePath: String? = null
    ): Long {
        require(text.isNotBlank()) { "Event text cannot be blank." }
        require(text.length <= 200) { "Event text must be at most 200 characters." }
        return repository.createEvent(groupId, text.trim(), eventDateMillis, recordedDateMillis, voicePath)
    }
}
