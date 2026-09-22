package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AtelierDatabase
import com.example.data.model.BatchJob
import com.example.data.model.JobStatus
import com.example.worker.BatchForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatchViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AtelierDatabase.getDatabase(application)
    private val batchDao = db.batchDao()

    val jobs = batchDao.getAllJobs()

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    fun createBatch(basePrompt: String, count: Int) {
        viewModelScope.launch {
            val generated = (1..count).map { i ->
                BatchJob(
                    prompt = "$basePrompt #$i",
                    model = "Agnes-Video-v2",
                    status = JobStatus.PENDING
                )
            }
            batchDao.insertJobs(generated)
        }
    }

    fun startBatchExecution() {
        BatchForegroundService.startBatch(getApplication())
        _isServiceRunning.value = true
    }

    fun retryJob(job: BatchJob) {
        viewModelScope.launch {
            batchDao.updateJob(job.copy(status = JobStatus.PENDING, progress = 0f))
        }
    }

    fun deleteJob(id: String) {
        viewModelScope.launch {
            batchDao.deleteJob(id)
        }
    }

    fun clearAllJobs() {
        viewModelScope.launch {
            batchDao.clearAll()
        }
    }
}
