package com.agilefalcon.voicenotepad.audio_managers

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null

    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecording(): String {
        val recordingsDir = File(context.filesDir, "recordings")
        if (!recordingsDir.exists()) recordingsDir.mkdirs()

        val fileName = "recording_${timestamp()}.m4a"
        val file = File(recordingsDir, fileName)

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }

        currentFilePath = file.absolutePath
        return currentFilePath!!
    }

    fun stopRecording(): String? {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        return currentFilePath
    }

    fun abortRecording(){
        try {
            mediaRecorder?.reset()
            mediaRecorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null
        }
    }

    private fun timestamp(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }
}


