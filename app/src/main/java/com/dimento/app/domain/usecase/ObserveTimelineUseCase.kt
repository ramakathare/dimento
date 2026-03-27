package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveTimelineUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(groupId: Long): Flow<List<MemoryEvent>> = repository.observeEventsByGroup(groupId)
}
