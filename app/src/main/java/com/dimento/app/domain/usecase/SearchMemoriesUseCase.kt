package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.SearchResult
import com.dimento.app.domain.repository.MemoryRepository

class SearchMemoriesUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(query: String, groupId: Long? = null): SearchResult {
        if (query.isBlank()) return SearchResult(emptyList(), emptyList())
        return repository.search(query.trim(), groupId)
    }
}
