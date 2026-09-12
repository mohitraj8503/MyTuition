package com.example.mytuition.feature.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.usecase.GetHomeworkListUseCase
import com.example.mytuition.core.domain.usecase.MarkHomeworkCompleteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class HomeworkFilter {
    ALL, PENDING, COMPLETED, OVERDUE
}

class HomeworkViewModel(
    private val getHomeworkListUseCase: GetHomeworkListUseCase,
    private val markHomeworkCompleteUseCase: MarkHomeworkCompleteUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeworkUiState>(HomeworkUiState.Loading)
    val uiState: StateFlow<HomeworkUiState> = _uiState.asStateFlow()

    private val _currentFilter = MutableStateFlow(HomeworkFilter.ALL)
    val currentFilter: StateFlow<HomeworkFilter> = _currentFilter.asStateFlow()

    private val _allHomework = MutableStateFlow<List<Homework>>(emptyList())
    val allHomework: StateFlow<List<Homework>> = _allHomework.asStateFlow()

    init {
        loadHomework()
    }

    fun loadHomework() {
        viewModelScope.launch {
            _uiState.value = HomeworkUiState.Loading
            val result = getHomeworkListUseCase()
            if (result.isSuccess) {
                val list = result.getOrNull() ?: emptyList()
                _allHomework.value = list
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
        val list = _allHomework.value
        val filteredList = when (filter) {
            HomeworkFilter.ALL -> list
            HomeworkFilter.PENDING -> list.filter { it.status == HomeworkStatus.PENDING }
            HomeworkFilter.COMPLETED -> list.filter { it.status == HomeworkStatus.COMPLETED }
            HomeworkFilter.OVERDUE -> list.filter { it.status == HomeworkStatus.OVERDUE }
        }

        if (filteredList.isEmpty()) {
            _uiState.value = HomeworkUiState.Empty("No homework found for this filter.")
        } else {
            _uiState.value = HomeworkUiState.Success(filteredList)
        }
    }

    fun toggleHomeworkComplete(homeworkId: String) {
        viewModelScope.launch {
            _allHomework.value = _allHomework.value.map { hw ->
                if (hw.id == homeworkId) {
                    val newStatus = if (hw.status == HomeworkStatus.COMPLETED) HomeworkStatus.PENDING else HomeworkStatus.COMPLETED
                    hw.copy(status = newStatus)
                } else hw
            }
            applyFilter(_currentFilter.value)
            markHomeworkCompleteUseCase?.invoke(homeworkId)
        }
    }

    val totalCount: Int get() = _allHomework.value.size
    val completedCount: Int get() = _allHomework.value.count { it.status == HomeworkStatus.COMPLETED }
    val pendingCount: Int get() = _allHomework.value.count { it.status == HomeworkStatus.PENDING }
    val overdueCount: Int get() = _allHomework.value.count { it.status == HomeworkStatus.OVERDUE }

    companion object {
        fun provideFactory(
            getHomeworkListUseCase: GetHomeworkListUseCase = AppContainer.getHomeworkListUseCase,
            markHomeworkCompleteUseCase: MarkHomeworkCompleteUseCase = AppContainer.markHomeworkCompleteUseCase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeworkViewModel(getHomeworkListUseCase, markHomeworkCompleteUseCase) as T
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
