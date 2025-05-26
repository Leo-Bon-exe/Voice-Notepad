package com.agilefalcon.voicenotepad.viewmodel

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.agilefalcon.voicenotepad.data.RecordRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agilefalcon.voicenotepad.audio_managers.AudioPlayer
import com.agilefalcon.voicenotepad.audio_managers.AudioRecorder
import com.agilefalcon.voicenotepad.data.VoiceRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit
import com.agilefalcon.voicenotepad.helper.AudioShareHelper


@HiltViewModel
class VoiceRecordViewModel @Inject constructor(
    private val recorder: AudioRecorder,
    private val repository: RecordRepository,
    private val player: AudioPlayer,
    private val sharedPreferences: SharedPreferences,
    private val audioShareHelper: AudioShareHelper
) : ViewModel(){

    private val _isHoldMode = MutableStateFlow(false)
    val isHoldMode: StateFlow<Boolean> get() = _isHoldMode

    private val _currentFilePath = MutableStateFlow<String?>(null)
    val currentFilePath: StateFlow<String?> = _currentFilePath

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playbackProgress = MutableStateFlow(0)
    val playbackProgress: StateFlow<Int> = _playbackProgress

    private val _playbackDuration = MutableStateFlow(1)
    val playbackDuration: StateFlow<Int> = _playbackDuration

    private val _records = MutableStateFlow<List<VoiceRecord>>(emptyList())

    val records = _records
        .map { it.sortedByDescending { record -> record.timestamp } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        _isHoldMode.value = sharedPreferences.getBoolean("isHoldMode", false)
        player.setPlaybackFinishedCallback ({
            _isPlaying.value = false
            _currentFilePath.value = null
            _playbackProgress.value = 0
        })

        loadRecords()
    }

    fun shareAudio(path: String) {
        audioShareHelper.shareAudio(path)
    }

    fun toggleHoldMode() {
        _isHoldMode.value = !_isHoldMode.value
        sharedPreferences.edit { putBoolean("isHoldMode", _isHoldMode.value) }
    }

    fun updatePlaybackProgress(value: Int) {
        _playbackProgress.value = value
    }

    fun seekToCurrentPosition() {
        player.seekTo(_playbackProgress.value)
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _records.value = repository.getRecords()
        }
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (_isPlaying.value) {
                _playbackProgress.value = player.getCurrentPosition()
                delay(500L)
            }
        }
    }

    fun playRecording(filePath: String) {
        if (filePath == _currentFilePath.value) {
            if (player.isPlaying()) {
                player.pause()
                _isPlaying.value = false
            } else {
                player.resume()
                _isPlaying.value = true
                startProgressUpdater()
            }
        } else {
            player.play(filePath)
            _currentFilePath.value = filePath
            _isPlaying.value = true
            _playbackDuration.value = player.getDuration()
            startProgressUpdater()
        }
    }

    fun abortRecording() {
        recorder.abortRecording()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecording() {
     recorder.startRecording()
    }

    fun stopRecording(time: Int) {
        if (time >= 1) {
            val path = recorder.stopRecording()
            if (path != null) {
                saveRecord(path, time)
            }
        }else{
            recorder.abortRecording()
        }
    }

    private fun saveRecord(path: String,time: Int) {
        viewModelScope.launch {
            repository.addRecord(path,time)
            loadRecords()
        }
    }

    fun changeRecordName(id: Int, name:String){
        viewModelScope.launch {
            repository.changeRecordName(id,name)
            loadRecords()
        }
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteRecord(id)
            loadRecords()
        }
    }
}