package com.example.mytuition.feature.classdetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.data.network.PbClassSessionRecord
import com.example.mytuition.core.data.repository.HomeRepository
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Professor(val name: String, val avatarUrl: String?)

data class ClassInfo(
    val dateTime: String,      // "12 Nov • 10 AM"
    val title: String,          // "History of Physics"
    val duration: String,       // "1h 30m"
    val type: String,           // "Interactive"
    val illustrationUrl: String?
)

data class Lesson(
    val icon: ImageVector,
    val iconColor: Color,
    val subjectName: String,
    val duration: String,
    val resourceType: String,
    val resourceIcon: ImageVector
)

data class ClassDetailUiState(
    val isLoading: Boolean = false,
    val professor: Professor? = null,
    val classInfo: ClassInfo? = null,
    val lastLessons: List<Lesson> = emptyList(),
    val error: String? = null
)

class ClassDetailViewModel(
    private val classId: String,
    private val api: PocketBaseApi? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassDetailUiState(isLoading = true))
    val uiState: StateFlow<ClassDetailUiState> = _uiState.asStateFlow()

    init {
        loadClassDetail()
    }

    fun loadClassDetail() {
        viewModelScope.launch {
            _uiState.value = ClassDetailUiState(isLoading = true)
            if (api == null || classId.isBlank()) {
                _uiState.value = ClassDetailUiState(isLoading = false, error = "Class not found")
                return@launch
            }

            try {
                val resp = api.getClassSession(classId)
                if (resp.isSuccessful && resp.body() != null) {
                    val s = resp.body()!!
                    val expand = s.expand
                    val teacherMap = expand?.get("teacher") as? Map<*, *>
                    val subjectMap = expand?.get("subject") as? Map<*, *>
                    val roomMap = expand?.get("room") as? Map<*, *>

                    val teacherName = (teacherMap?.get("name") as? String) ?: "Teacher"
                    val teacherAvatar = (teacherMap?.get("avatarFile") as? String)?.let {
                        val tId = teacherMap["id"] as? String ?: ""
                        "${com.example.mytuition.core.data.network.PocketBaseClient.DEFAULT_BASE_URL}files/users/$tId/$it"
                    }

                    val subjectName = (subjectMap?.get("name") as? String) ?: "General"
                    val roomName = (roomMap?.get("name") as? String) ?: ""
                    val startTime = s.startTime ?: ""
                    val endTime = s.endTime ?: ""

                    // Compute real duration
                    val durationText = computeDuration(startTime, endTime)
                    val dateFormatted = s.date ?: "Scheduled"

                    // Fetch past completed sessions for this batch (limit 5)
                    val pastLessons = mutableListOf<Lesson>()
                    val batchId = s.batch
                    if (!batchId.isNullOrBlank()) {
                        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                        try {
                            val pastResp = api.getClassSessions(
                                filter = "batch = '$batchId' && date < '$todayStr' && status = 'COMPLETED'",
                                sort = "-date",
                                perPage = 5
                            )
                            if (pastResp.isSuccessful && pastResp.body() != null) {
                                pastResp.body()!!.items.forEach { pastS ->
                                    val pastSubj = (pastS.expand?.get("subject") as? Map<*, *>)?.get("name") as? String ?: subjectName
                                    val pastDur = computeDuration(pastS.startTime ?: "", pastS.endTime ?: "")
                                    pastLessons.add(
                                        Lesson(
                                            icon = Icons.Rounded.Schedule,
                                            iconColor = MyTuitionColors.SubjectMath,
                                            subjectName = pastS.topic?.ifBlank { pastSubj } ?: pastSubj,
                                            duration = pastDur,
                                            resourceType = pastS.date ?: "Past Class",
                                            resourceIcon = Icons.Rounded.PlayCircle
                                        )
                                    )
                                }
                            }
                        } catch (_: Exception) {}
                    }

                    _uiState.value = ClassDetailUiState(
                        isLoading = false,
                        professor = Professor(
                            name = teacherName,
                            avatarUrl = teacherAvatar
                        ),
                        classInfo = ClassInfo(
                            dateTime = "$dateFormatted • $startTime - $endTime",
                            title = if (!s.topic.isNullOrBlank()) s.topic else subjectName,
                            duration = durationText,
                            type = if (roomName.isNotBlank()) "Room: $roomName" else "Interactive Session",
                            illustrationUrl = null
                        ),
                        lastLessons = pastLessons
                    )
                } else {
                    _uiState.value = ClassDetailUiState(
                        isLoading = false,
                        error = "Class session not found or cancelled"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ClassDetailUiState(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load class details"
                )
            }
        }
    }

    private fun computeDuration(start: String, end: String): String {
        return try {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val d1 = sdf.parse(start.trim().take(5))
            val d2 = sdf.parse(end.trim().take(5))
            if (d1 != null && d2 != null) {
                val diffMin = ((d2.time - d1.time) / (1000 * 60)).toInt()
                if (diffMin > 0) {
                    val h = diffMin / 60
                    val m = diffMin % 60
                    if (h > 0 && m > 0) "${h}h ${m}m"
                    else if (h > 0) "${h}h"
                    else "${m}m"
                } else "1h"
            } else "1h"
        } catch (_: Exception) {
            "1h"
        }
    }

    companion object {
        fun provideFactory(
            classId: String,
            api: PocketBaseApi = AppContainer.pocketBaseApi
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ClassDetailViewModel(classId, api) as T
                }
            }
    }
}
