package com.example.mytuition.feature.homework

import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.usecase.GetHomeworkDetailUseCase
import com.example.mytuition.core.domain.usecase.MarkHomeworkCompleteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeworkDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var mockRepository: FakeHomeworkRepository
    private lateinit var getHomeworkDetailUseCase: GetHomeworkDetailUseCase
    private lateinit var markHomeworkCompleteUseCase: MarkHomeworkCompleteUseCase
    private lateinit var viewModel: HomeworkDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = FakeHomeworkRepository()
        getHomeworkDetailUseCase = GetHomeworkDetailUseCase(mockRepository)
        markHomeworkCompleteUseCase = MarkHomeworkCompleteUseCase(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDetail success updates uiState`() = runTest {
        viewModel = HomeworkDetailViewModel("1", getHomeworkDetailUseCase, markHomeworkCompleteUseCase)
        
        assertEquals(HomeworkDetailUiState.Loading, viewModel.uiState.value)
        
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is HomeworkDetailUiState.Success)
        assertEquals("Title 1", (state as HomeworkDetailUiState.Success).homework.title)
    }
}
