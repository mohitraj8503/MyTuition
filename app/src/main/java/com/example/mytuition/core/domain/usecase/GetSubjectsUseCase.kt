package com.example.mytuition.core.domain.usecase

import com.example.mytuition.core.domain.model.Subject
import com.example.mytuition.core.domain.repository.SubjectRepository

class GetSubjectsUseCase(private val repository: SubjectRepository) {
    suspend operator fun invoke(): Result<List<Subject>> {
        return repository.getSubjects()
    }
}
