package com.example.service

import com.example.data.model.ApiKeyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Client officiel pour les interactions de production avec les APIs d'IA (Agnes, OpenAI, Anthropic, Gemini).
 * Aucun texte simulé arbitraire : effectue de vrais appels HTTP SSE en streaming et parse les tokens.
 */
class AgnesApiClient(
    private val keyManager: ApiKeyManager
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Exécute un appel streaming réel vers le fournisseur configuré (Agnes / OpenAI / Gemini / Anthropic).
     */
    fun streamChat(prompt: String): Flow<String> = flow {
        val keyEntity: ApiKeyEntity? = keyManager.getActiveKeyEntity()

        if (keyEntity == null) {
            emit("Aucune clé API configurée. Veuillez ajouter votre clé d'API (Agnes, OpenAI ou Gemini) dans les Paramètres.")
            return@flow
        }

        val rawKey = keyManager.decrypt(keyEntity.encryptedKey)
        val provider = keyEntity.provider.lowercase()

        try {
            when (provider) {
                "anthropic" -> {
                    val payload = JSONObject().apply {
                        put("model", "claude-3-5-sonnet-20240620")
                        put("max_tokens", 2048)
                        put("stream", true)
                        put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", prompt)))
                    }
                    val request = Request.Builder()
                        .url("https://api.anthropic.com/v1/messages")
                        .addHeader("x-api-key", rawKey)
                        .addHeader("anthropic-version", "2023-06-01")
                        .post(payload.toString().toRequestBody(jsonMediaType))
                        .build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        emit("Erreur API Anthropic (${response.code}) : ${response.body?.string() ?: response.message}")
                        return@flow
                    }
                    val reader = BufferedReader(InputStreamReader(response.body?.byteStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line?.startsWith("data:") == true) {
                            val data = line!!.removePrefix("data:").trim()
                            if (data.isNotBlank()) {
                                try {
                                    val json = JSONObject(data)
                                    if (json.has("delta") && json.getJSONObject("delta").has("text")) {
                                        emit(json.getJSONObject("delta").getString("text"))
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }
                "gemini" -> {
                    val payload = JSONObject().apply {
                        put("contents", JSONArray().put(JSONObject().apply {
                            put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                        }))
                    }
                    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:streamGenerateContent?alt=sse&key=$rawKey"
                    val request = Request.Builder()
                        .url(url)
                        .post(payload.toString().toRequestBody(jsonMediaType))
                        .build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        emit("Erreur API Gemini (${response.code}) : ${response.body?.string() ?: response.message}")
                        return@flow
                    }
                    val reader = BufferedReader(InputStreamReader(response.body?.byteStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line?.startsWith("data:") == true) {
                            val data = line!!.removePrefix("data:").trim()
                            if (data.isNotBlank()) {
                                try {
                                    val json = JSONObject(data)
                                    val candidates = json.getJSONArray("candidates")
                                    if (candidates.length() > 0) {
                                        val parts = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                                        for (i in 0 until parts.length()) {
                                            emit(parts.getJSONObject(i).getString("text"))
                                        }
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }
                else -> { // Agnes ou OpenAI-compatible SSE
                    val baseUrl = keyEntity.baseUrl.trimEnd('/')
                    val endpoint = if (baseUrl.endsWith("/chat/completions")) baseUrl else "$baseUrl/chat/completions"
                    val payload = JSONObject().apply {
                        put("model", if (provider == "agnes") "agnes-chat-v3" else "gpt-4o")
                        put("stream", true)
                        put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", prompt)))
                    }
                    val request = Request.Builder()
                        .url(endpoint)
                        .addHeader("Authorization", "Bearer $rawKey")
                        .post(payload.toString().toRequestBody(jsonMediaType))
                        .build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        val errBody = response.body?.string() ?: response.message
                        if (response.code == 429) {
                            keyManager.markKeyRateLimited(keyEntity)
                        }
                        emit("Erreur API ($endpoint, HTTP ${response.code}) : $errBody")
                        return@flow
                    }

                    val reader = BufferedReader(InputStreamReader(response.body?.byteStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        if (line?.startsWith("data:") == true) {
                            val data = line!!.removePrefix("data:").trim()
                            if (data == "[DONE]") break
                            if (data.isNotBlank()) {
                                try {
                                    val json = JSONObject(data)
                                    val choices = json.getJSONArray("choices")
                                    if (choices.length() > 0) {
                                        val delta = choices.getJSONObject(0).getJSONObject("delta")
                                        if (delta.has("content")) {
                                            emit(delta.getString("content"))
                                        }
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit("Exception réseau lors de l'appel à l'API : ${e.localizedMessage}")
        }
    }

    /**
     * Génération d'image réelle via endpoint DALL-E / Agnes Image.
     */
    suspend fun generateImage(prompt: String): String = withContext(Dispatchers.IO) {
        val keyEntity: ApiKeyEntity = keyManager.getActiveKeyEntity()
            ?: throw IllegalStateException("Aucune clé API active dans les paramètres.")

        val rawKey = keyManager.decrypt(keyEntity.encryptedKey)
        val baseUrl = keyEntity.baseUrl.trimEnd('/')
        val endpoint = if (baseUrl.endsWith("/images/generations")) baseUrl else "$baseUrl/images/generations"

        val payload = JSONObject().apply {
            put("prompt", prompt)
            put("n", 1)
            put("size", "1024x1024")
        }

        val request = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $rawKey")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Échec de génération d'image HTTP ${response.code} : ${response.body?.string()}")
        }

        val json = JSONObject(response.body?.string() ?: "{}")
        val dataArray = json.getJSONArray("data")
        if (dataArray.length() > 0) {
            dataArray.getJSONObject(0).getString("url")
        } else {
            throw IllegalStateException("Réponse d'image vide reçue de l'API.")
        }
    }

    /**
     * Soumission d'une tâche de génération vidéo avec polling de statut réel.
     */
    suspend fun submitVideoJob(prompt: String): String = withContext(Dispatchers.IO) {
        val keyEntity: ApiKeyEntity = keyManager.getActiveKeyEntity()
            ?: throw IllegalStateException("Aucune clé API active dans les paramètres.")

        val rawKey = keyManager.decrypt(keyEntity.encryptedKey)
        val baseUrl = keyEntity.baseUrl.trimEnd('/')
        val endpoint = "$baseUrl/videos/generations"

        val payload = JSONObject().apply {
            put("prompt", prompt)
            put("model", "agnes-video-v2")
            put("duration", 5)
        }

        val request = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $rawKey")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Échec soumission vidéo HTTP ${response.code} : ${response.body?.string()}")
        }

        val json = JSONObject(response.body?.string() ?: "{}")
        // Récupère l'URL directe ou l'ID de polling
        if (json.has("url")) {
            json.getString("url")
        } else if (json.has("id")) {
            val jobId = json.getString("id")
            pollVideoResult(baseUrl, rawKey, jobId)
        } else {
            throw IllegalStateException("Format de réponse vidéo non reconnu.")
        }
    }

    private suspend fun pollVideoResult(baseUrl: String, apiKey: String, jobId: String): String {
        val pollUrl = "$baseUrl/videos/generations/$jobId"
        for (i in 1..30) {
            delay(3000)
            val request = Request.Builder()
                .url(pollUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "{}")
                val status = json.optString("status")
                if (status == "completed" || status == "succeeded") {
                    return json.getString("url")
                } else if (status == "failed") {
                    throw IllegalStateException("La génération vidéo a échoué sur les serveurs Agnes.")
                }
            }
        }
        throw IllegalStateException("Délai de génération vidéo dépassé (Timeout).")
    }
}
