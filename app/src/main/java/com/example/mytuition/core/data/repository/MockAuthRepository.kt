package com.example.mytuition.core.data.repository

import com.example.mytuition.core.domain.model.UserRole
import com.example.mytuition.core.domain.model.UserSession
import com.example.mytuition.core.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class MockAuthRepository : AuthRepository {
    
    private var currentSession: UserSession? = null

    override suspend fun signInWithGoogle(): Result<UserSession> {
        delay(1000) // Simulate network delay
        val session = UserSession(
            userId = "user_google",
            instituteId = "inst_456",
            studentId = "stu_789",
            role = UserRole.STUDENT,
            accessToken = "mock_access_token_google",
            refreshToken = "mock_refresh_token_google",
            expiresAt = System.currentTimeMillis() + 86400000
        )
        currentSession = session
        return Result.success(session)
    }

    override suspend fun signInWithGitHub(): Result<UserSession> {
        delay(1000) // Simulate network delay
        val session = UserSession(
            userId = "user_github",
            instituteId = "inst_456",
            studentId = "stu_789",
            role = UserRole.STUDENT,
            accessToken = "mock_access_token_github",
            refreshToken = "mock_refresh_token_github",
            expiresAt = System.currentTimeMillis() + 86400000
        )
        currentSession = session
        return Result.success(session)
    }

    override suspend fun enterDemoMode(): Result<UserSession> {
        delay(600) // Short transition
        val session = UserSession(
            userId = "demo_user_1",
            instituteId = "demo_inst_1",
            studentId = "demo_student_1",
            role = UserRole.STUDENT,
            accessToken = "demo_access_token",
            refreshToken = "demo_refresh_token",
            expiresAt = System.currentTimeMillis() + 86400000
        )
        currentSession = session
        return Result.success(session)
    }

    override suspend fun getCurrentSession(): UserSession? {
        delay(500)
        return currentSession
    }

    override suspend fun logout() {
        delay(500)
        currentSession = null
    }
}
