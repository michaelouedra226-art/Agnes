package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AtelierDatabase
import com.example.data.model.ChatMessage
import com.example.ui.components.AtelierChatInput
import com.example.ui.components.AtelierMessageCard
import com.example.ui.icons.AtelierIcons
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String = "default_session",
    viewModel: ChatViewModel = viewModel(),
    onNavigateToBatch: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = remember { AtelierDatabase.getDatabase(context) }
    val chatDao = remember { db.chatDao() }
    val coroutineScope = rememberCoroutineScope()

    val messages by chatDao.getMessages(conversationId).collectAsState(initial = emptyList())
    val isStreaming by viewModel.isStreaming.collectAsState()
    val activeMode by viewModel.activeMode.collectAsState()

    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Atelier Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = "Mode $activeMode • Agnes Engine actif",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierAccent
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.clearHistory(conversationId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Historique effacé")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = AtelierIcons.Delete,
                            contentDescription = "Effacer",
                            tint = AtelierTextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AtelierSurfaceDark
                )
            )
        },
        bottomBar = {
            AtelierChatInput(
                text = inputText,
                onTextChanged = { inputText = it },
                activeMode = activeMode,
                onModeChange = { viewModel.setMode(it) },
                isStreaming = isStreaming,
                onStopStreaming = { viewModel.stopStreaming() },
                onSendMessage = {
                    val prompt = inputText.trim()
                    if (prompt.isEmpty()) return@AtelierChatInput
                    inputText = ""
                    when (activeMode) {
                        "Image" -> viewModel.generateImageInline(conversationId, prompt)
                        "Vidéo" -> viewModel.generateVideoInline(conversationId, prompt)
                        else -> viewModel.sendMessage(conversationId, prompt)
                    }
                },
                onGenerateImage = {
                    val prompt = inputText.trim().ifEmpty { "Paysage néon cinématique 8k" }
                    inputText = ""
                    viewModel.generateImageInline(conversationId, prompt)
                },
                onGenerateVideo = {
                    val prompt = inputText.trim().ifEmpty { "Séquence quantique ultra-fluide 60fps" }
                    inputText = ""
                    viewModel.generateVideoInline(conversationId, prompt)
                }
            )
        },
        containerColor = AtelierBgDark,
        modifier = modifier
    ) { innerPadding ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(AtelierSurfaceVariantDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AtelierIcons.Sparkles,
                            contentDescription = null,
                            tint = AtelierAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bienvenue dans Atelier Studio",
                        style = MaterialTheme.typography.titleMedium,
                        color = AtelierTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chat conversationnel, synthèse d'images et rendus vidéo propulsés par Agnes AI.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierTextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("chat_messages_list")
            ) {
                items(messages, key = { it.id }) { msg ->
                    // Micro-interaction 11.6: Apparition des messages avec fade + léger slide-up
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
                    ) {
                        AtelierMessageCard(
                            message = msg,
                            onCopy = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Copié dans le presse-papier")
                                }
                            },
                            onVary = { promptToVary ->
                                inputText = "Variation : $promptToVary"
                            },
                            onDownload = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Média sauvegardé")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
