package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BatchJob
import com.example.data.model.JobStatus
import com.example.ui.components.AtelierMicroInteractionButton
import com.example.ui.icons.AtelierIcons
import com.example.ui.theme.*
import com.example.ui.viewmodel.BatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchScreen(
    viewModel: BatchViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val jobs by viewModel.jobs.collectAsState(initial = emptyList())
    val isRunning by viewModel.isServiceRunning.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var batchPromptTemplate by remember { mutableStateOf("") }
    var batchCount by remember { mutableStateOf(20) }
    val snackbarHostState = remember { SnackbarHostState() }

    val completedCount = jobs.count { it.status == JobStatus.COMPLETED }
    val pendingCount = jobs.count { it.status == JobStatus.PENDING || it.status == JobStatus.PROCESSING }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Production Batch Vidéo",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = "$completedCount terminés • $pendingCount en attente • Foreground Service actif",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierTextSecondary
                        )
                    }
                },
                actions = {
                    if (jobs.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAllJobs() }) {
                            Icon(imageVector = AtelierIcons.Delete, contentDescription = "Vider", tint = AtelierTextMuted)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtelierSurfaceDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = AtelierAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_batch_fab")
            ) {
                Icon(imageVector = AtelierIcons.Plus, contentDescription = "Nouveau Batch")
            }
        },
        containerColor = AtelierBgDark,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Surface(
                color = AtelierSurfaceVariantDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Traitement d'arrière-plan résilient",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = "Continue même l'application fermée ou redémarrée.",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierTextMuted
                        )
                    }

                    AtelierMicroInteractionButton(
                        onClick = { viewModel.startBatchExecution() },
                        enabled = pendingCount > 0,
                        modifier = Modifier.testTag("start_batch_btn")
                    ) {
                        Text(
                            if (isRunning) "En cours..." else "Démarrer ($pendingCount)",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (jobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(AtelierSurfaceVariantDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = AtelierIcons.Batch,
                                contentDescription = null,
                                tint = AtelierAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "File de batch vide",
                            style = MaterialTheme.typography.titleMedium,
                            color = AtelierTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Générez un lot de 20+ vidéos pour lancer la production autonome d'arrière-plan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("batch_jobs_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(jobs, key = { it.id }) { job ->
                        JobItemRow(
                            job = job,
                            onDelete = { viewModel.deleteJob(job.id) },
                            onRetry = { viewModel.retryJob(job) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(text = "Nouveau Batch de Vidéos", color = AtelierTextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Entrez votre prompt racine pour la série de vidéos haute performance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = batchPromptTemplate,
                        onValueChange = { batchPromptTemplate = it },
                        label = { Text("Prompt racine") },
                        placeholder = { Text("Ex: Séquence cinématique Agnes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("batch_prompt_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Nombre de jobs : $batchCount", color = AtelierTextPrimary)
                        Row {
                            TextButton(onClick = { if (batchCount > 5) batchCount -= 5 }) { Text("-5") }
                            TextButton(onClick = { batchCount += 5 }) { Text("+5") }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val base = batchPromptTemplate.ifBlank { "Production vidéo cinématique Agnes" }
                        viewModel.createBatch(base, batchCount)
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierAccent),
                    modifier = Modifier.testTag("confirm_create_batch_btn")
                ) {
                    Text("Créer ($batchCount jobs)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Annuler", color = AtelierTextMuted)
                }
            },
            containerColor = AtelierSurfaceDark
        )
    }
}

@Composable
fun JobItemRow(
    job: BatchJob,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AtelierCardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.prompt,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = AtelierTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (job.status) {
                        JobStatus.COMPLETED -> AtelierSuccess.copy(alpha = 0.15f)
                        JobStatus.PROCESSING -> AtelierAccent.copy(alpha = 0.15f)
                        JobStatus.FAILED -> AtelierError.copy(alpha = 0.15f)
                        else -> AtelierSurfaceVariantDark
                    }
                ) {
                    Text(
                        text = job.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (job.status) {
                            JobStatus.COMPLETED -> AtelierSuccess
                            JobStatus.PROCESSING -> AtelierAccent
                            JobStatus.FAILED -> AtelierError
                            else -> AtelierTextMuted
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (job.status == JobStatus.PROCESSING) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { job.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = AtelierAccent,
                    trackColor = AtelierSurfaceVariantDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (job.status == JobStatus.FAILED) {
                    TextButton(onClick = onRetry) {
                        Text("Relancer", color = AtelierAccent, style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = AtelierIcons.Delete,
                        contentDescription = "Supprimer",
                        tint = AtelierTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
