package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.network.PbCreateHomeworkRequest
import com.example.mytuition.core.data.network.PbMarkAttendanceRequest
import com.example.mytuition.core.data.network.PbRegisterStudentRequest
import com.example.mytuition.core.data.network.PbResetPasswordRequest
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.domain.model.*
import com.example.mytuition.core.domain.repository.TeacherRepository
import java.text.SimpleDateFormat
import java.util.*

class PocketBaseTeacherRepository(
    private val api: PocketBaseApi
) : TeacherRepository {

    override suspend fun getTeacherHomeData(date: String): Result<TeacherHomeData> {
        return try {
            val resp = api.getTeacherHomeData(date)
            if (resp.isSuccessful && resp.body() != null) {
                val b = resp.body()!!
                val todaySessions = b.timeline.map {
                    TeacherSessionItem(
                        sessionId = it.sessionId,
                        batchId = it.batchId ?: "",
                        batchName = it.subjectName ?: "Class Session",
                        subjectName = it.subjectName ?: "",
                        roomName = it.roomName ?: "",
                        floorName = it.floorName ?: "",
                        startTime = it.startTimeDisplay ?: "",
                        endTime = it.endTimeDisplay ?: "",
                        date = date,
                        studentCount = it.studentCount,
                        status = when (it.status) {
                            "CANCELLED" -> SessionStatus.CANCELLED
                            "COMPLETED" -> SessionStatus.COMPLETED
                            "IN_PROGRESS" -> SessionStatus.ONGOING
                            else -> SessionStatus.SCHEDULED
                        },
                        attendanceTaken = it.attendanceTaken
                    )
                }

                val weekDates = b.weekDates.map {
                    WeekDayItem(
                        date = it.date,
                        dayAbbr = it.dayAbbr,
                        dayNumber = it.dayNumber,
                        hasClasses = it.hasClasses
                    )
                }

                val announcements = b.announcements.map {
                    AnnouncementItem(
                        id = it.id,
                        title = it.title ?: "",
                        message = it.message ?: "",
                        type = it.type ?: "GENERAL",
                        date = it.date ?: ""
                    )
                }

                val data = TeacherHomeData(
                    teacherName = b.teacherName,
                    teacherId = b.teacherId,
                    instituteName = b.instituteName,
                    nextSession = todaySessions.firstOrNull(),
                    todaySessions = todaySessions,
                    weekDates = weekDates,
                    pendingAttendanceCount = b.pendingAttendanceCount,
                    latestHomeworkStats = "${b.pendingReviewCount} pending",
                    todayAnnouncementsCount = announcements.size,
                    totalStudentsCount = b.totalStudents,
                    recentAnnouncements = announcements
                )
                Result.success(data)
            } else {
                Result.failure(Exception("Failed to load teacher home data: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBatchStudents(batchId: String): Result<List<StudentRosterItem>> {
        return try {
            val filterQuery = if (batchId.isNotBlank()) "role = 'STUDENT' && batches ~ '$batchId'" else "role = 'STUDENT'"
            val resp = api.getUsers(filter = filterQuery)
            if (resp.isSuccessful && resp.body() != null) {
                val students = resp.body()!!.items
                val list = students.map { u ->
                    buildStudentRosterItem(u)
                }
                Result.success(list)
            } else {
                Result.failure(Exception("Failed to load batch students: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun buildStudentRosterItem(u: com.example.mytuition.core.data.network.PbUserRecord): StudentRosterItem {
        // Attendance calculation from attendance records
        var attendancePercent = 0
        try {
            val attResp = api.getAttendance(filter = "student = '${u.id}'")
            if (attResp.isSuccessful && attResp.body() != null) {
                val records = attResp.body()!!.items
                if (records.isNotEmpty()) {
                    val attended = records.count { it.status == "PRESENT" || it.status == "LATE" || it.status == "EXCUSED" }
                    attendancePercent = Math.round((attended.toFloat() / records.size.toFloat()) * 100)
                } else {
                    attendancePercent = 100
                }
            }
        } catch (_: Exception) {}

        // Pending homework count
        var pendingHomeworkCount = 0
        try {
            val hwSubResp = api.getHomeworkSubmissions(filter = "student = '${u.id}' && status != 'COMPLETED'")
            if (hwSubResp.isSuccessful && hwSubResp.body() != null) {
                pendingHomeworkCount = hwSubResp.body()!!.totalItems
            }
        } catch (_: Exception) {}

        // Parent info from expand or user record
        val parentMap = u.expand?.get("parentUser") as? Map<*, *>
        val parentName = (parentMap?.get("name") as? String)
            ?: u.parentName
            ?: ""
        val parentPhone = (parentMap?.get("phone") as? String)
            ?: u.phone
            ?: ""

        // Fee status from fees collection
        var feeStatus = "Paid"
        var latestFeeId: String? = null
        var latestFeeAmount = 0.0
        var latestFeeMonth = ""
        var latestFeeDueDate = ""
        try {
            val feeResp = api.getFees(filter = "student = '${u.id}'")
            if (feeResp.isSuccessful && feeResp.body() != null) {
                val latestFee = feeResp.body()!!.items.firstOrNull()
                if (latestFee != null) {
                    latestFeeId = latestFee.id
                    latestFeeAmount = latestFee.amount ?: 0.0
                    latestFeeMonth = latestFee.month ?: ""
                    latestFeeDueDate = latestFee.dueDate ?: ""
                    val st = latestFee.status?.uppercase() ?: "PENDING"
                    feeStatus = when (st) {
                        "PAID" -> "Paid"
                        "OVERDUE" -> "Overdue"
                        else -> "Due"
                    }
                }
            }
        } catch (_: Exception) {}

        return StudentRosterItem(
            studentId = u.id,
            name = u.name ?: "Student",
            rollNumber = u.rollNumber ?: "",
            avatarUrl = null,
            classGrade = u.classGrade ?: "Class 10",
            section = u.section ?: "A",
            schoolName = u.schoolName ?: "",
            attendancePercent = attendancePercent,
            pendingHomeworkCount = pendingHomeworkCount,
            parentName = parentName,
            parentPhone = parentPhone,
            parentRelationship = "Parent",
            feeStatus = feeStatus,
            latestFeeId = latestFeeId,
            latestFeeAmount = latestFeeAmount,
            latestFeeMonth = latestFeeMonth,
            latestFeeDueDate = latestFeeDueDate
        )
    }

    override suspend fun markAttendance(
        sessionId: String,
        attendanceMap: Map<String, AttendanceStatus>,
        notifyParents: Boolean
    ): Result<Unit> {
        return try {
            val reqMap = attendanceMap.mapValues { it.value.name }
            val resp = api.markAttendance(PbMarkAttendanceRequest(sessionId, reqMap, notifyParents))
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark attendance: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTeacherHomeworkList(): Result<List<TeacherHomeworkSummary>> {
        return try {
            val resp = api.getHomeworkList()
            if (resp.isSuccessful && resp.body() != null) {
                val list = resp.body()!!.items.map { hw ->
                    val expand = hw.expand
                    val batchMap = expand?.get("batch") as? Map<*, *>
                    val subjMap = expand?.get("subject") as? Map<*, *>

                    var totalStudents = 0
                    var submittedCount = 0
                    var pendingCount = 0
                    var overdueCount = 0

                    try {
                        val subsResp = api.getHomeworkSubmissions(filter = "homework = '${hw.id}'")
                        if (subsResp.isSuccessful && subsResp.body() != null) {
                            val subs = subsResp.body()!!.items
                            totalStudents = subs.size
                            submittedCount = subs.count { it.status == "COMPLETED" }
                            val isPastDue = try {
                                val dueTime = hw.dueDate?.let {
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)?.time
                                } ?: 0L
                                dueTime > 0 && dueTime < System.currentTimeMillis()
                            } catch (_: Exception) { false }

                            subs.filter { it.status != "COMPLETED" }.forEach { sub ->
                                if (sub.status == "OVERDUE" || isPastDue) {
                                    overdueCount++
                                } else {
                                    pendingCount++
                                }
                            }
                        }
                    } catch (_: Exception) {}

                    TeacherHomeworkSummary(
                        homeworkId = hw.id,
                        batchId = hw.batch ?: "",
                        batchName = (batchMap?.get("name") as? String) ?: "Batch",
                        subjectName = (subjMap?.get("name") as? String) ?: "Subject",
                        title = hw.title ?: "",
                        description = hw.description ?: "",
                        chapter = hw.chapter ?: "",
                        dueDate = hw.dueDate ?: "",
                        totalStudents = totalStudents,
                        submittedCount = submittedCount,
                        pendingCount = pendingCount,
                        overdueCount = overdueCount,
                        status = hw.status ?: "ACTIVE"
                    )
                }
                Result.success(list)
            } else {
                Result.failure(Exception("Failed to load homework list: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHomeworkSubmissions(homeworkId: String): Result<List<TeacherHomeworkSubmission>> {
        return try {
            val resp = api.getHomeworkSubmissions(filter = "homework = '$homeworkId'")
            if (resp.isSuccessful && resp.body() != null) {
                val list = resp.body()!!.items.map { sub ->
                    val expand = sub.expand
                    val studentMap = expand?.get("student") as? Map<*, *>
                    TeacherHomeworkSubmission(
                        submissionId = sub.id,
                        studentId = sub.student ?: "",
                        studentName = (studentMap?.get("name") as? String) ?: "Student",
                        rollNumber = (studentMap?.get("rollNumber") as? String) ?: "",
                        avatarUrl = null,
                        submittedAt = sub.submittedAt ?: "Submitted",
                        status = if (sub.status == "COMPLETED") HomeworkStatus.COMPLETED else HomeworkStatus.PENDING,
                        grade = sub.grade,
                        remarks = sub.remarks,
                        attachmentUrls = emptyList()
                    )
                }
                Result.success(list)
            } else {
                Result.failure(Exception("Failed to load submissions: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createHomework(
        batchId: String,
        title: String,
        description: String,
        chapter: String,
        dueDate: String,
        attachments: List<String>,
        notifyStudents: Boolean
    ): Result<String> {
        return try {
            val resp = api.createHomework(PbCreateHomeworkRequest(batchId, title, description, dueDate))
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!.homeworkId)
            } else {
                Result.failure(Exception("Failed to create homework: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun gradeSubmission(submissionId: String, grade: String, remarks: String): Result<Unit> {
        return try {
            val body = mapOf(
                "grade" to grade,
                "remarks" to remarks,
                "status" to "COMPLETED",
                "gradedAt" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
            )
            val resp = api.updateHomeworkSubmission(submissionId, body)
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to grade submission: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun remindPendingHomework(homeworkId: String): Result<Unit> {
        return try {
            val resp = api.remindPendingHomework(com.example.mytuition.core.data.network.PbRemindPendingHomeworkRequest(homeworkId))
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to send reminders: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTeacherAnnouncements(): Result<List<AnnouncementItem>> {
        return try {
            val resp = api.getAnnouncements()
            if (resp.isSuccessful && resp.body() != null) {
                val list = resp.body()!!.items.map {
                    AnnouncementItem(
                        id = it.id,
                        title = it.title ?: "",
                        message = it.message ?: "",
                        type = it.type ?: "GENERAL",
                        date = it.date ?: ""
                    )
                }
                Result.success(list)
            } else {
                Result.failure(Exception("Failed to load announcements: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun postAnnouncement(
        title: String,
        message: String,
        type: String,
        targetBatchIds: List<String>
    ): Result<Unit> {
        return try {
            val body = mapOf(
                "title" to title,
                "message" to message,
                "type" to type,
                "date" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
            )
            val resp = api.createAnnouncement(body)
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to post announcement: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentProfileForTeacher(studentId: String): Result<StudentRosterItem> {
        return try {
            val resp = api.getUserRecord(studentId)
            if (resp.isSuccessful && resp.body() != null) {
                val u = resp.body()!!
                Result.success(buildStudentRosterItem(u))
            } else {
                Result.failure(Exception("Student not found: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rescheduleSession(
        sessionId: String,
        newDate: String,
        newStartTime: String,
        newEndTime: String
    ): Result<Unit> {
        return try {
            val body = mapOf(
                "date" to newDate,
                "startTime" to newStartTime,
                "endTime" to newEndTime,
                "status" to "RESCHEDULED"
            )
            val resp = api.updateClassSession(sessionId, body)
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to reschedule session: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelSession(sessionId: String, reason: String): Result<Unit> {
        return try {
            val body = mapOf(
                "status" to "CANCELLED",
                "notes" to reason
            )
            val resp = api.updateClassSession(sessionId, body)
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel session: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTeacherEarnings(month: String, year: Int): Result<TeacherEarning> {
        return try {
            val resp = api.getTeacherEarnings(month = month, year = year.toString())
            if (resp.isSuccessful && resp.body() != null) {
                val b = resp.body()!!
                val batchSummaries = b.batches.map {
                    TeacherBatchEarning(
                        batchId = it.batchId,
                        batchName = it.batchName,
                        totalStudents = it.totalStudents,
                        feesCollectedCount = it.paidCount,
                        feesPendingCount = it.pendingCount,
                        amountCollected = it.collectedAmount.toLong()
                    )
                }
                Result.success(
                    TeacherEarning(
                        month = month,
                        year = year,
                        totalCollected = b.totalCollected.toLong(),
                        totalPendingStudents = b.pendingCount,
                        batchSummaries = batchSummaries
                    )
                )
            } else {
                Result.failure(Exception("Failed to load earnings: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTeacherBatches(): Result<List<TeacherBatchItem>> {
        return try {
            val resp = api.getBatches()
            if (resp.isSuccessful && resp.body() != null) {
                val items = resp.body()!!.items.map { b ->
                    TeacherBatchItem(
                        batchId = b.id,
                        name = b.name ?: "Batch",
                        subjectName = "Subject",
                        scheduleDisplay = "${b.startTime ?: ""} - ${b.endTime ?: ""}",
                        roomName = b.defaultRoom ?: "Room",
                        defaultFee = b.defaultFee ?: 0.0
                    )
                }
                Result.success(items)
            } else {
                Result.failure(Exception("Failed to fetch batches: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBatch(
        name: String,
        subjectName: String,
        days: List<String>,
        startTime: String,
        endTime: String,
        roomName: String,
        defaultFee: Double
    ): Result<TeacherBatchItem> {
        return try {
            val req = com.example.mytuition.core.data.network.PbCreateBatchRequest(
                name = name,
                startTime = startTime,
                endTime = endTime,
                defaultRoom = roomName,
                defaultFee = defaultFee
            )
            val resp = api.createBatch(req)
            if (resp.isSuccessful && resp.body() != null) {
                val created = resp.body()!!
                val newBatch = TeacherBatchItem(
                    batchId = created.id,
                    name = created.name ?: name,
                    subjectName = subjectName,
                    scheduleDisplay = "${days.joinToString(", ")} • $startTime - $endTime",
                    roomName = created.defaultRoom ?: roomName,
                    defaultFee = created.defaultFee ?: defaultFee
                )
                Result.success(newBatch)
            } else {
                Result.failure(Exception("Failed to create batch: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerStudent(
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
    ): Result<RegistrationResult> {
        return try {
            val response = api.registerStudent(
                PbRegisterStudentRequest(
                    name = name,
                    phone = phone,
                    email = email,
                    batchId = batchId,
                    monthlyFee = monthlyFee,
                    joinDate = joinDate,
                    customPassword = customPassword,
                    rollNumber = rollNumber,
                    notifyParent = notifyParent
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val res = response.body()!!
                Result.success(
                    RegistrationResult(
                        studentUid = res.studentUid,
                        studentId = res.studentUid,
                        username = res.username,
                        password = res.password,
                        batchName = res.batchName,
                        feeRecordId = "fee_${System.currentTimeMillis()}"
                    )
                )
            } else {
                Result.failure(Exception("Student registration failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentCredentials(studentId: String): Result<StudentCredentials> {
        return try {
            val response = api.getStudentCredentials(studentId)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Result.success(
                    StudentCredentials(
                        username = data.username,
                        passwordPlain = data.password,
                        studentUid = data.studentId,
                        studentId = data.studentId,
                        name = data.name
                    )
                )
            } else {
                Result.failure(Exception("Credentials not found: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetStudentPassword(studentId: String, newPassword: String?): Result<StudentCredentials> {
        return try {
            val response = api.resetPassword(studentId, PbResetPasswordRequest(newPassword))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Result.success(
                    StudentCredentials(
                        username = data.username,
                        passwordPlain = data.password,
                        studentUid = data.studentId,
                        studentId = data.studentId,
                        name = data.name
                    )
                )
            } else {
                Result.failure(Exception("Reset password failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
