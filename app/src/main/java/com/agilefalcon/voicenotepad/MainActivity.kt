package com.agilefalcon.voicenotepad

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.agilefalcon.voicenotepad.ui.theme.VoiceNotepadTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import com.agilefalcon.voicenotepad.helper.PermissionHelper
import com.agilefalcon.voicenotepad.ui.screen.RecordingList
import com.agilefalcon.voicenotepad.ui.screen.TimerWithControls
import com.agilefalcon.voicenotepad.viewmodel.VoiceRecordViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionHelper: PermissionHelper

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHelper = PermissionHelper(this, android.Manifest.permission.RECORD_AUDIO)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {

            VoiceNotepadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()
                    .consumeWindowInsets(WindowInsets.systemBars)) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        RecorderScreen(

                            onRequestPermission = { onPermissionGranted ->
                                permissionHelper.checkAndRequestPermission {
                                    onPermissionGranted()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecorderScreen(onRequestPermission: (onPermissionGranted: () -> Unit) -> Unit) {
    val viewModel: VoiceRecordViewModel = hiltViewModel()
    var isRecording by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    val isHoldMode by viewModel.isHoldMode.collectAsState()
    val records by viewModel.records.collectAsState()
    val currentFilePath by viewModel.currentFilePath.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()
    val playbackDuration by viewModel.playbackDuration.collectAsState()



    LaunchedEffect(key1 = isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000L)
                seconds++
            }
        } else {
            seconds = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize().background(Color(0xFF37474F))
            ,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f).padding(top = 40.dp)) {
            RecordingList(
                records = records,
                onPlay = { filePath -> viewModel.playRecording(filePath) },
                onNameChange = {id, newName -> viewModel.changeRecordName(id, newName)},
                onDelete = { id -> viewModel.deleteRecord(id) },
                currentFilePath = currentFilePath,
                isPlaying = isPlaying,
                playbackProgress = playbackProgress,
                playbackDuration = playbackDuration,
                updatePlaybackProgress = {value -> viewModel.updatePlaybackProgress(value)},
                seekToCurrentPosition = {viewModel.seekToCurrentPosition()},
                shareAudio = {filePath -> viewModel.shareAudio(filePath)}

            )
        }
        TimerWithControls(
            seconds = seconds,
            isRecording = isRecording,
            isHoldMode = isHoldMode,
            onToggleHoldMode = { viewModel.toggleHoldMode() },
            onToggleRecording = {
                if (!isRecording) {
                    onRequestPermission({
                        isRecording = true
                        viewModel.startRecording()
                    })
                } else {
                    isRecording = false
                    viewModel.stopRecording(seconds)
                }
            },
            abortRecording = {
                isRecording = false
                viewModel.abortRecording()},
            placeholder = { /* TODO*/ }
        )
    }
}






