package com.example.mytuition.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.mytuition.core.data.local.TokenManager

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashState>(SplashState.Loading)
    val uiState: StateFlow<SplashState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val session = authRepository.getCurrentSession()
            if (session != null) {
                _uiState.value = SplashState.NavigateToHome(session.role)
            } else {
                val onboardingCompleted = tokenManager?.isOnboardingCompletedSync() ?: true
                if (onboardingCompleted) {
                    _uiState.value = SplashState.NavigateToLogin
                } else {
                    _uiState.value = SplashState.NavigateToOnboarding
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            authRepository: AuthRepository,
            tokenManager: TokenManager? = null
        ): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SplashViewModel(authRepository, tokenManager) as T
                }
            }
    }
}

sealed interface SplashState {
    object Loading : SplashState
    data class NavigateToHome(val role: com.example.mytuition.core.domain.model.UserRole = com.example.mytuition.core.domain.model.UserRole.STUDENT) : SplashState
    object NavigateToLogin : SplashState
    object NavigateToOnboarding : SplashState
}
