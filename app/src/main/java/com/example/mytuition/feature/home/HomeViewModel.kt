package com.example.mytuition.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class HomeViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            delay(1000) // mock network delay
            _uiState.value = HomeUiState.Success(
                studentName = "Mohit",
                className = "Class 10-A",
                todayClasses = listOf("5:00 PM — Mathematics", "6:30 PM — Physics"),
                homeworkDueCount = 2,
                outstandingFee = "₹2,500 due on 10 Sep",
                recentMessages = listOf(
                    "Tomorrow's Mathematics class starts at 5 PM.",
                    "New Physics worksheet uploaded."
                )
            )
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
        }
    }

    companion object {
        fun provideFactory(authRepository: AuthRepository): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(authRepository) as T
                }
            }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val studentName: String,
        val className: String,
        val todayClasses: List<String>,
        val homeworkDueCount: Int,
        val outstandingFee: String,
        val recentMessages: List<String>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
