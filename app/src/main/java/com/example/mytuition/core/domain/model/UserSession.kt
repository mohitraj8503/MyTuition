package com.example.mytuition.core.domain.model

data class UserSession(
    val uid: String = "",
    val userId: String = "",
    val instituteId: String = "inst_456",
    val studentId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.STUDENT,
    val batchIds: List<String> = emptyList(),
    val classGrade: String = "",
    val section: String = "",
    val rollNumber: String = "",
    val schoolName: String = "",
    val photoUrl: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresAt: Long? = null,
    val isDemo: Boolean = false,
    val teacherId: String = "",
    val teacherInfo: TeacherInfo? = null
) {
    val isStaff: Boolean get() = role == UserRole.TEACHER || role == UserRole.ADMIN
}

data class TeacherInfo(
    val teacherId: String = "",
    val subjects: List<String> = emptyList(),
    val batchIds: List<String> = emptyList(),
    val qualification: String = "",
    val experienceYears: Int = 0
)
