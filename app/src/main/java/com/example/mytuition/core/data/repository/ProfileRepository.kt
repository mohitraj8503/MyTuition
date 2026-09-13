package com.example.mytuition.core.data.repository

import androidx.compose.runtime.Immutable

@Immutable
data class AttendanceRecordItem(
    val id: String,
    val date: String,
    val status: String,
    val sessionName: String = "Class Session"
)

@Immutable
data class UserProfileData(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val studentId: String,
    val classGrade: String,
    val section: String,
    val rollNumber: String,
    val schoolName: String,
    val instituteName: String,
    val instituteAddress: String,
    val institutePhone: String,
    val instituteEmail: String,
    val attendancePercent: Int,
    val homeworkDoneText: String,
    val gpaGrade: String,
    val notificationsEnabled: Boolean,
    val isDemo: Boolean,
    val avatarUrl: String? = null,
    val feeStatus: String = "PAID",
    val attendanceHistory: List<AttendanceRecordItem> = emptyList()
)

interface ProfileRepository {
    suspend fun getProfileData(): Result<UserProfileData>
    suspend fun updatePersonalInfo(name: String, email: String, phone: String, schoolName: String): Result<Unit>
    suspend fun updateNotificationPreference(enabled: Boolean): Result<Unit>
}
