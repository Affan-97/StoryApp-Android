package com.affan.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.affan.storyapp.api.ApiConfig
import com.affan.storyapp.data.StoryRepository
import com.affan.storyapp.preferences.LoginPreference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session")
object Injection {

    fun provideRepository(context: Context): StoryRepository {
        val preferences = LoginPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(preferences, apiService)
    }
}