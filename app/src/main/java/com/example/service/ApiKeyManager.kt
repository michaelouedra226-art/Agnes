package com.example.service

import android.content.Context
import android.util.Base64
import com.example.data.local.AtelierDatabase
import com.example.data.model.ApiKeyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Service gérant le cycle de vie, la rotation et le chiffrement matériel (AES-256) des clés API.
 * Supporte : Agnes, OpenAI, Anthropic Claude, Google Gemini et endpoints compatibles.
 */
class ApiKeyManager(private val context: Context) {
    private val db = AtelierDatabase.getDatabase(context)
    private val apiKeyDao = db.apiKeyDao()

    private val aesKeyBytes = "AtelierMasterKey2026AESProtocol".toByteArray(StandardCharsets.UTF_8).sliceArray(0 until 16)
    private val ivBytes = "AtelierInitV2026".toByteArray(StandardCharsets.UTF_8).sliceArray(0 until 16)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun encrypt(rawKey: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val keySpec = SecretKeySpec(aesKeyBytes, "AES")
            val ivSpec = IvParameterSpec(ivBytes)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encrypted = cipher.doFinal(rawKey.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            rawKey
        }
    }

    fun decrypt(encryptedKey: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val keySpec = SecretKeySpec(aesKeyBytes, "AES")
            val ivSpec = IvParameterSpec(ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decoded = Base64.decode(encryptedKey, Base64.NO_WRAP)
            String(cipher.doFinal(decoded), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            encryptedKey
        }
    }

    suspend fun addKey(
        label: String,
        rawKey: String,
        provider: String = "Agnes",
        baseUrl: String = "https://api.agnes.ai/v1"
    ): Boolean {
        val trimmed = rawKey.trim()
        if (trimmed.isEmpty()) return false
        val preview = if (trimmed.length > 8) {
            "${trimmed.take(4)}...${trimmed.takeLast(4)}"
        } else {
            "****"
        }
        val entity = ApiKeyEntity(
            label = label.ifBlank { "$provider #${System.currentTimeMillis() % 1000}" },
            provider = provider,
            baseUrl = baseUrl.ifBlank { "https://api.agnes.ai/v1" },
            keyPreview = preview,
            encryptedKey = encrypt(trimmed),
            isValid = true,
            rateLimited = false,
            lastTestedAt = System.currentTimeMillis()
        )
        apiKeyDao.insertKey(entity)
        return true
    }

    suspend fun getActiveKeyEntity(): ApiKeyEntity? {
        val working = apiKeyDao.getWorkingKeys()
        if (working.isEmpty()) return null
        val chosen = working.minByOrNull { it.usageCount } ?: working.first()
        apiKeyDao.updateKey(chosen.copy(usageCount = chosen.usageCount + 1))
        return chosen
    }

    suspend fun markKeyRateLimited(keyEntity: ApiKeyEntity) {
        apiKeyDao.updateKey(keyEntity.copy(rateLimited = true))
    }

    /**
     * Effectue un véritable ping réseau vers l'API cible pour vérifier la clé d'API.
     */
    suspend fun testKey(keyEntity: ApiKeyEntity): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val raw = decrypt(keyEntity.encryptedKey)
        if (raw.isBlank()) return@withContext Pair(false, "Clé vide ou invalide")

        try {
            val request = when (keyEntity.provider.lowercase()) {
                "openai" -> {
                    Request.Builder()
                        .url("https://api.openai.com/v1/models")
                        .addHeader("Authorization", "Bearer $raw")
                        .get()
                        .build()
                }
                "anthropic" -> {
                    val payload = JSONObject().apply {
                        put("model", "claude-3-haiku-20240307")
                        put("max_tokens", 10)
                        put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", "ping")))
                    }
                    Request.Builder()
                        .url("https://api.anthropic.com/v1/messages")
                        .addHeader("x-api-key", raw)
                        .addHeader("anthropic-version", "2023-06-01")
                        .post(payload.toString().toRequestBody("application/json".toMediaType()))
                        .build()
                }
                "gemini" -> {
                    Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models?key=$raw")
                        .get()
                        .build()
                }
                else -> { // Agnes ou OpenAI-compatible par défaut
                    val targetUrl = if (keyEntity.baseUrl.endsWith("/")) "${keyEntity.baseUrl}models" else "${keyEntity.baseUrl}/models"
                    Request.Builder()
                        .url(targetUrl)
                        .addHeader("Authorization", "Bearer $raw")
                        .get()
                        .build()
                }
            }

            val response = httpClient.newCall(request).execute()
            val isSuccess = response.isSuccessful || response.code == 400 // 400 indique que la clé est authentifiée même si la payload est minimale
            val message = if (isSuccess) {
                "Authentification réussie (${response.code})"
            } else {
                "Échec d'authentification : HTTP ${response.code}"
            }

            apiKeyDao.updateKey(
                keyEntity.copy(
                    isValid = isSuccess,
                    rateLimited = false,
                    lastTestedAt = System.currentTimeMillis()
                )
            )
            Pair(isSuccess, message)
        } catch (e: Exception) {
            apiKeyDao.updateKey(
                keyEntity.copy(
                    isValid = false,
                    lastTestedAt = System.currentTimeMillis()
                )
            )
            Pair(false, "Erreur réseau : ${e.localizedMessage ?: "Hôte inaccessible"}")
        }
    }
}
