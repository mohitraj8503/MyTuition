package com.example.mytuition.core.data.repository

import android.net.Uri

import com.example.mytuition.core.data.local.AppDatabase
import com.example.mytuition.core.data.local.FeatureCacheEntity
import com.example.mytuition.core.data.local.TokenManager
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.data.network.PocketBaseClient
import com.example.mytuition.core.domain.model.Resource
import com.example.mytuition.core.domain.model.ResourceType
import com.example.mytuition.core.domain.repository.LibraryRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first

class PocketBaseLibraryRepository(
    private val api: PocketBaseApi,
    private val tokenManager: TokenManager,
    private val database: AppDatabase? = null
) : LibraryRepository {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(
        List::class.java,
        Resource::class.java
    )

    private val adapter = moshi.adapter<List<Resource>>(listType)

    override suspend fun getResources(): Result<List<Resource>> {
        val requestCacheKey = getCacheKey()

        return try {
            val records = mutableListOf<com.example.mytuition.core.data.network.PbResourceRecord>()
            var page = 1
            var totalPages = 1

            while (page <= totalPages) {
                val response = api.getResources(
                    sort = "-created",
                    page = page,
                    perPage = 50
                )

                if (!response.isSuccessful) {
                    return getCachedResources(requestCacheKey)
                        ?: Result.failure(
                            Exception(
                                "Failed to fetch resources on page $page: HTTP ${response.code()} ${response.message()}"
                            )
                        )
                }

                val pageBody = response.body()
                    ?: return getCachedResources(requestCacheKey)
                        ?: Result.failure(
                            Exception("The resources response was empty on page $page.")
                        )

                if (
                    pageBody.totalPages == 0 &&
                    pageBody.totalItems == 0 &&
                    pageBody.items.isEmpty() &&
                    page == 1
                ) {
                    totalPages = 0
                    break
                }

                if (pageBody.totalPages < page || pageBody.totalPages < 0) {
                    return getCachedResources(requestCacheKey)
                        ?: Result.failure(Exception("Invalid pagination metadata on page $page."))
                }

                records.addAll(pageBody.items)
                totalPages = pageBody.totalPages
                page++
            }

            val resources = records.map { record ->
                val resourceType = when (record.type?.uppercase()) {
                    "PDF" -> ResourceType.PDF
                    "IMAGE" -> ResourceType.IMAGE
                    "VIDEO" -> ResourceType.VIDEO
                    "LINK" -> ResourceType.LINK
                    "DOCUMENT" -> ResourceType.DOCUMENT
                    else -> ResourceType.OTHER
                }

                val fileUrl = record.file
                    ?.takeIf { it.isNotBlank() }
                    ?.let {
                        "${PocketBaseClient.DEFAULT_BASE_URL}files/resources/${record.id}/${Uri.encode(it)}"
                    }

                Resource(
                    id = record.id,
                    title = record.title
                        ?.takeIf { it.isNotBlank() }
                        ?: "Untitled resource",
                    type = resourceType,
                    url = fileUrl,
                    sizeBytes = record.size?.takeIf { it > 0L },
                    subject = (record.expand?.get("subject") as? Map<*, *>)?.get("name") as? String,
                    batch = (record.expand?.get("batch") as? Map<*, *>)?.get("name") as? String
                )
            }

            // Cache only when a valid account-specific key is available.
            if (requestCacheKey != null && getCacheKey() == requestCacheKey) {
                try {
                    val json = adapter.toJson(resources)
                    database?.featureCacheDao()?.saveCache(
                        FeatureCacheEntity(
                            key = requestCacheKey,
                            dataJson = json
                        )
                    )
                } catch (exception: kotlinx.coroutines.CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    // A cache failure must not discard valid network data.
                }
            }

            Result.success(resources)
        } catch (exception: kotlinx.coroutines.CancellationException) {
            throw exception
        } catch (exception: Exception) {
            getCachedResources(requestCacheKey) ?: Result.failure(exception)
        }
    }
    private suspend fun getCacheKey(): String? {
        return try {
            val userId = tokenManager.getUserIdSync()
                ?.takeIf { it.isNotBlank() }
                ?: return null

            val instituteId = tokenManager.getInstituteId()
                .first()
                ?.takeIf { it.isNotBlank() }
                ?: return null

            "library_resources_${instituteId}_$userId"
        } catch (exception: kotlinx.coroutines.CancellationException) {
            throw exception
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getCachedResources(
        cacheKey: String?
    ): Result<List<Resource>>? {
        if (cacheKey == null || getCacheKey() != cacheKey) return null

        return try {
            val cached = database
                ?.featureCacheDao()
                ?.getCache(cacheKey)

            if (cached == null || cached.dataJson.isBlank()) {
                null
            } else {
                val resources = adapter.fromJson(cached.dataJson)
                    ?: return null

                Result.success(
                    resources.map { it.copy(isFromCache = true) }
                )
            }
        } catch (exception: kotlinx.coroutines.CancellationException) {
            throw exception
        } catch (_: Exception) {
            null
        }
    }
}
