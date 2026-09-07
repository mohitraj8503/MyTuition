package com.example.mytuition.core.domain.usecase

import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.repository.HomeworkRepository

class GetHomeworkDetailUseCase(private val repository: HomeworkRepository) {
    suspend operator fun invoke(id: String): Result<Homework> {
        return repository.getHomeworkDetail(id)
    }
}
