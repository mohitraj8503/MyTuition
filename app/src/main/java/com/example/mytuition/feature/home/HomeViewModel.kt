package com.example.mytuition.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.data.repository.HomeRepository
import com.example.mytuition.core.domain.model.HomeData
import com.example.mytuition.core.domain.model.TimelineSessionItem
import com.example.mytuition.core.domain.model.WeekDayItem
import com.example.mytuition.core.designsystem.components.NextClassInfo
import com.example.mytuition.core.domain.repository.AuthRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val data: HomeData
    ) : HomeUiState {
        val studentName: String get() = data.studentName
        val className: String get() = data.className
        val tuitionName: String get() = data.tuitionName
        val nextClass: NextClassInfo? get() = data.nextClass
        val weekDates: List<WeekDayItem> get() = data.weekDates
        val timeline: List<TimelineSessionItem> get() = data.timeline
    }
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timelineListener: ListenerRegistration? = null
    private var currentDate: String = "2025-08-17"

    init {
        loadHomeData(currentDate)
    }

    fun loadHomeData(date: String = currentDate) {
        currentDate = date
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = homeRepository.getHomeData(date)
            result.onSuccess { data ->
                _uiState.value = HomeUiState.Success(data)
                listenToTimelineUpdates(date)
            }.onFailure { error ->
                _uiState.value = HomeUiState.Error(
                    error.localizedMessage ?: "Failed to load tuition schedule. Please retry."
                )
            }
        }
    }

    fun selectDate(date: String) {
        if (date == currentDate) return
        loadHomeData(date)
    }

    private fun listenToTimelineUpdates(date: String) {
        timelineListener?.remove()
        timelineListener = homeRepository.observeTimeline(date) { updatedTimeline ->
            val current = _uiState.value
            if (current is HomeUiState.Success) {
                val updatedData = current.data.copy(timeline = updatedTimeline)
                _uiState.value = HomeUiState.Success(updatedData)
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            timelineListener?.remove()
            authRepository.logout()
            onLogoutSuccess()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timelineListener?.remove()
    }

    companion object {
        fun provideFactory(
            homeRepository: HomeRepository,
            authRepository: AuthRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(homeRepository, authRepository) as T
                }
            }
    }
}
