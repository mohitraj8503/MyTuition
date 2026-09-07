package com.example.mytuition.feature.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.model.SubjectDetail
import com.example.mytuition.core.domain.usecase.GetSubjectDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubjectDetailViewModel(
    private val subjectId: String,
    private val getSubjectDetailUseCase: GetSubjectDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubjectDetailUiState>(SubjectDetailUiState.Loading)
    val uiState: StateFlow<SubjectDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = SubjectDetailUiState.Loading
            val result = getSubjectDetailUseCase(subjectId)
            if (result.isSuccess) {
                _uiState.value = SubjectDetailUiState.Success(result.getOrThrow())
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to load subject detail"
                if (errorMsg.contains("not found", ignoreCase = true)) {
                    _uiState.value = SubjectDetailUiState.NotFound
                } else {
                    _uiState.value = SubjectDetailUiState.Error(errorMsg)
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            subjectId: String,
            getSubjectDetailUseCase: GetSubjectDetailUseCase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SubjectDetailViewModel(subjectId, getSubjectDetailUseCase) as T
                }
            }
    }
}

sealed interface SubjectDetailUiState {
    object Loading : SubjectDetailUiState
    data class Success(val detail: SubjectDetail) : SubjectDetailUiState
    data class Error(val message: String) : SubjectDetailUiState
    object NotFound : SubjectDetailUiState
}
