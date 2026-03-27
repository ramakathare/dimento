package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.MemoryGroup
import com.dimento.app.domain.repository.MemoryRepository

class GetGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(groupId: Long): MemoryGroup? = repository.getGroup(groupId)
}
