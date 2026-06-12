package com.dimento.app.domain.usecase

import com.dimento.app.core.ValidationConstants
import com.dimento.app.domain.repository.MemoryRepository

class UpdateEventUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(
        eventId: Long,
        text: String,
        eventDateMillis: Long,
        voicePath: String?,
        linkPreviewJson: String? = null
    ) {
        require(text.isNotBlank()) { ValidationConstants.EVENT_TEXT_BLANK_MESSAGE }
        require(text.length <= ValidationConstants.MAX_EVENT_TEXT_LENGTH) { ValidationConstants.EVENT_TEXT_MAX_MESSAGE }
        repository.updateEvent(eventId, text.trim(), eventDateMillis, voicePath, linkPreviewJson)
    }
}
