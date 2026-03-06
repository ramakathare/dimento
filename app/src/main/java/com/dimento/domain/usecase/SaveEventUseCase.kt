package com.dimento.domain.usecase

import com.dimento.domain.model.Event
import com.dimento.domain.model.EventDraft
import com.dimento.domain.repository.EventRepository
import java.time.LocalDate
import javax.inject.Inject

class SaveEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val validateEventTextUseCase: ValidateEventTextUseCase,
) {
    suspend operator fun invoke(draft: EventDraft): Result<Long> {
        validateEventTextUseCase(draft.eventText).getOrElse { return Result.failure(it) }
        val event = Event(
            id = draft.id ?: 0L,
            recordedDate = LocalDate.now(),
            eventDate = draft.eventDate,
            completedDate = null,
            eventText = draft.eventText.trim(),
            eventType = draft.eventType,
        )
        val savedId = eventRepository.saveEvent(event)
        return Result.success(savedId)
    }
}
