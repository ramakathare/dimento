package com.dimento.app.domain.usecase

import com.dimento.app.core.ValidationConstants
import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.repository.MemoryRepository
import com.dimento.app.domain.util.EventTypeResolver

class CreateEventUseCase(
    private val repository: MemoryRepository,
    private val eventTypeResolver: EventTypeResolver = EventTypeResolver()
) {
    data class CreateResult(
        val eventId: Long,
        val eventType: EventType
    )

    /**
     * Creates an event and auto-completes it if the event date is not in the future.
     *
     * - No date selected (eventDateMillis ≈ now): mark as complete with recordedDateMillis
     * - Past date: mark as complete with eventDateMillis as completed date
     * - Future date: leave incomplete, notification will fire at the scheduled time
     */
    suspend operator fun invoke(
        groupId: Long,
        text: String,
        eventDateMillis: Long,
        recordedDateMillis: Long,
        voicePath: String? = null
    ): CreateResult {
        require(text.isNotBlank()) { ValidationConstants.EVENT_TEXT_BLANK_MESSAGE }
        require(text.length <= ValidationConstants.MAX_EVENT_TEXT_LENGTH) { ValidationConstants.EVENT_TEXT_MAX_MESSAGE }

        val type = eventTypeResolver.resolve(eventDateMillis, recordedDateMillis)

        val completedDateMillis = when (type) {
            EventType.FUTURE -> null
            EventType.TODAY -> recordedDateMillis
            EventType.PAST -> eventDateMillis
        }

        val eventId = repository.createEvent(groupId, text.trim(), eventDateMillis, recordedDateMillis, voicePath, completedDateMillis)
        return CreateResult(eventId = eventId, eventType = type)
    }
}
