package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AtelierDatabase
import com.example.data.model.ApiKeyEntity
import com.example.service.ApiKeyManager
import com.example.service.GoogleAuthManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AtelierDatabase.getDatabase(application)
    private val apiKeyDao = db.apiKeyDao()
    private val keyManager = ApiKeyManager(application)
    private val authManager = GoogleAuthManager(application)

    val keys = apiKeyDao.getAllKeys()
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _testStatus = MutableStateFlow<String?>(null)
    val testStatus: StateFlow<String?> = _testStatus.asStateFlow()

    fun signInWithGoogle(activityContext: Context, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _authLoading.value = true
            val result = authManager.signInWithGoogle(activityContext)
            _authLoading.value = false
            result.fold(
                onSuccess = { user -> onResult(true, null) },
                onFailure = { error -> onResult(false, error.localizedMessage) }
            )
        }
    }

    fun signOut() {
        authManager.signOut()
    }

    fun addKey(label: String, key: String, provider: String, baseUrl: String) {
        viewModelScope.launch {
            keyManager.addKey(label, key, provider, baseUrl)
        }
    }

    fun testKey(key: ApiKeyEntity) {
        viewModelScope.launch {
            val (valid, message) = keyManager.testKey(key)
            _testStatus.value = message
        }
    }

    fun deleteKey(key: ApiKeyEntity) {
        viewModelScope.launch {
            apiKeyDao.deleteKey(key)
        }
    }
}
