package com.example.mytuition.core.domain.model

import com.example.mytuition.core.designsystem.components.NextClassInfo

data class WeekDayItem(
    val date: String,          // "2025-08-17"
    val dayAbbr: String,      // "Mon"
    val dayNumber: String,    // "17"
    val hasClasses: Boolean
)

data class TimelineSessionItem(
    val sessionId: String,
    val time: String,
    val startTimeDisplay: String,
    val endTimeDisplay: String,
    val subjectName: String,
    val topic: String,
    val subjectIconColorHex: String,
    val subjectIconName: String,
    val teacherName: String,
    val roomName: String,
    val floorName: String,
    val status: String
)

data class HomeSummary(
    val attendancePercent: Int = 92,
    val classesAttended: Int = 18,
    val totalClasses: Int = 20,
    val feeStatus: FeeStatus = FeeStatus.PAID,
    val feeAmount: Double? = 2500.0,
    val pendingHomeworkCount: Int = 2,
    val outstandingFeeText: String = "₹2,500 due on 10 Sep"
)

data class AnnouncementItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val date: String
)

data class HomeData(
    val studentName: String,
    val className: String,
    val tuitionName: String,
    val avatarUrl: String?,
    val nextClass: NextClassInfo?,
    val weekDates: List<WeekDayItem>,
    val selectedDate: String,
    val timeline: List<TimelineSessionItem>,
    val summary: HomeSummary,
    val announcements: List<AnnouncementItem>
)
