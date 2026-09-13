package com.example.mytuition.core.domain.repository

import com.example.mytuition.core.domain.model.*

interface TeacherRepository {
    suspend fun getTeacherHomeData(date: String): Result<TeacherHomeData>
    suspend fun getBatchStudents(batchId: String): Result<List<StudentRosterItem>>
    suspend fun markAttendance(
        sessionId: String,
        attendanceMap: Map<String, AttendanceStatus>,
        notifyParents: Boolean
    ): Result<Unit>
    suspend fun getTeacherHomeworkList(): Result<List<TeacherHomeworkSummary>>
    suspend fun getHomeworkSubmissions(homeworkId: String): Result<List<TeacherHomeworkSubmission>>
    suspend fun createHomework(
        batchId: String,
        title: String,
        description: String,
        chapter: String,
        dueDate: String,
        attachments: List<String>,
        notifyStudents: Boolean
    ): Result<String>
    suspend fun gradeSubmission(submissionId: String, grade: String, remarks: String): Result<Unit>
    suspend fun remindPendingHomework(homeworkId: String): Result<Unit>
    suspend fun getTeacherAnnouncements(): Result<List<AnnouncementItem>>
    suspend fun postAnnouncement(
        title: String,
        message: String,
        type: String,
        targetBatchIds: List<String>
    ): Result<Unit>
    suspend fun getStudentProfileForTeacher(studentId: String): Result<StudentRosterItem>
    suspend fun rescheduleSession(sessionId: String, newDate: String, newStartTime: String, newEndTime: String): Result<Unit>
    suspend fun cancelSession(sessionId: String, reason: String): Result<Unit>
    suspend fun getTeacherEarnings(month: String, year: Int): Result<TeacherEarning>

    // Student Registration & Credential Management
    suspend fun getTeacherBatches(): Result<List<TeacherBatchItem>>
    suspend fun createBatch(name: String, subjectName: String, days: List<String>, startTime: String, endTime: String, roomName: String, defaultFee: Double): Result<TeacherBatchItem>
    suspend fun registerStudent(
        name: String,
        phone: String?,
        email: String?,
        batchId: String,
        monthlyFee: Double,
        joinDate: String,
        regFee: Double?,
        notifyParent: Boolean,
        customPassword: String?,
        rollNumber: String?
    ): Result<RegistrationResult>
    suspend fun getStudentCredentials(studentId: String): Result<StudentCredentials>
    suspend fun resetStudentPassword(studentId: String, newPassword: String?): Result<StudentCredentials>
}
