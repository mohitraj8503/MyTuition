package com.example.mytuition.core.domain.repository

import com.example.mytuition.core.domain.model.Subject
import com.example.mytuition.core.domain.model.SubjectDetail

interface SubjectRepository {
    suspend fun getSubjects(): Result<List<Subject>>
    suspend fun getSubjectDetail(id: String): Result<SubjectDetail>
}
