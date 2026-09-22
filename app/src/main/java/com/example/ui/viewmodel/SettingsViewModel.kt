package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AtelierDatabase
import com.example.data.model.ApiKeyEntity
import com.example.service.ApiKeyManager
import com.example.service.GoogleAuthManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AtelierDatabase.getDatabase(application)
    private val apiKeyDao = db.apiKeyDao()
    private val keyManager = ApiKeyManager(application)
    
    private val authManager: GoogleAuthManager? = try {
        GoogleAuthManager(application)
    } catch (e: Throwable) {
        Log.e("SettingsViewModel", "Failed to initialize GoogleAuthManager", e)
        null
    }

    val keys: Flow<List<ApiKeyEntity>> = apiKeyDao.getAllKeys()
        .catch { e ->
            Log.e("SettingsViewModel", "Error collecting keys", e)
            emit(emptyList())
        }

    val currentUser: StateFlow<FirebaseUser?> = authManager?.currentUser
        ?: MutableStateFlow(null)

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _testStatus = MutableStateFlow<String?>(null)
    val testStatus: StateFlow<String?> = _testStatus.asStateFlow()

    fun signInWithGoogle(activityContext: Context, onResult: (Boolean, String?) -> Unit) {
        val manager = authManager
        if (manager == null) {
            onResult(false, "Module Google Sign-In non disponible sur cet environnement.")
            return
        }

        viewModelScope.launch {
            _authLoading.value = true
            try {
                val result = manager.signInWithGoogle(activityContext)
                _authLoading.value = false
                result.fold(
                    onSuccess = { user -> onResult(true, null) },
                    onFailure = { error -> onResult(false, error.localizedMessage ?: "Échec d'authentification") }
                )
            } catch (t: Throwable) {
                _authLoading.value = false
                onResult(false, t.localizedMessage ?: "Erreur inattendue")
            }
        }
    }

    fun signOut() {
        try {
            authManager?.signOut()
        } catch (e: Throwable) {
            Log.e("SettingsViewModel", "Sign out failed", e)
        }
    }

    fun addKey(label: String, key: String, provider: String, baseUrl: String) {
        viewModelScope.launch {
            try {
                keyManager.addKey(label, key, provider, baseUrl)
            } catch (e: Throwable) {
                Log.e("SettingsViewModel", "Add key failed", e)
            }
        }
    }

    fun testKey(key: ApiKeyEntity) {
        viewModelScope.launch {
            try {
                val (valid, message) = keyManager.testKey(key)
                _testStatus.value = message
            } catch (e: Throwable) {
                _testStatus.value = "Erreur: ${e.localizedMessage}"
            }
        }
    }

    fun deleteKey(key: ApiKeyEntity) {
        viewModelScope.launch {
            try {
                apiKeyDao.deleteKey(key)
            } catch (e: Throwable) {
                Log.e("SettingsViewModel", "Delete key failed", e)
            }
        }
    }
}
