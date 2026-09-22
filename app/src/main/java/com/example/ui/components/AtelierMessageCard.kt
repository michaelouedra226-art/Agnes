package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChatMessage
import com.example.data.model.MediaType
import com.example.data.model.MessageRole
import com.example.ui.icons.AtelierIcons
import com.example.ui.theme.*

@Composable
fun AtelierMessageCard(
    message: ChatMessage,
    onCopy: () -> Unit = {},
    onVary: (String) -> Unit = {},
    onDownload: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // En-tête avec label de rôle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isUser) AtelierAccent else AtelierSurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUser) AtelierIcons.User else AtelierIcons.Sparkles,
                    contentDescription = null,
                    tint = if (isUser) Color.White else AtelierAccent,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isUser) "Vous" else "Atelier IA",
                style = MaterialTheme.typography.labelSmall,
                color = AtelierTextMuted
            )
        }

        // Bulle de contenu
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) AtelierAccent else AtelierSurfaceVariantDark,
            border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
            modifier = Modifier
                .widthIn(max = 340.dp)
                .testTag("message_bubble_${message.id}")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.content.isNotEmpty()) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = if (isUser) Color.White else AtelierTextPrimary
                    )
                }

                // Affichage d'image générée inline
                if (message.mediaType == MediaType.IMAGE && message.mediaUrl != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = message.mediaUrl,
                            contentDescription = "Génération Image Atelier",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Affichage vidéo générée inline avec lecteur stylisé
                if (message.mediaType == MediaType.VIDEO && message.mediaUrl != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, AtelierAccent, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(AtelierAccent.copy(alpha = 0.8f))
                                    .clickable { onDownload(message.mediaUrl) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = AtelierIcons.Play,
                                    contentDescription = "Lecture vidéo",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Vidéo 1080p générée",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }

                // Barre d'outils micro-actions
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.content))
                            onCopy()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = AtelierIcons.Copy,
                            contentDescription = "Copier",
                            tint = if (isUser) Color.White.copy(alpha = 0.8f) else AtelierTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    if (message.mediaUrl != null) {
                        IconButton(
                            onClick = { onVary(message.prompt ?: message.content) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = AtelierIcons.Variation,
                                contentDescription = "Varier",
                                tint = AtelierAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        IconButton(
                            onClick = { onDownload(message.mediaUrl) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = AtelierIcons.Download,
                                contentDescription = "Télécharger",
                                tint = AtelierSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
