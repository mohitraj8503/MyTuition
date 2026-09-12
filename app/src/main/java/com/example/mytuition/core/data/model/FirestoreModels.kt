package com.example.mytuition.core.data.model

import com.example.mytuition.core.designsystem.components.NextClassInfo
import com.example.mytuition.core.designsystem.components.NextClassStatus
import com.example.mytuition.core.domain.model.*
import com.google.firebase.firestore.PropertyName
import java.text.SimpleDateFormat
import java.util.Locale

data class UserDoc(
    val uid: String = "",
    val instituteId: String = "inst_456",
    val role: String = "STUDENT",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val studentInfo: StudentInfoDoc? = null,
    val parentInfo: ParentInfoDoc? = null,
    val fcmToken: String = "",
    val notificationPrefs: Map<String, Boolean> = emptyMap(),
    val isActive: Boolean = true,
    val createdAt: Any? = null,
    val updatedAt: Any? = null
) {
    fun toUserSession(): UserSession {
        return UserSession(
            uid = uid,
            userId = uid,
            instituteId = instituteId,
            studentId = studentInfo?.studentId ?: uid,
            name = name,
            email = email,
            phone = phone,
            photoUrl = photoUrl.ifEmpty { null },
            role = when (role.uppercase()) {
                "PARENT" -> UserRole.PARENT
                else -> UserRole.STUDENT
            },
            batchIds = studentInfo?.batches ?: emptyList(),
            classGrade = studentInfo?.classGrade ?: "",
            section = studentInfo?.section ?: "",
            rollNumber = studentInfo?.rollNumber ?: "",
            schoolName = studentInfo?.schoolName ?: "",
            accessToken = "firebase_token_$uid",
            refreshToken = null,
            expiresAt = System.currentTimeMillis() + 86400000,
            isDemo = false
        )
    }
}

data class StudentInfoDoc(
    val studentId: String = "",
    val classGrade: String = "Class 10",
    val section: String = "A",
    val rollNumber: String = "27",
    val schoolName: String = "",
    val batches: List<String> = emptyList(),
    val parentUid: String = "",
    val dateOfBirth: String = "",
    val address: String = ""
)

data class ParentInfoDoc(
    val childUids: List<String> = emptyList(),
    val childStudentIds: List<String> = emptyList(),
    val relationship: String = "Father"
)

data class InstituteDoc(
    val instituteId: String = "inst_456",
    val name: String = "Bright Minds Tutorials",
    val address: String = "12, MG Road, Bengaluru, KA 560001",
    val phone: String = "+918012345678",
    val email: String = "contact@brightminds.in",
    val logoUrl: String = "",
    val floors: List<FloorDoc> = emptyList()
)

data class FloorDoc(
    val floorId: String = "",
    val name: String = ""
)

data class BatchDoc(
    val batchId: String = "",
    val instituteId: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val name: String = "",
    val classGrade: String = "",
    val section: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val studentIds: List<String> = emptyList(),
    val defaultRoomId: String = "",
    val defaultRoomName: String = "",
    val defaultFloor: String = "",
    val academicYear: String = "2025-2026"
)

data class SubjectDoc(
    val subjectId: String = "",
    val instituteId: String = "",
    val name: String = "",
    val code: String = "",
    val iconColor: String = "#34C759",
    val iconName: String = "calculate",
    val description: String = "",
    val syllabus: List<SyllabusChapterDoc> = emptyList()
) {
    fun toSubject(
        pendingHomeworkCount: Int = 0,
        teacherName: String = "Instructor",
        nextClass: Long? = null
    ): Subject {
        return Subject(
            id = subjectId,
            name = name,
            teacherName = teacherName,
            teacherAvatarUrl = null,
            homeworkCount = pendingHomeworkCount,
            resourceCount = syllabus.size,
            nextClass = nextClass
        )
    }
}

data class SyllabusChapterDoc(
    val chapter: Int = 0,
    val title: String = "",
    val status: String = "UPCOMING"
)

data class TeacherDoc(
    val teacherId: String = "",
    val instituteId: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val subjects: List<String> = emptyList(),
    val batches: List<String> = emptyList(),
    val qualification: String = "",
    val experienceYears: Int = 0
)

