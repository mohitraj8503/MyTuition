package com.example.mytuition.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("pocketbase_token")
        private val USER_ID_KEY = stringPreferencesKey("pocketbase_user_id")
        private val USER_ROLE_KEY = stringPreferencesKey("pocketbase_user_role")
        private val USER_NAME_KEY = stringPreferencesKey("pocketbase_user_name")
        private val USERNAME_KEY = stringPreferencesKey("pocketbase_username")
        private val INSTITUTE_ID_KEY = stringPreferencesKey("pocketbase_institute_id")
        private val ONBOARDING_COMPLETED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("onboarding_completed")
        private val NOTIFICATIONS_ENABLED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("notifications_enabled")
    }

    suspend fun saveSession(
        token: String,
        userId: String,
        role: String,
        name: String,
        username: String,
        instituteId: String = "inst_456"
    ) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[USER_ROLE_KEY] = role
            prefs[USER_NAME_KEY] = name
            prefs[USERNAME_KEY] = username
            prefs[INSTITUTE_ID_KEY] = instituteId
        }
    }

    fun getToken(): Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    suspend fun getTokenSync(): String? = getToken().first()

    fun getUserId(): Flow<String?> = dataStore.data.map { it[USER_ID_KEY] }
    suspend fun getUserIdSync(): String? = getUserId().first()

    fun getUserRole(): Flow<String?> = dataStore.data.map { it[USER_ROLE_KEY] }
    suspend fun getUserRoleSync(): String? = getUserRole().first()

    fun getUserName(): Flow<String?> = dataStore.data.map { it[USER_NAME_KEY] }
    fun getUsername(): Flow<String?> = dataStore.data.map { it[USERNAME_KEY] }
    fun getInstituteId(): Flow<String?> = dataStore.data.map { it[INSTITUTE_ID_KEY] }

    fun isOnboardingCompleted(): Flow<Boolean> = dataStore.data.map { it[ONBOARDING_COMPLETED_KEY] ?: false }
    suspend fun isOnboardingCompletedSync(): Boolean = isOnboardingCompleted().first()
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETED_KEY] = completed }
    }

    fun getNotificationsEnabled(): Flow<Boolean> = dataStore.data.map { it[NOTIFICATIONS_ENABLED_KEY] ?: true }
    suspend fun getNotificationsEnabledSync(): Boolean = getNotificationsEnabled().first()
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[NOTIFICATIONS_ENABLED_KEY] = enabled }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
            prefs.remove(USER_ROLE_KEY)
            prefs.remove(USER_NAME_KEY)
            prefs.remove(USERNAME_KEY)
            prefs.remove(INSTITUTE_ID_KEY)
        }
    }
}
