package com.example.mytuition.core.domain.usecase

import com.example.mytuition.core.domain.model.SubjectDetail
import com.example.mytuition.core.domain.repository.SubjectRepository

class GetSubjectDetailUseCase(private val repository: SubjectRepository) {
    suspend operator fun invoke(id: String): Result<SubjectDetail> {
        return repository.getSubjectDetail(id)
    }
}
