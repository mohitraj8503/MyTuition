package com.example.mytuition.feature.subjects

import com.example.mytuition.core.domain.model.Subject
import com.example.mytuition.core.domain.repository.SubjectRepository
import com.example.mytuition.core.domain.model.SubjectDetail
import com.example.mytuition.core.domain.usecase.GetSubjectsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSubjects success returns list`() = runTest {
        val mockSubjects = listOf(
            Subject("1", "Math", "Rajesh", null, 2, 0, null)
        )
        val repository = object : SubjectRepository {
            override suspend fun getSubjects() = Result.success(mockSubjects)
            override suspend fun getSubjectDetail(id: String) = Result.failure<SubjectDetail>(Exception())
        }
        
        val viewModel = SubjectsViewModel(GetSubjectsUseCase(repository))
        
        // Initial is Loading
        assertTrue(viewModel.uiState.value is SubjectsUiState.Loading)
        
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is SubjectsUiState.Success)
        assertEquals(mockSubjects, (state as SubjectsUiState.Success).subjects)
    }

    @Test
    fun `loadSubjects empty returns Empty state`() = runTest {
        val repository = object : SubjectRepository {
            override suspend fun getSubjects() = Result.success(emptyList<Subject>())
            override suspend fun getSubjectDetail(id: String) = Result.failure<SubjectDetail>(Exception())
        }
        
        val viewModel = SubjectsViewModel(GetSubjectsUseCase(repository))
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is SubjectsUiState.Empty)
    }

    @Test
    fun `loadSubjects error returns Error state`() = runTest {
        val repository = object : SubjectRepository {
            override suspend fun getSubjects() = Result.failure<List<Subject>>(Exception("Network Error"))
            override suspend fun getSubjectDetail(id: String) = Result.failure<SubjectDetail>(Exception())
        }
        
        val viewModel = SubjectsViewModel(GetSubjectsUseCase(repository))
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is SubjectsUiState.Error)
        assertEquals("Network Error", (state as SubjectsUiState.Error).message)
    }
}
