package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.local.AppDatabase
import com.example.mytuition.core.data.local.FeatureCacheEntity
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.domain.model.*
import com.example.mytuition.core.domain.repository.SubjectRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class PocketBaseSubjectRepository(
    private val api: PocketBaseApi,
    private val database: AppDatabase? = null
) : SubjectRepository {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Subject::class.java)
    private val adapter = moshi.adapter<List<Subject>>(listType)

    override suspend fun getSubjects(): Result<List<Subject>> {
        return try {
            val batchesResp = api.getBatches(expand = "subject,teacher")
            if (batchesResp.isSuccessful && batchesResp.body() != null) {
                val batches = batchesResp.body()!!.items
                val hwResp = api.getHomeworkList()
                val activeHw = hwResp.body()?.items ?: emptyList()
                val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

                val subjectList = batches.mapNotNull { batch ->
                    val expand = batch.expand
                    val subjMap = expand?.get("subject") as? Map<*, *>
                    val teacherMap = expand?.get("teacher") as? Map<*, *>
                    val subjName = (subjMap?.get("name") as? String) ?: batch.name ?: return@mapNotNull null
                    val teacherName = (teacherMap?.get("name") as? String) ?: "Teacher"
                    val subjId = (subjMap?.get("id") as? String) ?: batch.id

                    val pendingCount = activeHw.count { it.batch == batch.id && it.status != "COMPLETED" }

                    // Real next class session query for this batch
                    var nextClassTimestamp: Long? = null
                    try {
                        val sessionResp = api.getClassSessions(
                            filter = "batch = '${batch.id}' && date >= '$todayStr'",
                            sort = "date,startTime",
                            perPage = 1
                        )
                        val nextSession = sessionResp.body()?.items?.firstOrNull()
                        if (nextSession != null && !nextSession.date.isNullOrBlank()) {
                            val timePart = nextSession.startTime?.trim() ?: "00:00"
                            val dateTimeStr = "${nextSession.date} ${if (timePart.length == 5) "$timePart:00" else timePart}"
                            val parsed = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).parse(dateTimeStr)
                                ?: java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(nextSession.date)
                            nextClassTimestamp = parsed?.time
                        }
                    } catch (_: Exception) {}

                    Subject(
                        id = subjId,
                        name = subjName,
                        teacherName = teacherName,
                        teacherAvatarUrl = null,
                        homeworkCount = pendingCount,
                        resourceCount = 0,
                        nextClass = nextClassTimestamp
                    )
                }
                // Save to Room cache
                try {
                    val json = adapter.toJson(subjectList)
                    database?.featureCacheDao()?.saveCache(
                        FeatureCacheEntity(key = "subjects_list", dataJson = json)
                    )
                } catch (_: Exception) {}
                Result.success(subjectList)
            } else {
                // If batches empty, fallback to subjects collection directly
                val subjResp = api.getSubjects()
                if (subjResp.isSuccessful && subjResp.body() != null) {
                    val list = subjResp.body()!!.items.map { s ->
                        Subject(
                            id = s.id,
                            name = s.name ?: "Subject",
                            teacherName = "Teacher",
                            teacherAvatarUrl = null,
                            homeworkCount = 0,
                            resourceCount = 0,
                            nextClass = null
                        )
                    }
                    Result.success(list)
                } else {
                    getCachedSubjects()
                        ?: Result.failure(Exception("Failed to fetch subjects: ${batchesResp.code()}"))
                }
            }
        } catch (e: Exception) {
            getCachedSubjects() ?: Result.failure(e)
        }
    }

    private suspend fun getCachedSubjects(): Result<List<Subject>>? {
        return try {
            val cached = database?.featureCacheDao()?.getCache("subjects_list")
            if (cached != null && cached.dataJson.isNotBlank()) {
                val parsed = adapter.fromJson(cached.dataJson)
                if (parsed != null) Result.success(parsed.map { it.copy(isFromCache = true) }) else null
            } else null
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getSubjectDetail(id: String): Result<SubjectDetail> {
        return try {
            val subjsResult = getSubjects()
            val list = subjsResult.getOrNull() ?: emptyList()
            val found = list.find { it.id == id } ?: list.firstOrNull()
                ?: return Result.failure(Exception("Subject not found: $id"))

            val hwResp = api.getHomeworkList(filter = "subject = '$id' || batch.subject = '$id'")
            val hwList = hwResp.body()?.items?.map { rec ->
                val attachments = rec.files?.mapIndexed { index, fileName ->
                    val ext = fileName.substringAfterLast('.', "").lowercase()
                    val rType = when (ext) {
                        "pdf" -> ResourceType.PDF
                        "png", "jpg", "jpeg", "webp" -> ResourceType.IMAGE
                        "mp4", "mkv" -> ResourceType.VIDEO
                        else -> ResourceType.DOCUMENT
                    }
                    Resource(
                        id = "${rec.id}_att_$index",
                        title = fileName,
                        type = rType,
                        url = "${com.example.mytuition.core.data.network.PocketBaseClient.DEFAULT_BASE_URL}files/homework/${rec.id}/$fileName",
                        sizeBytes = 0L
                    )
                } ?: emptyList()

                val dueTimestamp = if (!rec.dueDate.isNullOrBlank()) {
                    try {
                        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(rec.dueDate)?.time ?: 0L
                    } catch (_: Exception) {
                        0L
                    }
                } else {
                    0L
                }

                Homework(
                    id = rec.id,
                    subjectName = found.name,
                    title = rec.title ?: "",
                    description = rec.description ?: "",
                    assignedAt = System.currentTimeMillis(),
                    dueAt = dueTimestamp,
                    status = HomeworkStatus.PENDING,
                    teacherName = found.teacherName,
                    attachments = attachments
                )
            } ?: emptyList()

            val resResp = api.getResources(filter = "subject = '$id' || batch.subject = '$id'")
            val resourcesList = resResp.body()?.items?.map { rec ->
                val resourceType = when (rec.type?.uppercase()) {
                    "PDF" -> ResourceType.PDF
                    "IMAGE" -> ResourceType.IMAGE
                    "VIDEO" -> ResourceType.VIDEO
                    "LINK" -> ResourceType.LINK
                    else -> ResourceType.DOCUMENT
                }
                val fileUrl = if (!rec.file.isNullOrBlank()) {
                    "${com.example.mytuition.core.data.network.PocketBaseClient.DEFAULT_BASE_URL}files/resources/${rec.id}/${rec.file}"
                } else null

                Resource(
                    id = rec.id,
                    title = rec.title ?: "Resource Document",
                    type = resourceType,
                    url = fileUrl,
                    sizeBytes = rec.size ?: 0L
                )
            } ?: emptyList()

            Result.success(
                SubjectDetail(
                    subject = found,
                    recentHomework = hwList,
                    recentResources = resourcesList
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
