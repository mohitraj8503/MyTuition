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
    private val homeRepository: HomeRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassDetailUiState(isLoading = true))
    val uiState: StateFlow<ClassDetailUiState> = _uiState.asStateFlow()

    init {
        loadClassDetail()
    }

    fun loadClassDetail() {
        viewModelScope.launch {
            _uiState.value = ClassDetailUiState(isLoading = true)
            val result = homeRepository?.getClassDetail(classId)
            if (result != null && result.isSuccess) {
                val session = result.getOrThrow()
                _uiState.value = ClassDetailUiState(
                    isLoading = false,
                    professor = Professor(
                        name = session.teacherName.ifBlank { "Dr. Aalvina Fatehi" },
                        avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=300&q=80"
                    ),
                    classInfo = ClassInfo(
                        dateTime = "${session.date} • ${session.startTimeDisplay}",
                        title = session.subjectName,
                        duration = "1h 30m",
                        type = session.sessionType,
                        illustrationUrl = null
                    ),
                    lastLessons = listOf(
                        Lesson(
                            icon = Icons.Rounded.Schedule,
                            iconColor = MyTuitionColors.SubjectMath,
                            subjectName = session.subjectName,
                            duration = "55 min",
                            resourceType = "Video",
                            resourceIcon = Icons.Rounded.PlayCircle
                        ),
                        Lesson(
                            icon = Icons.Rounded.Science,
                            iconColor = MyTuitionColors.SubjectPhysics,
                            subjectName = "Physics",
                            duration = "50 min",
                            resourceType = "Notes",
                            resourceIcon = Icons.Rounded.Description
                        )
                    )
                )
            } else {
                _uiState.value = ClassDetailUiState(
                    isLoading = false,
                    professor = Professor(
                        name = "Dr. Aalvina Fatehi",
                        avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=300&q=80"
                    ),
                    classInfo = ClassInfo(
                        dateTime = "Today • 5:00 PM",
                        title = "Creative Sketching",
                        duration = "1h 30m",
                        type = "Class",
                        illustrationUrl = null
                    )
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            classId: String,
            homeRepository: HomeRepository = AppContainer.homeRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ClassDetailViewModel(classId, homeRepository) as T
                }
            }
    }
}
