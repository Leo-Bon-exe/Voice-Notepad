package com.agilefalcon.voicenotepad.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.agilefalcon.voicenotepad.data.VoiceRecord




@Composable
fun RecordingList(
    records: List<VoiceRecord>,
    onPlay: (String) -> Unit,
    onNameChange: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    currentFilePath: String?,
    isPlaying: Boolean,
    playbackProgress: Int,
    playbackDuration: Int,
    updatePlaybackProgress: (Int) -> Unit,
    seekToCurrentPosition: () -> Unit,
    shareAudio: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )  {
        items(records) { record ->
            VoiceRecordItem(
                record = record,
                onPlay = onPlay,
                onDelete = onDelete,
                onNameChange = onNameChange,
                currentFilePath = currentFilePath,
                isPlaying = isPlaying,
                playbackProgress = playbackProgress,
                playbackDuration = playbackDuration,
                updatePlaybackProgress =  updatePlaybackProgress,
                seekToCurrentPosition =  seekToCurrentPosition,
                shareAudio = shareAudio
            )
        }
    }
}

@Composable
fun VoiceRecordItem(
    record: VoiceRecord,
    onPlay: (String) -> Unit,
    onDelete: (Int) -> Unit,
    onNameChange: (Int, String) -> Unit,
    currentFilePath: String?,
    isPlaying: Boolean,
    playbackProgress: Int,
    playbackDuration: Int,
    updatePlaybackProgress: (Int) -> Unit,
    seekToCurrentPosition: () -> Unit,
    shareAudio: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    val isCurrent = currentFilePath == record.path
    val icon = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow


    Column  {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 4.dp)
                .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.medium)
                .padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onPlay(record.path) },
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))


                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = record.durationFormatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                        Text(
                            text = record.formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }


                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options",
                            tint =  Color.DarkGray
                            )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            onClick = {
                                expanded = false
                                newName = record.displayName
                                showDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share") },
                            onClick = {
                                shareAudio(record.path)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                expanded = false
                                onDelete(record.id)
                            }
                        )
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Rename") },
                            text = {
                                TextField(
                                    value = newName,
                                    onValueChange = { newName = it },
                                    singleLine = true,
                                )
                            },
                            confirmButton = {
                                Button(onClick = {
                                    onNameChange(record.id, newName)
                                    showDialog = false
                                    newName = ""
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    showDialog = false
                                    newName = ""
                                }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
        if (isCurrent) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Slider(
                    value = playbackProgress.toFloat(),
                    onValueChange = {newValue -> updatePlaybackProgress(newValue.toInt())},
                    onValueChangeFinished = {seekToCurrentPosition()},
                    valueRange = 0f..playbackDuration.toFloat(),
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF2196F3),
                    activeTrackColor = Color(0xFF2196F3),
                    inactiveTrackColor = Color(0xFFBBDEFB)
                )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatMillis(playbackProgress),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    Text(
                        text = formatMillis(playbackDuration),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }


    }
}

fun formatMillis(millis: Int): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}