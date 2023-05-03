package com.affan.storyapp.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.affan.storyapp.data.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreference private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN = stringPreferencesKey("session_token")
    private val NAME = stringPreferencesKey("session_name")
    private val ID = stringPreferencesKey("session_id")

    fun getLoginSession(): Flow<UserModel?> {
        return dataStore.data.map { preferences ->
            UserModel(
            preferences[NAME]?:null,
            preferences[TOKEN]?:null,
            preferences[ID]?:null,

            )
        }
    }

    suspend fun saveSession(userModel: UserModel) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = userModel.token as String
            preferences[NAME] = userModel.name as String
            preferences[ID] = userModel.id as String
        }
    }

    suspend fun deleteSession() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
            preferences.remove(NAME)
            preferences.remove(ID)
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