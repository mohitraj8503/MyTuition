package com.example.mytuition.feature.subjects

import com.example.mytuition.core.domain.model.Subject
import com.example.mytuition.core.domain.model.SubjectDetail
import com.example.mytuition.core.domain.repository.SubjectRepository
import com.example.mytuition.core.domain.usecase.GetSubjectDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectDetailViewModelTest {

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
    fun `loadDetail success returns detail`() = runTest {
        val subject = Subject("1", "Math", "Rajesh", null, 0, 0, null)
        val mockDetail = SubjectDetail(subject, emptyList(), emptyList())
        val repository = object : SubjectRepository {
            override suspend fun getSubjects() = Result.success(emptyList<Subject>())
            override suspend fun getSubjectDetail(id: String): Result<SubjectDetail> {
                if (id == "1") return Result.success(mockDetail)
                return Result.failure(Exception("not found"))
            }
        }
        
        val viewModel = SubjectDetailViewModel("1", GetSubjectDetailUseCase(repository))
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is SubjectDetailUiState.Success)
        assertEquals(mockDetail, (state as SubjectDetailUiState.Success).detail)
    }

    @Test
    fun `loadDetail not found returns NotFound state`() = runTest {
        val repository = object : SubjectRepository {
            override suspend fun getSubjects() = Result.success(emptyList<Subject>())
            override suspend fun getSubjectDetail(id: String) = Result.failure<SubjectDetail>(Exception("Subject not found"))
        }
        
        val viewModel = SubjectDetailViewModel("2", GetSubjectDetailUseCase(repository))
        advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is SubjectDetailUiState.NotFound)
    }

    @Test
    fun `loadDetail error returns Error state`() = runTest {
        val repository = object : SubjectRepository {
            override suspend fun getSubjects() = Result.success(emptyList<Subject>())
            override suspend fun getSubjectDetail(id: String) = Result.failure<SubjectDetail>(Exception("Network issue"))
        }
        
        val viewModel = SubjectDetailViewModel("1", GetSubjectDetailUseCase(repository))
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is SubjectDetailUiState.Error)
        assertEquals("Network issue", (state as SubjectDetailUiState.Error).message)
    }
}
