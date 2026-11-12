package com.example.mytuition.core.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

import com.example.mytuition.core.data.local.AppDatabase
import com.example.mytuition.core.data.local.HomeDataCacheEntity
import com.example.mytuition.core.data.local.TokenManager
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.designsystem.components.NextClassInfo
import com.example.mytuition.core.designsystem.components.NextClassStatus
import com.example.mytuition.core.domain.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

typealias PocketBaseHomeRepository = HomeRepository

class HomeRepository(
    private val api: PocketBaseApi? = null,
    private val tokenManager: TokenManager? = null,
    private val database: AppDatabase? = null
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(HomeData::class.java)

    private suspend fun getCacheKey(date: String): String? {
        val manager = tokenManager ?: return null
        val userId = manager.getUserIdSync()?.takeIf { it.isNotBlank() } ?: return null
        val instituteId = manager.getInstituteId().first()?.takeIf { it.isNotBlank() } ?: return null
        return "home_data_${instituteId}_${userId}_$date"
    }

    suspend fun getHomeData(date: String): Result<HomeData> {
        val requestCacheKey = getCacheKey(date)
        // 1. Try real PocketBase network API
        if (api != null) {
            try {
                val resp = api.getStudentHomeData(date)
                if (resp.isSuccessful && resp.body() != null) {
                    val raw = resp.body()!!
                    val nextClass = raw.nextClass?.let {
                        NextClassInfo(
                            id = it.id,
                            subjectName = it.subjectName ?: "",
                            teacherName = it.teacherName ?: "",
                            timeText = it.timeText ?: "",
                            countdownText = it.countdownText ?: "",
                            room = it.room ?: "",
                            floor = it.floor ?: "",
                            directionsNote = it.directionsNote ?: "",
                            status = when (it.status) {
                                "CANCELLED" -> NextClassStatus.CANCELLED
                                "RESCHEDULED" -> NextClassStatus.RESCHEDULED
                                else -> NextClassStatus.UPCOMING
                            }
                        )
                    }

                    val weekDates = raw.weekDates.map {
                        WeekDayItem(
                            date = it.date,
                            dayAbbr = it.dayAbbr,
                            dayNumber = it.dayNumber,
                            hasClasses = it.hasClasses
                        )
                    }

                    val timeline = raw.timeline.map {
                        TimelineSessionItem(
                            sessionId = it.sessionId,
                            time = it.time ?: "",
                            startTimeDisplay = it.startTimeDisplay ?: "",
                            endTimeDisplay = it.endTimeDisplay ?: "",
                            subjectName = it.subjectName ?: "",
                            topic = it.topic ?: "",
                            subjectIconColorHex = it.subjectIconColorHex ?: "#34C759",
                            subjectIconName = it.subjectIconName ?: "calculate",
                            teacherName = it.teacherName ?: "",
                            roomName = it.roomName ?: "",
                            floorName = it.floorName ?: "",
                            status = it.status ?: "SCHEDULED"
                        )
                    }

                    val summary = HomeSummary(
                        attendancePercent = raw.summary.attendancePercent,
                        classesAttended = raw.summary.classesAttended,
                        totalClasses = raw.summary.totalClasses,
                        feeStatus = if (raw.summary.feeStatus == "PAID") FeeStatus.PAID else FeeStatus.PENDING,
                        feeAmount = raw.summary.feeAmount,
                        pendingHomeworkCount = raw.summary.pendingHomeworkCount,
                        outstandingFeeText = raw.summary.outstandingFeeText
                    )

                    val announcements = raw.announcements.map {
                        AnnouncementItem(
                            id = it.id,
                            title = it.title ?: "",
                            message = it.message ?: "",
                            type = it.type ?: "GENERAL",
                            date = it.date ?: ""
                        )
                    }

                    val homeData = HomeData(
                        studentName = raw.studentName,
                        className = raw.className,
                        tuitionName = raw.tuitionName,
                        avatarUrl = raw.avatarUrl,
                        nextClass = nextClass,
                        weekDates = weekDates,
                        selectedDate = raw.selectedDate,
                        timeline = timeline,
                        summary = summary,
                        announcements = announcements
                    )

                    // Cache only if the account that initiated this request is still active.
                    try {
                        if (requestCacheKey != null && requestCacheKey == getCacheKey(date)) {
                            val json = adapter.toJson(homeData)
                            database?.homeDataCacheDao()?.saveHomeData(
                                HomeDataCacheEntity(date = requestCacheKey, dataJson = json)
                            )
                        }
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (_: Exception) {
                        // Cache failure must not fail a successful network response.
                    }

                    return Result.success(homeData)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Network failed -> fallback only to Room database cache
            }
        }

        // 2. Offline Fallback: Room Database cache ONLY (no static fallback models)
        if (database != null && requestCacheKey != null && requestCacheKey == getCacheKey(date)) {
            try {
                val cached = database.homeDataCacheDao().getHomeData(requestCacheKey)
                if (cached != null && cached.dataJson.isNotBlank()) {
                    val parsed = adapter.fromJson(cached.dataJson)
                    if (parsed != null) {
                        return Result.success(parsed.copy(isFromCache = true))
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Cache read failure falls through to the normal network error.
            }
        }

        return Result.failure(Exception("Unable to load home data. Please check your network connection."))
    }
}
