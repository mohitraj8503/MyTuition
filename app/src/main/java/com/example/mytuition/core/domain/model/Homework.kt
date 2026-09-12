package com.example.mytuition.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Homework(
    val id: String,
    val subjectName: String,
    val title: String,
    val description: String,
    val assignedAt: Long,
    val dueAt: Long?,
    val status: HomeworkStatus,
    val teacherName: String,
    val attachments: List<Resource>
)
