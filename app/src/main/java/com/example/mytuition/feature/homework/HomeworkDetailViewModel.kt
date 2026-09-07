package com.example.mytuition.feature.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.usecase.GetHomeworkDetailUseCase
import com.example.mytuition.core.domain.usecase.MarkHomeworkCompleteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeworkDetailViewModel(
    private val homeworkId: String,
    private val getHomeworkDetailUseCase: GetHomeworkDetailUseCase,
    private val markHomeworkCompleteUseCase: MarkHomeworkCompleteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeworkDetailUiState>(HomeworkDetailUiState.Loading)
    val uiState: StateFlow<HomeworkDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = HomeworkDetailUiState.Loading
            val result = getHomeworkDetailUseCase(homeworkId)
            if (result.isSuccess) {
                _uiState.value = HomeworkDetailUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = HomeworkDetailUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load homework detail")
            }
        }
    }

    fun markComplete() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is HomeworkDetailUiState.Success) {
                _uiState.value = HomeworkDetailUiState.Loading
                val result = markHomeworkCompleteUseCase(homeworkId)
                if (result.isSuccess) {
                    loadDetail() // Refresh
                } else {
                    _uiState.value = HomeworkDetailUiState.Error(result.exceptionOrNull()?.message ?: "Failed to update status")
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            homeworkId: String,
            getHomeworkDetailUseCase: GetHomeworkDetailUseCase,
            markHomeworkCompleteUseCase: MarkHomeworkCompleteUseCase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeworkDetailViewModel(
                        homeworkId,
                        getHomeworkDetailUseCase,
                        markHomeworkCompleteUseCase
                    ) as T
                }
            }
    }
}

sealed interface HomeworkDetailUiState {
    object Loading : HomeworkDetailUiState
    data class Success(val homework: Homework) : HomeworkDetailUiState
    data class Error(val message: String) : HomeworkDetailUiState
}
