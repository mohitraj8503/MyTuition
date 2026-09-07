package com.example.mytuition.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthProvider {
    GOOGLE,
    GITHUB,
    DEMO
}

sealed interface LoginUiState {
    object Idle : LoginUiState
    data class Loading(val provider: AuthProvider) : LoginUiState
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading(AuthProvider.GOOGLE)
            val result = authRepository.signInWithGoogle()
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Couldn't sign in with Google.\nPlease try again.")
            }
        }
    }

    fun signInWithGitHub() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading(AuthProvider.GITHUB)
            val result = authRepository.signInWithGitHub()
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Couldn't sign in with GitHub.\nPlease try again.")
            }
        }
    }

    fun enterDemoMode() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading(AuthProvider.DEMO)
            val result = authRepository.enterDemoMode()
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Couldn't enter demo mode.\nPlease try again.")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    companion object {
        fun provideFactory(authRepository: AuthRepository): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(authRepository) as T
                }
            }
    }
}
