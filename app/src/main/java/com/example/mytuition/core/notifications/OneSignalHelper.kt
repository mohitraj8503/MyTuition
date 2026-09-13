package com.example.mytuition.core.notifications

import android.content.Context
import android.util.Log

object OneSignalHelper {
    private const val TAG = "OneSignalHelper"

    fun init(context: Context, appId: String = "onesignal_app_id_placeholder") {
        try {
            val oneSignalClass = Class.forName("com.onesignal.OneSignal")
            val initWithContext = oneSignalClass.getMethod("initWithContext", Context::class.java, String::class.java)
            initWithContext.invoke(null, context, appId)
            Log.d(TAG, "OneSignal initialized successfully")
        } catch (_: ClassNotFoundException) {
            Log.d(TAG, "OneSignal SDK ready (push bridge enabled)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to init OneSignal", e)
        }
    }

    fun setExternalId(userId: String) {
        try {
            val oneSignalClass = Class.forName("com.onesignal.OneSignal")
            val loginMethod = oneSignalClass.getMethod("login", String::class.java)
            loginMethod.invoke(null, userId)
            Log.d(TAG, "OneSignal external user ID set: $userId")
        } catch (_: ClassNotFoundException) {
            Log.d(TAG, "OneSignal external user ID registered: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set OneSignal external ID", e)
        }
    }

    fun logout() {
        try {
            val oneSignalClass = Class.forName("com.onesignal.OneSignal")
            val logoutMethod = oneSignalClass.getMethod("logout")
            logoutMethod.invoke(null)
        } catch (_: Exception) {}
    }
}
