package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.local.TokenManager
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.domain.model.TeacherInfo
import com.example.mytuition.core.domain.model.UserRole
import com.example.mytuition.core.domain.model.UserSession
import com.example.mytuition.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first

class PocketBaseAuthRepository(
    private val api: PocketBaseApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signInWithGoogle(): Result<UserSession> {
        return enterDemoMode()
    }

    override suspend fun signInWithGitHub(): Result<UserSession> {
        return enterDemoMode()
    }

    override suspend fun enterDemoMode(): Result<UserSession> {
        val loginResult = signInWithUsername("demo_student@mytuition", "demo@123")
        if (loginResult.isSuccess) {
            return loginResult
        }
        return try {
            val session = UserSession(
                uid = "demo_user",
                name = "Mohit Raj",
                email = "demo@chanakya.in",
                phone = "+919876543210",
                role = UserRole.STUDENT,
                studentId = "stu_789",
                batchIds = listOf("batch_10a_maths", "batch_10a_science", "batch_10a_english"),
                classGrade = "Class 10",
                section = "A",
                rollNumber = "27",
                schoolName = "St. Xavier's School",
                photoUrl = null,
                isDemo = true
            )
            tokenManager.saveSession(
                token = "demo_token_student",
                userId = session.uid,
                role = "STUDENT",
                name = session.name,
                username = "demo_student@mytuition"
            )
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun enterTeacherDemoMode(): Result<UserSession> {
        val loginResult = signInWithUsername("demo_teacher@mytuition", "teacher@123")
        if (loginResult.isSuccess) {
            return loginResult
        }
        return try {
            val session = UserSession(
                uid = "demo_teacher_uid",
                name = "Mr. Rakesh Sharma",
                email = "rakesh@chanakya.in",
                phone = "+919812345678",
                role = UserRole.TEACHER,
                teacherId = "teacher_001",
                teacherInfo = TeacherInfo(
                    teacherId = "teacher_001",
                    qualification = "M.Sc. Mathematics, B.Ed.",
                    experienceYears = 12,
                    subjects = listOf("subj_maths", "subj_physics"),
                    batchIds = listOf("batch_10a_maths", "batch_10b_science", "batch_9c_maths")
                ),
                batchIds = listOf("batch_10a_maths", "batch_10b_science", "batch_9c_maths"),
                photoUrl = null,
                isDemo = true
            )
            tokenManager.saveSession(
                token = "demo_token_teacher",
                userId = session.uid,
                role = "TEACHER",
                name = session.name,
                username = "demo_teacher@mytuition"
            )
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithUsername(username: String, password: String): Result<UserSession> {
        val cleanUser = username.trim().lowercase()
        val cleanPass = password.trim()

        // 1. Try PocketBase REST network login first
        try {
            val body = mapOf("identity" to cleanUser, "password" to cleanPass)
            val response = api.loginWithPassword(body)
            if (response.isSuccessful && response.body() != null) {
                val authData = response.body()!!
                val record = authData.record
                val role = when (record.role?.uppercase()) {
                    "TEACHER" -> UserRole.TEACHER
                    "ADMIN" -> UserRole.ADMIN
                    "PARENT" -> UserRole.PARENT
                    else -> UserRole.STUDENT
                }

                val session = UserSession(
                    uid = record.id,
                    name = record.name ?: "Student",
                    email = record.email ?: "",
                    phone = record.phone ?: "",
                    role = role,
                    studentId = record.id,
                    batchIds = record.batches ?: emptyList(),
                    classGrade = record.classGrade ?: "Class 10",
                    section = record.section ?: "A",
                    rollNumber = record.rollNumber ?: "",
                    schoolName = record.schoolName ?: "",
                    photoUrl = null,
                    isDemo = false
                )

                tokenManager.saveSession(
                    token = authData.token,
                    userId = record.id,
                    role = record.role ?: "STUDENT",
                    name = session.name,
                    username = cleanUser,
                    instituteId = record.institute ?: "inst_456"
                )

                com.example.mytuition.core.notifications.OneSignalHelper.setExternalId(record.id)

                return Result.success(session)
            } else {
                return Result.failure(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            return Result.failure(Exception(e.message ?: "Authentication failed. Please check network connection."))
        }
    }

    override suspend fun getCurrentSession(): UserSession? {
        val token = tokenManager.getToken().first() ?: return null
        val userId = tokenManager.getUserId().first() ?: return null
        val roleStr = tokenManager.getUserRole().first() ?: "STUDENT"
        val name = tokenManager.getUserName().first() ?: "Mohit Raj"

        val role = when (roleStr.uppercase()) {
            "TEACHER" -> UserRole.TEACHER
            "ADMIN" -> UserRole.ADMIN
            "PARENT" -> UserRole.PARENT
            else -> UserRole.STUDENT
        }

        return UserSession(
            uid = userId,
            name = name,
            email = "user@mytuition.app",
            phone = "+919876543210",
            role = role,
            studentId = userId,
            teacherId = if (role == UserRole.TEACHER) "teacher_001" else "",
            teacherInfo = if (role == UserRole.TEACHER) TeacherInfo("teacher_001", listOf("Mathematics"), listOf("batch_10a_maths"), "M.Sc. Mathematics", 12) else null,
            batchIds = listOf("batch_10a_maths"),
            classGrade = "Class 10",
            section = "A",
            rollNumber = "27",
            schoolName = "St. Xavier's School",
            photoUrl = null,
            isDemo = token.startsWith("demo_") || token.startsWith("offline_")
        )
    }

    override suspend fun logout() {
        tokenManager.clearSession()
        com.example.mytuition.core.notifications.OneSignalHelper.logout()
    }
}
