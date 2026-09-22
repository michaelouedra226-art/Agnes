package com.example.service

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

/**
 * Service gérant l'authentification Google avec Firebase Auth et Android Credential Manager.
 * Protection intégrale contre les crashes liés à Firebase non initialisé ou contextes d'émulateur restreints.
 */
class GoogleAuthManager(private val context: Context) {
    private val auth: FirebaseAuth? = try {
        // Vérifie si FirebaseApp est initialisé avant d'appeler FirebaseAuth.getInstance()
        if (FirebaseApp.getApps(context).isNotEmpty()) {
            FirebaseAuth.getInstance()
        } else {
            FirebaseApp.initializeApp(context)
            FirebaseAuth.getInstance()
        }
    } catch (t: Throwable) {
        Log.e("GoogleAuthManager", "Firebase Auth non disponible", t)
        null
    }

    private val credentialManager: CredentialManager? = try {
        CredentialManager.create(context)
    } catch (t: Throwable) {
        Log.e("GoogleAuthManager", "CredentialManager non disponible", t)
        null
    }

    private val serverClientId = "251704935538-cv7l8623phd0benkni0fhvivougmcg1m.apps.googleusercontent.com"

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        try {
            auth?.addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
            }
        } catch (t: Throwable) {
            Log.e("GoogleAuthManager", "Erreur lors de l'enregistrement de l'AuthStateListener", t)
        }
    }

    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> {
        val fbAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth n'est pas initialisé."))
        val cm = credentialManager ?: return Result.failure(IllegalStateException("Credential Manager n'est pas supporté sur cet appareil."))

        return try {
            val rawNonce = UUID.randomUUID().toString()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(rawNonce.toByteArray())
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = cm.getCredential(
                request = request,
                context = activityContext
            )

            val credential = result.credential
            val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            val authResult = fbAuth.signInWithCredential(authCredential).await()
            val user = authResult.user ?: throw IllegalStateException("Utilisateur Firebase introuvable après authentification.")
            Result.success(user)
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Connexion annulée par l'utilisateur."))
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Erreur Credential Manager : ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
            _currentUser.value = null
        } catch (t: Throwable) {
            Log.e("GoogleAuthManager", "Erreur lors de la déconnexion", t)
        }
    }
}
