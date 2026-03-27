package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class RenameGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(groupId: Long, name: String) {
        require(name.isNotBlank()) { "Group name cannot be blank." }
        repository.renameGroup(groupId, name.trim())
    }
}
