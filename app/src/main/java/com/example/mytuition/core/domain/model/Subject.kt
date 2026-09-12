package com.example.mytuition.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Subject(
    val id: String,
    val name: String,
    val teacherName: String,
    val teacherAvatarUrl: String?,
    val homeworkCount: Int,
    val resourceCount: Int,
    val nextClass: Long?
)
