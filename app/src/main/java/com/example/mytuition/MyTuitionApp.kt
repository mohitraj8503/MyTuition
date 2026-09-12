package com.example.mytuition

import android.app.Application
import com.example.mytuition.core.data.FirebaseConfig
import com.example.mytuition.core.security.AppGuard
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyTuitionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Anti-tamper check — must be FIRST
        AppGuard.init(this)
        FirebaseConfig.ensureInitialized(this)
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
            FirebaseConfig.db.firestoreSettings = settings
        } catch (_: Exception) {
            // Settings already applied or cached
        }
    }
}

