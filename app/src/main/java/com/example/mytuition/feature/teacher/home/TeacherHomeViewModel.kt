package com.example.mytuition.feature.teacher.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.model.TeacherHomeData
import com.example.mytuition.core.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed interface TeacherHomeUiState {
    object Loading : TeacherHomeUiState
    data class Success(val data: TeacherHomeData, val selectedDate: String) : TeacherHomeUiState
    data class Error(val message: String) : TeacherHomeUiState
}

class TeacherHomeViewModel(
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherHomeUiState>(TeacherHomeUiState.Loading)
    val uiState: StateFlow<TeacherHomeUiState> = _uiState.asStateFlow()

    private var currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        loadHomeData(currentDate)
    }

    fun selectDate(date: String) {
        currentDate = date
        loadHomeData(date)
    }

    fun refresh() {
        loadHomeData(currentDate)
    }

    private fun loadHomeData(date: String) {
        viewModelScope.launch {
            _uiState.value = TeacherHomeUiState.Loading
            val result = teacherRepository.getTeacherHomeData(date)
            result.onSuccess { data ->
                _uiState.value = TeacherHomeUiState.Success(data, date)
            }.onFailure { error ->
                _uiState.value = TeacherHomeUiState.Error(error.localizedMessage ?: "Failed to load teacher dashboard")
            }
        }
    }

    companion object {
        fun provideFactory(repository: TeacherRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TeacherHomeViewModel(repository) as T
                }
            }
    }
}
