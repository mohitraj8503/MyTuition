package com.example.mytuition.core.data.repository

import androidx.compose.runtime.Immutable
import com.example.mytuition.core.data.FirebaseConfig
import com.example.mytuition.core.data.model.InstituteDoc
import com.example.mytuition.core.data.model.UserDoc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

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
    val isDemo: Boolean
)

interface ProfileRepository {
    suspend fun getProfileData(): Result<UserProfileData>
    suspend fun updatePersonalInfo(name: String, email: String, phone: String, schoolName: String): Result<Unit>
    suspend fun updateNotificationPreference(enabled: Boolean): Result<Unit>
}

class FirebaseProfileRepository(
    private val db: FirebaseFirestore = FirebaseConfig.db,
    private val auth: FirebaseAuth = FirebaseConfig.auth
) : ProfileRepository {

    // Fallback demo user profile
    private var demoProfile = UserProfileData(
        uid = "demo_user",
        name = "Mohit Raj",
        email = "mohitraj8503@gmail.com",
        phone = "+91 98765 43210",
        studentId = "STU-2024-0891",
        classGrade = "Grade 10",
        section = "A",
        rollNumber = "27",
        schoolName = "St. Xavier's High School",
        instituteName = "Chanakya Classes",
        instituteAddress = "12, MG Road, Metro Pillar 44, Bengaluru, KA 560001",
        institutePhone = "+91 80 4123 4567",
        instituteEmail = "support@chanakyaclasses.in",
        attendancePercent = 94,
        homeworkDoneText = "18/20",
        gpaGrade = "3.8 / 4.0",
        notificationsEnabled = true,
        isDemo = true
    )

    override suspend fun getProfileData(): Result<UserProfileData> {
        val user = auth.currentUser
        if (user == null) {
            return Result.success(demoProfile)
        }

        return try {
            val userDocSnap = db.collection("users").document(user.uid).get().await()
            val userDoc = userDocSnap.toObject(UserDoc::class.java)

            val instId = userDoc?.instituteId ?: "inst_456"
            val instSnap = db.collection("institutes").document(instId).get().await()
            val instDoc = instSnap.toObject(InstituteDoc::class.java)

            val profile = UserProfileData(
                uid = user.uid,
                name = userDoc?.name?.takeIf { it.isNotBlank() } ?: (user.displayName ?: "Mohit Raj"),
                email = userDoc?.email?.takeIf { it.isNotBlank() } ?: (user.email ?: "mohitraj8503@gmail.com"),
                phone = userDoc?.phone?.takeIf { it.isNotBlank() } ?: (user.phoneNumber ?: "+91 98765 43210"),
                studentId = userDoc?.studentInfo?.studentId?.takeIf { it.isNotBlank() } ?: "STU-2024-0891",
                classGrade = userDoc?.studentInfo?.classGrade?.takeIf { it.isNotBlank() } ?: "Class 10",
                section = userDoc?.studentInfo?.section?.takeIf { it.isNotBlank() } ?: "A",
                rollNumber = userDoc?.studentInfo?.rollNumber?.takeIf { it.isNotBlank() } ?: "27",
                schoolName = userDoc?.studentInfo?.schoolName?.takeIf { it.isNotBlank() } ?: "St. Xavier's High School",
                instituteName = instDoc?.name ?: "Chanakya Classes",
                instituteAddress = instDoc?.address ?: "12, MG Road, Metro Pillar 44, Bengaluru",
                institutePhone = instDoc?.phone ?: "+91 80 4123 4567",
                instituteEmail = instDoc?.email ?: "support@chanakyaclasses.in",
                attendancePercent = 94,
                homeworkDoneText = "18/20",
                gpaGrade = "3.8 / 4.0",
                notificationsEnabled = userDoc?.notificationPrefs?.get("all") ?: true,
                isDemo = false
            )
            Result.success(profile)
        } catch (_: Exception) {
            Result.success(demoProfile)
        }
    }

    override suspend fun updatePersonalInfo(
        name: String,
        email: String,
        phone: String,
        schoolName: String
    ): Result<Unit> {
        val user = auth.currentUser
        if (user == null) {
            demoProfile = demoProfile.copy(
                name = name,
                email = email,
                phone = phone,
                schoolName = schoolName
            )
            return Result.success(Unit)
        }

        return try {
            val updates = hashMapOf<String, Any>(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "studentInfo.schoolName" to schoolName
            )
            db.collection("users").document(user.uid).set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            demoProfile = demoProfile.copy(
                name = name,
                email = email,
                phone = phone,
                schoolName = schoolName
            )
            Result.success(Unit)
        }
    }

    override suspend fun updateNotificationPreference(enabled: Boolean): Result<Unit> {
        val user = auth.currentUser
        if (user == null) {
            demoProfile = demoProfile.copy(notificationsEnabled = enabled)
            return Result.success(Unit)
        }

        return try {
            val updates = hashMapOf<String, Any>(
                "notificationPrefs.all" to enabled
            )
            db.collection("users").document(user.uid).set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            demoProfile = demoProfile.copy(notificationsEnabled = enabled)
            Result.success(Unit)
        }
    }
}
