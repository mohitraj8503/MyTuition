package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.local.TokenManager
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.data.network.PocketBaseClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PocketBaseProfileRepository(
    private val api: PocketBaseApi,
    private val tokenManager: TokenManager
) : ProfileRepository {

    override suspend fun getProfileData(): Result<UserProfileData> {
        return try {
            val userId = tokenManager.getUserIdSync()
            if (userId.isNullOrBlank()) {
                return Result.failure(Exception("Not logged in"))
            }

            // 1. Fetch user record from PocketBase
            val userResp = api.getUser(userId)
            if (!userResp.isSuccessful || userResp.body() == null) {
                return Result.failure(Exception("Failed to fetch user profile: ${userResp.code()}"))
            }
            val u = userResp.body()!!

            // 2. Resolve institute info
            val instExpand = u.expand?.get("institute") as? Map<*, *>
            val instituteName = (instExpand?.get("name") as? String)
                ?: (u.institute ?: "MyTuition Academy")
            val instituteAddress = (instExpand?.get("address") as? String)
                ?: "Metro Pillar 44, Main Road, Bengaluru"
            val institutePhone = (instExpand?.get("phone") as? String)
                ?: "+91 80 4123 4567"
            val instituteEmail = (instExpand?.get("email") as? String)
                ?: "support@mytuition.in"

            // 3. Avatar URL
            val avatarUrl = if (!u.avatarFile.isNullOrBlank()) {
                "${PocketBaseClient.DEFAULT_BASE_URL}files/users/${u.id}/${u.avatarFile}"
            } else null

            // 4. Real attendance calculations for current month & history
            val cal = Calendar.getInstance()
            val currentMonthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)
            var attendancePercent = 0
            val attendanceHistoryList = mutableListOf<AttendanceRecordItem>()
            try {
                val attResp = api.getAttendance(filter = "student = '$userId'", sort = "-date", perPage = 30)
                if (attResp.isSuccessful && attResp.body() != null) {
                    val attItems = attResp.body()!!.items
                    val currentMonthItems = attItems.filter { it.date?.startsWith(currentMonthStr) == true }
                    val totalAtt = currentMonthItems.size
                    val presentOrLate = currentMonthItems.count {
                        val st = it.status?.uppercase()
                        st == "PRESENT" || st == "LATE" || st == "EXCUSED"
                    }
                    if (totalAtt > 0) {
                        attendancePercent = Math.round((presentOrLate.toDouble() / totalAtt) * 100).toInt()
                    }

                    attItems.forEach { item ->
                        attendanceHistoryList.add(
                            AttendanceRecordItem(
                                id = item.id,
                                date = item.date ?: "",
                                status = item.status ?: "PRESENT"
                            )
                        )
                    }
                }
            } catch (_: Exception) {}

            // 5. Real homework completion calculations
            var homeworkDoneText = "0/0"
            try {
                val hwResp = api.getHomeworkList()
                val totalHwCount = hwResp.body()?.items?.size ?: 0
                val subResp = api.getHomeworkSubmissions(filter = "student = '$userId' && status = 'COMPLETED'")
                val completedCount = subResp.body()?.items?.size ?: 0
                homeworkDoneText = "$completedCount/$totalHwCount"
            } catch (_: Exception) {}

            // 6. Latest fee status
            var feeStatus = "PAID"
            try {
                val feeResp = api.getFees(filter = "student = '$userId'", sort = "-dueDate")
                if (feeResp.isSuccessful && feeResp.body() != null) {
                    val latestFee = feeResp.body()!!.items.firstOrNull()
                    if (latestFee != null) {
                        feeStatus = latestFee.status ?: "PENDING"
                    }
                }
            } catch (_: Exception) {}

            val notificationsEnabled = tokenManager.getNotificationsEnabledSync()

            val profileData = UserProfileData(
                uid = u.id,
                name = u.name ?: "Student",
                email = u.email ?: "",
                phone = u.phone ?: "",
                studentId = u.rollNumber ?: u.id,
                classGrade = u.classGrade ?: "Grade 10",
                section = u.section ?: "A",
                rollNumber = u.rollNumber ?: "-",
                schoolName = u.schoolName ?: "High School",
                instituteName = instituteName,
                instituteAddress = instituteAddress,
                institutePhone = institutePhone,
                instituteEmail = instituteEmail,
                attendancePercent = attendancePercent,
                homeworkDoneText = homeworkDoneText,
                gpaGrade = "A",
                notificationsEnabled = notificationsEnabled,
                isDemo = false,
                avatarUrl = avatarUrl,
                feeStatus = feeStatus,
                attendanceHistory = attendanceHistoryList
            )

            Result.success(profileData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePersonalInfo(
        name: String,
        email: String,
        phone: String,
        schoolName: String
    ): Result<Unit> {
        return try {
            val userId = tokenManager.getUserIdSync()
                ?: return Result.failure(Exception("Not logged in"))

            val patchBody = mutableMapOf<String, Any?>(
                "name" to name,
                "phone" to phone,
                "schoolName" to schoolName
            )
            if (email.isNotBlank()) {
                patchBody["email"] = email
            }

            val resp = api.updateUser(userId, patchBody)
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update profile: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNotificationPreference(enabled: Boolean): Result<Unit> {
        return try {
            tokenManager.setNotificationsEnabled(enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
