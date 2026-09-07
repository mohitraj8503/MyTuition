package com.example.mytuition.feature.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.model.Subject
import com.example.mytuition.core.domain.usecase.GetSubjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubjectsViewModel(
    private val getSubjectsUseCase: GetSubjectsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubjectsUiState>(SubjectsUiState.Loading)
    val uiState: StateFlow<SubjectsUiState> = _uiState.asStateFlow()

    init {
        loadSubjects()
    }

    fun loadSubjects() {
        viewModelScope.launch {
            _uiState.value = SubjectsUiState.Loading
            val result = getSubjectsUseCase()
            if (result.isSuccess) {
                val list = result.getOrNull() ?: emptyList()
                if (list.isEmpty()) {
                    _uiState.value = SubjectsUiState.Empty("No subjects found.")
                } else {
                    _uiState.value = SubjectsUiState.Success(list)
                }
            } else {
                _uiState.value = SubjectsUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load subjects")
            }
        }
    }

    companion object {
        fun provideFactory(getSubjectsUseCase: GetSubjectsUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SubjectsViewModel(getSubjectsUseCase) as T
                }
            }
    }
}

sealed interface SubjectsUiState {
    object Loading : SubjectsUiState
    data class Success(val subjects: List<Subject>) : SubjectsUiState
    data class Empty(val message: String) : SubjectsUiState
    data class Error(val message: String) : SubjectsUiState
}
