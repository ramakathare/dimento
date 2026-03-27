package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository

class EnsureDefaultGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke() = repository.ensureDefaultGroup()
}
