package com.dimento.app.domain.usecase

import com.dimento.app.core.ValidationConstants
import com.dimento.app.domain.repository.MemoryRepository

class CreateGroupUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(name: String, icon: String?, description: String?): Long {
        require(name.isNotBlank()) { ValidationConstants.GROUP_NAME_BLANK_MESSAGE }
        require(name.length <= ValidationConstants.MAX_GROUP_NAME_LENGTH) { ValidationConstants.GROUP_NAME_MAX_MESSAGE }
        require(description?.length ?: 0 <= ValidationConstants.MAX_GROUP_DESCRIPTION_LENGTH) { ValidationConstants.GROUP_DESCRIPTION_MAX_MESSAGE }
        return repository.createGroup(name.trim(), icon, description?.trim())
    }
}