data class ClassSessionDoc(
    val sessionId: String = "",
    val instituteId: String = "",
    val batchId: String = "",
    val batchName: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val subjectIconColor: String = "#34C759",
    val subjectIconName: String = "calculate",
    val teacherId: String = "",
    val teacherName: String = "",
    val roomId: String = "room_4b",
    val roomName: String = "4B",
    val floorName: String = "2nd Floor",
    val directionsNote: String = "Opposite Physics Lab • Next to Staircase B",
    val date: String = "",
    val dayOfWeek: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val startTimeDisplay: String = "",
    val endTimeDisplay: String = "",
    val topic: String = "",
    val chapter: Int = 0,
    val status: String = "SCHEDULED",
    val sessionType: String = "LECTURE",
    val notes: String = ""
) {
    fun toTimelineSessionItem(): TimelineSessionItem {
        val shortTime = startTimeDisplay.replace(" AM", "").replace(" PM", "")
        return TimelineSessionItem(
            sessionId = sessionId,
            time = shortTime,
            startTimeDisplay = startTimeDisplay,
            endTimeDisplay = endTimeDisplay,
            subjectName = subjectName,
            topic = topic,
            subjectIconColorHex = subjectIconColor,
            subjectIconName = subjectIconName,
            teacherName = teacherName,
            roomName = roomName,
            floorName = floorName,
            status = status
        )
    }

    fun toNextClassInfo(): NextClassInfo {
        return NextClassInfo(
            id = sessionId,
            subjectName = subjectName,
            teacherName = teacherName,
            timeText = "Today • $startTimeDisplay - $endTimeDisplay",
            countdownText = "Starts in 45 min",
            room = "Room $roomName",
            floor = "$floorName, Arts Block",
            directionsNote = if (directionsNote.isNotBlank()) directionsNote else "Opposite Physics Lab • Next to Staircase B",
            status = when (status) {
                "CANCELLED" -> NextClassStatus.CANCELLED
                "RESCHEDULED" -> NextClassStatus.RESCHEDULED
                else -> NextClassStatus.UPCOMING
            }
        )
    }
}

data class AttendanceDoc(
    val attendanceId: String = "",
    val instituteId: String = "",
    val sessionId: String = "",
    val studentId: String = "",
    val studentUid: String = "",
    val batchId: String = "",
    val date: String = "",
    val status: String = "PRESENT",
    val markedBy: String = "",
    val markedAt: String = "",
    val remarks: String = ""
)

data class AttachmentDoc(
    val name: String = "",
    val url: String = "",
    val type: String = "PDF",
    val sizeBytes: Long = 0L
)

data class HomeworkDoc(
    val homeworkId: String = "",
    val instituteId: String = "",
    val batchId: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val subjectIconColor: String = "#34C759",
    val teacherId: String = "",
    val teacherName: String = "",
    val title: String = "",
    val description: String = "",
    val assignedDate: String = "",
    val dueDate: String = "",
    val dueDateTime: String = "",
    val chapter: Int = 0,
    val topic: String = "",
    val studentIds: List<String> = emptyList(),
    val attachments: List<AttachmentDoc> = emptyList(),
    val status: String = "ACTIVE"
) {
    fun toHomework(submission: HomeworkSubmissionDoc? = null): Homework {
        val hwStatus = when {
            submission?.status == "SUBMITTED" || submission?.status == "GRADED" -> HomeworkStatus.COMPLETED
            status == "OVERDUE" -> HomeworkStatus.OVERDUE
            else -> HomeworkStatus.PENDING
        }

        val assignedMillis = parseDateToMillis(assignedDate)
        val dueMillis = parseDateToMillis(dueDate)

        val resList = attachments.mapIndexed { idx, att ->
            Resource(
                id = "att_${homeworkId}_$idx",
                title = att.name,
                type = ResourceType.PDF,
                url = att.url,
                sizeBytes = att.sizeBytes
            )
        }

        return Homework(
            id = homeworkId,
            subjectName = subjectName,
            title = title,
            description = description,
            assignedAt = assignedMillis,
            dueAt = dueMillis,
            status = hwStatus,
            teacherName = teacherName,
            attachments = resList
        )
    }

    private fun parseDateToMillis(dateStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}

data class HomeworkSubmissionDoc(
    val submissionId: String = "",
    val homeworkId: String = "",
    val studentId: String = "",
    val studentUid: String = "",
    val status: String = "PENDING",
    val submittedAt: String? = null,
    val gradedAt: String? = null,
    val grade: String? = null,
    val teacherRemarks: String? = null,
    val submissionFiles: List<String> = emptyList()
)

data class FeeDoc(
    val feeId: String = "",
    val instituteId: String = "",
    val studentId: String = "",
    val studentUid: String = "",
    val batchIds: List<String> = emptyList(),
    val amount: Double = 0.0,
    val amountDisplay: String = "₹0",
    val period: String = "MONTHLY",
    val month: String = "AUGUST",
    val academicYear: String = "2025-2026",
    val dueDate: String = "",
    val status: String = "PENDING",
    val paidDate: String? = null,
    val paymentMethod: String? = null,
    val razorpayPaymentId: String? = null,
    val razorpayOrderId: String? = null,
    val receiptUrl: String? = null
)

data class AnnouncementDoc(
    val announcementId: String = "",
    val instituteId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "GENERAL",
    val targetBatchIds: List<String> = emptyList(),
    val targetStudentIds: List<String> = emptyList(),
    val date: String = "",
    val expiresAt: String = ""
) {
    fun toAnnouncementItem(): AnnouncementItem {
        return AnnouncementItem(
            id = announcementId,
            title = title,
            message = message,
            type = type,
            date = date
        )
    }
}
