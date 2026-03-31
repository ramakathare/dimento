package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class RenameGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(groupId: Long, name: String, icon: String?, description: String?) {
        require(name.isNotBlank()) { "Group name cannot be blank." }
        require(description?.length ?: 0 <= 200) { "Description must be 200 characters or less." }
        repository.renameGroup(groupId, name.trim(), icon, description?.trim())
    }
}
