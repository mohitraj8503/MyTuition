package com.example.mytuition.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class TeacherHomeData(
    val teacherName: String = "Mr. Rakesh Sharma",
    val teacherId: String = "teacher_001",
    val instituteName: String = "Chanakya Classes",
    val nextSession: TeacherSessionItem? = null,
    val todaySessions: List<TeacherSessionItem> = emptyList(),
    val weekDates: List<WeekDayItem> = emptyList(),
    val pendingAttendanceCount: Int = 0,
    val latestHomeworkStats: String = "0/0",
    val todayAnnouncementsCount: Int = 0,
    val totalStudentsCount: Int = 0,
    val recentAnnouncements: List<AnnouncementItem> = emptyList()
)

@Immutable
data class TeacherSessionItem(
    val sessionId: String,
    val batchId: String,
    val batchName: String,
    val subjectName: String,
    val roomName: String,
    val floorName: String = "",
    val startTime: String,
    val endTime: String,
    val date: String,
    val studentCount: Int = 0,
    val status: SessionStatus = SessionStatus.SCHEDULED,
    val attendanceTaken: Boolean = false
) {
    val timeSlotDisplay: String get() = "$startTime - $endTime"
}

enum class SessionStatus {
    SCHEDULED,
    ONGOING,
    COMPLETED,
    CANCELLED
}

enum class AttendanceStatus {
    UNMARKED,
    PRESENT,
    ABSENT,
    LATE
}

@Immutable
data class StudentRosterItem(
    val studentId: String,
    val name: String,
    val rollNumber: String,
    val avatarUrl: String? = null,
    val classGrade: String = "Class 10",
    val section: String = "A",
    val schoolName: String = "",
    val attendancePercent: Int = 90,
    val pendingHomeworkCount: Int = 0,
    val parentName: String = "",
    val parentPhone: String = "",
    val parentRelationship: String = "Parent",
    val feeStatus: String = "Paid",
    val latestFeeId: String? = null,
    val latestFeeAmount: Double = 0.0,
    val latestFeeMonth: String = "",
    val latestFeeDueDate: String = ""
)

@Immutable
data class TeacherHomeworkSummary(
    val homeworkId: String,
    val batchId: String,
    val batchName: String,
    val subjectName: String,
    val title: String,
    val description: String,
    val chapter: String = "",
    val dueDate: String,
    val totalStudents: Int = 0,
    val submittedCount: Int = 0,
    val pendingCount: Int = 0,
    val overdueCount: Int = 0,
    val status: String = "ACTIVE"
) {
    val progressFraction: Float
        get() = if (totalStudents > 0) submittedCount.toFloat() / totalStudents.toFloat() else 0f
    val progressText: String
        get() = "$submittedCount/$totalStudents Done"
}

@Immutable
data class TeacherHomeworkSubmission(
    val submissionId: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val avatarUrl: String? = null,
    val submittedAt: String? = null,
    val status: HomeworkStatus = HomeworkStatus.PENDING,
    val grade: String? = null,
    val remarks: String? = null,
    val attachmentUrls: List<String> = emptyList()
)

@Immutable
data class TeacherEarning(
    val month: String = "August",
    val year: Int = 2025,
    val totalCollected: Long = 48000,
    val totalPendingStudents: Int = 5,
    val batchSummaries: List<TeacherBatchEarning> = emptyList()
)

@Immutable
data class TeacherBatchEarning(
    val batchId: String,
    val batchName: String,
    val totalStudents: Int,
    val feesCollectedCount: Int,
    val feesPendingCount: Int,
    val amountCollected: Long
)
