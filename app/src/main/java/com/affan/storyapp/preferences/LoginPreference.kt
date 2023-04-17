package com.affan.storyapp.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreference private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN = stringPreferencesKey("session_token")

    fun getLoginSession(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN]
        }
    }

    suspend fun saveSession(tokenKey: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = tokenKey
        }
    }
    suspend fun deleteSession() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: LoginPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}