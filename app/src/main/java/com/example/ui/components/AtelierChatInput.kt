package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.icons.AtelierIcons
import com.example.ui.theme.*

@Composable
fun AtelierChatInput(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onGenerateImage: () -> Unit,
    onGenerateVideo: () -> Unit,
    isStreaming: Boolean = false,
    activeMode: String = "Chat",
    onModeChange: (String) -> Unit = {},
    onStopStreaming: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = AtelierSurfaceDark,
        tonalElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Sélecteur de mode animé (11.7: Indication visuelle claire et animée du mode actif)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Chat", "Image", "Vidéo").forEach { mode ->
                    val isSelected = activeMode == mode
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.7f,
                        label = "mode_alpha"
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AtelierAccent else AtelierSurfaceVariantDark,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderDark),
                        modifier = Modifier
                            .scale(if (isSelected) 1.02f else 1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                onModeChange(mode)
                                if (mode == "Image") onGenerateImage()
                                if (mode == "Vidéo") onGenerateVideo()
                            }
                            .testTag("mode_$mode")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (mode) {
                                "Image" -> AtelierIcons.Image
                                "Vidéo" -> AtelierIcons.Video
                                else -> AtelierIcons.Chat
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = mode,
                                tint = if (isSelected) Color.White else AtelierAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = mode,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White else AtelierTextPrimary
                            )
                        }
                    }
                }
            }

            // Zone de saisie + Bouton d'action avec Spinner ou Send
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AtelierSurfaceVariantDark, RoundedCornerShape(24.dp))
                    .border(1.dp, AtelierBorderDark, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp)
                        .testTag("chat_input_field"),
                    textStyle = TextStyle(
                        color = AtelierTextPrimary,
                        fontSize = 15.sp
                    ),
                    cursorBrush = SolidColor(AtelierAccent),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                text = "Message à Atelier ou prompt Agnes...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AtelierTextMuted
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isStreaming) {
                    IconButton(
                        onClick = onStopStreaming,
                        modifier = Modifier
                            .size(36.dp)
                            .background(AtelierError, CircleShape)
                            .testTag("stop_stream_button")
                    ) {
                        Icon(
                            imageVector = AtelierIcons.Stop,
                            contentDescription = "Arrêter",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    val canSend = text.isNotBlank()
                    IconButton(
                        onClick = onSendMessage,
                        enabled = canSend,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (canSend) AtelierAccent else AtelierSurfaceDark,
                                CircleShape
                            )
                            .testTag("send_message_button")
                    ) {
                        Icon(
                            imageVector = AtelierIcons.Send,
                            contentDescription = "Envoyer",
                            tint = if (canSend) Color.White else AtelierTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
