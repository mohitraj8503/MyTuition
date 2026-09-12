package com.example.mytuition.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentPage: Int = 0,   // 0, 1, 2
    val isNavigating: Boolean = false
)

class OnboardingViewModel(
    private val onCompleteCallback: () -> Unit = {}
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNextPage() {
        if (_uiState.value.currentPage < 2) {
            _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        } else {
            completeOnboarding()
        }
    }

    fun onPreviousPage() {
        if (_uiState.value.currentPage > 0) {
            _uiState.update { it.copy(currentPage = it.currentPage - 1) }
        }
    }

    fun onPageChanged(newPage: Int) {
        _uiState.update { it.copy(currentPage = newPage.coerceIn(0, 2)) }
    }

    fun completeOnboarding() {
        _uiState.update { it.copy(isNavigating = true) }
        onCompleteCallback()
    }

    companion object {
        fun provideFactory(onComplete: () -> Unit = {}): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OnboardingViewModel(onComplete) as T
                }
            }
    }
}
