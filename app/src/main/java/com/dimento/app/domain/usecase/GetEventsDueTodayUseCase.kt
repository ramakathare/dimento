package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.domain.repository.MemoryRepository
import com.dimento.app.domain.util.EventTypeResolver

class GetEventsDueTodayUseCase(
    private val repository: MemoryRepository,
    private val eventTypeResolver: EventTypeResolver
) {
    suspend operator fun invoke(nowMillis: Long): List<MemoryEvent> {
        val (start, end) = eventTypeResolver.todayStartEnd(nowMillis)
        return repository.getEventsDueToday(start, end)
    }
}
