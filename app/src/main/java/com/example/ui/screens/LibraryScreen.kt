package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.AtelierDatabase
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.icons.AtelierIcons
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = remember { AtelierDatabase.getDatabase(context) }
    val mediaDao = remember { db.mediaDao() }
    val coroutineScope = rememberCoroutineScope()
    val mediaList by mediaDao.getAllMedia().collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedFilter by remember { mutableStateOf<MediaType?>(null) }
    val filteredList = remember(mediaList, selectedFilter) {
        if (selectedFilter == null) mediaList else mediaList.filter { it.type == selectedFilter }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bibliothèque Créative",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AtelierTextPrimary
                        )
                        Text(
                            text = "${mediaList.size} médias générés",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtelierTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtelierSurfaceDark)
            )
        },
        containerColor = AtelierBgDark,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filtres Images / Vidéos / Tous
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Tous") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AtelierAccent,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedFilter == MediaType.IMAGE,
                    onClick = { selectedFilter = MediaType.IMAGE },
                    label = { Text("Images") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AtelierAccent,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedFilter == MediaType.VIDEO,
                    onClick = { selectedFilter = MediaType.VIDEO },
                    label = { Text("Vidéos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AtelierAccent,
                        selectedLabelColor = Color.White
                    )
                )
            }

            if (filteredList.isEmpty()) {
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
                                imageVector = AtelierIcons.Library,
                                contentDescription = null,
                                tint = AtelierAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aucun média",
                            style = MaterialTheme.typography.titleMedium,
                            color = AtelierTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Les générations réalisées dans le chat ou en batch s'afficheront ici.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AtelierTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("media_grid"),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        MediaCardItem(
                            item = item,
                            onDelete = {
                                coroutineScope.launch {
                                    mediaDao.deleteMedia(item)
                                    snackbarHostState.showSnackbar("Média supprimé")
                                }
                            },
                            onDownload = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Téléchargement lancé")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaCardItem(
    item: MediaItem,
    onDelete: () -> Unit,
    onDownload: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = AtelierCardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.Black)
            ) {
                if (item.type == MediaType.IMAGE) {
                    AsyncImage(
                        model = item.url,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AtelierSurfaceVariantDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AtelierIcons.Play,
                            contentDescription = "Vidéo",
                            tint = AtelierAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Badge de type en haut à gauche
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (item.type == MediaType.IMAGE) "IMAGE" else "VIDÉO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = AtelierTextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDownload, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = AtelierIcons.Download,
                            contentDescription = "Télécharger",
                            tint = AtelierSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = AtelierIcons.Delete,
                            contentDescription = "Supprimer",
                            tint = AtelierTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
