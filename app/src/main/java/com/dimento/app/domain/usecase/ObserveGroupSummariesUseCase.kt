package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.GroupSummary
import com.dimento.app.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveGroupSummariesUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(): Flow<List<GroupSummary>> = repository.observeGroupSummaries()
}
