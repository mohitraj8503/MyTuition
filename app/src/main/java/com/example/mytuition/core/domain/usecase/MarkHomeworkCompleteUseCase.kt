package com.example.mytuition.core.domain.usecase

import com.example.mytuition.core.domain.repository.HomeworkRepository

class MarkHomeworkCompleteUseCase(private val repository: HomeworkRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.markHomeworkComplete(id)
    }
}
