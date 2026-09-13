package com.example.mytuition.core.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PbAuthResponse(
    val token: String,
    val record: PbUserRecord
)

@JsonClass(generateAdapter = true)
data class PbUserRecord(
    val id: String,
    val username: String? = null,
    val email: String? = null,
    val name: String? = null,
    val role: String? = "STUDENT",
    val phone: String? = null,
    val avatarFile: String? = null,
    val institute: String? = null,
    val classGrade: String? = null,
    val section: String? = null,
    val rollNumber: String? = null,
    val schoolName: String? = null,
    val batches: List<String>? = null,
    val primaryBatch: String? = null,
    val initialPassword: String? = null,
    val teacherId: String? = null,
    val qualification: String? = null,
    val experienceYears: Int? = null,
    val parentUser: String? = null,
    val parentName: String? = null,
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbListResponse<T>(
    val page: Int = 1,
    val perPage: Int = 50,
    val totalItems: Int = 0,
    val totalPages: Int = 1,
    val items: List<T> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PbRegisterStudentRequest(
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val batchId: String,
    val monthlyFee: Double = 0.0,
    val joinDate: String? = null,
    val customPassword: String? = null,
    val rollNumber: String? = null,
    val notifyParent: Boolean = true
)

@JsonClass(generateAdapter = true)
data class PbRegisterStudentResponse(
    val studentUid: String,
    val username: String,
    val password: String,
    val batchName: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class PbCredentialsResponse(
    val studentId: String,
    val username: String,
    val password: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class PbResetPasswordRequest(
    val newPassword: String? = null
)

@JsonClass(generateAdapter = true)
data class PbMarkAttendanceRequest(
    val sessionId: String,
    val attendanceMap: Map<String, String>,
    val notifyParents: Boolean = true
)

@JsonClass(generateAdapter = true)
data class PbCreateHomeworkRequest(
    val batchId: String,
    val title: String,
    val description: String = "",
    val dueDate: String
)

@JsonClass(generateAdapter = true)
data class PbCreateHomeworkResponse(
    val homeworkId: String,
    val studentCount: Int
)

@JsonClass(generateAdapter = true)
data class PbMarkFeePaidRequest(
    val feeId: String,
    val paymentMethod: String = "CASH",
    val paymentNote: String? = null
)

@JsonClass(generateAdapter = true)
data class PbMarkFeePaidResponse(
    val success: Boolean = true,
    val fee: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbMarkFeePendingRequest(
    val feeId: String
)

@JsonClass(generateAdapter = true)
data class PbMarkFeePendingResponse(
    val success: Boolean = true,
    val fee: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbHomeworkRecord(
    val id: String,
    val batch: String? = null,
    val teacher: String? = null,
    val subject: String? = null,
    val title: String? = null,
    val description: String? = null,
    val chapter: String? = null,
    val dueDate: String? = null,
    val status: String? = "ACTIVE",
    val files: List<String>? = null,
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbClassSessionRecord(
    val id: String,
    val batch: String? = null,
    val subject: String? = null,
    val teacher: String? = null,
    val room: String? = null,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val topic: String? = null,
    val status: String? = "SCHEDULED",
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbAttendanceRecord(
    val id: String,
    val session: String? = null,
    val student: String? = null,
    val status: String? = "PRESENT",
    val date: String? = null,
    val markedBy: String? = null,
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbResourceRecord(
    val id: String,
    val title: String? = null,
    val file: String? = null,
    val type: String? = "DOCUMENT",
    val size: Long? = 0L,
    val subject: String? = null,
    val batch: String? = null,
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbHomeworkSubmissionRecord(
    val id: String,
    val homework: String? = null,
    val student: String? = null,
    val status: String? = "PENDING",
    val submittedAt: String? = null,
    val gradedAt: String? = null,
    val grade: String? = null,
    val remarks: String? = null,
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbBatchRecord(
    val id: String,
    val name: String? = null,
    val institute: String? = null,
    val subject: String? = null,
    val teacher: String? = null,
    val classGrade: String? = null,
    val section: String? = null,
    val defaultRoom: String? = null,
    val defaultFee: Double? = 0.0,
    val students: List<String>? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbSubjectRecord(
    val id: String,
    val name: String? = null,
    val code: String? = null,
    val iconColor: String? = null,
    val iconName: String? = null,
    val institute: String? = null
)

@JsonClass(generateAdapter = true)
data class PbFeeRecord(
    val id: String,
    val student: String? = null,
    val batch: String? = null,
    val institute: String? = null,
    val amount: Double? = 0.0,
    val status: String? = "PENDING",
    val dueDate: String? = null,
    val paidDate: String? = null,
    val paymentMethod: String? = null,
    val razorpayOrderId: String? = null,
    val razorpayPaymentId: String? = null,
    val academicYear: String? = null,
    val month: String? = null,
    val expand: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class PbAnnouncementRecord(
    val id: String,
    val title: String? = null,
    val message: String? = null,
    val type: String? = "GENERAL",
    val date: String? = null,
    val institute: String? = null
)

@JsonClass(generateAdapter = true)
data class PbNextClassDto(
    val id: String,
    val subjectName: String? = "",
    val teacherName: String? = "",
    val timeText: String? = "",
    val countdownText: String? = "",
    val room: String? = "",
    val floor: String? = "",
    val directionsNote: String? = "",
    val status: String? = "SCHEDULED"
)

@JsonClass(generateAdapter = true)
data class PbWeekDateDto(
    val date: String,
    val dayAbbr: String,
    val dayNumber: String,
    val hasClasses: Boolean
)

@JsonClass(generateAdapter = true)
data class PbTimelineDto(
    val sessionId: String,
    val time: String? = "",
    val startTimeDisplay: String? = "",
    val endTimeDisplay: String? = "",
    val subjectName: String? = "",
    val topic: String? = "",
    val subjectIconColorHex: String? = "#34C759",
    val subjectIconName: String? = "calculate",
    val teacherName: String? = "",
    val roomName: String? = "",
    val floorName: String? = "",
    val studentCount: Int = 0,
    val status: String? = "SCHEDULED",
    val attendanceTaken: Boolean = false,
    val batchId: String? = null
)

@JsonClass(generateAdapter = true)
data class PbHomeSummaryDto(
    val attendancePercent: Int = 0,
    val classesAttended: Int = 0,
    val totalClasses: Int = 0,
    val feeStatus: String = "PAID",
    val feeAmount: Double = 0.0,
    val pendingHomeworkCount: Int = 0,
    val outstandingFeeText: String = ""
)

@JsonClass(generateAdapter = true)
data class PbStudentHomeDataResponse(
    val studentName: String = "",
    val className: String = "",
    val tuitionName: String = "",
    val avatarUrl: String? = null,
    val nextClass: PbNextClassDto? = null,
    val weekDates: List<PbWeekDateDto> = emptyList(),
    val selectedDate: String = "",
    val timeline: List<PbTimelineDto> = emptyList(),
    val summary: PbHomeSummaryDto = PbHomeSummaryDto(),
    val announcements: List<PbAnnouncementRecord> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PbTeacherHomeDataResponse(
    val teacherName: String = "",
    val teacherId: String = "",
    val instituteName: String = "",
    val totalStudents: Int = 0,
    val activeBatches: Int = 0,
    val todayClassesCount: Int = 0,
    val pendingReviewCount: Int = 0,
    val pendingAttendanceCount: Int = 0,
    val totalCollectedMonth: Double = 0.0,
    val weekDates: List<PbWeekDateDto> = emptyList(),
    val timeline: List<PbTimelineDto> = emptyList(),
    val announcements: List<PbAnnouncementRecord> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PbTeacherBatchEarningDto(
    val batchId: String,
    val batchName: String,
    val defaultFee: Double = 0.0,
    val collectedAmount: Double = 0.0,
    val pendingAmount: Double = 0.0,
    val paidCount: Int = 0,
    val pendingCount: Int = 0,
    val totalStudents: Int = 0
)

@JsonClass(generateAdapter = true)
data class PbTeacherEarningsResponse(
    val period: String = "",
    val totalCollected: Double = 0.0,
    val totalPending: Double = 0.0,
    val paidCount: Int = 0,
    val pendingCount: Int = 0,
    val batches: List<PbTeacherBatchEarningDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PbCheckShortnameResponse(
    val available: Boolean,
    val shortName: String,
    val suggestions: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PbCreateBatchRequest(
    val name: String,
    val subject: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val defaultRoom: String? = null,
    val defaultFee: Double? = 0.0
)

@JsonClass(generateAdapter = true)
data class PbRemindPendingHomeworkRequest(
    val homeworkId: String
)

