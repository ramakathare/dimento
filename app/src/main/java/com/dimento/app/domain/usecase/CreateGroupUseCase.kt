package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class CreateGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(name: String, icon: String?, description: String?): Long {
        require(name.isNotBlank()) { "Group name cannot be blank." }
        require(name.length <= 100) { "Group name must be 100 characters or less." }
        require(description?.length ?: 0 <= 200) { "Description must be 200 characters or less." }
        return repository.createGroup(name.trim(), icon, description?.trim())
    }
}
