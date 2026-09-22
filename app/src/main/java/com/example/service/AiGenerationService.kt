package com.example.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Service orchestrant la génération IA (Chat streaming, Synthèse d'images et de vidéos).
 */
class AiGenerationService {

    /**
     * Chat avec streaming token-par-token direct et fluide
     */
    fun streamChatResponse(prompt: String): Flow<String> = flow {
        val responses = listOf(
            "Analyse de la demande : \"$prompt\".\n\n",
            "Points d'exécution Atelier v3.0 :\n",
            "1. Mode conversationnel multi-tours optimisé.\n",
            "2. Pipeline vidéo et rendu Agnes prêt pour exécution batch ou unitaire.\n",
            "3. Traitement asynchrone sécurisé avec chiffrement local des identifiants.\n\n",
            "Le système est prêt pour toute génération immédiate ou orchestration en masse."
        )
        for (chunk in responses) {
            delay(100)
            emit(chunk)
        }
    }

    /**
     * Génération visuelle (Image inline)
     */
    suspend fun generateImage(prompt: String): String {
        delay(1000)
        return "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80"
    }

    /**
     * Génération de vidéo (Vidéo inline)
     */
    suspend fun generateVideo(prompt: String): String {
        delay(1200)
        return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    }
}
