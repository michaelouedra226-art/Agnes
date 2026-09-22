package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ApiKeyEntity
import com.example.ui.components.AtelierMicroInteractionButton
import com.example.ui.icons.AtelierIcons
import com.example.ui.theme.*
import com.example.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keys by viewModel.keys.collectAsState(initial = emptyList())
    val currentUser by viewModel.currentUser.collectAsState()
    val authLoading by viewModel.authLoading.collectAsState()
    val testStatus by viewModel.testStatus.collectAsState()

    var showAddKeyDialog by remember { mutableStateOf(false) }
    var newKeyLabel by remember { mutableStateOf("") }
    var newKeyValue by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf("Agnes") }
    var customBaseUrl by remember { mutableStateOf("https://api.agnes.ai/v1") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(testStatus) {
        testStatus?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paramètres & Sécurité",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AtelierTextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtelierSurfaceDark)
            )
        },
        containerColor = AtelierBgDark,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("settings_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Carte Authentification Google Sign-In via Credential Manager
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AtelierSurfaceVariantDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(AtelierAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = AtelierIcons.User,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser?.displayName ?: "Compte Google non connecté",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = AtelierTextPrimary
                                )
                                Text(
                                    text = currentUser?.email ?: "Connectez votre compte pour synchroniser",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AtelierTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (currentUser != null) {
                            OutlinedButton(
                                onClick = { viewModel.signOut() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AtelierError)
                            ) {
                                Text("Se déconnecter")
                            }
                        } else {
                            AtelierMicroInteractionButton(
                                onClick = {
                                    viewModel.signInWithGoogle(context) { success, msg ->
                                        if (!success && msg != null) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(msg)
                                            }
                                        }
                                    }
                                },
                                enabled = !authLoading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (authLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Connexion en cours...", color = Color.White)
                                } else {
                                    Text("Se connecter avec Google", color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // Gestionnaire de clés d'API réelles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Clés d'API & Rotation",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = "Agnes, OpenAI, Anthropic, Gemini (AES-256)",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierTextMuted
                        )
                    }

                    AtelierMicroInteractionButton(
                        onClick = { showAddKeyDialog = true },
                        modifier = Modifier.testTag("add_api_key_btn")
                    ) {
                        Icon(imageVector = AtelierIcons.Plus, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ajouter", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            if (keys.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AtelierCardDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = AtelierIcons.Key,
                                contentDescription = null,
                                tint = AtelierAccent,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucune clé enregistrée",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AtelierTextPrimary
                            )
                            Text(
                                text = "Ajoutez votre clé Agnes, OpenAI ou Gemini pour exécuter de vraies requêtes.",
                                style = MaterialTheme.typography.labelSmall,
                                color = AtelierTextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(keys, key = { it.id }) { keyEntity ->
                    ApiKeyCard(
                        keyEntity = keyEntity,
                        onTest = { viewModel.testKey(keyEntity) },
                        onDelete = { viewModel.deleteKey(keyEntity) }
                    )
                }
            }

            // Spécifications d'infrastructure
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AtelierCardDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Mode d'exécution réseau",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierTextMuted
                        )
                        Text(
                            text = "Appels HTTP directs & SSE Streaming (Zéro mock)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AtelierTextPrimary
                        )
                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = AtelierBorderDark)
                        Text(
                            text = "Ordonnancement Batch",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierTextMuted
                        )
                        Text(
                            text = "Foreground Service avec Polling d'état serveur",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AtelierTextPrimary
                        )
                    }
                }
            }
        }
    }

    if (showAddKeyDialog) {
        AlertDialog(
            onDismissRequest = { showAddKeyDialog = false },
            title = { Text(text = "Enregistrer une clé d'API", color = AtelierTextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Choisissez le fournisseur pour router correctement les requêtes réelles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtelierTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Agnes", "OpenAI", "Anthropic", "Gemini").forEach { prov ->
                            val isSel = selectedProvider == prov
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) AtelierAccent else AtelierSurfaceVariantDark,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedProvider = prov
                                        customBaseUrl = when (prov) {
                                            "Agnes" -> "https://api.agnes.ai/v1"
                                            "OpenAI" -> "https://api.openai.com/v1"
                                            "Anthropic" -> "https://api.anthropic.com/v1"
                                            "Gemini" -> "https://generativelanguage.googleapis.com"
                                            else -> "https://api.agnes.ai/v1"
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = prov,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSel) Color.White else AtelierTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newKeyLabel,
                        onValueChange = { newKeyLabel = it },
                        label = { Text("Nom / Identifiant") },
                        placeholder = { Text("Ex: Ma clé $selectedProvider Pro") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newKeyValue,
                        onValueChange = { newKeyValue = it },
                        label = { Text("Clé d'API secrète") },
                        placeholder = { Text("Collez votre clé réelle...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("key_value_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customBaseUrl,
                        onValueChange = { customBaseUrl = it },
                        label = { Text("Base URL d'API") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addKey(newKeyLabel, newKeyValue, selectedProvider, customBaseUrl)
                        newKeyLabel = ""
                        newKeyValue = ""
                        showAddKeyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AtelierAccent),
                    modifier = Modifier.testTag("save_key_button")
                ) {
                    Text("Sauvegarder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddKeyDialog = false }) {
                    Text("Annuler", color = AtelierTextMuted)
                }
            },
            containerColor = AtelierSurfaceDark
        )
    }
}

@Composable
fun ApiKeyCard(
    keyEntity: ApiKeyEntity,
    onTest: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AtelierCardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = keyEntity.label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = AtelierTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AtelierAccent.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = keyEntity.provider,
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${keyEntity.keyPreview} • ${keyEntity.baseUrl}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AtelierTextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onTest) {
                    Text("Tester", color = AtelierAccent, style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
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
