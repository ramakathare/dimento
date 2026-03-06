package com.dimento.domain.usecase

import com.dimento.domain.model.Event
import com.dimento.domain.model.EventType
import com.dimento.domain.repository.EventRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val validateEventTextUseCase: ValidateEventTextUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        eventDate: LocalDate,
        eventText: String,
        eventType: EventType,
    ): Result<Long> {
        validateEventTextUseCase(eventText).getOrElse { return Result.failure(it) }
        val existing = eventRepository.observeEvent(id).firstOrNull()
            ?: return Result.failure(IllegalArgumentException("Event not found."))

        val updated = Event(
            id = existing.id,
            recordedDate = existing.recordedDate,
            eventDate = eventDate,
            completedDate = existing.completedDate,
            eventText = eventText.trim(),
            eventType = eventType,
        )
        return Result.success(eventRepository.saveEvent(updated))
    }
}
