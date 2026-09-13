package com.example.mytuition

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.security.AppGuard

class MyTuitionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Anti-tamper check — must be FIRST
        AppGuard.init(this)

        // Initialize DI AppContainer with PocketBase & Local Cache
        AppContainer.init(this)

        // Initialize OneSignal push notification system
        com.example.mytuition.core.notifications.OneSignalHelper.init(this)

        // Global High-Performance Coil ImageLoader
        try {
            val imageLoader = ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder(this)
                        .maxSizePercent(0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.resolve("image_cache"))
                        .maxSizeBytes(50L * 1024 * 1024)
                        .build()
                }
                .crossfade(200)
                .build()
            Coil.setImageLoader(imageLoader)
        } catch (_: Exception) {
        }
    }
}
