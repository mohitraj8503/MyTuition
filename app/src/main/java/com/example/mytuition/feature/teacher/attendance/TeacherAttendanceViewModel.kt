package com.example.mytuition.feature.teacher.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.model.AttendanceStatus
import com.example.mytuition.core.domain.model.StudentRosterItem
import com.example.mytuition.core.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AttendanceUiState {
    object Loading : AttendanceUiState
    data class Ready(
        val sessionId: String,
        val students: List<StudentRosterItem>,
        val attendanceMap: Map<String, AttendanceStatus>,
        val notifyParents: Boolean = true,
        val isSaving: Boolean = false,
        val saveSuccessMessage: String? = null
    ) : AttendanceUiState {
        val allMarked: Boolean
            get() = students.isNotEmpty() && students.all { attendanceMap[it.studentId] != null && attendanceMap[it.studentId] != AttendanceStatus.UNMARKED }
        val presentCount: Int
            get() = attendanceMap.values.count { it == AttendanceStatus.PRESENT }
        val absentCount: Int
            get() = attendanceMap.values.count { it == AttendanceStatus.ABSENT }
        val lateCount: Int
            get() = attendanceMap.values.count { it == AttendanceStatus.LATE }
    }
    data class Error(val message: String) : AttendanceUiState
}

class TeacherAttendanceViewModel(
    private val sessionId: String,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            val result = teacherRepository.getBatchStudents("batch_10a_maths")
            result.onSuccess { list ->
                val initialMap = list.associate { it.studentId to AttendanceStatus.UNMARKED }
                _uiState.value = AttendanceUiState.Ready(
                    sessionId = sessionId,
                    students = list,
                    attendanceMap = initialMap
                )
            }.onFailure { error ->
                _uiState.value = AttendanceUiState.Error(error.localizedMessage ?: "Failed to load student roster")
            }
        }
    }

    fun setStatus(studentId: String, status: AttendanceStatus) {
        val current = _uiState.value as? AttendanceUiState.Ready ?: return
        val updated = current.attendanceMap.toMutableMap()
        updated[studentId] = status
        _uiState.value = current.copy(attendanceMap = updated)
    }

    fun markAllPresent() {
        val current = _uiState.value as? AttendanceUiState.Ready ?: return
        val updated = current.students.associate { it.studentId to AttendanceStatus.PRESENT }
        _uiState.value = current.copy(attendanceMap = updated)
    }

    fun toggleNotifyParents(notify: Boolean) {
        val current = _uiState.value as? AttendanceUiState.Ready ?: return
        _uiState.value = current.copy(notifyParents = notify)
    }

    fun saveAttendance(onSaved: () -> Unit) {
        val current = _uiState.value as? AttendanceUiState.Ready ?: return
        if (!current.allMarked) return

        viewModelScope.launch {
            _uiState.value = current.copy(isSaving = true)
            val result = teacherRepository.markAttendance(
                sessionId = sessionId,
                attendanceMap = current.attendanceMap,
                notifyParents = current.notifyParents
            )
            result.onSuccess {
                _uiState.value = current.copy(
                    isSaving = false,
                    saveSuccessMessage = "Attendance saved • ${current.presentCount} present, ${current.absentCount} absent"
                )
                onSaved()
            }.onFailure { error ->
                _uiState.value = current.copy(isSaving = false)
            }
        }
    }

    companion object {
        fun provideFactory(sessionId: String, repository: TeacherRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TeacherAttendanceViewModel(sessionId, repository) as T
                }
            }
    }
}
