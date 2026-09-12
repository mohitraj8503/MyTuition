package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.FirebaseConfig
import com.example.mytuition.core.data.model.HomeworkDoc
import com.example.mytuition.core.data.model.SubjectDoc
import com.example.mytuition.core.data.model.TeacherDoc
import com.example.mytuition.core.domain.model.*
import com.example.mytuition.core.domain.repository.SubjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseSubjectRepository(
    private val db: FirebaseFirestore = FirebaseConfig.db,
    private val auth: FirebaseAuth = FirebaseConfig.auth
) : SubjectRepository {

    override suspend fun getSubjects(): Result<List<Subject>> {
        return try {
            if (auth.currentUser == null) {
                return Result.success(getFallbackSubjects())
            }
            val subjectsSnap = db.collection("subjects").get().await()
            val teachersSnap = db.collection("teachers").get().await()
            val homeworkSnap = db.collection("homework").get().await()

            val teachers = teachersSnap.documents.mapNotNull { it.toObject(TeacherDoc::class.java) }
            val activeHw = homeworkSnap.documents.mapNotNull { it.toObject(HomeworkDoc::class.java) }

            val list = subjectsSnap.documents.mapNotNull { doc ->
                val subj = doc.toObject(SubjectDoc::class.java) ?: return@mapNotNull null
                val teacher = teachers.find { it.subjects.contains(subj.subjectId) }
                val teacherName = teacher?.name ?: "Dr. Aalvina Fatehi"
                val pendingCount = activeHw.count { it.subjectId == subj.subjectId && it.status == "ACTIVE" }

                subj.toSubject(
                    pendingHomeworkCount = pendingCount,
                    teacherName = teacherName,
                    nextClass = System.currentTimeMillis() + 4 * 3600000L
                )
            }
            if (list.isNotEmpty()) {
                Result.success(list)
            } else {
                Result.success(getFallbackSubjects())
            }
        } catch (_: Exception) {
            Result.success(getFallbackSubjects())
        }
    }

    override suspend fun getSubjectDetail(id: String): Result<SubjectDetail> {
        return try {
            val subjectDocSnap = db.collection("subjects").document(id).get().await()
            val subj = subjectDocSnap.toObject(SubjectDoc::class.java)
                ?: return Result.success(getFallbackSubjectDetail(id))

            val teachersSnap = db.collection("teachers").get().await()
            val teacher = teachersSnap.documents
                .mapNotNull { it.toObject(TeacherDoc::class.java) }
                .find { it.subjects.contains(subj.subjectId) }
            val teacherName = teacher?.name ?: "Dr. Aalvina Fatehi"

            val hwSnap = db.collection("homework")
                .whereEqualTo("subjectId", id)
                .get().await()
            val recentHw = hwSnap.documents.mapNotNull { it.toObject(HomeworkDoc::class.java)?.toHomework() }

            val resources = subj.syllabus.mapIndexed { idx, chapter ->
                Resource(
                    id = "res_${subj.subjectId}_$idx",
                    title = "Chapter ${chapter.chapter}: ${chapter.title}",
                    type = ResourceType.PDF,
                    url = null,
                    sizeBytes = 1024L * 512
                )
            }

            val domainSubject = subj.toSubject(
                pendingHomeworkCount = recentHw.count { it.status == HomeworkStatus.PENDING },
                teacherName = teacherName,
                nextClass = System.currentTimeMillis() + 3600000L * 4
            )

            Result.success(
                SubjectDetail(
                    subject = domainSubject,
                    recentHomework = recentHw,
                    recentResources = resources
                )
            )
        } catch (_: Exception) {
            Result.success(getFallbackSubjectDetail(id))
        }
    }

    private fun getFallbackSubjects(): List<Subject> {
        return listOf(
            Subject(
                id = "subj_art",
                name = "Creative Sketching",
                teacherName = "Dr. Aalvina Fatehi",
                teacherAvatarUrl = null,
                homeworkCount = 0,
                resourceCount = 4,
                nextClass = System.currentTimeMillis() + 3600000L * 2
            ),
            Subject(
                id = "subj_maths",
                name = "Mathematics",
                teacherName = "Mr. Rakesh Sharma",
                teacherAvatarUrl = null,
                homeworkCount = 1,
                resourceCount = 6,
                nextClass = System.currentTimeMillis() + 3600000L * 5
            ),
            Subject(
                id = "subj_science",
                name = "Science",
                teacherName = "Ms. Sara Khan",
                teacherAvatarUrl = null,
                homeworkCount = 1,
                resourceCount = 5,
                nextClass = System.currentTimeMillis() + 86400000L
            ),
            Subject(
                id = "subj_english",
                name = "English Literature",
                teacherName = "Mrs. Anjali Das",
                teacherAvatarUrl = null,
                homeworkCount = 0,
                resourceCount = 3,
                nextClass = System.currentTimeMillis() + 86400000L * 2
            )
        )
    }

    private fun getFallbackSubjectDetail(id: String): SubjectDetail {
        val subject = getFallbackSubjects().find { it.id == id } ?: getFallbackSubjects().first()
        return SubjectDetail(
            subject = subject,
            recentHomework = emptyList(),
            recentResources = listOf(
                Resource(
                    id = "res_1",
                    title = "Course Syllabus & Notes",
                    type = ResourceType.PDF,
                    url = null,
                    sizeBytes = 1024L * 800
                )
            )
        )
    }
}
