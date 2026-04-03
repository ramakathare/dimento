package com.dimento.app.domain.usecase

import com.dimento.app.core.ValidationConstants
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
        require(text.isNotBlank()) { ValidationConstants.EVENT_TEXT_BLANK_MESSAGE }
        require(text.length <= ValidationConstants.MAX_EVENT_TEXT_LENGTH) { ValidationConstants.EVENT_TEXT_MAX_MESSAGE }
        return repository.createEvent(groupId, text.trim(), eventDateMillis, recordedDateMillis, voicePath)
    }
}
