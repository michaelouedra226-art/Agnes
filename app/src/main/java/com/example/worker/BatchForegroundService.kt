package com.example.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.local.AtelierDatabase
import com.example.data.model.JobStatus
import com.example.service.AgnesApiClient
import com.example.service.ApiKeyManager
import kotlinx.coroutines.*

/**
 * Service de premier plan Android pour l'exécution réelle et résiliente des batches de vidéos.
 * Appelle l'API configurée (Agnes Video Pipeline) sans simulation locale.
 */
class BatchForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val channelId = "atelier_batch_foreground_channel"
    private val notificationId = 1002

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification(0, 1, "Démarrage de la file de production...")
        startForeground(notificationId, notification)

        serviceScope.launch {
            val db = AtelierDatabase.getDatabase(applicationContext)
            val batchDao = db.batchDao()
            val mediaDao = db.mediaDao()
            val keyManager = ApiKeyManager(applicationContext)
            val apiClient = AgnesApiClient(keyManager)

            val jobs = batchDao.getActiveJobs()
            val total = jobs.size

            if (total == 0) {
                stopSelf()
                return@launch
            }

            jobs.forEachIndexed { index, job ->
                updateNotification(index + 1, total, "Génération réelle ${index + 1}/$total : ${job.prompt.take(20)}...")
                batchDao.updateJob(job.copy(status = JobStatus.PROCESSING, progress = 0.1f))

                try {
                    // Appel réel à l'API vidéo avec polling de terminaison
                    val resultUrl = apiClient.submitVideoJob(job.prompt)
                    batchDao.updateJob(
                        job.copy(
                            status = JobStatus.COMPLETED,
                            progress = 1.0f,
                            resultUrl = resultUrl,
                            completedAt = System.currentTimeMillis()
                        )
                    )
                    mediaDao.insertMedia(
                        com.example.data.model.MediaItem(
                            title = "Batch Vidéo #${index + 1}",
                            prompt = job.prompt,
                            type = com.example.data.model.MediaType.VIDEO,
                            url = resultUrl
                        )
                    )
                } catch (e: Exception) {
                    batchDao.updateJob(
                        job.copy(
                            status = JobStatus.FAILED,
                            progress = 0f,
                            error = e.localizedMessage ?: "Échec de génération API"
                        )
                    )
                }
            }

            showCompletionNotification(total)
            stopForeground(STOP_FOREGROUND_DETACH)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Atelier Production Foreground",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Progression du service d'arrière-plan vidéo Atelier"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(current: Int, total: Int, message: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Atelier Studio - Production Batch")
            .setContentText(message)
            .setProgress(total, current, false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(current: Int, total: Int, message: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(notificationId, buildNotification(current, total, message))
    }

    private fun showCompletionNotification(total: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        val finalNotification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Batch Atelier terminé")
            .setContentText("$total jobs traités sur le serveur de rendu.")
            .setAutoCancel(true)
            .build()
        manager?.notify(1003, finalNotification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        fun startBatch(context: Context) {
            val intent = Intent(context, BatchForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
