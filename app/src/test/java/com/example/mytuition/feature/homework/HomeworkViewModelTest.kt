package com.example.mytuition.feature.homework

import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.repository.HomeworkRepository
import com.example.mytuition.core.domain.usecase.GetHomeworkListUseCase
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
class HomeworkViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var mockRepository: FakeHomeworkRepository
    private lateinit var getHomeworkListUseCase: GetHomeworkListUseCase
    private lateinit var viewModel: HomeworkViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = FakeHomeworkRepository()
        getHomeworkListUseCase = GetHomeworkListUseCase(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadHomework success updates uiState to Success`() = runTest {
        viewModel = HomeworkViewModel(getHomeworkListUseCase)
        
        // Initial state
        assertEquals(HomeworkUiState.Loading, viewModel.uiState.value)
        
        advanceUntilIdle() // Process coroutines
        
        val state = viewModel.uiState.value
        assertTrue(state is HomeworkUiState.Success)
        assertEquals(2, (state as HomeworkUiState.Success).homeworkList.size)
    }

    @Test
    fun `applyFilter PENDING shows only pending homework`() = runTest {
        viewModel = HomeworkViewModel(getHomeworkListUseCase)
        advanceUntilIdle()
        
        viewModel.setFilter(HomeworkFilter.PENDING)
        
        val state = viewModel.uiState.value
        assertTrue(state is HomeworkUiState.Success)
        val list = (state as HomeworkUiState.Success).homeworkList
        assertEquals(1, list.size)
        assertEquals(HomeworkStatus.PENDING, list[0].status)
    }
}

class FakeHomeworkRepository : HomeworkRepository {
    override suspend fun getHomeworkList(): Result<List<Homework>> {
        return Result.success(listOf(
            Homework(
                "1", "Math", "Title 1", "Desc 1", 0L, null, HomeworkStatus.PENDING, "Teacher 1", emptyList()
            ),
            Homework(
                "2", "Science", "Title 2", "Desc 2", 0L, null, HomeworkStatus.COMPLETED, "Teacher 2", emptyList()
            )
        ))
    }

    override suspend fun getHomeworkDetail(id: String): Result<Homework> {
        return Result.success(Homework(
                "1", "Math", "Title 1", "Desc 1", 0L, null, HomeworkStatus.PENDING, "Teacher 1", emptyList()
            ))
    }

    override suspend fun markHomeworkComplete(id: String): Result<Unit> {
        return Result.success(Unit)
    }
}
