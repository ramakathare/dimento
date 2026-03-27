package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class DeleteGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(groupId: Long) = repository.deleteGroup(groupId)
}
