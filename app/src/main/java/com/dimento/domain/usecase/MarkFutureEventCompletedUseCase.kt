package com.dimento.domain.usecase

import com.dimento.domain.repository.EventRepository
import java.time.LocalDate
import javax.inject.Inject

class MarkFutureEventCompletedUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(id: Long) {
        eventRepository.markFutureEventCompleted(id = id, completionDate = LocalDate.now())
    }
}
