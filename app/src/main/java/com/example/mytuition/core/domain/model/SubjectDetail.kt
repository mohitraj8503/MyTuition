package com.example.mytuition.core.domain.model

data class SubjectDetail(
    val subject: Subject,
    val recentHomework: List<Homework>,
    val recentResources: List<Resource>
)
