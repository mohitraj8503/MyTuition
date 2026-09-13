package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.network.PbHomeworkRecord
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.repository.HomeworkRepository
import java.text.SimpleDateFormat
import java.util.Locale

import com.example.mytuition.core.data.local.AppDatabase
import com.example.mytuition.core.data.local.FeatureCacheEntity
import com.example.mytuition.core.data.local.TokenManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class PocketBaseHomeworkRepository(
    private val api: PocketBaseApi,
    private val tokenManager: TokenManager? = null,
    private val database: AppDatabase? = null
) : HomeworkRepository {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Homework::class.java)
    private val adapter = moshi.adapter<List<Homework>>(listType)

    override suspend fun getHomeworkList(): Result<List<Homework>> {
        return try {
            val resp = api.getHomeworkList()
            if (resp.isSuccessful && resp.body() != null) {
                val list = resp.body()!!.items.map { mapRecordToHomework(it) }
                // Cache to Room
                try {
                    val json = adapter.toJson(list)
                    database?.featureCacheDao()?.saveCache(
                        FeatureCacheEntity(key = "homework_list", dataJson = json)
                    )
                } catch (_: Exception) {}
                Result.success(list)
            } else {
                getCachedHomeworkList()
                    ?: Result.failure(Exception("Failed to fetch homework: ${resp.code()} ${resp.message()}"))
            }
        } catch (e: Exception) {
            getCachedHomeworkList() ?: Result.failure(e)
        }
    }

    private suspend fun getCachedHomeworkList(): Result<List<Homework>>? {
        return try {
            val cached = database?.featureCacheDao()?.getCache("homework_list")
            if (cached != null && cached.dataJson.isNotBlank()) {
                val parsed = adapter.fromJson(cached.dataJson)
                if (parsed != null) Result.success(parsed.map { it.copy(isFromCache = true) }) else null
            } else null
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getHomeworkDetail(id: String): Result<Homework> {
        return try {
            val resp = api.getHomeworkDetail(id)
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(mapRecordToHomework(resp.body()!!))
            } else {
                Result.failure(Exception("Homework $id not found: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markHomeworkComplete(id: String): Result<Unit> {
        return try {
            val studentId = tokenManager?.getUserIdSync()
            val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())

            val filterStr = if (!studentId.isNullOrBlank()) {
                "homework = '$id' && student = '$studentId'"
            } else {
                "homework = '$id'"
            }

            val subResp = api.getHomeworkSubmissions(filter = filterStr)
            if (subResp.isSuccessful && subResp.body()?.items?.isNotEmpty() == true) {
                val subId = subResp.body()!!.items.first().id
                val patchResp = api.updateHomeworkSubmission(
                    id = subId,
                    body = mapOf(
                        "status" to "COMPLETED",
                        "submittedAt" to nowStr
                    )
                )
                if (patchResp.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to mark homework complete: ${patchResp.code()}"))
                }
            } else {
                // If no submission record exists for this specific student, create one
                val createResp = api.createHomeworkSubmission(
                    body = mapOf(
                        "homework" to id,
                        "student" to (studentId ?: ""),
                        "status" to "COMPLETED",
                        "submittedAt" to nowStr
                    )
                )
                if (createResp.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to create completion submission: ${createResp.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapRecordToHomework(rec: PbHomeworkRecord): Homework {
        val expand = rec.expand
        val subjectMap = expand?.get("subject") as? Map<*, *>
        val teacherMap = expand?.get("teacher") as? Map<*, *>

        val subjectName = (subjectMap?.get("name") as? String) ?: "General"
        val teacherName = (teacherMap?.get("name") as? String) ?: "Teacher"

        val assignedMillis = System.currentTimeMillis()
        val dueMillis = parseDate(rec.dueDate)

        val status = when (rec.status?.uppercase()) {
            "COMPLETED" -> HomeworkStatus.COMPLETED
            "OVERDUE" -> HomeworkStatus.OVERDUE
            else -> if (dueMillis < System.currentTimeMillis() && dueMillis > 0) HomeworkStatus.OVERDUE else HomeworkStatus.PENDING
        }

        val attachments = rec.files?.mapIndexed { index, fileName ->
            val ext = fileName.substringAfterLast('.', "").lowercase()
            val rType = when (ext) {
                "pdf" -> com.example.mytuition.core.domain.model.ResourceType.PDF
                "png", "jpg", "jpeg", "webp" -> com.example.mytuition.core.domain.model.ResourceType.IMAGE
                "mp4", "mkv" -> com.example.mytuition.core.domain.model.ResourceType.VIDEO
                else -> com.example.mytuition.core.domain.model.ResourceType.DOCUMENT
            }
            com.example.mytuition.core.domain.model.Resource(
                id = "${rec.id}_att_$index",
                title = fileName,
                type = rType,
                url = "${com.example.mytuition.core.data.network.PocketBaseClient.DEFAULT_BASE_URL}files/homework/${rec.id}/$fileName",
                sizeBytes = 0L
            )
        } ?: emptyList()

        return Homework(
            id = rec.id,
            subjectName = subjectName,
            title = rec.title ?: "Untitled Homework",
            description = rec.description ?: "",
            assignedAt = assignedMillis,
            dueAt = dueMillis,
            status = status,
            teacherName = teacherName,
            attachments = attachments
        )
    }

    private fun parseDate(dateStr: String?): Long {
        if (dateStr.isNullOrBlank()) return 0L
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }
}
