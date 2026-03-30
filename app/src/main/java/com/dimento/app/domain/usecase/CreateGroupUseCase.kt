package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class CreateGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(name: String, icon: String?): Long {
        require(name.isNotBlank()) { "Group name cannot be blank." }
        return repository.createGroup(name.trim(), icon)
    }
}
