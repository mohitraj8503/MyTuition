package com.example.mytuition.core.domain.usecase

import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.repository.HomeworkRepository

class GetHomeworkListUseCase(private val repository: HomeworkRepository) {
    suspend operator fun invoke(): Result<List<Homework>> {
        return repository.getHomeworkList()
    }
}
