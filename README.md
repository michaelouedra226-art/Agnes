# Atelier v3.0 — Android Studio IA Personnel

Atelier est un studio IA personnel natif Android (Kotlin + Jetpack Compose + Firebase) conçu pour le chat conversationnel haute performance, la synthèse multimédia (images et vidéos) et l'exécution de séries batch en tâche de fond.

---

## 1. Caractéristiques Principales

- **Chat Conversationnel Fluide** : Streaming réactif des tokens, arrêt instantané, copies avec feedback haptique et suggestions de variations.
- **Rendus Médias Inline** : Génération native d'images et de vidéos intégrées au fil de conversation avec lecteur et options d'export.
- **Production Batch Vidéo Résiliente** : Ordonnancement de 20+ générations continues propulsées par un `ForegroundService` Android résistant aux fermetures d'application ou redémarrages de l'appareil.
- **Gestionnaire Multi-Clés Agnes** : Chiffrement local matériel AES-256 des clés API et rotation dynamique automatique en cas de quota dépassé ou saturation réseau.
- **Authentification Sécurisée** : Connexion via Google Sign-In et Android Credential Manager couplée à Firebase Auth et Firestore Cloud Database.
- **Iconographie 100 % Propriétaire** : Set vectoriel SVG strict `AtelierIcons` exclusif (aucun pack tiers).
- **Intégration CI/CD** : Script et workflow GitHub Actions (`.github/workflows/build-apk.yml`) pour la compilation automatisée des APKs signés Debug et Release.

---

## 2. Architecture Technique

- **Langage & UI** : Kotlin 2.2, Jetpack Compose, Material 3 Dark Luxury Theme (`#0B0C0E`, `#141518`, `#7C5CFF`).
- **Architecture Applicative** : MVVM (Model-View-ViewModel) + StateFlow réactifs + injection propre.
- **Persistance** : Room Database (`chat_messages`, `conversations`, `batch_jobs`, `api_keys`, `media_items`) + synchronisation Firestore.
- **Arrière-plan** : Android `ForegroundService` avec notification de statut interactive et persistance d'état.
