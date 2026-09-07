package com.example.mytuition.core.data.repository

import com.example.mytuition.core.domain.model.*
import com.example.mytuition.core.domain.repository.HomeworkRepository
import kotlinx.coroutines.delay

class MockHomeworkRepository : HomeworkRepository {
    private val now = System.currentTimeMillis()
    private val oneDay = 86400000L

    private val homeworkList = mutableListOf(
        Homework(
            id = "hw_1",
            subjectName = "Mathematics",
            title = "Quadratic Equations Worksheet",
            description = "Solve questions 1-20 from the attached worksheet. Show all steps and derivations. Please refer to chapter 4 for reference.",
            assignedAt = now - oneDay,
            dueAt = now + oneDay,
            status = HomeworkStatus.PENDING,
            teacherName = "Rajesh Sir",
            attachments = listOf(
                Resource("res_1", "Worksheet.pdf", ResourceType.PDF, null, 1024 * 1024)
            )
        ),
        Homework(
            id = "hw_2",
            subjectName = "Physics",
            title = "Current Electricity Numericals",
            description = "Complete the numericals from Chapter 4 of the textbook.",
            assignedAt = now - 2 * oneDay,
            dueAt = now,
            status = HomeworkStatus.OVERDUE,
            teacherName = "Anita Ma'am",
            attachments = emptyList()
        ),
        Homework(
            id = "hw_3",
            subjectName = "Chemistry",
            title = "Chemical Reactions Notes",
            description = "Read and make notes on types of chemical reactions. Focus on balancing equations.",
            assignedAt = now - 3 * oneDay,
            dueAt = now - oneDay,
            status = HomeworkStatus.COMPLETED,
            teacherName = "Vikram Sir",
            attachments = listOf(
                Resource("res_2", "Reactions_Chapter.pdf", ResourceType.PDF, null, 2 * 1024 * 1024)
            )
        ),
        Homework(
            id = "hw_4",
            subjectName = "English",
            title = "Essay: Impact of Technology",
            description = "Write a 500-word essay on the impact of technology on modern education. Must be typed.",
            assignedAt = now,
            dueAt = now + 2 * oneDay,
            status = HomeworkStatus.PENDING,
            teacherName = "Meera Ma'am",
            attachments = emptyList()
        )
    )

    override suspend fun getHomeworkList(): Result<List<Homework>> {
        delay(800)
        return Result.success(homeworkList.toList())
    }

    override suspend fun getHomeworkDetail(id: String): Result<Homework> {
        delay(500)
        val homework = homeworkList.find { it.id == id }
        return if (homework != null) {
            Result.success(homework)
        } else {
            Result.failure(Exception("Homework not found"))
        }
    }

    override suspend fun markHomeworkComplete(id: String): Result<Unit> {
        delay(500)
        val index = homeworkList.indexOfFirst { it.id == id }
        if (index != -1) {
            val hw = homeworkList[index]
            homeworkList[index] = hw.copy(status = HomeworkStatus.COMPLETED)
            return Result.success(Unit)
        }
        return Result.failure(Exception("Homework not found"))
    }
}
