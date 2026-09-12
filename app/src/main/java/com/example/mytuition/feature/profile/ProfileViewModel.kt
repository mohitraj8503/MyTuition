package com.example.mytuition.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.data.repository.ProfileRepository
import com.example.mytuition.core.data.repository.UserProfileData
import com.example.mytuition.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val profile: UserProfileData) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = profileRepository.getProfileData()
            result.onSuccess { data ->
                _uiState.value = ProfileUiState.Success(data)
            }.onFailure { error ->
                _uiState.value = ProfileUiState.Error(error.localizedMessage ?: "Failed to load profile")
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            val updated = currentState.profile.copy(notificationsEnabled = enabled)
            _uiState.value = ProfileUiState.Success(updated)
            viewModelScope.launch {
                profileRepository.updateNotificationPreference(enabled)
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String, schoolName: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isUpdating.value = true
            val result = profileRepository.updatePersonalInfo(name, email, phone, schoolName)
            _isUpdating.value = false
            if (result.isSuccess) {
                val current = _uiState.value
                if (current is ProfileUiState.Success) {
                    _uiState.value = ProfileUiState.Success(
                        current.profile.copy(
                            name = name,
                            email = email,
                            phone = phone,
                            schoolName = schoolName
                        )
                    )
                }
                onComplete()
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
        }
    }

    companion object {
        fun provideFactory(
            profileRepository: ProfileRepository,
            authRepository: AuthRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(profileRepository, authRepository) as T
                }
            }
    }
}
