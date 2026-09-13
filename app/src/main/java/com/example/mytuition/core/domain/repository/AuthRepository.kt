package com.example.mytuition.core.domain.repository

import com.example.mytuition.core.domain.model.UserSession

interface AuthRepository {
    suspend fun signInWithGoogle(): Result<UserSession>
    suspend fun signInWithGitHub(): Result<UserSession>
    suspend fun enterDemoMode(): Result<UserSession>
    suspend fun enterTeacherDemoMode(): Result<UserSession>
    suspend fun signInWithUsername(username: String, password: String): Result<UserSession>
    suspend fun getCurrentSession(): UserSession?
    suspend fun logout()
}
