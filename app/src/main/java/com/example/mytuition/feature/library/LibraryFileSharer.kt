package com.example.mytuition.feature.library

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import android.webkit.MimeTypeMap
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.data.network.PocketBaseClient
import com.example.mytuition.core.domain.model.Resource
import com.example.mytuition.core.domain.model.ResourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URI

object LibraryFileSharer {

    suspend fun open(
        context: Context,
        api: PocketBaseApi,
        resource: Resource
    ) {
        val appContext = context.applicationContext
        val downloadedFile = downloadResource(appContext, api, resource)
        val contentUri = getContentUri(appContext, downloadedFile)
        val mimeType = getMimeType(downloadedFile)

        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            appContext.startActivity(openIntent)
        } catch (_: ActivityNotFoundException) {
            throw IOException("No installed app can open this file type.")
        }
    }

    suspend fun share(
        context: Context,
        api: PocketBaseApi,
        resource: Resource
    ) {
        val appContext = context.applicationContext
        val downloadedFile = downloadResource(appContext, api, resource)
        val contentUri = getContentUri(appContext, downloadedFile)
        val mimeType = getMimeType(downloadedFile)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            clipData = ClipData.newUri(
                appContext.contentResolver,
                downloadedFile.name,
                contentUri
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        appContext.startActivity(
            Intent.createChooser(shareIntent, "Share resource")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private suspend fun downloadResource(
        context: Context,
        api: PocketBaseApi,
        resource: Resource
    ): File {
        val fileUrl = resource.url
            ?.takeIf { it.isNotBlank() }
            ?: throw IOException("This resource does not have a file URL.")

        ensureTrustedPocketBaseUrl(fileUrl)

        return withContext(Dispatchers.IO) {
            val directory = File(context.cacheDir, "shared_resources")

            if (!directory.exists() && !directory.mkdirs()) {
                throw IOException("Could not create the resource cache directory.")
            }

            cleanupOldFiles(directory)

            val extension = getExtension(fileUrl, resource.type)
            val safeTitle = resource.title
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
                .trim('_', '.')
                .take(60)
                .ifBlank { "resource" }

            val destination = File(
                directory,
                "${safeTitle}_${resource.id.take(12)}.$extension"
            )

            try {
                val response = api.downloadResource(fileUrl)

                if (!response.isSuccessful) {
                    response.errorBody()?.close()
                    throw IOException(
                        "Download failed: HTTP ${response.code()} ${response.message()}"
                    )
                }

                val body = response.body()
                    ?: throw IOException("The server returned an empty file.")

                body.byteStream().use { input ->
                    destination.outputStream().buffered().use { output ->
                        input.copyTo(output)
                    }
                }

                if (!destination.exists() || destination.length() == 0L) {
                    throw IOException("The downloaded file is empty.")
                }

                destination
            } catch (exception: Exception) {
                destination.delete()
                throw exception
            }
        }
    }

    private fun getContentUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    private fun getMimeType(file: File): String {
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(file.extension.lowercase())
            ?: "application/octet-stream"
    }

    private fun cleanupOldFiles(directory: File) {
        val expiryTime = System.currentTimeMillis() - 24L * 60L * 60L * 1000L

        directory.listFiles()
            ?.filter { it.isFile && it.lastModified() < expiryTime }
            ?.forEach { it.delete() }
    }

    private fun ensureTrustedPocketBaseUrl(fileUrl: String) {
        val requestedUri = try {
            URI(fileUrl)
        } catch (_: Exception) {
            throw IOException("The resource URL is invalid.")
        }

        val trustedUri = URI(PocketBaseClient.DEFAULT_BASE_URL)

        fun effectivePort(uri: URI): Int {
            if (uri.port != -1) return uri.port

            return when (uri.scheme?.lowercase()) {
                "https" -> 443
                "http" -> 80
                else -> -1
            }
        }

        val isTrusted =
            requestedUri.userInfo == null &&
            requestedUri.scheme?.equals(trustedUri.scheme, ignoreCase = true) == true &&
            requestedUri.host?.equals(trustedUri.host, ignoreCase = true) == true &&
            effectivePort(requestedUri) == effectivePort(trustedUri)

        if (!isTrusted) {
            throw IOException("Sharing is blocked for an untrusted resource URL.")
        }
    }

    private fun getExtension(
        fileUrl: String,
        type: ResourceType
    ): String {
        val path = Uri.parse(fileUrl).lastPathSegment.orEmpty()
        val extension = path.substringAfterLast('.', "")
            .lowercase()
            .takeIf { it.matches(Regex("[a-z0-9]{1,8}")) }

        return extension ?: when (type) {
            ResourceType.PDF -> "pdf"
            ResourceType.IMAGE -> "jpg"
            ResourceType.VIDEO -> "mp4"
            ResourceType.DOCUMENT -> "doc"
            else -> "bin"
        }
    }
}
