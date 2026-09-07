package com.example.mytuition.core.domain.model

data class UserSession(
    val userId: String,
    val instituteId: String,
    val studentId: String,
    val role: UserRole,
    val accessToken: String?,
    val refreshToken: String?,
    val expiresAt: Long?
)
