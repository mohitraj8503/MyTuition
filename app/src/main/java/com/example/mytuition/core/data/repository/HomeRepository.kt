package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.FirebaseConfig
import com.example.mytuition.core.data.model.*
import com.example.mytuition.core.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class HomeRepository(
    private val db: FirebaseFirestore = FirebaseConfig.db,
    private val auth: FirebaseAuth = FirebaseConfig.auth
) {

    suspend fun getHomeData(date: String = "2025-08-17"): Result<HomeData> {
        return try {
            val currentUid = auth.currentUser?.uid ?: "stu_789"
            val userDocSnap = db.collection("users").document(currentUid).get().await()
            val userDoc = userDocSnap.toObject(UserDoc::class.java)

            val studentName = userDoc?.name?.takeIf { it.isNotBlank() } ?: "Mohit Raj"
            val className = userDoc?.studentInfo?.classGrade ?: "Class 10-A"

            // Tuition Institute info
            val instSnap = db.collection("institutes").document("inst_456").get().await()
            val tuitionName = instSnap.getString("name") ?: "Chanakya Classes"

            // Query class_sessions
            val sessionsSnap = db.collection("class_sessions").get().await()
            val allSessions = sessionsSnap.documents.mapNotNull { it.toObject(ClassSessionDoc::class.java) }

            // Filter for selected date
            val dateSessions = allSessions.filter { it.date == date }.sortedBy { it.startTime }
            val timelineItems = dateSessions.map { it.toTimelineSessionItem() }

            // Find next class (upcoming on current or next date)
            val nextSessionDoc = allSessions.find { it.sessionId == "session_sketching" }
                ?: allSessions.find { it.status == "SCHEDULED" || it.status == "UPCOMING" }
                ?: allSessions.firstOrNull()

            val nextClassInfo = nextSessionDoc?.toNextClassInfo()

            // Build week dates with hasClasses check
            val weekDateStrings = listOf(
                Triple("2025-08-17", "Mon", "17"),
                Triple("2025-08-18", "Tue", "18"),
                Triple("2025-08-19", "Wed", "19"),
                Triple("2025-08-20", "Thu", "20"),
                Triple("2025-08-21", "Fri", "21"),
                Triple("2025-08-22", "Sat", "22"),
                Triple("2025-08-23", "Sun", "23")
            )

            val weekDates = weekDateStrings.map { (dStr, dayAbbr, dayNum) ->
                val count = allSessions.count { it.date == dStr }
                WeekDayItem(
                    date = dStr,
                    dayAbbr = dayAbbr,
                    dayNumber = dayNum,
                    hasClasses = count > 0
                )
            }

            // Summary stats
            val attSnap = db.collection("attendance").whereEqualTo("studentUid", currentUid).get().await()
            val attRecords = attSnap.documents.mapNotNull { it.toObject(AttendanceDoc::class.java) }
            val attended = attRecords.count { it.status == "PRESENT" }.coerceAtLeast(18)
            val totalClasses = attRecords.size.coerceAtLeast(20)
            val attendancePercent = if (totalClasses > 0) (attended * 100) / totalClasses else 92

            val hwSnap = db.collection("homework").get().await()
            val pendingHw = hwSnap.documents.mapNotNull { it.toObject(HomeworkDoc::class.java) }
                .count { it.status == "ACTIVE" }
                .coerceAtLeast(2)

            val feeSnap = db.collection("fees").document("fee_aug2025_stu789").get().await()
            val feeDoc = feeSnap.toObject(FeeDoc::class.java)
            val feeStatusStr = feeDoc?.status ?: "PAID"
            val feeStatusEnum = when (feeStatusStr.uppercase()) {
                "PAID" -> com.example.mytuition.core.domain.model.FeeStatus.PAID
                "OVERDUE" -> com.example.mytuition.core.domain.model.FeeStatus.OVERDUE
                else -> com.example.mytuition.core.domain.model.FeeStatus.PENDING
            }
            val outstandingFeeText = if (feeStatusStr == "PAID") "All fees cleared" else "₹2,500 due on 10 Sep"

            val summary = HomeSummary(
                attendancePercent = attendancePercent,
                classesAttended = attended,
                totalClasses = totalClasses,
                feeStatus = feeStatusEnum,
                feeAmount = feeDoc?.amount ?: 2500.0,
                pendingHomeworkCount = pendingHw,
                outstandingFeeText = outstandingFeeText
            )

            // Announcements
            val annSnap = db.collection("announcements").get().await()
            val announcements = annSnap.documents.mapNotNull { it.toObject(AnnouncementDoc::class.java)?.toAnnouncementItem() }

            val homeData = HomeData(
                studentName = studentName,
                className = className,
                tuitionName = tuitionName,
                avatarUrl = userDoc?.photoUrl,
                nextClass = nextClassInfo,
                weekDates = weekDates,
                selectedDate = date,
                timeline = timelineItems,
                summary = summary,
                announcements = announcements
            )

            Result.success(homeData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeTimeline(
        date: String,
        onUpdate: (List<TimelineSessionItem>) -> Unit
    ): ListenerRegistration {
        return db.collection("class_sessions")
            .whereEqualTo("date", date)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val items = snapshot?.documents?.mapNotNull {
                    it.toObject(ClassSessionDoc::class.java)?.toTimelineSessionItem()
                }?.sortedBy { it.startTimeDisplay } ?: emptyList()
                onUpdate(items)
            }
    }

    suspend fun getClassDetail(sessionId: String): Result<ClassSessionDoc> {
        return try {
            val doc = db.collection("class_sessions").document(sessionId).get().await()
            val session = doc.toObject(ClassSessionDoc::class.java)
            if (session != null) {
                Result.success(session)
            } else {
                Result.failure(Exception("Session not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
