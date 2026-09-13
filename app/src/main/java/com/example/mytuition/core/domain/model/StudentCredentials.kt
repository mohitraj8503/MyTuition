package com.example.mytuition.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class StudentCredentials(
    val username: String,
    val passwordPlain: String,
    val studentUid: String = "",
    val studentId: String = "",
    val name: String = ""
)

@Immutable
data class RegistrationResult(
    val studentUid: String,
    val studentId: String,
    val username: String,
    val password: String,
    val batchName: String,
    val feeRecordId: String = ""
)

@Immutable
data class InstituteShortName(
    val shortName: String,
    val available: Boolean,
    val suggestions: List<String> = emptyList()
)

@Immutable
data class TeacherBatchItem(
    val batchId: String,
    val name: String,
    val subjectName: String,
    val scheduleDisplay: String = "Mon, Wed, Fri • 4:00 - 5:30 PM",
    val roomName: String = "Room 4B",
    val defaultFee: Double = 2500.0
)
