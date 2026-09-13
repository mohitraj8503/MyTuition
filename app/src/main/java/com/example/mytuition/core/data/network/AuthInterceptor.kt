package com.example.mytuition.core.data.network

import com.example.mytuition.core.data.local.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

object SessionEvents {
    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun emitSessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            try {
                tokenManager.getTokenSync()
            } catch (_: Exception) {
                null
            }
        }
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        val response = chain.proceed(requestBuilder.build())

        // If 401 Unauthorized occurs on an authenticated request (not login endpoint)
        if (response.code == 401 && !originalRequest.url.encodedPath.contains("auth-with-password")) {
            runBlocking {
                try {
                    tokenManager.clearSession()
                } catch (_: Exception) {}
            }
            SessionEvents.emitSessionExpired()
        }

        return response
    }
}
