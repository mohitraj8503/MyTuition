package com.example.mytuition.core.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

object FirebaseConfig {
    
    fun ensureInitialized(context: Context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:109876543210:android:a1b2c3d4e5f67890")
                .setProjectId("mytuition-app-live")
                .setApiKey("AIzaSyDummyDevKeyForMyTuitionOfflineApp123")
                .setStorageBucket("mytuition-app-live.firebasestorage.app")
                .build()
            FirebaseApp.initializeApp(context, options)
        }
    }

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    val functions: FirebaseFunctions by lazy { FirebaseFunctions.getInstance("asia-south1") }
    val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }
}
