package com.example.mytuition.core.data.repository

import com.example.mytuition.core.domain.model.*
import com.example.mytuition.core.domain.repository.SubjectRepository
import kotlinx.coroutines.delay

class MockSubjectRepository : SubjectRepository {
    private val now = System.currentTimeMillis()

    // Deterministic mock data matching the prompt requirements
    private val subjects = listOf(
        Subject(
            id = "sub_math",
            name = "Mathematics",
            teacherName = "Rajesh Sir",
            teacherAvatarUrl = null,
            homeworkCount = 2,
            resourceCount = 12,
            nextClass = now + 4 * 3600000 // Today, 4 hours from now
        ),
        Subject(
            id = "sub_physics",
            name = "Physics",
            teacherName = "Ankit Sir",
            teacherAvatarUrl = null,
            homeworkCount = 1,
            resourceCount = 0,
            nextClass = null
        ),
        Subject(
            id = "sub_chem",
            name = "Chemistry",
            teacherName = "Priya Ma'am",
            teacherAvatarUrl = null,
            homeworkCount = 3,
            resourceCount = 0,
            nextClass = null
        ),
        Subject(
            id = "sub_eng",
            name = "English",
            teacherName = "Neha Ma'am",
            teacherAvatarUrl = null,
            homeworkCount = 0,
            resourceCount = 0,
            nextClass = null
        ),
        Subject(
            id = "sub_cs",
            name = "Computer Science",
            teacherName = "Arjun Sir",
            teacherAvatarUrl = null,
            homeworkCount = 0,
            resourceCount = 0,
            nextClass = null
        )
    )

    override suspend fun getSubjects(): Result<List<Subject>> {
        delay(600)
        return Result.success(subjects)
    }

    override suspend fun getSubjectDetail(id: String): Result<SubjectDetail> {
        delay(600)
        val subject = subjects.find { it.id == id }
            ?: return Result.failure(Exception("Subject not found"))
            
        // Provide mock detail lists
        val mockHomework = if (subject.homeworkCount > 0) {
            List(subject.homeworkCount) { i ->
                Homework(
                    id = "hw_${id}_${i}",
                    subjectName = subject.name,
                    title = "Assignment ${i + 1}",
                    description = "Complete practice questions.",
                    assignedAt = now - 86400000,
                    dueAt = now + 86400000,
                    status = HomeworkStatus.PENDING,
                    teacherName = subject.teacherName,
                    attachments = emptyList()
                )
            }
        } else emptyList()

        val mockResources = if (subject.resourceCount > 0) {
            List(subject.resourceCount.coerceAtMost(3)) { i ->
                Resource(
                    id = "res_${id}_${i}",
                    title = "Chapter ${i + 1} Notes",
                    type = ResourceType.PDF,
                    url = null,
                    sizeBytes = 1024 * 512
                )
            }
        } else emptyList()

        return Result.success(
            SubjectDetail(
                subject = subject,
                recentHomework = mockHomework,
                recentResources = mockResources
            )
        )
    }
}
