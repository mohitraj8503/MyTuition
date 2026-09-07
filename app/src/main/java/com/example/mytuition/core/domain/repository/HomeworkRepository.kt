package com.example.mytuition.core.domain.repository

import com.example.mytuition.core.domain.model.Homework

interface HomeworkRepository {
    suspend fun getHomeworkList(): Result<List<Homework>>
    suspend fun getHomeworkDetail(id: String): Result<Homework>
    suspend fun markHomeworkComplete(id: String): Result<Unit>
}
