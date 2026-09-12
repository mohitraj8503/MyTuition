package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.FirebaseConfig
import com.example.mytuition.core.data.model.HomeworkDoc
import com.example.mytuition.core.data.model.HomeworkSubmissionDoc
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.repository.HomeworkRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseHomeworkRepository(
    private val db: FirebaseFirestore = FirebaseConfig.db,
    private val auth: FirebaseAuth = FirebaseConfig.auth
) : HomeworkRepository {

    override suspend fun getHomeworkList(): Result<List<Homework>> {
        return try {
            val snapshot = db.collection("homework").get().await()
            val currentUid = auth.currentUser?.uid ?: "stu_789"
            
            val submissionsSnap = db.collection("homework_submissions")
                .whereEqualTo("studentUid", currentUid)
                .get().await()

            val submissionsMap = submissionsSnap.documents.mapNotNull { doc ->
                doc.toObject(HomeworkSubmissionDoc::class.java)
            }.associateBy { it.homeworkId }

            val list = snapshot.documents.mapNotNull { doc ->
                val hwDoc = doc.toObject(HomeworkDoc::class.java) ?: return@mapNotNull null
                val submission = submissionsMap[hwDoc.homeworkId]
                hwDoc.toHomework(submission)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHomeworkDetail(id: String): Result<Homework> {
        return try {
            val doc = db.collection("homework").document(id).get().await()
            if (!doc.exists()) {
                return Result.failure(Exception("Homework not found"))
            }
            val hwDoc = doc.toObject(HomeworkDoc::class.java)!!
            val currentUid = auth.currentUser?.uid ?: "stu_789"

            val submissionDoc = db.collection("homework_submissions")
                .whereEqualTo("homeworkId", id)
                .whereEqualTo("studentUid", currentUid)
                .get().await()
                .documents
                .firstOrNull()
                ?.toObject(HomeworkSubmissionDoc::class.java)

            Result.success(hwDoc.toHomework(submissionDoc))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markHomeworkComplete(id: String): Result<Unit> {
        return try {
            val currentUid = auth.currentUser?.uid ?: "stu_789"
            val submissionId = "sub_${id}_${currentUid}"
            val subRef = db.collection("homework_submissions").document(submissionId)
            
            val data = mapOf(
                "submissionId" to submissionId,
                "homeworkId" to id,
                "studentId" to "stu_789",
                "studentUid" to currentUid,
                "status" to "SUBMITTED",
                "submittedAt" to FieldValue.serverTimestamp().toString()
            )
            subRef.set(data).await()

            // Also try calling Cloud Function if available
            try {
                FirebaseConfig.functions.getHttpsCallable("markHomeworkComplete")
                    .call(mapOf("homeworkId" to id))
                    .await()
            } catch (_: Exception) {
                // Ignore function invocation error when offline
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
