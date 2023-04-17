package com.affan.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.affan.storyapp.preferences.LoginPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LoginPreference) : ViewModel() {
    fun getLoginSession(): LiveData<String?> {
        return pref.getLoginSession().asLiveData()
    }

    fun saveSession(tokenKey: String) {
        viewModelScope.launch {
            pref.saveSession(tokenKey)
        }
    }
    fun deleteSession() {
        viewModelScope.launch {
            pref.deleteSession()
        }
    }
}