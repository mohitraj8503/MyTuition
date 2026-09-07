package com.example.mytuition.feature.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.usecase.GetHomeworkListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class HomeworkFilter {
    ALL, PENDING, COMPLETED, OVERDUE
}

class HomeworkViewModel(
    private val getHomeworkListUseCase: GetHomeworkListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeworkUiState>(HomeworkUiState.Loading)
    val uiState: StateFlow<HomeworkUiState> = _uiState.asStateFlow()

    private val _currentFilter = MutableStateFlow(HomeworkFilter.ALL)
    val currentFilter: StateFlow<HomeworkFilter> = _currentFilter.asStateFlow()

    private var allHomework: List<Homework> = emptyList()

    init {
        loadHomework()
    }

    fun loadHomework() {
        viewModelScope.launch {
            _uiState.value = HomeworkUiState.Loading
            val result = getHomeworkListUseCase()
            if (result.isSuccess) {
                allHomework = result.getOrNull() ?: emptyList()
                applyFilter(_currentFilter.value)
            } else {
                _uiState.value = HomeworkUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load homework")
            }
        }
    }

    fun setFilter(filter: HomeworkFilter) {
        _currentFilter.value = filter
        applyFilter(filter)
    }

    private fun applyFilter(filter: HomeworkFilter) {
        val filteredList = when (filter) {
            HomeworkFilter.ALL -> allHomework
            HomeworkFilter.PENDING -> allHomework.filter { it.status == HomeworkStatus.PENDING }
            HomeworkFilter.COMPLETED -> allHomework.filter { it.status == HomeworkStatus.COMPLETED }
            HomeworkFilter.OVERDUE -> allHomework.filter { it.status == HomeworkStatus.OVERDUE }
        }

        if (filteredList.isEmpty()) {
            _uiState.value = HomeworkUiState.Empty("No homework found for this filter.")
        } else {
            _uiState.value = HomeworkUiState.Success(filteredList)
        }
    }

    companion object {
        fun provideFactory(getHomeworkListUseCase: GetHomeworkListUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeworkViewModel(getHomeworkListUseCase) as T
                }
            }
    }
}

sealed interface HomeworkUiState {
    object Loading : HomeworkUiState
    data class Success(val homeworkList: List<Homework>) : HomeworkUiState
    data class Empty(val message: String) : HomeworkUiState
    data class Error(val message: String) : HomeworkUiState
}
