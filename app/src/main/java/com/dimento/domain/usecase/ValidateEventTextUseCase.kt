package com.dimento.domain.usecase

class ValidateEventTextUseCase {
    operator fun invoke(text: String): Result<Unit> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Event text cannot be empty."))
        }
        if (trimmed.length > 200) {
            return Result.failure(IllegalArgumentException("Event text must be 200 characters or less."))
        }
        return Result.success(Unit)
    }
}
