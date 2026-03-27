package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.MemoryGroup
import com.dimento.app.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveGroupsUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(): Flow<List<MemoryGroup>> = repository.observeGroups()
}
